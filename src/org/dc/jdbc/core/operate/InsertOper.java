package org.dc.jdbc.core.operate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.dc.jdbc.cache.core.JedisHelper;
import org.dc.jdbc.core.ConnectionManager;
import org.dc.jdbc.core.base.OperSuper;

import com.alibaba.druid.pool.DruidDataSource;
/**
 * 添加操作
 * @author dc
 * @time 2015-8-17
 */
public class InsertOper extends OperSuper{
	private static final JedisHelper jedisHelper = JedisHelper.getInstance();
    private static InsertOper oper = new InsertOper();
    public static InsertOper getInstance(){
        return oper;
    }
	public int insert(DruidDataSource dataSource,String sql,Object[] params) throws Exception{
		String key = super.getSQLKey(sql, params, dataSource);
		jedisHelper.delSQLCache(key);
		
		Connection conn = ConnectionManager.getConnection(dataSource);
	    return super.preparedAndExcuteSQL(conn, sql, params);
	}
	public int[] insertBatch(DruidDataSource dataSource,String sql,Object[][] params) throws Exception{
		PreparedStatement ps = null;
		try {
			Connection conn = ConnectionManager.getConnection(dataSource);
			ps = conn.prepareStatement(sql);
			if(params!=null && params.length>0){
				for (int i = 0; i < params.length; i++) {
					for (int j = 0; j < params[i].length; j++) {
						ps.setObject(i+1, params[i][j]);
					}
					ps.addBatch();
				}
			}
			return ps.executeBatch();
		} catch (Exception e) {
			throw e;
		}finally{
		    super.close(ps);
		}
	}
	/**
	 * 插入数据，返回主键，返回的主键可能是多个，则为数组数据接口，具体数组内数据类型，视数据库的字段类型而定
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
    public <T> T insertRtnPKKey(DruidDataSource dataSource,String sql,Object[] params) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Connection conn = ConnectionManager.getConnection(dataSource);
			ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			super.setParams(ps, params);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			Object key =null;
			if(rs.next()){
				key =  rs.getObject(1);
			}
			return (T) key;
		} catch (Exception e) {
			throw e;
		}finally{
		    super.close(ps,rs);
		}
	}
}
