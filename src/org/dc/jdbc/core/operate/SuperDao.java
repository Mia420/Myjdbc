package org.dc.jdbc.core.operate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SuperDao {
	private static final Log jdbclog = LogFactory.getLog(SuperDao.class);
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
	protected void setParams(PreparedStatement ps, Object[] params) throws Exception {
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i+1, params[i]);
			}
		}
	}
	protected Object getValueByObjectType(ResultSetMetaData metaData,ResultSet rs,int index) throws Exception{
		int columnIndex = index+1;
		Object return_obj = rs.getObject(columnIndex);
		if(return_obj!=null){
			int type = metaData.getColumnType(columnIndex);
			switch (type){
			case Types.BIT:
				return_obj = rs.getByte(columnIndex);
				break;
			case Types.TINYINT:
				return_obj = rs.getByte(columnIndex);
				break;
			case Types.SMALLINT:
				return_obj = rs.getShort(columnIndex);
				break;
			case Types.LONGVARBINARY:
				return_obj = rs.getBytes(columnIndex);
				break;
			default :
				return_obj = rs.getObject(columnIndex);
			}
		}
		return return_obj;
	}
}
