/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;


/** 
 * @ClassName: SQLiteDBUtils <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月3日 下午7:29:23 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class SQLiteDBUtils {
	
	private static Log logger = LogFactory.getLog(SQLiteDBUtils.class);

	public static final String DB_NAME = "database.db";
	
	/**
	 * DB connection list refer to APP package name.
	 */
	private static Connection connection = null;

	/**
	 * <p>
	 * Description: The database connection will never be closed until program
	 * is closed.
	 * </p>
	 * 
	 * @date: 2014-6-17
	 */
	public static synchronized Connection getConnection() {
		// cache DB connection to improve performance
		// get connection process cost about 7s on MFP
		if (connection != null) {
			return connection;
		}

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		try {
			String packagePath = DBConstant.CONF_DIR + File.separator ;
			File packageFile = new File(packagePath);
			if (!packageFile.exists()) {
				logger.debug(packagePath + " does not exist, create it!");
				packageFile.mkdirs();
			}
			String dbPath = packagePath + "/" + DB_NAME;
			String dbURL = "jdbc:sqlite:" + dbPath;
			connection = DriverManager.getConnection(dbURL);
			
			 // close auto commit
			connection.setAutoCommit(false);

			// create DB Table if not exist
			boolean initResult = initDB();
			logger.debug("init DB = " + initResult);
			
			if(initResult){
				//TODO
			} else {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			logger.error(e.getMessage());
			try{
				connection.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			connection = null;
		}
		return connection;
	}

	/**
	 * <p>
	 * Description: Close and remove all database connections
	 * </p>
	 * 
	 * @date: 2014-6-17
	 */
	public static synchronized void close() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				logger.error("close db connection exception:" + e.getMessage());
			}
		}
	}

	private static boolean initDB() {
		boolean initResult = true;
		Statement statement = null;
		try {
			statement = connection.createStatement();

			// create table
			statement.addBatch(DBConstant.SQL_CREATE_TABLE_DEVICE);
			
			// create index
			statement.addBatch(DBConstant.SQL_CREATE_INDEX_DEVICE);
			
			statement.executeBatch(); // batch execute to improve performance
			connection.commit();
			statement.clearBatch();
		} catch (Exception e) {
			logger.error("init db exception: " + e.getMessage());
			initResult = false;
			try {
				if (!connection.isClosed()) {
					// roll back if create any table failed
					connection.rollback();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			// Do not close connection for cache and thread safe.
			try {
				if (statement != null) {
					statement.close();
					statement = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return initResult;
	}

	/**
	 * <p>
	 * Description: All database accesses use this method to synchronize
	 * </p>
	 * 
	 * @date: 2014-6-17
	 */
	public static synchronized Result accessDB(String tableName, int type, Object valueObject) {

		Result result = new Result();
		
		if (connection == null) {
			result.setErrorCode(DBException.E_TOOL_DB_CONN);
			result.setReason("get db connect failed");
			return result;
		}

		Statement statement = null;

		PreparedStatement prep = null;

		// SQL request data
		JSONObject json;

		Object value;

		try {
			StringBuilder strBuilder;
			switch (type) {
			case DBConstant.SQL_INSERT:
				// get insert columns first
				JSONArray dataArray = ((JSONArray) valueObject);
				JSONArray columns = dataArray.getJSONObject(0).names();

				// create insert sql
				strBuilder = new StringBuilder().append("insert into ").append(tableName).append(" (");
				for (int i = 0; i < columns.length(); i++) {
					strBuilder.append(columns.getString(i)); // match to DB
					// define
					if (i < columns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(")  values(");
				for (int i = 0; i < columns.length(); i++) {
					strBuilder.append("?");
					if (i < columns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(");");

				String insertSQL = strBuilder.toString();

				prep = connection.prepareStatement(insertSQL);
				for (int i = 0; i < dataArray.length(); i++) {
					json = dataArray.getJSONObject(i);
					for (int j = 1; j < columns.length() + 1; j++) {
						value = json.get(columns.getString(j - 1));
						if (value instanceof String) {
							prep.setString(j, (String) value);
						} else if (value instanceof Integer) {
							prep.setInt(j, ((Integer) value).intValue());
						} else if (value instanceof Double) {
							prep.setDouble(j, ((Double) value).doubleValue());
						} else if (value instanceof Boolean) {
							prep.setBoolean(j, ((Boolean) value).booleanValue());
						}
					}
					prep.addBatch();
				}
				prep.executeBatch(); // batch execute to improve performance
				connection.commit();
				prep.clearBatch();
				prep.close();
				break;
			case DBConstant.SQL_REPLACE:
				// Mao 2014-09-11 Add For JobLog
				// get insert columns first
				JSONArray replaceDataArray = ((JSONArray) valueObject);
				JSONArray replaceColumns = replaceDataArray.getJSONObject(0).names();

				// create insert sql
				strBuilder = new StringBuilder().append("replace into ").append(tableName).append(" (");
				for (int i = 0; i < replaceColumns.length(); i++) {
					strBuilder.append(replaceColumns.getString(i)); // match to DB
					// define
					if (i < replaceColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(")  values(");
				for (int i = 0; i < replaceColumns.length(); i++) {
					strBuilder.append("?");
					if (i < replaceColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(");");

				String replaceSQL = strBuilder.toString();

				prep = connection.prepareStatement(replaceSQL);
				for (int i = 0; i < replaceDataArray.length(); i++) {
					json = replaceDataArray.getJSONObject(i);
					for (int j = 1; j < replaceColumns.length() + 1; j++) {
						value = json.get(replaceColumns.getString(j - 1));
						if (value instanceof String) {
							prep.setString(j, (String) value);
						} else if (value instanceof Integer) {
							prep.setInt(j, ((Integer) value).intValue());
						} else if (value instanceof Double) {
							prep.setDouble(j, ((Double) value).doubleValue());
						} else if (value instanceof Boolean) {
							prep.setBoolean(j, ((Boolean) value).booleanValue());
						}
					}
					prep.addBatch();
				}
				prep.executeBatch(); // batch execute to improve performance
				connection.commit();
				prep.clearBatch();
				prep.close();
				break;
			case DBConstant.SQL_INSERT_IGNORE:
				// Mao 2014-09-11 Add For JobLog
				// get insert columns first
				JSONArray ignoreDataArray = ((JSONArray) valueObject);
				JSONArray ignoreColumns = ignoreDataArray.getJSONObject(0).names();

				// create insert sql
				strBuilder = new StringBuilder().append("insert or ignore into ").append(tableName).append(" (");
				for (int i = 0; i < ignoreColumns.length(); i++) {
					strBuilder.append(ignoreColumns.getString(i)); // match to DB
					// define
					if (i < ignoreColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(")  values(");
				for (int i = 0; i < ignoreColumns.length(); i++) {
					strBuilder.append("?");
					if (i < ignoreColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(");");

				String ignoreSQL = strBuilder.toString();

				prep = connection.prepareStatement(ignoreSQL);
				for (int i = 0; i < ignoreDataArray.length(); i++) {
					json = ignoreDataArray.getJSONObject(i);
					for (int j = 1; j < ignoreColumns.length() + 1; j++) {
						value = json.get(ignoreColumns.getString(j - 1));
						if (value instanceof String) {
							prep.setString(j, (String) value);
						} else if (value instanceof Integer) {
							prep.setInt(j, ((Integer) value).intValue());
						} else if (value instanceof Double) {
							prep.setDouble(j, ((Double) value).doubleValue());
						} else if (value instanceof Boolean) {
							prep.setBoolean(j, ((Boolean) value).booleanValue());
						}
					}
					prep.addBatch();
				}
				prep.executeBatch(); // batch execute to improve performance
				connection.commit();
				prep.clearBatch();
				prep.close();
				break;				
			case DBConstant.SQL_UPDATE:
			case DBConstant.SQL_DELETE:
			case DBConstant.SQL_MIX:
				JSONArray sqlArray = ((JSONArray) valueObject);
				statement = connection.createStatement();
				String sqlStr;
				for (int i = 0; i < sqlArray.length(); i++) {
					sqlStr = sqlArray.getString(i);
					statement.addBatch(sqlStr);
				}
				statement.executeBatch(); // batch execute to improve performance
				connection.commit();
				statement.close();
				break;
			case DBConstant.SQL_SELECT:
				String selectSQL = ((String) valueObject);
				statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(selectSQL);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				int columnType;
				JSONArray records = new JSONArray();
				JSONObject record;
				while (resultSet.next()) {
					record = new JSONObject();
					for (int i = 1; i <= columnCount; i++) {
						columnType = metaData.getColumnType(i);
						if (Types.VARCHAR == columnType) {
							record.put(metaData.getColumnName(i), resultSet.getString(i));
						} else if (Types.INTEGER == columnType) {
							record.put(metaData.getColumnName(i), resultSet.getInt(i));
						} else if (Types.BOOLEAN == columnType) {
							record.put(metaData.getColumnName(i), resultSet.getInt(i));
						} else if (Types.FLOAT == columnType) {
							record.put(metaData.getColumnName(i), resultSet.getDouble(i));
						} else if (Types.DOUBLE == columnType) {
							record.put(metaData.getColumnName(i), resultSet.getDouble(i));
						} else if (Types.NULL == columnType) {
						} else {
							logger.error("exception unknow type, name = " + metaData.getColumnName(i) + ", value = "
									+ resultSet.getObject(i));
						}
					}
					records.put(record);
				}
				resultSet.close();
				statement.close();
				result.setJsonData(records.toString());
				break;							
			default:
				result.setErrorCode(DBException.E_TOOL_DB_OP_NOTSUPPORT);
				result.setReason("error db access type");
				return result;
			}
		} catch (SQLException e) {
			if (DBConstant.SQL_SELECT != type || connection != null) {
				try {
					logger.error("db access SQLException, db connection roll back");
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			logger.error("SQLException: " + e.getMessage());
			result.setErrorCode(DBException.SUCCESS);
			result.setReason(e.getMessage());
			return result;
		} catch (Exception e) {
			if (DBConstant.SQL_SELECT != type || connection != null) {
				try {
					logger.error("db access unknow Exception, db connection roll back");
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			logger.error("Exception: " + e.getMessage());
			result.setErrorCode(DBException.E_INTERNAL);
			result.setReason(e.getMessage());
			return result;
		}
		result.setErrorCode(DBException.SUCCESS);
		return result;
	}

	public static synchronized JSONArray dbSelect(String selectSQL) throws DBException {
		if (connection == null) {
			throw new DBException(DBException.E_TOOL_DB_CONN, "get db connect failed");
		}

		Statement statement = null;

		JSONArray records = new JSONArray();
		JSONObject record;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(selectSQL);
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			int columnType;
			while (resultSet.next()) {
				record = new JSONObject();
				for (int i = 1; i <= columnCount; i++) {
					columnType = metaData.getColumnType(i);
					if (Types.VARCHAR == columnType) {
						record.put(metaData.getColumnName(i), resultSet.getString(i));
					} else if (Types.INTEGER == columnType) {
						record.put(metaData.getColumnName(i), resultSet.getInt(i));
					} else if (Types.BOOLEAN == columnType) {
						record.put(metaData.getColumnName(i), resultSet.getInt(i));
					} else if (Types.FLOAT == columnType) {
						record.put(metaData.getColumnName(i), resultSet.getDouble(i));
					} else if (Types.DOUBLE == columnType) {
						record.put(metaData.getColumnName(i), resultSet.getDouble(i));
					} else if (Types.NULL == columnType) {
					} else {
						logger.error("exception unknow type, name = " + metaData.getColumnName(i) + ", value = "
								+ resultSet.getObject(i));
					}
				}
				records.put(record);
			}
			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			logger.error("SQLException: " + e.getMessage());
			throw new DBException(DBException.E_TOOL_DB_OP, e.getMessage());
		} catch (Exception e) {
			throw new DBException(DBException.E_INTERNAL, e.getMessage());
		}
		return records;
	}

	/**
	 * <p>
	 * Description: All database accesses use this method to synchronize
	 * </p>
	 * 
	 * @date: 2014-6-17
	 */
	public static synchronized void dbUpdate(String tableName, int type, Object valueObject) throws DBException {

		if (connection == null) {
			throw new DBException(DBException.E_TOOL_DB_CONN, "get db connect failed");
		}

		Statement statement = null;

		PreparedStatement prep = null;

		// SQL request data
		JSONObject json;

		Object value;

		try {
			StringBuilder strBuilder;
			switch (type) {
			case DBConstant.SQL_INSERT:
				// get insert columns first
				JSONArray dataArray = ((JSONArray) valueObject);
				JSONArray columns = dataArray.getJSONObject(0).names();

				// create insert sql
				strBuilder = new StringBuilder().append("insert into ").append(tableName).append(" (");
				for (int i = 0; i < columns.length(); i++) {
					strBuilder.append(columns.getString(i)); // match to DB
					// define
					if (i < columns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(")  values(");
				for (int i = 0; i < columns.length(); i++) {
					strBuilder.append("?");
					if (i < columns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(");");

				String insertSQL = strBuilder.toString();

				prep = connection.prepareStatement(insertSQL);
				for (int i = 0; i < dataArray.length(); i++) {
					json = dataArray.getJSONObject(i);
					for (int j = 1; j < columns.length() + 1; j++) {
						value = json.get(columns.getString(j - 1));
						if (value instanceof String) {
							prep.setString(j, (String) value);
						} else if (value instanceof Integer) {
							prep.setInt(j, ((Integer) value).intValue());
						} else if (value instanceof Double) {
							prep.setDouble(j, ((Double) value).doubleValue());
						} else if (value instanceof Boolean) {
							prep.setBoolean(j, ((Boolean) value).booleanValue());
						}
					}
					prep.addBatch();
				}
				prep.executeBatch(); // batch execute to improve performance
				connection.commit();
				prep.clearBatch();
				prep.close();
				break;
			case DBConstant.SQL_REPLACE:
				// Mao 2014-09-11 Add For JobLog
				// get insert columns first
				JSONArray replaceDataArray = ((JSONArray) valueObject);
				JSONArray replaceColumns = replaceDataArray.getJSONObject(0).names();

				// create insert sql
				strBuilder = new StringBuilder().append("replace into ").append(tableName).append(" (");
				for (int i = 0; i < replaceColumns.length(); i++) {
					strBuilder.append(replaceColumns.getString(i)); // match to
																	// DB
					// define
					if (i < replaceColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(")  values(");
				for (int i = 0; i < replaceColumns.length(); i++) {
					strBuilder.append("?");
					if (i < replaceColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(");");

				String replaceSQL = strBuilder.toString();
				prep = connection.prepareStatement(replaceSQL);
				for (int i = 0; i < replaceDataArray.length(); i++) {
					json = replaceDataArray.getJSONObject(i);
					for (int j = 1; j < replaceColumns.length() + 1; j++) {
						value = json.get(replaceColumns.getString(j - 1));
						if (value instanceof String) {
							prep.setString(j, (String) value);
						} else if (value instanceof Integer) {
							prep.setInt(j, ((Integer) value).intValue());
						} else if (value instanceof Double) {
							prep.setDouble(j, ((Double) value).doubleValue());
						} else if (value instanceof Boolean) {
							prep.setBoolean(j, ((Boolean) value).booleanValue());
						}
					}
					prep.addBatch();
				}
				prep.executeBatch(); // batch execute to improve performance
				connection.commit();
				prep.clearBatch();
				prep.close();
				break;
			case DBConstant.SQL_INSERT_IGNORE:
				// Mao 2014-09-11 Add For JobLog
				// get insert columns first
				JSONArray ignoreDataArray = ((JSONArray) valueObject);
				JSONArray ignoreColumns = ignoreDataArray.getJSONObject(0).names();

				// create insert sql
				strBuilder = new StringBuilder().append("insert or ignore into ").append(tableName).append(" (");
				for (int i = 0; i < ignoreColumns.length(); i++) {
					strBuilder.append(ignoreColumns.getString(i)); // match to
																	// DB
					// define
					if (i < ignoreColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(")  values(");
				for (int i = 0; i < ignoreColumns.length(); i++) {
					strBuilder.append("?");
					if (i < ignoreColumns.length() - 1) {
						strBuilder.append(", ");
					}
				}
				strBuilder.append(");");

				String ignoreSQL = strBuilder.toString();

				prep = connection.prepareStatement(ignoreSQL);
				for (int i = 0; i < ignoreDataArray.length(); i++) {
					json = ignoreDataArray.getJSONObject(i);
					for (int j = 1; j < ignoreColumns.length() + 1; j++) {
						value = json.get(ignoreColumns.getString(j - 1));
						if (value instanceof String) {
							prep.setString(j, (String) value);
						} else if (value instanceof Integer) {
							prep.setInt(j, ((Integer) value).intValue());
						} else if (value instanceof Double) {
							prep.setDouble(j, ((Double) value).doubleValue());
						} else if (value instanceof Boolean) {
							prep.setBoolean(j, ((Boolean) value).booleanValue());
						}
					}
					prep.addBatch();
				}
				prep.executeBatch(); // batch execute to improve performance
				connection.commit();
				prep.clearBatch();
				prep.close();
				break;
			case DBConstant.SQL_UPDATE:
			case DBConstant.SQL_DELETE:
			case DBConstant.SQL_MIX:
				JSONArray sqlArray = ((JSONArray) valueObject);
				statement = connection.createStatement();
				String sqlStr;
				for (int i = 0; i < sqlArray.length(); i++) {
					sqlStr = sqlArray.getString(i);
					statement.addBatch(sqlStr);
				}
				statement.executeBatch(); // batch execute to improve
											// performance
				connection.commit();
				statement.close();
				break;
			default:
				throw new DBException(DBException.E_TOOL_DB_OP_NOTSUPPORT, "error db access type");
			}
		} catch (SQLException e) {
			if (DBConstant.SQL_SELECT != type || connection != null) {
				try {
					logger.error("db access SQLException, db connection roll back");
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			logger.error("SQLException: " + e.getMessage());
			throw new DBException(DBException.E_TOOL_DB_OP, e.getMessage());
		} catch (Exception e) {
			if (DBConstant.SQL_SELECT != type || connection != null) {
				try {
					logger.error("db access unknow Exception, db connection roll back");
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new DBException(DBException.E_INTERNAL, e.getMessage());
		}
	}

	public static String getInsertSQL(JSONObject jsonObject, String tableName){
		return getInsertSQL(jsonObject, tableName, false);
	}

	public static String getInsertSQL(JSONObject jsonObject, String tableName, boolean canIgnore) {
		JSONArray columns = jsonObject.names();

		String sqlCommand = "insert into ";
		if (canIgnore) {
			sqlCommand = "insert or ignore into ";
		}
		StringBuilder strBuilder = new StringBuilder().append(sqlCommand).append(tableName).append(" (");
		for (int i = 0; i < columns.length(); i++) {
			strBuilder.append(columns.getString(i));
			if (i < columns.length() - 1) {
				strBuilder.append(", ");
			}
		}
		strBuilder.append(")  values(");
		Object value;
		for (int i = 0; i < columns.length(); i++) {
			value = jsonObject.get(columns.getString(i));
			if (value instanceof String) {
				strBuilder.append("'").append(((String) value).replace("'", "''")).append("'");
			} else if (value instanceof Integer) {
				strBuilder.append(((Integer) value).intValue());
			} else if (value instanceof Double) {
				strBuilder.append(((Double) value).doubleValue());
			} else if (value instanceof Boolean) {
				boolean bool = ((Boolean) value).booleanValue();
				if (bool) {
					strBuilder.append(1);
				} else {
					strBuilder.append(0);
				}
			}
			if (i < columns.length() - 1) {
				strBuilder.append(", ");
			}
		}
		strBuilder.append(")");

		return strBuilder.toString();
	}

}
