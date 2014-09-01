/**
 * 
 */
package com.dnaa.service.dbi.mock;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Chris
 *
 */
public class MockDriver implements Driver {

	// default values.
	private static final String _defaultUsername = "admin";
	private static final String _defaultPassword = "admin";
	
	// member data.
	private final Properties info;
	
	
	public MockDriver() {
		this.info = new Properties();
		this.info.put("username", _defaultUsername);
		this.info.put("password", _defaultPassword);
	}

	
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url != null && url.equalsIgnoreCase("jdbc:mockdb:localhost:8080");
	}

	
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		System.out.println("MockDriver.connect(\"" + url + "\", " + info + ") called!");
		
		// TODO Auto-generated method stub
		return new MockConnection(info);
	}

	
	@Override
	public int getMajorVersion() {
		return 1;
	}

	
	@Override
	public int getMinorVersion() {
		return 99;
	}

	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	
	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException
	{
		List<DriverPropertyInfo> props = new LinkedList<DriverPropertyInfo>();
		for (Object key : this.info.keySet())
			props.add( new DriverPropertyInfo("" + key, "" + this.info.get(key)) );
		
		for (Object key : info.keySet())
			props.add( new DriverPropertyInfo("" + key, "" + info.get(key)) );
		
		return props.toArray( new DriverPropertyInfo[1] );
	}

	
	@Override
	public boolean jdbcCompliant() {
		return false;
	}

}
