/**
 * 
 */
package core.molecules;

/**
 * 
 * @author Chris N. Hartley
 */
public class AminoAcid {
   
    /**
     * The number of <CODE>Nucleotide</CODE>s that make up a single <B>codon</B>.
     */
    public static final int NUCLEOTIDES_PER_CODON = 3;
    
    
    /**
     * The number of bits used to represent an <CODE>AminoAcid</CODE> value in
     * binary form.
     */
    public static final int SIZE = NUCLEOTIDES_PER_CODON * Nucleotide.SIZE;
    
    
    /**
     * The bit mask used for packing <CODE>AminoAcid</CODE>s into large primitive
     * structures, also indicates the number of bits required for indexing the
     * different amino acid types.
     */
    public static int BIT_MASK = 0;
    
    
    /**
     * The total number of possible <B>Codon</B>s based on the premises that
     * there are three <CODE>Nucleotide</CODE>s per <B>Codon</B> and four
     * options for each <CODE>Nucleotide</CODE>; therefore, there are 4x4x4 = 64
     * possible combinations.
     */
    public static final int NUMBER_OF_CODONS = 64;
    
    
    /**
     * 
     */
    public static final AminoAcid[] ByCodon = new AminoAcid[NUMBER_OF_CODONS];
    
    
    /**
     * 
     */
    private static int numAminoAcids = 0;

    
    // Private member data.
    private final int key;
    private final char symbol;
    private final String name;
    private final String shortName;
    
    
    /**
     * Constructor for a new instance of an amino acid with the specified data.
     * 
     * @param name
     * @param shortName
     * @param symbol
     */
    private AminoAcid(String name, String shortName, char symbol) {
        this.symbol = symbol;
        this.name = name;
        this.shortName = shortName;
        this.key = numAminoAcids++;
        
        BIT_MASK |= key;
    }
    
    
    /**
     * 
     * @return
     */
    public final int getKey() {
        return key;
    }
    
    
    /**
     * 
     * @return
     */
    public final String getName() {
        return name;
    }
    
    
    /**
     * 
     * @return
     */
    public final String getShortName() {
       return shortName;
    }
    
    
    /**
     * 
     * @return
     */
    public final char getSymbol() {
        return symbol;
    }

    
    @Override
    public String toString() {
        return name + "(" + symbol + ")";
    }
    
    
    /**
     * Returns the total number of <CODE>AminoAcid</CODE>s.
     * 
     * @return the total number of <CODE>AminoAcid</CODE>s.
     */
    public static final int getCount() {
        return numAminoAcids;
    }


    public static final AminoAcid METHIONINE = 
            new AminoAcid("Methionine", "Met", 'M');
    public static final AminoAcid ARGININE = 
            new AminoAcid("Arginine", "Arg", 'R');
    public static final AminoAcid SERINE = 
            new AminoAcid("Serine", "Ser", 'S');
    public static final AminoAcid LYSINE = 
            new AminoAcid("Lysine", "Lys", 'K');
    public static final AminoAcid ASPARAGINE = 
            new AminoAcid("Asparagine", "Asn", 'N');
    public static final AminoAcid THREONINE = 
            new AminoAcid("Threonine", "Thr", 'T');
    public static final AminoAcid ISOLEUCINE = 
            new AminoAcid("Isoleucine", "Ile", 'I');
    public static final AminoAcid GLUTAMINE = 
            new AminoAcid("Glutamine", "Gln", 'Q');
    public static final AminoAcid HISTIDINE = 
            new AminoAcid("Histidine", "His", 'H');
    public static final AminoAcid PROLINE = 
            new AminoAcid("Proline", "Pro", 'P');
    public static final AminoAcid LEUCINE = 
            new AminoAcid("Leucine", "Leu", 'L');
    public static final AminoAcid TRYPTOPHAN = 
            new AminoAcid("Tryptophan", "Trp", 'W');
    public static final AminoAcid CYSTEINE = 
            new AminoAcid("Cysteine", "Cys", 'C');
    public static final AminoAcid TYROSINE = 
            new AminoAcid("Tyrosine", "Tyr", 'Y');
    public static final AminoAcid PHENYLALANINE = 
            new AminoAcid("Phenylalanine", "Phe", 'F');
    public static final AminoAcid GLYCINE = 
            new AminoAcid("Glycine", "Gly", 'G');
    public static final AminoAcid GLUTAMIC_ACID = 
            new AminoAcid("Glutamic acid", "Glu", 'E');
    public static final AminoAcid ASPARTIC_ACID = 
            new AminoAcid("Aspartic acid", "Asp", 'D');
    public static final AminoAcid ALANINE = 
            new AminoAcid("Alanine", "Ala", 'A');
    public static final AminoAcid VALINE = 
            new AminoAcid("Valine", "Val", 'V');
    
    public static final AminoAcid STOP_CODON = 
            new AminoAcid("stop", "stop", '.');
    public static final AminoAcid START_CODON = METHIONINE;
    
    
    static {
        ByCodon[0x08] = ARGININE;
        ByCodon[0x0A] = ARGININE;
        ByCodon[0x18] = ARGININE;
        ByCodon[0x19] = ARGININE;
        ByCodon[0x1A] = ARGININE;
        ByCodon[0x1B] = ARGININE;
        
        ByCodon[0x09] = SERINE;
        ByCodon[0x0B] = SERINE;
        ByCodon[0x34] = SERINE;
        ByCodon[0x35] = SERINE;
        ByCodon[0x36] = SERINE;
        ByCodon[0x37] = SERINE;
        
        ByCodon[0x00] = LYSINE;
        ByCodon[0x02] = LYSINE;
        
        ByCodon[0x01] = ASPARAGINE;
        ByCodon[0x03] = ASPARAGINE;
        
        ByCodon[0x04] = THREONINE;
        ByCodon[0x05] = THREONINE;
        ByCodon[0x06] = THREONINE;
        ByCodon[0x07] = THREONINE;
        
        ByCodon[0x0E] = METHIONINE;
        
        ByCodon[0x0C] = ISOLEUCINE;
        ByCodon[0x0D] = ISOLEUCINE;
        ByCodon[0x0F] = ISOLEUCINE;
        
        ByCodon[0x10] = GLUTAMINE;
        ByCodon[0x12] = GLUTAMINE;
        
        ByCodon[0x11] = HISTIDINE;
        ByCodon[0x13] = HISTIDINE;
        
        ByCodon[0x14] = PROLINE;
        ByCodon[0x15] = PROLINE;
        ByCodon[0x16] = PROLINE;
        ByCodon[0x17] = PROLINE;
        
        ByCodon[0x1C] = LEUCINE;
        ByCodon[0x1D] = LEUCINE;
        ByCodon[0x1E] = LEUCINE;
        ByCodon[0x1F] = LEUCINE;
        ByCodon[0x3C] = LEUCINE;
        ByCodon[0x3E] = LEUCINE;
        
        ByCodon[0x3A] = TRYPTOPHAN;
        
        ByCodon[0x30] = STOP_CODON;
        ByCodon[0x32] = STOP_CODON;
        ByCodon[0x38] = STOP_CODON;
        
        ByCodon[0x39] = CYSTEINE;
        ByCodon[0x3B] = CYSTEINE;
        
        ByCodon[0x31] = TYROSINE;
        ByCodon[0x33] = TYROSINE;
        
        ByCodon[0x3D] = PHENYLALANINE;
        ByCodon[0x3F] = PHENYLALANINE;
        
        ByCodon[0x28] = GLYCINE;
        ByCodon[0x29] = GLYCINE;
        ByCodon[0x2A] = GLYCINE;
        ByCodon[0x2B] = GLYCINE;
        
        ByCodon[0x20] = GLUTAMIC_ACID;
        ByCodon[0x22] = GLUTAMIC_ACID;
        
        ByCodon[0x21] = ASPARTIC_ACID;
        ByCodon[0x23] = ASPARTIC_ACID;
        
        ByCodon[0x24] = ALANINE;
        ByCodon[0x25] = ALANINE;
        ByCodon[0x26] = ALANINE;
        ByCodon[0x27] = ALANINE;
        
        ByCodon[0x2C] = VALINE;
        ByCodon[0x2D] = VALINE;
        ByCodon[0x2E] = VALINE;
        ByCodon[0x2F] = VALINE;
    };
    
}
