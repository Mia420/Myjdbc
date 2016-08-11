package org.dc.jdbc.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class TableEntity {
	Map<String,Object> paramsMap = new LinkedHashMap<String,Object>();
	private int startTabNamePos;
	private int endTabNamePos;
	
	public Map<String, Object> getParamsMap() {
		return paramsMap;
	}
	public void setParamsMap(Map<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
	}
	public int getStartTabNamePos() {
		return startTabNamePos;
	}
	public void setStartTabNamePos(int startTabNamePos) {
		this.startTabNamePos = startTabNamePos;
	}
	public int getEndTabNamePos() {
		return endTabNamePos;
	}
	public void setEndTabNamePos(int endTabNamePos) {
		this.endTabNamePos = endTabNamePos;
	}
	
	
}
