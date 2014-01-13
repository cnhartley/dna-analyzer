/**
 * 
 */
package core.process;

/**
 * @author Chris N. Hartley
 *
 */
public class AnalyzedDeoxyribonucleicAcid {
   
    public static final int THREE_TO_FIVE_PRIME = 0;
    public static final int FIVE_TO_THREE_PRIME = 1;
    
    private final AnalyzedRibonucleicAcid[] strands;
    
    
    /**
     * Constructor for the analyzed DNA molecule.
     * 
     * @param strand0
     * @param strand1
     */
    public AnalyzedDeoxyribonucleicAcid(AnalyzedRibonucleicAcid strand0,
            AnalyzedRibonucleicAcid strand1)
    {
        strands = new AnalyzedRibonucleicAcid[2];
        strands[THREE_TO_FIVE_PRIME] = strand0;
        strands[FIVE_TO_THREE_PRIME] = strand1;
    }
    
    
    /**
     * 
     * @param strand
     * @return
     */
    public final AnalyzedRibonucleicAcid getStrand(int strand) {
        return strands[strand];
    }

    
    /**
     * 
     * @return
     */
    public long getSequenceLength() {
        return strands[0].getSequenceLength();
    }

    
    /**
     * 
     * @return
     */
    public int getMaxAminoAcidLength() {
        return strands[0].getMaxAminoAcidLength();
    }

}
