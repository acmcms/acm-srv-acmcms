/*
 * Created on 20.10.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.srv.acm;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

final class SqlConnectionInfo implements Enumeration<Connection> {
	static final String getKey(final String url, final Properties info) {
		final StringBuilder key = new StringBuilder( url ).append( '?' );
		for (final Object name : info.keySet()) {
			key.append( name ).append( '=' ).append( info.getProperty( String.valueOf( name ) ) ).append( ';' );
		}
		return key.toString();
	}
	
	private final String		url;
	
	private final Properties	info;
	
	final Driver				driver;
	
	final String				key;
	
	SqlConnectionInfo(final String url, final Properties info) throws SQLException {
		this.url = url;
		this.driver = DriverManager.getDriver( url );
		this.info = info;
		this.key = SqlConnectionInfo.getKey( url, info );
	}
	
	final Connection getConnection() throws SQLException {
		return this.driver.connect( this.url, this.info );
	}
	
	@Override
	public boolean hasMoreElements() {
		return true;
	}
	
	@Override
	public Connection nextElement() {
		try {
			return this.driver.connect( this.url, this.info );
		} catch (final SQLException e) {
			throw new RuntimeException( e );
		}
	}
	
	@Override
	public String toString() {
		return "SqlConnectionInfo [url=" + this.url + ", key=" + this.key + "]";
	}
}
