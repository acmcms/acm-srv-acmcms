/*
 * Created on 19.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import ru.myx.util.MapSqlResultSet;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormSqlTableBrowse extends AbstractForm<FormSqlTableBrowse> {
	private final String				alias;
	
	private final String				table;
	
	private final ControlFieldset<?>	fieldset;
	
	FormSqlTableBrowse(final String alias, final String table) {
		this.alias = alias;
		this.table = table;
		try (final Connection conn = Context.getServer( Exec.currentProcess() ).getServerConnection( alias )) {
			this.fieldset = ControlFieldset.createFieldset().addField( Control
					.createFieldList( "listing",
							MultivariantString.getString( "Contents", Collections.singletonMap( "ru", "Содержимое" ) ),
							null ).setConstant().setAttribute( "content_fieldset", this.init( conn ) ) );
		} catch (final SQLException e) {
			throw new RuntimeException( e );
		}
		this.setData( new BaseNativeObject( "listing", Base.forArray( this.getContents() ) ) );
		this.setAttributeIntern( "id", alias + "_" + table + "_browse" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Table browsing: " + alias + "." + table,
						Collections.singletonMap( "ru", "Просмотр таблицы: " + alias + "." + table ) ) );
		this.recalculate();
	}
	
	List<ControlBasic<?>> getContents() {
		try (final Connection conn = Context.getServer( Exec.currentProcess() ).getServerConnection( this.alias )) {
			try (final PreparedStatement ps = conn.prepareStatement( "SELECT * FROM " + this.table,
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY )) {
				try (final ResultSet rs = ps.executeQuery()) {
					final BaseObject source = Base.forUnknown( new MapSqlResultSet( rs ) );
					final List<ControlBasic<?>> result = new ArrayList<>();
					while (rs.next()) {
						final BaseObject data = new BaseNativeObject();
						data.baseDefineImportAllEnumerable(source);
						result.add( Control.createBasic( null, null, data ) );
					}
					return result;
				}
			}
		} catch (final SQLException e) {
			throw new RuntimeException( e );
		}
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return this.fieldset;
	}
	
	private ControlFieldset<?> init(final Connection conn) throws SQLException {
		final ControlFieldset<?> result = ControlFieldset.createFieldset( this.table );
		final String sourceSchema = conn.getMetaData().getDatabaseProductName().toLowerCase().indexOf( "oracle" ) == -1
				? "%"
				: conn.getMetaData().getUserName();
		try (final ResultSet rs = conn.getMetaData().getColumns( conn.getCatalog(), sourceSchema, this.table, "%" )) {
			while (rs.next()) {
				final String key = rs.getString( "COLUMN_NAME" );
				final String title = key;
				final int type = rs.getInt( "DATA_TYPE" );
				switch (type) {
				case Types.BIT:
				case Types.BOOLEAN: {
					result.addField( ControlFieldFactory.createFieldBoolean( key, title, false ) );
					break;
				}
				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.INTEGER:
				case Types.BIGINT: {
					result.addField( ControlFieldFactory.createFieldInteger( key, title, 0 ) );
					break;
				}
				case Types.FLOAT:
				case Types.REAL:
				case Types.DOUBLE:
				case Types.NUMERIC:
				case Types.DECIMAL: {
					result.addField( ControlFieldFactory.createFieldFloating( key, title, 0.0 ) );
					break;
				}
				case Types.CHAR:
				case Types.VARCHAR: {
					result.addField( ControlFieldFactory.createFieldString( key, title, "" ) );
					break;
				}
				case Types.LONGVARCHAR:
				case Types.CLOB: {
					result.addField( ControlFieldFactory.createFieldString( key, title, "" ).setFieldType( "text" ) );
					break;
				}
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP: {
					result.addField( ControlFieldFactory.createFieldDate( key, title, 0L ) );
					break;
				}
				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
				case Types.BLOB: {
					result.addField( ControlFieldFactory.createFieldBinary( key, title, Integer.MAX_VALUE ) );
					break;
				}
				case Types.NULL:
				case Types.OTHER:
				case Types.JAVA_OBJECT:
				case Types.DISTINCT:
				case Types.STRUCT:
				case Types.ARRAY:
				case Types.REF:
				case Types.DATALINK:
				default: {
					result.addField( ControlFieldFactory.createFieldString( key, title + " (?)", "" )
							.setFieldType( "text" ) );
					break;
				}
				}
			}
			return result;
		}
	}
	
}
