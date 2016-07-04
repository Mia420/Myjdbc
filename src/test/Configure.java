package test;
import java.lang.reflect.Field;


import com.alibaba.druid.pool.DruidDataSource;

public class Configure {
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		Class<?> bean = testSource.getClass().getSuperclass();
		Field[] fileds = bean.getDeclaredFields();
		for(int i = 0 ; i < fileds.length; i++){  
			Field f = fileds[i];
			f.setAccessible(true);
			//Object val = f.get(bean);//得到此属性的值     
			System.out.println("name:"+f.getName());  
		}
	}
	/**
	 * sql包目录，定义多个目录用逗号分割 
	 */
	public static DruidDataSource testSource = new DruidDataSource();
	public static DruidDataSource accSource = new DruidDataSource();
	static{
		testSource.setUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8");
		testSource.setUsername("root");
		testSource.setPassword("123456");
		/*密码加密
			try {
				testSource.setFilters("config");
			} catch (Exception e) {
				log.error("",e);
			}
			testSource.setConnectionProperties("config.decrypt=true");
		 */
		testSource.setInitialSize(1);
		testSource.setMaxActive(4);
		testSource.setMinIdle(0);
		testSource.setMaxWait(60000);
		testSource.setValidationQuery("SELECT 1");
		testSource.setTestOnBorrow(false);
		testSource.setTestWhileIdle(true);
		testSource.setPoolPreparedStatements(false);
		testSource.setDriverClassName("com.mysql.jdbc.Driver");
		testSource.setName("test");
	}
	static{
		accSource.setUrl("jdbc:mysql://localhost:3306/account_ms?useUnicode=true&characterEncoding=UTF-8");
		accSource.setUsername("root");
		accSource.setPassword("123456");
		/*密码加密
			try {
				testSource.setFilters("config");
			} catch (Exception e) {
				log.error("",e);
			}
			testSource.setConnectionProperties("config.decrypt=true");
		 */
		accSource.setInitialSize(2);
		accSource.setMaxActive(4);
		accSource.setMinIdle(0);
		accSource.setMaxWait(60000);
		accSource.setValidationQuery("SELECT 1");
		accSource.setTestOnBorrow(false);
		accSource.setTestWhileIdle(true);
		accSource.setPoolPreparedStatements(false);
		accSource.setDriverClassName("com.mysql.jdbc.Driver");
		accSource.setName("acc");
	}
}
