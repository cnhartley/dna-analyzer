/**
 * 
 */
package com.dnaa.service;

import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * @author Chris Hartley
 *
 */
@Path("/xml")
public class XmlResourceService {

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	@Path("{fileName}")
	@GET
	@Produces("text/xml")
	public Response downloadFile(@PathParam("fileName") String fileName) {
		//TODO add code for file downloading from the database...
		System.out.println("xml(" + fileName + ")");
		InputStream in =
				XmlResourceService.class.getClassLoader().getResourceAsStream("xml/" + fileName);
		System.out.println(" -> inputstream=" + in);
		
		if (in == null) {
			return Response.status(Status.NO_CONTENT).entity("<error>Unable to locate resource: " + fileName + "<code>204</code></error>").build();
		}
		else {
			return Response.status(Status.OK).entity(readFromStream(in)).build();
		}
		
	}
	
	/**
	 * 
	 * @param in
	 * @return
	 */
	private final String readFromStream(InputStream in) {
		Scanner scanner = new Scanner(in, "UTF-8");
		scanner.useDelimiter("\\A");
		String output = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		
		return output;
	}
}
