/**
 * 
 */
package com.dnaa.service;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONException;
import org.json.JSONObject;

import com.dnaa.common.dbi.DatabaseInterface;


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
	 * @throws SQLException 
	 */
	@Path("{id}")
	@GET
	@Produces("application/json")
	public Response getInfo(@PathParam("id") Integer sequenceId)
			throws JSONException, SQLException
	{
		JSONObject json = new JSONObject();
		json = DatabaseInterface.getSequenceInfo(sequenceId);
		
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
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	@Path("{id},{block}")
	@GET
	@Produces("application/json")
	public Response getBlock(	@PathParam("id") Integer sequenceId,
								@PathParam("block") Integer blockIndex )
			throws JSONException, SQLException
	{
		JSONObject json = new JSONObject();
		json = DatabaseInterface.getSequenceBlock(sequenceId, blockIndex);

		/*
		String unpacked = block != null && block.length() > 0 ?
				SequencePacker.unpackDNA(block.getBytes(encoding)) : "";
		System.out.println("unpack(" + block.getBytes(encoding) + ") --> [" + unpacked + "]");
		*/
		
		return Response.status(Status.OK).entity("" + json).build();
	}

}
