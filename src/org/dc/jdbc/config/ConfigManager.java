package org.dc.jdbc.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dc.jdbc.core.base.OperSuper;


public class ConfigManager {
	private static final Log jdbclog = LogFactory.getLog(OperSuper.class);
	private static ConfigManager configManger = new ConfigManager();

	private static volatile Map<String, Properties> proMap = new HashMap<String, Properties>();
	private ConfigManager(){}
	public static ConfigManager getInstance(){
		return configManger;
	}
	public String getProperty(String profileName, String key){
		if ((key == null) || (profileName == null)) {
			throw new IllegalArgumentException("key is null");
		}
		Properties properties = proMap.get(profileName);

		if (properties == null) {
			synchronized (this){
				if (proMap.get(profileName) == null){
					properties = loadProps(profileName);

					if (properties != null ){
						proMap.put(profileName, properties);
					}else {
						return null;
					}
				}
			}
		}
		String value = properties.getProperty(key);
		return value;
	}


	public Properties loadProps(String proFileName){


		Properties properties = null;

		InputStream in = null;
		try{
			properties = new Properties();
			if ((proFileName != null) && (proFileName.startsWith("http:"))) {
				URL url = new URL(proFileName);
				in = url.openStream();
			} else {
				String basePath = null;
				URL resource =  ConfigManager.class.getResource(".");
				//兼容web应用，java应用，resin服务器配置文件获取方法
				if(resource==null){
					basePath = ConfigManager.class.getResource("/").getPath();
				}else{
					basePath = resource.getPath();
					if(basePath.contains("/target/classes/")){
						basePath = basePath.substring(0,basePath.indexOf("/target/classes/"));
					}else{
						basePath = basePath.substring(0,basePath.indexOf("/WEB-INF/classes/")+17);
					}
				}

				String realPath = basePath+"/"+proFileName+".properties";
				jdbclog.info("Getting Config: "+realPath);
				in = new FileInputStream(realPath);
			}
			properties.load(in);
		}catch (Exception e){
			jdbclog.error("",e);
		} finally {
			if (in != null){
				try {
					in.close();
				} catch (Exception e) {
					jdbclog.error("",e);
				}
			}
		}
		return properties;
	}
}
