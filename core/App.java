/**
 * 
 */
package core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import core.gui.AppFrame;

/**
 * 
 * @author Chris N. Hartley
 */
public class App implements Runnable {

    public static Document ConfigurationsDoc = null;
    public static Document AminoAcidsDoc = null;

    private static String ConfigurationFileName = "resources/config.xml";

    // Private frame
    private AppFrame frm;


    /**
     * Constructor for this implementation of the application.
     */
    public App() {
        frm = new AppFrame();
        frm.setDefaultCloseOperation(AppFrame.EXIT_ON_CLOSE);
    }


    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        ConfigurationsDoc = loadLibrary(ConfigurationFileName);

        NodeList nodes = ConfigurationsDoc.getElementsByTagName("aminoacidslib");
        if (nodes.item(0) != null) {
            AminoAcidsDoc = loadLibrary( nodes.item(0).getTextContent() );
        }
        
        SwingUtilities.invokeLater(frm);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2 && "-c".startsWith(args[0]) && args[1] != null)
            ConfigurationFileName = args[1];

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                    );
        } 
        catch (Exception e) { }
      
        SwingUtilities.invokeLater( new App() );
    }


    /**
     * Load the XML library file based on the specified file name path and
     * returns the org.w3c.dom.Document containing the contents of the file.
     * 
     * @param libFilePath
     * @return
     */
    public final Document loadLibrary(String libFilePath) {
        String loadingMessage = "Loading library file " + libFilePath + "...";
        System.out.println(loadingMessage);
        Document doc = null;

        try {
            FileInputStream libInputStream = new FileInputStream(libFilePath);
            DocumentBuilder dBuilder = 
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = dBuilder.parse(
                    new BufferedInputStream(
                            new ProgressMonitorInputStream(
                                    null, loadingMessage, libInputStream
                                    )
                            )
                    );
        }
        catch (Exception e) {
            System.out.println("Error:loadLibrary " + e.getMessage());
            doc = null;
        }

        return doc;
    }

}
