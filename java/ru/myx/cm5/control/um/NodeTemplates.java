package ru.myx.cm5.control.um;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.control.AbstractNode;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.access.AccessPermissions;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Format;

/**
 * Title: Base Implementations Description: Copyright: Copyright (c) 2001
 * Company: -= MyX =-
 *
 * @author Alexander I. Kharitchev
 * @version 1.0
 */
final class NodeTemplates extends AbstractNode {
	
	private static final List<String> TemplateIDs = Arrays.asList(new String[]{
			"login.user", "register.user", "settings.user", "change-password.user", "forget-password.user", "change-email.user", "registration-complete.eml", "changed-email.eml",
			"forgotten-password.eml", "account-scheduled.eml", "to-a-manager.eml", "promo-to-a-manager.eml", "group-added.eml", "group-removed.eml",
	});

	static ControlFieldset<?> FIELDSET_LISTING = ControlFieldset.createFieldset("TemplatesListingDefinition").addFields(new ControlField[]{
			ControlFieldFactory.createFieldString("key", MultivariantString.getString("Name", Collections.singletonMap("ru", "Имя")), ""),
			ControlFieldFactory.createFieldString("description", MultivariantString.getString("Description", Collections.singletonMap("ru", "Описание")), "").setFieldType("text"),
	});

	private static final Object NODE_TITLE = MultivariantString.getString("Templates", Collections.singletonMap("ru", "Шаблоны"));

	static final BaseObject M_LOGIN_ERROR = MultivariantString
			.getString("ERROR: Wrong username or password!", Collections.singletonMap("ru", "ОШИБКА: Неправильное имя пользователя или пароль!"));

	static final BaseObject M_REGISTRATION_SUCCEED_SUBJECT = MultivariantString.getString("Registration succeed.", Collections.singletonMap("ru", "Регистрация прошла успешно"));

	static final BaseObject M_GROUP_ADD_SUBJECT = MultivariantString.getString("A group was added.", Collections.singletonMap("ru", "Членство в группе добавлено"));

	static final BaseObject M_GROUP_REMOVE_SUBJECT = MultivariantString.getString("A group was removed.", Collections.singletonMap("ru", "Членство в группе аннулировано"));

	static final BaseObject M_REGISTRATION_LOGIN_NUNIQUE = MultivariantString.getString("Login is not unique.", Collections.singletonMap("ru", "Такой логин уже зарегистрирован"));

	static final BaseObject M_REGISTRATION_EMAIL_NUNIQUE = MultivariantString
			.getString("E-mail is not unique.", Collections.singletonMap("ru", "Такой e-mail уже зарегистрирован"));

	static final BaseObject M_REGISTRATION_EMAIL_INVALID = MultivariantString
			.getString("No valid email address found.", Collections.singletonMap("ru", "Не верный почтовый адрес"));

	static final BaseObject M_PASSWORD_RESTORE_SUBJECT = MultivariantString
			.getString("Forgotten password restore.", Collections.singletonMap("ru", "Восстановление забытого пароля"));

	static final BaseObject M_WRONG_PASSWORD = MultivariantString.getString("Wrong current password specified!", Collections.singletonMap("ru", "Неправильный пароль"));

	static final BaseObject M_WRONG_PASSWORD_LENGTH = MultivariantString
			.getString("Password length shouldn't be less than 6 characters!", Collections.singletonMap("ru", "Пароль должен быть не менее 6 символов длинной"));

	static final BaseObject M_PASSWORDS_NOT_SAME = MultivariantString.getString("Passwords are not the same!", Collections.singletonMap("ru", "Пароли должны быть одинаковыми!"));

	static final String getDefaultTemplateFor(final String name) {
		
		final InputStream stream = NodeTemplates.class.getResourceAsStream("templates/" + name + ".htm");
		if (stream == null) {
			throw new IllegalArgumentException("Unknown template name: " + name);
		}
		try {
			return Transfer.createBuffer(stream).toString(StandardCharsets.UTF_8);
		} catch (final Throwable e) {
			return "<%RETURN: { title:'Unknown form', body:'error retrieving template: " + Format.Ecma.string(e.getMessage() + "!") + "'}%>";
		}
	}

	static final BaseObject getDescriptionFor(final String name) {
		
		if (name.equals("login.user")) {
			return MultivariantString.getString("Page: authorization while browsing the site.", Collections.singletonMap("ru", "Страница авторизации на сайте."));
		}
		if (name.equals("register.user")) {
			return MultivariantString.getString("Page: registration while browsing the site.", Collections.singletonMap("ru", "Страница регистрации на сайте."));
		}
		if (name.equals("settings.user")) {
			return MultivariantString.getString("Page: user settings while browsing the site.", Collections.singletonMap("ru", "Страница настроек пользователя на сайте."));
		}
		if (name.equals("change-password.user")) {
			return MultivariantString.getString("Page: change password while browsing the site.", Collections.singletonMap("ru", "Страница смены пароля на сайте."));
		}
		if (name.equals("forget-password.user")) {
			return MultivariantString
					.getString("Page: change forgotten password while browsing the site.", Collections.singletonMap("ru", "Страница смены забытого пароля на сайте."));
		}
		if (name.equals("change-email.user")) {
			return MultivariantString.getString("Page: change email while browsing the site.", Collections.singletonMap("ru", "Страница смены адреса e-mail на сайте."));
		}
		if (name.equals("registration-complete.eml")) {
			return MultivariantString.getString("Email: registration approval.", Collections.singletonMap("ru", "Письмо: подтверждение регистрации."));
		}
		if (name.equals("changed-email.eml")) {
			return MultivariantString.getString("Email: changed email validation.", Collections.singletonMap("ru", "Письмо: проверка нового адреса e-mail."));
		}
		if (name.equals("forgotten-password.eml")) {
			return MultivariantString.getString("Email: change forgotted password request.", Collections.singletonMap("ru", "Письмо: запрос о смене забытого пароля."));
		}
		if (name.equals("account-scheduled.eml")) {
			return MultivariantString
					.getString("Email: notification prior account removal.", Collections.singletonMap("ru", "Письмо: нотификация о грядущем удалении пользователя."));
		}
		if (name.equals("to-a-manager.eml")) {
			return MultivariantString.getString(
					"Email: manager's notification 'bout new user registration.",
					Collections.singletonMap("ru", "Письмо: нотификация менеджера о регистрации пользователя."));
		}
		if (name.equals("promo-to-a-manager.eml")) {
			return MultivariantString.getString(
					"Email: manager's notification 'bout new user promotion.",
					Collections.singletonMap("ru", "Письмо: нотификация менеджера о промоушене пользователя."));
		}
		if (name.equals("group-added.eml")) {
			return MultivariantString
					.getString("Email: notification about group membership addition.", Collections.singletonMap("ru", "Письмо: нотификация о добавлении в группу."));
		}
		if (name.equals("group-removed.eml")) {
			return MultivariantString
					.getString("Email: notification about group membership removal.", Collections.singletonMap("ru", "Письмо: нотификация об удалении из группы."));
		}
		return Base.forString("unknown");
	}

	static final String getHelpFor(final String name) {
		
		if (name.equals("login.user")) {
			return "Flags.backTo contains an url to return after succesful login.\n" + "Flags.Error contains an error description if an error occured while logging in.\n"
					+ "Login and Password values should be named as 'login' and 'password' respectively.";
		}
		if (name.equals("register.user")) {
			return "Flags.Scheduled used to indicate successful registration - an email is sent to a newly registered user with password in it.\n"
					+ "Flags.Paused used to indicate that registration is paused right now.\n"
					+ "Flags.Errors contains an array of field identifiers with errors if multiple errors were occured while trying to register.\n"
					+ "Flags.Error contains an error description if an error occured while trying to register.\n"
					+ "Login and Email values should be named as 'login' and 'email' respectively. "
					+ "All fields specified in 'Common User Fields' may be used in registration form with 'reg' prefix.";
		}
		if (name.equals("settings.user")) {
			return "Flags.Done used to indicate successful settings update.\n"
					+ "Flags.Errors contains an array of field identifiers with errors if multiple errors were occured while trying to register.\n"
					+ "Flags.Error contains an error description if an error occured while trying to update user settings.\n"
					+ "Password value should be named as 'password' field and is required for validation of submitted data! "
					+ "All fields specified in 'Common User Fields' may be used in settings update form with 'reg' prefix.";
		}
		if (name.equals("change-email.user")) {
			return "Flags.Scheduled used to indicate successful email change schedule - an email is sent to a newly specified address and waiting for validation.\n"
					+ "Flags.ValidTill specifies how long the system will wait for validation.\n"
					+ "Flags.Error contains an error description if an error occured while trying to change an email address.\n"
					+ "Flags.Done indicates that user has successfully finished email change procedure.\n"
					+ "Current Password and New Email values should be named as 'password' and 'email' respectively.";
		}
		if (name.equals("change-password.user")) {
			return "Flags.PasswordChanged used to indicate successful password change.\n"
					+ "Flags.Error contains an error description if an error occured while trying to change a password.\n"
					+ "Current Password value should be named as 'password' field, New Password and New Password Check values should be named as 'newpass' and 'newpass2' respectively.";
		}
		if (name.equals("forget-password.user")) {
			return "Flags.Scheduled used to indicate that an email is sent to a specified address and waiting for validation.\n"
					+ "Flags.ValidTill specifies how long the system will wait for validation.\n" + "Flags.PasswordChanged used to indicate successful password change.\n"
					+ "Flags.Error contains an error description if an error occured while trying to change a password.\n"
					+ "Flags.Form equals to 'email' while waiting en email address input and 'password' while waiting for a new password.\n"
					+ "Email, New Password and New Password Check values should be named as 'email', 'newpass' and 'newpass2' respectively.";
		}
		if (name.equals("registration-complete.eml")) {
			return "Flags.login contains login name, Flags.password contains newly generated password.\n";
		}
		if (name.equals("changed-email.eml")) {
			return "Flags.login contains user login.\n" + "Flags.url contains activation url.\n";
		}
		if (name.equals("forgotten-password.eml")) {
			return "Flags.login contains user login.\n" + "Flags.url contains activation url.\n";
		}
		if (name.equals("to-a-manager.eml")) {
			return "Flags.login contains login name.\n" + "Flags.email contains email address.\n" + "Flags.userid contains user id";
		}
		if (name.equals("promo-to-a-manager.eml")) {
			return "Flags.login contains login name.\n" + "Flags.email contains email address.\n" + "Flags.userid contains user id.\n"
					+ "Flags.registrarID contains registrar userid.\n" + "Flags.registrarName contains registrar name.\n"
					+ "Flags.registrarEmail contains registrar email address.";
		}
		if (name.equals("group-added.eml")) {
			return "Flags.grourName contains group name.\n" + "Flags.groupDescription contains group description.\n" + "Flags.group contains group id.\n"
					+ "Flags.userid contains user id.\n" + "Flags.login contains user login.\n"
					+ "Flags.password contains password when it's generated for directory synchronization.";
		}
		if (name.equals("group-removed.eml")) {
			return "Flags.groupName contains group name.\n" + "Flags.groupDescription contains group description.\n" + "Flags.group contains group id.\n"
					+ "Flags.userid contains user id";
		}
		return "unknown";
	}

	@Override
	public AccessPermissions getCommandPermissions() {
		
		return Access.createPermissionsLocal()
				.addPermission("view", MultivariantString.getString("View templates properties", Collections.singletonMap("ru", "Просмотр свойств шаблонов")))
				.addPermission("modify", MultivariantString.getString("Modify templates properties", Collections.singletonMap("ru", "Изменение свойств шаблонов")));
	}

	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if ("open".equals(command.getKey())) {
			final String key = Base.getString(command.getAttributes(), "key", null);
			return new FormEditTemplate(key);
		}
		if ("preview".equals(command.getKey())) {
			final String key = Base.getString(command.getAttributes(), "key", null);
			try {
				final URL result = new URL("http://" + Context.getRequest(Exec.currentProcess()).getTargetExact() + '/' + key);
				return result;
			} catch (final MalformedURLException e) {
				return e;
			}
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}

	@Override
	public ControlCommandset getContentCommands(final String key) {
		
		final ControlCommandset result = Control.createOptions();
		result.add(Control.createCommand("open", MultivariantString.getString("Properties", Collections.singletonMap("ru", "Свойства"))).setAttribute("key", key));
		if (key.endsWith("user")) {
			result.add(Control.createCommand("preview", MultivariantString.getString("Preview", Collections.singletonMap("ru", "Просмотр"))).setAttribute("key", key));
		}
		return result;
	}

	@Override
	public ControlFieldset<?> getContentFieldset() {
		
		return NodeTemplates.FIELDSET_LISTING;
	}

	@Override
	public List<ControlBasic<?>> getContents() {
		
		final List<ControlBasic<?>> result = new ArrayList<>();
		for (final String templateName : NodeTemplates.TemplateIDs) {
			final BaseObject templateTitle = NodeTemplates.getDescriptionFor(templateName);
			final BaseObject data = new BaseNativeObject()//
					.putAppend("key", templateName)//
					.putAppend("description", templateTitle)//
			;
			result.add(Control.createBasic(templateName, templateTitle, data));
		}
		return result;
	}

	@Override
	public String getKey() {
		
		return "templates";
	}

	@Override
	public String getTitle() {
		
		return NodeTemplates.NODE_TITLE.toString();
	}
}
