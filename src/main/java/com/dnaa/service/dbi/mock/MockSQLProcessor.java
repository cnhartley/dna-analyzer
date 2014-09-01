/**
 * 
 */
package com.dnaa.service.dbi.mock;

import java.sql.ResultSet;
import java.util.StringTokenizer;

/**
 * @author Chris
 *
 */
public class MockSQLProcessor {

	private final String sql;
	
	
	/**
	 * 
	 */
	public MockSQLProcessor(String sql) {
		this.sql = sql != null ? "" + sql : "";
	}

	public ResultSet process() {
		StringTokenizer sqlTokens = new StringTokenizer(sql);
		ResultSet rs = null;
		
		if (sqlTokens.hasMoreTokens()) {
			switch (sqlTokens.nextToken()) {
			case "SELECT": rs = processSelect(sqlTokens); break;
			default: rs = new MockResultSet(); // empty result set
			}
		}
		return rs;
	}

	private ResultSet processSelect(StringTokenizer sqlTokens) {
		String fields = "";
		String[] from = null;
		
		if (sqlTokens.hasMoreTokens())
			fields = sqlTokens.nextToken();
		
		if (sqlTokens.hasMoreTokens()) {
			switch (sqlTokens.nextToken()) {
			case "FROM": from = processFrom(sqlTokens); break;
			}
		}
			
		return null;
	}

	private String[] processFrom(StringTokenizer sqlTokens) {
		// TODO Auto-generated method stub
		return null;
	}

	private MockDataSource db;
	
	class MockDataSource {
		
		private Object[][] table = null;
		
		
	}
}
