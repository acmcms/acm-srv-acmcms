package ru.myx.cm5.control.um;

import java.util.Collections;
import java.util.List;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.SortMode;
import ru.myx.ae1.access.UserTypes;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Convert;

/**
 * Title: Base Implementations Description: Copyright: Copyright (c) 2001
 * Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
class NodeUsers extends NodeChildUserByGroups {
	static final ControlFieldset<?>			FIELDSET_USER_LISTING	= ControlFieldset
																			.createFieldset()
																			.addField( ControlFieldFactory
																					.createFieldString( "login",
																							MultivariantString
																									.getString( "Login",
																											Collections
																													.singletonMap( "ru",
																															"Логин" ) ),
																							MultivariantString
																									.getString( " -= unknown =-",
																											Collections
																													.singletonMap( "ru",
																															"-= неизвестен =-" ) ) ) )
																			.addField( ControlFieldFactory
																					.createFieldString( "email",
																							"E-mail",
																							MultivariantString
																									.getString( " -= unknown =-",
																											Collections
																													.singletonMap( "ru",
																															"-= неизвестен =-" ) ) ) )
																			.addField( ControlFieldFactory
																					.createFieldDate( "added",
																							MultivariantString
																									.getString( "Added",
																											Collections
																													.singletonMap( "ru",
																															"Зарегистрирован" ) ),
																							0L ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldDate( "logged",
																							MultivariantString
																									.getString( "Logged / changed",
																											Collections
																													.singletonMap( "ru",
																															"Посл.вход / изменен" ) ),
																							0L ).setConstant() );
	
	private static final Object				NODE_TITLE				= MultivariantString.getString( "All users",
																			Collections.singletonMap( "ru",
																					"Пользователи (все)" ) );
	
	private static final ControlCommand<?>	CMD_CREATE_USER			= Control
																			.createCommand( "new_user",
																					MultivariantString
																							.getString( "Add new user...",
																									Collections
																											.singletonMap( "ru",
																													"Добавить нового пользователя..." ) ) )
																			.setCommandPermission( "create" )
																			.setCommandIcon( "command-create-user" );
	
	private static final ControlCommandset	NODE_COMMANDS			= Control
																			.createOptionsSingleton( NodeUsers.CMD_CREATE_USER );
	
	int										MinType					= UserTypes.UT_REGISTERED;
	
	int										MaxType					= UserTypes.UT_SYSTEM;
	
	protected String						groupsFilter;
	
	@Override
	public AccessPermissions getCommandPermissions() {
		return Access
				.createPermissionsLocal()
				.addPermission( "view",
						MultivariantString.getString( "View user data",
								Collections.singletonMap( "ru", "Просматривать свойства пользователя" ) ) )
				.addPermission( "create",
						MultivariantString.getString( "Create users",
								Collections.singletonMap( "ru", "Создавать пользователей" ) ) )
				.addPermission( "modify",
						MultivariantString.getString( "Edit users",
								Collections.singletonMap( "ru", "Редактировать пользователей" ) ) )
				.addPermission( "delete",
						MultivariantString.getString( "Delete users",
								Collections.singletonMap( "ru", "Удалять пользователей" ) ) );
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == NodeUsers.CMD_CREATE_USER) {
			return new FormCreateUser( "/usman/" + this.getKey() );
		}
		final String userId = Base.getString( command.getAttributes(), "key", "" );
		if ("edit".equals( command.getKey() )) {
			assert userId.length() > 0 : "UserID parameter should be passed in command attributes!";
			return new FormEditUser( "/usman/" + this.getKey(), userId );
		}
		if ("delete".equals( command.getKey() )) {
			assert userId.length() > 0 : "UserID parameter should be passed in command attributes!";
			if (userId.equals( Context.getUserId( Exec.currentProcess() ) )) {
				throw new IllegalArgumentException( MultivariantString.getString( "Cannot delete yourself!!!",
						Collections.singletonMap( "ru", "Нельзя удалить себя!" ) ).toString() );
			}
			return new FormDeleteUserConfirmation( "/usman/" + this.getKey(), userId );
		}
		if ("resend".equals( command.getKey() )) {
			assert userId.length() > 0 : "UserID parameter should be passed in command attributes!";
			NodeUM.registerUserRetryEmail( userId );
			return "Ok!";
		}
		if ("delete_multi".equals( command.getKey() )) {
			final BaseArray keys = Convert.MapEntry.toCollection( command.getAttributes(), "keys", null );
			if (keys != null && !keys.isEmpty()) {
				if (keys.length() == 1) {
					return new FormDeleteUserConfirmation( "/usman/" + this.getKey(), Base.getFirstString( keys, null ) );
				}
				return new FormDeleteUserConfirmationMultiple( "/usman/" + this.getKey(), keys );
			}
			return null;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return NodeUsers.NODE_COMMANDS;
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		final ControlCommandset result = Control.createOptions();
		assert key != null && key.length() > 0 : "UserID parameter is required!";
		result.add( Control.createCommand( "edit", //
				MultivariantString.getString( "Properties", //
						Collections.singletonMap( "ru", "Свойства" ) ) )//
				.setCommandPermission( "view" )//
				.setCommandIcon( "command-edit" )//
				.setAttribute( "key", key ) )//
		;
		result.add( Control.createCommand( "delete", //
				MultivariantString.getString( "Delete", //
						Collections.singletonMap( "ru", "Удалить" ) ) ) //
				.setCommandPermission( "delete" )//
				.setCommandIcon( "command-delete" )//
				.setAttribute( "key", key ) )//
		;
		result.add( Control.createCommand( "resend", //
				MultivariantString.getString( "Resend registration email (password will be changed!)", //
						Collections.singletonMap( "ru", //
								"Перепослать регистрационное письмо (пароль будет изменен!)" ) ) ) //
				.setCommandPermission( "delete" )//
				.setCommandIcon( "command-reload" )//
				.setAttribute( "key", key ) )//
		;
		return result;
	}
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
		return NodeUsers.FIELDSET_USER_LISTING;
	}
	
	@Override
	public ControlCommandset getContentMultipleCommands(final BaseArray keys) {
		final ControlCommandset result = Control.createOptions();
		result.add( Control
				.createCommand( "delete_multi",
						MultivariantString.getString( "Delete", Collections.singletonMap( "ru", "Удалить" ) ) )
				.setCommandPermission( "delete" ).setCommandIcon( "command-delete" ).setAttribute( "keys", keys ) );
		return result;
	}
	
	@Override
	public List<ControlBasic<?>> getContents() {
		final SortMode sort = SortMode.SM_LOGIN;
		final AccessManager manager = Context.getServer( Exec.currentProcess() ).getAccessManager();
		return this.groupsFilter != null
				? new UsersListing( manager.searchByMembership( Collections.singleton( this.groupsFilter ), sort ) )
				: new UsersListing( manager.searchByType( this.MinType, this.MaxType, sort ) );
	}
	
	@Override
	public String getIcon() {
		return "container-users";
	}
	
	@Override
	public String getKey() {
		return "users";
	}
	
	@Override
	public String getTitle() {
		return NodeUsers.NODE_TITLE.toString();
	}
}
