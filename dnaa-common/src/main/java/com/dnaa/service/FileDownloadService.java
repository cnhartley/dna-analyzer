/**
 * 
 */
package com.dnaa.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author Chris
 *
 */
@Path("/file-download-service")
public final class FileDownloadService {

	/**
	 * 
	 * @param sequenceId    the sequence id from the database.
	 * @return  the specified sequence from the database as a byte stream/file
	 */
	@Path("{id}")
	@GET
	@Produces("application/xml")
	public Response downloadFile(@PathParam("id") Integer sequenceId) {
		//TODO add code for file downloading from the database...
		System.out.println("downloadFile(" + sequenceId + ")");
		
		return Response.status(Status.NOT_FOUND).entity("<error><code>404</code></error>").build();
	}
	
}
