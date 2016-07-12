package org.dc.jdbc.core.inter;

import java.sql.Connection;

import javax.sql.DataSource;

public interface IConnectionManager {
	public void startTransaction();
	public void setReadOnly();
	public Connection getConnection(DataSource dataSource) throws Exception;
	public void closeConnection();
	public void rollback();
	public void rollback(DataSource dataSource) throws Exception;
	public void commit() throws Exception;
}
