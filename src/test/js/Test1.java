package test.js;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dc.jdbc.config.ConfigManager;
import org.junit.Test;

public class Test1 {
	private static final Log jdbclog = LogFactory.getLog(Test1.class);
	public static void main(String[] args) {
		try {
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
			String realPath = basePath+"/resources/myjdbc2.js";
			jdbclog.info("Getting Config: "+realPath);
			File file = new File(realPath);
			System.out.println(Charset.defaultCharset());
			String str = FileUtils.readFileToString(file, Charset.defaultCharset());
			String[] s1 = str.replaceAll("	", "").replaceAll("\n", "").split(";");
			Map m1 = new TreeMap<>();
			for (int i = 0; i < s1.length; i++) {
				 String[] s2 = s1[i].split("=");
				 String value = s2[1];
				 for (int j = 0; j < value.length(); j++) {
					 char v = value.charAt(j);
					 if(' '==v){
						 continue;
					 }
					 if ('\"'==v){
						 m1.put(s2[0].trim(), value.substring(value.indexOf("\"")+1, value.lastIndexOf("\"")));
					 }
				 }
			}
			System.out.println(m1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void jsEngine(){
		try {
			ScriptEngineManager seManager=new ScriptEngineManager();
			ScriptEngine se=seManager.getEngineByName("js");
			Object ret = se.eval("3+4;");
			System.out.println(ret);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
