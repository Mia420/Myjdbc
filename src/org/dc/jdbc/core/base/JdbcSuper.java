package org.dc.jdbc.core.base;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.dc.jdbc.core.inter.TypeFactory;

public class JdbcSuper {
	public static TypeFactory typeFactory = null;
	protected void setParams(PreparedStatement ps, Object[] params) throws Exception {
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i+1, params[i]);
			}
		}
	}
	protected Object getValueByObjectType(ResultSetMetaData metaData,ResultSet rs,int index) throws Exception{
		int columnIndex = index+1;
		String typeName = metaData.getColumnTypeName(columnIndex);
		Object value = null;
		if(typeName.equals("TINYINT")){
			value = rs.getInt(columnIndex);
		}else{
			value = rs.getObject(columnIndex);
		}
		if(typeFactory!=null){
			value = typeFactory.typeChange(value, typeName);
		}
		return value;
	}
}
