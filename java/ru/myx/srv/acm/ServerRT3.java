package ru.myx.srv.acm;

import ru.myx.ae1.know.ZoneServer;
import ru.myx.ae3.skinner.Skinner;

/**
 * @author myx
 * 
 */
public interface ServerRT3 extends ZoneServer {
	
	/**
	 * @return boolean
	 */
	public boolean isControllerServer();
	
	/**
	 * @param name
	 * @param skinner
	 * @return previous skinnner
	 */
	public Skinner registerSkinner(final String name, final Skinner skinner);
	
}
