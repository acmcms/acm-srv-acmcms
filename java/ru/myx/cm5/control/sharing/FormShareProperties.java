/*
 * Created on 08.06.2004
 */
package ru.myx.cm5.control.sharing;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.sharing.AccessType;
import ru.myx.ae1.sharing.AuthType;
import ru.myx.ae1.sharing.SecureType;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
final class FormShareProperties extends FormShare {
	private final String					path;
	
	private final ShareListing				shareList;
	
	private final Share<?>					share;
	
	private final ControlFieldset<?>		fieldset;
	
	private static final ControlCommand<?>	CMD_SAVE	= Control.createCommand( "ok", " OK " )
																.setCommandPermission( "$modify_sharing" )
																.setCommandIcon( "command-save" );
	
	FormShareProperties(final String path, final ShareListing shareList, final String key) {
		this.path = path;
		this.shareList = shareList;
		this.share = shareList.get( key );
		this.setData( this.share.getData() );
		this.fieldset = FormShare.createFieldset( path );
		this.setAttributeIntern( "id", "entry_share_properties" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Access point properties",
						Collections.singletonMap( "ru", "Свойства точки доступа" ) ) );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		if (command == FormShareProperties.CMD_SAVE) {
			final String shareAlias = Base.getString( this.getData(), "alias", null ).toLowerCase().trim();
			final AuthType authType = AuthType.valueOf( Base.getString( this.getData(), "authType", "" ) );
			final AccessType accessType = AccessType.valueOf( Base.getString( this.getData(), "accessType", "" ) );
			final SecureType secureType = SecureType.valueOf( Base.getString( this.getData(), "secureType", "" ) );
			final String languageMode = Base.getString( this.getData(), "languageMode", null ).toLowerCase().trim();
			final String skinner = Base.getString( this.getData(), "skinner", null ).trim();
			final boolean commandMode = Convert.MapEntry.toBoolean( this.getData(), "commandMode", false );
			this.shareList.delete( this.share.getAlias() );
			this.shareList.add( new ShareImpl( this.path,
					shareAlias,
					authType,
					accessType,
					secureType,
					skinner,
					languageMode,
					commandMode ) );
			return null;
		}
		return super.getCommandResult( command, arguments );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormShareProperties.CMD_SAVE );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return this.fieldset;
	}
}
