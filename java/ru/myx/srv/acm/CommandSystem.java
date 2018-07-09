package ru.myx.srv.acm;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.i3.TargetInterface;
import ru.myx.ae3.l2.http.HttpTargetInterface;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.sapi.ApplicationSAPI;
import ae1.runtime.addons.RuntimeDefaultActionRunner;

final class CommandSystem implements Handler {
	private static final String		OWNER				= "ACM/SYSTEM";
	
	private static final Handler	defaultActionRunner	= new RuntimeDefaultActionRunner();
	
	private static final ReplyAnswer doSystem(
			final ExecProcess process,
			final Server server,
			final ServeRequest query,
			final String request) {
		if (request.endsWith( ",language" )) {
			final String language = server.getLanguage( request.substring( 0, request.indexOf( ',' ) ) );
			if (Context.getSessionState( process ) > AuthLevels.AL_UNAUTHORIZED) {
				final AccessUser<?> user = Context.getUser( process );
				user.setLanguage( language );
				user.commit();
			}
			return Respond.getBackLanguageRedirect( server, language, query ).setNoCaching();
		}
		if (request.endsWith( "login" )) {
			Context.invalidateAuth( process );
			Context.getServer( process ).ensureAuthorization( AuthLevels.AL_AUTHORIZED_NORMAL );
			return Respond.getBackRedirect( server, query ).setNoCaching();
		}
		if (request.endsWith( "logout" )) {
			Context.invalidateUser( process );
			return Respond.getBackRedirect( server, query ).setNoCaching();
		}
		if (request.startsWith( "ticket/" )) {
			return CommandSystem.doTicket( server, query, request.substring( 7 ) );
		}
		return null;
	}
	
	private static final ReplyAnswer doTicket(final Server server, final ServeRequest query, final String argument) {
		final String ticket;
		final String forward;
		{
			final int pos = argument.indexOf( '/' );
			ticket = pos == -1
					? argument
					: argument.substring( 0, pos );
			final String url = query.getUrl();
			forward = url.substring( url.indexOf( ticket ) + ticket.length() );
		}
		final BaseObject action = server.getStorage().load( ticket );
		assert action != null : "NULL java value";
		if (action == BaseObject.UNDEFINED) {
			return Reply.redirect( CommandSystem.OWNER, //
					query,
					true,
					forward );
		}
		final String typeName = Base.getString( action, "actionType", "" ).trim();
		if (typeName.length() == 0) {
			return Reply.string( CommandSystem.OWNER, //
					query,
					"ActionType is unknown!" )//
					.setCode( Reply.CD_UNKNOWN );
		}
		final Handler runner = CommandSystem.defaultActionRunner;
		if (runner == null) {
			return Reply.string( CommandSystem.OWNER, //
					query,
					"ActionType (" + typeName + ") is unknown or not registered!" )//
					.setCode( Reply.CD_UNKNOWN );
		}
		final ReplyAnswer result = runner.onQuery( query.setParameters( action ) );
		final BaseObject empty = new BaseNativeObject();
		server.getStorage().saveTemporary( ticket, empty, 0 );
		return result != null
				? result
				: Reply.redirect( CommandSystem.OWNER, //
						query,
						true,
						forward ) //
						.setNoCaching() //
						.setPrivate();
	}
	
	private final Server			server;
	
	private final TargetInterface	iface;
	
	CommandSystem(final Server server) {
		this.server = server;
		this.iface = new HttpTargetInterface( server.getVfsRootEntry() );
	}
	
	@Override
	public final ReplyAnswer onQuery(final ServeRequest query) {
		final ReplyAnswer answer = this.iface.onQuery( query );
		if (answer != null) {
			return answer;
		}
		final ExecProcess process = Exec.currentProcess();
		for (int scan_position = 0;;) {
			final String path = query.getResourceIdentifier();
			if (path.startsWith( "/_", scan_position )) {
				if (path.startsWith( "/_paging/", scan_position )) {
					final String newPath1 = path.substring( scan_position + 8 );
					final int sPos = newPath1.indexOf( '/', 1 );
					if (sPos != -1) {
						final String page = newPath1.substring( 1, sPos );
						try {
							Integer.parseInt( page );
							Context.getFlags( process ).baseDefine("pageRequested", page);
							query.setResourceIdentifier( scan_position > 0
									? path.substring( 0, scan_position ) + newPath1.substring( sPos )
									: newPath1.substring( sPos ) );
						} catch (final Throwable t) {
							Report.exception( CommandSystem.OWNER, "Paging error, skipping", t );
						}
					}
				} else //
				if (scan_position == 0 && path.length() > 4 && path.charAt( 1 ) == '_' && path.charAt( 4 ) == '/') {
					query.setLanguage( path.substring( 2, 4 ) );
					scan_position += 4;
				} else //
				if (scan_position == 0
						&& path.length() > 7
						&& path.charAt( 1 ) == '_'
						&& path.charAt( 4 ) == '-'
						&& path.charAt( 7 ) == '/') {
					query.setLanguage( path.substring( 2, 4 ) );
					scan_position += 7;
				} else //
				if (path.startsWith( "/_finder/", scan_position )) {
					final String request = path.substring( scan_position + 9 );
					return ApplicationSAPI.getFinderResultReplyImpl( process, this.server, query, request );
				} else //
				if (path.startsWith( "/_sys/", scan_position )) {
					final String request = path.substring( scan_position + 6 );
					return CommandSystem.doSystem( process, this.server, query, request );
				} else //
				if (scan_position == 0 && path.length() > 4 && path.charAt( 1 ) == '_' && path.charAt( 4 ) == '/') {
					query.setLanguage( path.substring( 2, 4 ) );
					scan_position += 4;
				} else //
				if (scan_position == 0
						&& path.length() > 7
						&& path.charAt( 1 ) == '_'
						&& path.charAt( 4 ) == '-'
						&& path.charAt( 7 ) == '/') {
					query.setLanguage( path.substring( 2, 4 ) );
					scan_position += 7;
				} else {
					break;
				}
			} else {
				break;
			}
		}
		return null;
	}
}
