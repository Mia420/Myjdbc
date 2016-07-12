package org.dc.jdbc.core;

import java.lang.reflect.Method;

import org.dc.jdbc.anno.Transactional;
import org.dc.jdbc.core.inter.IConnectionManager;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
/**
 * 动态反向代理,主要作用：拦截参数、管理数据库事务
 * @author dc
 * @time 2015-8-17
 */
public final class ObjectProxy implements MethodInterceptor {

	private static IConnectionManager connManager = ConnectionManager.getInstance();
	private static ThreadLocal<Method> methodLocal = new ThreadLocal<Method>();

	private final static  ObjectProxy jdbcProxy = new ObjectProxy();
	public static ObjectProxy getInstance(){
		return jdbcProxy;
	}

	private ObjectProxy(){}
	/**
	 * 新增的事务嵌套逻辑
	 */
	public Object intercept(Object obj, Method method, Object[] objects, MethodProxy proxy) throws Throwable {
		if(methodLocal.get()==null){
			methodLocal.set(method);

			Transactional transactional  = method.getAnnotation(Transactional.class);
			if (transactional == null) {
				//方法无注解，查找类上注解，并判断当前调用方法是否为当前类定义的（防止父类方法触发事务边界）
				transactional = method.getDeclaringClass().getAnnotation(Transactional.class);
			}

			if(transactional!=null){//如果不为空，则开启事务
				if(transactional.readonly()==false){
					connManager.startTransaction();
				}else{
					connManager.setReadOnly();
				}
			}
		}
		boolean isDo = false;
		if(methodLocal.get()==method){
			isDo = true;
		}
		Object invokeObj = null;
		try{
			//执行目标方法
			invokeObj = proxy.invokeSuper(obj, objects);
			if(isDo){
				connManager.commit();
			}
		}catch(Throwable e){
			if(isDo){
				connManager.rollback();
			}
			throw e;
		}finally{
			if(isDo){
				connManager.closeConnection();
			}
		}
		return invokeObj;
	}

	@SuppressWarnings("unchecked")
	public <T> T getTarget(Class<T> target) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target);
		// 回调方法
		enhancer.setCallback(this);

		// 创建代理对象
		return (T) enhancer.create();
	}
}
