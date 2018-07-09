package ru.myx.cm5.control.um;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import ru.myx.ae1.BaseRT3;
import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.PasswordType;
import ru.myx.ae1.access.SortMode;
import ru.myx.ae1.access.UserTypes;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.ControlNode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.Engine;
import ru.myx.ae3.access.AccessPermissions;
import java.util.function.Function;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.email.Email;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;
import ru.myx.ae3.exec.ProgramPart;
import ru.myx.ae3.help.Text;
import ru.myx.sapi.UserManagerSAPI;

/** Title: Base Implementations Description: Copyright: Copyright (c) 2001 Company: -= MyX =-
 *
 * @author Alexander I. Kharitchev
 * @version 1.0 */
public class NodeUM extends AbstractNode {

	private static final BaseObject CC_STR_USER_FIELDS = MultivariantString.getString("Common user fields", Collections.singletonMap("ru", "Общие поля пользователя"));
	
	private static final Object nodeTitle = MultivariantString.getString("User Management", Collections.singletonMap("ru", "Управление пользователями"));
	
	private static final ControlCommand<?> CMD_CREATE_USER = Control
			.createCommand("new_user", MultivariantString.getString("Add new user...", Collections.singletonMap("ru", "Новый пользователь..."))).setCommandPermission("create")
			.setCommandIcon("command-create-user");
	
	private static final ControlCommand<?> CMD_CREATE_GROUP = Control
			.createCommand("new_group", MultivariantString.getString("Add new group...", Collections.singletonMap("ru", "Новая группа..."))).setCommandPermission("create")
			.setCommandIcon("command-create-group");
	
	private static final ControlCommand<?> CMD_UM_SEARCH = Control
			.createCommand("search", MultivariantString.getString("Search users", Collections.singletonMap("ru", "Поиск пользователей"))).setCommandPermission("view")
			.setCommandIcon("command-search-users");
	
	private static final ControlCommand<?> CMD_UM_USER_FIELDS = Control.createCommand("cuf", NodeUM.CC_STR_USER_FIELDS).setCommandPermission("setup")
			.setCommandIcon("command-edit");
	
	private static final ControlCommand<?> CMD_UM_SETTINGS = Control
			.createCommand("settings", MultivariantString.getString("User management settings", Collections.singletonMap("ru", "Настройки управления пользователями")))
			.setCommandPermission("setup").setCommandIcon("command-setup");
	
	private static final ControlCommand<?> CMD_UM_GET_CSV = Control
			.createCommand("get_csv", MultivariantString.getString("Download user database", Collections.singletonMap("ru", "Выгрузить базу пользователей")))
			.setCommandPermission("view").setCommandIcon("command-download-csv");
	
	private static final ControlCommandset NODE_COMMANDS = Control.createOptions();
	
	static {
		NodeUM.NODE_COMMANDS.addAll(Arrays.asList(new ControlCommand<?>[]{
				NodeUM.CMD_CREATE_USER, NodeUM.CMD_CREATE_GROUP, NodeUM.CMD_UM_SEARCH, NodeUM.CMD_UM_GET_CSV, NodeUM.CMD_UM_USER_FIELDS, NodeUM.CMD_UM_SETTINGS,
		}));
	}

	/** @param length
	 * @param smallLetters
	 * @param bigLetters
	 * @return string */
	public static final String generatePassword(final int length, final boolean smallLetters, final boolean bigLetters) {

		return UserManagerSAPI.generatePassword(length, smallLetters, bigLetters);
	}

	/** @return fieldset */
	public static final ControlFieldset<?> getCommonFieldsDefinition() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-commonFields.def");
		assert data != null : "NULL java value";
		String definition = "<fieldset class=\"fieldsetgroup\" />";
		if (data != BaseObject.UNDEFINED && null != data.baseGetOwnProperty("dd")) {
			definition = Base.getString(data, "dd", definition);
		}
		return ControlFieldset.materializeFieldset(definition);
	}

	/** @param name
	 * @return template */
	public static final String getDefaultTemplateFor(final String name) {

		return NodeTemplates.getDefaultTemplateFor(name);
	}

	/** @return string */
	public static final String getMessageLoginError() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-login.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageLoginError")) {
			return NodeTemplates.M_LOGIN_ERROR.toString();
		}
		return data.baseGet("messageLoginError", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	public static final String getMessagePasswordRestoreSubject() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-forget-password.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageRestoreSubject")) {
			return NodeTemplates.M_PASSWORD_RESTORE_SUBJECT.toString();
		}
		return data.baseGet("messageRestoreSubject", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	public static final String getMessagePasswordsNotSame() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-change-password.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messagePasswordsNotSame")) {
			return NodeTemplates.M_PASSWORDS_NOT_SAME.toString();
		}
		return data.baseGet("messagePasswordsNotSame", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	public static final String getMessageRegistrationEmailInvalid() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-register.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageRegistrationEI")) {
			return NodeTemplates.M_REGISTRATION_EMAIL_INVALID.toString();
		}
		return data.baseGet("messageRegistrationEI", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	public static final String getMessageRegistrationEmailNotUnique() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-register.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageRegistrationENU")) {
			return NodeTemplates.M_REGISTRATION_EMAIL_NUNIQUE.toString();
		}
		return data.baseGet("messageRegistrationENU", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	public static final String getMessageRegistrationLoginNotUnique() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-register.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageRegistrationLNU")) {
			return NodeTemplates.M_REGISTRATION_LOGIN_NUNIQUE.toString();
		}
		return data.baseGet("messageRegistrationLNU", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	static final String getMessageRegistrationSucceedSubject() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-register.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageRegistrationSucceedSubject")) {
			return NodeTemplates.M_REGISTRATION_SUCCEED_SUBJECT.toString();
		}
		return data.baseGet("messageRegistrationSucceedSubject", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	public static final String getMessageWrongPassword() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-change-password.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageWrongPassword")) {
			return NodeTemplates.M_WRONG_PASSWORD.toString();
		}
		return data.baseGet("messageWrongPassword", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	public static final String getMessageWrongPasswordLength() {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-change-password.user");
		assert data != null : "NULL java value";
		if (null == data.baseGetOwnProperty("messageWrongPasswordLength")) {
			return NodeTemplates.M_WRONG_PASSWORD_LENGTH.toString();
		}
		return data.baseGet("messageWrongPasswordLength", BaseObject.UNDEFINED).toString();
	}

	/** @return string */
	static final String getRegistrationNotificationEmailList() {

		final BaseObject m = Context.getServer(Exec.currentProcess()).getStorage().load("umSettings");
		assert m != null : "NULL java value";
		return Base.getString(m, "notifyEmailList", "");
	}

	/** @param name
	 * @return string */
	public static final String getTemplateFor(final String name) {

		final BaseObject data = Context.getServer(Exec.currentProcess()).getStorage().load("um-" + name);
		String result;
		assert data != null : "NULL java value";
		if (data != BaseObject.UNDEFINED && (result = Base.getString(data, "template", null)) != null) {
			return result;
		}
		return NodeUM.getDefaultTemplateFor(name);
	}

	/** @return string */
	public static final String getUserManagerEmailAddress() {

		final BaseObject m = Context.getServer(Exec.currentProcess()).getStorage().load("umSettings");
		assert m != null : "NULL java value";
		return Base.getString(m, "robotEmail", "1@1.1");
	}

	/** @param defaultId
	 * @param login
	 * @param email
	 * @param password
	 * @param data
	 * @return userId
	 * @throws Exception */
	public static String registerUser(final String defaultId, final String login, final String email, final String password, final BaseObject data) throws Exception {

		if (email.length() < 3 || email.indexOf('@') == -1 || email.indexOf('.') == -1) {
			throw new IllegalArgumentException(NodeUM.getMessageRegistrationEmailInvalid());
		}
		final ExecProcess process = Exec.currentProcess();
		final Server server = Context.getServer(process);
		final BaseObject sourceData = new BaseNativeObject();
		final String name;
		final String language;
		if (data != null) {
			name = Base.getString(data, "name", "").trim();
			language = Base.getString(data, "language", Context.getLanguage(process).getName());
			for (final Iterator<String> iterator = Base.keys(data); iterator.hasNext();) {
				final String key = iterator.next();
				if (key.startsWith("reg")) {
					sourceData.baseDefine(key.substring(3), data.baseGet(key, BaseObject.UNDEFINED));
				}
			}
		} else {
			name = "";
			language = "";
		}
		final BaseObject registrationData = new BaseNativeObject();
		final ControlFieldset<?> cfd = NodeUM.getCommonFieldsDefinition();
		final Map<String, String> validation = cfd.dataValidate(sourceData);
		if (validation != null && validation.size() > 0) {
			throw new IllegalArgumentException("+" + Text.join(validation.keySet(), ","));
		}
		cfd.dataStore(sourceData, registrationData);
		final AccessManager accessManager = server.getAccessManager();
		{
			final AccessUser<?>[] users = accessManager.search(login, null, -1, -1, SortMode.SM_LOGIN);
			for (final AccessUser<?> user : users) {
				if (user == null) {
					continue;
				}
				if (user.getLogin() != null && user.getLogin().equals(login)) {
					throw new IllegalArgumentException(NodeUM.getMessageRegistrationLoginNotUnique());
				}
			}
		}
		{
			final AccessUser<?>[] users = accessManager.search(null, email, -1, -1, SortMode.SM_LOGIN);
			for (final AccessUser<?> user : users) {
				if (user == null) {
					continue;
				}
				if (user.getEmail() != null && user.getEmail().equals(email)) {
					throw new IllegalArgumentException(NodeUM.getMessageRegistrationEmailNotUnique());
				}
			}
		}
		final String currentUserId = defaultId;
		final String targetUserId;
		final AccessUser<?> uo;
		if (currentUserId == null || currentUserId.trim().length() == 0) {
			targetUserId = Engine.createGuid();
			uo = accessManager.getUser(targetUserId, true);
		} else {
			final AccessUser<?> userCheck = accessManager.getUser(currentUserId, true);
			if (userCheck.isInGroup("def.registered") || !userCheck.isAnonymous()) {
				targetUserId = Engine.createGuid();
				uo = accessManager.getUser(targetUserId, true);
			} else {
				targetUserId = currentUserId;
				uo = userCheck;
			}
		}
		uo.setLogin(login);
		uo.setEmail(email);
		uo.setLanguage(language);
		uo.setType(UserTypes.UT_HALF_REGISTERED);
		if (name.length() > 0) {
			registrationData.baseDefine("Name", name);
		}
		uo.setProfile(registrationData);
		accessManager.setPassword(uo, password, PasswordType.NORMAL);
		accessManager.setPassword(uo, password, PasswordType.HIGHER);
		accessManager.commitUser(uo);
		// //////////////////////////////////////////////////////////////////////
		NodeUM.sendRegistrationEmail(email, login, password);
		String emailSubject;
		String emailBody;
		final String emails = NodeUM.getRegistrationNotificationEmailList();
		if (emails != null && emails.trim().length() > 0) {
			try {
				Context.getFlags(process).baseDefine("userid", targetUserId);
				Context.getFlags(process).baseDefine("email", email);
				final String templateName;
				templateName = "to-a-manager.eml";
				emailSubject = "A new user was registered.";
				final String template = NodeUM.getTemplateFor(templateName);
				final ProgramPart renderer = server.createRenderer("UMAN:" + templateName, template);
				emailBody = renderer.callSE0(process, BaseObject.UNDEFINED);
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
			BaseRT3.runtime(process).getEmailSender().sendEmail(new Email(NodeUM.getUserManagerEmailAddress(), emails, emailSubject, emailBody));
		}
		return targetUserId;
	}

	/** @param userId */
	public static void registerUserRetryEmail(final String userId) {

		final Server server = Context.getServer(Exec.currentProcess());
		final String password = NodeUM.generatePassword(8, true, true);
		final AccessUser<?> user = server.getAccessManager().getUser(userId, true);
		server.getAccessManager().setPassword(user, password, PasswordType.NORMAL);
		server.getAccessManager().setPassword(user, password, PasswordType.HIGHER);
		server.getAccessManager().commitUser(user);
		// //////////////////////////////////////////////////////////////////////
		NodeUM.sendRegistrationEmail(user.getEmail(), user.getLogin(), password);
	}

	static void sendRegistrationEmail(final String email, final String login, final String password) {

		String emailSubject = NodeUM.getMessageRegistrationSucceedSubject();
		String emailBody = "Your login: " + login + "\r\nYour password: " + password;
		final ExecProcess process = Exec.currentProcess();
		try {
			Context.getFlags(process).baseDefine("login", login);
			Context.getFlags(process).baseDefine("password", password);
			final String template = NodeUM.getTemplateFor("registration-complete.eml");
			final ProgramPart renderer = Context.getServer(Exec.currentProcess()).createRenderer("UMAN:registration-complete.eml", template);
			emailBody = renderer.callSE0(process, BaseObject.UNDEFINED);
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
		BaseRT3.runtime(process).getEmailSender().sendEmail(new Email(NodeUM.getUserManagerEmailAddress(), email, emailSubject, emailBody));
	}

	static void storeCommonFieldsDefinition(final String source) {

		final Server server = Context.getServer(Exec.currentProcess());
		BaseObject data = server.getStorage().load("um-commonFields.def");
		assert data != null : "NULL java value";
		if (data == BaseObject.UNDEFINED) {
			data = new BaseNativeObject();
		}
		final BaseObject object = data;
		object.baseDefine("dd", source);
		server.getStorage().savePersistent("um-commonFields.def", data);
	}
	
	private final NodeTemplates TemplatesContext = new NodeTemplates();
	
	private final NodeGroups GroupsContext = new NodeGroups();
	
	private final NodeUsers UsersContext = new NodeUsers();
	
	private final NodeUsers UserAContext = new NodeUsersAlmost();

	@Override
	public AccessPermissions getCommandPermissions() {

		return Access.createPermissionsLocal()
				.addPermission("setup", MultivariantString.getString("Setup user management", Collections.singletonMap("ru", "Настраивать систему управления пользователями")))
				.addPermission("view", MultivariantString.getString("View group or user data", Collections.singletonMap("ru", "Просматривать свойства пользователя или группы")))
				.addPermission("create", MultivariantString.getString("Create groups and users", Collections.singletonMap("ru", "Создавать пользователей и группы")))
				.addPermission("modify", MultivariantString.getString("Edit groups and users", Collections.singletonMap("ru", "Редактировать пользователей и группы")))
				.addPermission("delete", MultivariantString.getString("Delete groups and users", Collections.singletonMap(
						"ru",
						"Удалять пользователей и группы")));
	}

	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {

		if (command == NodeUM.CMD_CREATE_USER) {
			return new FormCreateUser("/usman");
		}
		if (command == NodeUM.CMD_CREATE_GROUP) {
			return Context.getServer(Exec.currentProcess()).getAccessManager().createFormGroupCreation("/usman");
		}
		if (command == NodeUM.CMD_UM_USER_FIELDS) {
			return Control.createFieldsetEditorForm(
					NodeUM.CC_STR_USER_FIELDS, //
					NodeUM.getCommonFieldsDefinition(),
					new Function<ControlFieldset<?>, Object>() {
						
						@Override
						public Object apply(final ControlFieldset<?> fieldset) {
							
							final String serialized = ControlFieldset.serializeFieldset(fieldset, false);
							NodeUM.storeCommonFieldsDefinition(serialized);
							return null;
						}
					});
		}
		if (command == NodeUM.CMD_UM_SETTINGS) {
			return new FormUmSettings();
		}
		if (command == NodeUM.CMD_UM_SEARCH) {
			return Context.getServer(Exec.currentProcess()).getAccessManager().createFormUserSearch(null, this.UsersContext);
		}
		if (command == NodeUM.CMD_UM_GET_CSV) {
			return new FormUmCsvSetup();
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}

	@Override
	public ControlCommandset getCommands() {

		return NodeUM.NODE_COMMANDS;
	}

	@Override
	public String getIcon() {

		return "container-users";
	}

	@Override
	public String getKey() {

		return "usman";
	}

	@Override
	public String getTitle() {

		return NodeUM.nodeTitle.toString();
	}

	@Override
	protected ControlNode<?>[] internGetChildren() {

		return new ControlNode<?>[]{
				this.GroupsContext, this.UsersContext, this.UserAContext, this.TemplatesContext
		};
	}
}
