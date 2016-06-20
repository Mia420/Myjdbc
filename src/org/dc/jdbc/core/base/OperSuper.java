package org.dc.jdbc.core.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.pool.DruidDataSource;
/**
 * 元操作父类，所有操作继承此类
 * @author DC
 */
public abstract class OperSuper extends JdbcSuper{
	private static final Log jdbclog = LogFactory.getLog(OperSuper.class);
	public void close(PreparedStatement ps,ResultSet rs){
		close(rs);
		close(ps);
	}
	public void close(AutoCloseable ac){
		if(ac!=null){
			try{
				ac.close();
			}catch (Exception e) {
				jdbclog.error("",e);
			}
		}
	}

	public int preparedAndExcuteSQL(Connection conn,String sql,Object[] params) throws Exception{
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			super.setParams(ps, params);
			return ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		}finally{
			this.close(ps);
		}
	}
	public ResultSet preparedSQLReturnRS(PreparedStatement ps,String sql,Object[] params) throws Exception{
		super.setParams(ps, params);
		return ps.executeQuery();
	}
	
	public String getSQLKey(String sql,Object[] params,DruidDataSource dataSource){
		StringBuilder rtn_params = new StringBuilder();
		for (int i = 0; i < params.length; i++) {
			rtn_params.append(String.valueOf(params[i]));
		}
		return sql+rtn_params.toString()+dataSource.getName();
	}
}
