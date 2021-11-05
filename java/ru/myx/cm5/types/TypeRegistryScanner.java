package ru.myx.cm5.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ru.myx.ae1.know.Server;
import ru.myx.ae1.types.Type;
import ru.myx.ae1.types.TypeRegistry;
import ru.myx.ae3.act.Act;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseList;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Create;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.Storage;
import ru.myx.ae3.vfs.TreeReadType;
import ru.myx.jdbc.lock.Runner;

/**
 * @author myx
 */
public class TypeRegistryScanner implements Runner, Runnable, TypeRegistry {
	
	
	/**
	 * Scans all types
	 *
	 * @author myx
	 * 
	 */
	static final class CommonScanner implements Runnable {
		
		
		@Override
		public void run() {
			
			
			final Map<String, Entry> files = new TreeMap<>();
			{
				final Entry publicTypes = Storage.PUBLIC.relative("resources/type", null);
				TypeRegistryScanner.load(publicTypes, files);
			}
			{
				final Entry protectedTypes = Storage.PROTECTED.relative("resources/type", null);
				TypeRegistryScanner.load(protectedTypes, files);
			}
			{
				final Entry protectedTypes = Storage.SHARED.relative("resources/type", null);
				TypeRegistryScanner.load(protectedTypes, files);
			}
			{
				final Entry privateTypes = Storage.PRIVATE.relative("resources/type", null);
				TypeRegistryScanner.load(privateTypes, files);
			}
			TypeRegistryScanner.COMMON_FILES = files;
			Act.later(null, this, 15000L);
		}
	}

	static Map<String, Entry> COMMON_FILES;

	/**
	 * FIXME: common types should be compiled once, not for every server... then
	 * this compiled code should be put in separate type instances for each
	 * server.
	 */
	static final Runnable COMMON_SCANNER = new CommonScanner();

	static {
		TypeRegistryScanner.COMMON_SCANNER.run();
	}

	static void load(final Entry folder, final Map<String, Entry> files) {
		
		
		if (folder == null || !folder.isExist() || !folder.isContainer()) {
			return;
		}
		final BaseList<Entry> contents = folder.toContainer().getContentCollection(TreeReadType.ITERABLE).baseValue();
		for (final Entry file : contents) {
			if (file.isHidden()) {
				continue;
			}
			if (file.isBinary()) {
				if (file.getKey().endsWith(".scheme")) {
					if (file.toBinary().getBinaryContentLength() == 0) {
						files.remove(file.getKey());
					} else {
						files.put(file.getKey(), file);
					}
				}
			} else //
			if (file.isContainer()) {
				//
			}
		}
	}

	private final Map<String, Long> allDates = Create.tempMap();

	private final Map<String, Boolean> allRegistered = Create.tempMap();

	private Map<String, Type<?>> allReplacements = new HashMap<>();

	private Map<String, Type<?>> allTypes = Create.tempMap();

	private final Entry folder;

	private int scanIndex = 0;

	private long scanLastModified = -1;

	private final Server server;

	private boolean started = false;

	private Type<?> typeDefault = null;

	private final String typeNameDefault;

	/**
	 * @param typeNameDefault
	 * @param server
	 * @param folder
	 */
	public TypeRegistryScanner(final String typeNameDefault, final Server server, final Entry folder) {
		this.typeNameDefault = typeNameDefault;
		this.server = server;
		this.folder = folder;
	}

	@Override
	public final Type<?> getType(final String typeName) {
		
		
		if (typeName == null) {
			return this.typeDefault;
		}
		final Type<?> result = this.allReplacements.get(typeName);
		return result == null
			? this.typeDefault
			: result;
	}

	@Override
	public String getTypeNameDefault() {
		
		
		return this.typeNameDefault;
	}

	@Override
	public String[] getTypeNames() {
		
		
		final Map<String, Type<?>> staticTypes = this.allTypes;
		return staticTypes.keySet().toArray(new String[staticTypes.size()]);
	}

	@Override
	public int getVersion() {
		
		
		return 3;
	}

	@Override
	public void run() {
		
		
		try {
			if (++this.scanIndex % 3 == 1 || this.folder.isExist() && this.folder.getLastModified() != this.scanLastModified) {
				this.scanLastModified = this.folder.getLastModified();
				this.scan();
			}
		} catch (final Throwable t) {
			Report.exception("TYPE_REGISTRY_SCANNER", "Unhandled exception while scanning for changes", t);
		}
		if (this.started) {
			Act.later(null, this, 4000L);
		}
	}

	void scan() {
		
		
		final long startTime = System.currentTimeMillis();
		/**
		 * all files which are types %)
		 */
		final Map<String, Entry> files;
		{
			files = new TreeMap<>();
			/**
			 * all common type files
			 */
			files.putAll(TypeRegistryScanner.COMMON_FILES);
			/**
			 * our local type files (will replace common type files if there is
			 * name conflict)
			 */
			TypeRegistryScanner.load(this.folder, files);
		}
		/**
		 * check for early exit: nothing changed
		 */
		{
			/**
			 * adding all previous keys to a temporary set of keys.
			 */
			final Set<String> checkFiles = new TreeSet<>();
			checkFiles.addAll(this.allDates.keySet());
			{
				boolean changed = false;
				for (final Entry file : files.values()) {
					final long modified = file.getLastModified();
					final String name = file.getKey();
					checkFiles.remove(name);
					final Long date = this.allDates.get(name);
					if (date == null || date.longValue() != modified) {
						this.allDates.put(name, Long.valueOf(modified));
						changed = true;
					}
					Thread.yield();
				}
				/**
				 * early exit: nothing changed and previous key set is the same
				 * as new one
				 */
				if (!changed && checkFiles.isEmpty()) {
					return;
				}
			}
			/**
			 * this set contains keys for removed items...
			 *
			 * ...so we are going to remove them for next check
			 *
			 * if any, of course
			 */
			for (final String name : checkFiles) {
				this.allDates.remove(name);
			}
		}
		/**
		 * Now we are going to create and initialize new actual TypeRegistry to
		 * reflect new set of types.
		 */
		final TypeRegistryImpl registry = new TypeRegistryImpl(this.server, this.typeNameDefault, files);
		final Map<String, Type<?>> allReplacements = new HashMap<>();
		final Map<String, Type<?>> allTypes = new TreeMap<>();
		for (final String name : registry.getTypeNames()) {
			final Type<?> type = registry.getType(name);
			allTypes.put(name, type);
			final Collection<String> replacements = type.getReplacements();
			if (replacements != null && !replacements.isEmpty()) {
				for (final String replacement : replacements) {
					allReplacements.put(replacement, type);
				}
			}
		}
		/**
		 * Original names are more important than replacements
		 */
		allReplacements.putAll(allTypes);
		/**
		 * Update types in the server's root context
		 */
		{
			final Set<String> checkTypes = new TreeSet<>();
			checkTypes.addAll(this.allRegistered.keySet());
			{
				final BaseObject global = this.server.getRootContext().ri10GV;

				assert global == Context.getServer(Exec.currentProcess()).getRootContext().ri10GV //
				: "Current server is not typeRegistry's server: current=" + Context.getServer(Exec.currentProcess()) + ", expected=" + this.server;

				for (final String name : allReplacements.keySet()) {
					checkTypes.remove(name);
					final Type<?> type = allReplacements.get(name);
					final BaseProperty property = global.baseGetOwnProperty(name);
					final boolean success = property == null
						? global.baseDefine(name, type, BaseProperty.ATTRS_MASK_NEN)
						: property.propertySet(global, name, type, BaseProperty.ATTRS_MASK_NEN);
					this.allRegistered.put(name, Boolean.TRUE);
					if (!success) {
						Report.warning("TYPEREGISTRY", "Registered unsuccessfully: name=" + name);
					} else {
						assert global.baseGet(name, BaseObject.UNDEFINED) == type //
						: "Context property set, but unequal on read, name=" + name;
					}
				}
				/**
				 * Remove some server's root context properties for deleted
				 * types
				 *
				 * if any, of course
				 */
				for (final String name : checkTypes) {
					final boolean success;
					{
						final BaseProperty property = global.baseGetOwnProperty(name);
						success = property != null
							? global.baseDelete(name)
							: true;
						this.allRegistered.remove(name);
					}
					if (!success) {
						Report.warning("TYPEREGISTRY", "Error removing type from server root context: name=" + name);
					}
				}
			}
		}
		final Map<String, Type<?>> previous = this.allTypes;
		this.allTypes = allTypes;
		this.allReplacements = allReplacements;
		this.typeDefault = registry.getType(null);
		for (final Type<?> type : previous.values()) {
			type.typeStop();
		}
		for (final Type<?> type : allTypes.values()) {
			type.typeStart();
		}
		final long took = System.currentTimeMillis() - startTime;
		Report.info("TYPEREGISTRY", "Complete type load/init, took=" + Format.Compact.toPeriod(took));
	}

	@Override
	public void start() {
		
		
		if (!this.started) {
			synchronized (this) {
				if (!this.started) {
					this.scan();
					Act.later(null, this, 15000L);
					this.started = true;
				}
			}
		}
	}

	@Override
	public void stop() {
		
		
		if (this.started) {
			this.started = false;
		}
	}
}
