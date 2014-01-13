/**
 * The transcription process in cellular biology occurs in the nucleus of cells.
 * This process splits the deoxyribonucleic acid (DNA) and transcribes each side
 * of the DNA into the messenger ribonucleic acid (mRNA).
 * 
 * This class simulates the transcription process by taking input, the DNA
 * nucleotide sequence (A, C, G, and T), and transcribing it into the
 * corresponding mRNA sequence (containing A, C, G, and U).
 */
package core.process;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import core.molecules.AminoAcid;
import core.molecules.Nucleotide;
import core.molecules.Nucleotide.IllegalNucleotideSymbol;
import core.molecules.macro.DeoxyribonucleicAcid;
import core.molecules.macro.RibonucleicAcid;

/**
 * 
 * @author Chris N. Hartley
 */
public class Transcription extends Process<AnalyzedDeoxyribonucleicAcid> {

    /**
     * Number of <CODE>Nucleotide</CODE>s that can be packed into a single 
     * <CODE>Integer</CODE> value.
     */
    public static final int NUCLEOTIDES_PER_INT = Integer.SIZE / Nucleotide.SIZE;

    
    // Private member data.
    private InputStream input;
    private PrintWriter output;

    
    /**
     * Constructor for the transcription process in DNA replication.
     */
    public Transcription() {
        setInputStream(System.in);
        setOutputStream(System.out);
    }

    
    /**
     * 
     * @param is
     */
    public final void setInputStream(InputStream is) {
        input = new BufferedInputStream(is);
    }
    
    
    /**
     * 
     * @param os
     */
    public final void setOutputStream(OutputStream os) {
        output = new PrintWriter(os);
    }
    
    
    /**
     * 
     * @return
     * @throws InterruptedException
     */
    public final DeoxyribonucleicAcid readSequenceFromInput()
            throws InterruptedException
    {
        DeoxyribonucleicAcid dna = new DeoxyribonucleicAcid();
        Nucleotide nt;
        int value;
        
        try {
            while ((value = input.read()) != -1) {
                if (isCanceled())
                    throw new InterruptedException("Canceled");
                
                if (!Character.isWhitespace(value)) {
                    nt = Nucleotide.getNucleotideBySymbol( (char)value );

                    if (!dna.add(nt)) {
                        String msg = "Unable to add nucleotide to DNA";
                        throw new InterruptedException(msg);
                    }
                }
            }
            
            //// testing ////
            /*java.util.Iterator<Nucleotide> itr = dna.iterator();
              while (itr.hasNext())
                 output.printf("%c", itr.next().symbol);
              itr = null;
              output.println("\nSequence length=" + dna.getSequenceLength());//*/
        }
        catch (IOException ioe) {
            String msg = "IOException occured: " +ioe.getMessage();
            throw new InterruptedException(msg);
        }
        catch (IllegalNucleotideSymbol ins) {
            String msg = "IllegalNucleotideSymbol occured:" + ins.getMessage();
            throw new InterruptedException(msg);
        }
        
        return dna;
    }

    
    @Override
    public void process() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        DeoxyribonucleicAcid dna = readSequenceFromInput();
        
        // TODO Output the two corresponding mRNA sequences.
        //   (thread each one to a translation object to create the amino acids)
        RibonucleicAcid[] mRNA = new RibonucleicAcid[2];
        mRNA[0] = dna.getMessengerRNAfrom(DeoxyribonucleicAcid.TemplateStrand.THREE_TO_FIVE_PRIME);
        mRNA[1] = dna.getMessengerRNAfrom(DeoxyribonucleicAcid.TemplateStrand.FIVE_TO_THREE_PRIME);
        
        Translation[] tr = new Translation[mRNA.length];
        Thread[] tt = new Thread[mRNA.length];
        for (int i = 0; i < mRNA.length; i++) {
            tr[i] = new Translation(mRNA[i]);
            //tr[i].addProcessListener(this);
            
            tt[i] = new Thread(tr[i]);
            tt[i].start();
        }
        
        AnalyzedRibonucleicAcid[] amRNA = new AnalyzedRibonucleicAcid[mRNA.length]; 
        try {
            for (int i = 0; i < mRNA.length; i++) {
                tt[i].join();
                tt[i] = null;
                
                amRNA[i] = tr[i].getResult();
                for (int f = 0; f < amRNA[i].getNumOfFrames(); f++) {
                    output.printf("Frame %d: ", f);
                    printFrame(amRNA[i].getFrame(f), output);
                }
            }
            AnalyzedDeoxyribonucleicAcid aDNA =
                    new AnalyzedDeoxyribonucleicAcid(amRNA[0], amRNA[1]);
            
            setResult(aDNA);
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        output.println("Transcription took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    
    /**
     * 
     * @param frame
     * @param out
     */
    private static synchronized final void printFrame(Collection<AminoAcid> frame, PrintWriter out) {
        out.write("length=" + frame.size() + ", [");
        for (AminoAcid aa : frame)
            out.write(" " + aa.getShortName());
        out.write(" ]\n");
        out.flush();
    }

}
