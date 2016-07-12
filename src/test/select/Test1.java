package test.select;

import java.util.List;
import java.util.Map;

import org.dc.jdbc.config.JDBCConfig;
import org.dc.jdbc.core.ConnectionManager;
import org.dc.jdbc.core.inter.IConnectionManager;
import org.dc.jdbc.helper.DBHelper;
import org.junit.Before;
import org.junit.Test;

import test.Configure;

public class Test1 {
	private static DBHelper accDBHelper;
	private static DBHelper testDBHelper;
	
	private static IConnectionManager connectionManage = ConnectionManager.getInstance();
	
	@Before
	public void initJdbc(){
		try {
			JDBCConfig.isPrintSqlLog = false;
			JDBCConfig.isSQLCache = true;
			JDBCConfig.setProFileName("resources/myjdbc");
			testDBHelper = new DBHelper(Configure.testSource);
			accDBHelper= new DBHelper(Configure.accSource);
			//LoadSqlUtil.loadSql("D:\\Git\\MyJdbc\\target\\classes\\test\\sql");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Test
	public void select1(){
		try {
			List<Map<String,Object>> mapList = testDBHelper.selectList("select * from user");
			System.out.println(mapList);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			connectionManage.closeConnection();
		}
	}
}
