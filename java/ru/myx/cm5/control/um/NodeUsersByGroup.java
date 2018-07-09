package ru.myx.cm5.control.um;

import ru.myx.ae1.access.AccessGroup;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.exec.Exec;

/**
 * Title: Base Implementations Description: Copyright: Copyright (c) 2001
 * Company: -= MyX =-
 * 
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
final class NodeUsersByGroup extends NodeUsers {
	NodeUsersByGroup(final String group) {
		this.groupsFilter = group;
	}
	
	@Override
	public String getKey() {
		return this.groupsFilter;
	}
	
	@Override
	public String getTitle() {
		final AccessGroup<?> group = Context.getServer( Exec.currentProcess() ).getAccessManager().getGroup( this.groupsFilter, false );
		return group == null
				? this.groupsFilter
				: group.getTitle();
	}
	
	@Override
	protected ControlNode<?> internGetChildByName(final String name) {
		return null;
	}
	
	@Override
	protected ControlNode<?>[] internGetChildren() {
		return null;
	}
	
	@Override
	protected boolean internHasChildren() {
		return false;
	}
}
