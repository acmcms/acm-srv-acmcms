package ru.myx.srv.acm;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.produce.ObjectFactory;
import ru.myx.sapi.ApplicationSAPI;

/*
 * Created on 14.11.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public final class ApplicationFactory implements ObjectFactory<Object, Object> {

	private static final Class<?>[] TARGETS = {
			ApplicationSAPI.class
	};

	private static final String[] VARIETY = {
			"ACM_API_RT3_FACTORY_APPLICATION"
	};

	@Override
	public final boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {

		return true;
	}

	@Override
	public final Object produce(final String variant, final BaseObject attributes, final Object source) {

		return Base.getJava(Context.getServer(Exec.currentProcess()).getRootContext(), "Application", null);
	}

	@Override
	public final Class<?>[] sources() {

		return null;
	}

	@Override
	public final Class<?>[] targets() {

		return ApplicationFactory.TARGETS;
	}

	@Override
	public final String[] variety() {

		return ApplicationFactory.VARIETY;
	}
}
