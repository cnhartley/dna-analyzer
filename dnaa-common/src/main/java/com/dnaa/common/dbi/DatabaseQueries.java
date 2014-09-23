/**
 * 
 */
package com.dnaa.common.dbi;

/**
 * @author Chris
 *
 */
public interface DatabaseQueries {

	/**
	 * SQL query string to SELECT for user information without a WHERE clause.
	 */
	static final String SELECT_USER_INFO =
			"SELECT * FROM dnaa.`current_users_view` ";
	
	/**
	 * SQL query string to SELECT for a specific user id based on the parameters
	 * of  the {@code username}, first '?', and the {@code password}, second '?'.
	 */
	static final String SELECT_USER_ID_BY_LOGIN =
			"SELECT id FROM dnaa.user AS u "
			+ "WHERE u.username = ? AND u.password = ? "
			+ "LIMIT 1";
	
	/**
	 * SQL query string to SELECT for user information based on the parameters
	 * of the {@code username}, first '?', and the {@code password}, second '?'
	 * for the query string {@link #SELECT_USER_ID_BY_LOGIN}.
	 */
	static final String SELECT_USER_INFO_BY_LOGIN = 
			"SELECT * FROM dnaa.`current_users_view` "
			+ "WHERE id = (" + SELECT_USER_ID_BY_LOGIN + ") LIMIT 1";
	
	/**
	 * SQL query string to SELECT for user information based on the parameter
	 * of the {@code id}, first '?'.
	 */
	static final String SELECT_USER_INFO_BY_USER_ID =
			"SELECT * FROM dnaa.`current_users_view` "
			+ "WHERE id = ?";
	
	/**
	 * SQL query string to SELECT for sequence information based on the 
	 * parameter of the {@code id}, first '?'.
	 */
	static final String SELECT_SEQUENCE_INFO_BY_ID =
			"SELECT * FROM dnaa.sequence_info AS i WHERE i.id = ?";
	
	/**
	 * SQL query string to SELECT for a specific sequence block based on the 
	 * parameters of the {@code sequence_info_id}, first '?', and the 
	 * {@code index}, second '?'.
	 */
	static final String SELECT_SEQUENCE_BLOCK_BY_ID_AND_INDEX =
			"SELECT b.length, b.data "
			+ "FROM dnaa.sequence_blocks AS b "
			+ "WHERE b.sequence_info_id = ? AND b.index = ?";
	
	/**
	 * SQL string to create a new user account, not verified yet, based on the
	 * parameters of (in order):<ol>
	 * <li>User's login name, {@code username}</li>
	 * <li>Their password, {@code password}</li>
	 * <li>A valid email address, {@code email}</li>
	 * <li>Their first name, {@code first_name} <em>[optional]</em></li>
	 * <li>And last name, {@code last_name} <em>[optional]</em></li>
	 * </ol>
	 */
	static final String CREATE_NEW_USER_ACCOUNT = 
			"INSERT INTO dnaa.user"
			+ "(username, password, email, first_name, last_name) "
			+ "VALUES (?, ?, ?, ?, ?)";
	
}
