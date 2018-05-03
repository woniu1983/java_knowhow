package cn.woniu.db;

public class DBException extends Exception {

	/** 
	 * serialVersionUID:TODO  
	 */ 
	private static final long serialVersionUID = 2938065489568246178L;

	private int code = -1;

	public DBException(int code, String message) {
		super(message);
		this.code 		= code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/** 成功 */
	public static final int SUCCESS		 					= 0;
	
	/*  初始化 */
	/** 内部错误 */
	public static final int E_INTERNAL	 					= 10000;
	
	/** IP地址错误，不是Ip v4地址 */
	public static final int E_IP_INVALID 					= 10001;
	
	/** conf.acl 配置文件可能不存在或者损坏  */
	public static final int E_TOOL_CONF_INVALID 			= 10002;
	
	/** DB 连接失败 */
	public static final int E_TOOL_DB_CONN 					= 10003;
	
	/** DB 处理失败 */
	public static final int E_TOOL_DB_OP 					= 10004;
	
	/** DB 处理不支持 */
	public static final int E_TOOL_DB_OP_NOTSUPPORT 		= 10005;
	
	
	/*  获取打印机信息  */
	/** 获取打印机信息失败 */
	public static final int E_DEVICE_OBTAIN 				= 20001;
	
	/** 获取打印机固件信息失败 */
	public static final int E_DEVICE_FW_OBTAIN 				= 20002;
	
	/** 连接打印机失败 */
	public static final int E_DEVICE_CONN_FAIL 				= 20003;
	
	/** 重启打印机失败 */
	public static final int E_DEVICE_REBOOT_FAIL			= 20004;
	
	/** 设置Java Heap Stack 失败  */
	public static final int E_DEVICE_HEAP_FAIL				= 20005;
	
	
	/* 安装 */
	/** 安装文件有问题, productID为空或者安装包路径为空 */
	public static final int E_INSTALL_PKG_INVALID 			= 30001;
	
	/** 安装文件不存在 */
	public static final int E_INSTALL_PKG_NOT_EXIST 		= 30002;
	
	/** 安装文件不在支持范围 */
	public static final int E_INSTALL_PKG_NOT_SUPPORT 		= 30003;	
	
	/** 打印机不支持： 不是SOP面板机器  */
	public static final int E_INSTALL_DEVICE_NOT_SOP 		= 30004;	
	
	/** 打印机不支持： 不是支持的机种  */
	public static final int E_INSTALL_DEVICE_NOT_SUPPORT 	= 30005;
	
	/** 安装失败: 超时或者其他原因 */
	public static final int E_INSTALL_FAIL				 	= 30006;
	
	
	/* 卸载 */
	/** 卸载失败：   */
	public static final int E_UNINSTALL_FAIL 				= 40001;
	
	
	/* SP 配置*/	
	/** SP：  找不到 RXSPServlet的安装包 */
	public static final int E_SP_RXSPSERVLET_INSTALL_NOTFOUND	= 50001;
	/** SP：  RXSPServlet安装失败 */
	public static final int E_SP_RXSPSERVLET_INSTALL_FAIL		= 50002;
	/** SP：  RXSPServlet启动失败 */
	public static final int E_SP_RXSPSERVLET_START_FAIL			= 50003;
	/** SP：  ACL文件丢失/找不到/读取失败 */
	public static final int E_SP_ACL_READ_IO					= 50004;
	/** SP：  连接打印机失败 */
	public static final int E_SP_MFP_CONN_FAIL					= 50005;
	/** SP：  获取SP失败 */
	public static final int E_SP_OBTAIN_FAIL					= 50006;
	/** SP：  设置SP失败 */
	public static final int E_SP_SET_FAIL						= 50007;
	
	/* WIM 配置*/	
	/** WIM: 连接不上， 或者出现异常 */
	public static final int E_WIM_CONN_FAIL						= 60001;
	/** WIM: Login失败 */
	public static final int E_WIM_LOGIN_FAIL					= 60002;
	/** WIM: 用户验证管理 设置失败 */
	public static final int E_WIM_UAM_CONF_FAIL					= 60003;
	/** WIM: 用户验证管理 保存设置失败 */
	public static final int E_WIM_UAM_SAVE_FAIL					= 60004;
	
	/** WIM: 打印容量使用限制 保存设置失败 */
	public static final int E_WIM_TRACKING_SAVE_FAIL			= 60005;
	
	/** WIM: SOP面板设置： 保存设置失败 */
	public static final int E_WIM_SOP_SAVE_FAIL					= 60006;

}
