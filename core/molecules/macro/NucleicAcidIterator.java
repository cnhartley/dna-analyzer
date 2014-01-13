/**
 * 
 */
package core.molecules.macro;

import java.util.Iterator;
import java.util.NoSuchElementException;

import core.molecules.Nucleotide;

/**
 * @author MRR54
 *
 */
public class NucleicAcidIterator implements Iterator<Nucleotide> {

    public enum Direction {
        FORWARD (1),
        REVERSE (-1);
        
        protected int increment;
        
        Direction(int increment) {
            this.increment = increment;
        }
    }
    
    private NucleicAcid nucleicAcid;
    private long position;
    private long maxLength;
    private Direction direction;
    
    
    /**
     * 
     */
    public NucleicAcidIterator(NucleicAcid na, long startPosition) {
        this(na, startPosition, Direction.FORWARD);
    }
    
    
    /**
     * 
     * @param na
     * @param startIndex
     * @param d
     */
    public NucleicAcidIterator(NucleicAcid na, long startIndex, Direction d) {
        nucleicAcid = na;
        maxLength = na.getSequenceLength();
        direction = d;
        position = d == Direction.FORWARD ?
                startIndex - 1 : maxLength - startIndex;
    }
    
    
    @Override
    public boolean hasNext() {
        if (direction == Direction.FORWARD)
            return position + 1 < maxLength;
        else
            return position > 0;
    }
    
    
    @Override
    public Nucleotide next() {
        if (!hasNext())
            throw new NoSuchElementException();
        
        position += direction.increment;
        return nucleicAcid.getNucleotideAtIndex(position);
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
