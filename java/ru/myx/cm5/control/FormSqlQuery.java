/*
 * Created on 17.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArrayDynamic;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Format;
import ru.myx.util.BaseMapSqlResultSet;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormSqlQuery extends AbstractForm<FormSqlQuery> {
	private static final ControlFieldset<?>	FIELDSET		= ControlFieldset
																	.createFieldset()
																	.addField( ControlFieldFactory
																			.createFieldString( "connection",
																					MultivariantString
																							.getString( "Connection",
																									Collections
																											.singletonMap( "ru",
																													"Соединение" ) ),
																					"" ).setConstant() )
																	.addField( ControlFieldFactory
																			.createFieldString( "query",
																					MultivariantString
																							.getString( "Query",
																									Collections
																											.singletonMap( "ru",
																													"Запрос" ) ),
																					"",
																					1,
																					16384 ).setFieldType( "text" ) );
	
	private static final ControlFieldset<?>	FIELDSET_UPDATE	= ControlFieldset
																	.createFieldset()
																	.addField( ControlFieldFactory
																			.createFieldString( "connection",
																					MultivariantString
																							.getString( "Connection",
																									Collections
																											.singletonMap( "ru",
																													"Соединение" ) ),
																					"" ).setConstant() )
																	.addField( ControlFieldFactory
																			.createFieldString( "query",
																					MultivariantString
																							.getString( "Query",
																									Collections
																											.singletonMap( "ru",
																													"Запрос" ) ),
																					"",
																					1,
																					16384 ).setFieldType( "text" ) )
																	.addField( ControlFieldFactory
																			.createFieldString( "result",
																					MultivariantString
																							.getString( "Result",
																									Collections
																											.singletonMap( "ru",
																													"Результат" ) ),
																					"" ).setConstant() )
																	.addField( ControlFieldFactory
																			.createFieldString( "took",
																					MultivariantString
																							.getString( "Time elapsed",
																									Collections
																											.singletonMap( "ru",
																													"Время выполнения" ) ),
																					"" ).setConstant() );
	
	private static final ControlFieldset<?>	FIELDSET_ERROR	= ControlFieldset
																	.createFieldset()
																	.addField( ControlFieldFactory
																			.createFieldString( "connection",
																					MultivariantString
																							.getString( "Connection",
																									Collections
																											.singletonMap( "ru",
																													"Соединение" ) ),
																					"" ).setConstant() )
																	.addField( ControlFieldFactory
																			.createFieldString( "query",
																					MultivariantString
																							.getString( "Query",
																									Collections
																											.singletonMap( "ru",
																													"Запрос" ) ),
																					"",
																					1,
																					16384 ).setFieldType( "text" ) )
																	.addField( ControlFieldFactory
																			.createFieldString( "result",
																					MultivariantString
																							.getString( "Result",
																									Collections
																											.singletonMap( "ru",
																													"Результат" ) ),
																					"" ).setFieldType( "text" )
																			.setConstant() );
	
	private ControlFieldset<?>				fieldset;
	
	private final String					alias;
	
	private static final ControlCommand<?>	CMD_QUERY		= Control
																	.createCommand( "query",
																			MultivariantString.getString( "Execute",
																					Collections.singletonMap( "ru",
																							"Выполнить" ) ) )
																	.setCommandIcon( "command-run" );
	
	private static final ControlFieldset<?> createFieldset(final ResultSetMetaData md) throws SQLException {
		final ControlFieldset<?> result = ControlFieldset.createFieldset();
		final int count = md.getColumnCount();
		for (int i = 1; i <= count; ++i) {
			final String id = md.getColumnName( i );
			final String title = md.getColumnLabel( i );
			switch (md.getColumnType( i )) {
			case Types.BIT:
			case Types.BOOLEAN:
				result.addField( ControlFieldFactory.createFieldBoolean( id, title, false ).setConstant() );
				break;
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
				result.addField( ControlFieldFactory.createFieldInteger( id, title, 0 ).setConstant() );
				break;
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
				result.addField( ControlFieldFactory.createFieldFloating( id, title, 0.0 ).setConstant() );
				break;
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
				result.addField( ControlFieldFactory.createFieldString( id, title, "" ).setConstant() );
				break;
			case Types.DATE:
			case Types.TIME:
			case Types.TIMESTAMP:
				result.addField( ControlFieldFactory.createFieldDate( id, title, 0L ).setConstant() );
				break;
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
				result.addField( ControlFieldFactory.createFieldBinary( id, title, Integer.MAX_VALUE ).setConstant() );
				break;
			case Types.NULL:
			case Types.OTHER:
			case Types.JAVA_OBJECT:
			case Types.DISTINCT:
			case Types.STRUCT:
			case Types.ARRAY:
			case Types.REF:
			case Types.DATALINK:
			default:
				result.addField( ControlFieldFactory.createFieldString( id, title + " (?)", "" ).setConstant() );
				break;
			}
		}
		return result;
	}
	
	FormSqlQuery(final String alias, final String query) {
		this.alias = alias;
		final BaseObject data = new BaseNativeObject()//
				.putAppend( "connection", alias )//
				.putAppend( "query", query )//
		;
		this.fieldset = FormSqlQuery.FIELDSET;
		this.setData( data );
		this.setAttributeIntern( "id", "query" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Query", Collections.singletonMap( "ru", "Выполнение запроса" ) ) );
		this.recalculate();
	}
	
	private final void execute() {
		final BaseObject data = this.getData();
		data.baseDefine("connection", this.alias);
		final String query = Base.getString( data, "query", "" );
		try (final Connection conn = Context.getServer( Exec.currentProcess() ).getServerConnection( this.alias )) {
			final long started = System.currentTimeMillis();
			try (final Statement st = conn.createStatement()) {
				st.execute( query );
				data.baseDefine("took", Format.Compact.toPeriod( System.currentTimeMillis() - started ));
				try (final ResultSet rs = st.getResultSet()) {
					if (rs == null) {
						this.fieldset = FormSqlQuery.FIELDSET_UPDATE;
						data.baseDefine("result", st.getUpdateCount() + " rows updated.");
					} else {
						final ControlFieldset<?> fieldset = FormSqlQuery.createFieldset( rs.getMetaData() );
						this.fieldset = ControlFieldset
								.createFieldset()
								.addField( ControlFieldFactory.createFieldString( "connection",
										MultivariantString.getString( "Connection",
												Collections.singletonMap( "ru", "Соединение" ) ),
										"" ).setConstant() )
								.addField( ControlFieldFactory.createFieldString( "query",
										MultivariantString.getString( "Query",
												Collections.singletonMap( "ru", "Запрос" ) ),
										"",
										1,
										16384 ).setFieldType( "text" ) )
								.addField( Control
										.createFieldList( "result",
												MultivariantString.getString( "Result",
														Collections.singletonMap( "ru", "Результат" ) ),
												null ).setConstant().setAttribute( "content_fieldset", fieldset ) )
								.addField( ControlFieldFactory.createFieldInteger( "count",
										MultivariantString.getString( "Row count",
												Collections.singletonMap( "ru", "К-во строк" ) ),
										0 ).setConstant() )
								.addField( ControlFieldFactory.createFieldString( "took",
										MultivariantString.getString( "Time elapsed",
												Collections.singletonMap( "ru", "Время выполнения" ) ),
										"" ).setConstant() );
						final BaseArrayDynamic<ControlBasic<?>> result = BaseObject.createArray();
						final BaseObject source = new BaseMapSqlResultSet( rs );
						int counter = 0;
						while (rs.next()) {
							counter++;
							final BaseObject target = new BaseNativeObject();
							fieldset.dataRetrieve( source, target );
							result.add( Control.createBasic( null, null, target ) );
						}
						final long value = counter;
						data.baseDefine("count", value);
						data.baseDefine("result", result);
					}
				}
			}
		} catch (final SQLException e) {
			this.fieldset = FormSqlQuery.FIELDSET_ERROR;
			data.baseDefine("result", Format.Throwable.toText( e ));
		}
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject parameters) {
		if (command == FormSqlQuery.CMD_QUERY) {
			this.execute();
			return this;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormSqlQuery.CMD_QUERY );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return this.fieldset;
	}
}
