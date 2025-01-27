/*
 * Created on 15.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.srv.acm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import ru.myx.ae1.PluginInstance;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.report.Report;

/** @author myx
 *
 *         To change the template for this generated type comment go to Window>Preferences>Java>Code
 *         Generation>Code and Comments */
public class HostRT3 {

	private static void tryConnection(final ServerDomain server, final BaseObject object) {

		final Properties info = new Properties();
		info.setProperty("useUnicode", "true");
		info.setProperty("characterEncoding", StandardCharsets.UTF_8.name());
		for (final Iterator<String> iterator = Base.keys(object); iterator.hasNext();) {
			String key = iterator.next();
			if (key.equalsIgnoreCase("dbuser")) {
				key = "user";
			}
			if (key.equalsIgnoreCase("dbpassword")) {
				key = "password";
			}
			info.setProperty(key, Convert.MapEntry.toString(object, key, ""));
		}
		final String alias = Convert.MapEntry.toString(info, "id", "").trim();
		final String url = Convert.MapEntry.toString(info, "url", "").trim();
		if (alias.length() == 0) {
			throw new IllegalArgumentException("'id' attribute is required!");
		}
		if (url.length() == 0) {
			throw new IllegalArgumentException("'url' attribute is required!");
		}
		try {
			server.connections.registerConnection(alias, url, info);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void tryLanguage(final Collection<String> languages, final BaseObject object) {

		assert object != null : "NULL java value";
		final String id = Base.getString(object, "id", "").trim();
		if (id.length() > 0) {
			languages.add(id);
		}
	}
	
	private static void tryPlugin(final ServerDomain server, final BaseObject object) {

		assert object != null : "NULL java value";
		final String pluginClass = Base.getString(object, "class", "").trim();
		if (pluginClass.length() > 0) {
			final BaseNativeObject info = new BaseNativeObject();
			for (final Iterator<String> iterator = Base.keys(object); iterator.hasNext();) {
				final String key = iterator.next();
				if (key.length() > 0 && !"class".equals(key)) {
					final String value = Base.getString(object, key, "");
					final String realValue = value.startsWith("RootFolder/")
						? new File(server.getSystemRoot(), value.substring(11)).getAbsolutePath()
						: value;
					info.putAppend(key, realValue);
				}
			}
			PluginInstance plugin = Produce.object(PluginInstance.class, pluginClass, info, null);
			if (plugin == null) {
				try {
					final Class<?> cls = Class.forName(pluginClass);
					final Object o = cls.getConstructor().newInstance();
					if (!(o instanceof PluginInstance)) {
						System.out.println("Not a plugin: " + pluginClass);
					}
					plugin = (PluginInstance) o;
				} catch (final InvocationTargetException e) {
					System.out.println("InvocationTargetException: " + pluginClass);
					return;
				} catch (final NoSuchMethodException e) {
					System.out.println("NoSuchMethodException: " + pluginClass);
					return;
				} catch (final InstantiationException e) {
					System.out.println("InstantiationException: " + pluginClass);
					return;
				} catch (final IllegalAccessException e) {
					System.out.println("IllegalAccessException: " + pluginClass);
					return;
				} catch (final ClassNotFoundException e) {
					System.out.println("CLASS NOT FOUND: " + pluginClass);
					return;
				}
			}
			server.addPlugin(plugin, info);
		}
	}
	
	private static void tryProperty(final ServerDomain server, final BaseObject object) {

		assert object != null : "NULL java object";
		final String name = Base.getString(object, "name", "").trim();
		if (name.length() > 0) {
			final String value = Base.getString(object, "value", "");
			server.getProperties().setProperty(name, value);
		}
	}
	
	static final void stdinit(final ServerDomain server, final BaseObject attributes, final BaseObject config) {

		server.getProperties().setProperty("DomainID", server.getDomainId());
		{
			final BaseObject object = config.baseGet("application.variable", BaseObject.UNDEFINED);
			assert object != null : "NULL java object";
			if (object == BaseObject.UNDEFINED) {
				// ignore
			} else {
				final BaseArray array = object.baseArray();
				if (array != null) {
					final int length = array.length();
					for (int i = 0; i < length; ++i) {
						HostRT3.tryProperty(server, array.baseGet(i, BaseObject.UNDEFINED));
					}
				} else {
					HostRT3.tryProperty(server, object);
				}
			}
			server.getProperties().put("created", new Date());
		}
		{
			final BaseObject connectionInfo = new BaseNativeObject();
			connectionInfo.baseDefineImportAllEnumerable(attributes);
			connectionInfo.baseDefine("id", "default");
			connectionInfo.baseDelete("domain");
			connectionInfo.baseDelete("entrance");
			connectionInfo.baseDelete("aliases");
			connectionInfo.baseDelete("exclude");
			connectionInfo.baseDelete("class");
			HostRT3.tryConnection(server, connectionInfo);
		}
		{
			final List<String> languages = new ArrayList<>();
			final BaseObject object = config.baseGet("language", BaseObject.UNDEFINED);
			assert object != null : "NULL java object";
			if (object.baseIsPrimitive()) {
				// ignore
			} else {
				final BaseArray array = object.baseArray();
				if (array != null) {
					final int length = array.length();
					for (int i = 0; i < length; ++i) {
						HostRT3.tryLanguage(languages, array.baseGet(i, BaseObject.UNDEFINED));
					}
				} else {
					HostRT3.tryLanguage(languages, object);
				}
			}
			if (languages.size() == 0) {
				languages.add("en");
			}
			server.languages = languages.toArray(new String[languages.size()]);
			server.languageDefault = server.languages[0];
		}
		{
			final BaseObject object = config.baseGet("pool", BaseObject.UNDEFINED);
			assert object != null : "NULL java object";
			if (object.baseIsPrimitive()) {
				// ignore
			} else {
				final BaseArray array = object.baseArray();
				if (array != null) {
					final int length = array.length();
					for (int i = 0; i < length; ++i) {
						HostRT3.tryConnection(server, array.baseGet(i, BaseObject.UNDEFINED));
					}
				} else {
					HostRT3.tryConnection(server, object);
				}
			}
		}
		{
			final BaseObject object = config.baseGet("plugin", BaseObject.UNDEFINED);
			assert object != null : "NULL java object";
			if (object.baseIsPrimitive()) {
				// ignore
			} else {
				final BaseArray array = object.baseArray();
				if (array != null) {
					final int length = array.length();
					for (int i = 0; i < length; ++i) {
						HostRT3.tryPlugin(server, array.baseGet(i, BaseObject.UNDEFINED));
					}
				} else {
					HostRT3.tryPlugin(server, object);
				}
			}
		}
		{
			try (final Connection conn = server.getServerConnection("default")) {
				if (conn == null) {
					Report.warning("HOST_RT3", "'default' connection is unknown or unavailable!");
				} else {
					Report.info(
							"HOST_RT3",
							"'default' connection source info: " + conn.getMetaData().getDatabaseProductName() + ", ver: " + conn.getMetaData().getDatabaseProductVersion());
				}
			} catch (final RuntimeException e) {
				Report.exception("HOST_RT3", "Error while conneting to: " + attributes.baseGet("url", BaseObject.UNDEFINED), e);
				for (final Enumeration<Driver> enumeration = DriverManager.getDrivers(); enumeration.hasMoreElements();) {
					Report.info("HOST_RT3", "Next driver: " + enumeration.nextElement());
				}
				throw e;
			} catch (final SQLException e) {
				throw new RuntimeException("Fatal error while checking default connection!", e);
			}
		}
	}
}
