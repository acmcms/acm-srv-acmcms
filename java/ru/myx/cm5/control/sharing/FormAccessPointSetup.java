/*
 * Created on 08.06.2004
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
public class FormAccessPointSetup extends AbstractForm<FormAccessPointSetup> {

	private static final ControlFieldset<?> FIELDSET_SHARE_LIST = ControlFieldset.createFieldset()
			.addField(ControlFieldFactory.createFieldString("alias", MultivariantString.getString("Alias", Collections.singletonMap("ru", "Имя")), "").setConstant())
			.addField(
					ControlFieldFactory.createFieldString("authType", MultivariantString.getString("Authorization type", Collections.singletonMap("ru", "Тип авторизации")), "")
							.setConstant().setFieldType("select").setAttribute("lookup", Sharing.LOOKUP_AUTH_TYPE))
			.addField(
					ControlFieldFactory.createFieldString("secureType", MultivariantString.getString("Security", Collections.singletonMap("ru", "Безопасность")), "").setConstant()
							.setFieldType("select").setAttribute("lookup", Sharing.LOOKUP_SECURE_TYPE))
			.addField(
					ControlFieldFactory.createFieldString("skinner", MultivariantString.getString("Skinner", Collections.singletonMap("ru", "Скиннер")), "").setConstant()
							.setFieldType("select").setAttribute("lookup", Sharing.SKINNER_SELECTION))
			.addField(
					ControlFieldFactory.createFieldString("languageMode", MultivariantString.getString("Language", Collections.singletonMap("ru", "Язык")), "").setConstant()
							.setFieldType("select").setAttribute("lookup", Sharing.LOOKUP_LANGUAGE_MODE));
	
	private final ControlFieldset<?> fieldset;
	
	private final Server server;
	
	private final String path;
	
	private final ShareListing shareList;
	
	private static final ControlCommand<?> CMD_SAVE = Control.createCommand("ok", " OK ").setCommandPermission("$modify_sharing").setCommandIcon("command-save-ok");
	
	private static final ControlCommand<?> CMD_APPLY = Control.createCommand("apply", MultivariantString.getString("Apply", Collections.singletonMap("ru", "Применить")))
			.setCommandPermission("$modify_sharing").setCommandIcon("command-apply");
	
	private static final ControlCommand<?> CMD_REMOVE = Control.createCommand("remove", MultivariantString.getString("Remove", Collections.singletonMap("ru", "Удалить")))
			.setCommandPermission("$modify_sharing").setCommandIcon("command-dispose");
	
	/**
	 * @param server
	 * @param path
	 */
	public FormAccessPointSetup(final Server server, final String path) {
		this.server = server;
		this.path = path;
		this.setAttributeIntern("id", "sharing");
		this.setAttributeIntern("title", MultivariantString.getString("Public access point settings", Collections.singletonMap("ru", "Настройки публичного доступа")));
		this.setAttributeIntern("path", path);
		this.recalculate();
		this.shareList = new ShareListing(Sharing.getShareSetupFor(server, path));
		final ContainerSharing container = new ContainerSharing(path, this.shareList);
		this.fieldset = ControlFieldset.createFieldset().addField(
				ControlFieldFactory.createFieldString("path", MultivariantString.getString("Current path", Collections.singletonMap("ru", "Текущий путь")), path).setConstant())
				.addField(
						Control.createFieldList("shares", MultivariantString.getString("Access points", Collections.singletonMap("ru", "Точки доступа")), null)
								.setFieldHint(
										MultivariantString.getString(
												"Sub-tree of current node will be accessible via aliases specified here. Use domain names as aliases.\r\nNOTE: alias should be registered in DNS servers to be accessible by clients.",
												Collections.singletonMap(
														"ru",
														"Начинающаяся в данном узле ветка дерева будет доступна по именам, указанным в этой форме. В качестве имен точек доступа используйте имена доменов.\r\nВНИМАНИЕ: чтобы указанные имена были доступны пользователям сайта они должны быть зарегистрированны на сервере DNS.")))
								.setAttribute("content_fieldset", FormAccessPointSetup.FIELDSET_SHARE_LIST)
								.setAttribute("content_handler", new BaseFunctionActAbstract<Void, ContainerSharing>(Void.class, ContainerSharing.class) {
									
									@Override
									public ContainerSharing apply(final Void argument) {

										return container;
									}
								}));
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {

		if (command == FormAccessPointSetup.CMD_SAVE || command == FormAccessPointSetup.CMD_APPLY) {
			Sharing.commitSharing(this.server, this.shareList);
			if (command == FormAccessPointSetup.CMD_APPLY) {
				this.shareList.replace(Sharing.getShareSetupFor(this.server, this.path));
				return this;
			}
			return null;
		}
		if (command == FormAccessPointSetup.CMD_REMOVE) {
			this.shareList.clear();
			Sharing.commitSharing(this.server, this.shareList);
			return null;
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}
	
	@Override
	public ControlCommandset getCommands() {

		final ControlCommandset result = Control.createOptions();
		result.add(FormAccessPointSetup.CMD_SAVE);
		result.add(FormAccessPointSetup.CMD_APPLY);
		result.add(FormAccessPointSetup.CMD_REMOVE);
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
