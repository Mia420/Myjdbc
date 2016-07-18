package test.core.sqlhandler;

import java.lang.reflect.Field;

import org.dc.jdbc.config.JDBCConfig;
import org.dc.jdbc.core.ContextHandle;
import org.dc.jdbc.entity.SqlEntity;
import org.dc.jdbc.helper.DBHelper;
import org.junit.Before;
import org.junit.Test;
import test.Configure;

/**
 * Created by wyx on 2016/5/3.
 */
public class TestPrintSqlLogHandler {

	private static DBHelper testDBHelper = null;
	@Before
	public void init(){
		JDBCConfig.isPrintSqlLog=true;
		testDBHelper = new DBHelper(Configure.testSource);
	}
	@Test
	public void testReplaceParamTag() throws Exception {
		Field contextHandlerField = DBHelper.class.getDeclaredField("contextHandler");
		contextHandlerField.setAccessible(true);
		ContextHandle contextHandle = (ContextHandle) contextHandlerField.get(testDBHelper);
		SqlEntity sql1 = contextHandle.handleRequest("select * from user where user = ? and pass = ?", new Object[]{"aaa", "bbb"});
		SqlEntity sql2 = contextHandle.handleRequest("select * from user where user = ? and pass = ?", new Object[]{"\"???", "???"});
		System.out.println(sql1);
		System.out.println(sql2);
	}
}
