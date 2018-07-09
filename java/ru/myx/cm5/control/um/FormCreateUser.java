/*
 * Created on 13.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessGroup;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.PasswordType;
import ru.myx.ae1.access.UserTypes;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Convert;

/**
 * @author myx
 * 		
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormCreateUser extends AbstractForm<FormCreateUser> {
	
	private static final ControlFieldset<?> FIELDSET_NEW_USER;
	
	private static final ControlCommand<?> CMD_REGISTER = Control
			.createCommand("register", MultivariantString.getString("Register!", Collections.singletonMap("ru", "Зарегистрировать"))).setCommandPermission("control")
			.setCommandIcon("command-create-user");
			
	private static final ControlCommand<?> CMD_NEXT = Control.createCommand("next", MultivariantString.getString("Next...", Collections.singletonMap("ru", "Далее...")))
			.setCommandPermission("control").setCommandIcon("command-next");
			
	static {
		FIELDSET_NEW_USER = ControlFieldset.createFieldset("new_user")//
				.addField(ControlFieldFactory.createFieldGuid(
						"id", //
						MultivariantString.getString(
								"UserID", //
								Collections.singletonMap(
										"ru", //
										"ID польз.")))
						.setConstant())//
				.addField(ControlFieldFactory.createFieldString(
						"login", //
						MultivariantString.getString(
								"Login", //
								Collections.singletonMap("ru", "Логин")),
						MultivariantString.getString(
								" -= unknown =-", //
								Collections.singletonMap(
										"ru", //
										"-= неизвестен =-"))))//
				.addField(ControlFieldFactory.createFieldString(
						"name", //
						MultivariantString.getString(
								"Name", //
								Collections.singletonMap("ru", "Имя")),
						MultivariantString.getString(
								" -= unnamed =-", //
								Collections.singletonMap(
										"ru", //
										"-= безымянный =-"))))//
				.addField(ControlFieldFactory.createFieldString(
						"email", //
						"E-mail",
						MultivariantString.getString(
								" -= unknown =-", //
								Collections.singletonMap(
										"ru", //
										"-= неизвестен =-"))))//
				.addField(
						ControlFieldFactory
								.createFieldString(
										"language", //
										MultivariantString.getString(
												"Language", //
												Collections.singletonMap(
														"ru", //
														"Язык")),
										"en")//
								.setFieldType("select")//
								.setAttribute("lookup", ru.myx.ae1.know.Know.SYSTEM_LANGUAGES))//
				.addField(ControlFieldFactory.createFieldString(
						"password", //
						MultivariantString.getString(
								"Password", //
								Collections.singletonMap(
										"ru", //
										"Пароль")),
						"").setFieldVariant("password"))//
				.addField(
						ControlFieldFactory
								.createFieldString(
										"passwordHigh", //
										MultivariantString.getString(
												"Password high", //
												Collections.singletonMap(
														"ru", //
														"Пароль (high)")),
										"")//
								.setFieldVariant("password"))//
				.addField(
						ControlFieldFactory
								.createFieldSet(
										"groups", //
										MultivariantString.getString(
												"Group membership", //
												Collections.singletonMap(
														"ru", //
														"Членство в группах")),
										null)//
								.setFieldVariant("select")//
								.setAttribute("lookup", Access.GROUPS));
	}
	
	private final AccessUser<?> user;
	
	private final BaseObject commonFields;
	
	private final String path;
	
	FormCreateUser(final String path) {
		this.path = path;
		this.user = Context.getServer(Exec.currentProcess()).getAccessManager().getUser(Engine.createGuid(), true);
		this.commonFields = this.user.getProfile();
		this.setAttributeIntern("id", "new_user");
		this.setAttributeIntern("title", MultivariantString.getString("Create user", Collections.singletonMap("ru", "Создание пользователя")));
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	FormCreateUser(final String path, final AccessUser<?> user, final BaseObject data, final BaseObject commonFields) {
		this.path = path;
		this.user = user;
		this.commonFields = commonFields;
		this.setData(data);
		this.setAttributeIntern("id", "new_user");
		this.setAttributeIntern("title", MultivariantString.getString("Create user", Collections.singletonMap("ru", "Создание пользователя")));
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormCreateUser.CMD_REGISTER) {
			final BaseObject data = this.getData();
			final BaseObject commonFields = this.commonFields;
			this.user.setLogin(Base.getString(data, "login", null));
			this.user.setEmail(Base.getString(data, "email", null));
			this.user.setLanguage(Base.getString(data, "language", null));
			this.user.setType(UserTypes.UT_HANDMADE);
			commonFields.baseDefine("name", Base.getString(data, "name", null));
			this.user.setProfile(commonFields);
			final AccessManager manager = Context.getServer(Exec.currentProcess()).getAccessManager();
			final String password = Base.getString(data, "password", "").trim();
			if (password.length() > 0) {
				manager.setPassword(this.user, password, PasswordType.NORMAL);
			}
			final String passwordHigh = Base.getString(data, "passwordHigh", "").trim();
			if (passwordHigh.length() > 0) {
				manager.setPassword(this.user, passwordHigh, PasswordType.HIGHER);
			}
			manager.commitUser(this.user);
			final Set<AccessGroup<?>> groups = new HashSet<>();
			{
				final BaseArray set = Convert.MapEntry.toCollection(data, "groups", null);
				if (set != null) {
					final int length = set.length();
					for (int i = 0; i < length; ++i) {
						final String id = set.baseGet(i, BaseObject.UNDEFINED).baseToJavaString();
						final AccessGroup<?> group = manager.getGroup(id, false);
						if (group == null) {
							continue;
						}
						groups.add(group);
					}
				}
			}
			manager.setGroups(this.user, groups.toArray(new AccessGroup<?>[groups.size()]));
			return null;
		}
		if (command == FormCreateUser.CMD_NEXT) {
			return new FormCreateUserCommonFields(this.path, this.user, this.getData(), this.commonFields);
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		return Control.createOptionsSingleton(NodeUM.getCommonFieldsDefinition().isEmpty()
			? FormCreateUser.CMD_REGISTER
			: FormCreateUser.CMD_NEXT);
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return FormCreateUser.FIELDSET_NEW_USER;
	}
}
