/*
 * Created on 20.05.2004
 */
package ru.myx.cm5.control.um;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class FormEditTemplate extends AbstractForm<FormEditTemplate> {
	private static final ControlFieldset<?>	F_DEFAULT;
	
	private static final ControlFieldset<?>	F_GROUP_ADD;
	
	private static final ControlFieldset<?>	F_GROUP_REMOVE;
	
	private static final ControlFieldset<?>	F_LOGIN;
	
	private static final ControlFieldset<?>	F_REGISTER;
	
	private static final ControlFieldset<?>	F_FORGET_PASSWORD;
	
	private static ControlFieldset<?>		F_CHANGE_PASSWORD;
	
	private static final ControlCommand<?>	CMD_SAVE;
	
	private static final ControlCommand<?>	CMD_APPLY;
	
	private static final ControlCommand<?>	CMD_RESET;
	
	static {
		F_DEFAULT = ControlFieldset.createFieldset().addFields( new ControlField[] { //
			ControlFieldFactory.createFieldString( "name",
					MultivariantString.getString( "Name", Collections.singletonMap( "ru", "Имя" ) ),
					"" )//
					.setConstant(), //
			ControlFieldFactory.createFieldString( "description",
					MultivariantString.getString( "Description", Collections.singletonMap( "ru", "Описание" ) ),
					"" )//
					.setConstant(), //
		} );
		
		F_GROUP_ADD = ControlFieldset.createFieldset().addFields( new ControlField[] { //
			ControlFieldFactory.createFieldString( "name",
					MultivariantString.getString( "Name", Collections.singletonMap( "ru", "Имя" ) ),
					"" )//
					.setConstant(), //
			ControlFieldFactory.createFieldString( "description",
					MultivariantString.getString( "Description", Collections.singletonMap( "ru", "Описание" ) ),
					"" )//
					.setConstant(), //
			ControlFieldFactory.createFieldString( "messageRASubject",
					MultivariantString.getString( "Subject", Collections.singletonMap( "ru", "Тема" ) ),
					NodeTemplates.M_GROUP_ADD_SUBJECT )//
					.setFieldHint( NodeTemplates.M_GROUP_ADD_SUBJECT ), //
		} );
		
		F_GROUP_REMOVE = ControlFieldset.createFieldset().addFields( new ControlField[] { //
			ControlFieldFactory.createFieldString( "name",
					MultivariantString.getString( "Name", Collections.singletonMap( "ru", "Имя" ) ),
					"" )//
					.setConstant(),//
			ControlFieldFactory.createFieldString( "description",
					MultivariantString.getString( "Description", Collections.singletonMap( "ru", "Описание" ) ),
					"" )//
					.setConstant(),//
			ControlFieldFactory.createFieldString( "messageRRSubject",
					MultivariantString.getString( "Subject", Collections.singletonMap( "ru", "Тема" ) ),
					NodeTemplates.M_GROUP_REMOVE_SUBJECT )//
					.setFieldHint( NodeTemplates.M_GROUP_REMOVE_SUBJECT ), //
		} );
		
		F_LOGIN = ControlFieldset.createFieldset().addFields( new ControlField[] { //
			ControlFieldFactory.createFieldString( "name",
					MultivariantString.getString( "Name", Collections.singletonMap( "ru", "Имя" ) ),
					"" )//
					.setConstant(),//
			ControlFieldFactory.createFieldString( "description",
					MultivariantString.getString( "Description", Collections.singletonMap( "ru", "Описание" ) ),
					"" )//
					.setConstant(),//
			ControlFieldFactory.createFieldString( "messageLoginError",
					MultivariantString.getString( "Error text", Collections.singletonMap( "ru", "Текст ошибки" ) ),
					NodeTemplates.M_LOGIN_ERROR )//
					.setFieldHint( NodeTemplates.M_LOGIN_ERROR ), //
		} );
		
		F_REGISTER = ControlFieldset.createFieldset().addFields( new ControlField[] { //
		ControlFieldFactory.createFieldString( "name", //
				MultivariantString.getString( "Name", //
						Collections.singletonMap( "ru", "Имя" ) ),
				"" )//
				.setConstant(),//
			ControlFieldFactory.createFieldString( "description", //
					MultivariantString.getString( "Description", //
							Collections.singletonMap( "ru", "Описание" ) ),
					"" )//
					.setConstant(),//
			ControlFieldFactory.createFieldString( "messageRegistrationSucceedSubject", //
					MultivariantString.getString( "Message text", //
							Collections.singletonMap( "ru", "Текст сообщения" ) ),
					NodeTemplates.M_REGISTRATION_SUCCEED_SUBJECT ) //
					.setFieldHint( NodeTemplates.M_REGISTRATION_SUCCEED_SUBJECT ), //
			ControlFieldFactory.createFieldString( "messageRegistrationLNU", //
					MultivariantString.getString( "Message text", //
							Collections.singletonMap( "ru", "Текст сообщения" ) ),
					NodeTemplates.M_REGISTRATION_LOGIN_NUNIQUE ) //
					.setFieldHint( NodeTemplates.M_REGISTRATION_LOGIN_NUNIQUE ), //
			ControlFieldFactory.createFieldString( "messageRegistrationENU", //
					MultivariantString.getString( "Message text", //
							Collections.singletonMap( "ru", "Текст сообщения" ) ),
					NodeTemplates.M_REGISTRATION_EMAIL_NUNIQUE ) //
					.setFieldHint( NodeTemplates.M_REGISTRATION_EMAIL_NUNIQUE ), //
			ControlFieldFactory.createFieldString( "messageRegistrationEI", //
					MultivariantString.getString( "Message text", //
							Collections.singletonMap( "ru", "Текст сообщения" ) ),
					NodeTemplates.M_REGISTRATION_EMAIL_INVALID ) //
					.setFieldHint( NodeTemplates.M_REGISTRATION_EMAIL_INVALID ), //
		} );
		
		F_FORGET_PASSWORD = ControlFieldset.createFieldset().addFields( new ControlField[] { //
		ControlFieldFactory.createFieldString( "name", //
				MultivariantString.getString( "Name", //
						Collections.singletonMap( "ru", "Имя" ) ),
				"" )//
				.setConstant(), //
			ControlFieldFactory.createFieldString( "description", //
					MultivariantString.getString( "Description", //
							Collections.singletonMap( "ru", "Описание" ) ),
					"" )//
					.setConstant(),//
			ControlFieldFactory.createFieldString( "messageRestoreSubject", //
					MultivariantString.getString( "Error text", //
							Collections.singletonMap( "ru", "Текст ошибки" ) ),
					NodeTemplates.M_PASSWORD_RESTORE_SUBJECT )//
					.setFieldHint( NodeTemplates.M_PASSWORD_RESTORE_SUBJECT ), //
		} );
		
		FormEditTemplate.F_CHANGE_PASSWORD = ControlFieldset.createFieldset()
				.addFields( new ControlField[] { //
			ControlFieldFactory.createFieldString( "name",
					MultivariantString.getString( "Name", Collections.singletonMap( "ru", "Имя" ) ),
					"" )//
					.setConstant(), //
			ControlFieldFactory.createFieldString( "description",
					MultivariantString.getString( "Description", Collections.singletonMap( "ru", "Описание" ) ),
					"" )//
					.setConstant(), //
			ControlFieldFactory.createFieldString( "messageWrongPassword",
					MultivariantString.getString( "Error text", Collections.singletonMap( "ru", "Текст ошибки" ) ),
					NodeTemplates.M_WRONG_PASSWORD )//
					.setFieldHint( NodeTemplates.M_WRONG_PASSWORD ), //
			ControlFieldFactory.createFieldString( "messagePasswordsNotSame",
					MultivariantString.getString( "Error text", Collections.singletonMap( "ru", "Текст ошибки" ) ),
					NodeTemplates.M_PASSWORDS_NOT_SAME )//
					.setFieldHint( NodeTemplates.M_PASSWORDS_NOT_SAME ), //
			ControlFieldFactory.createFieldString( "messageWrongPasswordLength",
					MultivariantString.getString( "Error text", Collections.singletonMap( "ru", "Текст ошибки" ) ),
					NodeTemplates.M_WRONG_PASSWORD_LENGTH )//
					.setFieldHint( NodeTemplates.M_WRONG_PASSWORD_LENGTH ), //
				} );
		
		CMD_SAVE = Control.createCommand( "save", " OK " ) //
				.setCommandPermission( "modify" ) //
				.setCommandIcon( "command-save" );
		
		CMD_APPLY = Control.createCommand( "apply", //
				MultivariantString.getString( "Apply", //
						Collections.singletonMap( "ru", "Применить" ) ) ) //
				.setCommandPermission( "modify" ) //
				.setCommandIcon( "command-apply" );
		
		CMD_RESET = Control.createCommand( "reset", //
				MultivariantString.getString( "Reset", //
						Collections.singletonMap( "ru", "Сбросить" ) ) ) //
				.setCommandPermission( "modify" ) //
				.setCommandIcon( "command-reset" );
		
	}
	
	private final String					key;
	
	/**
	 * @param key
	 */
	public FormEditTemplate(final String key) {
		this.setAttributeIntern( "id", "edit_template" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Edit template",
						Collections.singletonMap( "ru", "Редактирование шаблона" ) ) );
		this.recalculate();
		this.key = key;
		final BaseObject data = Context.getServer( Exec.currentProcess() ).getStorage().load( "um-" + key );
		assert data != null : "NULL java value";
		this.setData( data );
		this.makeData();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormEditTemplate.CMD_SAVE || command == FormEditTemplate.CMD_APPLY) {
			final BaseObject data = new BaseNativeObject();
			final ControlFieldset<?> fieldset = this.getFieldset();
			fieldset.dataStore( this.getData(), data );
			Context.getServer( Exec.currentProcess() ).getStorage().savePersistent( "um-" + this.key, data );
			return command == FormEditTemplate.CMD_SAVE
					? null
					: this;
		}
		if (command == FormEditTemplate.CMD_RESET) {
			this.getData().baseClear();
			this.makeData();
			return this;
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		final ControlCommandset result = Control.createOptions();
		result.add( FormEditTemplate.CMD_SAVE );
		result.add( FormEditTemplate.CMD_APPLY );
		result.add( FormEditTemplate.CMD_RESET );
		return result;
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		final ControlFieldset<?> fieldsetOriginal;
		if (this.key.equals( "login.user" )) {
			fieldsetOriginal = FormEditTemplate.F_LOGIN;
		} else //
		if (this.key.equals( "register.user" )) {
			fieldsetOriginal = FormEditTemplate.F_REGISTER;
		} else //
		if (this.key.equals( "forget-password.user" )) {
			fieldsetOriginal = FormEditTemplate.F_FORGET_PASSWORD;
		} else //
		if (this.key.equals( "change-password.user" )) {
			fieldsetOriginal = FormEditTemplate.F_CHANGE_PASSWORD;
		} else //
		if (this.key.equals( "group-added.eml" )) {
			fieldsetOriginal = FormEditTemplate.F_GROUP_ADD;
		} else //
		if (this.key.equals( "group-removed.eml" )) {
			fieldsetOriginal = FormEditTemplate.F_GROUP_REMOVE;
		} else {
			fieldsetOriginal = FormEditTemplate.F_DEFAULT;
		}
		final ControlFieldset<?> fieldset = ControlFieldset.createFieldset()
				.setAttributes( fieldsetOriginal.getAttributes() );
		fieldset.addFields( fieldsetOriginal );
		{
			final ControlField template = ControlFieldFactory.createFieldTemplate( "template", //
					MultivariantString.getString( "Source", //
							Collections.singletonMap( "ru", "Шаблон" ) ),
					NodeTemplates.getDefaultTemplateFor( this.key ) )//
					.setFieldHint( NodeTemplates.getHelpFor( this.key ) );
			
			if (this.key.equals( "login.user" )) {
				template.setConstant();
			}
			
			fieldset.addField( template );
		}
		return fieldset;
	}
	
	private void makeData() {
		final BaseObject data = this.getData();
		data.baseDefine("name", '/' + this.key);
		data.baseDefine("description", NodeTemplates.getDescriptionFor( this.key ));
		this.getFieldset().dataStore( data, data );
	}
}
