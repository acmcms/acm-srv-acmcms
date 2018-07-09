package ru.myx.cm5.skin;

import java.util.Collections;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.cm5.control.sharing.Sharing;

/**
 * Title: Editorial for WSM3 Description: Copyright: Copyright (c) 2001
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */

final class FormInterfaceChoose extends AbstractForm<FormInterfaceChoose> {
	private static final ControlCommand<?>	CMD_SAVE	= Control.createCommand( "save", " OK " )
																.setCommandIcon( "command-save" );
	
	FormInterfaceChoose(final BaseObject title, final BaseObject data) {
		this.setData( data );
		this.setAttributeIntern( "id", "admin.choose" );
		this.setAttributeIntern( "title", title );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormInterfaceChoose.CMD_SAVE) {
			final ExecProcess process = Exec.currentProcess();
			Context.getSessionData( process ).baseDelete( "mwmAdminUrlPath" );
			final AccessUser<?> user = Context.getUser( process );
			final BaseObject profile = user.getProfile();
			profile.baseDefineImportAllEnumerable(this.getData());
			user.setProfile( profile );
			user.commit();
			return null;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormInterfaceChoose.CMD_SAVE );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return ControlFieldset.createFieldset( "admin.customization" ).addField( ControlFieldFactory
				.createFieldString( "useInterface",
						MultivariantString.getString( "Interface", Collections.singletonMap( "ru", "Интерфейс" ) ),
						"choose" ).setFieldType( "select" ).setFieldVariant( "bigselect" )
				.setAttribute( "lookup", Sharing.SKINNER_SELECTION ) );
	}
}
