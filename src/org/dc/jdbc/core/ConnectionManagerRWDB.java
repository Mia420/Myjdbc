package org.dc.jdbc.core;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.dc.jdbc.config.JDBCConfig;

/**
 * 连接管理
 * @author dc
 * @time 2015-8-17
 */
public class ConnectionManagerRWDB extends ConnectionManager{

	private static final Map<String,DataSource> readDataSource = new HashMap<String,DataSource>();
	private static DataSource[] readDataSourceArr = null;
	private static int readNum;
	private static final Map<String,DataSource> writeDataSource = new HashMap<String,DataSource>();
	private static DataSource[] writeDataSourceArr = null;
	private static int writeNum;

	private static AtomicInteger readNumCounter = new AtomicInteger(0);
	private static AtomicInteger writeNumCounter = new AtomicInteger(0);

	static{//加载配置
		if(JDBCConfig.isSepDB){
			String readDB = JDBCConfig.readDB;
			if(readDB!=null && readDB.trim().length()>0){
				for (String name : readDB.split(",")) {
					readDataSource.put(name, DataSourceManager.getDataSource(name));
				}
				readDataSourceArr = (DataSource[]) readDataSource.values().toArray();
				readNum = readDataSourceArr.length;
			}

			String writeDB = JDBCConfig.writeDB;
			if(writeDB!=null && writeDB.trim().length()>0){
				for (String name : writeDB.split(",")) {
					writeDataSource.put(name, DataSourceManager.getDataSource(name));
				}
				writeDataSourceArr = (DataSource[]) writeDataSource.values().toArray();
				writeNum = writeDataSourceArr.length;
			}
		}
	}

	@Override
	public Connection getConnection(DataSource dataSource) throws Exception {
		if(readDataSource.containsValue(dataSource)){
			dataSource = readDataSourceArr[readNumCounter.incrementAndGet()%readNum];
		}else if(writeDataSource.containsValue(dataSource)){
			dataSource = writeDataSourceArr[writeNumCounter.incrementAndGet()%writeNum];
		}
		return super.getConnection(dataSource);
	}
}
