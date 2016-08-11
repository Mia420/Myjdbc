package org.dc.jdbc.helper;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dc.jdbc.config.JDBCConfig;
import org.dc.jdbc.core.ConnectionManager;
import org.dc.jdbc.core.ContextHandle;
import org.dc.jdbc.core.inter.IConnectionManager;
import org.dc.jdbc.core.operate.DataBaseDao;
import org.dc.jdbc.core.operate.IDataBaseDao;
import org.dc.jdbc.core.sqlhandler.PrintSqlLogHandler;
import org.dc.jdbc.core.sqlhandler.XmlSqlHandler;
import org.dc.jdbc.entity.SqlEntity;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 数据持久化操作类
 * sql执行三部曲：1，sql解析，2，获得数据库连接，3，执行核心jdbc操作。
 * @author dc
 * @time 2015-8-17
 */
public class DBHelper {
	private volatile DataSource dataSource;
	private ContextHandle contextHandler;
	private static IConnectionManager connectionManager = ConnectionManager.getInstance();

	private static IDataBaseDao OPERATE = new DataBaseDao();
	public DBHelper(DruidDataSource dataSource){
		this.dataSource = dataSource;
	}
	/*private void init(){
		final ContextHandle ch = new ContextHandle();

		//初始化程序
		//contextHandler.registerInit(new SQLInitAnalysis());

		//责任链执行sql处理
		//以后需要加入的功能
		//1，解析用户定义的分库分表xml规则，处理一部分逻辑。
		//2，分库分表后，验证参数的合法性
		//3，根据参数动态改变sql语句的功能，如根据用户传入的userId，hash算法动态改变原sql中的表。。完成hash分表的功能
		ch.registerSQLHandle(XmlSqlHandler.getInstance());
		if(JDBCConfig.isPrintSqlLog){
			ch.registerSQLHandle(PrintSqlLogHandler.getInstance());
		}
		this.contextHandler = ch;

		//初始化连接管理
		//this.connectionManager  = ConnectionManager.getInstance();
	}*/

	public <T> T selectOne(String sqlOrID,Class<? extends T> returnClass,Object...params) throws Exception{

		List<T> rtn_list = this.selectList(sqlOrID, returnClass, params);
		if(rtn_list==null){
			return null;
		}else if(rtn_list.size()>1){
			throw new Exception("Query results too much!");
		}else{
			return rtn_list.get(0);
		}
	}
	public Map<String,Object> selectOne(String sqlOrID,Object...params) throws Exception{
		return this.selectOne(sqlOrID, null,params);
	}
	public <T> List<T> selectList(String sqlOrID,Class<? extends T> returnClass,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();
		//1,根据解析出来的sqlentity得到数据源
		Connection conn = connectionManager.getConnection(sqlEntity.getCurrentDataSource());
		return OPERATE.selectList(conn,sql,returnClass,params_obj);
	}
	public List<Map<String,Object>> selectList(String sqlOrID,Object...params) throws Exception{
		return this.selectList(sqlOrID, null, params);
	}
	/**
	 * 返回受影响的行数
	 * @param sqlOrID
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int insert(String sqlOrID,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();

		Connection conn = connectionManager.getConnection(dataSource);
		return OPERATE.insert(conn, sql, params_obj);
	}
	/**
	 * 单条语句插入，返回一个主键
	 * @param sqlOrID
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Object insertReturnKey(String sqlOrID,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();

		Connection conn = connectionManager.getConnection(dataSource);
		return OPERATE.insertRtnPKKey(conn, sql, params_obj);
	}

	public int update(String sqlOrID,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();

		Connection conn = connectionManager.getConnection(dataSource);
		return OPERATE.update(conn, sql, params_obj);
	}


	public int delete(String sqlOrID,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();

		Connection conn = connectionManager.getConnection(dataSource);
		return OPERATE.delete(conn, sql, params_obj);
	}

	public void rollback() throws Exception{
		connectionManager.rollback(dataSource);
	}
}
