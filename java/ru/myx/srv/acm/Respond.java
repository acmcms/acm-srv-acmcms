/**
 * Created on 23.11.2002
 * 
 * myx - barachta */
package ru.myx.srv.acm;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author barachta
 * 
 * myx - barachta 
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
class Respond {
	
	static final ReplyAnswer getBackLanguageRedirect(
			final Server server,
			final String language,
			final ServeRequest query) {
		String url = Base.getString( query.getParameters(), "back", query.getAttributes(), "Referer", null );
		final String base = query.getUrlBase();
		if (url == null) {
			url = "/";
		} else //
		/**
		 * base doesn't have trailing '/'
		 */
		if (url.startsWith( base ) && (url.length() == base.length() || url.charAt( base.length() ) == '/')) {
			url = url.substring( base.length() );
		}
		return Reply.redirect( "BACK/REDIR", //
				query,
				false,
				language == null
						? server.fixUrl( url )
						: server.fixUrl( url, language ) );
	}
	
	static final ReplyAnswer getBackRedirect(final Server server, final ServeRequest query) {
		return Respond.getBackLanguageRedirect( server, null, query );
	}
	
	private Respond() {
		// empty
	}
}
