/**
 *
 */
package ru.myx.srv.acm;

import java.util.Collections;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import java.util.function.Function;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.SimpleCommand;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Format;

final class SignalGarbageCollect extends SimpleCommand implements Function<Void, Object> {
	
	private static final BaseObject STR_GARBAGE_COLLECT = MultivariantString.getString("Force Garbage Collector", Collections.singletonMap("ru", "Запустить сборщик мусора"));

	SignalGarbageCollect() {
		this.setAttributeIntern("id", "force_gc");
		this.setAttributeIntern("title", SignalGarbageCollect.STR_GARBAGE_COLLECT);
		this.recalculate();
		this.setCommandPermission("garbage_collect");
		this.setCommandIcon("command-dispose");
	}

	@Override
	public Object apply(final Void arg) {
		
		final Server server = Context.getServer(Exec.currentProcess());
		final long free1 = Runtime.getRuntime().freeMemory();
		System.runFinalization();
		System.gc();
		final long free2 = Runtime.getRuntime().freeMemory();
		server.logQuickTaskUsage("ACM_ROOT_COMMAND_GARBAGE_COLLECT", BaseObject.UNDEFINED);
		return "Garbage collector successfully launched\nFree before: " + Format.Compact.toBytes(free1) + "B\nFree after: " + Format.Compact.toBytes(free2) + "B.";
	}

	@Override
	public String getTitle() {
		
		return SignalGarbageCollect.STR_GARBAGE_COLLECT.toString();
	}
}
