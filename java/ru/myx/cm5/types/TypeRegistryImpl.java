package ru.myx.cm5.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ru.myx.ae1.know.Server;
import ru.myx.ae1.types.Type;
import ru.myx.ae1.types.TypeRegistry;
import ru.myx.ae1.types.Types;
import java.util.function.Function;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.flow.Flow;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.mime.MimeType;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.TreeLinkType;

final class TypeRegistryImpl implements TypeRegistry {

	private static final String $DEFAULT = "$default";

	private final String typeNameDefault;

	private final Map<String, Entry> files;

	private final Map<String, Type<?>> allReplacementsMap;

	private final Map<String, Type<?>> allTypesMap;

	private final Type<?> typeDefault;

	private final String[] allTypeNames;

	private final Collection<String> skipSearch = new HashSet<>();

	private final Server server;

	TypeRegistryImpl(final Server server, final String typeNameDefault, final Map<String, Entry> files) {
		this.server = server;
		this.typeNameDefault = typeNameDefault;
		this.files = files;
		this.allReplacementsMap = new HashMap<>();
		this.allTypesMap = new HashMap<>();
		final Collection<String> allTypeNames = Create.tempSet();
		final Collection<Type<?>> allTypes = new ArrayList<>();
		try {
			final Type<?> type = this.getType(TypeRegistryImpl.$DEFAULT);
			if (type != null) {
				allTypes.add(type);
				allTypeNames.add(TypeRegistryImpl.$DEFAULT);
			}
		} catch (final Throwable t) {
			Report.exception("STATIC/SCANNER", "While loading default type", t);
		}
		final Type<?> defaultCandidate = this.allTypesMap.get(TypeRegistryImpl.$DEFAULT);
		this.typeDefault = defaultCandidate == null
			? new TypeDefault(server.getRootContext())
			: defaultCandidate;
		if (files != null) {
			for (final Entry file : files.values()) {
				final String name = file.getKey().substring(0, file.getKey().length() - ".scheme".length());
				try {
					final Type<?> type = this.getType(name);
					if (type != null) {
						allTypes.add(type);
						allTypeNames.add(name);
					}
				} catch (final Throwable t) {
					Report.exception("STATIC/SCANNER", "While loading: " + name, t);
				}
			}
		}
		/**
		 * Now some default types
		 */
		for (final TypeInternal type : new TypeInternal[]{
				//
				new TypeInternalScript(this.typeDefault),
				//
				new TypeInternalDisabled("*", this.typeDefault),
				//
		}) {
			if (allTypeNames.add(type.getKey())) {
				allTypes.add(type);
			}
		}
		this.allTypeNames = allTypeNames.toArray(new String[allTypeNames.size()]);
	}

	@Override
	public final Type<?> getType(final String typeName) {

		if (typeName == null) {
			return this.typeDefault;
		}
		{
			final Type<?> result = this.allTypesMap.get(typeName);
			if (result != null) {
				return result;
			}
		}
		{
			this.skipSearch.add(typeName);
			final Entry file = this.files.get(typeName + ".scheme");
			if (file != null && file.isExist()) {
				final BaseMessage message = Flow.entry("REGISTRY_IMPL", typeName, new BaseNativeObject("Content-Type", MimeType.forEntry(file, "application/octet-stream")), file);
				final Entry resourceFolder = file.getParent().relative(typeName + ".resource", TreeLinkType.PUBLIC_TREE_REFERENCE);
				
				final Function<String, BaseMessage> resources = key -> {
					if (!resourceFolder.isExist()) {
						return null;
					}
					final Entry file1 = resourceFolder.relative(key, null);
					return file1 == null || !file1.isExist()
						? null
						: Flow.entry("TYPE(" + typeName + ')', "resource", new BaseNativeObject("Content-Type", MimeType.forEntry(file1, "application/octet-stream")), file1);
				};

				final Type<?> type = Types.materializeType(this.server, this, message, file.getLastModified(), typeName, this.typeDefault, resources);
				if (type != null) {
					this.allTypesMap.put(typeName, type);
					final Collection<String> replacements = type.getReplacements();
					if (replacements != null && !replacements.isEmpty()) {
						for (final String replacement : replacements) {
							this.allReplacementsMap.put(replacement, type);
						}
					}
					return type;
				}
			}
		}
		if (this.files != null) {
			for (final Entry file : this.files.values()) {
				final String name = file.getKey().substring(0, file.getKey().length() - ".scheme".length());
				if (this.skipSearch.contains(name)) {
					continue;
				}
				final Type<?> type = this.getType(name);
				if (type != null) {
					final Collection<String> replacements = type.getReplacements();
					if (replacements != null && replacements.contains(typeName)) {
						return type;
					}
				}
			}
		}
		return this.typeDefault;
	}

	@Override
	public final String getTypeNameDefault() {

		return this.typeNameDefault;
	}

	@Override
	public final String[] getTypeNames() {

		return this.allTypeNames;
	}

	@Override
	public void start() {

		// ignore
	}

	@Override
	public void stop() {

		// ignore
	}
}
