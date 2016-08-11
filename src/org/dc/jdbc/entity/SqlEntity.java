package org.dc.jdbc.entity;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * 当前线程上下文全局对象
 * @author DC
 *
 */
public class SqlEntity{
	private String sql;
    private Object[] params;
    private Object[] paramKeys;
    private List<TableEntity> tabList;
    
    private LinkedHashMap<String, Object> paramsMap = new LinkedHashMap<>();
    private String tableName;
    
    private int startTabNamePos;
    private int endTabNamePos;
    
    
    private boolean transaction;
    private boolean readOnly;
    
    private DataSource currentDataSource;
    //连接重用关联设计
    private Map<DataSource,Connection> dataSourceMap = new HashMap<DataSource,Connection>();
    
    
    
	public LinkedHashMap<String, Object> getParamsMap() {
		return paramsMap;
	}
	public void setParamsMap(LinkedHashMap<String, Object> paramsMap) {
		this.paramsMap = paramsMap;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Map<DataSource, Connection> getDataSourceMap() {
		return dataSourceMap;
	}
	public void setDataSourceMap(Map<DataSource, Connection> dataSourceMap) {
		this.dataSourceMap = dataSourceMap;
	}
	public boolean getTransaction() {
		return transaction;
	}
	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}
	public String getSql() {
        return sql;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
    public Object[] getParams() {
        return params;
    }
    public void setParams(Object[] params) {
        this.params = params;
    }
	public boolean getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public List<TableEntity> getTabList() {
		return tabList;
	}
	public void setTabList(List<TableEntity> tabList) {
		this.tabList = tabList;
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
	public Object[] getParamKeys() {
		return paramKeys;
	}
	public void setParamKeys(Object[] paramKeys) {
		this.paramKeys = paramKeys;
	}
	public DataSource getCurrentDataSource() {
		return currentDataSource;
	}
	public void setCurrentDataSource(DataSource currentDataSource) {
		this.currentDataSource = currentDataSource;
	}
}
