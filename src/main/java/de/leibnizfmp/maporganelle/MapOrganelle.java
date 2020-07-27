/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package de.leibnizfmp.maporganelle;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ChannelSplitter;
import net.imagej.ImageJ;
import ij.Prefs;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;


/**
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>Cellular Imaging>Map Organelle")
public class MapOrganelle<T extends RealType<T>>  implements Command {

    @Override
    public void run() {

        Prefs.blackBackground = true;

        IJ.log("Starting map-organelle plugin");

        InputGuiFiji start = new InputGuiFiji();
        start.createWindow();

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
        Prefs.blackBackground = true;
        ij.command().run(MapOrganelle.class, true);

        boolean runTest1 = false;
        boolean runTest2 = false;

        if ( runTest1 || runTest2 ) {

            //String testInDir = "/data1/FMP_Docs/Projects/orgaPosJ_ME/Plugin_InputTest/";
            String testInDir = "/data1/FMP_Docs/Projects/orgaPosJ_ME/Plugin_InputTest_nd2/";
            //String testInDir = "/data1/FMP_Docs/Projects/orgaPosJ_ME/TestDataSet_2ndChannel/";
            String testOutDir = "/data1/FMP_Docs/Projects/orgaPosJ_ME/Plugin_OutputTest";
            int channelNumber = 4;
            //String fileEnding = ".tif";
            String fileEnding = ".nd2";
            String settings = "setting";

            FileList getFileList = new FileList(fileEnding);
            ArrayList<String> fileList = getFileList.getFileMultiSeriesList(testInDir);

            for (String file : fileList) {
                System.out.println(file);
            }

            ArrayList<String> fileListTest = new ArrayList<>(fileList.subList(0, 1));


            if ( runTest1 ) {

                IJ.log("Run test 1");
                //PreviewGui guiTest = new PreviewGui(testInDir, testOutDir, fileListTest, fileEnding, 3, 0.157);
                //guiTest.setUpGui();

                InputGuiFiji guiTest = new InputGuiFiji(testInDir, testOutDir, fileEnding, settings);
                guiTest.createWindow();

                //BatchProcessor processBatch = new BatchProcessor(testInDir, testOutDir, fileListTest, fileEnding, channelNumber);
                //processBatch.processImage();

                IJ.log("Test 1 done");

            }

        }
    }

}