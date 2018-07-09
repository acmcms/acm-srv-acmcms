/*
 * Created on 27.06.2004
 */
package ru.myx.cm5.types;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.storage.BaseChange;
import ru.myx.ae1.storage.BaseEntry;
import ru.myx.ae1.storage.ModuleInterface;
import ru.myx.ae1.types.Type;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;

abstract class TypeInternal extends AbstractBasic<TypeInternal> implements Type<TypeInternal> {
	
	
	private static final Set<String> EMPTY_SET_STRING = Collections.unmodifiableSet(new TreeSet<String>());

	static final int HT_ALL = 3;

	static final int HT_ANY = 2;

	static final int HT_DEFAULT = 0;

	static final String[] HT_NAMES = {
			"default", "parent", "any", "all"
	};

	static final int HT_PARENT = 1;

	static final BaseHostLookup LOOKUP_SCRIPT_TYPES = new ru.myx.ae3.control.ControlLookupStatic()
			.putAppend(String.valueOf(ModuleInterface.SCRIPT_TYPE_DYNAMIC), MultivariantString.getString("Dynamic script", Collections.singletonMap("ru", "Динамический скрипт")))
			.putAppend(
					String.valueOf(ModuleInterface.SCRIPT_TYPE_STATIC),
					MultivariantString.getString("Static script", Collections.singletonMap("ru", "Статический скрипт (кешируемый)")))
			.putAppend(String.valueOf(ModuleInterface.SCRIPT_TYPE_PAGE), MultivariantString.getString("Static page", Collections.singletonMap("ru", "Статическая страница")));

	private static final String NULL_SORT = "history";

	private int behaviorAutoRecalculate = -1;

	private int behaviorHandleAllIncoming = -1;

	private int behaviorHandleAnyThrough = -1;

	private int behaviorHandleToParent = -1;

	private String behaviorSort = null;

	private final ControlFieldset<?> fieldset;

	private final String icon;

	private final String key;

	private Set<String> respondable = null;

	private final Object title;

	private ControlFieldset<?> typeLoadFieldset = null;

	private final Type<?> typeDefault;

	private final ExecProcess typeContext;

	protected TypeInternal(final Type<?> typeDefault, final String key, final Object title, final String icon, final ControlFieldset<?> fieldset) {
		this.typeDefault = typeDefault;
		this.typeContext = Exec.createProcess(typeDefault == null
			? null
			: typeDefault.getTypeContext(), "TypeInternal Context: " + key);
		this.key = key;
		this.title = title;
		this.icon = icon;
		this.fieldset = fieldset;
	}

	@Override
	public Object getCommandAdditionalResult(final BaseEntry<?> entry, final ControlCommand<?> command, final BaseObject arguments) {
		
		
		return this.typeDefault.getCommandAdditionalResult(entry, command, arguments);
	}

	@Override
	public BaseObject getCommandAttributes(final String key) {
		
		
		return this.typeDefault.getCommandAttributes(key);
	}

	@Override
	public ControlFieldset<?> getCommandFieldset(final String key) {
		
		
		return this.typeDefault.getCommandFieldset(key);
	}

	@Override
	public ControlCommandset getCommandsAdditional(final BaseEntry<?> entry, final ControlCommandset target, final Set<String> include, final Set<String> exclude) {
		
		
		return this.typeDefault.getCommandsAdditional(entry, target, include, exclude);
	}

	@Override
	public Collection<String> getContentListingFields() {
		
		
		final Type<?> parentType = this.getParentType();
		return parentType == null
			? null
			: parentType.getContentListingFields();
	}

	@Override
	public boolean getDefaultFolder() {
		
		
		return Convert.MapEntry.toBoolean(this.getAttributes(), "folder", false);
	}

	@Override
	public int getDefaultState() {
		
		
		return ModuleInterface.STATE_DRAFT;
	}

	@Override
	public boolean getDefaultVersioning() {
		
		
		return false;
	}

	@Override
	public ControlFieldset<?> getFieldsetCreate() {
		
		
		return this.fieldset;
	}

	@Override
	public ControlFieldset<?> getFieldsetDelete() {
		
		
		return null;
	}

	@Override
	public ControlFieldset<?> getFieldsetLoad() {
		
		
		if (this.typeLoadFieldset == null) {
			synchronized (this) {
				if (this.typeLoadFieldset == null) {
					if (this.fieldset == null || this.fieldset.isEmpty()) {
						this.typeLoadFieldset = ControlFieldset.createFieldset();
					} else {
						this.typeLoadFieldset = this.fieldset;
					}
				}
			}
		}
		return this.typeLoadFieldset;
	}

	@Override
	public ControlFieldset<?> getFieldsetProperties() {
		
		
		return this.fieldset;
	}

	@Override
	public Set<String> getFieldsEvaluable() {
		
		
		return Collections.emptySet();
	}

	@Override
	public final Set<String> getFieldsPublic() {
		
		
		if (this.respondable == null) {
			synchronized (this) {
				if (this.respondable == null) {
					final Set<String> respondable = new HashSet<>();
					final ControlFieldset<?> fieldsetLoad = this.getFieldsetLoad();
					for (final Iterator<String> iterator = Base.keys(fieldsetLoad); iterator.hasNext();) {
						final String key = iterator.next();
						final ControlField field = fieldsetLoad.getField(key);
						if (Convert.MapEntry.toBoolean(field.getAttributes(), "respond", false)) {
							field.fillFields(respondable);
						}
					}
					if (respondable.isEmpty()) {
						this.respondable = TypeInternal.EMPTY_SET_STRING;
					} else //
					if (respondable.size() == 1) {
						this.respondable = Collections.singleton(respondable.iterator().next());
					} else {
						this.respondable = respondable;
					}
				}
			}
		}
		return this.respondable;
	}

	@Override
	public String getIcon() {
		
		
		return this.icon;
	}

	@Override
	public String getKey() {
		
		
		return this.key;
	}

	@Override
	public Type<?> getParentType() {
		
		
		return this.typeDefault;
	}

	@Override
	public Collection<String> getReplacements() {
		
		
		return null;
	}

	@Override
	public final BaseMessage getResource(final String key) {
		
		
		return null;
	}

	@Override
	public BaseObject getResponse(final ExecProcess process, final BaseEntry<?> entry, final BaseObject content) {
		
		
		return content;
	}

	@Override
	public String getTitle() {
		
		
		return String.valueOf(this.title);
	}

	@Override
	public boolean getTypeBehaviorAutoRecalculate() {
		
		
		if (this.behaviorAutoRecalculate == -1) {
			final boolean autoRecalculate = Convert.MapEntry.toBoolean(this.getAttributes(), "autotouch", false);
			this.behaviorAutoRecalculate = autoRecalculate
				? 1
				: 0;
			return autoRecalculate;
		}
		return this.behaviorAutoRecalculate != 0;
	}

	@Override
	public boolean getTypeBehaviorHandleAllIncoming() {
		
		
		if (this.behaviorHandleAllIncoming == -1) {
			final int handle = Convert.MapEntry.toInt(this.getAttributes(), "handle", TypeInternal.HT_NAMES, 0);
			this.behaviorHandleAllIncoming = handle > TypeInternal.HT_ANY
				? 1
				: 0;
			return handle > TypeInternal.HT_ANY;
		}
		return this.behaviorHandleAllIncoming != 0;
	}

	@Override
	public boolean getTypeBehaviorHandleAnyThrough() {
		
		
		if (this.behaviorHandleAnyThrough == -1) {
			final int handle = Convert.MapEntry.toInt(this.getAttributes(), "handle", TypeInternal.HT_NAMES, 0);
			this.behaviorHandleAnyThrough = handle >= TypeInternal.HT_ANY
				? 1
				: 0;
			return handle >= TypeInternal.HT_ANY;
		}
		return this.behaviorHandleAnyThrough != 0;
	}

	@Override
	public boolean getTypeBehaviorHandleToParent() {
		
		
		if (this.behaviorHandleToParent == -1) {
			final int handle = Convert.MapEntry.toInt(this.getAttributes(), "handle", TypeInternal.HT_NAMES, 0);
			this.behaviorHandleToParent = handle == TypeInternal.HT_PARENT
				? 1
				: 0;
			return handle == TypeInternal.HT_PARENT;
		}
		return this.behaviorHandleToParent != 0;
	}

	@Override
	public String getTypeBehaviorListingSort() {
		
		
		if (this.behaviorSort == null) {
			final String sort = Base.getString(this.getAttributes(), "sort", TypeInternal.NULL_SORT);
			this.behaviorSort = sort == TypeInternal.NULL_SORT || TypeInternal.NULL_SORT.equals(sort)
				? TypeInternal.NULL_SORT
				: sort;
		}
		return this.behaviorSort;
	}

	@Override
	public boolean getTypeBehaviorResponseFiltering() {
		
		
		return false;
	}

	@Override
	public ExecProcess getTypeContext() {
		
		
		return this.typeContext;
	}

	@Override
	public long getTypeModificationDate() {
		
		
		return 1088274636890L;
	}

	@Override
	public BaseObject getTypePrototypeObject() {
		
		
		return null;
	}

	@Override
	public Collection<String> getValidChildrenTypeNames() {
		
		
		return null;
	}

	@Override
	public Collection<String> getValidParentsTypeNames() {
		
		
		return null;
	}

	@Override
	public Collection<Integer> getValidStateList() {
		
		
		return null;
	}

	@Override
	public boolean hasDeletionForm() {
		
		
		return false;
	}

	@Override
	public final boolean isClientVisible() {
		
		
		return false;
	}

	@Override
	public final boolean isFinal() {
		
		
		return true;
	}

	@Override
	public boolean isInstance(final String typeName) {
		
		
		int counter = 64;
		Type<?> currentType = this;
		for (;;) {
			final String current = currentType.getKey();
			if (--counter < 0) {
				throw new RuntimeException("Type heirarchy recursion detected!");
			}
			if (current.equals(typeName)) {
				return true;
			}
			final Collection<String> replacements = currentType.getReplacements();
			if (replacements != null) {
				for (final String replacement : replacements) {
					if (replacement.equals(typeName)) {
						return true;
					}
				}
			}
			final Type<?> nextType = currentType.getParentType();
			if (nextType == null || currentType == nextType) {
				return false;
			}
			currentType = nextType;
		}
	}

	@Override
	public boolean isValidState(final Object state) {
		
		
		return true;
	}

	@Override
	public void scriptCommandFormPrepare(final String key, final BaseEntry<?> entry, final BaseObject parameters) {
		
		
		// empty
	}

	@Override
	public void scriptCommandFormSubmit(final String key, final BaseEntry<?> entry, final BaseObject parameters) {
		
		
		// empty
	}

	@Override
	public void scriptPrepareCreate(final BaseChange change, final BaseObject data) {
		
		
		// empty
	}

	@Override
	public void scriptPrepareDelete(final BaseEntry<?> entry, final BaseObject data) {
		
		
		// empty
	}

	@Override
	public void scriptPrepareModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		
		// empty
	}

	@Override
	public void scriptSubmitCreate(final BaseChange change, final BaseObject data) {
		
		
		change.getData().baseDefineImportAllEnumerable(data);
	}

	@Override
	public void scriptSubmitDelete(final BaseEntry<?> entry, final BaseObject data) {
		
		
		// empty
	}

	@Override
	public void scriptSubmitModify(final BaseEntry<?> entry, final BaseChange change, final BaseObject data) {
		
		
		change.getData().baseDefineImportAllEnumerable(data);
	}

	@Override
	protected String toStringDetails() {
		
		
		return this.getKey() + ", extends: " + this.getParentType();
	}

	@Override
	public void typeStart() {
		
		
		// ignore
	}

	@Override
	public void typeStop() {
		
		
		// ignore
	}
}
