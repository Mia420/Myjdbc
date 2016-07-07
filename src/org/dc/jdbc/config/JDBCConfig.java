package org.dc.jdbc.config;

public class JDBCConfig {
	public static ConfigManager configManager = ConfigManager.getInstance();
	
	public static boolean isPrintSqlLog;
	public static boolean isSQLCache;
	public static String tableCache;
	public static String profileName;
	
	public static void setProFileName(String profileName){
		JDBCConfig.profileName = profileName;
		isPrintSqlLog = Boolean.parseBoolean(configManager.getProperty(profileName, "isPrintSqlLog"));
		isSQLCache = Boolean.parseBoolean(configManager.getProperty(profileName, "isSQLCache"));
		tableCache = configManager.getProperty(profileName, "tableCache");
	}
}
