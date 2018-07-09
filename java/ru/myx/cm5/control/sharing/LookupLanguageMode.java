/**
 * 
 */
package ru.myx.cm5.control.sharing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BasePrimitive;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.know.Language;

final class LookupLanguageMode extends BaseHostLookup {
	
	
	private final BaseObject STR_LM_AUTO = MultivariantString.getString("automatic", Collections.singletonMap("ru", "автоматический"));
	
	private final BaseObject STR_LM_NONE = MultivariantString.getString("disabled", Collections.singletonMap("ru", "отключено"));
	
	@Override
	public final BaseObject baseGetLookupValue(final BaseObject key) {
		
		
		assert key != null : "NULL java object";
		if (key == BaseObject.UNDEFINED) {
			return this.STR_LM_AUTO;
		}
		final String string = key.baseToJavaString();
		if ("*".equals(string)) {
			return this.STR_LM_AUTO;
		}
		if ("-".equals(string)) {
			return this.STR_LM_NONE;
		}
		return Language.getLanguage(Context.getServer(Exec.currentProcess()).getLanguage(string)).baseGet("nativeName", BaseObject.UNDEFINED);
	}
	
	@Override
	public boolean baseHasKeysOwn() {
		
		
		return true;
	}
	
	@Override
	public Iterator<String> baseKeysOwn() {
		
		
		final List<String> result = new ArrayList<>();
		result.add("*");
		result.add("-");
		final String[] languages = Context.getServer(Exec.currentProcess()).getLanguages();
		if (languages != null) {
			for (final String element : languages) {
				result.add(element);
			}
		}
		return result.iterator();
	}
	
	@Override
	public Iterator<? extends CharSequence> baseKeysOwnAll() {
		
		
		return this.baseKeysOwn();
	}
	
	@Override
	public Iterator<? extends BasePrimitive<?>> baseKeysOwnPrimitive() {
		
		
		return Base.iteratorPrimitiveSafe(this.baseKeysOwn());
	}
	
	@Override
	public String toString() {
		
		
		return "[Lookup: Language Mode]";
	}
}
