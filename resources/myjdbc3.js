import = "one.js";
className = "com.dc.jdbc.config.JDBCConfig";
dbCommonConfig = {
		initialSize:1,
		maxActive:4, 
		minIdle:0,
		maxWait:20000,
		validationQuery:"SELECT 1 \n",
		testOnBorrow:false,
		testWhileIdle:true,
		poolPreparedStatements:false
};
dbWrite1 = {
		className:"com.alibaba.druid.pool.DruidDataSource",
		url:"jdbc:mysql://192.168.1.127:3306/ucenter_userManage?useUnicode=true&characterEncoding=UTF-8",
		username:"username",
		password:"*****",
		initialSize:dbCommonConfig.initialSize,
		maxActive:dbCommonConfig.maxActive, 
		minIdle:dbCommonConfig.minIdle,
		maxWait:dbCommonConfig.maxWait,
		validationQuery:dbCommonConfig.validationQuery,
		testOnBorrow:dbCommonConfig.testOnBorrow,
		testWhileIdle:dbCommonConfig.testWhileIdle,
		poolPreparedStatements:dbCommonConfig.poolPreparedStatements
};
dbWrite2 = {
		className:"com.alibaba.druid.pool.DruidDataSource",
		url:"jdbc:mysql://192.168.1.127:3306/ucenter_userManage?useUnicode=true&characterEncoding=UTF-8",
		username:"username",
		password:"*****",
		initialSize:dbCommonConfig.initialSize,
		maxActive:dbCommonConfig.maxActive, 
		minIdle:dbCommonConfig.minIdle,
		maxWait:dbCommonConfig.maxWait,
		validationQuery:dbCommonConfig.validationQuery,
		testOnBorrow:dbCommonConfig.testOnBorrow,
		testWhileIdle:dbCommonConfig.testWhileIdle,
		poolPreparedStatements:dbCommonConfig.poolPreparedStatements
};
redisCommon = {
		className:"redis.clients.jedis.JedisPoolConfig",
		maxIdle:8,
		minIdle:0,
		maxTotal:8,
		maxWaitMillis:-1,
		testOnBorrow:false,
		port:6379
};
redisMaster = {
		className:"redis.clients.jedis.JedisPoolConfig",
		maxIdle:redisCommon.maxIdle,
		minIdle:redisCommon.minIdle,
		maxTotal:redisCommon.maxTotal,
		maxWaitMillis:redisCommon.maxWaitMillis,
		testOnBorrow:redisCommon.testOnBorrow,
		host:"localhost",
		port:redisCommon.port
};
redisSlave = {
		className:"redis.clients.jedis.JedisPool",
		maxIdle:redisCommon.maxIdle,
		minIdle:redisCommon.minIdle,
		maxTotal:redisCommon.maxTotal,
		maxWaitMillis:redisCommon.maxWaitMillis,
		testOnBorrow:redisCommon.testOnBorrow,
		host:"localhost",
		port:redisCommon.port
};
tableCacheStrategy = {
		{
			className:"",
			strategyType:1,
			dataSourceName:"test",
			tableName:"user"
		},
		{
			className:"",
			strategyType:2,
			dataSourceName:"test",
			tableName:"student"
		}
};
isPrintSqlLog = false;
isSQLCache = false;

isSepDB = true;
//轮询写
writeDB={dbWrite1,dbWrite2};
backupWriteDB={"testWrite3"};
//轮询读
readDB={"testRead1"};
backupReadDB= {"testRead2"};