/*
 * Created on 20.10.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.srv.acm;

import ru.myx.ae1.control.status.NodeStatusProvider;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.produce.ObjectFactory;
import ru.myx.ae3.status.StatusProviderFiller;
import ru.myx.ae3.status.StatusRegistry;

/** @author myx */
public final class FactoryRtControl implements ObjectFactory<Object, Server> {
	
	private static final Class<?>[] TARGETS = {
			Server.class
	};
	
	private static final Class<?>[] SOURCES = null;
	
	private static final String[] VARIETY = {
			"ae1:RT3_CONTROL"
	};
	
	@Override
	public boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {
		
		return attributes != null;
	}
	
	@Override
	public Server produce(final String variant, final BaseObject attributes, final Object source) {
		
		final String id = Base.getString(attributes, "id", Engine.createGuid());
		final ServerDomain server = new ServerDomainController(id, attributes);
		HostRT3.stdinit(server, attributes, server.config);
		server.getControlRoot().bind(//
				new NodeStatusProvider(//
						new StatusProviderFiller("sysstate", "System status", StatusRegistry.ROOT_REGISTRY)//
				)//
		);
		server.start();
		return server;
	}
	
	@Override
	public Class<?>[] sources() {
		
		return FactoryRtControl.SOURCES;
	}
	
	@Override
	public Class<?>[] targets() {
		
		return FactoryRtControl.TARGETS;
	}
	
	@Override
	public String[] variety() {
		
		return FactoryRtControl.VARIETY;
	}
}
