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
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseNativeObject;
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
final class FormEditUser extends AbstractForm<FormEditUser> {
	
	private static final ControlFieldset<?> FIELDSET_USER;
	
	private static final ControlCommand<?> CMD_SAVE = Control
			.createCommand(
					"save", //
					MultivariantString.getString(
							"Save", //
							Collections.singletonMap(
									"ru", //
									"Сохранить")))//
			.setCommandPermission("modify")//
			.setCommandIcon("command-save")//
			;
			
	private static final ControlCommand<?> CMD_NEXT = Control
			.createCommand(
					"next", //
					MultivariantString.getString(
							"Next...", //
							Collections.singletonMap(
									"ru", //
									"Далее...")))//
			.setCommandPermission("modify")//
			.setCommandIcon("command-next")//
			;
			
	static {
		FIELDSET_USER = ControlFieldset.createFieldset("edit_user")//
				.addField(
						ControlFieldFactory
								.createFieldGuid(
										"id", //
										MultivariantString.getString(
												"UserID", //
												Collections.singletonMap(
														"ru", //
														"ID польз.")))//
								.setConstant())//
				.addField(
						ControlFieldFactory
								.createFieldDate(
										"added", //
										MultivariantString.getString(
												"Added", //
												Collections.singletonMap(
														"ru", //
														"Зарегистрирован")),
										0L)//
								.setConstant())//
				.addField(
						ControlFieldFactory
								.createFieldDate(
										"lastLogin", //
										MultivariantString.getString(
												"Logged", //
												Collections.singletonMap(
														"ru", //
														"Посл. вход")),
										0L)//
								.setConstant())//
				.addField(
						ControlFieldFactory
								.createFieldInteger(
										"account", //
										"Account",
										0)//
								.setConstant())//
				.addField(ControlFieldFactory.createFieldString(
						"login", //
						MultivariantString.getString(
								"Login", //
								Collections.singletonMap(
										"ru", //
										"Логин")),
						MultivariantString.getString(
								" -= unknown =-", //
								Collections.singletonMap(
										"ru", //
										"-= неизвестен =-"))))//
				.addField(ControlFieldFactory.createFieldString(
						"name", //
						MultivariantString.getString(
								"Name", //
								Collections.singletonMap(
										"ru", //
										"Имя")),
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
				.addField(
						ControlFieldFactory
								.createFieldString(
										"password", //
										MultivariantString.getString(
												"Password", //
												Collections.singletonMap(
														"ru", //
														"Пароль")),
										"")//
								.setFieldVariant("password")//
								.setFieldHint(MultivariantString.getString(
										"no change if empty", //
										Collections.singletonMap(
												"ru", //
												"не меняется если пусто"))))//
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
								.setFieldVariant("password")//
								.setFieldHint(MultivariantString.getString(
										"no change if empty", //
										Collections.singletonMap(
												"ru", //
												"не меняется если пусто"))))//
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
	
	static final void commitChanges(final AccessUser<?> user, final BaseObject data, final BaseObject commonFields) {
		
		user.setLogin(Base.getString(data, "login", null));
		user.setEmail(Base.getString(data, "email", null));
		user.setLanguage(Base.getString(data, "language", null));
		user.setType(UserTypes.UT_HANDMADE);
		commonFields.baseDefine("name", Base.getString(data, "name", null));
		user.setProfile(commonFields);
		final AccessManager manager = Context.getServer(Exec.currentProcess()).getAccessManager();
		final String password = Base.getString(data, "password", "").trim();
		if (password.length() > 0) {
			manager.setPassword(user, password, PasswordType.NORMAL);
		}
		final String passwordHigh = Base.getString(data, "passwordHigh", "").trim();
		if (passwordHigh.length() > 0) {
			manager.setPassword(user, passwordHigh, PasswordType.HIGHER);
		}
		manager.commitUser(user);
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
		manager.setGroups(user, groups.toArray(new AccessGroup<?>[groups.size()]));
	}
	
	private final AccessUser<?> user;
	
	private final BaseObject commonFields;
	
	private final String path;
	
	FormEditUser(final String path, final AccessUser<?> user, final BaseObject data, final BaseObject commonFields) {
		this.path = path;
		this.commonFields = commonFields;
		this.user = user;
		this.setData(data);
		this.setAttributeIntern("id", "edit_user");
		this.setAttributeIntern("title", MultivariantString.getString("Edit user", Collections.singletonMap("ru", "Редактирование пользователя")));
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	FormEditUser(final String path, final String key) {
		this.path = path;
		final AccessManager manager = Context.getServer(Exec.currentProcess()).getAccessManager();
		this.user = manager.getUser(key, true);
		this.commonFields = this.user.getProfile();
		final BaseObject data = new BaseNativeObject()//
				.putAppend("id", key)//
				.putAppend("added", Base.forDateMillis(this.user.getCreated()))//
				.putAppend("lastLogin", Base.forDateMillis(this.user.getChanged()))//
				.putAppend("login", this.user.getLogin())//
				.putAppend("name", this.commonFields.baseGet("name", BaseObject.UNDEFINED))//
				.putAppend("email", this.user.getEmail())//
				.putAppend("language", this.user.getLanguage())//
				;
		final AccessGroup<?>[] groups = manager.getGroups(this.user);
		if (groups != null && groups.length > 0) {
			final String[] groupIDs = new String[groups.length];
			for (int i = groups.length - 1; i >= 0; --i) {
				groupIDs[i] = groups[i].getKey();
			}
			data.baseDefine("groups", Base.forArray(groupIDs));
		} else {
			data.baseDefine("groups", BaseObject.UNDEFINED);
		}
		this.setData(data);
		this.setAttributeIntern("id", "edit_user");
		this.setAttributeIntern(
				"title", //
				MultivariantString.getString(
						"Edit user", //
						Collections.singletonMap("ru", "Редактирование пользователя")));
		this.setAttributeIntern("path", path);
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormEditUser.CMD_SAVE) {
			FormEditUser.commitChanges(this.user, this.getData(), this.commonFields);
			return null;
		}
		if (command == FormEditUser.CMD_NEXT) {
			return new FormEditUserCommonFields(this.path, this.user, this.getData(), this.commonFields);
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		final ControlCommandset result = Control.createOptions();
		if (!NodeUM.getCommonFieldsDefinition().isEmpty()) {
			result.add(FormEditUser.CMD_NEXT);
		}
		result.add(FormEditUser.CMD_SAVE);
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return FormEditUser.FIELDSET_USER;
	}
}
