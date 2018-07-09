package ru.myx.cm5.control.um;

import java.util.ArrayList;
import java.util.List;

import ru.myx.ae1.access.AccessGroup;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.exec.Exec;

/**
 * <p>
 * Title: RT3 adaptor
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */
public abstract class NodeChildUserByGroups extends AbstractNode {
	@Override
	protected ControlNode<?> internGetChildByName(final String name) {
		return new NodeUsersByGroup( name );
	}
	
	@Override
	protected ControlNode<?>[] internGetChildren() {
		final AccessGroup<?>[] groups = Context.getServer( Exec.currentProcess() ).getAccessManager().getAllGroups();
		final List<NodeUsersByGroup> result = new ArrayList<>( groups.length );
		for (int i = groups.length - 1; i >= 0; --i) {
			final AccessGroup<?> group = groups[i];
			final int authLevel = group.getAuthLevel();
			if (authLevel > AuthLevels.AL_UNAUTHORIZED && authLevel < AuthLevels.AL_AUTHORIZED_SYSTEM_EXCLUSIVE) {
				result.add( new NodeUsersByGroup( group.getKey() ) );
			}
		}
		return result.toArray( new ControlNode<?>[result.size()] );
	}
	
	@Override
	protected boolean internHasChildren() {
		return true;
	}
}
