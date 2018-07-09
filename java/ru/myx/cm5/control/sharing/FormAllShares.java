/*
 * Created on 10.06.2004
 */
package ru.myx.cm5.control.sharing;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseFunctionActAbstract;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/**
 * @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FormAllShares extends AbstractForm<FormAllShares> {

	private static final ControlFieldset<?> FIELDSET_SHARE_LIST = ControlFieldset.createFieldset()
			.addField(ControlFieldFactory.createFieldString("alias", MultivariantString.getString("Alias", Collections.singletonMap("ru", "Имя")), "").setConstant())
			.addField(ControlFieldFactory.createFieldString("path", MultivariantString.getString("Path", Collections.singletonMap("ru", "Путь")), "").setConstant())
			.addField(
					ControlFieldFactory.createFieldString("authType", MultivariantString.getString("Authorization type", Collections.singletonMap("ru", "Тип авторизации")), "")
							.setConstant().setFieldType("select").setAttribute("lookup", Sharing.LOOKUP_AUTH_TYPE))
			.addField(
					ControlFieldFactory.createFieldString("accessType", MultivariantString.getString("Access type", Collections.singletonMap("ru", "Тип доступа")), "")
							.setConstant().setFieldType("select").setAttribute("lookup", Sharing.LOOKUP_ACCESS_TYPE))
			.addField(
					ControlFieldFactory.createFieldString("secureType", MultivariantString.getString("Security", Collections.singletonMap("ru", "Безопасность")), "").setConstant()
							.setFieldType("select").setAttribute("lookup", Sharing.LOOKUP_SECURE_TYPE))
			.addField(
					ControlFieldFactory.createFieldString("skinner", MultivariantString.getString("Skinner", Collections.singletonMap("ru", "Скиннер")), "").setConstant()
							.setFieldType("select").setAttribute("lookup", Sharing.SKINNER_SELECTION))
			.addField(
					ControlFieldFactory.createFieldString("languageMode", MultivariantString.getString("Language", Collections.singletonMap("ru", "Язык")), "").setConstant()
							.setFieldType("select").setAttribute("lookup", Sharing.LOOKUP_LANGUAGE_MODE));

	private final Server server;

	private final ControlFieldset<?> fieldset;

	private final ShareListing shareList;

	private static final ControlCommand<?> CMD_SAVE = Control.createCommand("ok", " OK ").setCommandPermission("$modify_sharing").setCommandIcon("command-save-ok");

	private static final ControlCommand<?> CMD_APPLY = Control.createCommand("apply", MultivariantString.getString("Apply", Collections.singletonMap("ru", "Применить")))
			.setCommandPermission("$modify_sharing").setCommandIcon("command-apply");

	/**
	 * @param server
	 *
	 */
	public FormAllShares(final Server server) {
		this.server = server;
		this.setAttributeIntern("id", "sharing");
		this.setAttributeIntern("title", MultivariantString.getString("All access points", Collections.singletonMap("ru", "Все точки публичного доступа")));
		this.recalculate();
		this.shareList = new ShareListing(Sharing.getSharings(server));
		final ContainerAllShares container = new ContainerAllShares(this.shareList);
		this.fieldset = ControlFieldset.createFieldset().addField(
				Control.createFieldList("shares", MultivariantString.getString("Access points", Collections.singletonMap("ru", "Точки доступа")), null)
						.setFieldHint(
								MultivariantString.getString(
										"NOTE: alias should be registered in DNS servers to be accessible by clients.",
										Collections.singletonMap(
												"ru",
												"ВНИМАНИЕ: чтобы указанные имена были доступны пользователям сайта они должны быть зарегистрированны на сервере DNS.")))
						.setAttribute("content_fieldset", FormAllShares.FIELDSET_SHARE_LIST)
						.setAttribute("content_handler", new BaseFunctionActAbstract<Void, ContainerAllShares>(Void.class, ContainerAllShares.class) {
							
							@Override
							public ContainerAllShares apply(final Void listing) {

								return container;
							}
						}));
	}

	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {

		if (command == FormAllShares.CMD_SAVE || command == FormAllShares.CMD_APPLY) {
			Sharing.commitSharing(this.server, this.shareList);
			if (command == FormAllShares.CMD_APPLY) {
				this.shareList.replace(Sharing.getSharings(this.server));
				return this;
			}
			return null;
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}

	@Override
	public ControlCommandset getCommands() {

		final ControlCommandset result = Control.createOptions();
		result.add(FormAllShares.CMD_SAVE);
		result.add(FormAllShares.CMD_APPLY);
		return result;
	}

	@Override
	public BaseObject getData() {

		return new BaseNativeObject("shares", Base.forArray(this.shareList.getListing()));
	}

	@Override
	public ControlFieldset<?> getFieldset() {

		return this.fieldset;
	}
}
