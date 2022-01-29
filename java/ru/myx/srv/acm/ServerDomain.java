/*
 * Created on 20.10.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.srv.acm;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import ru.myx.ae1.AbstractPluginInstance;
import ru.myx.ae1.BaseRT3;
import ru.myx.ae1.PluginInstance;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.handle.Handle;
import ru.myx.ae1.know.AbstractZoneServer;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.sharing.AccessType;
import ru.myx.ae1.sharing.SecureType;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae1.types.TypeRegistry;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Act;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.auth.InvalidCredentials;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseMap;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.control.ControlActor;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecNonMaskedException;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.flow.ObjectTarget;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.SkinScanner;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.Storage;
import ru.myx.ae3.vfs.TreeLinkType;
import ru.myx.ae3.vfs.filesystem.StorageImplFilesystem;
import ru.myx.ae3.vfs.signals.StorageImplSignals;
import ru.myx.cm5.control.CommonActorProvider;
import ru.myx.cm5.control.NodeSiteConfig;
import ru.myx.cm5.control.personal.ActorUserPersonal;
import ru.myx.cm5.control.personal.NodePersonal;
import ru.myx.cm5.control.sharing.Sharing;
import ru.myx.cm5.control.um.LookupCommonUserFields;
import ru.myx.cm5.control.um.NodeUM;
import ru.myx.cm5.skin.ActorSkinPersonal;
import ru.myx.cm5.types.TypeRegistryScanner;
import ru.myx.sapi.RuntimeEnvironment;

/** @author myx */
public class ServerDomain extends AbstractZoneServer implements ServerRT3 {

	private static final Share<?>[] EMPTY_SHARE_ARRAY = new Share[0];

	private static final Handler HANDLER_SHARE_DEAD = new HandlerShareReload();

	private static final Map<String, PluginInstance> PUBLIC_PLUGIN_EXPORT = new ConcurrentHashMap<>();

	private static final QuickCommandActor QUICK_ACTOR = new QuickCommandActor();

	private static final ReplyAnswer ROBOTS_PRIVATE = Reply.string("SRVDOMAIN", null, "User-Agent: *\nDisallow: /");

	private static final ReplyAnswer ROBOTS_PUBLIC = Reply.string("SRVDOMAIN", null, "User-Agent: *\nAllow: /");

	/** never unloads anyway */
	private static final BaseObject KNOWN_JAILS;

	/** for public access (used in ru.myx.farm for example) */
	public static final BaseObject KNOWN_JAILS_SEALED;

	static {
		KNOWN_JAILS = new BaseNativeObject();
		KNOWN_JAILS_SEALED = Base.seal(ServerDomain.KNOWN_JAILS);
	}

	/** FIXME robots already handled - do something useful, ie: webdav support, URL session id,
	 * etc...
	 *
	 * @param query
	 * @param accessType
	 * @return */
	final static ReplyAnswer prepareRequest(final ServeRequest query, final AccessType accessType) {

		final String identifier = query.getResourceIdentifier();
		if (identifier.length() == 11) {
			if ("/robots.txt".equals(identifier)) {
				if (accessType == AccessType.PUBLIC) {
					return ServerDomain.ROBOTS_PUBLIC.nextClone(query);
				}
				return ServerDomain.ROBOTS_PRIVATE.nextClone(query);
			}
		}
		return null;
	}

	private final String[] allAliases;

	private final String[] allDomains;

	private final String[] allExcludeAliases;

	private final String[] allExcludeDomains;

	/**
	 *
	 */
	protected final BaseMap config = new BaseNativeObject();

	/**
	 *
	 */
	protected final SqlManager connections;

	/**
	 *
	 */
	protected final String entrance;

	private final Map<String, Boolean> initializedTargets = new ConcurrentHashMap<>();

	/**
	 *
	 */
	protected String languageDefault = "en";

	/**
	 *
	 */
	protected String[] languages = {
			"en"
	};

	private final Function<PluginInstance, Object> pluginRegistrar = new Function<>() {

		@Override
		public Object apply(final PluginInstance plugin) {

			plugin.register();
			ServerDomain.this.plugins.add(plugin);
			return null;
		}
	};

	/**
	 *
	 */
	protected final List<PluginInstance> plugins = BaseObject.createArray();

	/**
	 *
	 */
	protected final RuntimeEnvironment rt;

	private final AbstractPluginInstance serverPlugin;

	private Share<?>[] shareAll = ServerDomain.EMPTY_SHARE_ARRAY;

	private Share<?>[] shareLocation = ServerDomain.EMPTY_SHARE_ARRAY;

	private Map<String, Share<?>> shareMap = new HashMap<>();

	private final Map<String, Skinner> skinners = new TreeMap<>();

	private final SkinScanner skins;

	private final String splashName;

	private boolean started = false;

	private final TypeRegistry types;

	ServerDomain(final String id, final BaseObject attributes) {

		super(id, Base.getString(attributes, "domain", id), Exec.currentProcess());
		ServerDomain.KNOWN_JAILS.baseDefine(this.zoneId, Base.forUnknown(this));
		{
			final String splashName = Base.getString(attributes, "splash", "").trim();
			this.splashName = splashName.length() == 0
				? null
				: splashName;
		}
		{
			final Set<String> domainList = new TreeSet<>();
			final Set<String> aliasList = new TreeSet<>();
			final Set<String> excludeDomainList = new TreeSet<>();
			final Set<String> excludeAliasList = new TreeSet<>();
			domainList.add(this.domainId);
			if (Engine.HOST_NAME != null) {
				domainList.add(id + "." + Engine.HOST_NAME);
				domainList.add(this.domainId + "." + Engine.HOST_NAME);
			}
			for (final StringTokenizer st = new StringTokenizer(Base.getString(attributes, "aliases", ""), ",;"); st.hasMoreTokens();) {
				final String current = st.nextToken().trim();
				if (current.startsWith("*.")) {
					domainList.add(current.substring(2));
				} else {
					aliasList.add(current);
				}
			}
			for (final StringTokenizer st = new StringTokenizer(Base.getString(attributes, "exclude", ""), ",;"); st.hasMoreTokens();) {
				final String current = st.nextToken().trim();
				if (current.startsWith("*.")) {
					excludeDomainList.add(current.substring(2));
				} else {
					excludeAliasList.add(current);
				}
			}
			aliasList.removeAll(excludeAliasList);
			domainList.removeAll(excludeDomainList);
			this.allDomains = domainList.toArray(new String[domainList.size()]);
			this.allAliases = aliasList.toArray(new String[aliasList.size()]);
			this.allExcludeDomains = excludeDomainList.toArray(new String[excludeDomainList.size()]);
			this.allExcludeAliases = excludeAliasList.toArray(new String[excludeAliasList.size()]);
		}
		this.connections = new SqlManager();
		{
			final String path = Base.getString(attributes, "path", "").trim();
			this.systemRoot = path.length() == 0
				? new File(new File(System.getProperty("serve.root")), this.getZoneId())
				: new File(path);
		}
		final File folder = this.systemRoot;
		{
			Report.event("RT3/HOST", "INIT", "Root folder is: " + folder.getAbsolutePath());
			if (!folder.exists()) {
				Report.error("RT3/HOST", "ROOT FOLDER DOESN'T EXIST, trying to create...");
				if (!folder.mkdirs()) {
					Report.error("RT3/HOST", "CANNOT CREATE ROOT FOLDER!");
				}
			}
		}
		{
			final String entranceCandidate = Base.getString(attributes, "entrance", "http://" + this.getDomainId());
			this.entrance = entranceCandidate.endsWith("/")
				? entranceCandidate.substring(0, entranceCandidate.length() - 1)
				: entranceCandidate;
		}
		final Properties properties = this.getProperties();
		properties.put("entrance", this.entrance);
		this.registerCommonActor(new CommonActorProvider(this));

		this.rt = new RuntimeEnvironment(this);
		BaseRT3.setRuntime(this.getRootContext(), this.rt);
		this.rt.registerPersonalActor(new ActorUserPersonal());
		this.rt.registerPersonalActor(new ActorSkinPersonal());

		this.registerCommonPermission(
				new SimplePermission(
						"$modify_sharing", //
						MultivariantString.getString(
								"View/Modify public share settings", //
								Collections.singletonMap(
										"ru", //
										"Просматривать/Редактировать настройки публичной точки доступа") //
						),
						true));
		this.lookups.baseDefine("System.CommonUserFields", new LookupCommonUserFields());
		{
			final ControlNode<?> controlRoot = this.getControlRoot();
			controlRoot.bind(new NodeSiteConfig(this));
			controlRoot.bind(new NodePersonal());
			controlRoot.bind(new NodeUM());
		}
		this.serverPlugin = new AbstractPluginInstance() {
			// empty
		};
		{
			final Properties serverPluginProperties = new Properties();
			serverPluginProperties.setProperty("id", "$$self");
			this.serverPlugin.setup(this, serverPluginProperties);
		}

		final Entry root = this.getVfsRootEntry();
		final Entry folderVfs;
		{
			assert root != null : "Root entry shouldn't be null!";
			final Map<String, Function<Void, Object>> signals = this.registrySignals();
			if (this.isControllerServer()) {
				{
					final SignalGarbageCollect signal = new SignalGarbageCollect();
					signals.put(signal.getKey(), signal);
				}
				{
					final SignalRestartServer signal = new SignalRestartServer();
					signals.put(signal.getKey(), signal);
				}

				final Entry acm = root.relativeFolderEnsure("acm");
				assert acm != null : "Should not return NULL when mode is not NULL";
				/** public is already there */
				// Storage.mount( acm, "public",
				// TreeLinkType.PUBLIC_TREE_REFERENCE, Storage
				// .createRoot( new StorageImplFilesystem( Engine.PATH_PUBLIC,
				// true ) ) );
				Storage.mount(acm, "protected", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(Engine.PATH_PROTECTED, false)));
				Storage.mount(acm, "private", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(Engine.PATH_PRIVATE, false)));
				Storage.mount(acm, "shared", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(Engine.PATH_SHARED, false)));
				Storage.mount(acm, "cache", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(Engine.PATH_CACHE, false)));
				Storage.mount(acm, "logs", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(Engine.PATH_LOGS, false)));
				Storage.mount(acm, "temp", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(Engine.PATH_TEMP, false)));
				Storage.mount(acm, "vfs", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.getRoot(Exec.getRootProcess()));
			}
			{
				final Entry backup = Storage.SHARED.relative("backup/" + id, null);
				if (backup != null && backup.isContainer()) {
					final Entry mount = root.relativeFolderEnsure("backup");
					Storage.mount(mount, id, TreeLinkType.PUBLIC_TREE_REFERENCE, backup);
				}
			}
			{
				assert signals != null : "Expected not to be null";
				Storage.mount(root, "ctrl", TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplSignals(signals)));
			}
			{
				final Entry docs = root.relativeFolderEnsure("docs");
				Storage.mount(docs, "acm.cms-ru.pdf", TreeLinkType.PUBLIC_TREE_REFERENCE, root.relative("acm/public/resources/doc/acm.cms-ru.pdf", null));
				final Entry example = docs.relativeFolderEnsure("example");
				Storage.mount(
						example,
						"SchemeDescribedEnglish.scheme",
						TreeLinkType.PUBLIC_TREE_REFERENCE,
						root.relative("acm/public/resources/doc/example/SchemeDescribedEnglish.scheme", null));
				Storage.mount(example, "skins", TreeLinkType.PUBLIC_TREE_REFERENCE, root.relative("acm/public/resources/skin", null));
				Storage.mount(example, "types", TreeLinkType.PUBLIC_TREE_REFERENCE, root.relative("acm/public/resources/type", null));
			}
			{
				final Entry logs = root.relativeFolderEnsure("logs");
				Storage.mount(
						logs,
						id + "-audit",
						TreeLinkType.PUBLIC_TREE_REFERENCE,
						Storage.createRoot(new StorageImplFilesystem(new File(Engine.PATH_LOGS, id + "-audit"), false)));
				Storage.mount(
						logs,
						id + "-log",
						TreeLinkType.PUBLIC_TREE_REFERENCE,
						Storage.createRoot(new StorageImplFilesystem(new File(Engine.PATH_LOGS, id + "-log"), false)));
			}
		}

		this.skinners.put("xml", Skinner.NUL_SKINNER);
		// this.skinners.put( "layouts", new SkinnerLayout() );
		this.skinners.put("WebDAV", Produce.object(Skinner.class, "DAV", null, root));

		{
			final Entry site = root.relativeFolderEnsure("site");
			folderVfs = Storage.mount(site, id, TreeLinkType.PUBLIC_TREE_REFERENCE, Storage.createRoot(new StorageImplFilesystem(folder, false)));
		}
		{
			final Entry siteTypes = folderVfs.relativeFolderEnsure("types");
			final Entry siteSkin = folderVfs.relativeFolderEnsure("skin");
			/* final Entry siteLibs = */folderVfs.relativeFolder("lib");
			this.types = new TypeRegistryScanner("*", this, siteTypes);
			this.skins = new SkinScanner(siteSkin, this.skinners);
			this.getRootContext().baseDefine("$skins", this.skins, BaseProperty.ATTRS_MASK_NNN);
		}

		{
			try {
				Act.run(this.getRootContext(), TaskServerInitializer.INSTANCE, this);
			} catch (final Throwable t) {
				Report.exception("DOMAIN-START", "While starting a domain", t);
			}
		}
	}

	@Override
	public final boolean absorb(final ServeRequest query) {

		if (query == Sharing.SHARE_RELOAD) {
			this.setShares(Sharing.getSharings(this));
			return true;
		}
		{
			final String target = query.getTarget();
			final ObjectTarget<ServeRequest> server;
			server : try {
				if (this.initializedTargets.containsKey(target)) {
					server = Handle.getServer(target);
					break server;
				}
				server = this.absorbChooseServer(target);
				if (Report.MODE_DEBUG) {
					Report.debug("RT3/HOST", "absorbChooseServer: chosen, Domain: " + this + ", Query:" + query + ", server: " + server);
				}
			} catch (final Throwable e) {
				if (Report.MODE_DEBUG) {
					Report.debug("RT3/HOST", "absorbChooseServer: failed, Domain: " + this + ", Query:" + query + ", e: " + Format.Throwable.toText(e));
				}
				throw e;
			}
			return server != null
				? server.absorb(query)
				: false;
		}
	}

	/** @param target
	 * @return server */
	private ObjectTarget<ServeRequest> absorbChooseServer(final String target) {

		final Server server;
		synchronized (this) {
			if (this.initializedTargets.containsKey(target)) {
				return Handle.getServer(target);
			}
			final Share<?> share = this.shareMap.get(target);
			final Share<?>[] variants;
			final Share<?>[] nearest;
			if (share == null) {
				variants = null;
				nearest = null;
			} else {
				List<Share<?>> variantsList = null;
				Map<String, Share<?>> nearestMap = null;
				final Share<?>[] shareLocation = this.shareLocation;
				if (shareLocation != null) {
					for (int i = shareLocation.length - 1; i >= 0; --i) {
						final Share<?> candidate = shareLocation[i];
						final String path = candidate.getPath();
						if (share != candidate && path.length() > share.getPath().length() && path.startsWith(share.getPath())) {
							if (nearestMap == null) {
								nearestMap = new TreeMap<>();
							}
							final String key = candidate.getLanguageName() + "#" + candidate.getPath();
							final Object previous = nearestMap.get(key);
							if (previous == null || ((Share<?>) previous).getAlias().length() > candidate.getAlias().length()) {
								nearestMap.put(key, candidate);
							}
						}
						if (share != candidate && candidate.getLanguageMode() != Share.LM_NONE && path.equals(share.getPath())) {
							if (variantsList == null) {
								variantsList = new ArrayList<>();
							}
							variantsList.add(candidate);
						}
					}
					if (nearestMap == null) {
						nearest = null;
					} else {
						final Collection<Share<?>> nearestList = nearestMap.values();
						nearest = nearestList.toArray(new Share[nearestList.size()]);
						Arrays.sort(nearest, Share.COMPARATOR_SHARE);
					}
					variants = variantsList == null
						? null
						: variantsList.toArray(new Share[variantsList.size()]);
				} else {
					variants = null;
					nearest = null;
				}
			}
			if (share == null) {
				if (!this.checkAllowedShare(target)) {
					return null;
				}
				server = new ServerUnknown(this, this.entrance, target, nearest);
			} else {
				final String path = share.getPath();
				final ControlNode<?> controlRoot = this.getControlRoot();
				assert controlRoot != null : "ControlRoot is NULL, domain=" + this + ", class=" + this.getClass().getName();
				final ControlNode<?> handlerProvider = Control.relativeNode(controlRoot, path);
				if (handlerProvider == null) {
					assert false : "Handler provider is NULL, domain=" + this + ", path=" + path + ", share=" + share;
					server = new ServerUnknown(this, this.entrance, target, nearest);
				} else {
					final Skinner skinner = this.getSkinner(share.getSkinner());
					final Handler handlerSubstitute = handlerProvider.substituteHandler();
					final Handler handlerToUse = handlerSubstitute == null
						? ServerDomain.HANDLER_SHARE_DEAD
						: handlerSubstitute;
					server = new ServerShare(
							this,
							skinner,
							handlerProvider,
							handlerToUse,
							target,
							path,
							share.getAuthType(),
							share.getAccessType(),
							share.getSecureType(),
							share.getLanguageMode(),
							share.getLanguageName(),
							this.splashName,
							variants,
							nearest);
				}
			}
			Handle.registerServer(target, server);
			this.initializedTargets.put(target, Boolean.TRUE);
			Report.event("RT3/HOST", "SHARE_ACTIVATE", "share=" + target + ", server=" + server);
		}
		return server;
	}

	/** @param plugin
	 * @param info
	 */
	protected void addPlugin(final PluginInstance plugin, final BaseObject info) {

		final Properties props = new Properties();
		for (final Iterator<String> iterator = Base.keys(info); iterator.hasNext();) {
			final String key = iterator.next();
			props.setProperty(key, Base.getString(info, key, ""));
		}
		plugin.setup(this, props);
		try {
			Act.run(this.getRootContext(), this.pluginRegistrar, plugin);
		} catch (final ExecNonMaskedException e) {
			throw e;
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} catch (final Throwable e) {
			throw e;
		}
		final String public_export = Base.getString(info, "public_export", "");
		if (public_export.length() > 0) {
			ServerDomain.PUBLIC_PLUGIN_EXPORT.put(this.getZoneId() + ':' + public_export, plugin);
		}
	}

	/** ru.myx.farm/dns
	 *
	 * @param hostname
	 * @return */
	public boolean checkAllowedShare(final String hostname) {

		if (hostname == null) {
			return false;
		}
		for (final String alias : this.allExcludeAliases) {
			if (alias.equals(hostname)) {
				return false;
			}
		}
		for (final String domain : this.allExcludeDomains) {
			if (domain.equals(hostname) || hostname.endsWith('.' + domain)) {
				return false;
			}
		}
		if (this.isControllerServer()) {
			for (final String alias : this.allAliases) {
				if (alias.equals("*")) {
					return true;
				}
			}
		}
		for (final String alias : this.allAliases) {
			if (alias.equals(hostname)) {
				return true;
			}
		}
		for (final String domain : this.allDomains) {
			if (domain.equals(hostname) || hostname.endsWith('.' + domain)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public AccessUser<?> ensureAuthorization(final int level) {

		if (level != AuthLevels.AL_AUTHORIZED_NORMAL && level != AuthLevels.AL_AUTHORIZED_NORMAL_LOCAL && level != AuthLevels.AL_AUTHORIZED_3RDPARTY) {
			return super.ensureAuthorization(level);
		}
		final ExecProcess process = Exec.currentProcess();
		final Context context = Context.getContext(process);
		if (context.getSessionState() >= level) {
			return context.getUser();
		}
		final ServeRequest query = context.getRequest();
		boolean failure = false;
		boolean success = false;
		try {
			if (context.checkAuthorization(level)) {
				success = true;
			}
		} catch (final AbstractReplyException e) {
			if (e.getCode() == Reply.CD_UNAUTHORIZED) {
				/**
				 *
				 */
				failure = true;
			} else {
				/** any non login request response must be forwarded. Including CD_DENIED. */
				throw e;
			}
		} catch (final InvalidCredentials e) {
			/**
			 *
			 */
			failure = true;
		}

		final BaseObject session = context.getSessionData();
		final BaseObject error;

		if (failure) {
			error = Base.forString(NodeUM.getMessageLoginError());
			session.baseDefine("loginError", NodeUM.getMessageLoginError());
		} else {
			error = null;
			session.baseDelete("loginError");
		}

		final Server server = context.getServer();
		String back = null;
		/** If not on login page already */
		if (query.getUrl().indexOf("/login.user") == -1) {
			back = query.getUrl();
			final String value = back;
			session.baseDefine("loginUrl", value);
			final String url = server//
					.fixUrl(
							query.getUrlBase() + "/login.user?tp=rt3&back="
									+ Text.encodeUriComponent(back.substring(query.getProtocolName().length() + 3 + query.getTargetExact().length()), StandardCharsets.UTF_8));
			/** Redirecting to login page */
			throw Reply.exception(
					Reply.redirect(
							"AE1.CONTEXT", //
							query,
							false,
							url)//
							.setNoCaching()//
							.setPrivate());
		}
		{
			final BaseObject parameters = query.getParameters();
			if (parameters != null) {
				back = Base.getString(parameters, "back", null);
			}
		}
		if (back == null || back.startsWith("login.user")) {
			back = Base.getString(session, "loginUrl", null);
			if (back == null || back.startsWith("login.user")) {
				back = "/";
			}
		}
		if (success) {
			throw Reply.exception(
					Reply.redirect("AUTH", query, false, server.fixUrl(back))//
							.setNoCaching()//
							.setPrivate());
		}
		{
			final ReplyAnswer response = Reply.object(
					"AUTH", //
					query,
					new BaseNativeObject()//
							.putAppend("template", "401")//
							.putAppend("back", back)//
							.putAppend(
									"error",
									error == null
										? BaseObject.UNDEFINED
										: error)//
							.putAppend(
									"body",
									error == null
										? Base.forString("Please authenticate")
										: error)//
			)
					/** NO! This code is executed only for site form authentication.
					 *
					 * We do need code 200 here */
					// .setCode( Reply.CD_UNAUTHORIZED )//
					.setCode(Reply.CD_OK)//
					.setTitle("Authentication")//
					.setContentID("ru.myx.srv.acm.ServerDomain")//
			;
			throw Reply.exception(
					response//
							.setNoCaching()//
							.setPrivate()//
			);
		}
	}

	/** @param shares
	 * @return shares */
	@Override
	public final Share<?>[] filterAllowedShares(final Share<?>[] shares) {

		if (shares == null) {
			return null;
		}
		final Set<Share<?>> shareLocationActive = new TreeSet<>(Share.COMPARATOR_SHARE);
		for (int i = shares.length - 1; i >= 0; --i) {
			final String alias = shares[i].getAlias();
			if (this.checkAllowedShare(alias)) {
				shareLocationActive.add(shares[i]);
			}
		}
		final Share<?>[] shareLocation = shareLocationActive.toArray(new Share[shareLocationActive.size()]);
		// ensure? aren't they really sorted already by set?
		Arrays.sort(shareLocation, Share.COMPARATOR_SHARE);
		return shareLocation;
	}

	@Override
	public String fixLocation(final ExecProcess ctx, final String path, final boolean absolute) {

		if (!absolute) {
			for (final Share<?> share : this.shareLocation) {
				final String check = share.getPath() + '/';
				if (path.startsWith(check)) {
					final String localPath = path.substring(check.length());
					if (localPath.startsWith("/")) {
						return (share.getSecureType() == SecureType.REQUIRED
							? "https://"
							: "http://") + share.getAlias() + localPath;
					}
					return (share.getSecureType() == SecureType.REQUIRED
						? "https://"
						: "http://") + share.getAlias() + '/' + localPath;
				}
				if (check.equals(path + '/')) {
					return (share.getSecureType() == SecureType.REQUIRED
						? "https://"
						: "http://") + share.getAlias();
				}
			}
		}
		{
			for (final Share<?> share : this.shareAll) {
				final String check = share.getPath() + '/';
				if (path.startsWith(check)) {
					final String localPath = path.substring(check.length());
					if (localPath.startsWith("/")) {
						return (share.getSecureType() == SecureType.REQUIRED
							? "https://"
							: "http://") + share.getAlias() + localPath;
					}
					return (share.getSecureType() == SecureType.REQUIRED
						? "https://"
						: "http://") + share.getAlias() + '/' + localPath;
				}
				if (check.equals(path + '/')) {
					return (share.getSecureType() == SecureType.REQUIRED
						? "https://"
						: "http://") + share.getAlias();
				}
			}
		}
		Report.warning("SERVER_DOMAIN", "cannot find a suitable share for: " + path);
		return "#";
	}

	@Override
	public String fixUrl(final String url) {

		if (url == null) {
			return Context.getRequest(Exec.currentProcess()).getUrlBase();
		}
		if (url.startsWith("/")) {
			final ExecProcess process = Exec.currentProcess();
			final String base = Context.getRequest(process).getUrlBase();
			final String language = Context.getLanguage(process).getName();
			if (this.getLanguageDefault().equals(language)) {
				if (url.startsWith("/intl/")) {
					if (base.endsWith("/")) {
						return base + url.substring(9);
					}
					return base + url.substring(8);
				}
				if (base.endsWith("/")) {
					return base + url.substring(1);
				}
				return base + url;
			}
			if (base.endsWith("/")) {
				return url.startsWith("/intl/")
					? base + url.substring(1)
					: base + "intl/" + language + url;
			}
			return url.startsWith("/intl/")
				? base + url
				: base + "/intl/" + language + url;
		}
		return url;
	}

	@Override
	public String fixUrl(String url, final String language) {

		if (url == null) {
			url = "/";
		}
		if (url.startsWith("/")) {
			if (url.startsWith("/intl/")) {
				url = url.substring(8);
			}
			String currentBase = Context.getRequest(Exec.currentProcess()).getUrlBase();
			if (!this.getLanguageDefault().equals(language)) {
				currentBase += (currentBase.endsWith("/")
					? "intl/"
					: "/intl/") + language;
			}
			url = currentBase + url;
		}
		return url;
	}

	@Override
	public Map<String, Enumeration<Connection>> getConnections() {

		return this.connections;
	}

	/** @param shareHostName
	 * @return node */
	@Override
	public ControlNode<?> getControlNodeForShare(final String shareHostName) {

		final Share<?> share = this.shareMap.get(shareHostName);
		if (share == null) {
			return null;
		}
		return Control.relativeNode(this.getControlRoot(), share.getPath());
	}

	@Override
	public ControlActor<?> getControlQuickActor() {

		return ServerDomain.QUICK_ACTOR;
	}

	@Override
	public String[] getControlSharePoints() {

		return Sharing.getSharePoints(this);
	}

	@Override
	public String getLanguageDefault() {

		return this.languageDefault;
	}

	@Override
	public String[] getLanguages() {

		return this.languages;
	}

	@Override
	public Connection getServerConnection(final String alias) {

		return this.connections.getConnection(alias);
	}

	Collection<Share<?>> getShareAll() {

		return Collections.unmodifiableList(Arrays.asList(this.shareAll));
	}

	@Override
	public Share<?>[] getSharings() {

		return Sharing.getSharings(this);
	}

	@Override
	public final Skinner getSkinner(final String name) {

		if (name == null) {
			return this.getSkinner("");
		}
		{
			final Skinner skinner = this.skinners.get(name);
			if (skinner != null) {
				return skinner;
			}
		}
		{
			final Skinner skinner = SkinScanner.getSystemSkinner(name);
			if (skinner != null) {
				return skinner;
			}
		}
		{
			final Skinner defaultSkinner = this.skinners.get("");
			if (defaultSkinner == null) {
				return Skinner.NUL_SKINNER;
			}
			return defaultSkinner;
		}
	}

	@Override
	public final Collection<String> getSkinnerNames() {

		final Set<String> names = new TreeSet<>();
		names.addAll(this.skinners.keySet());
		SkinScanner.getSystemSkinnerNames(names);
		return names;
	}

	SkinScanner getSkins() {

		return this.skins;
	}

	@Override
	public TypeRegistry getTypes() {

		return this.types;
	}

	@Override
	public boolean isControllerServer() {

		return false;
	}

	@Override
	public void logQuickTaskUsage(final String task, final BaseObject arguments) {

		NodePersonal.tasksLog(task, arguments);
	}

	@Override
	public final Skinner registerSkinner(final String name, final Skinner skinner) {

		if (skinner == null) {
			return this.skinners.remove(name);
		}
		return this.skinners.put(name, skinner);
	}

	@Override
	public String registerUser(final String guid, final String login, final String lowerCase, final String passwordToUse, final BaseObject data) throws Exception {

		return NodeUM.registerUser(guid, login, lowerCase, passwordToUse, data);
	}

	final void setShares(final Share<?>[] shares) {

		final Map<String, Share<?>> shareMap;
		final Share<?>[] shareLocation;
		if (shares == null) {
			shareMap = new HashMap<>();
			shareLocation = ServerDomain.EMPTY_SHARE_ARRAY;
		} else {
			shareMap = new HashMap<>();
			Arrays.sort(shares, Share.COMPARATOR_SHARE);
			for (final Share<?> share : shares) {
				shareMap.put(share.getKey(), share);
			}
			shareLocation = this.filterAllowedShares(shares);
		}
		Report.event("RT3/HOST", "SHARE_RELOAD", "share list = " + shareMap.keySet());
		synchronized (this) {
			this.shareMap = shareMap;
			this.shareAll = shares;
			this.shareLocation = shareLocation;
			for (final String target : this.initializedTargets.keySet()) {
				Handle.registerServer(target, this);
			}
			this.initializedTargets.clear();
		}
	}

	/**
	 *
	 */
	protected void start() {

		if (!this.started) {
			this.started = true;
			try {
				Act.run(this.getRootContext(), TaskServerStarter.INSTANCE, this);
			} catch (final Throwable t) {
				Report.exception("DOMAIN-START", "While starting a domain", t);
				this.stop();
			}
		}
	}

	/**
	 *
	 */
	protected void stop() {

		if (this.started) {
			this.started = false;
			this.types.stop();
			for (final PluginInstance plugin : this.plugins) {
				plugin.destroy();
			}
			this.connections.shutDown();
		}
	}

	@Override
	public String toString() {

		return "DOMAIN( " + this.getZoneId() + " )";
	}
}
