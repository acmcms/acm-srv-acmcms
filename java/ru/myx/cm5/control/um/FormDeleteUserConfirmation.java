/*
 * Created on 13.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
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
final class FormDeleteUserConfirmation extends AbstractForm<FormDeleteUserConfirmation> {
	private static final ControlCommand<?>	DELETE	= Control.createCommand( "delete",
															MultivariantString.getString( "Delete",
																	Collections.singletonMap( "ru", "Удалить" ) ) )
															.setCommandIcon( "command-delete" );
	
	private final String					key;
	
	FormDeleteUserConfirmation(final String path, final String key) {
		this.key = key;
		this.setAttributeIntern( "id", "confirmation" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Do you really want to delete this user?",
						Collections.singletonMap( "ru", "Удалить этого пользователя?" ) ) );
		this.setAttributeIntern( "path", path );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormDeleteUserConfirmation.DELETE) {
			if (Convert.MapEntry.toBoolean( this.getData(), "confirmation", false )) {
				Context.getServer( Exec.currentProcess() ).getAccessManager().deleteUser( this.key );
				return null;
			}
			return "Action cancelled.";
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormDeleteUserConfirmation.DELETE );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return ControlFieldset.createFieldset( "confirmation" ).addField( ControlFieldFactory.createFieldBoolean( "confirmation",
				"yes, i do.",
				false ) );
	}
}
