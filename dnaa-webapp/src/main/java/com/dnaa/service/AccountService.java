/**
 * 
 */
package com.dnaa.service;

import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import com.dnaa.common.dbi.DatabaseInterface;

/**
 * @author Chris
 *
 */
@Path("/accounts")
public final class AccountService {

	@Path("/info/{id}")
	@GET
	@Produces("application/json")
	public Response getInfo(@PathParam("id") Integer userId) throws LoginException {
		JSONObject userInfo;
		try {
			userInfo = DatabaseInterface.getUserInfo(userId);
		}
		catch (SQLException e) {
			throw new LoginException(e.getMessage());
		}
		
		return Response.status(Status.OK).entity("" + userInfo).build();
	}
	
	@Path("/login/{username},{password}")
	@POST
	@Produces("application/json")
	public Response login( @PathParam("username") String username,
	                       @PathParam("password") String password )
	        throws LoginException
	{
		JSONObject userInfo;
		try {
			userInfo = DatabaseInterface.getUserInfo(username, password);
		}
		catch (SQLException e) {
			throw new LoginException(e.getMessage());
		}
		
		return Response.status(Status.OK).entity("" + userInfo).build();
	}
	
}
