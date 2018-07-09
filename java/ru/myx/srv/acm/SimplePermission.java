/*
 * Created on 08.06.2004
 */
package ru.myx.srv.acm;

import ru.myx.ae3.access.AccessPermission;
import ru.myx.ae3.base.BaseObject;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class SimplePermission implements AccessPermission {
	private final String		key;
	
	private final BaseObject	title;
	
	private final boolean		forControl;
	
	SimplePermission(final String key, final BaseObject title, final boolean forControl) {
		this.key = key;
		this.title = title;
		this.forControl = forControl;
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public BaseObject getTitle() {
		return this.title;
	}
	
	@Override
	public boolean isForControl() {
		return this.forControl;
	}
}
