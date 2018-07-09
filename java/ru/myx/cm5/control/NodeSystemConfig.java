/*
 * Created on 23.01.2006
 */
package ru.myx.cm5.control;

import java.util.Collections;

import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.MultivariantString;

/**
 * @author myx
 * 
 */
public class NodeSystemConfig extends AbstractNode {
	/**
	 * 
	 */
	public NodeSystemConfig() {
		this.setAttributeIntern( "id", "sysconfig" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "System configuration",
						Collections.singletonMap( "ru", "Настройки системы" ) ) );
		this.recalculate();
	}
}
