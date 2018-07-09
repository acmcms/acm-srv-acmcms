/*
 * Created on 05.05.2006
 */
package ru.myx.srv.acm;

import ru.myx.ae1.control.Control;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractActor;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.cm5.control.personal.NodePersonal;

final class QuickCommandActor extends AbstractActor<QuickCommandActor> {
	@Override
	public final Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments)
			throws Exception {
		return NodePersonal.tasksCommandResult( command, arguments );
	}
	
	@Override
	public final ControlCommandset getCommands() {
		return NodePersonal.tasksChart( 25, Control.createOptions() );
	}
}
