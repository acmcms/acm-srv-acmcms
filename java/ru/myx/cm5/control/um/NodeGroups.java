package ru.myx.cm5.control.um;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessGroup;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;

/**
 * Title: Base Implementations Description: Copyright: Copyright (c) 2001
 * Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
final class NodeGroups extends AbstractNode {
	private static final Object				nodeTitle				= MultivariantString.getString( "Groups",
																			Collections.singletonMap( "ru", "Группы" ) );
	
	private static final ControlCommand<?>	CMD_CREATE_GROUP		= Control
																			.createCommand( "new_group",
																					MultivariantString
																							.getString( "Add new group...",
																									Collections
																											.singletonMap( "ru",
																													"Добавить новую группу..." ) ) )
																			.setCommandPermission( "create" )
																			.setCommandIcon( "command-create-group" );
	
	private static ControlFieldset<?>		FIELDSET_GROUP_LISTING	= ControlFieldset
																			.createFieldset()
																			.addFields( new ControlField[] {
		ControlFieldFactory.createFieldString( "title",
				MultivariantString.getString( "Title", Collections.singletonMap( "ru", "Название" ) ),
				"- untitled -" ),
		ControlFieldFactory.createFieldString( "description",
				MultivariantString.getString( "Description", Collections.singletonMap( "ru", "Описание" ) ),
				"" ).setFieldType( "text" ),
		ControlFieldFactory.createFieldString( "count",
				MultivariantString.getString( "User count", Collections.singletonMap( "ru", "К-во пользователей" ) ),
				"" ).setConstant(),
		ControlFieldFactory
				.createFieldInteger( "authLevel",
						MultivariantString.getString( "Auth level for membership checks",
								Collections.singletonMap( "ru", "Авторизация" ) ),
						AuthLevels.AL_UNAUTHORIZED ).setFieldType( "select" ).setAttribute( "lookup", Access.AUTHORIZATION_TYPES ), } );
	
	private static Set<String>				defaultGroups			= new TreeSet<>( Arrays.asList( new String[] {
		"def.guest",
		"def.registered",
		"def.supervisor",
		"def.system"												} ) );
	
	
	@Override
	public AccessPermissions getCommandPermissions() {
	
		return Access
				.createPermissionsLocal()
				.addPermission( "view",
						MultivariantString.getString( "View group data",
								Collections.singletonMap( "ru", "Просматривать свойства группы" ) ) )
				.addPermission( "create",
						MultivariantString.getString( "Create group", Collections.singletonMap( "ru", "Создавать группы" ) ) )
				.addPermission( "modify",
						MultivariantString.getString( "Edit group", Collections.singletonMap( "ru", "Редактировать группы" ) ) )
				.addPermission( "delete",
						MultivariantString.getString( "Delete group", Collections.singletonMap( "ru", "Удалять группы" ) ) );
	}
	
	
	@Override
	public Object getCommandResult(
			final ControlCommand<?> command,
			final BaseObject arguments) {
	
		if (command == NodeGroups.CMD_CREATE_GROUP) {
			return Context.getServer( Exec.currentProcess() ).getAccessManager()
					.createFormGroupCreation( "/usman/" + this.getKey() );
		}
		if ("edit".equals( command.getKey() )) {
			return Context.getServer( Exec.currentProcess() ).getAccessManager()
					.createFormGroupProperties( "/usman/" + this.getKey(), Base.getString( command.getAttributes(), "key", "" ) );
		}
		if ("delete".equals( command.getKey() )) {
			return new FormDeleteGroupConfirmation( "/usman/" + this.getKey(),
					Base.getString( command.getAttributes(), "key", "" ) );
		}
		{
			throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
		}
	}
	
	
	@Override
	public ControlCommandset getCommands() {
	
		return Control.createOptionsSingleton( NodeGroups.CMD_CREATE_GROUP );
	}
	
	
	@Override
	public ControlCommandset getContentCommands(
			final String key) {
	
		final ControlCommandset result = Control.createOptions();
		if (NodeGroups.defaultGroups.contains( key )) {
			result.add( Control
					.createCommand( "edit",
							MultivariantString.getString( "Properties", Collections.singletonMap( "ru", "Свойства" ) ) )
					.setCommandPermission( "view" ).setCommandIcon( "command-edit" ).setAttribute( "key", key ) );
		} else {
			result.add( Control
					.createCommand( "edit",
							MultivariantString.getString( "Properties", Collections.singletonMap( "ru", "Свойства" ) ) )
					.setCommandPermission( "view" ).setCommandIcon( "command-edit" ).setAttribute( "key", key ) );
			result.add( Control
					.createCommand( "delete",
							MultivariantString.getString( "Delete", Collections.singletonMap( "ru", "Удалить" ) ) )
					.setCommandPermission( "delete" ).setCommandIcon( "command-delete" ).setAttribute( "key", key ) );
		}
		return result;
	}
	
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
	
		return NodeGroups.FIELDSET_GROUP_LISTING;
	}
	
	
	@Override
	public List<ControlBasic<?>> getContents() {
	
		final List<ControlBasic<?>> result = new ArrayList<>();
		final AccessManager manager = Context.getServer( Exec.currentProcess() ).getAccessManager();
		final AccessGroup<?>[] groups = manager.getAllGroups();
		for (final AccessGroup<?> element : groups) {
			try {
				final String id = element.getKey();
				final AccessUser<?>[] users = manager.getUsers( element );
				final int count = users == null
						? -1
						: users.length;
				
				final BaseObject data = new BaseNativeObject()//
						.putAppend( "title", element.getTitle() )//
						.putAppend( "description", element.getDescription() )//
						.putAppend( "authLevel", element.getAuthLevel() )//
						.putAppend( "count", //
								count == -1
										? "unknown"
										: String.valueOf( count ) )//
				;
				result.add( Control.createBasic( id, element.getTitle(), data ) );
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException( e );
			}
		}
		return result;
	}
	
	
	@Override
	public String getKey() {
	
		return "groups";
	}
	
	
	@Override
	public String getTitle() {
	
		return NodeGroups.nodeTitle.toString();
	}
}
