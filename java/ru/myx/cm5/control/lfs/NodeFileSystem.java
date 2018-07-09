/**
 * 
 */
package ru.myx.cm5.control.lfs;

import ru.myx.ae1.control.ControlEntry;
import ru.myx.ae3.vfs.EntryContainer;

/**
 * @author myx
 * 
 */
public final class NodeFileSystem extends NodeFolder {
	
	private final String	key;
	
	private final Object	title;
	
	/**
	 * @param parent
	 * @param folder
	 * @param key
	 * @param title
	 */
	public NodeFileSystem(final ControlEntry<?> parent,
			final EntryContainer folder,
			final String key,
			final Object title) {
		super( parent, folder );
		this.key = key;
		this.title = title;
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public String getTitle() {
		return this.title.toString();
	}
	
}
