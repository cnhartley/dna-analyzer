/**
 * 
 */
package core.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import core.molecules.AminoAcid;
import core.molecules.Nucleotide;
import core.molecules.macro.RibonucleicAcid;

/**
 * @author Chris N. Hartley
 *
 */
public class AnalyzedRibonucleicAcid implements Iterable<Nucleotide> {

    /**
     * Number of <CODE>Nucleotide</CODE>s that are contained in a <I>codon</I>.
     */
    public static final int NUCLEOTIDES_PER_CODON = 3;


    private final RibonucleicAcid rna;
    private ArrayList<AminoAcid> frame0;
    private ArrayList<AminoAcid> frame1;
    private ArrayList<AminoAcid> frame2;
    private final int maxAminoAcids;

    
    /**
     * Constructor for the analyzed RNA molecule.
     * 
     * @param rna
     */
    public AnalyzedRibonucleicAcid(RibonucleicAcid rna) {
        this.rna = rna;
        
        maxAminoAcids = (int)(rna.getSequenceLength() / NUCLEOTIDES_PER_CODON);
        
        frame0 = new ArrayList<AminoAcid>(maxAminoAcids);
        frame1 = new ArrayList<AminoAcid>(maxAminoAcids);
        frame2 = new ArrayList<AminoAcid>(maxAminoAcids);
    }
    
    
    /**
     * 
     * @return
     */
    public long getSequenceLength() {
        return rna.getSequenceLength();
    }
    
    
    /**
     * 
     * @return
     */
    public int getMaxAminoAcidLength() {
        return maxAminoAcids;
    }
    
    
    /**
     * 
     * @return
     */
    public int getNumOfFrames() {
        return 3;
    }
    
    
    /**
     * 
     * @param frameIndex
     * @return
     */
    public final Collection<AminoAcid> getFrame(int frameIndex) {
        return frameIndex < 1 ? frame0 : frameIndex > 1 ? frame2 : frame1;
    }
    
    
    @Override
    public Iterator<Nucleotide> iterator() {
        return rna.iterator();
    }

}
