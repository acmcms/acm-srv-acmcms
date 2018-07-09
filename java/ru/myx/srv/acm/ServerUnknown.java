/*
 * Created on 17.06.2004
 */
package ru.myx.srv.acm;

import java.util.Collection;

import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.know.FilterZoneServer;
import ru.myx.ae1.sharing.AccessType;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferDescription;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.i3.web.WebInterface;
import ru.myx.ae3.i3.web.WebTarget;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.Serve;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.cm5.control.sharing.Sharing;

/**
 * @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class ServerUnknown extends FilterZoneServer implements ServerRT3 {

	private static final ReplyAnswer ROBOTS_PRIVATE = Reply.string("SRVDOMAIN", null, "User-Agent: *\nDisallow: /");

	private final static String normalizeUrl(final String url, final String defaultValue) {

		if (url == null || url.length() == 0) {
			return defaultValue;
		}
		return url.charAt(url.length() - 1) == '/'
			? url
			: url + '/';
	}

	private final String alias;

	private final Handler cmdSystem;

	private final String controlPath;

	private final ServerDomain domain;

	/*
	 * ends with '/'
	 */
	private final String entrance;

	private final Share<?>[] nearest;

	private final ExecProcess rootContext;

	ServerUnknown(final ServerDomain domain, final String entrance, final String alias, final Share<?>[] nearest) {
		super(domain);
		this.domain = domain;
		this.entrance = ServerUnknown.normalizeUrl(entrance, null);
		this.alias = alias;
		this.controlPath = "/";
		this.nearest = nearest;
		this.cmdSystem = new CommandSystem(this);
		this.rootContext = Exec.createProcess(domain.getRootContext(), "UnknownShare: " + alias + ", path=" + this.controlPath);
	}

	@Override
	public final boolean absorb(final ServeRequest query) {

		if (Report.MODE_DEBUG) {
			Report.debug("SRV/UNKNOWN", "Share: " + this + ", Query:" + query);
		}
		if (query == Sharing.SHARE_RELOAD) {
			return this.domain.absorb(query);
		}
		final ExecProcess process = Exec.currentProcess();
		Context.replaceServer(process, this);
		/**
		 * default system hosts
		 */
		{
			final WebTarget target = WebInterface.dispatcherForQuery(query);
			if (target != null) {
				final WebTarget actual = target.getWaitRealTarget();
				if (actual != null && actual != WebInterface.TARGET_UNKNOWN) {
					actual.onDispatch(query);
					return true;
				}
			}
		}
		final Context context = Context.getContext(process);
		{
			try {
				context.ensureAuthorization(AuthLevels.AL_AUTHORIZED_HIGH);
			} catch (final AbstractReplyException reply) {
				WebInterface.sendReply(query, reply.getReply());
				return true;
			}
		}
		{
			final String identifier = query.getResourceIdentifier();
			if (identifier.length() == 11 && "/robots.txt".equals(identifier)) {
				WebInterface.sendReply(query, ServerUnknown.ROBOTS_PRIVATE.nextClone(query));
				return true;
			}
		}
		{
			final ReplyAnswer preAnswer = ServerDomain.prepareRequest(query, AccessType.TESTING);
			if (preAnswer != null) {
				WebInterface.sendReply(query, preAnswer);
				return true;
			}
		}
		Serve.checkParsePostParameters(query);
		{
			if (query.getResourceIdentifier().startsWith("/intl/")) {
				final String path = query.getResourceIdentifier();
				final String language = this.getLanguage(path.substring(6, 8));
				query.setLanguage(language);
				query.setResourceIdentifier(path.substring(8));
			} else {
				query.setLanguage(this.getLanguage(query.getLanguage()));
			}
		}

		ReplyAnswer response = null;
		Skinner skinner = null;

		try {
			// set the sharedObject
			query.setAttachment(this.domain.getControlRoot().getEntry());

			response = this.cmdSystem.onQuery(query);
			if (response == null) {
				response = CommandUser.handleRequest(this, this.domain.rt, true, query);
				if (response == null) {
					if (query.getResourceIdentifier().equals("/!")) {
						query.setResourceIdentifier("/");
						final Skinner chooser = this.domain.getSkinner("skin-choose-session-interface");
						response = chooser.onQuery(query);
						if (response != null) {
							response = chooser.handleReply(response);
						}
					}
					if (response == null) {
						final BaseObject profile = context.getUser().getProfile();
						final String skin = Base.getString(profile, "useInterface-" + query.getTarget(), "useInterface", "").trim();
						if (skin.length() > 0) {
							skinner = this.domain.getSkinner(skin);
							if (skinner != null) {
								query.setSettings(skinner.getSkinSettings());
								response = skinner.onQuery(query);
								if (response == null) {
									response = Reply.string("SHARE_UNKNOWN", query, "no response! original url: " + query.getUrl()).setCode(Reply.CD_UNKNOWN).setNoCaching();
								}
							}
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
				this.domain.getCache().clear();
				System.runFinalization();
				System.gc();
				Report.error("RT3/CTX_MEM", "Out of memory error - cache cleared, gc started", "Redirection request was sent to a client");
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
			Report.info("SERVER_UNKNOWN", "Query is not actual - will skip skinner processing, source=" + query.getSourceAddress() + ", url=" + query.getUrl());
			return true;
		}
		if (response == null || response.isEmpty()) {
			response = ServerUnknown.normalizeUrl(query.getUrl(), "").equalsIgnoreCase(this.entrance)
				? Reply.string("RT3/UNKNOWN", query, "Sorry, there is no content to show (" + this.entrance + ").").setCode(Reply.CD_UNKNOWN)
				: Reply.redirect("RT3/UNKNOWN", query, true, this.entrance);
		}
		if (skinner != null) {
			final ReplyAnswer newResponse = skinner.handleReply(response);
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
						: Base.forUnknown(TransferDescription.DEFAULT_UNLIMITED));
		}
		WebInterface.sendReply(query, response);
		return true;
	}

	@Override
	public final String fixLocation(final ExecProcess process, final String path, final boolean absolute) {

		if (absolute) {
			return this.domain.fixLocation(process, path, true);
		}
		if (path.startsWith(this.controlPath)) {
			final String localPath = path.substring(this.controlPath.length());
			if (localPath.startsWith("/")) {
				return localPath;
			}
			return '/' + localPath;
		}
		if (this.controlPath.equals(path + '/')) {
			return "/";
		}
		if (this.nearest != null) {
			for (int i = this.nearest.length - 1; i >= 0; --i) {
				final String check = this.nearest[i].getPath() + '/';
				if (path.startsWith(check)) {
					final String localPath = path.substring(check.length());
					if (localPath.startsWith("/")) {
						return "http://" + this.nearest[i].getAlias() + localPath;
					}
					return "http://" + this.nearest[i].getAlias() + '/' + localPath;
				}
				if (check.equals(path + '/')) {
					return "http://" + this.nearest[i].getAlias();
				}
			}
		}
		if (path.startsWith(this.controlPath)) {
			final String localPath = path.substring(this.controlPath.length());
			if (localPath.startsWith("/")) {
				return localPath;
			}
			return '/' + localPath;
		}
		return this.domain.fixLocation(process, path, false);
	}

	@Override
	public final ExecProcess getRootContext() {

		return this.rootContext;
	}

	@Override
	public Skinner getSkinner(final String name) {

		return this.domain.getSkinner(name);
	}

	@Override
	public Collection<String> getSkinnerNames() {

		return this.domain.getSkinnerNames();
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
	public String toString() {

		return "DEFAULT( alias=" + this.alias + ", domain=" + this.domain + " )";
	}
}
