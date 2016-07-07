package org.dc.jdbc.cache.core;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dc.jdbc.config.ConfigManager;
import org.dc.jdbc.config.JDBCConfig;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConfig {
	public static ConfigManager configManager = ConfigManager.getInstance();
	private static final Log jdbclog = LogFactory.getLog(JedisHelper.class);
	public static JedisPool defaultJedisPool;
	public static Properties defaultProperties = new Properties();
	static{
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			//最大空闲连接数, 默认8个
			config.setMaxIdle(Integer.parseInt(configManager.getProperty(JDBCConfig.profileName, "redis.maxIdle")));
			//最小空闲连接数, 默认0
			config.setMinIdle(Integer.parseInt(configManager.getProperty(JDBCConfig.profileName, "redis.minIdle")));
			
			//最大连接数, 默认8个
			config.setMaxTotal(Integer.parseInt(configManager.getProperty(JDBCConfig.profileName, "redis.maxTotal")));
			
			//得到一个jedis实例的最大的等待时间(毫秒)，默认阻塞，如果超过等待时间，则直接抛出JedisConnectionException；  
			config.setMaxWaitMillis(Long.parseLong(configManager.getProperty(JDBCConfig.profileName, "redis.maxWaitMillis")));
			
			//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；  
			config.setTestOnBorrow(Boolean.parseBoolean(configManager.getProperty(JDBCConfig.profileName, "redis.testOnBorrow")));
			defaultJedisPool = new JedisPool(config,configManager.getProperty(JDBCConfig.profileName, "redis.host") , Integer.parseInt(configManager.getProperty(JDBCConfig.profileName, "redis.port")));
		} catch (Exception e) {
			jdbclog.error("",e);
		}
	}
}
