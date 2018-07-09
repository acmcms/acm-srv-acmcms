package ru.myx.cm5.skin;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.Control;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.skinner.Skinner;

/**
 * Title: Editorial for WSM3 Description: Copyright: Copyright (c) 2001
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
final class FormAdminCustomization extends AbstractForm<FormAdminCustomization> {
	private static final ControlCommand<?>	CMD_SAVE	= Control.createCommand( "save", " OK " )
																.setCommandIcon( "command-save" );
	
	FormAdminCustomization(final BaseObject title, final BaseObject data) {
		this.setData( data );
		this.setAttributeIntern( "id", "admin.customization" );
		this.setAttributeIntern( "title", title );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormAdminCustomization.CMD_SAVE) {
			final ExecProcess process = Exec.currentProcess();
			Context.getSessionData( process ).baseDelete( "mwmAdminUrlPath" );
			final AccessUser<?> user = Context.getUser( process );
			final BaseObject profile = user.getProfile( "mwmAdmin", true );
			profile.baseDefineImportAllEnumerable(this.getData());
			user.setProfile( "mwmAdmin", profile );
			user.commit();
			return null;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormAdminCustomization.CMD_SAVE );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		final Object object = Base.getJava( Context.getRequest( Exec.currentProcess() ).getAttributes(),
				"skinner",
				null );
		if (object == null) {
			return null;
		}
		if (!(object instanceof Skinner)) {
			return null;
		}
		return ((Skinner) object).getSkinSettingsFieldset();
	}
}
