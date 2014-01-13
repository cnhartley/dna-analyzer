/**
 * 
 */
package core.gui.panes;

import core.gui.panes.dnainterface.DNAInterfacePanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import core.process.Process.ProcessEvent;
import core.process.Process.ProcessListener;
import core.process.Transcription;

/**
 * 
 * @author Chris N Hartley
 */
public class DNASequenceFromFilePanel extends JPanel implements Runnable,
        ActionListener, ProcessListener
{
    
    //// Testing only ////
    private static final String DEFAULT_FILE_PATH =
            // For laptop:
            //"C:/Documents and Settings/mrr54.NGCCORP/Desktop/workspace/Bioinformatics/sequences/test_sequence.txt";
            // For home computer:
   		    "C:/Users/Chris Hartley/Google Drive/School/Cal Poly/Senior Project/DNA Analyzer/sequences/Bacillus_subtilis.txt";
            //"C:/Users/Chris Hartley/My Active Projects/Programming/Bioinfomatics/workspace/Bioinformatics/sequences/test_sequence.txt";
    
    /**
     * 
     */
    private static final long serialVersionUID = 527254109032427823L;
    
    
    /**
     * 
     */
    private static final String ACTION_FILE_CHOOSER = "Find File...";
    
    
    /**
     * 
     */
    private static final String ACTION_PROCESS = "Process...";
    
    
    // Private member data.
    private JTextField fileTextField;
    private JTextArea outputTextArea;
    private DNAInterfacePanel inspector;
    
    private PrintStream output;
    private long startTime;
    
    private static int index = 0;

    
    /**
     * Constructor for a new instance of this object which loads a specified
     * DNA sequence based on a user selected file.
     */
    public DNASequenceFromFilePanel() {
        super( new BorderLayout(6, 6) );
        index++;
        makePanel();
    }
    
    
    /**
     * 
     */
    private final void makePanel() {
        setBorder( BorderFactory.createTitledBorder("DNA Sequence " + index) );
        //setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6) );
        setOpaque(false);
        
        add( makeFileInputPanel(), BorderLayout.NORTH);
        //add(
        
        makeOutputStreamPanel();//, BorderLayout.CENTER);
        
        add( makeVisualPanel(), BorderLayout.SOUTH);
    }
    
    
    /**
     * 
     * @return
     */
    private final Component makeFileInputPanel() {
        JPanel main = new JPanel( new BorderLayout(6, 6) );
        main.setOpaque(false);
        
        main.add( new JLabel("File Path:"), BorderLayout.WEST);
        main.add( fileTextField = new JTextField(DEFAULT_FILE_PATH), BorderLayout.CENTER);
        
        JPanel pnl = new JPanel( new BorderLayout(0, 0) );
        pnl.setOpaque(false);
        
        JButton btn = new JButton(ACTION_FILE_CHOOSER);
        btn.setActionCommand(ACTION_FILE_CHOOSER);
        btn.addActionListener(this);
        pnl.add(btn, BorderLayout.WEST);
        
        btn = new JButton(ACTION_PROCESS);
        btn.setActionCommand(ACTION_PROCESS);
        btn.addActionListener(this);
        pnl.add(btn, BorderLayout.EAST);
        
        main.add(pnl, BorderLayout.EAST);
        
        return main;
    }
    
    
    /**
     * 
     * @return
     */
    private final Component makeOutputStreamPanel() {
        outputTextArea = new JTextArea();
        JTextComponentOutputStream outputStream =
                new JTextComponentOutputStream(outputTextArea, "output");
        
        output = new PrintStream(outputStream);
        
        JScrollPane scroll = new JScrollPane(outputTextArea);
        
        return scroll;
    }
    
    
    /**
     * 
     * @return
     */
    private final Component makeVisualPanel() {
        inspector = new DNAInterfacePanel();//DNAInspectorPanel();
        return inspector;
    }
    
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ACTION_PROCESS.equals(ae.getActionCommand())) {
            Transcription transcribe = new Transcription();
            final String filepath = fileTextField.getText();
            
            try {
                output.printf("opening file \"%s\"\n", filepath);
                FileInputStream fin =
                        new FileInputStream(filepath);
                transcribe.setInputStream(fin);
                transcribe.setOutputStream(output);
                transcribe.addProcessListener(this);
                SwingUtilities.invokeLater(transcribe);
            }
            catch (FileNotFoundException fnfe) {
                output.printf("Failed to open file: \"%s\"\n", filepath);
                JOptionPane.showMessageDialog(getParent(),
                        "The specified file was not found. Please use the ["
                        + ACTION_FILE_CHOOSER
                        + "] \nbutton to locate the correct valid file to load from.",
                        "File Not Found",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (ACTION_FILE_CHOOSER.equals(ae.getActionCommand())) {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(getParent());
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    fileTextField.setText(file.getCanonicalPath());
                }
                catch (IOException ioe) {
                    JOptionPane.showMessageDialog(getParent(),
                            "Unable to resolve the selected file to its "
                            + "canonical path: " + ioe.getMessage() + "\n\n"
                            + "Please use the [" + ACTION_FILE_CHOOSER + "] \n"
                            + "button to locate the correct valid file to load from.",
                            "Unable to Resolve File",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        System.out.printf("Running from file [%s]\n", fileTextField.getText());
    }
    
    
    @Override
    public void processCompleted(ProcessEvent pe) {
        long lapse = System.currentTimeMillis() - startTime;
        
        Transcription tr = (Transcription)pe.getSource();
        String process = tr.getClass().getName();
        output.printf("Process [%s] completed in %d ms\n", process, lapse);
        
        inspector.loadDNA( tr.getResult() );
    }
    
    
    @Override
    public void processStarted(ProcessEvent pe) {
        startTime = pe.getWhen();
    }
    
    
    @Override
    public void processInterrupted(ProcessEvent pe) {
        output.println("Process interrupted: " + pe.getException().getMessage());
    }
    
    
    /**
     * 
     * @author Chris Hartley
     *
     */
    public static class JTextComponentOutputStream extends OutputStream {
        
        // Private member data.
        private final StringBuilder sb;
        private final Document doc;
        private String prompt, promptPostfix = "> ";
        
        
        /**
         * 
         * @param textComp
         * @param prompt
         */
        public JTextComponentOutputStream(JTextComponent textComp, String prompt) {
            this.doc = textComp.getDocument();
            this.sb = new StringBuilder();
            this.prompt = prompt;
            
            sb.append(prompt + promptPostfix);
        }
        
        
        @Override
        public void write(int c) throws IOException {
            if (c == '\r')
                return;
            
            if (c == '\n') {
                final String text = sb.toString() + "\n";
                
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        try {
                            doc.insertString(doc.getLength(), text, null);
                        }
                        catch (BadLocationException ble) {
                            ; //throw new IOException(ble);
                        }
                    }
                } );
                sb.setLength(0);
                sb.append(prompt + promptPostfix);
                
                return;
            }
            
            sb.append((char) c);
        }
        
        
        @Override
        public void flush() { }
        
        
        @Override
        public void close() { }
      
    }
    
}
