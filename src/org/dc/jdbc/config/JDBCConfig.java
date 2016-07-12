package org.dc.jdbc.config;

/**
 * 全局框架配置
 * @author DC
 *
 */
public class JDBCConfig {
	
	private JDBCConfig(){}
	private static JDBCConfig config = new JDBCConfig();
	public static JDBCConfig getInstance(){
		return config;
	}
	
	public static ConfigManager configManager = ConfigManager.getInstance();
	
	/**是否打印详细sql语句**/
	public static boolean isPrintSqlLog;
	/**是否启用sql结果集二级缓存策略**/
	public static boolean isSQLCache;
	
	/**二级缓存策略配置**/
	public static String tableCache;
	
	/**myjdbc配置文件读取路径**/
	public static String profileName;
	
	/**是否启用读写分离配置**/
	public static boolean isSepDB = false;
	
	public static String writeDB;
	public static String backupWriteDB;
	public static String readDB;
	public static String backupReadDB;
	
	public static void setProFileName(String profileName){
		JDBCConfig.profileName = profileName;
		isPrintSqlLog = Boolean.parseBoolean(configManager.getProperty(profileName, "isPrintSqlLog"));
		
		isSQLCache = Boolean.parseBoolean(configManager.getProperty(profileName, "isSQLCache"));
		tableCache = configManager.getProperty(profileName, "tableCache");
		
		isSepDB = Boolean.parseBoolean(configManager.getProperty(profileName, "isSepDB"));
		writeDB = configManager.getProperty(profileName, "writeDB");
		backupWriteDB = configManager.getProperty(profileName, "backupWriteDB");
		readDB = configManager.getProperty(profileName, "readDB");
		backupReadDB = configManager.getProperty(profileName, "backupReadDB");
	}
}
