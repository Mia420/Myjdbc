package org.dc.jdbc.core;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

public class DataSourceManager {
	private static DataSourceManager dataSourceManager = new DataSourceManager();
	public static DataSourceManager getInstance(){
		return dataSourceManager;
	}
	private DataSourceManager(){}
	
	private static Map<String,DataSource> dataSourceMap = new HashMap<String,DataSource>();
	
	public synchronized void setDateSourceMap(Map<String,DataSource> dataSourceMap){
		dataSourceMap.putAll(dataSourceMap);
	}
	
	public static String getName(DataSource dataSource){
		for (String name : dataSourceMap.keySet()) {
			if(dataSourceMap.get(name)==dataSource){
				return name;
			}
		}
		return null;
	}
	public static DataSource getDataSource(String dataSourceName){
		return dataSourceMap.get(dataSourceName);
	}
}
