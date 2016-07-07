package org.dc.jdbc.config;

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

	public static ConfigManager getInstance(){
		return configManger;
	}
	public String getProperty(String profileName, String key){
		if ((key == null) || (profileName == null)) {
			throw new IllegalArgumentException("key is null");
		}
		Properties properties = (Properties)proMap.get(profileName);

		if (properties == null) {
			synchronized (this){
				if (properties == null){
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
		jdbclog.info("Getting Config "+proFileName);

		Properties properties = null;

		InputStream in = null;
		try{
			properties = new Properties();
			if ((proFileName != null) && (proFileName.startsWith("http:"))) {
				URL url = new URL(proFileName);
				in = url.openStream();
			} else {
				String fileName = "/" + proFileName + ".properties";
				in = this.getClass().getResourceAsStream(fileName);
				properties.load(in);
			}
			properties.load(in);
		}
		catch (Exception e){
			jdbclog.error("",e);
		} finally {
			try {
				if (in != null){
					in.close();
				}
			} catch (Exception e) {
				jdbclog.error("",e);
			}
		}
		return properties;
	}
}
