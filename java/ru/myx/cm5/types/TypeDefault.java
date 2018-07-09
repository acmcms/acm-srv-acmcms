package ru.myx.cm5.types;

import java.util.Collection;

import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.types.AbstractType;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.serve.ServeRequest;

/*
 * Created on 18.09.2005
 */
final class TypeDefault extends AbstractType {
	
	TypeDefault(final ExecProcess parentContext) {
		super( null, parentContext, "$default", null );
	}
	
	@Override
	public ControlFieldset<?> getFieldsetCreate() {
		return null;
	}
	
	@Override
	public ControlFieldset<?> getFieldsetLoad() {
		return null;
	}
	
	@Override
	public ControlFieldset<?> getFieldsetProperties() {
		return null;
	}
	
	@Override
	public Collection<String> getReplacements() {
		return null;
	}
	
	@Override
	public ReplyAnswer getResponse(final ServeRequest query, final BaseEntry<?> entry) {
		return null;
	}
	
	@Override
	public boolean getTypeBehaviorAutoRecalculate() {
		return false;
	}
	
	@Override
	public boolean getTypeBehaviorHandleAllIncoming() {
		return false;
	}
	
	@Override
	public boolean getTypeBehaviorHandleAnyThrough() {
		return false;
	}
	
	@Override
	public boolean getTypeBehaviorHandleToParent() {
		return false;
	}
	
	@Override
	public String getTypeBehaviorListingSort() {
		return null;
	}
	
	@Override
	public int getTypeBehaviorResponseCacheClientTtl() {
		return 0;
	}
	
	@Override
	public long getTypeBehaviorResponseCacheServerTtl() {
		return 0;
	}
	
	@Override
	public long getTypeModificationDate() {
		return 0;
	}
	
	@Override
	public BaseObject getTypePrototypeObject() {
		return null;
	}
}
