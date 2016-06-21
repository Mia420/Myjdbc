package org.dc.jdbc.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dc.jdbc.config.JDBCConfig;
import org.dc.jdbc.core.ConnectionManager;
import org.dc.jdbc.core.ContextHandle;
import org.dc.jdbc.core.operate.DeleteOper;
import org.dc.jdbc.core.operate.InsertOper;
import org.dc.jdbc.core.operate.SelectOper;
import org.dc.jdbc.core.operate.UpdateOper;
import org.dc.jdbc.core.sqlhandler.PrintSqlLogHandler;
import org.dc.jdbc.core.sqlhandler.XmlSqlHandler;
import org.dc.jdbc.entity.SqlEntity;

import com.alibaba.druid.pool.DruidDataSource;

import redis.clients.jedis.JedisPool;

/**
 * 数据持久化操作类
 * sql执行三部曲：1，sql解析，2，获得数据库连接，3，执行核心jdbc操作。
 * @author dc
 * @time 2015-8-17
 */
public class DBHelper {
	private volatile DataSource dataSource;
	private final ContextHandle contextHandler;
	
	public static Map<String,DataSource> dataSourceMaps = new HashMap<String,DataSource>();

	public DBHelper(DruidDataSource dataSource){
		this.dataSource = dataSource;
		this.contextHandler = this.createContextHandle();
		dataSourceMaps.put(dataSource.getName(), dataSource);
	}
	public DBHelper(DruidDataSource dataSource,JedisPool jedisPool){
		this.dataSource = dataSource;
		this.contextHandler = this.createContextHandle();
		dataSourceMaps.put(dataSource.getName(), dataSource);
	}
	private ContextHandle createContextHandle(){
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
		return ch;
	}

	private static final SelectOper selectOper = SelectOper.getInstance();
	private static final UpdateOper updateOper = UpdateOper.getInstance();
	private static final InsertOper insertOper = InsertOper.getInstance();
	private static final DeleteOper deleteOper = DeleteOper.getInstance();

	public <T> T selectOne(String sqlOrID,Class<? extends T> returnClass,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();
		
		List<T> rtn_list = selectOper.selectList(dataSource, sql, returnClass, params_obj,1);
		if(rtn_list!=null){
			return rtn_list.get(0);
		}
		return null;
	}
	public Map<String,Object> selectOne(String sqlOrID,Object...params) throws Exception{
		return this.selectOne(sqlOrID, null,params);
	}
	public <T> List<T> selectList(String sqlOrID,Class<? extends T> returnClass,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();
		
		return selectOper.selectList(dataSource,sql,returnClass,params_obj,2);
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
	
		return insertOper.insert(dataSource, sql, params_obj);
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
		return insertOper.insertRtnPKKey(dataSource, sql, params_obj);
	}

	public int update(String sqlOrID,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();
		return updateOper.update(dataSource, sql, params_obj);
	}


	public int delete(String sqlOrID,Object...params) throws Exception{
		SqlEntity sqlEntity = contextHandler.handleRequest(sqlOrID,params);
		String sql = sqlEntity.getSql();
		Object[] params_obj = sqlEntity.getParams();
		return deleteOper.delete(dataSource, sql, params_obj);
	}

	public void rollback() throws Exception{
		ConnectionManager.rollback(dataSource);
	}
}
