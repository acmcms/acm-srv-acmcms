/*
 * Created on 29.04.2004
 */
package ru.myx.cm5.control;

import ru.myx.ae1.know.Server;
import java.util.function.Function;
import ru.myx.ae3.control.ControlActor;

/**
 * @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class CommonActorProvider implements Function<String, ControlActor<?>> {
	
	private final Server server;

	/**
	 * @param server
	 */
	public CommonActorProvider(final Server server) {
		this.server = server;
	}

	@Override
	public final ControlActor<?> apply(final String arg) {
		
		return new CommonActor(this.server, arg);
	}
}
