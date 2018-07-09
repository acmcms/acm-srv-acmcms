/*
 * Created on 05.11.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.Iterator;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/**
 * @author myx
 * 
 */
public final class LookupCommonUserFields extends BaseHostLookup {
	
	
	@Override
	public BaseObject baseGetLookupValue(final BaseObject key) {
		
		
		final ControlFieldset<?> cuf = ru.myx.cm5.control.um.NodeUM.getCommonFieldsDefinition();
		final ControlField current = cuf.getField(String.valueOf(key));
		return current == null
			? Base.forString("n/a")
			: Base.forString(current.getKey() + " (" + current.getTitle() + ')');
	}
	
	@Override
	public Iterator<String> baseKeysOwn() {
		
		
		final ControlFieldset<?> cuf = ru.myx.cm5.control.um.NodeUM.getCommonFieldsDefinition();
		return Base.keys(cuf);
	}
	
	@Override
	public Iterator<? extends CharSequence> baseKeysOwnAll() {
		
		
		return this.baseKeysOwn();
	}
	
	@Override
	public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
		
		
		final ControlFieldset<?> cuf = ru.myx.cm5.control.um.NodeUM.getCommonFieldsDefinition();
		return Base.keysPrimitive(cuf);
	}
	
	@Override
	public String toString() {
		
		
		return "[Lookup: Common User Fields]";
	}
}
