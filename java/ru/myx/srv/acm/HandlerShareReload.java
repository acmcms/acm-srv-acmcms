/*
 * Created on 13.04.2006
 */
package ru.myx.srv.acm;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.i3.RequestHandler;
import ru.myx.ae3.serve.ServeRequest;

final class HandlerShareReload implements RequestHandler {
	@Override
	public final ReplyAnswer onQuery(final ServeRequest request) {
		return Reply.string( "DEAD_SHARE", request, "Share is dead!" ).setCode( Reply.CD_UNKNOWN );
	}
}
