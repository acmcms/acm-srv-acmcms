/*
 * Created on 17.06.2004
 */
package ru.myx.srv.acm;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.know.FilterZoneServer;
import ru.myx.ae1.sharing.AccessType;
import ru.myx.ae1.sharing.AuthType;
import ru.myx.ae1.sharing.SecureType;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.auth.InvalidCredentials;
import ru.myx.ae3.auth.LoginCheckContextBean;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.base.BaseProperty;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferDescription;
import ru.myx.ae3.eval.Evaluate;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecArgumentsEmpty;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ResultHandler;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.i3.web.WebInterface;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.Serve;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.cm5.control.sharing.Sharing;

/**
 * @author myx
 *
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class ServerShare extends FilterZoneServer implements ServerRT3 {
	
	private static final Set<String> KEEP_TYPES = new HashSet<>(Arrays.asList(new String[]{
			"image/gif", "image/png", "image/jpeg", "image/pjpeg", "text/css", "text/javascript",
	}));
	
	private static final ReplyAnswer ROBOTS_PRIVATE = Reply.string("SRVDOMAIN", null, "User-Agent: *\nDisallow: /");
	
	private static final ReplyAnswer ROBOTS_PUBLIC = Reply.string("SRVDOMAIN", null, "User-Agent: *\nAllow: /");
	
	private static final boolean checkKeepType(final String contentType) {
		
		final int index = contentType.indexOf(';');
		if (index == -1) {
			return ServerShare.KEEP_TYPES.contains(contentType);
		}
		return ServerShare.KEEP_TYPES.contains(contentType.substring(0, index).trim());
	}
	
	private final AccessType accessType;
	
	private final String alias;
	
	private final AuthType authType;
	
	private final Handler cmdSystem;
	
	private final String controlPath;
	
	private final ServerDomain domain;
	
	private int fixLocations = 16;
	
	private final Handler handler;
	
	private final int languageMode;
	
	private final String languageName;
	
	private final Share<?>[] nearest;
	
	private final ControlNode<?> node;
	
	private final String owner;
	
	private final boolean requireAuthorization;
	
	private final boolean requireSecure;
	
	private final boolean requireUnSecure;
	
	private final ReplyAnswer robots;
	
	private final ExecProcess rootContext;
	
	private final SecureType secureType;
	
	private final boolean shareTesting;
	
	private final Skinner skinner;
	
	private final String splashName;
	
	private final TransferDescription traffic;
	
	private final Share<?>[] variants;
	
	ServerShare(
			final ServerDomain domain,
			final Skinner skinner,
			final ControlNode<?> node,
			final Handler handler,
			final String alias,
			final String controlPath,
			final AuthType authType,
			final AccessType accessType,
			final SecureType secureType,
			final int languageMode,
			final String languageName,
			final String splashName,
			final Share<?>[] variants,
			final Share<?>[] nearest) {
		super(domain);
		assert domain != null : "Domain is NULL";
		this.domain = domain;
		assert skinner != null : "Skinner is NULL, domain=" + domain;
		this.skinner = skinner;
		assert node != null : "Node is NULL, domain=" + domain + ", skin=" + skinner + ", controlPath=" + controlPath;
		this.node = node;
		this.handler = handler;
		this.alias = alias;
		this.controlPath = controlPath.endsWith("/")
			? controlPath
			: controlPath + '/';
		this.authType = authType;
		this.accessType = accessType;
		this.secureType = secureType;
		this.shareTesting = accessType == AccessType.TESTING;
		this.requireAuthorization = accessType == AccessType.CLOSED || skinner.requireAuth();
		this.requireSecure = secureType == SecureType.REQUIRED || skinner.requireSecure();
		this.requireUnSecure = secureType == SecureType.NONE && !skinner.requireSecure();
		this.languageMode = languageMode;
		this.languageName = languageMode == Share.LM_LANG
			? languageName
			: domain.getLanguageDefault();
		this.splashName = splashName;
		this.variants = variants;
		this.nearest = nearest;
		this.cmdSystem = new CommandSystem(this);
		this.traffic = this.shareTesting
			? TransferDescription.DEFAULT_UNLIMITED
			: TransferDescription.HIGH_UNLIMITED;
		this.owner = "SRV_SHARE(" + this.alias + "=" + this.controlPath + ")";
		this.rootContext = Exec.createProcess(domain.getRootContext(), this.owner);
		switch (accessType) {
			case PUBLIC :
				this.robots = ServerShare.ROBOTS_PUBLIC;
				break;
			case TESTING :
				final Collection<Share<?>> shareAll = domain.getShareAll();
				Share<?> selected = null;
				for (final Share<?> share : shareAll) {
					if (share.getAccessType() == AccessType.PUBLIC) {
						if (share.getPath().equals(controlPath)) {
							selected = share;
							break;
						}
					}
				}
				if (selected == null && nearest != null) {
					for (final Share<?> share : shareAll) {
						if (share.getAccessType() == AccessType.PUBLIC && (languageMode != Share.LM_LANG || this.languageName.equals(share.getLanguageName()))) {
							selected = share;
							break;
						}
					}
				}
				if (selected != null) {
					this.robots = Reply.string("SRVDOMAIN", null, "User-Agent: *\nHost: " + selected.getKey() + "\nDisallow: /");
					break;
				}
				//$FALL-THROUGH$
			case CLOSED :
			default :
				this.robots = ServerShare.ROBOTS_PRIVATE;
				break;
		}
	}
	
	@Override
	public final boolean absorb(final ServeRequest query) {
		
		if (Report.MODE_DEBUG) {
			Report.debug(this.owner, "Share: " + this + ", Query:" + query);
		}
		if (query == Sharing.SHARE_RELOAD) {
			return this.parent.absorb(query);
		}
		if (this.requireSecure) {
			final ReplyAnswer switcher = query.toSecureChannel();
			if (switcher != null) {
				WebInterface.sendReply(query, switcher);
				return true;
			}
		}
		if (this.requireUnSecure) {
			final ReplyAnswer switcher = query.toUnSecureChannel();
			if (switcher != null) {
				WebInterface.sendReply(query, switcher);
				return true;
			}
		}
		final ExecProcess process = Exec.currentProcess();
		Context.replaceServer(process, this);
		final Context context = Context.getContext(process);
		{
			final String identifier = query.getResourceIdentifier();
			if (identifier.length() == 11 && "/robots.txt".equals(identifier)) {
				WebInterface.sendReply(query, this.robots.nextClone(query));
				return true;
			}
			if (this.splashName != null) {
				final BaseObject session = context.getSessionData();
				final String key = "splash-" + this.splashName;
				if (identifier.length() == 9 && "/continue".equals(identifier)) {
					final BaseObject initial = session.baseGet("initial", BaseObject.UNDEFINED);
					session.baseDefine(key, BaseObject.TRUE, BaseProperty.ATTRS_MASK_WED);
					try {
						final ReplyAnswer reply = initial == null
							? Reply.binary(
									"SPLASH", //
									query,
									Transfer.createBuffer(ServerShare.class.getResourceAsStream("splash-cookie.html")),
									"splash-cookie.html")
							: Reply.redirect(
									"SPLASH", //
									query,
									false,
									String.valueOf(initial));
						query.getResponseTarget()
								.apply(reply//
										.setPrivate()//
										.setSessionID(context.getSessionId()) //
						);
					} catch (final Throwable e) {
						Report.exception(this.owner, "splash error, continuation", e);
					}
					return true;
				}
				if (session.baseGetOwnProperty(key) == null) {
					if (session.baseGetOwnProperty("initial") == null) {
						session.baseDefine("initial", query.getResourcePrefix() + query.getResourceIdentifier());
					}
					try {
						final ReplyAnswer reply = Reply
								.binary(
										"SPLASH", //
										query,
										Transfer.createBuffer(ServerShare.class.getResourceAsStream(key + ".html")),
										key + ".html")//
								.setPrivate()//
								.setSessionID(context.getSessionId());
						query.getResponseTarget().apply(reply);
					} catch (final Throwable e) {
						Report.exception(this.owner, "splash error", e);
					}
					return true;
				}
			}
		}
		{
			final ReplyAnswer preAnswer = ServerDomain.prepareRequest(query, this.accessType);
			if (preAnswer != null) {
				WebInterface.sendReply(query, preAnswer);
				return true;
			}
		}
		Serve.checkParsePostParameters(query);
		if (this.requireAuthorization) {
			try {
				this.ensureAuthorization(this.accessType == AccessType.CLOSED
					? AuthLevels.AL_AUTHORIZED_HIGH
					: this.authType.getLevel());
			} catch (final AbstractReplyException reply) {
				WebInterface.sendReply(query, reply.getReply());
				return true;
			}
		}
		if (this.languageMode == Share.LM_AUTO) {
			if (query.getResourceIdentifier().startsWith("/intl/")) {
				final String path = query.getResourceIdentifier();
				final String language = this.getLanguage(path.substring(6, 8));
				query.setLanguage(language);
				query.setResourceIdentifier(path.substring(8));
			} else {
				query.setLanguage(this.getLanguage(query.getLanguage()));
			}
		} else {
			query.setLanguage(this.languageMode == Share.LM_LANG
				? this.languageName
				: this.getLanguage(query.getLanguage()));
		}
		
		ReplyAnswer response;
		
		try {
			// set the sharedObject
			query.setAttachment(this.node.getEntry());
			query.setSettings(this.skinner.getSkinSettings());
			
			response = this.cmdSystem.onQuery(query);
			if (response == null) {
				response = CommandUser.handleRequest(this, this.domain.rt, this.authType != AuthType.SITEFORM, query);
				if (response == null) {
					response = this.skinner.onQuery(query);
					if (response == null) {
						if (this.handler != null) {
							response = this.handler.onQuery(query);
						}
						if (response == null) {
							response = Reply
									.string(
											"RT3/SHARE", //
											query,
											"no response! original url: " + query.getUrl())//
									.setCode(Reply.CD_UNKNOWN)//
									.setNoCaching();
						}
					}
				}
			}
		} catch (final AbstractReplyException e) {
			response = e.getReply().addAttribute("via", "ACM");
		} catch (final Exception e) {
			Report.exception("SRV_SHARE", "share: " + this.alias + ", unhandled exception, sent as reply", e);
			response = Reply
					.string(
							"RT3/CTX", //
							query,
							Format.Throwable.toText(e))//
					.setCode(Reply.CD_EXCEPTION)//
					.setNoCaching()//
					.setPrivate();
		} catch (final OutOfMemoryError e) {
			try {
				this.parent.getCache().clear();
				System.runFinalization();
				System.gc();
				Report.error(this.owner, "Out of memory error - cache cleared, gc started", "Redirection request was sent to a client");
			} catch (final OutOfMemoryError ee) {
				// empty
			} catch (final Throwable t) {
				// empty
			}
			response = Reply
					.redirect(
							"RT3/CTX_MEM", //
							query,
							false,
							this.fixUrl(""))//
					.setNoCaching()//
					.setPrivate();
		} catch (final Throwable e) {
			response = Reply
					.string(
							"RT3/CTX", //
							query,
							Format.Throwable.toText(e))//
					.setCode(Reply.CD_EXCEPTION)//
					.setNoCaching()//
					.setPrivate();
		}
		if (!query.getStillActual()) {
			Report.info(this.owner, "Query is not actual - will skip skinner processing, source=" + query.getSourceAddress() + ", url=" + query.getUrl());
			return true;
		}
		if (this.secureType == SecureType.ONDEMAND && response.getCode() / 100 != 5) {
			if (response.isPrivate() || context.getSessionState() >= AuthLevels.AL_AUTHORIZED_NORMAL) {
				final ReplyAnswer switcher = query.toSecureChannel();
				if (switcher != null) {
					response = switcher;
					response.setFinal();
				}
			} else {
				final ReplyAnswer switcher = query.toUnSecureChannel();
				if (switcher != null) {
					final String contentType = Base.getString(response.getAttributes(), "Content-Type", null);
					final String referer = Base.getString(query.getAttributes(), "Referer", null);
					if (contentType == null || referer == null || !(referer.startsWith("https://") && ServerShare.checkKeepType(contentType))) {
						response = switcher;
						response.setFinal();
					}
				}
			}
		}
		if (this.skinner != null) {
			if (this.shareTesting) {
				response.setAttribute("testing", BaseObject.TRUE);
			}
			final ReplyAnswer newResponse = this.skinner.handleReply(response);
			if (newResponse != null && newResponse != response) {
				response = newResponse;
			}
		}
		if (response.isPrivate()) {
			response.setUserID(context.getUserId());
			response.setSessionID(context.getSessionId());
			response.setSourceAddress(this.getDomainId());
		}
		{
			final int code = response.getCode();
			response.setAttribute(
					"Transfer-Class",
					code != Reply.CD_UNAUTHORIZED && (code / 100 == 4 || code / 100 == 5) && context.getSessionState() < AuthLevels.AL_AUTHORIZED_NORMAL
						// this will delay response slightly
						? Base.forUnknown(TransferDescription.IDLE_UNLIMITED)
						: Base.forUnknown(this.traffic));
		}
		WebInterface.sendReply(query, response);
		return true;
	}
	
	@Override
	public final AccessUser<?> ensureAuthorization(final int level) {
		
		final AuthType authType = this.authType;
		if (authType == AuthType.SITEFORM) {
			final String preCheck = Base.getString(this.skinner.getSkinSettings(), "authEscalationHandler", "").trim();
			if (preCheck.length() > 0) {
				final ExecProcess ctx = Exec.currentProcess();
				ctx.vmFrameEntryExCall(true, BaseObject.UNDEFINED, null, ExecArgumentsEmpty.INSTANCE, ResultHandler.FA_BNN_NXT);
				ctx.vmScopeDeriveContext(this.rootContext.ri10GV);
				final LoginCheckContextBean checkContext = new LoginCheckContextBean();
				ctx.contextCreateMutableBinding("authEscalationHandler", preCheck, false);
				ctx.contextCreateMutableBinding("checkContext", Base.forUnknown(checkContext), false);
				final boolean success = Evaluate.evaluateBoolean("require(authEscalationHandler).checkAuth(checkContext)", ctx, null);
				ctx.vmFrameLeave();
				final String userId = checkContext.getUserId();
				if (success && userId != null) {
					return this.domain.getAccessManager().getUser(userId, true);
				}
				final String errorText = checkContext.getErrorText();
				if (errorText != null) {
					throw new Error("AUTH fail: " + errorText);
				}
			}
			return this.parent.ensureAuthorization(this.accessType == AccessType.CLOSED
				? Math.max(level, authType.getLevel())
				: level);
		}
		if (authType == AuthType.SYSTEM) {
			final ExecProcess process = Exec.currentProcess();
			try {
				return Context.getContext(process).ensureAuthorization(this.accessType == AccessType.CLOSED
					? Math.max(level, authType.getLevel())
					: level);
			} catch (final InvalidCredentials e) {
				return null;
			}
		}
		assert false : "its either SITEFORM, either SYSTEM. even NULL is invalid here, authType=" + authType;
		return this.parent.ensureAuthorization(level);
	}
	
	@Override
	public final String fixLocation(final ExecProcess process, final String path, final boolean absolute) {
		
		if (absolute) {
			return this.parent.fixLocation(process, path, true);
		}
		if (path == null) {
			if (this.fixLocations > 0) {
				final Error error = new Error("CHECKPOINT!");
				Report.exception(this.owner, "entry doesn't have a path, maybe lost? (" + --this.fixLocations + " messages left)", error);
			} else {
				Report.warning(this.owner, "entry doesn't have a path, maybe lost?");
			}
			return "#";
		}
		if (this.controlPath.equals(path + '/')) {
			if (this.languageMode == Share.LM_AUTO) {
				final String language = Context.getLanguage(process).getName();
				if (!this.languageName.equals(language)) {
					return "/intl/" + language + '/';
				}
				return "/";
			}
			return "/";
		}
		if (path.startsWith(this.controlPath)) {
			final String localPath = path.substring(this.controlPath.length());
			if (this.languageMode == Share.LM_AUTO) {
				final String language = Context.getLanguage(process).getName();
				if (!this.languageName.equals(language)) {
					if (localPath.startsWith("/")) {
						return "/intl/" + language + localPath;
					}
					return "/intl/" + language + '/' + localPath;
				}
			}
			if (localPath.startsWith("/")) {
				return localPath;
			}
			return '/' + localPath;
		}
		if (this.nearest != null) {
			for (final Share<?> share : this.nearest) {
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
		return this.parent.fixLocation(process, path, false);
	}
	
	@Override
	public final String fixUrl(final String url) {
		
		if (this.languageMode == Share.LM_AUTO) {
			return this.domain.fixUrl(url);
		}
		if (this.languageMode == Share.LM_NONE) {
			return url;
		}
		if (url == null) {
			return Context.getRequest(Exec.currentProcess()).getUrlBase();
		}
		if (url.startsWith("/")) {
			final ServeRequest query = Context.getRequest(Exec.currentProcess());
			final String base = query.getUrlBase();
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
		if (url.lastIndexOf("://", 10) != -1) {
			return url;
		}
		try {
			/**
			 * context relative?
			 */
			final String base = Context.getRequest(Exec.currentProcess()).getUrl();
			final URL baseUrl = new URL(base);
			final URL relativeUrl = new URL(baseUrl, url);
			return relativeUrl.toExternalForm();
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final String fixUrl(final String url, final String language) {
		
		if (this.languageMode == Share.LM_AUTO) {
			return this.domain.fixUrl(url, language);
		}
		if (url == null) {
			return this.fixUrl("/", language);
		}
		if (url.startsWith("/")) {
			if (url.startsWith("/intl/")) {
				return this.fixUrl(url.substring(8), language);
			}
			if (this.languageMode == Share.LM_LANG && this.languageName.equals(language) || this.variants == null) {
				return Context.getRequest(Exec.currentProcess()).getUrlBase() + url.substring(1);
			}
			Share<?> variantAuto = null;
			for (int i = this.variants.length - 1; i >= 0; --i) {
				final Share<?> variant = this.variants[i];
				switch (variant.getLanguageMode()) {
					case Share.LM_AUTO :
						variantAuto = variant;
						break;
					case Share.LM_LANG :
						if (variant.getLanguageName().equals(language)) {
							return (variant.getSecureType() == SecureType.REQUIRED
								? "https://"
								: "http://") + variant.getAlias() + url;
						}
						break;
					default :
				}
			}
			if (variantAuto == null) {
				return Context.getRequest(Exec.currentProcess()).getUrlBase() + url.substring(1);
			}
			return (variantAuto.getSecureType() == SecureType.REQUIRED
				? "https://"
				: "http://") + variantAuto.getAlias() + url;
		}
		if (url.lastIndexOf("://", 10) != -1) {
			return url;
		}
		assert false : "unsupported url (context relative?): " + url + ", language=" + language;
		return url;
	}
	
	@Override
	public String getControlBase() {
		
		return this.controlPath;
	}
	
	@Override
	public final String getLanguageDefault() {
		
		return this.languageName;
	}
	
	@Override
	public final ExecProcess getRootContext() {
		
		return this.rootContext;
	}
	
	protected final Skinner getSkin() {
		
		return this.skinner;
	}
	
	@Override
	public Skinner getSkinner(final String name) {
		
		return this.parent.getSkinner(name);
	}
	
	@Override
	public Collection<String> getSkinnerNames() {
		
		return this.parent.getSkinnerNames();
	}
	
	@Override
	public boolean isControllerServer() {
		
		return this.domain.isControllerServer();
	}
	
	@Override
	public Skinner registerSkinner(final String name, final Skinner skinner) {
		
		return this.domain.registerSkinner(name, skinner);
	}
	
	@Override
	public String registerUser(final String guid, final String login, final String lowerCase, final String passwordToUse, final BaseObject data) throws Exception {
		
		return this.domain.registerUser(guid, login, lowerCase, passwordToUse, data);
	}
	
	@Override
	public String toString() {
		
		return "SHARE( alias=" + this.alias + ", handler=" + this.handler + ", renderer=" + this.skinner + ", authType=" + this.authType.name() + ", accessType="
				+ this.accessType.name() + ", language=" + this.languageName + " )";
	}
}
