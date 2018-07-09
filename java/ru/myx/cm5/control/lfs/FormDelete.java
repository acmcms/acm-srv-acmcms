/*
 * Created on 26.01.2006
 */
package ru.myx.cm5.control.lfs;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.vfs.Entry;

/**
 * @author myx
 * 		
 */
final class FormDelete extends AbstractForm<FormDelete> {
	
	private static final ControlCommand<?> UNLINK = Control.createCommand("delete", "Delete").setCommandPermission("delete").setCommandIcon("command-delete");
	
	private static final ControlFieldset<?> FIELDSET_DELETE = ControlFieldset.createFieldset("confirmation")
			.addField(ControlFieldFactory.createFieldBoolean("confirmation", MultivariantString.getString("yes, i do.", Collections.singletonMap("ru", "однозначно")), false));
			
	private final Entry folder;
	
	private final String name;
	
	/**
	 * @param folder
	 * @param name
	 * @param path
	 */
	FormDelete(final Entry folder, final String name, final String path) {
		this.folder = folder;
		this.name = name;
		this.setAttributeIntern("id", "confirmation");
		this.setAttributeIntern(
				"title",
				MultivariantString.getString("Do you really want to delete this file?", Collections.singletonMap("ru", "Вы действительно хотите удалить этот файл?")));
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormDelete.UNLINK) {
			if (Convert.MapEntry.toBoolean(this.getData(), "confirmation", false)) {
				final Entry file = this.folder.relative(this.name, null);
				if (file != null && file.isExist()) {
					if (file.doUnlink().baseValue().booleanValue()) {
						return null;
					}
					return "Failed!";
				}
			}
			return "Action cancelled.";
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		final ControlCommandset result = Control.createOptions();
		result.add(FormDelete.UNLINK);
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return FormDelete.FIELDSET_DELETE;
	}
}
