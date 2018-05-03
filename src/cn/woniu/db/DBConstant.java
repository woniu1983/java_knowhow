package cn.woniu.db;

/** 
 * @ClassName: DBConstant <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu983 
 * @version  
 * @since JDK 1.6 
 */
public class DBConstant {
	public static final String CONF_DIR		= "config"; 
	
	// SQL type
	public static final int SQL_INSERT 			= 1;
	public static final int SQL_DELETE 			= 2;
	public static final int SQL_UPDATE 			= 3;
	public static final int SQL_SELECT 			= 4;
	public static final int SQL_MIX 			= 5;
	public static final int SQL_REPLACE 		= 6;
	public static final int SQL_INSERT_IGNORE 	= 7;


	/* DB */
	public static final String TABLE_DEVICE				= "device";

	public class DeviceTable {
		public static final String COLUMN_MFP_IP 			= "ip";
		public static final String COLUMN_NAME 				= "admin";
		public static final String COLUMN_PASSWORD 			= "password";
	}

	public static final String SQL_CREATE_TABLE_DEVICE = "CREATE TABLE IF NOT EXISTS " 
			+  DBConstant.TABLE_DEVICE
			+ " ("
			+  DBConstant.DeviceTable.COLUMN_MFP_IP +              " VARCHAR(32) PRIMARY KEY NOT NULL,"
			+  DBConstant.DeviceTable.COLUMN_NAME +                " VARCHAR(128) NOT NULL,"
			+  DBConstant.DeviceTable.COLUMN_PASSWORD +            " VARCHAR(128) NOT NULL"
			+ " );";


	/* 建立索引  */

	public static final String INDEX_IP = "ipindex";
	public static final String SQL_CREATE_INDEX_DEVICE = "CREATE INDEX IF NOT EXISTS " + INDEX_IP + " ON "
			+  DBConstant.TABLE_DEVICE
			+ " ("
			+  DBConstant.DeviceTable.COLUMN_MFP_IP
			+ " );";

}
