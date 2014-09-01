/**
 * 
 */
package com.dnaa.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.dnaa.service.dbi.DatabaseInterface;
import com.dnaa.service.utils.SequencePacker;


/**
 * 
 * 
 * @author Chris
 */
@Path("/sequence-grabber-service")
public class SequenceGrabber {
	
	/**
	 * 
	 * @return
	 */
	@GET
	@Produces("application/xml")
	public String getBlock() {
		return makeReturnObject(null, "");
	}
	
	
	/**
	 * Returns a {@link String} comprised of the XML document containing the
	 * results of the requested sequence, by sequence id, and the corresponding
	 * block of sequence data, by the block index.
	 * 
	 * @param sequenceId  
	 * @param blockIndex  
	 * 
	 * @return  the {@link String} of an XML document for the results of the
	 *          request.
	 */
	@Path("{id},{block}")
	@GET
	@Produces("application/xml")
	public String getBlock(	@PathParam("id") Integer sequenceId,
							@PathParam("block") Integer blockIndex )
	{
		String block = DatabaseInterface.getSequenceBlock(sequenceId, blockIndex);
		
		String unpacked = block != null && block.length() > 0 ?
				SequencePacker.unpackDNA(block) : "";
		
		System.out.println("unpack(" + block + ") --> [" + unpacked + "]");
		
		return makeReturnObject(sequenceId, block);
	}

	
	private static final void validation(String original, String unpacked) {
		original = original.toUpperCase();
		System.out.println(" validation:");
		System.out.println("      original=[" + original + "]");
		System.out.println("  vs. unpacked=[" + unpacked + "])");
		
		if (original.equalsIgnoreCase(unpacked)) {
			System.out.println("     => is equals.");
		}
		else {
			System.out.println("     => NOT equals!!!");
			if (original.length() != unpacked.length())
				System.out.println("        -> length are different: " + original.length() + " vs. " + unpacked.length());
			else {
				for (int i = 0; i < original.length(); i++) {
					if (original.charAt(i) != unpacked.charAt(i))
						System.out.println("        -> Differ at " + i + ", [" + original.charAt(i) + "]<!>[" + unpacked.charAt(i) + "]");
				}
			}
		}

	}
	
	private String makeReturnObject(Integer id, String block) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<sequence>");
			sb.append("<id>" + id + "</id>");
			sb.append("<organism-name>" + null + "</organism-name>");
			sb.append("<block>" + block + "</block>");
		sb.append("</sequence>");
		
		return sb.toString();
	}

}
