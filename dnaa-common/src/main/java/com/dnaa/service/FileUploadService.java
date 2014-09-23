/**
 * 
 */
package com.dnaa.service;

import java.nio.charset.Charset;

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
@Path("/file-upload-service")
public final class FileUploadService {

	/**
	 * 
	 * @param fileName  the files name to upload to the database.
	 * @param data      the byte sequence of the specified file.
	 * @return  the sequence information for the uploaded sequence file.
	 */
	@Path("{fileName},{data}")
	@GET
	@Produces("application/xml")
	public Response uploadFile( @PathParam("fileName") String fileName,
	                          @PathParam("data") String data )
	{
		//TODO add code for file uploading to the database...
		System.out.println("uploadFile('" + fileName + "','" + data + "')");
		boolean success = false;
		Status status = null;
		
		if (fileName != null && fileName.length() > 0) {
			//TODO create the file connection...
			
			//TODO use input-stream to upload the file...
			for (byte b : data.getBytes(Charset.forName("UTF-8")))
				System.out.println(b); // TODO read each byte in...
			
			if (success)
				status = Status.ACCEPTED;
		}
		else
			status = Status.BAD_REQUEST;
		
		return Response.status(status).build();
	}
	
}
