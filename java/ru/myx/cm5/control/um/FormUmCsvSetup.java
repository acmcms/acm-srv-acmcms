package ru.myx.cm5.control.um;

import java.util.Collections;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.base.BaseHostLookup;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.ControlLookupStatic;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/*
 * Created on 16.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormUmCsvSetup extends AbstractForm<FormUmCsvSetup> {

	private static final BaseHostLookup DATATYPES = new ControlLookupStatic()//
			.putAppend("0", "text/plain (users.txt)")//
			.putAppend("1", "text/comma-separated-values (users.csv)")//
			.putAppend("2", "text/tab-separated-values (users.csv)")//
			.putAppend("3", "application/ms-excel (users.xls)")//
	;

	private static final ControlFieldset<?> FIELDSET = ControlFieldset.createFieldset("dl_csv_setup")//
			.addField(ControlFieldFactory.createFieldInteger(//
					"type", //
					MultivariantString.getString(//
							"ContentType", //
							Collections.singletonMap("ru", "Тип файла")//
					), //
					0)//
					.setFieldType("select")//
					.setAttribute("lookup", FormUmCsvSetup.DATATYPES)//
			)//
			.addField(ControlFieldFactory.createFieldBoolean(//
					"headers", //
					MultivariantString.getString(//
							"Include column headers", //
							Collections.singletonMap("ru", "Включить заголовки столбцов")//
					), //
					true)//
			)//
			.addField(ControlFieldFactory.createFieldBoolean(//
					"userid", //
					MultivariantString.getString(//
							"Include UserID column", //
							Collections.singletonMap("ru", "Включить стобец с идентификатором пользователя")//
					), //
					false)//
			)//
			.addField(ControlFieldFactory.createFieldBoolean(//
					"common", //
					MultivariantString.getString(//
							"Include common user fields", //
							Collections.singletonMap("ru", "Включить общие поля пользователей")//
					), //
					true)//
			)//
			.addField(ControlFieldFactory.createFieldSet(//
					"groups", //
					MultivariantString.getString(//
							"Group membership", //
							Collections.singletonMap("ru", "Членство в группах")//
					), //
					null)//
					.setFieldVariant("select")//
					.setAttribute("lookup", Access.GROUPS)//
	);

	private static final ControlCommand<?> CMD_NEXT = Control.createCommand(//
			"next", //
			MultivariantString.getString(//
					"Next...", //
					Collections.singletonMap("ru", "Далее...")//
			))//
			.setCommandPermission("view")//
			.setCommandIcon("command-next")//
	;

	FormUmCsvSetup() {
		this.setAttributeIntern("id", "dl_csv_setup");
		this.setAttributeIntern(
				"title",
				MultivariantString.getString(//
						"Download CSV with user database", //
						Collections.singletonMap("ru", "Выгрузка базы пользователей")//
				));
		this.setAttributeIntern("path", "/usman");
		this.recalculate();
	}

	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {

		if (command == FormUmCsvSetup.CMD_NEXT) {
			try {
				return new FormUmCsvDownload(this.getData());
			} catch (final RuntimeException e) {
				throw e;
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
		throw new IllegalArgumentException("Unknown command: " + command.getKey());
	}

	@Override
	public ControlCommandset getCommands() {

		return Control.createOptionsSingleton(FormUmCsvSetup.CMD_NEXT);
	}

	@Override
	public ControlFieldset<?> getFieldset() {

		return FormUmCsvSetup.FIELDSET;
	}
}
