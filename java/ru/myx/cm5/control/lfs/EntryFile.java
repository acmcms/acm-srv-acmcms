/**
 * 
 */
package ru.myx.cm5.control.lfs;

import ru.myx.ae1.control.AbstractControlEntry;
import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.mime.MimeType;
import ru.myx.ae3.vfs.EntryBinary;

/**
 * @author myx
 * 
 */
final class EntryFile extends AbstractControlEntry<EntryFile> {
	
	private final ControlEntry<?>	parent;
	
	private final EntryBinary		file;
	
	private BaseObject			data	= null;
	
	EntryFile(final ControlEntry<?> parent, final EntryBinary file) {
		this.parent = parent;
		this.file = file;
	}
	
	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		return null;
	}
	
	@Override
	public final ControlCommandset getCommands() {
		return null;
	}
	
	@Override
	public final BaseObject getData() {
		if (this.data == null) {
			final BaseObject data = new BaseNativeObject()//
					.putAppend( "$key", this.file.getKey() )//
					.putAppend( "$type", MimeType.forEntry( this.file, "application/octet-stream" ) )//
					.putAppend( "$size", this.file.getBinaryContentLength() )//
			;
			this.data = data;
		}
		return this.data;
	}
	
	@Override
	public final ControlCommandset getForms() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public final String getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public final String getKey() {
		return this.file.getKey();
	}
	
	@Override
	public final String getLocationControl() {
		final String parentLocation = this.parent.getLocationControl();
		return parentLocation.endsWith( "/" )
				? parentLocation + this.getKey()
				: parentLocation + '/' + this.getKey();
	}
	
	@Override
	public final String getTitle() {
		return this.file.getKey();
	}
	
}
