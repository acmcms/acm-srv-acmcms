/*
 * Created on 14.06.2004
 */
package ru.myx.cm5.skin;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractActor;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.skinner.Skinner;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActorSkinPersonal extends AbstractActor<ActorSkinPersonal> {
	private static final BaseObject		STR_IFACE_CHOOSE	= MultivariantString
																		.getString( "Control: choose interface",
																				Collections.singletonMap( "ru",
																						"Управление: выбор интерфейса" ) );
	
	private static final BaseObject		STR_IFACE_SETUP		= MultivariantString
																		.getString( "Control: interface customization",
																				Collections
																						.singletonMap( "ru",
																								"Управление: настройки интерфейса" ) );
	
	private static final ControlCommand<?>	CMD_IFACE_CHOOSE	= Control.createCommand( "ifacechoose",
																		ActorSkinPersonal.STR_IFACE_CHOOSE )
																		.setCommandIcon( "command-change-interface" );
	
	private static final ControlCommand<?>	CMD_IFACE_SETUP		= Control.createCommand( "ifacesetup",
																		ActorSkinPersonal.STR_IFACE_SETUP )
																		.setCommandIcon( "command-setup-interface" );
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == ActorSkinPersonal.CMD_IFACE_CHOOSE) {
			return new FormInterfaceChoose( ActorSkinPersonal.STR_IFACE_CHOOSE, Context.getUser( Exec.currentProcess() )
					.getProfile() );
		}
		if (command == ActorSkinPersonal.CMD_IFACE_SETUP) {
			return new FormAdminCustomization( ActorSkinPersonal.STR_IFACE_SETUP, Context
					.getUser( Exec.currentProcess() ).getProfile( "mwmAdmin", false ) );
		}
		{
			throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
		}
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( ActorSkinPersonal.CMD_IFACE_CHOOSE );
		final ExecProcess process = Exec.currentProcess();
		final Object object = Base.getJava( Context.getRequest( process ).getAttributes(), "skinner", null );
		if (object != null && object instanceof Skinner) {
			final ControlFieldset<?> fieldset = ((Skinner) object).getSkinSettingsFieldset();
			if (fieldset != null && fieldset.size() > 0) {
				result.add( ActorSkinPersonal.CMD_IFACE_SETUP );
			}
		}
		return result;
	}
}
