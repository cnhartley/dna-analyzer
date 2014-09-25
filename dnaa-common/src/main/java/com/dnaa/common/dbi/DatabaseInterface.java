/**
 * 
 */
package com.dnaa.common.dbi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Chris
 *
 */
public class DatabaseInterface implements DatabaseQueries {
	
	private static final String _DB_PROPERTIES_FILENAME = "db.properties";
	private static final String encoding = "UTF-8";
	private static Connection dbc = null;
	

	/**
	 * 
	 * @param props
	 * @return
	 */
	public static final String getConnectionURLString(Properties props) {
		StringBuilder sb = new StringBuilder();
		sb.append(props.getProperty("jdbc", "jdbc"));
		sb.append(":");
		sb.append(props.getProperty("subprotocol"));
		sb.append("://");
		sb.append(props.getProperty("host", "localhost"));
		sb.append(":");
		sb.append(props.getProperty("port"));
		sb.append("/");
		sb.append(props.getProperty("database"));
		
		return sb.toString();
	}
	
	/**
	 * 
	 */
	private static void connect() {
		Properties props = new Properties();
		try {
			props.load( DatabaseInterface.class.getClassLoader()
					.getResourceAsStream(_DB_PROPERTIES_FILENAME) );
			
			String url = getConnectionURLString(props);
			
			Class.forName(props.getProperty("driver")).newInstance();
			dbc = DriverManager.getConnection(url, props);
		}
		catch (SecurityException se) {
			se.printStackTrace();
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			
			System.out.println("SQLException: " + sqle.getMessage());
		    System.out.println("SQLState: " + sqle.getSQLState());
		    System.out.println("VendorError: " + sqle.getErrorCode());
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts a given {@link ResultSet} into a {@link JSONArray} comprised of
	 * {@link JSONObject}s representing the results.
	 * 
	 * @param rs  the {@link ResultSet} to convert to the {@link JSONArray}.
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONArray convertToJSONArray(ResultSet rs)
			throws SQLException, JSONException
	{
		JSONArray json = new JSONArray();
		JSONObject obj;
		ResultSetMetaData md = rs.getMetaData();
		int cols = md.getColumnCount();
		
		while (rs.next()) {
		    obj = new JSONObject();
		    for (int i = 1; i <= cols; i++) {
		        String colName = md.getColumnName(i);
		        
		        switch (md.getColumnType(i)) {
		        case Types.ARRAY:
		            obj.put(colName, rs.getArray(colName));
		            break;
		            
		        case Types.BIGINT:
		            obj.put(colName, rs.getLong(colName));
		            break;
		            
		        case Types.REAL:
		            obj.put(colName, rs.getFloat(colName));
		            break;
		            
		        case Types.BIT:
		        case Types.BOOLEAN:
		            obj.put(colName, rs.getBoolean(colName));
		            break;
		            
		        case Types.BLOB:
		            obj.put(colName, rs.getBlob(colName));
		            break;
		            
		        case Types.DOUBLE:
		        case Types.FLOAT:
		        	obj.put(colName, rs.getDouble(colName)); break;
		        	
		        case Types.INTEGER:
		            obj.put(colName, rs.getInt(colName));
		            break;

		        case Types.VARCHAR:
		        case Types.CHAR:
		        case Types.LONGVARCHAR:
			        obj.put(colName, rs.getString(colName));
			        break;
			        
		        case Types.NVARCHAR:
			    case Types.NCHAR:
		        case Types.LONGNVARCHAR:
		            obj.put(colName, rs.getNString(colName));
		            break;

		        case Types.TINYINT:
		            obj.put(colName, rs.getByte(colName));
		            break;
		            
		        case Types.SMALLINT:
		            obj.put(colName, rs.getShort(colName));
		            break;
		            
		        case Types.DATE:
		            obj.put(colName, rs.getDate(colName));
		            break;
		            
		        case Types.TIME:
		            obj.put(colName, rs.getTime(colName));
		            break;
		            
		        case Types.TIMESTAMP:
		            obj.put(colName, rs.getTimestamp(colName));
		            break;
		            
		        case Types.BINARY:
		        case Types.VARBINARY:
		            obj.put(colName, rs.getBytes(colName));
		            break;
		            
		        case Types.LONGVARBINARY:
		            obj.put(colName, rs.getBinaryStream(colName));
		            break;
		            
		        case Types.CLOB:
		            obj.put(colName, rs.getClob(colName));
		            break;
		            
		        case Types.NUMERIC:
		        case Types.DECIMAL:
		            obj.put(colName, rs.getBigDecimal(colName));
		            break;
		            
		        case Types.DATALINK:
		            obj.put(colName, rs.getURL(colName));
		            break;
		            
		        case Types.REF:
		            obj.put(colName, rs.getRef(colName));
		            break;
		            
		        case Types.STRUCT:
		        case Types.DISTINCT:
		        case Types.JAVA_OBJECT:
		            obj.put(colName, rs.getObject(colName));
		            break;
		            
		        default:
		            obj.put(colName, rs.getString(i));
		        }
		    }

		    json.put(obj);
		}

		return json;
	}
	
	public static final JSONObject getUserInfo(final String username, final String password) throws SQLException {
		verifyConnection();
		
		PreparedStatement stmt = dbc.prepareStatement(SELECT_USER_INFO_BY_LOGIN);
		stmt.setString(1, username);
		stmt.setString(2, password);

		JSONArray json = convertToJSONArray(stmt.executeQuery());
		return json.length() == 1 ? json.getJSONObject(0) : null;
	}
	
	public static final JSONObject getUserInfo(final int id) throws SQLException {
		verifyConnection();
		
		PreparedStatement stmt = dbc.prepareStatement(SELECT_USER_INFO_BY_USER_ID);
		stmt.setInt(1, id);

		JSONArray json = convertToJSONArray(stmt.executeQuery());
		return json.length() == 1 ? json.getJSONObject(0) : null;
	}
	
	
	public static final Integer registerNewUser(Map<String, String[]> paramMap) throws SQLException {
		verifyConnection();
		
		int newUserId = 0;
		//TODO code to create a new user registration entity in the database...
		//PreparedStatement stmt = dbc.prepareStatement(CREATE_NEW_USER_ACCOUNT);
		//stmt.setInt(1, id);//parameters...
		
		//TODO return the newly created user id...
		return newUserId;
	}
	
	
	public static JSONObject getSequenceInfo(final int id) throws SQLException {
		verifyConnection();
		
		PreparedStatement stmt = dbc.prepareStatement(SELECT_SEQUENCE_INFO_BY_ID);
		stmt.setInt(1, id);
		
		JSONArray json = convertToJSONArray(stmt.executeQuery());
		return json.length() == 1 ? json.getJSONObject(0) : null;
	}
	
	
	public static JSONArray searchForSequences(String search) throws SQLException {
		verifyConnection();
		
		JSONArray matches = new JSONArray();
		
		//TODO remove testing only!!!
		JSONObject obj = new JSONObject();
		obj.putOnce("name", search.toUpperCase());
		obj.putOnce("createdby", "some professional");
		obj.putOnce("createdon", "03/19/14 11:54:12");
		obj.putOnce("size", 2934857);
		obj.putOnce("url", "viewer.jsp?id=1");
		matches.put(obj);
		obj = new JSONObject();
		obj.putOnce("name", search.toUpperCase() + "GF");
		obj.putOnce("createdby", "some educator");
		obj.putOnce("createdon", "06/03/14 22:12:36");
		obj.putOnce("size", 546);
		obj.putOnce("url", "viewer.jsp?id=2");
		matches.put(obj);
		obj = new JSONObject();
		obj.putOnce("name", search.toUpperCase() + "SDKJSV");
		obj.putOnce("createdby", "some silly student");
		obj.putOnce("createdon", "04/23/14 01:05:22");
		obj.putOnce("size", 4957835);
		obj.putOnce("url", "viewer.jsp?id=3");
		matches.put(obj);
		
		return matches;
	}
	
	
	public static JSONObject/*SequenceBlock*/ getSequenceBlock(final int id, final long index) throws SQLException {
		verifyConnection();
		
		PreparedStatement stmt = dbc.prepareStatement(SELECT_SEQUENCE_BLOCK_BY_ID_AND_INDEX);
		stmt.setInt(1, id);
		stmt.setLong(2, index);

		ResultSet rs = stmt.executeQuery();
		JSONObject json = new JSONObject();
		if (rs != null && rs.next()) {
			try {
				json.put("id", id);
				json.put("charset", encoding);
				json.put("length", rs.getInt("length"));
				json.put("block", readFully(rs.getBinaryStream("data"), encoding));
			}
			catch (SQLException | IOException e) {
				e.printStackTrace();
				json.put("error", e.getMessage());
			}
		}
		return json;
	}


	private static String readFully(InputStream in, String encoding)
	        throws IOException
	{
		final int bufferSize = 4096;
	    ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
	    byte[] buffer = new byte[bufferSize];
	    int length = 0;
	    while ((length = in.read(buffer)) != -1) {
	        out.write(buffer, 0, length);
	    }
	    try {
	    	in.close();
	    } catch (Exception ignore) { }
	    try {
	    	out.close();
	    } catch (Exception ignore) { }

	    return new String(out.toByteArray());
	}

	private final static void verifyConnection() throws SQLException {
		if (!hasValidConnection())
			throw new SQLException("Unable to connection to the database");
	}
	
	private final static boolean hasValidConnection() {
		try {
			if (dbc == null)
				connect();
			
			if (dbc == null || !dbc.isValid(2))
				return false;
		}
		catch (SQLException ignore) { /* thrown when timeout is less than 0 */ }
		return true;
	}
	
}
