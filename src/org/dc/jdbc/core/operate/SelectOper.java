package org.dc.jdbc.core.operate;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dc.jdbc.cache.core.JedisHelper;
import org.dc.jdbc.config.JDBCConfig;
import org.dc.jdbc.core.ConnectionManager;
import org.dc.jdbc.core.GlobalCache;
import org.dc.jdbc.core.base.OperSuper;
/**
 * 查询数据的操作
 * @author dc
 * @updateTime 2015-8-17
 */
public class SelectOper extends OperSuper{
	private static final JedisHelper jedisHelper = JedisHelper.getInstance();
	private static final SelectOper oper = new SelectOper();
	public static SelectOper getInstance(){
		return oper;
	}

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
	@SuppressWarnings("unchecked")
	public <T> List<T> selectList(DataSource dataSource,String sql,Class<? extends T> cls,Object[] params,int selectType) throws Exception{
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			List<Object> list = null;
			if(JDBCConfig.isSQLCache){
				String sqlKey = super.getSQLKey(sql, params);
				list = jedisHelper.getSQLCache(sqlKey);
			}
			int cols_len = 0;
			if(list==null){//如果缓存中没查到，去数据里面查找
				Connection conn = ConnectionManager.getConnection(dataSource);
				ps = conn.prepareStatement(sql);
				rs = super.preparedSQLReturnRS(ps, sql, params);
				rs.last();
				int rowNum = rs.getRow();
				if(selectType==1 && rowNum > 1){
					throw new Exception("Query results too much!");
				}
				if(rowNum>0){//有数据
					list = new ArrayList<Object>(rowNum);
					rs.beforeFirst();
					ResultSetMetaData metaData  = rs.getMetaData();
					cols_len = metaData.getColumnCount();
					while(rs.next()){
						Map<String, Object> map = new HashMap<String, Object>(cols_len,1);//长度固定，扩容因子调成1
						for(int i=0; i<cols_len; i++){  
							String cols_name = metaData.getColumnLabel(i+1);
							Object cols_value = super.getValueByObjectType(metaData, rs, i);
							map.put(cols_name, cols_value);
						}
						list.add(map);
					}
					if(JDBCConfig.isSQLCache){
						String sqlKey = super.getSQLKey(sql, params);
						jedisHelper.setSQLCache(sqlKey,list);
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
						Map<String,Field> fieldsMap = GlobalCache.getCacheFields(cls);
						for (int i = 0,len=list.size(); i < len; i++) {
							Map<String, Object> m = (Map<String, Object>) list.get(i);
							Object obj_newInsten = cls.newInstance();
							for (String key : m.keySet()) {
								Field field = fieldsMap.get(key);
								if(field!=null){
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
}
