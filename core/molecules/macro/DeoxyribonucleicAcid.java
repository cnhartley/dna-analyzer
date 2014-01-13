/**
 * 
 */
package core.molecules.macro;

import java.util.Iterator;

import core.molecules.Nucleotide;

/**
 * Deoxyribonucleic acids are double helix strands of essential two RNA strands
 * containing nucleotide adenine, cytosine, guanine, and thymine.
 * 
 * @author Chris Hartley
 */
public class DeoxyribonucleicAcid extends NucleicAcid {

    /**
     * 
     * @author Chris Hartley
     *
     */
    public enum TemplateStrand {
        THREE_TO_FIVE_PRIME,
        FIVE_TO_THREE_PRIME
    }
    
    
    /**
     * Constructor for DNA molecule.
     */
    public DeoxyribonucleicAcid() {
        super( Nucleotide.getNucleotideBasesForDNA() );
    }
    
    
    @Override
    public Iterator<Nucleotide> iterator() {
       return getThreeToFivePrimeIterator();
    }
    
    
    /**
     * 
     * @return
     */
    public final Iterator<Nucleotide> getThreeToFivePrimeIterator() {
        return new NucleicAcidIterator(this, 0l);
    }
    
    
    /**
     * 
     * @return
     */
    public final Iterator<Nucleotide> getFiveToThreePrimeIterator() {
        return new NucleicAcidIterator(this, 0l, NucleicAcidIterator.Direction.REVERSE) {
            public Nucleotide next() {
                return getComplementNucleotide( super.next() );
            }
        };
    }


    /**
     * 
     * @param strand
     * @return
     */
    public final RibonucleicAcid getMessengerRNAfrom(TemplateStrand strand) {
        RibonucleicAcid mRNA = new RibonucleicAcid();
        
        Iterator<Nucleotide> itr =
                (strand == TemplateStrand.THREE_TO_FIVE_PRIME) ?
                getThreeToFivePrimeIterator() : getFiveToThreePrimeIterator();
                        
        while ( itr.hasNext() )
        mRNA.add( mRNA.getComplementNucleotide( itr.next() ) );

        return mRNA;
    }

}
