/*
 * Created on 27.06.2004
 * 
 */
package ru.myx.cm5.types;

import java.util.Collections;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.types.Type;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.serve.ServeRequest;

final class TypeInternalDisabled extends TypeInternal {
	TypeInternalDisabled(final String key, final Type<?> typeDefault) {
		super( typeDefault, key, MultivariantString.getString( "INTERNAL: Empty",
				Collections.singletonMap( "ru", "ВСТРОЕННЫЙ: Пустой" ) ), "disabled", ControlFieldset.createFieldset( key ) );
	}
	
	@Override
	public ReplyAnswer getResponse(final ServeRequest query, final BaseEntry<?> entry) {
		return Reply.empty( "EMPTY", query );
	}
	
	@Override
	public int getTypeBehaviorResponseCacheClientTtl() {
		return 60 * 60;
	}
	
	@Override
	public long getTypeBehaviorResponseCacheServerTtl() {
		return 0L;
	}
}
