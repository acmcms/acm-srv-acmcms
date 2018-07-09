/*
 * Created on 15.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.ControlLookupStatic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.help.Text;

/**
 * @author myx
 * 		
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormUmSettings extends AbstractForm<FormUmSettings> {
	
	private static final ControlLookupStatic LOOKUP_REGISTRATION_MODES = new ControlLookupStatic()
			.putAppend("0", MultivariantString.getString("Disabled", Collections.singletonMap("ru", "Запрещена")))
			.putAppend("1", MultivariantString.getString("Paused", Collections.singletonMap("ru", "Приостановлена")))
			.putAppend("10", MultivariantString.getString("Enabled", Collections.singletonMap("ru", "Разрешена")));
			
	private static final ControlLookupStatic LOOKUP_PERIOD = new ControlLookupStatic()
			.putAppend("0", MultivariantString.getString("Disabled", Collections.singletonMap("ru", "Запрещено")))
			.putAppend("1", MultivariantString.getString("1 month", Collections.singletonMap("ru", "1 месяц")))
			.putAppend("2", MultivariantString.getString("2 months", Collections.singletonMap("ru", "2 месяца")))
			.putAppend("3", MultivariantString.getString("3 months", Collections.singletonMap("ru", "3 месяца")))
			.putAppend("6", MultivariantString.getString("6 months", Collections.singletonMap("ru", "6 месяцев")))
			.putAppend("12", MultivariantString.getString("1 year", Collections.singletonMap("ru", "1 год")));
			
	private static final ControlFieldset<?> FIELDSET = ControlFieldset.createFieldset("Setup")
			.addField(
					ControlFieldFactory
							.createFieldInteger("onlineRegistration", MultivariantString.getString("Online registration", Collections.singletonMap("ru", "Регистрация on-line")), 0)
							.setFieldType("select").setAttribute("lookup", FormUmSettings.LOOKUP_REGISTRATION_MODES))
			.addField(
					ControlFieldFactory.createFieldString("robotEmail", MultivariantString.getString("Robot email", Collections.singletonMap("ru", "E-mail системы")), "1@1.1")
							.setFieldVariant("email"))
			.addField(
					ControlFieldFactory
							.createFieldInteger("neverLogged", MultivariantString.getString("Never logged accounts", Collections.singletonMap("ru", "Никогда не входившие")), 1)
							.setFieldType("select").setAttribute("lookup", FormUmSettings.LOOKUP_PERIOD))
			.addField(
					ControlFieldFactory
							.createFieldInteger("notLogged", MultivariantString.getString("Accounts, not logged for", Collections.singletonMap("ru", "Не входившие в течении")), 12)
							.setFieldType("select").setAttribute("lookup", FormUmSettings.LOOKUP_PERIOD))
			.addField(
					ControlFieldFactory.createFieldSet(
							"notifyUserList",
							MultivariantString.getString("Notify on registration & promote user", Collections.singletonMap("ru", "Нотификация регистрации и промоушена")),
							null).setFieldVariant("select").setAttribute("lookup", Access.HM_USERS));
							
	private static final ControlCommand<?> CMD_SAVE = Control.createCommand("save", " OK ").setCommandPermission("setup").setCommandIcon("command-save");
	
	FormUmSettings() {
		final BaseObject source = Context.getServer(Exec.currentProcess()).getStorage().load("umSettings");
		assert source != null : "NULL java value";
		FormUmSettings.FIELDSET.dataRetrieve(source, this.getData());
		this.setAttributeIntern("id", "settings");
		this.setAttributeIntern("title", MultivariantString.getString("Settings", Collections.singletonMap("ru", "Настройки")));
		this.setAttributeIntern("path", "/usman");
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		
		if (command == FormUmSettings.CMD_SAVE) {
			final BaseArray requested = Convert.MapEntry.toCollection(this.getData(), "notifyUserList", null);
			final List<String> emails = new ArrayList<>();
			final Server server = Context.getServer(Exec.currentProcess());
			if (requested != null) {
				final int length = requested.length();
				for (int i = 0; i < length; ++i) {
					final String id = requested.baseGet(i, BaseObject.UNDEFINED).baseToJavaString();
					final AccessUser<?> user = server.getAccessManager().getUser(id, false);
					if (user == null) {
						continue;
					}
					final String email = user.getEmail();
					if (email == null) {
						continue;
					}
					emails.add(email);
				}
			}
			this.getData().baseDefine("notifyEmailList", Text.join(emails, ";"));
			final BaseObject settings = server.getStorage().load("umSettings");
			assert settings != null : "NULL java value";
			if (settings == BaseObject.UNDEFINED) {
				server.getStorage().savePersistent("umSettings", this.getData());
			} else {
				FormUmSettings.FIELDSET.dataStore(this.getData(), settings);
				server.getStorage().savePersistent("umSettings", settings);
			}
			return null;
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {
		
		return Control.createOptionsSingleton(FormUmSettings.CMD_SAVE);
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return FormUmSettings.FIELDSET;
	}
}
