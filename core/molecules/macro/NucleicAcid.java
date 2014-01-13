/**
 * 
 */
package core.molecules.macro;

import java.util.Iterator;

import core.molecules.Nucleotide;

/**
 * Class to manage and store all <CODE>Nucleotide</CODE>s within a given
 * <CODE>NucleicAcid</CODE> instance.
 * 
 * @author Chris Hartley
 */
public abstract class NucleicAcid implements Iterable<Nucleotide> {

    /**
     * 
     * @author Chris N. Hartley
     */
    public static class NucleotideSequence {
        
        
        /**
         * 
         * @author Chris Hartley
         *
         */
        private static class MemBlock {
         
            protected static int BLOCK_SIZE = 4096;
            protected MemBlock nextBlock;
            protected final int[] cells;
            
            /**
             * Constructor for a memory block.
             */
            private MemBlock() {
                cells = new int[BLOCK_SIZE];
                nextBlock = null;
            }
            
        }
        
        private static final int NUCLEOTIDE_PER_CELL =
                Integer.SIZE / Nucleotide.SIZE;
        
        private static final int NUCLEOTIDES_PER_MEMBLOCK =
                NUCLEOTIDE_PER_CELL * MemBlock.BLOCK_SIZE;
        
        private MemBlock mem;
        private long length;
        
        
        /**
         * Constructor for a nucleotide sequence.
         */
        protected NucleotideSequence() {
            mem = new MemBlock();
            length = 0;
        }
        
        
        /**
         * Returns the number of <CODE>Nucleotide</CODE>s this sequence contains.
         * @return
         */
        public final long length() {
            return length;
        }
        
        
        /**
         * 
         * @param index
         * @return
         */
        private final int getCellAtIndex(long index) {
            if (index >= length)
                throw new IndexOutOfBoundsException();
            
            MemBlock mb = getMemBlockContainingIndex(index);
            return mb.cells[getCellIndexContainingIndex(index)];
        }
        
        
        /**
         * 
         * @param index
         * @return
         */
        private final int getCellIndexContainingIndex(long index) {
            if (index >= length)
                throw new IndexOutOfBoundsException();
            
            int cellIndex = (int) (index % NUCLEOTIDES_PER_MEMBLOCK);
            cellIndex = cellIndex / NUCLEOTIDE_PER_CELL;
            
            return cellIndex;
        }
        
        
        /**
         * 
         * @param index
         * @return
         */
        private final MemBlock getMemBlockContainingIndex(long index) {
            if (index >= length)
                throw new IndexOutOfBoundsException();
            
            MemBlock mb = mem;
            
            while (index >= NUCLEOTIDES_PER_MEMBLOCK) {
                if (mb.nextBlock == null)
                    mb.nextBlock = new MemBlock();
                
                mb = mb.nextBlock;
                index = index - NUCLEOTIDES_PER_MEMBLOCK;
            }
            
            return mb;
        }
        
        
        /**
         * 
         * @param index
         * @param newCellValue
         */
        private final void setCellContainingIndex(long index, int newCellValue) {
            if (index >= length)
                throw new IndexOutOfBoundsException();
            
            MemBlock mb = getMemBlockContainingIndex(index);
            mb.cells[getCellIndexContainingIndex(index)] = newCellValue;
        }
        
        
        /**
         * 
         * @param nt
         * @return
         */
        public final boolean add(Nucleotide nt) {
            return add(nt.key);
        }
        
        
        /**
         * 
         * @param nucleotideKey
         * @return
         */
        public final boolean add(int nucleotideKey) {
            long loc = length++;
            int cell = getCellAtIndex(loc);
            int offset = Integer.SIZE - (int) (loc % NUCLEOTIDE_PER_CELL) * 2;
            
            // mask on the new key...
            offset -= Nucleotide.SIZE;
            cell |= nucleotideKey << offset;
            
            setCellContainingIndex(loc, cell);
            return true;
        }
        
        
        /**
         * Returns the <CODE>Nucleotide</CODE> <I>key</I> value represented as an
         * <CODE>int</CODE> at the specified <I>index</I> in this nucleotide
         * sequence.
         * 
         * @param index a <CODE>long</CODE> value specifying the location of the
         *              <CODE>Nucleotide</CODE> <I>key</I> to be returned.
         *              
         * @return  the <CODE>Nucleotide</CODE> <I>key</I> as an <I>int</I> at
         *          the specified <I>index</I>.
         * 
         * @see molecules.Nucleotide
         */
        public final int getAtIndex(long index) {
            if (index >= length)
                throw new IndexOutOfBoundsException();
            
            int cell = getCellAtIndex(index);
            int offset = Integer.SIZE - (int) (index % NUCLEOTIDE_PER_CELL) * 2;
            offset -= Nucleotide.SIZE;
            
            return (cell >> offset) & Nucleotide.BIT_MASK;
        }

    }
    
    
    /**
     * The number of <CODE>Nucleotide</CODE> that can be packed into a
     * <CODE>byte</CODE>.
     */
    protected static final int NUMBER_OF_NUCLEOTIDES_PER_BYTE =
            Byte.SIZE / Nucleotide.SIZE;

    
    /**
     * The number of <I>Nucleotide bases</I> found within
     * <CODE>NucleicAcid</CODE>.
     */
    protected static final int NUMBER_OF_NUCLEOTIDE_BASES = 4;

    
    /**
     * The <CODE>Nucleotide</CODE> bases for this implementation.
     */
    protected final Nucleotide NucleotideBases[];
    
    
    // Private member data.
    private final NucleotideSequence sequence;
    
    
    /**
     * 
     * @param symbol
     * @return
     * @throws Exception
     */
    protected static final int getKey(Character symbol) throws Exception {
        return Nucleotide.getNucleotideBySymbol(symbol).key;
    }
    
    
    /**
     * 
     * @param base0
     * @param base1
     * @param base2
     * @param base3
     */
    public NucleicAcid(Nucleotide base0, Nucleotide base1, Nucleotide base2,
            Nucleotide base3)
    {
        NucleotideBases = new Nucleotide[NUMBER_OF_NUCLEOTIDE_BASES];
        NucleotideBases[0] = base0;
        NucleotideBases[1] = base1;
        NucleotideBases[2] = base2;
        NucleotideBases[3] = base3;
        
        sequence = new NucleotideSequence();
    }
    
    
    /**
     * 
     * @param bases
     */
    public NucleicAcid(Nucleotide[] bases) {
        assert(bases.length == NUMBER_OF_NUCLEOTIDE_BASES);
        NucleotideBases = bases;
        sequence = new NucleotideSequence();
    }
    
    
    /**
     * 
     * @param nt
     * @return
     */
    public boolean add(Nucleotide nt) {
        return sequence.add(nt);
    }

    
    /**
     * 
     * @param n
     * @return
     */
    public final boolean isValidNucleotideIndex(int n) {
        return n >= 0 && n < NucleotideBases.length;
    }
    
    
    /**
     * 
     * @param  symbol
     * @return
     * @see #isValidNucleotideSymbol(char)
     */
    public final boolean isValidNucleotideSymbol(int symbol) {
        return isValidNucleotideSymbol((char)(symbol));
    }
    
    
    /**
     * 
     * @param symbol
     * @return
     */
    public final boolean isValidNucleotideSymbol(char symbol) {
        for (int i = 0; i < NucleotideBases.length; i++)
            if (NucleotideBases[i].symbol == symbol)
                return true;
        
        return false;
    }
    
    
    /**
     * 
     * @return
     */
    public final long getSequenceLength() {
        return sequence.length();
    }
    
    
    /**
     * 
     * @param index
     * @return
     */
    public Nucleotide getNucleotideAtIndex(long index) {
        return NucleotideBases[sequence.getAtIndex(index)];
    }
    
    
    /**
     * 
     * @param nt
     * @return
     */
    protected Nucleotide getComplementNucleotide(Nucleotide nt) {
        return NucleotideBases[~nt.key & Nucleotide.BIT_MASK];
    }

    
    /**
     * Returns an iterator over a set of elements of type T.
     * 
     * @return an iterator over a set of elements of type T.
     */
    public Iterator<Nucleotide> iterator() {
        return new NucleicAcidIterator(this, 0l);
    }
    

    /**
     * 
     * @param nucleotideSymbol
     * @return
     * @throws Exception
     */
    public final byte getComplementNucleotideByte(int nucleotideSymbol)
            throws Exception
    {
        return (byte)(~getNucleotideByte(nucleotideSymbol) & 0x03);
    }
    
    
    /**
     * 
     * @param nucleotideSymbol
     * @return
     * @throws Exception
     */
    public final byte getNucleotideByte(int nucleotideSymbol)
            throws Exception
    {
        nucleotideSymbol = Character.toUpperCase(nucleotideSymbol);

        for (int i = 0; i < NucleotideBases.length; i++)
            if (NucleotideBases[i].symbol == nucleotideSymbol)
                return (byte)i;

        throw new Exception("Illegal DNA nucleotide " + nucleotideSymbol);
    }
    
}
