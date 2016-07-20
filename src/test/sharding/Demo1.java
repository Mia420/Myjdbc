package test.sharding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class Demo1{
	@Test
	public void getTablesss() {
		try{
			String insert = "  insert into master(barid,userid,role,applystatus,time,nickname,area,course,stage) values(12.10000003,?,'Master',1,now(),?,450000,99,?)";
			Lexer lexer = new Lexer(insert);
			List<String> sqlList = new ArrayList<String>();
			List<Token> tokenList = new ArrayList<Token>();
			List<String> paramList = new ArrayList<String>();
			while(true){
				lexer.nextToken();

				Token tok = lexer.token();
				if (tok == Token.EOF) {
					break;
				}
				String printStr = null;
				if(tok == Token.IDENTIFIER || (tok == Token.VARIANT && tok.name == null) || tok == Token.MULTI_LINE_COMMENT || tok == Token.LITERAL_CHARS){
					printStr = lexer.stringVal();
				}else if(tok == Token.LITERAL_INT || tok == Token.LITERAL_FLOAT){
					printStr = lexer.numberString();
				}else{
					printStr = tok.name;
				}
				
				sqlList.add(printStr);
				tokenList.add(tok);
				System.out.println(tok.name()+"--"+printStr);
			}
			Map<String,Object> paramMap = new HashMap<String,Object>();
			if(tokenList.get(3)==Token.LPAREN && tokenList.get(1)==Token.INTO){
				for (int i = 4,len=tokenList.size(); i < len; i++) {
					if(tokenList.get(i)==Token.RPAREN && tokenList.get(i+1)==Token.VALUES){
						break;
					}
					if(tokenList.get(i)==Token.IDENTIFIER){
						paramMap.put(sqlList.get(i), "");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
