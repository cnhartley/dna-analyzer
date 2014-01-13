/**
 * 
 */
package core.utils;

import core.molecules.Nucleotide;


/**
 * 
 * 
 * @author Chris N. Hartley
 */
public class PackSequence<P extends PackSequence.Packable> {

    /**
     * Interface for objects that wish to be packed using the
     * <CODE>PackSequence</CODE> structure.
     * 
     * @author Chris N. Hartley
     */
    public static interface Packable {

        public int getKey();
        public int getKeySize();

    }

    
    /**
     * Provides memory blocks of storage that can contain the packed objects.
     * 
     * @author Chris N. Hartley
     */
    private static class MemBlock {

        /**
         * Defines the length of cells found in each memory block.
         */
        protected static int BLOCK_SIZE = 4096;

        // Protected member data.
        protected MemBlock nextBlock;
        protected final int[] cells;

        
        /**
         * Constructor for a new memory block with the default size.
         */
        private MemBlock() {
            cells = new int[BLOCK_SIZE];
            nextBlock = null;
        }

    }

    
    //TODO need to rework these values to a generic form...
    private static final int PACKED_ITEMS_PER_CELL =
            Integer.SIZE / Nucleotide.SIZE;
    
    private static final int PACKED_ITEMS_PER_MEMBLOCK =
            PACKED_ITEMS_PER_CELL * MemBlock.BLOCK_SIZE;


    // Private member data.
    private MemBlock mem;
    private long length;


    /**
     * Constructor to create a new pack sequence of data.
     */
    public PackSequence() {
        mem = new MemBlock();
        length = 0;
    }


    /**
     * Returns the number of packed items this sequence contains.
     * 
     * @return <I>long</I> value specifying the length of this sequence.
     */
    public final long length() {
        return length;
    }
   
   
    /**
     * Returns the entire cell contents, as an <I>int</I>, at a specific packed
     * item index.
     * 
     * @param index <I>long</I> packed item index location.
     * @return the cell value at the location.
     */
    private final int getCellAtIndex(long index) {
        if (index >= length)
            throw new IndexOutOfBoundsException();

        MemBlock mb = getMemBlockContainingIndex(index);
        return mb.cells[getCellIndexContainingIndex(index)];
    }

    
    /**
     * Returns the cell index number found within a memory block at the specific
     * packed index.
     * 
     * @param index
     * @return
     */
    private final int getCellIndexContainingIndex(long index) {
        if (index >= length)
            throw new IndexOutOfBoundsException();

        int cellIndex = (int) (index % PACKED_ITEMS_PER_MEMBLOCK);
        cellIndex = cellIndex / PACKED_ITEMS_PER_CELL;

        return cellIndex;
    }

    
    /**
     * Returns the reference to the memory block containing the specified
     * packed index.
     * 
     * @param index
     * @return
     */
    private final MemBlock getMemBlockContainingIndex(long index) {
        if (index >= length)
            throw new IndexOutOfBoundsException();

        MemBlock mb = mem;

        while (index >= PACKED_ITEMS_PER_MEMBLOCK) {
            if (mb.nextBlock == null)
                mb.nextBlock = new MemBlock();

            mb = mb.nextBlock;
            index = index - PACKED_ITEMS_PER_MEMBLOCK;
        }

        return mb;
    }

    
    /**
     * Sets the entire cell value to the <B>newCellValue</B> that contains the
     * specified packed index.
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

    
    //TODO probably remove this method from this class...
    public final boolean add(Nucleotide nt) {
        return add(nt.key);
    }

    
    /**
     * Adds the specified <CODE>Packable</CODE> object to the end of this
     * sequence and returns <I>true</I> if it was successful or <I>false</I> if
     * not.
     * 
     * @param p
     * @return
     */
    public final boolean add(P p) {
        return add( p.getKey() );
    }

    
    /**
     * Adds the specified <I>key</I> to the end of this sequence and returns
     * <I>true</I> if it was successful or <I>false</I> if not.
     * 
     * @param key the new <I>int</I> key to add.
     * @return  <I>true</I> if the key was successfully added; otherwise,
     *          returns <I>false</I>.
     */
    public final boolean add(int key) {
        long loc = length++;
        int cell = getCellAtIndex(loc);
        int offset = Integer.SIZE - (int) (loc % PACKED_ITEMS_PER_CELL) * 2;

        // mask on the new key...
        offset -= Nucleotide.SIZE;
        cell |= key << offset;

        setCellContainingIndex(loc, cell);
        return true;
    }

    
    /**
     * Returns the <CODE>Packable</CODE> <I>key</I> value represented as an
     * <CODE>int</CODE> at the specified <I>index</I> in this pack sequence.
     * 
     * @param index a <CODE>long</CODE> value specifying the location of the
     *              <CODE>Packable</CODE> <I>key</I> to be returned.
     *              
     * @return  the <CODE>Packable</CODE> <I>key</I> as an <I>int</I> at the
     *          specified <I>index</I>.
     * 
     * @see PackSequence.Packable
     */
    public final int getAtIndex(long index) {
        if (index >= length)
            throw new IndexOutOfBoundsException();

        int cell = getCellAtIndex(index);
        int offset = Integer.SIZE - (int) (index % PACKED_ITEMS_PER_CELL) * 2;
        offset -= Nucleotide.SIZE;

        return (cell >> offset) & Nucleotide.BIT_MASK;
    }

}
