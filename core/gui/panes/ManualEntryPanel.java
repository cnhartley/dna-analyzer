/**
 * 
 */
package core.gui.panes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * @author MRR54
 *
 */
public class ManualEntryPanel extends JPanel implements Runnable, ActionListener {

   /**
    * 
    */
   private static final long serialVersionUID = 527254109032427823L;

   private JTextField entryTextField;
   private JTextArea outputTextArea;
   private JButton processButton;
   
   private boolean isProcessing = false;
   
   public ManualEntryPanel() {
      super( new BorderLayout(6, 6) );
      
      makePanel();
   }
   
   
   private final void makePanel() {
      setBorder( BorderFactory.createEmptyBorder(6, 6, 6, 6) );
      setOpaque(false);
      
      add( makeTopInputArea(), BorderLayout.NORTH);
      add( makeBottomOutputArea(), BorderLayout.CENTER);
   }
   
   private final Component makeTopInputArea() {
      JPanel main = new JPanel( new BorderLayout(6, 6) );
      main.setOpaque(false);
      
      main.add( new JLabel("DNA (3') Sequence:"), BorderLayout.WEST);
      
      entryTextField = new JTextField("CTACGTTCAGCTTGTCAGGACTGGTAC");
      main.add(entryTextField, BorderLayout.CENTER);
      
      processButton = new JButton("Process...");
      processButton.addActionListener(this);
      main.add(processButton, BorderLayout.EAST);
      
      return main;
   }
   
   private final Component makeBottomOutputArea() {
      outputTextArea = new JTextArea("Analysis:\n");
      outputTextArea.setEditable(false);
      
      JScrollPane scroll = new JScrollPane(outputTextArea);
      
      return scroll;
   }
   
   public synchronized void setProcessing(boolean processing) {
      if (isProcessing != processing) {
         isProcessing = processing;
         processButton.setEnabled(!processing);
         entryTextField.setEnabled(!processing);
      }
   }
   
   /**
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   @Override
   public void actionPerformed(ActionEvent ae) {
      long startTime = System.currentTimeMillis();
      if (!isProcessing) {
         setProcessing(true);
         System.out.printf("Process manual entry of DNA sequence...\n");

         //Reader reader = null;
         //ByteArrayInputStream buffer = 
          //     new ByteArrayInputStream( entryTextField.getText().getBytes() );
         
         //DNA dna = new DNA( entryTextField.getText().length() );
         //dna.load(buffer);
         
         /*try {
            int symbol;
            reader = dna.getThreePrimeReader();
            outputTextArea.append("DNA:\n3' ");
            while ((symbol = reader.read()) != -1) {
               outputTextArea.append("[" + (char)symbol + "]");
            }
            outputTextArea.append(" 5'\n5' ");
            reader = dna.getFivePrimeReader();
            while ((symbol = reader.read()) != -1) {
               outputTextArea.append("[" + (char)symbol + "]");
            }
            outputTextArea.append(" 3'\n");
            
            RNA mRNA3 = dna.transcribeMessengerRNA(DNA.Side.THREE_PRIME);
            reader = mRNA3.getReader();
            outputTextArea.append("mRNA:\n3' ");
            while ((symbol = reader.read()) != -1) {
               outputTextArea.append("[" + (char)symbol + "]");
            }
            outputTextArea.append(" 5'\n");
            
            // transcription
            
            RNA mRNA5 = dna.transcribeMessengerRNA(DNA.Side.FIVE_PRIME);
            reader = mRNA5.getReader();
            outputTextArea.append("5' ");
            while ((symbol = reader.read()) != -1) {
               outputTextArea.append("[" + (char)symbol + "]");
            }
            outputTextArea.append(" 3'\n");
         }
      catch (IOException e) {
            outputTextArea.append("Error while trying to read in DNA sequence!\n");
            e.printStackTrace();
         }
         finally {
            if (reader != null) {
               try {
                  reader.close();
               } catch (IOException ioe) { }
            }//*/
            setProcessing(false);
            outputTextArea.append("(time to process " + (System.currentTimeMillis() - startTime) + " ms)\n");
         //}
      }
   }

   /**
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      System.out.printf("Running on manual entry [%s]\n", entryTextField.getText());
   }

}
