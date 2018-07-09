/*
 * Created on 12.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseArray;
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
final class FormDeleteUserConfirmationMultiple extends AbstractForm<FormDeleteUserConfirmationMultiple> {
	
	private static final ControlCommand<?> DELETE = Control.createCommand("delete", MultivariantString.getString("Delete", Collections.singletonMap("ru", "Удалить")))
			.setCommandPermission("delete").setCommandIcon("command-delete");
			
	private final BaseArray keys;
	
	FormDeleteUserConfirmationMultiple(final String path, final BaseArray keys) {
		this.keys = keys;
		this.setAttributeIntern("id", "confirmation");
		this.setAttributeIntern(
				"title",
				MultivariantString.getString(
						"Do you really want to delete these " + keys.length() + " users?",
						Collections.singletonMap("ru", "Вы действительно хотите удалить эти " + keys.length() + " пользовател(ей/я)?")));
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormDeleteUserConfirmationMultiple.DELETE) {
			if (Convert.MapEntry.toBoolean(this.getData(), "confirmation", false)) {
				final int length = this.keys.length();
				for (int i = 0; i < length; ++i) {
					final String userId = this.keys.baseGet(i, BaseObject.UNDEFINED).baseToJavaString();
					Context.getServer(Exec.currentProcess()).getAccessManager().deleteUser(userId);
				}
				return null;
			}
			return "Action cancelled.";
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		return Control.createOptionsSingleton(FormDeleteUserConfirmationMultiple.DELETE);
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return ControlFieldset.createFieldset("confirmation")
				.addField(ControlFieldFactory.createFieldBoolean("confirmation", MultivariantString.getString("yes, i do.", Collections.singletonMap("ru", "однозначно")), false));
	}
}
