/**
 * 
 */
package ru.myx.cm5.control.lfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseList;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.EntryContainer;
import ru.myx.ae3.vfs.TreeReadType;

/**
 * @author myx
 * 		
 */
class NodeFolder extends AbstractNode {
	
	private static final Comparator<ControlBasic<?>> COMPARATOR_CTRLBASIC_KEY_ASC = new Comparator<>() {
		
		@Override
		public final int compare(final ControlBasic<?> o1, final ControlBasic<?> o2) {
			
			return o1.getKey().compareTo(o2.getKey());
		}
	};
	
	private static final ControlFieldset<?> CONTENT_FIELDSET = ControlFieldset.createFieldset()
			.addField(ControlFieldFactory.createFieldString("$key", MultivariantString.getString("Name", Collections.singletonMap("ru", "Имя")), ""))
			.addField(ControlFieldFactory.createFieldString("$type", MultivariantString.getString("Type", Collections.singletonMap("ru", "Тип")), ""))
			.addField(ControlFieldFactory.createFieldLong("$size", MultivariantString.getString("Size", Collections.singletonMap("ru", "Размер")), 0L));
			
	private static final Object STR_MKDIR = MultivariantString.getString("Create folder", Collections.singletonMap("ru", "Создать папку"));
	
	private static final Object STR_CREATE = MultivariantString.getString("Create text file", Collections.singletonMap("ru", "Создать текстовый файл"));
	
	private static final Object STR_UPLOAD = MultivariantString.getString("Upload file", Collections.singletonMap("ru", "Загрузить файл"));
	
	private static final ControlCommand<?> CMD_CREATE = Control.createCommand("create", NodeFolder.STR_CREATE).setCommandPermission("create")
			.setAttribute("icon", "command-create");
			
	private static final ControlCommand<?> CMD_UPLOAD = Control.createCommand("upload", NodeFolder.STR_UPLOAD).setCommandPermission("create")
			.setAttribute("icon", "command-create");
			
	private static final ControlCommand<?> CMD_MKDIR = Control.createCommand("mkdir", NodeFolder.STR_MKDIR).setCommandPermission("create").setAttribute("icon", "command-create");
	
	private static final Object STR_DELETE = MultivariantString.getString("Delete", Collections.singletonMap("ru", "Удалить"));
	
	private static final ControlCommand<?> CMD_DELETE = Control.createCommand("delete", NodeFolder.STR_DELETE).setCommandPermission("delete")
			.setAttribute("icon", "command-delete");
			
	private final ControlEntry<?> parent;
	
	private final EntryContainer folder;
	
	/**
	 * @param parent
	 * @param folder
	 */
	NodeFolder(final ControlEntry<?> parent, final EntryContainer folder) {
		this.parent = parent;
		this.folder = folder;
	}
	
	@Override
	public AccessPermissions getCommandPermissions() {
		
		return null;
	}
	
	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == NodeFolder.CMD_MKDIR) {
			return new FormMkDir(this.folder, this.getLocationControl());
		}
		if (command == NodeFolder.CMD_CREATE) {
			return new FormCreate(this.folder, this.getLocationControl());
		}
		if (command == NodeFolder.CMD_UPLOAD) {
			return new FormUpload(this.folder, this.getLocationControl());
		}
		if (command == NodeFolder.CMD_DELETE) {
			return new FormDelete(this.folder.getParent(), this.folder.getKey(), this.getLocationControl());
		}
		if ("delete".equals(command.getKey())) {
			return new FormDelete(this.folder, Base.getString(command.getAttributes(), "key", "").trim(), this.getLocationControl());
		}
		if ("deletem".equals(command.getKey())) {
			final BaseArray names = Convert.MapEntry.toCollection(command.getAttributes(), "keys", null);
			return new FormDeleteM(this.folder, names, this.getLocationControl());
		}
		return null;
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		if (!this.folder.canWrite()) {
			return null;
		}
		final ControlCommandset result = Control.createOptions();
		result.add(NodeFolder.CMD_CREATE);
		result.add(NodeFolder.CMD_UPLOAD);
		result.add(NodeFolder.CMD_MKDIR);
		result.add(NodeFolder.CMD_DELETE);
		return result;
	}
	
	@Override
	public ControlCommandset getContentCommands(final String key) {
		
		final ControlCommandset result = Control.createOptions();
		result.add(Control.createCommand("delete", NodeFolder.STR_DELETE).setCommandPermission("delete").setAttribute("key", key).setAttribute("icon", "command-delete"));
		return result;
	}
	
	@Override
	public ControlEntry<?> getContentEntry(final String key) {
		
		final Entry file = this.folder.relative(key, null);
		if (file != null && file.isExist() && file.isBinary()) {
			return new EntryFile(this, file.toBinary());
		}
		return null;
	}
	
	@Override
	public ControlFieldset<?> getContentFieldset() {
		
		return NodeFolder.CONTENT_FIELDSET;
	}
	
	@Override
	public ControlCommandset getContentMultipleCommands(final BaseArray keys) {
		
		final ControlCommandset result = Control.createOptions();
		result.add(Control.createCommand("deletem", NodeFolder.STR_DELETE).setCommandPermission("delete").setAttribute("keys", keys).setAttribute("icon", "command-delete"));
		return result;
	}
	
	@Override
	public List<ControlBasic<?>> getContents() {
		
		final BaseList<Entry> files = this.folder.getContentCollection(TreeReadType.ITERABLE).baseValue();
		if (files == null || files.length() == 0) {
			return null;
		}
		final List<ControlBasic<?>> result = new ArrayList<>(files.length());
		for (int i = files.length() - 1; i >= 0; --i) {
			final Entry entry = files.get(i);
			if (entry.isBinary()) {
				result.add(new EntryFile(this, entry.toBinary()));
			}
		}
		if (result.isEmpty()) {
			return null;
		}
		final ControlBasic<?>[] array = result.toArray(new ControlBasic[result.size()]);
		Arrays.sort(array, NodeFolder.COMPARATOR_CTRLBASIC_KEY_ASC);
		return Arrays.asList(array);
	}
	
	@Override
	public String getKey() {
		
		return this.folder.getKey();
	}
	
	@Override
	public String getLocationControl() {
		
		final String parentLocation = this.parent.getLocationControl();
		return parentLocation.endsWith("/")
			? parentLocation + this.getKey()
			: parentLocation + '/' + this.getKey();
	}
	
	@Override
	public String getTitle() {
		
		return this.folder.getKey();
	}
	
	@Override
	public ControlNode<?> internGetChildByName(final String name) {
		
		final Entry file = this.folder.relative(name, null);
		return file == null || !file.isExist() || !file.isContainer()
			? null
			: new NodeFolder(this, file.toContainer());
	}
	
	@Override
	public ControlNode<?>[] internGetChildren() {
		
		final BaseList<Entry> folders = this.folder.getContentCollection(TreeReadType.ITERABLE).baseValue();
		if (folders == null || folders.length() == 0) {
			return null;
		}
		final List<ControlNode<?>> result = new ArrayList<>(folders.length());
		for (int i = folders.length() - 1; i >= 0; --i) {
			final Entry entry = folders.get(i);
			if (entry.isContainer()) {
				result.add(new NodeFolder(this, entry.toContainer()));
			}
		}
		if (result.isEmpty()) {
			return null;
		}
		final ControlNode<?>[] array = result.toArray(new ControlNode[result.size()]);
		Arrays.sort(array, NodeFolder.COMPARATOR_CTRLBASIC_KEY_ASC);
		return array;
	}
	
	@Override
	public final boolean internHasChildren() {
		
		final BaseList<Entry> contents = this.folder.getContentRange(null, null, 1, false, TreeReadType.ITERABLE).baseValue();
		return contents != null && contents.length() > 0;
	}
	
	@Override
	protected String toStringDetails() {
		
		return this.folder.toString();
	}
	
}
