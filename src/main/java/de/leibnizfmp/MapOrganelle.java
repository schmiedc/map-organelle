/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package de.leibnizfmp;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.plugin.frame.RoiManager;
import net.imagej.ImageJ;

import ij.plugin.ChannelSplitter;
import loci.common.DebugTools;
import loci.formats.meta.IMetadata;
import net.imagej.Dataset;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>Map Organelle")
public class MapOrganelle<T extends RealType<T>> implements Command {
    //
    // Feel free to add more parameters here...
    //

    @Parameter
    private Dataset currentData;

    @Parameter
    private UIService uiService;

    @Parameter
    private OpService opService;

    @Override
    public void run() {

        // test paths
        String workingDir = "/data1/FMP_Docs/Projects/orgaPosJ_ME/";
        String inputDir = "/Plugin_InputTest/";
        //String inputDir = "/TestDataSet_LysoPos/";
        String outputDir = "/Plugin_OutputTest/";
        String testFile =  "HeLa_control_1_WellA1_Seq0000.nd2 - HeLa_control_1_WellA1_Seq0000.nd2 (series 01).tif";
        //String testFile = "HeLa_scr.nd2";
        String testInput = workingDir + inputDir;
        String inputPath = workingDir + inputDir + testFile;

        Image testImage = new Image(testInput, ".nd2", 1.0, 3, 0, 1, 2, 3);
        ImagePlus imp = testImage.openWithBF(testFile);

        ImagePlus[] imp_channels = ChannelSplitter.split(imp);
        ImagePlus nucleus = imp_channels[testImage.nucleus - 1];
        ImagePlus cytoplasm = imp_channels[testImage.cytoplasm - 1];
        ImagePlus organelle = imp_channels[testImage.organelle - 1];

        ImagePlus nucleusMask = NucleusSegmenter.segmentNuclei(nucleus, 5, 50, "Otsu", 2, 100, 20000, 0.5, 1.00);
        nucleusMask.show();

        ImagePlus backgroundMask = CellAreaSegmenter.segmentCellArea(cytoplasm, 10, 50, 200);
        //backgroundMask.show();

        ImagePlus separatedCells = CellSeparator.separateCells(nucleus, cytoplasm, 15, 500);
        //separatedCells.show();

        ImagePlus filteredCells = CellFilter.filterByCellSize(backgroundMask, separatedCells, 100, 150000, 0.00, 1.00);
        //filteredCells.show();

        RoiManager filteredCellRois = CellFilter.filterByNuclei(filteredCells, nucleusMask);

        imp.setC( testImage.cytoplasm );
        filteredCellRois.moveRoisToOverlay(imp);
        Overlay overlay = imp.getOverlay();
        overlay.drawLabels(false);
        imp.show();

        //ImagePlus detectedLysosomes = LysosomeDetector.detectLysosomes(organelle, 2, 2);
        //ImagePlus filteredDetections = DetectionFilter.filterByNuclei(nucleusMask, detectedLysosomes);

        SegmentationVisualizer segmentationVisualizer = new SegmentationVisualizer();
        //segmentationVisualizer.visualizeSpots(imp, testImage, 2, 2, true);
        //segmentationVisualizer.visualizeNucleiSegments(imp, testImage,2,50, "Otsu", 2, 100, 20000, 0.5, 1.00, false);
        //segmentationVisualizer.visualizeCellSegments(imp, testImage, 10, 50, 200, 15, 500, 100, 150000, 0.00, 1.00, true);



        try {

            DebugTools.setRootLevel("OFF");

            // get the meta data from multiseries file
            ImageReader reader = new ImageReader();
            IMetadata omeMeta = MetadataTools.createOMEXMLMetadata();
            reader.setMetadataStore(omeMeta);
            reader.setId(inputPath);
            int nSeries = reader.getSeriesCount();
            reader.close();

        } catch (FormatException e) {
            IJ.error("Sorry, an error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            IJ.error("Sorry, an error occurred: " + e.getMessage());
        }

    }

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();

        //new MapOrganelle().run();

        String testInDir = "/home/schmiedc/Desktop/Projects/pHlorin_Review_TS/New TestIn/";
        String testOutDir = "/home/schmiedc/Desktop/Projects/pHlorin_Review_TS/New TestOut/";
        String settings = "setting";
        String fileEnding = ".nd2";

        FileList getFileList = new FileList(fileEnding);
        ArrayList<String> fileList = getFileList.getFileList(testInDir);

        for (String file : fileList) {
            System.out.println(file);
        }

        PreviewGui guiTest = new PreviewGui(testInDir, testOutDir, fileList);
        guiTest.setUpGui();


        //final UnsignedByteType threshold = new UnsignedByteType( 127 );
        //final net.imagej.ImageJ ij = new ImageJ();
        //final Img< BitType > mask = (Img<BitType>) ij.op().threshold().apply( img, threshold );
        //ImageJFunctions.show( mask );

    }

}
