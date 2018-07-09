/*
 * Created on 26.01.2006
 */
package ru.myx.cm5.control.lfs;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.BaseArray;
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
final class FormDeleteM extends AbstractForm<FormDeleteM> {
	
	private static final ControlCommand<?> UNLINK = Control.createCommand("delete", "Delete").setCommandPermission("delete").setCommandIcon("command-delete");
	
	private static final ControlFieldset<?> FIELDSET_DELETE = ControlFieldset.createFieldset("confirmation")
			.addField(ControlFieldFactory.createFieldBoolean("confirmation", MultivariantString.getString("yes, i do.", Collections.singletonMap("ru", "однозначно")), false));
			
	private final Entry folder;
	
	private final BaseArray name;
	
	/**
	 * @param folder
	 * @param name
	 * @param path
	 */
	FormDeleteM(final Entry folder, final BaseArray name, final String path) {
		this.folder = folder;
		this.name = name;
		this.setAttributeIntern("id", "confirmation");
		this.setAttributeIntern(
				"title",
				MultivariantString.getString(
						"Do you really want to delete these " + name.length() + " file(s)?",
						Collections.singletonMap("ru", "Вы действительно хотите удалить эти " + name.length() + " файл(ов)?")));
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormDeleteM.UNLINK) {
			if (Convert.MapEntry.toBoolean(this.getData(), "confirmation", false)) {
				int deleted = 0;
				int failed = 0;
				final int length = this.name.length();
				for (int i = 0; i < length; ++i) {
					final String name = this.name.baseGet(i, BaseObject.UNDEFINED).baseToJavaString();
					final Entry file = this.folder.relative(name, null);
					if (file.doUnlink().baseValue().booleanValue()) {
						deleted++;
					} else {
						failed++;
					}
				}
				return failed == 0
					? deleted + " files deleted!"
					: deleted + " files deleted, " + failed + " failed!";
			}
			return "Action cancelled.";
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		final ControlCommandset result = Control.createOptions();
		result.add(FormDeleteM.UNLINK);
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return FormDeleteM.FIELDSET_DELETE;
	}
}
