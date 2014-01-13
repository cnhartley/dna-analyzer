package core.molecules;


/**
 * class for the Nucleotides which makes up Nucleic Acids in
 * microbiology.
 * 
 * <P>Definition:<PRE>
 *   "any of a group of molecules that, when linked together, form the
 *    building blocks of DNA or RNA: composed of a phosphate group, the
 *    bases adenine, cytosine, guanine, and thymine, and a pentose sugar,
 *    in RNA the thymine base being replaced by uracil."
 * 
 * "nucleotide." <I>Dictionary.com Unabridged</I>. Random House, Inc. 29 May. 2013.
 *     &lt;Dictionary.com http://dictionary.reference.com/browse/nucleotide&gt;.
 * </PRE></P>
 * 
 * @author Chris N. Hartley
 */
public class Nucleotide {
    
    /**
     * 
     * @author Chris Hartley
     *
     */
    public enum Base { // nitrogenous bases
        PURINE     ("purine", 'R'),
        PYRIMIDINE ("pyrimidine", 'Y');
        
        final String name;
        final char symbol;
        
      
        /**
         * 
         * @param name
         * @param symbol
         */
        Base(String name, char symbol) {
            this.name = name;
            this.symbol = symbol;
        }
    };
    
    
    /**
     * The number of bits used to represent a <CODE>Nucleotide</CODE> value in
     * binary form.
     */
    public static final int SIZE = 2;
    
    
    /**
     * The bit mask used for packing nucleotides into large primitive structures,
     * also indicates the number of bits required for indexing the different
     * nucleotide types.
     */
    public static final int BIT_MASK = (1 << SIZE) - 1;
    
    
    /*
     * Static nucleotide definitions types
     */
    public static final Nucleotide ADENINE =
            new Nucleotide("adenine", 'A', 0, Base.PURINE);
    public static final Nucleotide CYTOSINE =
            new Nucleotide("cytosine", 'C', 1, Base.PYRIMIDINE);
    public static final Nucleotide GUANINE =
            new Nucleotide("guanine", 'G', 2, Base.PURINE);
    public static final Nucleotide THYMINE =
            new Nucleotide("thymine", 'T', 3, Base.PYRIMIDINE);
    public static final Nucleotide URACIL =
            new Nucleotide("uracil", 'U', 3, Base.PYRIMIDINE);
    
    
    // Member data.
    private final String name;
    public final char symbol;
    public final int key;
    public final Base base;
    
    
    /**
     * Constructor for a Nucleotide with the specified name and symbol.
     *
     * @param name   <CODE>String</CODE> name of the <CODE>Nucleotide</CODE>
     * @param symbol <CODE>char</CODE> symbol of the <CODE>Nucleotide</CODE>
     * @param key    <CODE>int</CODE> key, or 2-bit mask used for this
     *               <CODE>Nucleotide</CODE>
     */
    protected Nucleotide(String name, char symbol, int key, Base base) {
        this.name = name;
        this.symbol = symbol;
        this.key = key;
        this.base = base;
    }
    
    
    /**
     * Returns the single character symbol, as a <CODE>String</CODE>, that
     * represents the <CODE>Nucleotide</CODE> for this instance of the
     * object.
     * 
     * @return  A <CODE>String</CODE> of the single character symbol for the
     *          <CODE>Nucleotide</CODE>
     */
    public String getSymbol() {
        return "" + symbol;
    }
    
    
    /**
     * Returns the <CODE>String</CODE> containing the name of this
     * <CODE>Nucleotide</CODE> object.
     * 
     * @return  A <CODE>String</CODE> of the name for this
     *          <CODE>Nucleotide</CODE>
     */
    public String getName() {
        return name.toString();
    }
    
    
    @Override
    public String toString() {
        return getName() + " (" + getSymbol() + ")";
    }
    
    
    /**
     * 
     * @return
     */
    public static final Nucleotide[] getNucleotideBasesForDNA() {
        Nucleotide[] bases = new Nucleotide[4];
        bases[ADENINE.key] = ADENINE;
        bases[CYTOSINE.key]= CYTOSINE;
        bases[GUANINE.key]= GUANINE;
        bases[THYMINE.key] = THYMINE;
        
        return bases;
    }
    
    
    /**
     * 
     * @return
     */
    public static final Nucleotide[] getNucleotideBasesForRNA() {
        Nucleotide[] bases = new Nucleotide[4];
        bases[ADENINE.key] = ADENINE;
        bases[CYTOSINE.key]= CYTOSINE;
        bases[GUANINE.key]= GUANINE;
        bases[URACIL.key] = URACIL;
        
        return bases;
    }
    
    
    /**
     * 
     * @param symbol
     * @return
     * @throws IllegalNucleotideSymbol
     */
    public static final Nucleotide getNucleotideBySymbol(Character symbol)
            throws IllegalNucleotideSymbol
    {
        symbol = Character.toUpperCase(symbol);
        Nucleotide nucleotide = null;
        
        switch (symbol) {
        case 'A':
            nucleotide = ADENINE;
            break;
        case 'C':
            nucleotide = CYTOSINE;
            break;
        case 'G':
            nucleotide = GUANINE;
            break;
        case 'T':
            nucleotide = THYMINE;
            break;
        case 'U':
            nucleotide = URACIL;
            break;
        default:
            throw new IllegalNucleotideSymbol(symbol);
        }
        return nucleotide;
    }
    
    
    /**
     * 
     * @author Chris N. Hartley
     */
    public static class IllegalNucleotideSymbol extends RuntimeException {
        
        // Serial version UID
        private static final long serialVersionUID = -3485225565511700763L;
        
        
        /**
         *
         * @param symbol
         */
        protected IllegalNucleotideSymbol(Character symbol) {
            super("Illegal nucleotide symbol (" + symbol + ")");
        }
        
    }
    
}