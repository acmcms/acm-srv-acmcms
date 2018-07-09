/*
 * Created on 19.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormSqlTableStructure extends AbstractForm<FormSqlTableStructure> {
	private static final ControlFieldset<?>	FIELDSET_FIELDS			= ControlFieldset
																			.createFieldset()
																			.addField( ControlFieldFactory
																					.createFieldString( "columnName",
																							MultivariantString
																									.getString( "Column name",
																											Collections
																													.singletonMap( "ru",
																															"Имя столбца" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "typeOriginalName",
																							MultivariantString
																									.getString( "DBMS type name",
																											Collections
																													.singletonMap( "ru",
																															"Имя типа DBMS" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "typeName",
																							MultivariantString
																									.getString( "JDBC type name",
																											Collections
																													.singletonMap( "ru",
																															"Имя типа JDBC" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldInteger( "typeValue",
																							MultivariantString
																									.getString( "Type",
																											Collections
																													.singletonMap( "ru",
																															"Тип" ) ),
																							-1 ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldInteger( "columnSize",
																							MultivariantString
																									.getString( "Size",
																											Collections
																													.singletonMap( "ru",
																															"Размер" ) ),
																							-1 ).setConstant() );
	
	private static final ControlFieldset<?>	FIELDSET_PRIMARY_KEYS	= ControlFieldset
																			.createFieldset()
																			.addField( ControlFieldFactory
																					.createFieldString( "columnName",
																							MultivariantString
																									.getString( "Column name",
																											Collections
																													.singletonMap( "ru",
																															"Имя столбца" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldInteger( "columnIndex",
																							MultivariantString
																									.getString( "Index",
																											Collections
																													.singletonMap( "ru",
																															"Индекс" ) ),
																							-1 ).setConstant() );
	
	private static final ControlFieldset<?>	FIELDSET_ROW_ID			= ControlFieldset
																			.createFieldset()
																			.addField( ControlFieldFactory
																					.createFieldString( "columnName",
																							MultivariantString
																									.getString( "Column name",
																											Collections
																													.singletonMap( "ru",
																															"Имя столбца" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "typeOriginalName",
																							MultivariantString
																									.getString( "DBMS type name",
																											Collections
																													.singletonMap( "ru",
																															"имя типа DBMS" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "typeName",
																							MultivariantString
																									.getString( "JDBC type name",
																											Collections
																													.singletonMap( "ru",
																															"имя типа JDBC" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldInteger( "typeValue",
																							MultivariantString
																									.getString( "Type",
																											Collections
																													.singletonMap( "ru",
																															"Тип" ) ),
																							-1 ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldInteger( "columnSize",
																							MultivariantString
																									.getString( "Size",
																											Collections
																													.singletonMap( "ru",
																															"Размер" ) ),
																							-1 ).setConstant() );
	
	private static final ControlFieldset<?>	FIELDSET				= ControlFieldset
																			.createFieldset()
																			.addField( ControlFieldFactory
																					.createFieldString( "table",
																							MultivariantString
																									.getString( "Table",
																											Collections
																													.singletonMap( "ru",
																															"Таблица" ) ),
																							"" ).setConstant() )
																			.addField( Control
																					.createFieldList( "fields",
																							MultivariantString
																									.getString( "Columns",
																											Collections
																													.singletonMap( "ru",
																															"Столбцы" ) ),
																							null )
																					.setConstant()
																					.setAttribute( "content_fieldset",
																							FormSqlTableStructure.FIELDSET_FIELDS ) )
																			.addField( Control
																					.createFieldList( "keys",
																							MultivariantString
																									.getString( "Primary keys",
																											Collections
																													.singletonMap( "ru",
																															"Ключи" ) ),
																							null )
																					.setConstant()
																					.setAttribute( "content_fieldset",
																							FormSqlTableStructure.FIELDSET_PRIMARY_KEYS ) )
																			.addField( Control
																					.createFieldList( "rowid",
																							MultivariantString
																									.getString( "Best row identifier",
																											Collections
																													.singletonMap( "ru",
																															"Идентификатор строки" ) ),
																							null )
																					.setConstant()
																					.setAttribute( "content_fieldset",
																							FormSqlTableStructure.FIELDSET_ROW_ID ) );
	
	private static final String getTypeName(final int type) {
		switch (type) {
		case Types.BIT:
			return "bit";
		case Types.BOOLEAN:
			return "boolean";
		case Types.TINYINT:
			return "tinyint";
		case Types.SMALLINT:
			return "smallint";
		case Types.INTEGER:
			return "integer";
		case Types.BIGINT:
			return "bigint";
		case Types.FLOAT:
			return "float";
		case Types.REAL:
			return "real";
		case Types.DOUBLE:
			return "double";
		case Types.NUMERIC:
			return "numeric";
		case Types.DECIMAL:
			return "decimal";
		case Types.CHAR:
			return "char";
		case Types.VARCHAR:
			return "varchar";
		case Types.LONGVARCHAR:
			return "longvarchar";
		case Types.CLOB:
			return "clob";
		case Types.DATE:
			return "date";
		case Types.TIME:
			return "time";
		case Types.TIMESTAMP:
			return "timestamp";
		case Types.BINARY:
			return "binary";
		case Types.VARBINARY:
			return "varbinary";
		case Types.LONGVARBINARY:
			return "longvarbinary";
		case Types.BLOB:
			return "blob";
		case Types.NULL:
			return "null";
		case Types.OTHER:
			return "other";
		case Types.JAVA_OBJECT:
			return "object";
		case Types.DISTINCT:
			return "distinct";
		case Types.STRUCT:
			return "struct";
		case Types.ARRAY:
			return "array";
		case Types.REF:
			return "ref";
		case Types.DATALINK:
			return "datalink";
		default:
			return "-= unknown =-";
		}
	}
	
	private final String	table;
	
	FormSqlTableStructure(final String alias, final String table) {
		this.table = table;
		final BaseObject data = new BaseNativeObject();
		try (final Connection conn = Context.getServer( Exec.currentProcess() ).getServerConnection( alias )) {
			data.baseDefine("table", table);
			data.baseDefine("fields", Base.forArray( this.getFieldsListing( conn ) ));
			data.baseDefine("keys", Base.forArray( this.getPrimaryKeyListing( conn ) ));
			data.baseDefine("rowid", Base.forArray( this.getRowIdentifierListing( conn ) ));
			this.setData( data );
		} catch (final SQLException e) {
			throw new RuntimeException( e );
		}
		this.setAttributeIntern( "id", alias + "_" + table + "_info" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Table structure info: " + alias + "." + table,
						Collections.singletonMap( "ru", "Информация о структуре таблицы: " + alias + "." + table ) ) );
		this.recalculate();
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return FormSqlTableStructure.FIELDSET;
	}
	
	private final List<ControlBasic<?>> getFieldsListing(final Connection conn) throws SQLException {
		final List<ControlBasic<?>> result = new ArrayList<>();
		final String sourceSchema = conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf( "oracle" ) == -1
				? "%"
				: conn.getMetaData().getUserName();
		try (final ResultSet rs = conn.getMetaData().getColumns( conn.getCatalog(), sourceSchema, this.table, "%" )) {
			while (rs.next()) {
				final String key = rs.getString( "COLUMN_NAME" );
				final String title = key;
				final int type = rs.getInt( "DATA_TYPE" );
				final BaseObject data = new BaseNativeObject()//
						.putAppend( "columnName", title )//
						.putAppend( "typeOriginalName", rs.getString( "TYPE_NAME" ) )//
						.putAppend( "columnSize", rs.getString( "COLUMN_SIZE" ) )//
						.putAppend( "typeName", FormSqlTableStructure.getTypeName( type ) )//
						.putAppend( "typeValue", type )//
				;
				result.add( Control.createBasic( key, title, data ) );
			}
			return result;
		}
	}
	
	private final List<ControlBasic<?>> getPrimaryKeyListing(final Connection conn) throws SQLException {
		final List<ControlBasic<?>> result = new ArrayList<>();
		final boolean isOracle = conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf( "oracle" ) != -1;
		final boolean isMSSQL = !isOracle
				&& conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf( "mssql" ) != -1;
		final String catalog = isOracle || isMSSQL
				? conn.getCatalog()
				: null;
		final String schema = isOracle
				? conn.getMetaData().getUserName()
				: isMSSQL
						? "%"
						: null;
		try (final ResultSet rs = conn.getMetaData().getPrimaryKeys( catalog, schema, this.table )) {
			while (rs.next()) {
				final String primaryKeyName = rs.getString( "PK_NAME" );
				final String key = primaryKeyName == null
						? this.table
						: primaryKeyName;
				final String title = key;
				final BaseObject data = new BaseNativeObject()//
						.putAppend( "columnName", rs.getString( "COLUMN_NAME" ) )//
						.putAppend( "columnIndex", rs.getInt( "KEY_SEQ" ) )//
				;
				result.add( Control.createBasic( key, title, data ) );
			}
			return result;
		}
	}
	
	private final List<ControlBasic<?>> getRowIdentifierListing(final Connection conn) throws SQLException {
		final List<ControlBasic<?>> result = new ArrayList<>();
		final boolean isOracle = conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf( "oracle" ) != -1;
		final boolean isMSSQL = !isOracle
				&& conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf( "mssql" ) != -1;
		final String catalog = isOracle || isMSSQL
				? conn.getCatalog()
				: null;
		final String schema = isOracle
				? conn.getMetaData().getUserName()
				: isMSSQL
						? "%"
						: null;
		try (final ResultSet rs = conn.getMetaData().getBestRowIdentifier( catalog,
				schema,
				this.table,
				DatabaseMetaData.bestRowSession,
				true )) {
			while (rs.next()) {
				final String key = rs.getString( "COLUMN_NAME" );
				final String title = key;
				final int type = rs.getInt( "DATA_TYPE" );
				final BaseObject data = new BaseNativeObject()//
						.putAppend( "columnName", title )//
						.putAppend( "typeOriginalName", rs.getString( "TYPE_NAME" ) )//
						.putAppend( "columnSize", rs.getString( "COLUMN_SIZE" ) )//
						.putAppend( "typeName", FormSqlTableStructure.getTypeName( type ) )//
						.putAppend( "typeValue", type )//
				;
				result.add( Control.createBasic( key, title, data ) );
			}
			return result;
		}
	}
}
