package ae1.runtime.addons;

import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;

/**
 * @author myx
 * 
 */
public class RuntimeDefaultActionRunner implements Handler {
	@Override
	public ReplyAnswer onQuery(final ServeRequest query) {
		final BaseObject data = query.getParameters();
		final String action = Base.getString( data, "action", "" );
		if (action.length() == 0) {
			return Reply.string( "RT3/DAR:RS/NAS", query, "No action specified!" ).setCode( Reply.CD_UNKNOWN );
		}
		final ExecProcess process = Exec.currentProcess();
		if (action.equals( "sessionTransfer" )) {
			final String uid = Base.getString( data, "uid", "" ).trim();
			final String sid = Base.getString( data, "sid", "" ).trim();
			final int state = Convert.MapEntry.toInt( data, "state", 0 );
			final Context context = Context.getContext( process );
			context.replaceUserId( uid );
			context.replaceSessionId( sid );
			context.setSessionState( state );
			Report.audit( "RT3/DAR:AU/ST",
					"SESSION-TRANSFER",
					"Session transfer, referer="
							+ Base.getString( query.getAttributes(), "Referer", null )
							+ ", uid="
							+ uid
							+ ", sid="
							+ sid
							+ ", state="
							+ state );
			return null;
		}
		return Reply.string( "RT3/DAR:RS/UAS", query, "Unknown action specified - '" + action + "'!" )
				.setCode( Reply.CD_UNKNOWN );
	}
}
