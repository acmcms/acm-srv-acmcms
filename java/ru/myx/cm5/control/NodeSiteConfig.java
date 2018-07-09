/*
 * Created on 02.06.2004
 */
package ru.myx.cm5.control;

import java.util.Collections;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.Exec;
import ru.myx.cm5.control.lfs.NodeFileSystem;
import ru.myx.cm5.control.sharing.FormAllShares;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class NodeSiteConfig extends AbstractNode {
	private static final Object				STR_SITEFILES			= MultivariantString.getString( "Site files",
																			Collections.singletonMap( "ru",
																					"Файлы сайта" ) );
	
	private static final ControlCommand<?>	CMD_SITE_DETAILS		= Control
																			.createCommand( "details",
																					MultivariantString
																							.getString( "Site details",
																									Collections
																											.singletonMap( "ru",
																													"Параметры сайта" ) ) )
																			.setCommandPermission( "view" )
																			.setCommandIcon( "command-info" );
	
	private static final ControlCommand<?>	CMD_APPLICATION_VARS	= Control
																			.createCommand( "variables",
																					MultivariantString
																							.getString( "Application variables",
																									Collections
																											.singletonMap( "ru",
																													"Переменные окружения" ) ) )
																			.setCommandPermission( "view" )
																			.setCommandIcon( "command-info" );
	
	private static final ControlCommand<?>	CMD_ALL_SHARES			= Control
																			.createCommand( "shares",
																					MultivariantString
																							.getString( "All public site access points",
																									Collections
																											.singletonMap( "ru",
																													"Все публичные точки доступа" ) ) )
																			.setCommandPermission( "$modify_sharing" )
																			.setCommandIcon( "command-sharing" );
	
	private final Server					server;
	
	/**
	 * @param server
	 */
	public NodeSiteConfig(final Server server) {
		this.server = server;
		this.setAttributeIntern( "id", "config" );
		this.setAttributeIntern( "title", MultivariantString.getString( "Site configuration",
				Collections.singletonMap( "ru", "Настройки сайта" ) ) );
		this.recalculate();
		this.bind( new NodeSqlPools( server ) );
		this.bind( new NodePlugins( this ) );
		this.bind( new NodeFileSystem( this,
				server.getVfsRootEntry().toContainer(),
				"root",
				NodeSiteConfig.STR_SITEFILES ) );
	}
	
	@Override
	public AccessPermissions getCommandPermissions() {
		return Access
				.createPermissionsLocal()
				.addPermission( "view",
						MultivariantString.getString( "View site details and application variables",
								Collections.singletonMap( "ru", "Просматривать параметры сайта и переменные окружения" ) ) )
				.addPermission( "modify",
						MultivariantString.getString( "Modify application variables",
								Collections.singletonMap( "ru", "Изменять переменные окружения" ) ) );
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == NodeSiteConfig.CMD_SITE_DETAILS) {
			return new FormSiteDetails( this.server );
		}
		if (command == NodeSiteConfig.CMD_ALL_SHARES) {
			return new FormAllShares( this.server );
		}
		if (command == NodeSiteConfig.CMD_APPLICATION_VARS) {
			return new FormMapEditor( MultivariantString.getString( "Application variables",
					Collections.singletonMap( "ru", "Переменные окружения" ) ),
					MultivariantString.getString( "Application variables",
							Collections.singletonMap( "ru", "Переменные окружения" ) ),
					"modify",
					Base.forUnknown( Context.getServer( Exec.currentProcess() ).getProperties() ) );
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( NodeSiteConfig.CMD_SITE_DETAILS );
		result.add( NodeSiteConfig.CMD_APPLICATION_VARS );
		result.add( NodeSiteConfig.CMD_ALL_SHARES );
		return result;
	}
	
	@Override
	public final String getKey() {
		return "config";
	}
	
	@Override
	public final String getLocationControl() {
		return '/' + this.getKey();
	}
}
