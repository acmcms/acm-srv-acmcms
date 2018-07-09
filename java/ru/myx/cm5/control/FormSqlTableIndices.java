/*
 * Created on 19.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control;

import java.sql.Connection;
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
final class FormSqlTableIndices extends AbstractForm<FormSqlTableIndices> {
	private static final ControlFieldset<?>	FIELDSET_FIELDS		= ControlFieldset
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
	
	private static final ControlFieldset<?>	FIELDSET_INDICES	= ControlFieldset
																		.createFieldset()
																		.addField( ControlFieldFactory
																				.createFieldBoolean( "nonUnique",
																						MultivariantString
																								.getString( "non-Unique",
																										Collections
																												.singletonMap( "ru",
																														"не уникальный" ) ),
																						false ).setConstant() )
																		.addField( ControlFieldFactory
																				.createFieldString( "type",
																						MultivariantString
																								.getString( "Index type",
																										Collections
																												.singletonMap( "ru",
																														"Тип индекса" ) ),
																						"" ).setConstant() )
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
																						-1 ).setConstant() )
																		.addField( ControlFieldFactory
																				.createFieldFloating( "cardinality",
																						MultivariantString
																								.getString( "Cardinality",
																										Collections
																												.singletonMap( "ru",
																														"Кардинальность" ) ),
																						-1.0 ).setConstant() )
																		.addField( ControlFieldFactory
																				.createFieldInteger( "pages",
																						MultivariantString
																								.getString( "Pages",
																										Collections
																												.singletonMap( "ru",
																														"Страницы" ) ),
																						-1 ).setConstant() );
	
	private static final ControlFieldset<?>	FIELDSET			= ControlFieldset
																		.createFieldset()
																		.addField( Control
																				.createFieldList( "indices",
																						MultivariantString
																								.getString( "Indices",
																										Collections
																												.singletonMap( "ru",
																														"Индексы" ) ),
																						null )
																				.setConstant()
																				.setAttribute( "content_fieldset",
																						FormSqlTableIndices.FIELDSET_INDICES ) )
																		.addField( Control
																				.createFieldList( "fields",
																						MultivariantString
																								.getString( "Fields",
																										Collections
																												.singletonMap( "ru",
																														"Поля" ) ),
																						null )
																				.setConstant()
																				.setAttribute( "content_fieldset",
																						FormSqlTableIndices.FIELDSET_FIELDS ) );
	
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
	
	FormSqlTableIndices(final String alias, final String table) {
		this.table = table;
		final BaseObject data = new BaseNativeObject();
		try (final Connection conn = Context.getServer( Exec.currentProcess() ).getServerConnection( alias )) {
			data.baseDefine("fields", Base.forArray( this.getFieldsListing( conn ) ));
			data.baseDefine("indices", Base.forArray( this.getIndicesListing( conn ) ));
			this.setData( data );
		} catch (final SQLException e) {
			throw new RuntimeException( e );
		}
		this.setAttributeIntern( "id", alias + "_" + table + "_index" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Table index info: " + alias + "." + table,
						Collections.singletonMap( "ru", "Информация о индексах таблицы: " + alias + "." + table ) ) );
		this.recalculate();
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return FormSqlTableIndices.FIELDSET;
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
						.putAppend( "typeName", FormSqlTableIndices.getTypeName( type ) )//
						.putAppend( "typeValue", type )//
				;
				result.add( Control.createBasic( key, title, data ) );
			}
			return result;
		}
	}
	
	private final List<ControlBasic<?>> getIndicesListing(final Connection conn) throws SQLException {
		final List<ControlBasic<?>> result = new ArrayList<>();
		final String sourceSchema = conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf( "oracle" ) == -1
				? "%"
				: conn.getMetaData().getUserName();
		try (final ResultSet rs = conn.getMetaData().getIndexInfo( conn.getCatalog(),
				sourceSchema,
				this.table,
				false,
				false )) {
			while (rs.next()) {
				final String key = rs.getString( "INDEX_NAME" );
				final String title = key;
				final BaseObject data = new BaseNativeObject()//
						.putAppend( "nonUnique", rs.getBoolean( "NON_UNIQUE" ) )//
						.putAppend( "type", rs.getInt( "TYPE" ) )//
						.putAppend( "columnName", rs.getString( "COLUMN_NAME" ) )//
						.putAppend( "columnIndex", rs.getInt( "ORDINAL_POSITION" ) )//
						.putAppend( "cardinality", rs.getInt( "CARDINALITY" ) )//
						.putAppend( "pages", rs.getInt( "PAGES" ) )//
				;
				result.add( Control.createBasic( key, title, data ) );
			}
			return result;
		}
	}
	
}
