/*
 * Created on 03.06.2004
 */
package ru.myx.cm5.control;

import ru.myx.ae1.control.Control;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormMapEditor extends AbstractForm<FormMapEditor> {
	private final ControlFieldset<?>	fieldset;
	
	private final ControlCommand<?>		cmdSave;
	
	FormMapEditor(final BaseObject title,
			final Object fieldTitle,
			final String modifyPermission,
			final BaseObject map) {
		this.cmdSave = Control.createCommand( "ok", " OK " ).setCommandPermission( modifyPermission )
				.setCommandIcon( "command-save" );
		this.setAttributeIntern( "id", "map_editor" );
		this.setAttributeIntern( "title", title );
		this.recalculate();
		this.fieldset = ControlFieldset.createFieldset().addField( ControlFieldFactory.createFieldMap( "map", fieldTitle, null ) );
		this.setData( new BaseNativeObject( "map", map ) );
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == this.cmdSave) {
			return null;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( this.cmdSave );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return this.fieldset;
	}
}
