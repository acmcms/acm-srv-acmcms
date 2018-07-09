/*
 * Created on 08.12.2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ru.myx.cm5.control.personal;

import java.util.Collections;

import ru.myx.ae1.access.Access;
import ru.myx.ae1.access.AccessManager;
import ru.myx.ae1.access.AccessUser;
import ru.myx.ae1.access.PasswordType;
import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae3.act.Context;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlField;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.exec.Exec;
import ru.myx.ae3.exec.ExecProcess;

final class FormPasswordChange extends AbstractForm<FormPasswordChange> {
	static final class PasswordValidator implements FormPasswordChange.Validator {
		private final PasswordType	type;
		
		PasswordValidator(final PasswordType type) {
			this.type = type;
		}
		
		@Override
		public void validate(final String prev, final String newOne) {
			final ExecProcess process = Exec.currentProcess();
			final String login = Context.getUser( process ).getLogin();
			if (newOne.trim().equals( newOne ) && newOne.indexOf( ' ' ) == -1) {
				if (newOne.length() < 6) {
					throw new IllegalArgumentException( "Password must be at least 6 characters long!" );
				}
				if (newOne.equals( login )) {
					throw new IllegalArgumentException( "Password must not match login name!" );
				}
				final AccessUser<?> user = Access.getUserByLoginCheckPassword( Context.getServer( process )
						.getAccessManager(), login, prev, this.type );
				if (user == null) {
					throw new IllegalArgumentException( "Incorrect password for login '" + login + "'!" );
				}
			} else {
				throw new IllegalArgumentException( "Password must not contain whitespaces!" );
			}
		}
	}
	
	/**
	 * @author myx
	 * 
	 */
	static interface Validator {
		/**
		 * @param prev
		 * @param newOne
		 */
		public void validate(final String prev, final String newOne);
	}
	
	static final FormPasswordChange.Validator	VALIDATE_PASSWORD_NORM	= new PasswordValidator( PasswordType.NORMAL );
	
	static final FormPasswordChange.Validator	VALIDATE_PASSWORD_HIGH	= new PasswordValidator( PasswordType.HIGHER );
	
	static final ControlFieldset<?>				DEFINITION				= ControlFieldset
																				.createFieldset( "admin.passwordchange" )
																				.addFields( new ControlField[] {
			ControlFieldFactory
					.createFieldString( "old",
							MultivariantString.getString( "Current password",
									Collections.singletonMap( "ru", "Текущий пароль" ) ),
							"" ).setFieldVariant( "password" ),
			ControlFieldFactory.createFieldString( "new",
					MultivariantString.getString( "New password", Collections.singletonMap( "ru", "Новый пароль" ) ),
					"" ).setFieldVariant( "password" )							} );
	
	private static final ControlCommand<?>		CMD_SAVE				= Control.createCommand( "save", " OK " )
																				.setCommandIcon( "command-save" );
	
	private final FormPasswordChange.Validator	validator;
	
	private final AccessUser<?>					user;
	
	FormPasswordChange(final BaseObject title,
			final String key,
			final FormPasswordChange.Validator validator,
			final AccessUser<?> user) {
		this.user = user;
		this.setAttributeIntern( "title", title );
		this.setAttributeIntern( "id", key.replace( '/', '_' ).replace( '\\', '_' ).replace( ' ', '_' ) );
		this.validator = validator;
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormPasswordChange.CMD_SAVE) {
			final BaseObject data = this.getData();
			final String Old = Base.getString( data, "old", "" );
			final String New = Base.getString( data, "new", "" );
			data.baseDelete( "old" );
			data.baseDelete( "new" );
			if (this.validator != null) {
				this.validator.validate( Old, New );
			}
			final AccessManager manager = Context.getServer( Exec.currentProcess() ).getAccessManager();
			manager.setPassword( this.user, New, this.validator == FormPasswordChange.VALIDATE_PASSWORD_NORM
					? PasswordType.NORMAL
					: PasswordType.HIGHER );
			manager.commitUser( this.user );
			return "Password was set.";
		}
		throw new IllegalArgumentException( "Unknown command: " + command.getKey() );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormPasswordChange.CMD_SAVE );
	}
	
	@Override
	public final ControlFieldset<?> getFieldset() {
		return FormPasswordChange.DEFINITION;
	}
}
