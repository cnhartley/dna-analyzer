/**
 * 
 */
package core.molecules.macro;

import core.molecules.Nucleotide;

/**
 * Ribonucleic acids, or RNA, are single strands of nucleic acids containing
 * some number of nucleotides. For RNA, the valid nucleotides are adenine,
 * cytosine, guanine, and uracil. 
 * 
 * @author Chris N. Hartley
 */
public class RibonucleicAcid extends NucleicAcid {

    /**
     * Constructor for a RNA molecule.
     */
    public RibonucleicAcid() {
        super( Nucleotide.getNucleotideBasesForRNA() );
    }

}
