package org.dc.jdbc.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.dc.jdbc.cache.core.JedisHelper;
import org.dc.jdbc.core.base.OperSuper;

public class JDBCCacheProxy extends OperSuper implements InvocationHandler{
	//要代理的原始对象
	private Object obj;

	public JDBCCacheProxy(Object obj) {
		super();
		this.obj = obj;
	}
	private static final JedisHelper jedisHelper = JedisHelper.getInstance();
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(!method.getName().startsWith("select")){
			String key = super.getSQLKey(args[1].toString(), (Object[])args[3],super.getDataSourceName((Connection)args[0]));
			jedisHelper.delSQLCache(key);
		}
		return method.invoke(obj, args);
	}

}
