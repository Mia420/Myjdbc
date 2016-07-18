package org.dc.jdbc.core;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dc.jdbc.config.JDBCConfig;

/**
 * 连接管理
 * @author dc
 * @time 2015-8-17
 */
public class ConnectionManagerRWDB extends ConnectionManager{
	private static final Log jdbclog = LogFactory.getLog(ConnectionManagerRWDB.class);


	private static List<DataSource> readDataSourceList = new ArrayList<DataSource>();
	private static List<DataSource> writeDataSourceList = new ArrayList<DataSource>();

	private static AtomicInteger readNumCounter = new AtomicInteger(0);
	private static AtomicInteger writeNumCounter = new AtomicInteger(0);

	private static List<DataSource> dataSourceBadList = new ArrayList<DataSource>();

	private static Lock lock = new ReentrantLock();

	static{//加载配置
		if(JDBCConfig.isSepDB){
			String readDB = JDBCConfig.readDB;
			if(readDB!=null && readDB.trim().length()>0){
				for (String name : readDB.split(",")) {
					readDataSourceList.add(DataSourceManager.getDataSource(name));
				}
			}

			String writeDB = JDBCConfig.writeDB;
			if(writeDB!=null && writeDB.trim().length()>0){
				for (String name : writeDB.split(",")) {
					writeDataSourceList.add(DataSourceManager.getDataSource(name));
				}
			}
		}

		//启动已损坏数据源在线监控
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try{
						if(dataSourceBadList.size()>0){
							for (DataSource dataSource:dataSourceBadList) {
								try {
									Connection conn = dataSource.getConnection();
									if(conn!=null){
										try{
											lock.lock();
											if(readDataSourceList.contains(dataSource)){
												readDataSourceList.add(dataSource);
											}
											if(writeDataSourceList.contains(dataSource)){
												writeDataSourceList.add(dataSource);
											}
											dataSourceBadList.remove(dataSource);
										}catch(Exception e){
											jdbclog.error("",e);
										}finally{
											lock.unlock();
											conn.close();
										}
									}
								} catch (Exception e) {
									jdbclog.error("数据源健康监测问题",e);
								}
							}
						}
						Thread.sleep(10000);
					}catch(Exception e){
						jdbclog.error("",e);
					}
				}
			}
		}).start();
	}

	@Override
	public Connection getConnection(DataSource dataSource) throws Exception {
		if(readDataSourceList.contains(dataSource)){
			return this.getRWConnection(dataSource, readDataSourceList, readNumCounter);
		}else if(writeDataSourceList.contains(dataSource)){
			return this.getRWConnection(dataSource, writeDataSourceList, writeNumCounter);
		}
		return super.getConnection(dataSource);
	}
	private Connection getRWConnection(DataSource dataSource,List<DataSource> list,AtomicInteger counter){
		while(true){
			int read_len = list.size();
			if(read_len>0){
				int index = counter.incrementAndGet()%read_len;
				dataSource = list.get(index);
				try{
					return super.getConnection(dataSource);
				}catch(Exception e){
					jdbclog.warn("链接获取失败，直接剔除此节点，启用下一个节点");
					try {
						ConnectionManagerRWDB.delDBNode(list, dataSource);
					} catch (Exception e1) {
						jdbclog.error("",e1);
					}
				}
			}else{
				return null;
			}
		}
	}
	private static synchronized void delDBNode(List<DataSource> list,DataSource dataSource) throws Exception{
		if(list.contains(dataSource)){
			list.remove(dataSource);
		}
		if(!dataSourceBadList.contains(dataSource)){
			try{
				lock.lock();
				dataSourceBadList.add(dataSource);
			}catch(Exception e){
				throw e;
			}finally{
				lock.unlock();
			}
		}
	}
}
