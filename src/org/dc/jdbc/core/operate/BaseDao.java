package org.dc.jdbc.core.operate;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dc.jdbc.core.GlobalCache;

public class BaseDao extends SuperDao {
	/**
	 * 编译sql并执行查询
	 * @param ps
	 * @param sql
	 * @param params
	 * @return 返回结果集对象
	 * @throws Exception
	 */
	public ResultSet preparedSQLReturnRS(PreparedStatement ps,String sql,Object[] params) throws Exception{
		super.setParams(ps, params);
		return ps.executeQuery();
	}
	/**
	 * 执行sql语句，返回受影响的行数
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public int preparedAndExcuteSQL(Connection conn,String sql,Object[] params) throws Exception{
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			super.setParams(ps, params);
			return ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		}finally{
			super.close(ps);
		}
	}
	/**
	 * 将sql查询结果转化成map类型的集合
	 * @param rs
	 * @param list
	 * @throws Exception
	 */
	public void parseSqlResultToListMap(ResultSet rs,List<Object> list) throws Exception{
		ResultSetMetaData metaData  = rs.getMetaData();
		int cols_len = metaData.getColumnCount();
		while(rs.next()){
			Map<String, Object> map = new HashMap<String, Object>(cols_len,1);
			for(int i=0; i<cols_len; i++){  
				String cols_name = metaData.getColumnLabel(i+1);
				Object cols_value = super.getValueByObjectType(metaData, rs, i);
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}
	}
	/**
	 * 将sql查询结果转化成Map
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public Map<?,?> parseSqlResultToMap(ResultSet rs) throws Exception{
		ResultSetMetaData metaData  = rs.getMetaData();
		int cols_len = metaData.getColumnCount();
		Map<String, Object> map = new HashMap<String, Object>(cols_len,1);
		for(int i=0; i<cols_len; i++){
			String cols_name = metaData.getColumnLabel(i+1);  
			Object cols_value = super.getValueByObjectType(metaData, rs, i);
			map.put(cols_name, cols_value);
		}
		return (Map<?,?>)map;
	}
	/**
	 * 将sql查询结果转化成对象
	 * @param rs
	 * @param cls
	 * @param list
	 * @throws Exception
	 */
	public void parseSqlResultToListObject(ResultSet rs,Class<?> cls,List<Object> list) throws Exception{
		ResultSetMetaData metaData  = rs.getMetaData();
		int cols_len = metaData.getColumnCount();
		Map<String,Field> fieldsMap = GlobalCache.getCacheFields(cls);
		while(rs.next()){
			Object obj_newInsten = cls.newInstance();
			for(int i = 0; i<cols_len; i++){
				String cols_name = metaData.getColumnLabel(i+1);  
				Field field = fieldsMap.get(cols_name);
				if(field!=null){
					Object cols_value =  super.getValueByObjectType(metaData, rs, i);

					field.setAccessible(true);
					field.set(obj_newInsten, cols_value);
				}
			}
			list.add(obj_newInsten);
		}
	}
	public Object parseSqlResultToObject(ResultSet rs,Class<?> cls) throws Exception{
		ResultSetMetaData metaData  = rs.getMetaData();
		int cols_len = metaData.getColumnCount();

		Object obj_newInsten = cls.newInstance();
		Map<String,Field> fieldsMap = GlobalCache.getCacheFields(cls);
		for(int i = 0; i<cols_len; i++){
			String cols_name = metaData.getColumnLabel(i+1);
			Field field = fieldsMap.get(cols_name);
			if(field!=null){
				Object cols_value = super.getValueByObjectType(metaData, rs, i);
				field.setAccessible(true);
				field.set(obj_newInsten, cols_value);
			}
		}
		return obj_newInsten;
	}
	public void parseSqlResultToListBaseType(ResultSet rs,List<Object> list) throws Exception{
		ResultSetMetaData metaData  = rs.getMetaData();
		int cols_len = metaData.getColumnCount();
		if(cols_len>1){
			throw new Exception("The number of returned data columns is too many");
		}
		//String cols_name = metaData.getColumnLabel(1);
		while(rs.next()){
			for(int i=0; i<cols_len; i++){  
				Object cols_value = super.getValueByObjectType(metaData, rs, i);
				list.add(cols_value);
			}
		}
	}
	public Object parseSqlResultToBaseType(ResultSet rs) throws Exception{
		ResultSetMetaData metaData  = rs.getMetaData();
		int cols_len = metaData.getColumnCount();//列数
		if(cols_len>1){
			throw new Exception("The number of returned data columns is too many");
		}
		Object cols_value = super.getValueByObjectType(metaData, rs, 0);

		return cols_value;
	}
}
