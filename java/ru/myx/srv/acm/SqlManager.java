/*
 * Created on 20.10.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.srv.acm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

final class SqlManager implements Map<String, Enumeration<Connection>> {
	private final Object											lock				= new Object();
	
	private final Map<String, Enumeration<Connection>>				connectionsByKey	= new HashMap<>( 64, 0.25f );
	
	private final Map<String, Enumeration<Connection>>				connectionsByAlias	= new HashMap<>( 64, 0.25f );
	
	private final Collection<Enumeration<Connection>>				mapValues			= this.connectionsByAlias
																								.values();
	
	private final Set<Map.Entry<String, Enumeration<Connection>>>	mapEntries			= this.connectionsByAlias
																								.entrySet();
	
	private boolean													shutDown			= false;
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean containsKey(final Object key) {
		return this.connectionsByAlias.containsKey( key );
	}
	
	@Override
	public boolean containsValue(final Object value) {
		return this.connectionsByAlias.containsValue( value );
	}
	
	@Override
	public Set<Map.Entry<String, Enumeration<Connection>>> entrySet() {
		return this.mapEntries;
	}
	
	@Override
	public final Enumeration<Connection> get(final Object key) {
		{
			final Enumeration<Connection> ci = this.connectionsByAlias.get( key == null
					? "default"
					: key.toString() );
			if (ci != null) {
				return ci;
			}
		}
		synchronized (this.lock) {
			final Enumeration<Connection> ci = this.connectionsByAlias.get( key == null
					? "default"
					: key.toString() );
			return ci;
		}
	}
	
	final Connection getConnection(final String alias) {
		{
			final Enumeration<Connection> ci = this.connectionsByAlias.get( alias == null
					? "default"
					: alias );
			if (ci != null) {
				return ci.nextElement();
			}
		}
		synchronized (this.lock) {
			final Enumeration<Connection> ci = this.connectionsByAlias.get( alias == null
					? "default"
					: alias );
			return ci == null
					? null
					: ci.nextElement();
		}
	}
	
	@Override
	public boolean isEmpty() {
		return this.connectionsByAlias.isEmpty();
	}
	
	@Override
	public Set<String> keySet() {
		return this.connectionsByAlias.keySet();
	}
	
	@Override
	public Enumeration<Connection> put(final String key, final Enumeration<Connection> value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void putAll(final Map<? extends String, ? extends Enumeration<Connection>> t) {
		throw new UnsupportedOperationException();
	}
	
	final void registerConnection(final String alias, final String url, final Properties info) throws SQLException {
		final SqlConnectionInfo ci = new SqlConnectionInfo( url, info );
		synchronized (this.lock) {
			final Object oldByKey = this.connectionsByKey.get( ci.key );
			if (oldByKey == null) {
				this.connectionsByKey.put( ci.key, ci );
			}
			final Enumeration<Connection> oldByAlias = this.connectionsByAlias.get( alias );
			if (oldByAlias == null || !((SqlConnectionInfo) oldByAlias).key.equals( ci.key )) {
				this.connectionsByAlias.put( alias, ci );
			}
		}
	}
	
	@Override
	public Enumeration<Connection> remove(final Object key) {
		throw new UnsupportedOperationException();
	}
	
	final void shutDown() {
		if (!this.shutDown) {
			synchronized (this.lock) {
				this.shutDown = true;
				try {
					this.connectionsByAlias.clear();
				} catch (final Throwable t) {
					// ignore
				}
				try {
					this.connectionsByKey.clear();
				} catch (final Throwable t) {
					// ignore
				}
			}
		}
	}
	
	@Override
	public int size() {
		return this.connectionsByAlias.size();
	}
	
	@Override
	public Collection<Enumeration<Connection>> values() {
		return this.mapValues;
	}
}
