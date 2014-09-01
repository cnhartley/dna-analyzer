/**
 * 
 */
package com.dnaa.service.dbi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import com.dnaa.service.dbi.mock.MockDriver;

/**
 * @author Chris
 *
 */
public class DatabaseInterface {

	static {
		try {
			DriverManager.registerDriver( new MockDriver() );
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	};
	
	
	private static Connection dbc = null;

	
	/**
	 * 
	 */
	private static void connect() {
		try {
			Properties props = new Properties();
			props.put("username", "admin");
			props.put("password", "admin");
			dbc = DriverManager.getConnection("jdbc:mockdb:localhost:8080", props);
			
			System.out.println("DatabaseInterface:dbc=[" + dbc + "]");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static Object/*SequenceInfo*/ getSequenceInfo(final int id) {
		hasValidConnection();
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM info WHERE id=");
		sb.append(id);
		
		ResultSet rs = null;
		if (sb.length() > 0) {
			try {
				rs = dbc.prepareStatement(sb.toString()).executeQuery();
				InputStream is = rs.getBinaryStream(0);
				
				sb.setLength(0);
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				while (is.read(buffer.array()) != -1) {
					sb.append( new String(buffer.array()) );
					buffer.clear();
				}
				
				return sb.toString();
			}
			catch (SQLException | IOException e) {
				e.printStackTrace();
			}
		}
		
		return null; // no info for sequence id
	}
	
	
	public static int findSequence(String search) {
		hasValidConnection();
		
		return -1; // not found
	}
	
	
	public static String/*SequenceBlock*/ getSequenceBlock(final int id, final int block) {
		if (!hasValidConnection()) {
			System.err.println("Unable to connect to database!");
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT block FROM sequence_table WHERE id=");
		sb.append(id);
		sb.append(" AND block_id=");
		sb.append(block);
		
		ResultSet rs = null;
		if (sb.length() > 0) {
			try {
				rs = dbc.prepareStatement(sb.toString()).executeQuery();
				return readFully(rs.getBinaryStream(0), "UTF-8");
			}
			catch (SQLException | IOException e) {
				e.printStackTrace();
			}
			catch (NullPointerException npe) {
				System.err.println("Resultset was <null>!");
			}
		}
		
		return null; // no sequence block found
	}
	
	private static String readFully(InputStream inputStream, String encoding)
	        throws IOException {
	    return new String(readFully(inputStream), encoding);
	}    

	private static byte[] readFully(InputStream inputStream)
	        throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int length = 0;
	    while ((length = inputStream.read(buffer)) != -1) {
	        baos.write(buffer, 0, length);
	    }
	    return baos.toByteArray();
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
