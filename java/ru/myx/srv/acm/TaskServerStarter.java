/*
 * Created on 13.04.2006
 */
package ru.myx.srv.acm;

import ru.myx.ae1.PluginInstance;
import java.util.function.Function;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.report.Report;
import ru.myx.cm5.control.sharing.Sharing;

final class TaskServerStarter implements Function<ServerDomain, Object> {

	static final TaskServerStarter INSTANCE = new TaskServerStarter();

	private TaskServerStarter() {
		//
	}

	@Override
	public final Object apply(final ServerDomain server) {
		
		{
			Report.info("DOMAIN-START", "starting server (id = " + server.getZoneId() + "), starting plugins");
			for (final PluginInstance plugin : server.plugins) {
				try {
					plugin.start();
				} catch (final Throwable t) {
					Report.exception("DOMAIN-START", "While starting plugin (" + plugin + ")", t);
				}
			}
		}
		{
			final Object object = server.config.get("startup");
			if (object != null) {
				if (object instanceof Function<?, ?>) {
					final Function<ExecProcess, Object> script = Convert.Any.toAny(object);
					try {
						script.apply(server.getRootContext());
					} catch (final Throwable t) {
						Report.exception("DOMAIN-START", "Error in startup script!", t);
					}
				} else {
					Report.warning("DOMAIN-START", "startup script is not a script, class=" + object.getClass().getName());
				}
			}
		}
		try {
			Report.info("DOMAIN-START", "starting server (id = " + server.getZoneId() + "), starting types");
			server.getTypes().start();
		} catch (final Throwable t) {
			Report.exception("DOMAIN-START", "While initializing types", t);
		}
		try {
			Report.info("DOMAIN-START", "starting server (id = " + server.getZoneId() + "), starting skins");
			server.getSkins().start();
		} catch (final Throwable t) {
			Report.exception("DOMAIN-START", "While initializing skins", t);
		}
		try {
			Report.info("DOMAIN-START", "starting server (id = " + server.getZoneId() + "), starting shares");
			server.setShares(Sharing.getSharings(server));
		} catch (final Throwable t) {
			Report.exception("DOMAIN-START", "While setting shares", t);
		}
		return null;
	}

	@Override
	public final String toString() {
		
		return "Server Starter Task";
	}
}
