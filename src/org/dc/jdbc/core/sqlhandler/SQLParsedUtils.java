package org.dc.jdbc.core.sqlhandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dc.jdbc.entity.SqlEntity;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class SQLParsedUtils {
	public static void main(String[] args) {
		try{
			StringBuffer sql = new StringBuffer("select * from user u2 where name = ?");
			Map<Object,Object> allparamMap = null;
			List<Object>  allParamList = new ArrayList<>();
			allParamList.add("dc");
			Set<String> tableSet =new HashSet<String>();
			List<Object> returnList = new ArrayList<Object>();
			Lexer lexer = new Lexer(sql.toString());
			int lastCharLen = 0;
			Token lastTok = null;
			LinkedHashMap<String, Object> paramsMap = new LinkedHashMap<String, Object>();
			List<String> sqlList = new ArrayList<String>();
			int countQues = 0;
			while(true){
				lexer.nextToken();
				Token tok = lexer.token();
				
				if (tok == Token.EOF) {
					break;
				}
				String str = lexer.stringVal();
				sqlList.add(str);
				int curpos = lexer.pos();
				if(tok.name == null && tok == Token.VARIANT){//异类匹配，这里的异类只有#号，sql编写规范的情况下，不需要判断str.contains("#")
					String key = str.substring(2, str.length()-1);
					if(!allparamMap.containsKey(key)){
						throw new Exception("sqlhandle analysis error! parameters \""+key+"\" do not match to!");
					}
					
					System.out.println(sqlList.get(sqlList.size()-2)+"="+allparamMap.get(key));
					sql.replace(curpos-str.length()-lastCharLen, curpos-lastCharLen, "?");
					lastCharLen = lastCharLen+str.length()-1;
				}else if(tok == Token.QUES){
					System.out.println(sqlList.get(sqlList.size()-2)+"="+allParamList.get(countQues));
					countQues++;
				}else if((lastTok != null && tok== Token.IDENTIFIER || tok== Token.USER) 
						&& (lastTok == Token.FROM || lastTok == Token.INTO || lastTok == Token.UPDATE || lastTok == Token.JOIN)){//记录表名
					tableSet.add(str);
				}
				lastTok = tok;
			}
			System.out.println(tableSet);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void parseQues(String sql,SqlEntity entity){
		
	}
}