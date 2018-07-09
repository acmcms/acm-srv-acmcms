/*
 * Created on 16.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.um;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.SortMode;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseMessage;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.flow.Flow;
import ru.myx.ae3.help.Convert;
import ru.myx.util.xls.ExcelSAPI;
import ru.myx.util.xls.WorkbookChange;
import ru.myx.util.xls.WorkbookSheetChange;

/**
 * @author myx
 * 		
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormUmCsvDownload extends AbstractForm<FormUmCsvDownload> {
	
	private static final ControlFieldset<?> FIELDSET = ControlFieldset.createFieldset("download")
			.addField(ControlFieldFactory.createFieldBinary("file", MultivariantString.getString("File", Collections.singletonMap("ru", "Файл")), Integer.MAX_VALUE).setConstant());
			
	FormUmCsvDownload(final BaseObject data) throws Exception {
		final char delim;
		final String fileName;
		final String contentType;
		final Collection<String> groups = new ArrayList<>();
		{
			final BaseArray groupsArray = Convert.MapEntry.toCollection(data, "groups", null);
			if (groupsArray != null) {
				final int length = groupsArray.length();
				for (int i = 0; i < length; ++i) {
					groups.add(groupsArray.baseGet(i, BaseObject.UNDEFINED).baseToJavaString());
				}
			}
		}
		switch (Convert.MapEntry.toInt(data, "type", 0)) {
			case 0 :
				delim = ',';
				fileName = "users.txt";
				contentType = "text/plain";
				break;
			case 1 :
				delim = ',';
				fileName = "users.csv";
				contentType = "text/comma-separated-values";
				break;
			case 2 :
				delim = '\t';
				fileName = "users.csv";
				contentType = "text/tab-separated-values";
				break;
			default :
				delim = 0;
				fileName = "users.xls";
				contentType = "application/msexcel";
		}
		final boolean sendUserIDs = Convert.MapEntry.toBoolean(data, "userid", false);
		final boolean commonFields = Convert.MapEntry.toBoolean(data, "common", true);
		final ControlFieldset<?> commonFieldsDef = commonFields
			? NodeUM.getCommonFieldsDefinition()
			: null;
		final int baseFieldCount;
		final List<String> headers = new ArrayList<>();
		{
			if (sendUserIDs) {
				headers.add("UserID");
			}
			headers.add("Login");
			headers.add("Email");
			headers.add("Language");
			headers.add("Added");
			headers.add("Changed");
			baseFieldCount = headers.size();
			if (commonFieldsDef != null) {
				for (int i = 0; i < commonFieldsDef.size(); ++i) {
					final ControlField cf = commonFieldsDef.get(i);
					final String title = cf.getTitle();
					headers.add(title.indexOf(delim) == -1
						? title
						: cf.getKey());
				}
			}
		}
		final BaseMessage resultMessage;
		final AccessManager manager = Context.getServer(Exec.currentProcess()).getAccessManager();
		if (delim == 0) {
			final WorkbookChange workbook = ExcelSAPI.createWorkbook();
			final WorkbookSheetChange sheet = workbook.createSheet("Users");
			int rowIndex = 0;
			int cellIndex = 0;
			if (Convert.MapEntry.toBoolean(data, "headers", true)) {
				for (int j = 0; j < headers.size(); j++) {
					sheet.setCellLabel(cellIndex++, rowIndex, headers.get(j), null);
				}
				rowIndex++;
				cellIndex = 0;
			}
			final AccessUser<?>[] IDs = manager.searchByMembership(groups, SortMode.SM_LOGIN);
			for (final AccessUser<?> element : IDs) {
				final AccessUser<?> user = manager.getUser(element.getKey(), true);
				if (sendUserIDs) {
					sheet.setCellLabel(cellIndex++, rowIndex, user.getKey(), null);
				}
				sheet.setCellLabel(cellIndex++, rowIndex, user.getLogin(), null);
				sheet.setCellLabel(cellIndex++, rowIndex, user.getEmail(), null);
				sheet.setCellLabel(cellIndex++, rowIndex, user.getLanguage(), null);
				sheet.setCellDate(cellIndex++, rowIndex, user.getCreated(), "yyyy.MM.dd", null);
				sheet.setCellDate(cellIndex++, rowIndex, user.getChanged(), "yyyy.MM.dd", null);
				final BaseObject profileDef = user.getProfile();
				if (commonFieldsDef != null) {
					for (int j = baseFieldCount; j < headers.size(); j++) {
						final ControlField cf = commonFieldsDef.get(j - baseFieldCount);
						final BaseObject o = profileDef.baseGet(cf.getKey(), BaseObject.UNDEFINED);
						sheet.setCellLabel(cellIndex++, rowIndex, o == null
							? "-"
							: o.toString(), null);
					}
				}
				rowIndex++;
				cellIndex = 0;
			}
			final BaseObject attributes = new BaseNativeObject()//
					.putAppend("Content-Type", contentType)//
					.putAppend("Content-Disposition", "attachment; filename=" + fileName)//
					;
			resultMessage = Flow.binary("UMC", "Users", attributes, workbook.buildWorkbook());
		} else {
			final StringBuilder result = new StringBuilder(2048);
			{
				if (Convert.MapEntry.toBoolean(data, "headers", true)) {
					for (int j = 0; j < headers.size(); j++) {
						if (j > 0) {
							result.append(delim);
						}
						result.append(headers.get(j));
					}
					result.append("\r\n");
				}
				final AccessUser<?>[] IDs = manager.searchByMembership(groups, SortMode.SM_LOGIN);
				for (final AccessUser<?> element : IDs) {
					final AccessUser<?> user = manager.getUser(element.getKey(), true);
					if (sendUserIDs) {
						result.append(user.getKey());
						result.append(delim);
					}
					result.append(user.getLogin());
					result.append(delim);
					result.append(user.getEmail());
					result.append(delim);
					result.append(user.getLanguage());
					result.append(delim);
					result.append(user.getCreated());
					result.append(delim);
					result.append(user.getChanged());
					final BaseObject profileDef = user.getProfile();
					if (commonFieldsDef != null) {
						for (int j = baseFieldCount; j < headers.size(); j++) {
							result.append(delim);
							final ControlField cf = commonFieldsDef.get(j - baseFieldCount);
							final BaseObject o = profileDef.baseGet(cf.getKey(), BaseObject.UNDEFINED);
							result.append(o == null
								? "-"
								: delim == ','
									? o.toString().replace(',', '|')
									: o.toString());
						}
					}
					result.append("\r\n");
				}
			}
			final BaseObject attributes = new BaseNativeObject()//
					.putAppend("Content-Type", contentType)//
					.putAppend("Content-Disposition", "attachment; filename=" + fileName)//
					;
			resultMessage = Flow.character("UMC", "Users", attributes, result.toString());
		}
		final BaseObject map = new BaseNativeObject()//
				.putAppend("file", resultMessage)//
				;
		this.setData(map);
		this.setAttributeIntern("id", "csv_download");
		this.setAttributeIntern("title", MultivariantString.getString("Download CSV with user database", Collections.singletonMap("ru", "Выгрузка базы пользователей")));
		this.setAttributeIntern("path", "/usman");
		this.recalculate();
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		
		return FormUmCsvDownload.FIELDSET;
	}
}
