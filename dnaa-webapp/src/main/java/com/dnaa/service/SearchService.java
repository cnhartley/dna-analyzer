/**
 * 
 */
package com.dnaa.service;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dnaa.common.dbi.DatabaseInterface;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * @author Chris
 *
 */
@Path("/search")
public final class SearchService {

	/*@Path("{params}")
	@GET
	@Produces("application/json")
	public final Response doGet(@PathParam("params") MultivaluedMap<String, String> params) {
		return runSearch(params);
	}*/

	//@Path("")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json")
	public final Response doPost(MultivaluedMap<String, String> params) {
		return runSearch(params);
	}
	
	private final Response runSearch(MultivaluedMap<String, String> params) {
		JSONArray results = new JSONArray();
		try {
			//results = DatabaseInterface.searchForSequences(criteria);
			
			//TODO remove testing only!!!
			JSONObject obj = new JSONObject(
					"{ 'name':'" + params.getFirst("criteria") + "', "
					+ "'path':'http://www.google.com/', }");
			results.put(obj);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.OK).entity("" + results).build();
	}
	/*
	@Path("criteria={criteria}")
	@GET
	@Produces("application/json")
	public final Response search1(@PathParam("criteria") String criteria) {
		return search2(criteria);
	}
	
	@Path("{criteria}")
	@GET
	@Produces("application/json")
	public final Response search2(@PathParam("criteria") String criteria) {
		JSONArray results = new JSONArray();
		try {
			//results = DatabaseInterface.searchForSequences(criteria);
			
			//TODO remove testing only!!!
			JSONObject obj = new JSONObject(
					"{ 'name':'" + criteria + "', "
					+ "'path':'http://www.google.com/', }");
			results.put(obj);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.status(Status.OK).entity("" + results).build();
	}*/
	
}
