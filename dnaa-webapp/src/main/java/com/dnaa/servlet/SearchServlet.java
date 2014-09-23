package com.dnaa.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dnaa.common.dbi.DatabaseInterface;

/**
 * Servlet implementation class SearchServlet
 */
public final class SearchServlet extends HttpServlet {
       
    /**
	 * Unique serial version number for this extension of the {@link HttpServlet}
	 */
	private static final long serialVersionUID = -8234796310464981383L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		processSearch(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		processSearch(request, response);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws JSONException
	 * @throws IOException
	 */
	private void processSearch(HttpServletRequest request, HttpServletResponse response)
			throws JSONException, IOException
	{
		final Map<String,String[]> params = request.getParameterMap();
		final String criteria = params.get("criteria")[0];
		
		JSONObject results = new JSONObject();
		try {
			results.putOnce("criteria", criteria);
			results.putOnce("filter", params.get("filter"));
			
			JSONArray matches = DatabaseInterface.searchForSequences(criteria);
			results.put("matches", matches);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		response.setStatus(Status.OK.getStatusCode());
		response.setHeader("Content-Type", "application/json");
		results.write(response.getWriter());
	}

}
