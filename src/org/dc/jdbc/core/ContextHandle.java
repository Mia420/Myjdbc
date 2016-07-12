package org.dc.jdbc.core;

import org.dc.jdbc.core.sqlhandler.SQLHandler;
import org.dc.jdbc.entity.SqlEntity;

public class ContextHandle {
	private volatile SQLHandler firsthandle = null;
	public  void registerSQLHandle(SQLHandler handle){
			handle.setSuccessor(firsthandle);
			firsthandle = handle;
	}
	/*public  void registerInit(InitHandler...initHandlers){
		for (int i = 0; i < initHandlers.length; i++) {
			try {
				initHandlers[i].init();
			} catch (Exception e) {
				jdbclog.error("SQL initialization exception",e);
			}
		}
	}*/
	public SqlEntity handleRequest(String sqlOrID, Object[] params) throws Exception {
		return firsthandle.handleRequest(sqlOrID, params);
	}
}
