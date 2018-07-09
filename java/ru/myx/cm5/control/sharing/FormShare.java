/*
 * Created on 04.10.2004
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ru.myx.cm5.control.sharing;

import java.util.Collections;

import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.sharing.AccessType;
import ru.myx.ae1.sharing.AuthType;
import ru.myx.ae1.sharing.SecureType;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;

/**
 * @author myx
 */
abstract class FormShare extends AbstractForm<FormShare> {
	private static final BaseObject	STR_CPATH		= MultivariantString.getString( "Current path",
																Collections.singletonMap( "ru", "Текущий путь" ) );
	
	private static final BaseObject	STR_ALIAS		= MultivariantString.getString( "Alias",
																Collections.singletonMap( "ru", "Имя" ) );
	
	private static final BaseObject	STR_ALIAS_HINT	= MultivariantString
																.getString( "Sub-tree of current node will be accessible by this alias. Use domain names as aliases.\r\nNOTE: alias should be registered in DNS servers to be accessible by clients.",
																		Collections
																				.singletonMap( "ru",
																						"Начинающаяся в данном узле ветка дерева будет доступна по указанному в этом поле имени. В качестве имен точек доступа используйте имена доменов.\r\nВНИМАНИЕ: чтобы указанные имена были доступны пользователям сайта они должны быть зарегистрированны на сервере DNS." ) );
	
	private static final BaseObject	STR_ATYPE		= MultivariantString.getString( "Access type",
																Collections.singletonMap( "ru", "Тип доступа" ) );
	
	private static final BaseObject	STR_LTYPE		= MultivariantString.getString( "Authorization type",
																Collections.singletonMap( "ru", "Тип авторизации" ) );
	
	private static final BaseObject	STR_STYPE		= MultivariantString.getString( "Security",
																Collections.singletonMap( "ru", "Безопасность" ) );
	
	private static final BaseObject	STR_LMODE		= MultivariantString.getString( "Language mode",
																Collections.singletonMap( "ru", "Языковой режим" ) );
	
	private static final BaseObject	STR_SNAME		= MultivariantString.getString( "Skinner",
																Collections.singletonMap( "ru", "Скиннер" ) );
	
	private static final BaseObject	STR_CMODE		= MultivariantString
																.getString( "Enable system command execution",
																		Collections
																				.singletonMap( "ru",
																						"Разрешить выполнение системных команд" ) );
	
	protected static final ControlFieldset<?> createFieldset(final String path) {
		final ControlFieldset<?> result = ControlFieldset.createFieldset();
		result.addField( ControlFieldFactory.createFieldString( "path", FormShare.STR_CPATH, path ).setConstant() );
		result.addField( ControlFieldFactory.createFieldString( "alias", FormShare.STR_ALIAS, "", 1, 255 )
				.setFieldHint( FormShare.STR_ALIAS_HINT ) );
		result.addField( ControlFieldFactory.createFieldBoolean( "commandMode", FormShare.STR_CMODE, false ) );
		result.addField( ControlFieldFactory.createFieldString( "languageMode", FormShare.STR_LMODE, "*" ).setFieldType( "select" )
				.setAttribute( "lookup", Sharing.LOOKUP_LANGUAGE_MODE ) );
		result.addField( ControlFieldFactory.createFieldString( "skinner", FormShare.STR_SNAME, "*" ).setFieldType( "select" )
				.setAttribute( "lookup", Sharing.SKINNER_SELECTION ) );
		result.addField( ControlFieldFactory.createFieldString( "authType", FormShare.STR_LTYPE, AuthType.SYSTEM.name() )
				.setFieldType( "select" ).setFieldVariant( "bigselect" )
				.setAttribute( "lookup", Sharing.LOOKUP_AUTH_TYPE ) );
		result.addField( ControlFieldFactory.createFieldString( "accessType", FormShare.STR_ATYPE, AccessType.TESTING.name() )
				.setFieldType( "select" ).setFieldVariant( "bigselect" )
				.setAttribute( "lookup", Sharing.LOOKUP_ACCESS_TYPE ) );
		result.addField( ControlFieldFactory.createFieldString( "secureType", FormShare.STR_STYPE, SecureType.ANY.name() )
				.setFieldType( "select" ).setFieldVariant( "bigselect" )
				.setAttribute( "lookup", Sharing.LOOKUP_SECURE_TYPE ) );
		return result;
	}
}
