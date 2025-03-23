package ae1.runtime.addons;

import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.serve.ServeRequest;

/** @author myx */
public class RuntimeDefaultUserActionRunner implements Handler {
	
	private static final String OWNER = "RT3/DUAR";
	
	@Override
	public ReplyAnswer onQuery(final ServeRequest query) {
		
		final BaseObject data = query.getParameters();
		final String action = Base.getString(data, "action", "");
		if (action.length() == 0) {
			return Reply.string(RuntimeDefaultUserActionRunner.OWNER, query, "No action specified!").setCode(Reply.CD_UNKNOWN);
		}
		final ExecProcess process = Exec.currentProcess();
		final Server server = Context.getServer(process);
		final AccessManager manager = server.getAccessManager();
		if (action.equals("changeEmail")) {
			final String userid = Base.getString(data, "userid", "");
			final String email = Base.getString(data, "email", "");
			final AccessUser<?> user = manager.getUser(userid, false);
			if (user == null) {
				return Reply.string(RuntimeDefaultUserActionRunner.OWNER, query, "UserID (" + userid + ") was not found!").setCode(Reply.CD_UNKNOWN);
			}
			user.setEmail(email);
			manager.commitUser(user);
			throw Reply.exception(Reply.redirect("USR/RUNNER", query, false, server.fixUrl("/change-email.user?done=1")));
		}
		if (action.equals("forgotPassword")) {
			final BaseObject flags = Context.getFlags(process);
			final String userid = Base.getString(data, "userid", "");
			final AccessUser<?> user = manager.getUser(userid, false);
			if (user == null) {
				return Reply.string(RuntimeDefaultUserActionRunner.OWNER, query, "UserID (" + userid + ") was not found!").setCode(Reply.CD_UNKNOWN);
			}
			flags.baseDefine("Form", "password");
			final BaseObject mmdp = query.getParameters();
			final String password = Base.getString(mmdp, "newpass", "");
			if (password.length() > 0) {
				if (password.length() < 6) {
					flags.baseDefine("Error", ru.myx.cm5.control.um.NodeUM.getMessageWrongPasswordLength());
				} else {
					final String pass2 = Base.getString(mmdp, "newpass2", "");
					if (!pass2.equals(password)) {
						flags.baseDefine("Error", ru.myx.cm5.control.um.NodeUM.getMessagePasswordsNotSame());
					} else {
						manager.setPassword(user, password, null);
						manager.commitUser(user);
						flags.baseDefine("PasswordChanged", BaseObject.TRUE);
					}
				}
			}
			final BaseObject dta = server.getStorage().load("um-forget-password.user");
			assert dta != null : "NULL java value";
			final String Default = ru.myx.cm5.control.um.NodeUM.getDefaultTemplateFor("forget-password.user");
			final String template = Base.getString(dta, "template", Default);
			try {
				return Reply.object(
						RuntimeDefaultUserActionRunner.OWNER, //
						query,
						server.createRenderer("DEFAULT-USER-ACTION", template)//
								.callNE0(Exec.currentProcess(), BaseObject.UNDEFINED)//
				)//
						.setTitle("Forgot password")//
						.setTimeToLiveHours(1);
			} catch (final Error | RuntimeException e) {
				throw e;
			} catch (final Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return Reply.string(
				RuntimeDefaultUserActionRunner.OWNER, //
				query,
				"Unknown action specified - '" + action + "'!").setCode(Reply.CD_UNKNOWN);
	}
}
