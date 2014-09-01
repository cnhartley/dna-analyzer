/**
 * 
 */
package com.dnaa.service.dbi.mock;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author Chris
 *
 */
public class MockConnection implements Connection {

	// Member data.
	private boolean autoCommitIsEnabled = true;
	private boolean connectionIsClosed = false;
	private boolean connectionIsReadOnly = false;
	private String connectionSchema = null;
	private String connectionCatalog = null;
	private int connectionTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
	private Properties clientProperties = new Properties();
	
	private final List<Object> commitList = new LinkedList<Object>();
	private final List<SQLWarning> warnings = new LinkedList<SQLWarning>();
	
	
	/**
	 * 
	 */
	public MockConnection() {
		System.out.println("new instance of MockConnection!");
	}
	public MockConnection(Properties properties) {
		System.out.println("new instance of MockConnection(" + properties + ")!");
		try {
			setClientInfo(properties);
		} catch (SQLClientInfoException ignore) { }
	}


	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void abort(Executor executor) throws SQLException {
		if (executor == null)
			throw new SQLException("executor passed is null!");
		
		close();
	}


	@Override
	public void clearWarnings() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		warnings.clear();
	}


	@Override
	public void close() throws SQLException {
		connectionIsClosed = true;
		
		//TODO close connection...
	}


	@Override
	public void commit() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		if (autoCommitIsEnabled)
			throw new SQLException("auto-commit is enabled!");
		
		for (Object change : commitList) {
			//TODO apply the change for each item in the commit list...
			change.toString();
		}
		commitList.clear();
	}


	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public Blob createBlob() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public Clob createClob() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public NClob createNClob() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public SQLXML createSQLXML() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public Statement createStatement() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		Statement stmt = (Statement)createMockCallableStatement();
		return stmt;
	}
	

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		if (typeName == null)
			throw new SQLException("typeName is null!");
		
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean getAutoCommit() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return autoCommitIsEnabled;
	}


	@Override
	public String getCatalog() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return connectionCatalog;
	}


	@Override
	public Properties getClientInfo() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return clientProperties;
	}


	@Override
	public String getClientInfo(String name) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return clientProperties != null ? clientProperties.getProperty(name) : null;
	}


	@Override
	public int getHoldability() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getNetworkTimeout() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public String getSchema() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return connectionSchema;
	}


	@Override
	public int getTransactionIsolation() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return connectionTransactionIsolation;
	}


	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public SQLWarning getWarnings() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return warnings.size() > 0 ? warnings.remove(0) : null;
	}


	@Override
	public boolean isClosed() throws SQLException {
		return connectionIsClosed;
	}


	@Override
	public boolean isReadOnly() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		return connectionIsReadOnly;
	}


	@Override
	public boolean isValid(int timeout) throws SQLException {
		if (timeout < 0)
			throw new SQLException("timeout is less then 0!");
		
		return !connectionIsClosed;
	}


	@Override
	public String nativeSQL(String sql) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		CallableStatement stmt = (CallableStatement)createMockCallableStatement();
		return stmt;
	}


	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		PreparedStatement stmt = (PreparedStatement)createMockCallableStatement(sql);
		return stmt;
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		PreparedStatement stmt = null;
		switch (autoGeneratedKeys) {
			case Statement.RETURN_GENERATED_KEYS:
			case Statement.NO_GENERATED_KEYS:
				stmt = (PreparedStatement)createMockCallableStatement();
				//TODO stmt.setGeneratedKeys(autoGeneratedKeys);
				break;
			default:
				throw new SQLException("autoGeneratedKeys is not either Statement.RETURN_GENERATED_KEYS or Statement.NO_GENERATED_KEYS!");
		}
		return stmt;
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		PreparedStatement stmt = (PreparedStatement)createMockCallableStatement();
		//TODO stmt.setColumnIndexes(columnIndexes);
		return stmt;
	}


	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		PreparedStatement stmt = (PreparedStatement)createMockCallableStatement();
		//TODO stmt.setColumnNames(columnNames);
		return stmt;
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public void rollback() throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		if (autoCommitIsEnabled)
			throw new SQLException("auto-commit is enabled!");
		
		for (Object change : commitList) {
			//TODO remove each change in the commit list...
			change.toString();
		}
	}


	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		autoCommitIsEnabled = autoCommit;
	}
	

	@Override
	public void setCatalog(String catalog) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		connectionCatalog = catalog;
	}


	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		if (connectionIsClosed)
			throw new SQLClientInfoException("connection is closed!", null);
		
		clientProperties = properties;
	}


	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		if (connectionIsClosed)
			throw new SQLClientInfoException("connection is closed!", null);
		
		clientProperties.put(name, value);
	}


	@Override
	public void setHoldability(int holdability) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		connectionIsReadOnly = readOnly;
	}


	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}


	@Override
	public void setSchema(String schema) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		connectionSchema = schema;
	}


	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if (connectionIsClosed)
			throw new SQLException("connection is closed!");
		
		switch (level) {
			case Connection.TRANSACTION_READ_UNCOMMITTED:
			case Connection.TRANSACTION_READ_COMMITTED:
			case Connection.TRANSACTION_REPEATABLE_READ:
			case Connection.TRANSACTION_SERIALIZABLE:
				connectionTransactionIsolation = level;
				break;
			default:
				throw new SQLException("invalid connection constraint! level=" + level);
		}
	}


	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
	
	
	private final CallableStatement createMockCallableStatement() throws SQLException {
		CallableStatement stmt = new MockCallableStatement(this);
		
		try {
			stmt.setQueryTimeout(getNetworkTimeout());
		} catch (SQLFeatureNotSupportedException ignore) { }
		
		return stmt;
	}
	
	
	private final CallableStatement createMockCallableStatement(String sql) throws SQLException {
		CallableStatement stmt = new MockCallableStatement(this, sql);
		
		try {
			stmt.setQueryTimeout(getNetworkTimeout());
		} catch (SQLFeatureNotSupportedException ignore) { }
		
		return stmt;
	}

}
