/**
 * 
 */
package com.dnaa.service;

import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dnaa.common.dbi.DatabaseInterface;
import com.dnaa.common.utils.SequencePacker;


/**
 * 
 * 
 * @author Chris
 */
@Path("/sequences")
public class SequenceGrabberService {
	
	/**
	 * 
	 * @param sequenceId
	 * @return
	 * @throws JSONException
	 */
	@Path("{id}")
	@GET
	@Produces("application/json")
	public Response getInfo(@PathParam("id") Integer sequenceId) throws JSONException {
		JSONObject json = new JSONObject();
		
		//TODO remove and replace with DatabaseInterface method instead...
		json.put("id", sequenceId);
		json.put("length", 10000);
		json.put("organism", "{organism}");	// the {organisms}' name the sequence belongs to
		json.put("createdBy", "{creator}"); // original uploaded by {creator}
		json.put("createdOn", new Date());
		json.put("lastEdittedBy", "{editor}");
		json.put("lastEdittedOn", new Date());
		
		return Response.status(Status.OK).entity("" + json).build();
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
		String block = null;
		try {
			block = DatabaseInterface.getSequenceBlock(sequenceId, blockIndex).toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String unpacked = block != null && block.length() > 0 ?
				SequencePacker.unpackDNA(block.getBytes()) : "";
		System.out.println("unpack(" + block + ") --> [" + unpacked + "]");
		
		return makeReturnObject(sequenceId, block);
	}

	//TODO should remove and handle in each method...
	private String makeReturnObject(Integer id, String block) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<sequence>");
			sb.append("<id>" + id + "</id>");
			sb.append("<charset>UTF-8</charset>");
			sb.append("<block>" + block + "</block>");
		sb.append("</sequence>");
		
		return sb.toString();
	}

}
