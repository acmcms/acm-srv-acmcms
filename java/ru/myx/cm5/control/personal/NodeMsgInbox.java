/*
 * Created on 10.10.2004
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.cm5.control.personal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.messaging.Message;
import ru.myx.ae1.messaging.MessageFactory;
import ru.myx.ae1.messaging.Messaging;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Convert;

/**
 * @author myx
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
final class NodeMsgInbox extends AbstractNode {
	private static final Object				STR_NODE_TITLE		= MultivariantString.getString( "Inbox",
																		Collections.singletonMap( "ru", "Входящие" ) );
	
	private static final ControlFieldset<?>	FIELDSET_LISTING	= ControlFieldset
																		.createFieldset()
																		.addField( ControlFieldFactory
																				.createFieldOwner( "sender",
																						MultivariantString
																								.getString( "From",
																										Collections
																												.singletonMap( "ru",
																														"От" ) ) ) )
																		.addField( ControlFieldFactory
																				.createFieldString( "subject",
																						MultivariantString
																								.getString( "Subject",
																										Collections
																												.singletonMap( "ru",
																														"Тема" ) ),
																						"" ) )
																		.addField( ControlFieldFactory
																				.createFieldBoolean( "read",
																						MultivariantString
																								.getString( "Read",
																										Collections
																												.singletonMap( "ru",
																														"Читал" ) ),
																						false ) )
																		.addField( ControlFieldFactory.createFieldDate( "date",
																				MultivariantString.getString( "Date",
																						Collections.singletonMap( "ru",
																								"Дата" ) ),
																				0L ) );
	
	private static final Object				STR_DELETE			= MultivariantString.getString( "Delete",
																		Collections.singletonMap( "ru", "Удалить" ) );
	
	private static final Object				STR_OPEN			= MultivariantString.getString( "Open",
																		Collections.singletonMap( "ru", "Открыть" ) );
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		if ("open".equals( command.getKey() )) {
			final Message message = Context.getServer( Exec.currentProcess() ).getMessagingManager()
					.getInbox( Convert.MapEntry.toInt( command.getAttributes(), "key", -1 ) );
			if (message == null) {
				return "No message found!";
			}
			final MessageFactory factory = Messaging.getMessageFactory( message.getMessageFactoryId() );
			if (factory != null && factory.isFormSupported()) {
				return factory.createMessageForm( message );
			}
			return "Factory is null or no forms supported: " + factory;
		}
		if ("delete".equals( command.getKey() )) {
			final int key = Convert.MapEntry.toInt( command.getAttributes(), "key", -1 );
			Context.getServer( Exec.currentProcess() ).getMessagingManager().deleteInbox( key );
			return null;
		}
		if ("deletem".equals( command.getKey() )) {
			final BaseArray keysArray = command.getAttributes().baseGet( "keys", BaseObject.UNDEFINED )
					.baseArray();
			final int length = keysArray.length();
			final int[] keys = new int[length];
			for (int i = 0; i < length; ++i) {
				keys[i] = Convert.Any.toInt( keysArray.baseGet( i, null ), -1 );
			}
			Context.getServer( Exec.currentProcess() ).getMessagingManager().deleteInbox( keys );
			return null;
		}
		{
			return super.getCommandResult( command, arguments );
		}
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		final Message message = Context.getServer( Exec.currentProcess() ).getMessagingManager()
				.getInbox( Convert.Any.toInt( key, -1 ) );
		if (message == null) {
			return null;
		}
		final MessageFactory factory = Messaging.getMessageFactory( message.getMessageFactoryId() );
		if (factory != null && factory.isFormSupported()) {
			final ControlCommandset result = Control.createOptions();
			result.add( Control.createCommand( "open", NodeMsgInbox.STR_OPEN ).setCommandIcon( "command-edit" )
					.setAttribute( "key", key ) );
			result.add( Control.createCommand( "delete", NodeMsgInbox.STR_DELETE ).setCommandIcon( "command-delete" )
					.setAttribute( "key", key ) );
			return result;
		}
		return Control.createOptionsSingleton( Control.createCommand( "delete", NodeMsgInbox.STR_DELETE )
				.setCommandIcon( "command-delete" ).setAttribute( "key", key ) );
	}
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
		return NodeMsgInbox.FIELDSET_LISTING;
	}
	
	@Override
	public ControlCommandset getContentMultipleCommands(final BaseArray keys) {
		return Control.createOptionsSingleton( Control.createCommand( "deletem", NodeMsgInbox.STR_DELETE )
				.setCommandIcon( "command-delete" ).setAttribute( "keys", keys ) );
	}
	
	@Override
	public List<ControlBasic<?>> getContents() {
		final Message[] messages = Context.getServer( Exec.currentProcess() ).getMessagingManager().getInbox();
		if (messages == null) {
			return null;
		}
		final List<ControlBasic<?>> result = new ArrayList<>();
		for (final Message message : messages) {
			final BaseObject data = new BaseNativeObject()//
					.putAppend( "sender", message.getMessageSender() )//
					.putAppend( "date", Base.forDateMillis( message.getMessageDate() ) )//
					.putAppend( "read", message.getMessageRead() )//
					.putAppend( "subject", //
							Messaging.getMessageFactory( message.getMessageFactoryId() ).getMessageTitle( message ) )//
			;
			result.add( Control.createBasic( String.valueOf( message.getMessageLuid() ), message.getMessageId(), data ) );
		}
		return result;
	}
	
	@Override
	public String getIcon() {
		return "container-inbox";
	}
	
	@Override
	public String getKey() {
		return "inbox";
	}
	
	@Override
	public String getTitle() {
		return NodeMsgInbox.STR_NODE_TITLE.toString();
	}
}
