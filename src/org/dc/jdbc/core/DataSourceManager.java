package org.dc.jdbc.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceManager {
	private static Log jdbclog = LogFactory.getLog(DataSourceManager.class);
	private static DataSourceManager dataSourceManager = new DataSourceManager();
	public static DataSourceManager getInstance(){
		return dataSourceManager;
	}
	private DataSourceManager(){}
	
	private Map<String,DruidDataSource> dataSourceMap = new HashMap<String,DruidDataSource>();
	public synchronized void registerDataSource(DruidDataSource dataSource){
		if(dataSourceMap.containsKey(dataSource.getName()) || dataSourceMap.containsValue(dataSource)){
			dataSourceMap.clear();
			jdbclog.error("数据源名字或者数据源有重复！");
		}else{
			dataSourceMap.put(dataSource.getName(), dataSource);
			jdbclog.info("数据源"+dataSource.getName()+"注册成功");
		}
	}
	
	public String getName(DruidDataSource dataSource){
		return dataSource.getName();
	}
	public DruidDataSource getDataSource(String dataSourceName){
		return dataSourceMap.get(dataSourceName);
	}
}
