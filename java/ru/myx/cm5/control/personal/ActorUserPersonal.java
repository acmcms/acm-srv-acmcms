/*
 * Created on 14.06.2004
 */
package ru.myx.cm5.control.personal;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractActor;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.Exec;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActorUserPersonal extends AbstractActor<ActorUserPersonal> {
	private static final BaseObject		STR_PERSONAL_INFO			= MultivariantString
																				.getString( "Personal information",
																						Collections
																								.singletonMap( "ru",
																										"Персональная информация" ) );
	
	private static final BaseObject		STR_PASSWORD_SITE			= MultivariantString
																				.getString( "Site password change",
																						Collections
																								.singletonMap( "ru",
																										"Смена пароля (для сайта)" ) );
	
	private static final BaseObject		STR_PASSWORD_HIGH			= MultivariantString
																				.getString( "Priveleged password change",
																						Collections.singletonMap( "ru",
																								"Смена пароля (high)" ) );
	
	private static final ControlCommand<?>	CMD_PERSONAL_INFORMATION	= Control
																				.createCommand( "personalinfo",
																						ActorUserPersonal.STR_PERSONAL_INFO )
																				.setCommandIcon( "command-edit-personal" );
	
	private static final ControlCommand<?>	CMD_PASSWORD_SITE			= Control
																				.createCommand( "passwordsite",
																						ActorUserPersonal.STR_PASSWORD_SITE )
																				.setCommandIcon( "command-change-password" );
	
	private static final ControlCommand<?>	CMD_PASSWORD_HIGH			= Control
																				.createCommand( "passwordhigh",
																						ActorUserPersonal.STR_PASSWORD_HIGH )
																				.setCommandIcon( "command-change-password" );
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == ActorUserPersonal.CMD_PERSONAL_INFORMATION) {
			return new FormPersonalInformation( ActorUserPersonal.STR_PERSONAL_INFO, Context.getUser( Exec
					.currentProcess() ) );
		}
		if (command == ActorUserPersonal.CMD_PASSWORD_SITE) {
			return new FormPasswordChange( ActorUserPersonal.STR_PASSWORD_SITE,
					"passwordsite",
					FormPasswordChange.VALIDATE_PASSWORD_NORM,
					Context.getUser( Exec.currentProcess() ) );
		}
		if (command == ActorUserPersonal.CMD_PASSWORD_HIGH) {
			return new FormPasswordChange( ActorUserPersonal.STR_PASSWORD_HIGH,
					"passwordhigh",
					FormPasswordChange.VALIDATE_PASSWORD_HIGH,
					Context.getUser( Exec.currentProcess() ) );
		}
		{
			throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
		}
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( ActorUserPersonal.CMD_PERSONAL_INFORMATION );
		result.add( ActorUserPersonal.CMD_PASSWORD_SITE );
		result.add( ActorUserPersonal.CMD_PASSWORD_HIGH );
		return result;
	}
}
