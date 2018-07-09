/*
 * Created on 08.12.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.cm5.control.personal;

import java.util.Collections;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Know;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

final class FormPersonalInformation extends AbstractForm<FormPersonalInformation> {
	private static final ControlFieldset<?>	FIELDSET_PERSONAL;
	
	private final AccessUser<?>				user;
	
	private static final ControlCommand<?>	CMD_SAVE	= Control.createCommand( "save", " OK " )
																.setCommandIcon( "command-save" );
	
	static {
		FIELDSET_PERSONAL = ControlFieldset
				.createFieldset( "admin.persinfo" ) //
				.addField( ControlFieldFactory.createFieldString( "Nick",//
						MultivariantString.getString( "Nick name", //
								Collections.singletonMap( "ru", "Ник" ) ),
						"" ) )//
				.addField( ControlFieldFactory.createFieldString( "Name",//
						MultivariantString.getString( "Full name",//
								Collections.singletonMap( "ru", "Ф.И.О." ) ),
						"" ) )//
				.addField( ControlFieldFactory.createFieldString( "Language",//
						MultivariantString.getString( "Language",//
								Collections.singletonMap( "ru", "Язык" ) ),
						new CurrentLanguage() )//
						.setFieldType( "select" )//
						.setAttribute( "lookup", Know.SYSTEM_LANGUAGES ) );
	}
	
	FormPersonalInformation(final BaseObject title, final AccessUser<?> user) {
		this.user = user;
		this.setData( user.getProfile() );
		this.setAttributeIntern( "title", title );
		this.setAttributeIntern( "id", "personal.info" );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormPersonalInformation.CMD_SAVE) {
			this.user.setProfile( this.getData() );
			this.user.commit();
			return null;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormPersonalInformation.CMD_SAVE );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return FormPersonalInformation.FIELDSET_PERSONAL;
	}
}
