/**
 * 
 */
package ru.myx.cm5.control.lfs;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.TreeLinkType;

/**
 * @author myx
 * 		
 */
final class FormUpload extends AbstractForm<FormUpload> {
	
	private static final ControlFieldset<?> FIELDSET = ControlFieldset.createFieldset()
			.addField(ControlFieldFactory.createFieldString("name", MultivariantString.getString("Name", Collections.singletonMap("ru", "Имя")), ""))
			.addField(ControlFieldFactory.createFieldBinary("file", MultivariantString.getString("File", Collections.singletonMap("ru", "Файл")), Integer.MAX_VALUE));
			
	private final Entry folder;
	
	private static final ControlCommand<?> CMD_CREATE = Control.createCommand("create", MultivariantString.getString("Create", Collections.singletonMap("ru", "Создать")));
	
	FormUpload(final Entry folder, final String path) {
		this.folder = folder;
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		
		if (command == FormUpload.CMD_CREATE) {
			final String name = Base.getString(arguments, "name", "").trim();
			if (name.length() == 0) {
				throw new IllegalArgumentException("Name cannot be empty!");
			}
			final Entry file = this.folder.relative(name, TreeLinkType.PUBLIC_TREE_REFERENCE);
			if (file == null) {
				throw new IllegalArgumentException("Can't create (" + name + ")! Is read only?");
			}
			if (file.isExist()) {
				throw new IllegalArgumentException("Already exists (" + name + ")!");
			}
			final BaseMessage message = (BaseMessage) arguments.baseGet("file", BaseObject.UNDEFINED);
			if (message == null) {
				throw new IllegalArgumentException("No file uploaded!");
			}
			file.doSetBinary(message.toBinary().getBinary());
			return null;
		}
		return super.getCommandResult(command, arguments);
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		return Control.createOptionsSingleton(FormUpload.CMD_CREATE);
	}
	
	@Override
	public final ControlFieldset<?> getFieldset() {
		
		return FormUpload.FIELDSET;
	}
	
}
