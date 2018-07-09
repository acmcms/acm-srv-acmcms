/*
 * Created on 13.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.Collections;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormEditUserCommonFields extends AbstractForm<FormEditUserCommonFields> {
	private static final ControlCommand<?>	CMD_SAVE	= Control.createCommand( "save",//
																MultivariantString.getString( "Save",//
																		Collections.singletonMap( "ru",//
																				"Сохранить" ) ) )//
																.setCommandPermission( "modify" )//
																.setCommandIcon( "command-save" )//
														;
	
	private static final ControlCommand<?>	CMD_PREV	= Control.createCommand( "prev",//
																MultivariantString.getString( "Prev...",//
																		Collections.singletonMap( "ru",//
																				"Назад..." ) ) )//
																.setCommandPermission( "modify" )//
																.setCommandIcon( "command-prev" )//
														;
	
	private final AccessUser<?>				user;
	
	private final BaseObject				data;
	
	private final String					path;
	
	FormEditUserCommonFields(final String path,
			final AccessUser<?> user,
			final BaseObject data,
			final BaseObject commonFields) {
		this.path = path;
		this.user = user;
		this.data = data;
		this.setData( commonFields );
		this.setAttributeIntern( "id", "edit_user_cf" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Edit user (common fields)",
						Collections.singletonMap( "ru", "Редактирование пользователя (общие поля)" ) ) );
		this.setAttributeIntern( "path", path );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormEditUserCommonFields.CMD_SAVE) {
			FormEditUser.commitChanges( this.user, this.data, this.getData() );
			return null;
		}
		if (command == FormEditUserCommonFields.CMD_PREV) {
			return new FormEditUser( this.path, this.user, this.data, this.getData() );
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( FormEditUserCommonFields.CMD_PREV );
		result.add( FormEditUserCommonFields.CMD_SAVE );
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return NodeUM.getCommonFieldsDefinition();
	}
}
