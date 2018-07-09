/*
 * Created on 14.11.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.cm5.control;

import java.sql.Connection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

final class NodeSqlPools extends AbstractNode {
	protected final Server				server;
	
	private static ControlFieldset<?>	FIELDSET_CONNECTION	= ControlFieldset
																	.createFieldset( "pluginsDatabaseConnections" )
																	.addFields( new ControlField[] {
			ControlFieldFactory.createFieldString( "alias",
					MultivariantString.getString( "Pool alias", Collections.singletonMap( "ru", "Имя коннекта" ) ),
					"" ),
			ControlFieldFactory.createFieldString( "dbName",
					MultivariantString.getString( "Database name", Collections.singletonMap( "ru", "Название СУБД" ) ),
					"" ),
			ControlFieldFactory.createFieldString( "dbVer",
					MultivariantString.getString( "Database version", Collections.singletonMap( "ru", "Версия СУБД" ) ),
					"" ),
			ControlFieldFactory.createFieldString( "drName",
					MultivariantString.getString( "Driver name", Collections.singletonMap( "ru", "Название драйвера" ) ),
					"" ),
			ControlFieldFactory.createFieldString( "drVer", MultivariantString.getString( "Driver version",
					Collections.singletonMap( "ru", "Версия драйвера" ) ), "" ), } );
	
	NodeSqlPools(final Server server) {
		this.setAttributeIntern( "id", "pools" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Database connections",
						Collections.singletonMap( "ru", "Подключения к СУБД" ) ) );
		this.server = server;
	}
	
	@Override
	public AccessPermissions getCommandPermissions() {
		return Access
				.createPermissionsLocal()
				.addPermission( "view",
						MultivariantString.getString( "View connection properties",
								Collections.singletonMap( "ru", "Просмотр свойств соединения" ) ) )
				.addPermission( "modify",
						MultivariantString.getString( "Query execution",
								Collections.singletonMap( "ru", "Выполнение запросов" ) ) );
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject parameters) {
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		return null;
	}
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
		return NodeSqlPools.FIELDSET_CONNECTION;
	}
	
	@Override
	public List<ControlBasic<?>> getContents() {
		final List<ControlBasic<?>> result = BaseObject.createArray();
		for (final Map.Entry<String, Enumeration<Connection>> source : this.server.getConnections().entrySet()) {
			result.add( new NodeSqlPool( source.getKey(), source.getValue() ) );
		}
		return result;
	}
	
	@Override
	protected ControlNode<?> internGetChildByName(final String name) {
		return new NodeSqlPool( name, this.server.getConnections().get( name ) );
	}
	
	@Override
	protected ControlNode<?>[] internGetChildren() {
		final List<ControlBasic<?>> result = this.getContents();
		return result.toArray( new ControlNode<?>[result.size()] );
	}
	
	@Override
	protected ControlNode<?>[] internGetChildrenExternal() {
		return null;
	}
}
