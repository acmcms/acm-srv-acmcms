/**
 * 
 */
package ru.myx.cm5.control.personal;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.exec.Exec;

final class CurrentLanguage {
	@Override
	public Object clone() throws CloneNotSupportedException {
		return this;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return obj == this || obj != null && this.toString().equals( obj.toString() );
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public String toString() {
		return Context.getRequest( Exec.currentProcess() ).getLanguage();
	}
}
