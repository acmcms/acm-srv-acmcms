/*
 * Created on 17.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class NodeSqlPool extends AbstractNode {
	private static final BaseObject		STR_UNSUPPORTED	= MultivariantString.getString( "-= unsupported =-",
																	Collections.singletonMap( "ru",
																			"-= не поддерживается =-" ) );
	
	private static final ControlFieldset<?>	FIELDSET_TABLE	= ControlFieldset
																	.createFieldset( "pluginsDatabaseConnections" )
																	.addFields( new ControlField[] {
			ControlFieldFactory.createFieldString( "name",
					MultivariantString.getString( "Name", Collections.singletonMap( "ru", "Имя" ) ),
					"" ),
			ControlFieldFactory.createFieldString( "type",
					MultivariantString.getString( "Type", Collections.singletonMap( "ru", "Тип" ) ),
					"" ),
			ControlFieldFactory.createFieldString( "remarks",
					MultivariantString.getString( "Remarks", Collections.singletonMap( "ru", "Комментарии" ) ),
					"" ),											} );
	
	private final BaseObject				data;
	
	private final String					alias;
	
	private final Enumeration<Connection>	source;
	
	private static final ControlCommand<?>	CMD_QUERY		= Control
																	.createCommand( "query",
																			MultivariantString.getString( "Query...",
																					Collections.singletonMap( "ru",
																							"Запрос..." ) ) )
																	.setCommandPermission( "modify" )
																	.setCommandIcon( "command-run" );
	
	private static final ControlCommand<?>	CMD_INFO		= Control
																	.createCommand( "info",
																			MultivariantString
																					.getString( "Properties...",
																							Collections
																									.singletonMap( "ru",
																											"Свойства..." ) ) )
																	.setCommandPermission( "view" )
																	.setCommandIcon( "command-info" );
	
	NodeSqlPool(final String alias, final Enumeration<Connection> source) {
		this.alias = alias;
		this.source = source;
		this.setAttributeIntern( "id", alias );
		this.setAttributeIntern( "title", alias );
		this.recalculate();
		this.data = new BaseNativeObject();
		final Connection conn;
		{
			Connection connCheck = null;
			try {
				connCheck = source.nextElement();
			} catch (final Throwable t) {
				// ignore
			}
			conn = connCheck;
		}
		if (conn == null) {
			this.data.baseDefine("alias", alias + " [unavailable]");
			this.data.baseDefine("dbName", "n/a");
			this.data.baseDefine("dbVer", "n/a");
			this.data.baseDefine("drName", "n/a");
			this.data.baseDefine("drVer", "n/a");
		} else {
			try {
				final DatabaseMetaData metaData = conn.getMetaData();
				this.data.baseDefine("alias", alias);
				this.data.baseDefine("dbName", metaData.getDatabaseProductName());
				this.data.baseDefine("dbVer", metaData.getDatabaseProductVersion());
				this.data.baseDefine("drName", metaData.getDriverName());
				this.data.baseDefine("drVer", metaData.getDriverVersion());
			} catch (final SQLException ex) {
				Report.exception( "STCFG_DB_NODE", "Error retrieving database connection info", ex );
			} finally {
				try {
					conn.close();
				} catch (final Throwable t) {
					// ignore
				}
			}
		}
	}
	
	@Override
	public AccessPermissions getCommandPermissions() {
		return Access
				.createPermissionsLocal()
				.addPermission( "view",
						MultivariantString.getString( "View connection properties and table structure",
								Collections.singletonMap( "ru", "Просмотр свойств соединения и структуры таблиц" ) ) )
				.addPermission( "modify",
						MultivariantString.getString( "Query execution",
								Collections.singletonMap( "ru", "Выполнение запросов" ) ) )
				.addPermission( "browse",
						MultivariantString.getString( "Browse table contents",
								Collections.singletonMap( "ru", "Просмотр содержания таблиц" ) ) );
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject parameters) {
		if ("query".equals( command.getKey() )) {
			// if (command == NodeSqlPool.CMD_QUERY) {
			return new FormSqlQuery( this.alias, "" );
		}
		if ("info".equals( command.getKey() )) {
			// if (command == NodeSqlPool.CMD_INFO) {
			return new FormSqlInfo( this.alias, this.source );
		}
		final BaseObject attributes = command.getAttributes();
		if ("query_table".equals( command.getKey() )) {
			return new FormSqlQuery( this.alias, "SELECT * FROM " + Base.getString( attributes, "key", "" ) );
		}
		if ("struct".equals( command.getKey() )) {
			return new FormSqlTableStructure( this.alias, Base.getString( attributes, "key", "" ) );
		}
		if ("index".equals( command.getKey() )) {
			return new FormSqlTableIndices( this.alias, Base.getString( attributes, "key", "" ) );
		}
		if ("browse".equals( command.getKey() )) {
			return new FormSqlTableBrowse( this.alias, Base.getString( attributes, "key", "" ) );
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( NodeSqlPool.CMD_QUERY );
		result.add( NodeSqlPool.CMD_INFO );
		return result;
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		final ControlCommandset result = Control.createOptions();
		result.add( Control.createCommand( "query_table", //
				MultivariantString.getString( "Query", //
						Collections.singletonMap( "ru", "Запрос" ) ) )//
				.setCommandPermission( "modify" )//
				.setAttribute( "key", key ) );
		result.add( Control.createCommand( "struct", //
				MultivariantString.getString( "Structure", //
						Collections.singletonMap( "ru", "Структура" ) ) )//
				.setCommandPermission( "view" )//
				.setAttribute( "key", key ) );
		result.add( Control.createCommand( "index", //
				MultivariantString.getString( "Indices", //
						Collections.singletonMap( "ru", "Индексы" ) ) )//
				.setCommandPermission( "view" )//
				.setAttribute( "key", key ) );
		result.add( Control.createCommand( "browse", //
				MultivariantString.getString( "Browse", //
						Collections.singletonMap( "ru", "Просмотр" ) ) )//
				.setCommandPermission( "browse" )//
				.setAttribute( "key", key ) );
		return result;
	}
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
		return NodeSqlPool.FIELDSET_TABLE;
	}
	
	@Override
	public List<ControlBasic<?>> getContents() {
		return this.internGetContents();
	}
	
	@Override
	public BaseObject getData() {
		return this.data;
	}
	
	private List<ControlBasic<?>> internGetContents() {
		final List<ControlBasic<?>> result = BaseObject.createArray();
		final Connection conn = this.source.nextElement();
		try {
			ResultSet rs = null;
			try {
				final String[] types = { "TABLE" };
				final String sourceSchema = conn.getMetaData().getDatabaseProductName().toLowerCase()
						.indexOf( "oracle" ) == -1
						? "%"
						: conn.getMetaData().getUserName();
				rs = conn.getMetaData().getTables( conn.getCatalog(), sourceSchema, "%", types );
				while (rs.next()) {
					final String tableName = rs.getString( "TABLE_NAME" );
					BaseObject remarks = null;
					try {
						remarks = Base.forString( rs.getString( "REMARKS" ) );
					} catch (final SQLException e) {
						try {
							remarks = Base.forString( rs.getClob( "REMARKS" ).toString() );
						} catch (final NullPointerException t) {
							// empty
						} catch (final Throwable t) {
							remarks = NodeSqlPool.STR_UNSUPPORTED;
						}
					}
					
					final BaseObject data = new BaseNativeObject()//
							.putAppend( "name", tableName )//
							.putAppend( "type", rs.getString( "TABLE_TYPE" ) )//
							.putAppend( "remarks", remarks )//
					;
					// result.add(new NodeSqlPoolTable(alias, tableName, conn));
					result.add( Control.createBasic( tableName, tableName, data ) );
				}
			} catch (final SQLException e) {
				Report.exception( "STCFG_DB_NODE", "Error retrieving table list", e );
			}
			return result;
		} finally {
			try {
				conn.close();
			} catch (final Throwable t) {
				// ignore
			}
		}
	}
}
