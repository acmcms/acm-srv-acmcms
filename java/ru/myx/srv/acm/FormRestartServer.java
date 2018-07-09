/*
 * Created on 12.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.srv.acm;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Convert;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormRestartServer extends AbstractForm<FormRestartServer> {
	private static final ControlCommand<?>	RESTART	= Control.createCommand( Engine.createGuid(), "Restart" )
															.setCommandIcon( "command-reload" );
	
	FormRestartServer() {
		this.setAttributeIntern( "id", "confirmation" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Do you really want to restart server?",
						Collections.singletonMap( "ru", "Вы действительно хотите перезапустить сервер?" ) ) );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormRestartServer.RESTART) {
			if (Convert.MapEntry.toBoolean( this.getData(), "confirmation", false )) {
				Context.getServer( Exec.currentProcess() ).logQuickTaskUsage( "ACM_ROOT_COMMAND_RESTART_SERVER", BaseObject.UNDEFINED );
				Runtime.getRuntime().exit( 0 );
				return null;
			}
			return MultivariantString.getString( "Action cancelled.",
					Collections.singletonMap( "ru", "Действие отменено." ) );
		}
		return MultivariantString.getString( "Restarted already",
				Collections.singletonMap( "ru", "Пререзапуск уже осуществлен" ) );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormRestartServer.RESTART );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return ControlFieldset.createFieldset( "confirmation" )
				.addField( ControlFieldFactory.createFieldBoolean( "confirmation",
						MultivariantString.getString( "yes, i do.", Collections.singletonMap( "ru", "однозначно!" ) ),
						false ) );
	}
}
