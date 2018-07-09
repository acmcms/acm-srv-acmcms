package ru.myx.cm5.control.personal;

import java.util.Arrays;
import java.util.Collections;

import ru.myx.ae1.BaseRT3;
import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.messaging.MessagingManager;
import ru.myx.ae3.access.AccessPermission;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.access.AccessPreset;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.control.ControlContainer;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.xml.Xml;

/**
 * @author myx
 * 
 */
public final class NodePersonal extends AbstractNode {
	private static final Object				STR_NODE_TITLE	= MultivariantString.getString( "Personal",
																	Collections.singletonMap( "ru", "Персональное" ) );
	
	private static final ControlCommand<?>	CMD_CLEAR_TASKS	= Control
																	.createCommand( "clear_tasks",
																			MultivariantString
																					.getString( "Clear \"quick links\"",
																							Collections
																									.singletonMap( "ru",
																											"Сбросить \"быстрые ссылки\"" ) ) )
																	.setCommandIcon( "command-dispose" );
	
	/**
	 * @param limit
	 * @param options
	 * @return commandset
	 */
	public static final ControlCommandset tasksChart(final int limit, final ControlCommandset options) {
		final ExecProcess process = Exec.currentProcess();
		final Server server = Context.getServer( process );
		server.ensureAuthorization( AuthLevels.AL_AUTHORIZED_AUTOMATICALLY );
		final AccessUser<?> user = server.getAccessManager().getUser( Context.getUserId( process ), true );
		final BaseObject tasks = user.getProfile( "tasks_data", true );
		final TaskHistory history = new TaskHistory( tasks );
		return history.chart( limit, options );
	}
	
	/**
	 * @param command
	 * @param arguments
	 * @return object
	 * @throws Exception
	 */
	public static Object tasksCommandResult(final ControlCommand<?> command, final BaseObject arguments)
			throws Exception {
		final String taskId = Base.getString( command.getAttributes(), "quick_tasks_id", "" ).trim();
		final ControlContainer<?> container = (ControlContainer<?>) Base.getJava( command.getAttributes(),
				"quick_tasks_container",
				null );
		if (container == null) {
			throw new IllegalArgumentException( "Container inaccessible: " + taskId );
		}
		final ControlCommandset options = container.getCommands();
		if (options == null || options.isEmpty()) {
			throw new IllegalArgumentException( "Container has no options: " + taskId );
		}
		return container.getCommandResult( options.get( 0 ), arguments );
	}
	
	/**
	 * @param taskID
	 * @param taskAttributes
	 */
	public static final void tasksLog(final String taskID, final BaseObject taskAttributes) {
		NodePersonal.tasksLog( taskID, Xml.toXmlString( "data", taskAttributes, false ) );
	}
	
	private static final void tasksLog(final String taskID, final String taskXML) {
		Report.info( "PERSONAL", "TASK_LOG: " + taskID + ", xml=" + taskXML );
		final Server server = Context.getServer( Exec.currentProcess() );
		server.ensureAuthorization( AuthLevels.AL_AUTHORIZED_AUTOMATICALLY );
		final AccessUser<?> user = server.getAccessManager().getUser( Context.getUserId( Exec.currentProcess() ), true );
		final BaseObject tasks = user.getProfile( "tasks_data", true );
		final TaskHistory history = new TaskHistory( tasks );
		history.append( taskID, taskXML );
		history.store( tasks );
		user.setProfile( "tasks_data", tasks );
	}
	
	private final ControlNode<?>	nodeInbox	= new NodeMsgInbox();
	
	private final ControlNode<?>	nodeSent	= new NodeMsgSent();
	
	@Override
	public AccessPermissions getCommandPermissions() {
		final ControlActor<?>[] actors = BaseRT3.runtime().getPersonalActors();
		if (actors == null || actors.length == 0) {
			return null;
		}
		final AccessPermissions result = Access.createPermissionsLocal();
		for (final ControlActor<?> current : Arrays.asList( actors )) {
			final AccessPermissions permissions = current.getCommandPermissions();
			if (permissions != null) {
				final AccessPermission[] perms = permissions.getAllPermissions();
				if (perms != null) {
					for (final AccessPermission element : perms) {
						result.addPermission( element );
					}
				}
				final AccessPreset[] presets = permissions.getPresets();
				if (presets != null) {
					for (final AccessPreset element : presets) {
						result.addPreset( element );
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		if (command == NodePersonal.CMD_CLEAR_TASKS) {
			final ExecProcess process = Exec.currentProcess();
			Context.getServer( process ).ensureAuthorization( AuthLevels.AL_AUTHORIZED_AUTOMATICALLY );
			final AccessUser<?> user = Context.getServer( process ).getAccessManager()
					.getUser( Context.getUserId( process ), true );
			user.setProfile( "tasks_data", new BaseNativeObject() );
			return MultivariantString.getString( "\"Quick links\" were successfully cleared!",
					Collections.singletonMap( "ru", "\"Быстрые ссылки\" успешно очищены!" ) );
		}
		final ControlActor<?> item = (ControlActor<?>) command.getAttributes().baseGet( "pers_actor", BaseObject.UNDEFINED );
		final ControlCommand<?> cmnd = (ControlCommand<?>) command.getAttributes().baseGet( "pers_cmnd", BaseObject.UNDEFINED );
		if (item != null) {
			return item.getCommandResult( cmnd, arguments );
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( NodePersonal.CMD_CLEAR_TASKS );
		final ControlActor<?>[] actors = BaseRT3.runtime().getPersonalActors();
		for (final ControlActor<?> actor : Arrays.asList( actors )) {
			if (actor != null) {
				final ControlCommandset commands = actor.getCommands();
				if (commands != null && !commands.isEmpty()) {
					for (final ControlCommand<?> command : commands) {
						result.add( Control.createCommand( "", "" ).setAttributes( command.getAttributes() )
								.setAttribute( "pers_actor", actor ).setAttribute( "pers_cmnd", command ) );
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public String getIcon() {
		return "container-personal";
	}
	
	@Override
	public String getKey() {
		return "personal";
	}
	
	@Override
	public String getTitle() {
		return NodePersonal.STR_NODE_TITLE.toString();
	}
	
	@Override
	protected ControlNode<?> internGetChildByName(final String name) {
		if ("inbox".equals( name )) {
			return this.nodeInbox;
		}
		if ("sent".equals( name )) {
			return this.nodeSent;
		}
		return null;
	}
	
	@Override
	protected ControlNode<?>[] internGetChildren() {
		final Server server = Context.getServer( Exec.currentProcess() );
		final MessagingManager manager = server.getMessagingManager();
		if (manager.hasInbox()) {
			if (manager.hasSent()) {
				return new ControlNode<?>[] { this.nodeInbox, this.nodeSent };
			}
			return new ControlNode<?>[] { this.nodeInbox };
		}
		return null;
	}
	
	@Override
	protected boolean internHasChildren() {
		final Server server = Context.getServer( Exec.currentProcess() );
		final MessagingManager manager = server.getMessagingManager();
		return manager.hasInbox();
	}
}
