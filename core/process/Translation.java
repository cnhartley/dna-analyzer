/**
 * The translation process in cellular biology occurs within the cell and
 * outside of the nucleus. This process uses <I>ribosomes</I> to attach to the
 * mRNA, produced in the transcription process, which builds amino acids based
 * on the <I>codon</I> sequences of the mRNA.
 */
package core.process;

import java.util.Collection;
import java.util.Iterator;

import core.molecules.AminoAcid;
import core.molecules.Nucleotide;
import core.molecules.macro.RibonucleicAcid;

/**
 * 
 * 
 * @author Chris N. Hartley
 */
public class Translation extends Process<AnalyzedRibonucleicAcid> {

    /**
     * Number of <CODE>Nucleotide</CODE>s that are contained in a <I>codon</I>.
     */
    public static final int NUCLEOTIDES_PER_CODON = 3;

    
    // Member data.
    private int position;
    protected final AnalyzedRibonucleicAcid mRNA;

    
    /**
     * Constructor for the pocess of translation in DNA replication.
     * 
     * @param messengerRNA
     */
    public Translation(RibonucleicAcid messengerRNA) {
        mRNA = new AnalyzedRibonucleicAcid(messengerRNA);
    }


    /**
     * 
     * @return
     */
    public final synchronized long getPosition() {
        return position;
    }

    
    @Override
    public void process() {
        Ribosome[] rbs = new Ribosome[3];
        for (int i = 0; i < rbs.length; i++) {
            rbs[i] = new Ribosome(mRNA.iterator(), mRNA.getFrame(i), i);
            rbs[i].start();
        }

        try {
            for (Ribosome rb : rbs)
                rb.join();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        finally {
            setResult(mRNA);
        }
    }

    
    /**
     * 
     * @author Chris Hartley
     *
     */
    private static final class Ribosome extends Thread {
    
        // Private member data.
        private long position;
        private final Collection<AminoAcid> aminoAcidList;
        private final Iterator<Nucleotide> mRNAIterator;
        
        
        /**
         * Constructor for the thread acting as a ribosome in DNA replication.
         * 
         * @param itr
         * @param list
         * @param skip
         */
        protected Ribosome(Iterator<Nucleotide> itr, Collection<AminoAcid> list, int skip) {
            mRNAIterator = itr;
            position = skip;
            aminoAcidList = list;
        }
        
        
        @Override
        public void run() {
            int codonIndex = 0;
            int codonSize = NUCLEOTIDES_PER_CODON * Nucleotide.SIZE;
            int shift = codonSize;
            
            for (long skip = position; skip > 0 && mRNAIterator.hasNext(); mRNAIterator.next())
                skip--;

            Nucleotide nt;
            while (mRNAIterator.hasNext()) {
                shift -= Nucleotide.SIZE;
                nt = mRNAIterator.next();
                codonIndex |= nt.key << shift;
                
                if (shift == 0) {
                    aminoAcidList.add(AminoAcid.ByCodon[codonIndex]);
                    shift = codonSize;
                    codonIndex = 0;
                }
                position++;
            }
        }

   }

}
