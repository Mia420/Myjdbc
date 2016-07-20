package org.dc.jdbc.core.operate;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dc.jdbc.cache.core.JedisHelper;
import org.dc.jdbc.config.JDBCConfig;
import org.dc.jdbc.core.GlobalCache;
import org.dc.jdbc.core.base.OperSuper;

public class JDBCOperateImp extends OperSuper implements IJdbcOperate{
	
	private static final JDBCOperateImp instance = new JDBCOperateImp();
	
	public static JDBCOperateImp getInstance(){
		return instance;
	}
	
	private static final JedisHelper jedisHelper = JedisHelper.getInstance();
	/**
	 * 查询
	 * @param dataSource 数据源
	 * @param sql 标准的jdbc sql语句
	 * @param cls 返回的数据类型，这个和数据库里面的字段类型保持一样。如果不一样，会抛出强转异常
	 * @param params 参数数组
	 * @param selectType 查询类型，1：selectOne，2：selectList
	 * @return
	 * @throws Exception 抛出各种可能出现的异常
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> selectList(Connection conn,String sql,Class<? extends T> cls,Object[] params) throws Exception{
		ResultSet rs = null;
		PreparedStatement ps = null;
		String sqlKey = null;
		try {
			List<Object> list = null;
			String dataSourceName = null;
			if(JDBCConfig.isSQLCache){
				dataSourceName = super.getDataSourceName(conn);
				sqlKey = super.getSQLKey(sql, params,dataSourceName);
				list = jedisHelper.getSQLCache(sqlKey);
			}
			
			int cols_len = 0;
			if(list==null){//如果缓存中没查到，去数据里面查找
				ps = conn.prepareStatement(sql);
				rs = super.preparedSQLReturnRS(ps, sql, params);
				rs.last();
				int rowNum = rs.getRow();
				if(rowNum>0){//有数据
					list = new ArrayList<Object>(rowNum);
					rs.beforeFirst();
					super.parseSqlResultToListMap(rs, list);
					if(JDBCConfig.isSQLCache){
						jedisHelper.setSQLCache(sqlKey,dataSourceName,list);
					}
				}
			}
			if(list!=null){
				if(cls==null || Map.class.isAssignableFrom(cls)){//封装成Map
					return (List<T>) list;
				}else{
					if(cls.getClassLoader()==null){//封装成基本类型
						if(cols_len>1){
							throw new Exception("The number of returned data columns is too many");
						}
						for (int i = 0,len=list.size(); i < len; i++) {
							Map<String, Object> m = (Map<String, Object>) list.get(i);
							for (Object value : m.values()) {
								list.set(i,(T)value);
							}
						}
						return (List<T>)list;
					}else{//对象
						super.parseSqlResultToObject(rs,cls,list);
						Map<String,Field> fieldsMap = GlobalCache.getCacheFields(cls);
						for (int i = 0,len=list.size(); i < len; i++) {
							Map<String, Object> m = (Map<String, Object>) list.get(i);
							Object obj_newInsten = cls.newInstance();
							for (String key : m.keySet()) {
								Field field = fieldsMap.get(key);
								if(field!=null && field.getModifiers()!=Modifier.STATIC){
									field.setAccessible(true);
									field.set(obj_newInsten, m.get(key));
								}
							}
							list.set(i,obj_newInsten);
						}
						return (List<T>)list;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			super.close(ps,rs);
		}
		return null;
	}
	
	@Override
	public int update(Connection conn,String sql,Object[] params) throws Exception{
		
		return super.preparedAndExcuteSQL(conn, sql, params);
	}
	@Override
	public int insert(Connection conn,String sql,Object[] params) throws Exception{
	    return super.preparedAndExcuteSQL(conn, sql, params);
	}
	@Override
	public int[] insertBatch(Connection conn,String sql,Object[][] params) throws Exception{
		PreparedStatement ps = null;
		try {
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
	@Override
	@SuppressWarnings("unchecked")
    public <T> T insertRtnPKKey(Connection conn,String sql,Object[] params) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
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
	@Override
	public  int delete(Connection conn,String sql,Object[] params) throws Exception{
		return super.preparedAndExcuteSQL(conn, sql, params);
	}
}
