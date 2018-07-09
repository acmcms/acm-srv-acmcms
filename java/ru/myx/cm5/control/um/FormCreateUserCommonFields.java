/*
 * Created on 13.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ru.myx.ae1.access.AccessGroup;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.PasswordType;
import ru.myx.ae1.access.UserTypes;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormCreateUserCommonFields extends AbstractForm<FormCreateUserCommonFields> {
	private static final ControlCommand<?>	CMD_REGISTER	= Control
																	.createCommand( "register",
																			MultivariantString.getString( "Register!",
																					Collections.singletonMap( "ru",
																							"Зарегистрировать!" ) ) )
																	.setCommandPermission( "control" )
																	.setCommandIcon( "command-create-user" );
	
	private static final ControlCommand<?>	CMD_PREV		= Control
																	.createCommand( "prev",
																			MultivariantString.getString( "Prev...",
																					Collections.singletonMap( "ru",
																							"Назад..." ) ) )
																	.setCommandPermission( "control" )
																	.setCommandIcon( "command-prev" );
	
	private final AccessUser<?>				user;
	
	private final BaseObject				data;
	
	private final String					path;
	
	FormCreateUserCommonFields(final String path,
			final AccessUser<?> user,
			final BaseObject data,
			final BaseObject commonFields) {
		this.path = path;
		this.user = user;
		this.data = data;
		this.setData( commonFields );
		this.setAttributeIntern( "id", "create_user" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Create user (common fields)",
						Collections.singletonMap( "ru", "Создание пользователя (общие поля)" ) ) );
		this.setAttributeIntern( "path", path );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormCreateUserCommonFields.CMD_REGISTER) {
			final BaseObject userData = this.data;
			final BaseObject commonFields = this.getData();
			this.user.setLogin( Base.getString( userData, "login", null ) );
			this.user.setEmail( Base.getString( userData, "email", null ) );
			this.user.setLanguage( Base.getString( userData, "language", null ) );
			this.user.setType( UserTypes.UT_HANDMADE );
			commonFields.baseDefine("name", Base.getString( userData, "name", null ));
			this.user.setProfile( commonFields );
			final AccessManager manager = Context.getServer( Exec.currentProcess() ).getAccessManager();
			final String password = Base.getString( userData, "password", "" ).trim();
			if (password.length() > 0) {
				manager.setPassword( this.user, password, PasswordType.NORMAL );
			}
			final String passwordHigh = Base.getString( userData, "passwordHigh", "" ).trim();
			if (passwordHigh.length() > 0) {
				manager.setPassword( this.user, passwordHigh, PasswordType.HIGHER );
			}
			manager.commitUser( this.user );
			final Set<AccessGroup<?>> groups = new HashSet<>();
			{
				final BaseArray groupIds = userData.baseGet( "groups", BaseObject.UNDEFINED ).baseArray();
				if (groupIds != null) {
					for (int i = groupIds.length() - 1; i >= 0; --i) {
						groups.add( manager.getGroup( groupIds.baseGet( i, BaseObject.UNDEFINED ).baseToString()
								.baseValue(), true ) );
					}
				}
			}
			manager.setGroups( this.user, groups.toArray( new AccessGroup<?>[groups.size()] ) );
			return null;
		}
		if (command == FormCreateUserCommonFields.CMD_PREV) {
			return new FormCreateUser( this.path, this.user, this.data, this.getData() );
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( FormCreateUserCommonFields.CMD_PREV );
		result.add( FormCreateUserCommonFields.CMD_REGISTER );
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return NodeUM.getCommonFieldsDefinition();
	}
}
