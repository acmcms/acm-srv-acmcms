/*
 * Created on 03.06.2004
 */
package ru.myx.cm5.control;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Know;
import ru.myx.ae1.know.Server;
import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArrayDynamic;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.ControlBasic;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.know.Language;
import ru.myx.sapi.RuntimeSAPI;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormSiteDetails extends AbstractForm<FormSiteDetails> {
	private static final ControlFieldset<?>	FIELDSET_LANG_LISTING	= ControlFieldset
																			.createFieldset()
																			.addField( ControlFieldFactory
																					.createFieldString( "id",
																							MultivariantString
																									.getString( "id",
																											Collections
																													.singletonMap( "ru",
																															"Имя" ) ),
																							"" ) )
																			.addField( ControlFieldFactory
																					.createFieldString( "short",
																							MultivariantString
																									.getString( "Short",
																											Collections
																													.singletonMap( "ru",
																															"Кратко" ) ),
																							"" ) )
																			.addField( ControlFieldFactory
																					.createFieldString( "common",
																							MultivariantString
																									.getString( "Common",
																											Collections
																													.singletonMap( "ru",
																															"Полное" ) ),
																							"" ) )
																			.addField( ControlFieldFactory
																					.createFieldString( "original",
																							MultivariantString
																									.getString( "Original",
																											Collections
																													.singletonMap( "ru",
																															"Оригинальное" ) ),
																							"" ) )
																			.addField( ControlFieldFactory
																					.createFieldString( "cencoding",
																							MultivariantString
																									.getString( "Common encoding",
																											Collections
																													.singletonMap( "ru",
																															"Кодировка" ) ),
																							"" ) )
																			.addField( ControlFieldFactory
																					.createFieldString( "jencoding",
																							MultivariantString
																									.getString( "Java encoding",
																											Collections
																													.singletonMap( "ru",
																															"Кодировка явы" ) ),
																							"" ) );
	
	private static final ControlFieldset<?>	FIELDSET				= ControlFieldset
																			.createFieldset()
																			.addField( ControlFieldFactory
																					.createFieldString( "srv_classname",
																							MultivariantString
																									.getString( "Class<?> name",
																											Collections
																													.singletonMap( "ru",
																															"Имя класса" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "srv_domainid",
																							MultivariantString
																									.getString( "Primary domain",
																											Collections
																													.singletonMap( "ru",
																															"Основной домен" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "srv_serverid",
																							MultivariantString
																									.getString( "Server identifier",
																											Collections
																													.singletonMap( "ru",
																															"Идентификатор сервера" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "rt_mainentrance",
																							MultivariantString
																									.getString( "Main entrance",
																											Collections
																													.singletonMap( "ru",
																															"Главный вход" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "srv_langdefault",
																							MultivariantString
																									.getString( "Default language",
																											Collections
																													.singletonMap( "ru",
																															"Основной язык" ) ),
																							"" ).setConstant() )
																			.addField( Control
																					.createFieldList( "srv_languages",
																							MultivariantString
																									.getString( "Active languages",
																											Collections
																													.singletonMap( "ru",
																															"Активные языки" ) ),
																							null )
																					.setConstant()
																					.setAttribute( "content_fieldset",
																							FormSiteDetails.FIELDSET_LANG_LISTING ) )
																			.addField( Control
																					.createFieldList( "all_languages",
																							MultivariantString
																									.getString( "All languages",
																											Collections
																													.singletonMap( "ru",
																															"Все языки" ) ),
																							null )
																					.setConstant()
																					.setAttribute( "content_fieldset",
																							FormSiteDetails.FIELDSET_LANG_LISTING ) )
																			.addField( ControlFieldFactory
																					.createFieldDate( "srv_startdate",
																							MultivariantString
																									.getString( "Start date",
																											Collections
																													.singletonMap( "ru",
																															"Дата запуска" ) ),
																							0L ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "srv_uptime",
																							MultivariantString
																									.getString( "Up-time",
																											Collections
																													.singletonMap( "ru",
																															"Время работы" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "srv_rootpath",
																							MultivariantString
																									.getString( "Root path",
																											Collections
																													.singletonMap( "ru",
																															"Коренной путь" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "rt_name",
																							MultivariantString
																									.getString( "Runtime name",
																											Collections
																													.singletonMap( "ru",
																															"Имя ядра" ) ),
																							"" ).setConstant() )
																			.addField( ControlFieldFactory
																					.createFieldString( "rt_version",
																							MultivariantString
																									.getString( "Runtime version",
																											Collections
																													.singletonMap( "ru",
																															"Версия ядра" ) ),
																							"" ).setConstant() );
	
	private final Server					server;
	
	FormSiteDetails(final Server server) {
		this.server = server;
		this.setAttributeIntern( "id", "site_details" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Site details", Collections.singletonMap( "ru", "Параметры сайта" ) ) );
		this.recalculate();
	}
	
	@Override
	public BaseObject getData() {
		final String defaultLanguage;
		{
			final String dlang = this.server.getLanguageDefault();
			final Language lang = Language.getLanguage( dlang );
			defaultLanguage = dlang + " (" + lang.getCommonName() + " / " + lang.getOriginalName() + ')';
		}
		
		final BaseObject serverLanguages;
		{
			final BaseArrayDynamic<ControlBasic<?>> result = BaseObject.createArray();
			final String[] langs = this.server.getLanguages();
			for (final String element : langs) {
				final Language lang = Language.getLanguage( element );
				final BaseObject ldata = new BaseNativeObject()//
						.putAppend( "id", lang.getName() )//
						.putAppend( "short", lang.getShortName() )//
						.putAppend( "common", lang.getCommonName() )//
						.putAppend( "original", lang.getOriginalName() )//
						.putAppend( "cencoding", lang.getCommonEncoding() )//
						.putAppend( "jencoding", lang.getJavaEncoding() )//
				;
				result.add( Control.createBasic( element, lang.getCommonName(), ldata ) );
			}
			serverLanguages = result;
		}
		
		final BaseObject allLanguages;
		{
			final BaseArrayDynamic<ControlBasic<?>> result = BaseObject.createArray();
			for (final Language language : Language.getAllLanguages()) {
				final BaseObject ldata = new BaseNativeObject()//
						.putAppend( "id", language.getName() )//
						.putAppend( "short", language.getShortName() )//
						.putAppend( "common", language.getCommonName() )//
						.putAppend( "original", language.getOriginalName() )//
						.putAppend( "cencoding", language.getCommonEncoding() )//
						.putAppend( "jencoding", language.getJavaEncoding() )//
				;
				result.add( Control.createBasic( language.getName(), language.getCommonName(), ldata ) );
			}
			allLanguages = result;
		}
		
		final BaseObject data = new BaseNativeObject()//
				.putAppend( "srv_classname", this.server.getClass().getName() )//
				.putAppend( "srv_domainid", this.server.getDomainId() )//
				.putAppend( "srv_serverid", this.server.getZoneId() )//
				.putAppend( "rt_mainentrance", this.server.getProperty( "entrance", null ) )//
				.putAppend( "srv_langdefault", defaultLanguage )//
				.putAppend( "srv_languages", serverLanguages )//
				.putAppend( "all", allLanguages )//
				.putAppend( "srv_startdate", Base.forDateMillis( this.server.getServerStartTime() ) )//
				.putAppend( "srv_uptime",
						Format.Compact.toPeriod( Engine.fastTime() - this.server.getServerStartTime() ) )//
				.putAppend( "srv_rootpath", this.server.getSystemRoot().getAbsolutePath() )//
				.putAppend( "rt_name", RuntimeSAPI.getRuntimeName() )//
				.putAppend( "rt_version", Know.systemVersion() + '/' + Know.systemBuild() )//
		;
		return data;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return FormSiteDetails.FIELDSET;
	}
}
