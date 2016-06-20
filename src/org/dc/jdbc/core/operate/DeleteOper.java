package org.dc.jdbc.core.operate;

import java.sql.Connection;

import org.dc.jdbc.cache.core.JedisHelper;
import org.dc.jdbc.core.ConnectionManager;
import org.dc.jdbc.core.base.OperSuper;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 删除操作
 * @author dc
 * @time 2015-8-17
 */
public class DeleteOper extends OperSuper{
	private static final JedisHelper jedisHelper = JedisHelper.getInstance();
    private static final DeleteOper deleteOper = new DeleteOper();
    public static DeleteOper getInstance(){
        return deleteOper;
    }
	public  int delete(DruidDataSource dataSource,String sql,Object[] params) throws Exception{
		String key = super.getSQLKey(sql, params, dataSource);
		jedisHelper.delSQLCache(key);
		
		Connection conn = ConnectionManager.getConnection(dataSource);
		return super.preparedAndExcuteSQL(conn, sql, params);
	}
}
