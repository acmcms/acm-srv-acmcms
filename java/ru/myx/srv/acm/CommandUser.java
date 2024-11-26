package ru.myx.srv.acm;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import ae1.runtime.addons.RuntimeDefaultUserActionRunner;
import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.AuthLevels;
import ru.myx.ae1.access.PasswordType;
import ru.myx.ae1.access.SortMode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.email.Email;
import ru.myx.ae3.email.EmailSender;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Text;
import ru.myx.ae3.i3.Handler;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.cm5.control.um.NodeUM;
import ru.myx.sapi.RuntimeEnvironment;
import ru.myx.sapi.UserManagerSAPI;

/** @author myx */
final class CommandUser {
	
	private static final String OWNER = "ACM/USER";
	
	private static final Object EMAIL_VALIDATION_STR = MultivariantString
			.getString("e-mail address validation", Collections.singletonMap("ru", "подтверждение адреса электронной почты"));
	
	private static final Handler defaultUserActionRunner = new RuntimeDefaultUserActionRunner();
	
	private static ReplyAnswer doChangeEmail(final Server server, final EmailSender mta, final ServeRequest query, final BaseObject flags) {
		
		server.ensureAuthorization(AuthLevels.AL_AUTHORIZED_NORMAL);
		final AccessUser<?> user = Context.getUser(Exec.currentProcess());
		final BaseObject rd = query.getParameters();
		final String password = Base.getString(rd, "password", "");
		if (password.length() > 0) {
			final String actionID = Engine.createGuid();
			final String newEmail = Base.getString(rd, "email", "").trim().toLowerCase();
			if (newEmail.length() < 3 || newEmail.indexOf('@') == -1) {
				flags.baseDefine("Error", NodeUM.getMessageRegistrationEmailInvalid());
			} else {
				String emailSubject = CommandUser.EMAIL_VALIDATION_STR.toString();
				final String login = user.getLogin();
				final String url = server.fixUrl("/user-action.user?action=" + actionID);
				String emailBody = "<a href=" + url + ">" + url + "</a>";
				try {
					flags.baseDefine("login", login);
					flags.baseDefine("url", url);
					emailBody = server.createRenderer("UMAN:changed-email.eml", NodeUM.getTemplateFor("changed-email.eml"))//
							.callSE0(Exec.currentProcess(), BaseObject.UNDEFINED);
				} catch (final AbstractReplyException e) {
					final ReplyAnswer reply = e.getReply();
					emailSubject = reply.getTitle();
					try {
						emailBody = reply.toCharacter().getText().toString();
					} catch (final Exception ee) {
						emailBody = ee.getMessage();
					}
				} catch (final RuntimeException e) {
					throw e;
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}
				mta.sendEmail(new Email(NodeUM.getUserManagerEmailAddress(), newEmail, emailSubject, emailBody));
				final BaseObject action = new BaseNativeObject()//
						.putAppend("actionType", "defaultUAR")//
						.putAppend("action", "changeEmail") //
						.putAppend("userid", user.getKey()) //
						.putAppend("email", newEmail) //
				;
				final long expiration = Engine.fastTime() + 60_000L * 60L * 24L * 7L;
				server.getStorage().saveTemporary(actionID, action, expiration);
				flags.baseDefine("Scheduled", BaseObject.TRUE);
				flags.baseDefine("ValidTill", Base.forDateMillis(expiration));
			}
		} else //
		if (Convert.MapEntry.toBoolean(rd, "done", false)) {
			flags.baseDefine("Done", BaseObject.TRUE);
		}
		try {
			return Reply.object(
					CommandUser.OWNER, //
					query,
					server.createRenderer("UMAN:change-email.user", NodeUM.getTemplateFor("change-email.user"))//
							.callNE0(Exec.currentProcess(), BaseObject.UNDEFINED)//
			) //
					.setTitle("User settings") //
					.setTimeToLiveHours(1) //
					.setFlags(flags) //
					.setPrivate();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static ReplyAnswer doChangePassword(final Server server, final ServeRequest query, final BaseObject flags) {
		
		server.ensureAuthorization(AuthLevels.AL_AUTHORIZED_NORMAL);
		final ExecProcess process = Exec.currentProcess();
		final AccessUser<?> user = Context.getUser(process);
		final BaseObject rd = query.getParameters();
		final String password = Base.getString(rd, "password", "").trim();
		if (password.length() > 0) {
			final AccessUser<?> userCheck = Access.getUserByLoginCheckPassword(server.getAccessManager(), user.getLogin(), password, PasswordType.NORMAL);
			if (userCheck == null) {
				flags.baseDefine("Error", NodeUM.getMessageWrongPassword());
			} else {
				final String pass1 = Base.getString(rd, "newpass", "");
				if (pass1.length() < 6) {
					flags.baseDefine("Error", NodeUM.getMessageWrongPasswordLength());
				} else {
					final String pass2 = Base.getString(rd, "newpass2", "");
					if (!pass2.equals(pass1)) {
						flags.baseDefine("Error", NodeUM.getMessagePasswordsNotSame());
					} else {
						server.getAccessManager().setPassword(userCheck, pass1, PasswordType.NORMAL);
						server.getAccessManager().commitUser(userCheck);
						flags.baseDefine("PasswordChanged", BaseObject.TRUE);
					}
				}
			}
		}
		final Object result;
		try {
			result = server.createRenderer("UMAN:change-password.user", NodeUM.getTemplateFor("change-password.user"))//
					.callNE0(process, BaseObject.UNDEFINED);
		} catch (final AbstractReplyException e) {
			return e.getReply().setContentID("mwmUserSettings").setPrivate();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		return Reply.object(
				CommandUser.OWNER, //
				query,
				result) //
				.setTitle("User settings") //
				.setTimeToLiveHours(1) //
				.setFlags(flags) //
				.setPrivate() //
		// .setContentIdentity( "mwmUserSettings" )
		;
	}
	
	private static ReplyAnswer doForgetPassword(final Server server, final EmailSender mta, final ServeRequest query, final BaseObject flags) {
		
		final String email = Base.getString(query.getParameters(), "email", "").toLowerCase();
		flags.baseDefine("Form", "email");
		if (email.length() > 0) {
			if (email.length() < 3 || email.indexOf('@') == -1) {
				flags.baseDefine("Error", NodeUM.getMessageRegistrationEmailInvalid());
			} else {
				final AccessUser<?>[] users = server.getAccessManager().search(null, email, -1, -1, SortMode.SM_LOGIN);
				if (users == null || users.length == 0) {
					flags.baseDefine("Error", "No such users found!");
				} else {
					for (final AccessUser<?> user : users) {
						if (user == null) {
							continue;
						}
						if (user.getEmail() == null || !user.getEmail().equals(email)) {
							continue;
						}
						final String actionID = Engine.createGuid();
						String emailSubject = NodeUM.getMessagePasswordRestoreSubject();
						final String login = user.getLogin();
						final String url = server.fixUrl("/user-action.user?action=" + actionID);
						String emailBody = "<a href=" + url + ">" + url + "</a>";
						try {
							flags.baseDefine("login", login);
							flags.baseDefine("url", url);
							emailBody = server.createRenderer("UMAN:forgotten-password.eml", NodeUM.getTemplateFor("forgotten-password.eml"))//
									.callSE0(Exec.currentProcess(), BaseObject.UNDEFINED);
						} catch (final AbstractReplyException e) {
							final ReplyAnswer reply = e.getReply();
							emailSubject = reply.getTitle();
							try {
								emailBody = reply.toCharacter().getText().toString();
							} catch (final Exception ee) {
								emailBody = ee.getMessage();
							}
						} catch (final RuntimeException e) {
							throw e;
						} catch (final Exception e) {
							throw new RuntimeException(e);
						}
						mta.sendEmail(new Email(NodeUM.getUserManagerEmailAddress(), email, emailSubject, emailBody));
						final BaseObject action = new BaseNativeObject()//
								.putAppend("actionType", "defaultUAR")//
								.putAppend("action", "forgotPassword")//
								.putAppend("userid", user.getKey())//
						;
						final long expiration = Engine.fastTime() + 60_000L * 60L * 24L * 7L;
						server.getStorage().saveTemporary(actionID, action, expiration);
						flags.baseDefine("Scheduled", BaseObject.TRUE);
						flags.baseDefine("ValidTill", Base.forDateMillis(expiration));
						break;
					}
				}
			}
		}
		try {
			return Reply.object(
					CommandUser.OWNER, //
					query,
					server.createRenderer("UMAN:forget-password.user", NodeUM.getTemplateFor("forget-password.user"))//
							.callNE0(Exec.currentProcess(), BaseObject.UNDEFINED)//
			)//
					.setTitle("User settings")//
					.setTimeToLiveHours(1)//
					.setFlags(flags)//
					.setPrivate();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static final ReplyAnswer doLogin(final Server server, final boolean useSystemAuth, final ServeRequest query, final ExecProcess process, final BaseObject flags) {
		
		final Context context = Context.getContext(process);
		final BaseObject session = context.getSessionData();
		final BaseObject request = query.getParameters();
		String back = Base.getString(request, "back", "").trim();
		if (back.length() == 0 || back.endsWith("login.user")) {
			back = Base.getString(session, "loginBack", "").trim();
			if (back.length() == 0 || back.endsWith("login.user")) {
				back = Base.getString(query.getAttributes(), "Referer", "/");
			}
		}
		final boolean attempt = Base.getString(request, "login", "").length() > 0 || Base.getString(request, "__auth_type", "").length() > 0;
		if (attempt) {
			try {
				/** there was invalidateAuth() but we'd like to keep the userId
				 *
				 * TODO make context.prepareAuth something */
				context.setSessionState(AuthLevels.AL_UNAUTHORIZED);
				// context.invalidateAuth();
				server.ensureAuthorization(AuthLevels.AL_AUTHORIZED_NORMAL);
				return Reply.redirect(
						CommandUser.OWNER, //
						query,
						false,
						server.fixUrl(back))//
						.setPrivate()//
						.setNoCaching();
			} catch (final AbstractReplyException f) {
				/** Any non login related response must be forwarded (and likely to be an error by
				 * the way) */
				if (f.getCode() != Reply.CD_DENIED && f.getCode() != Reply.CD_UNAUTHORIZED) {
					return f.getReply().setPrivate();
				}
			}
		}
		return Reply.object(
				CommandUser.OWNER, //
				query,
				new BaseNativeObject()//
						.putAppend("template", "401")//
						.putAppend("back", back)//
						.putAppend(
								"error", //
								/** no need to check state */
								attempt
									? session.baseGet("loginError", BaseObject.UNDEFINED)
									: BaseObject.UNDEFINED //
						)//
		)//
				.setCode(
						useSystemAuth
							? Reply.CD_UNAUTHORIZED
							: Reply.CD_OK)//
				.setTitle("Authentication")//
				.setContentID("ru.myx.srv.acm.CommandUser")//
				.setNoCaching()//
				.setFlags(flags)//
				.setPrivate();
	}
	
	private static final ReplyAnswer doRegister(final Server server, final ServeRequest query, final ExecProcess process, final BaseObject flags) {
		
		final BaseObject rd = query.getParameters();
		final String login = Base.getString(rd, "login", "").trim().toLowerCase();
		if (login.length() > 0) {
			final String email = Base.getString(rd, "email", "").trim().toLowerCase();
			final String password = UserManagerSAPI.generatePassword();
			try {
				NodeUM.registerUser(Context.getUserId(process), login, email, password, rd);
				flags.baseDefine("Scheduled", BaseObject.TRUE);
			} catch (final IllegalArgumentException e) {
				final String message = e.getMessage();
				if (message.startsWith("+")) {
					flags.baseDefine("Error", "errors: " + message.substring(1));
					flags.baseDefine("Errors", Base.forArray(message.substring(1).split(",")));
				} else {
					flags.baseDefine("Error", message);
				}
			} catch (final Exception e) {
				flags.baseDefine("Error", e.getMessage());
			}
		}
		final Object result;
		try {
			result = server.createRenderer("UMAN:register.user", NodeUM.getTemplateFor("register.user"))//
					.callNE0(process, BaseObject.UNDEFINED);
		} catch (final AbstractReplyException e) {
			return e.getReply().setContentID("mwmRegistration").setNoCaching().setPrivate();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		return Reply.object(
				CommandUser.OWNER, //
				query,
				result)//
				.setTitle("Registration")//
				.setContentID("mwmRegistration")//
				.setTimeToLiveHours(1)//
				.setNoCaching()//
				.setFlags(flags)//
				.setPrivate();
	}
	
	private static final ReplyAnswer doSettings(final Server server, final ServeRequest query, final BaseObject flags) {
		
		server.ensureAuthorization(AuthLevels.AL_AUTHORIZED_NORMAL);
		final ExecProcess process = Exec.currentProcess();
		final AccessUser<?> user = Context.getUser(process);
		final BaseObject rd = query.getParameters();
		if (rd != null) {
			final String password = Base.getString(rd, "password", "");
			if (password.length() > 0) {
				try {
					final String login = user.getLogin();
					final AccessUser<?> userCheck = Access.getUserByLoginCheckPassword(server.getAccessManager(), login, password, PasswordType.NORMAL);
					if (userCheck == null || !userCheck.getKey().equals(user.getKey())) {
						throw new RuntimeException("Wrong password!");
					}
					CommandUser.updateUser(user, rd);
					flags.baseDefine("Done", BaseObject.TRUE);
				} catch (final IllegalArgumentException e) {
					final String message = e.getMessage();
					if (message.startsWith("+")) {
						flags.baseDefine("Error", "errors: " + message.substring(1));
						flags.baseDefine("Errors", Base.forArray(message.substring(1).split(",")));
					} else {
						flags.baseDefine("Error", message);
					}
				} catch (final Exception e) {
					flags.baseDefine("Error", e.getMessage());
				}
			}
		}
		final Object result;
		try {
			result = server.createRenderer("UMAN:settings.user", NodeUM.getTemplateFor("settings.user"))//
					.callNE0(process, BaseObject.UNDEFINED);
		} catch (final AbstractReplyException e) {
			return e.getReply().setContentID("mwmUserSettings").setNoCaching().setPrivate();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		return Reply.object(
				CommandUser.OWNER, //
				query,
				result)//
				.setTitle("Settings")//
				.setContentID("mwmUserSettings")//
				.setNoCaching()//
				.setFlags(flags)//
				.setPrivate();
	}
	
	private static final ReplyAnswer doUserAction(final Server server, final ServeRequest query) {
		
		final BaseObject queryParameters = query.getParameters();
		final String actionID = Base.getString(queryParameters, "action", "").trim();
		if (actionID.length() == 0) {
			return Reply.string(CommandUser.OWNER, query, "'action' argument is not specified!").setCode(Reply.CD_UNKNOWN);
		}
		final BaseObject action = server.getStorage().load(actionID);
		assert action != null : "NULL java value";
		if (action == BaseObject.UNDEFINED) {
			return Reply.string(
					CommandUser.OWNER, //
					query,
					"'action' argument is wrong or obsolete!").setCode(Reply.CD_UNKNOWN);
		}
		final String typeName = Base.getString(action, "actionType", "").trim();
		if (typeName.length() == 0) {
			return Reply.string(
					CommandUser.OWNER, //
					query,
					"ActionType (" + typeName + ") is unknown!").setCode(Reply.CD_UNKNOWN);
		}
		final Handler runner = CommandUser.defaultUserActionRunner;
		if (runner == null) {
			return Reply.string(
					CommandUser.OWNER, //
					query,
					"ActionType (" + typeName + ") is unknown or not registered!").setCode(Reply.CD_UNKNOWN);
		}
		for (final Iterator<String> iterator = Base.keys(action); iterator.hasNext();) {
			final String key = iterator.next();
			/** setParameter exactly, don't want to have random arrays */
			query.setParameter(key, action.baseGet(key, BaseObject.UNDEFINED));
		}
		return runner.onQuery(query);
	}
	
	private static final void updateUser(final AccessUser<?> user, final BaseObject data) throws Exception {
		
		final BaseObject sourceData = new BaseNativeObject();
		final String language;
		if (data != null) {
			language = Base.getString(data, "language", Context.getLanguage(Exec.currentProcess()).getName());
			for (final Iterator<String> iterator = Base.keys(data); iterator.hasNext();) {
				final String key = iterator.next();
				if (key.startsWith("reg")) {
					sourceData.baseDefine(key.substring(3), data.baseGet(key, BaseObject.UNDEFINED));
				}
			}
		} else {
			language = Context.getLanguage(Exec.currentProcess()).getName();
		}
		final BaseObject registrationData = new BaseNativeObject();
		final ControlFieldset<?> cfd = NodeUM.getCommonFieldsDefinition();
		final Map<String, String> validation = cfd.dataValidate(sourceData);
		if (validation != null && validation.size() > 0) {
			throw new IllegalArgumentException("+" + Text.join(validation.keySet(), ","));
		}
		cfd.dataStore(sourceData, registrationData);
		user.setLanguage(language);
		user.setProfile(registrationData);
		user.commit();
	}
	
	public static final ReplyAnswer handleRequest(final Server server, final RuntimeEnvironment rt, final boolean useSystemAuth, final ServeRequest query) {
		
		final String path = query.getResourceIdentifier();
		if (path.endsWith(".user")) {
			final ExecProcess process = Exec.currentProcess();
			final BaseObject flags = Context.getFlags(process);
			final int pos = path.lastIndexOf('/');
			final String request = path.substring(
					pos == -1
						? 0
						: pos + 1, //
					path.length() - ".user".length());
			if (request.equals("login")) {
				return CommandUser.doLogin(server, useSystemAuth, query, process, flags);
			}
			if (request.endsWith("logout")) {
				Context.invalidateUser(process);
				return Respond.getBackRedirect(server, query).setNoCaching().setPrivate();
			}
			if (request.equals("register")) {
				return CommandUser.doRegister(server, query, process, flags);
			}
			if (request.equals("settings")) {
				return CommandUser.doSettings(server, query, flags);
			}
			if (request.equals("change-password")) {
				return CommandUser.doChangePassword(server, query, flags);
			}
			if (request.equals("forget-password")) {
				return CommandUser.doForgetPassword(server, rt.getEmailSender(), query, flags);
			}
			if (request.equals("change-email")) {
				return CommandUser.doChangeEmail(server, rt.getEmailSender(), query, flags);
			}
			if (request.equals("user-action")) {
				return CommandUser.doUserAction(server, query);
			}
		}
		return null;
	}
}
