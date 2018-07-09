/*
 * Created on 17.05.2006
 */
package ru.myx.cm5.control.sharing;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.skinner.Skinner;

final class LookupSkinner extends BaseHostLookup {
	
	
	@Override
	public BaseObject baseGetLookupValue(final BaseObject key) {
		
		
		final Server server = Context.getServer(Exec.currentProcess());
		final Skinner skinner = server.getSkinner(String.valueOf(key));
		return skinner == null
			? key
			: skinner.getTitle();
	}
	
	@Override
	public Iterator<String> baseKeysOwn() {
		
		
		final Server server = Context.getServer(Exec.currentProcess());
		final Set<String> names = new TreeSet<>();
		for (final String key : server.getSkinnerNames()) {
			final Skinner skinner = server.getSkinner(key);
			if (!skinner.isAbstract()) {
				names.add(key);
			}
		}
		return names.iterator();
	}
	
	@Override
	public Iterator<? extends CharSequence> baseKeysOwnAll() {
		
		
		final Server server = Context.getServer(Exec.currentProcess());
		return server.getSkinnerNames().iterator();
	}
	
	@Override
	public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
		
		
		return Base.iteratorPrimitiveSafe(this.baseKeysOwn());
	}
	
	@Override
	public String toString() {
		
		
		return "[Lookup: Skinner Selection]";
	}
}
