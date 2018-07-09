/*
 * Created on 29.04.2004
 */
package ru.myx.cm5.control;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractActor;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.Exec;
import ru.myx.cm5.control.sharing.FormAccessPointSetup;
import ru.myx.cm5.control.sharing.FormClearSharings;
import ru.myx.cm5.control.sharing.Sharing;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class CommonActor extends AbstractActor<CommonActor> {
	private final Server					server;
	
	private final String					path;
	
	private static final ControlCommand<?>	CMD_SHARING		= Control
																	.createCommand( "sha",
																			MultivariantString
																					.getString( "Public access point settings",
																							Collections
																									.singletonMap( "ru",
																											"Настройки публичной точки доступа" ) ) )
																	.setCommandPermission( "$modify_sharing" )
																	.setCommandIcon( "command-sharing" );
	
	private static final ControlCommand<?>	CMD_SECURITY	= Control
																	.createCommand( "sec",
																			MultivariantString
																					.getString( "Security settings",
																							Collections
																									.singletonMap( "ru",
																											"Настройки безопасности" ) ) )
																	.setCommandPermission( "$modify_security" )
																	.setCommandIcon( "command-security" );
	
	CommonActor(final Server server, final String path) {
		this.server = server;
		this.path = Sharing.fixPath( path );
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == CommonActor.CMD_SHARING) {
			return new FormAccessPointSetup( this.server, this.path );
		}
		if (command == CommonActor.CMD_SECURITY) {
			return Context.getServer( Exec.currentProcess() ).getAccessManager().createFormSecuritySetup( this.path );
		}
		if ("sha_cancel".equals( command.getKey() )) {
			return new FormClearSharings( this.server, this.path );
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		if (this.server.getAccessManager().createFormSecuritySetup( this.path ) == null) {
			final ControlCommandset result = Control.createOptions();
			result.add( CommonActor.CMD_SHARING );
			try {
				final Share<?>[] shares = Sharing.getShareSetupFor( this.server, this.path );
				if (shares != null && shares.length > 0) {
					result.add( Control
							.createCommand( "sha_cancel",
									MultivariantString.getString( "Cancel sharings (" + shares.length + ")",
											Collections.singletonMap( "ru", "Отменить точки доступа ("
													+ shares.length
													+ ")" ) ) ).setCommandPermission( "$modify_sharing" )
							.setCommandIcon( "command-sharing" ) );
				}
			} catch (final Throwable t) {
				// ignore
			}
			return result;
		}
		final ControlCommandset result = Control.createOptions();
		result.add( CommonActor.CMD_SHARING );
		try {
			final Share<?>[] shares = Sharing.getShareSetupFor( this.server, this.path );
			if (shares != null && shares.length > 0) {
				result.add( Control
						.createCommand( "sha_cancel",
								MultivariantString.getString( "Cancel sharings (" + shares.length + ")",
										Collections.singletonMap( "ru", "Отменить точки доступа ("
												+ shares.length
												+ ")" ) ) ).setCommandPermission( "$modify_sharing" )
						.setCommandIcon( "command-sharing" ) );
			}
		} catch (final Throwable t) {
			// ignore
		}
		result.add( CommonActor.CMD_SECURITY );
		return result;
	}
}
