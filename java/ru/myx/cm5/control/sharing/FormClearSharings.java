/*
 * Created on 12.04.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ru.myx.cm5.control.sharing;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.know.Server;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.Engine;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractForm;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.control.field.ControlFieldFactory;
import ru.myx.ae3.control.fieldset.ControlFieldset;
import ru.myx.ae3.help.Convert;

/**
 * @author myx
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class FormClearSharings extends AbstractForm<FormClearSharings> {
	private static final ControlCommand<?>	RESTART	= Control.createCommand( Engine.createGuid(),
															MultivariantString.getString( "Delete",
																	Collections.singletonMap( "ru", "Удалить" ) ) )
															.setCommandIcon( "command-delete" );
	
	private final Server					server;
	
	private final ShareListing				listing;
	
	/**
	 * @param server
	 * @param path
	 */
	public FormClearSharings(final Server server, final String path) {
		this.server = server;
		final Share<?>[] shares = Sharing.getShareSetupFor( server, path );
		this.listing = new ShareListing( shares );
		this.setAttributeIntern( "id", "confirmation" );
		this.setAttributeIntern( "title",
				MultivariantString.getString( "Do you really want to remove " + shares.length + " sharings?",
						Collections.singletonMap( "ru", "Вы действительно хотите удалить "
								+ shares.length
								+ " точек доступа?" ) ) );
		this.recalculate();
	}
	
	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) {
		if (command == FormClearSharings.RESTART) {
			if (Convert.MapEntry.toBoolean( this.getData(), "confirmation", false )) {
				this.listing.clear();
				Sharing.commitSharing( this.server, this.listing );
				return null;
			}
			return MultivariantString.getString( "Action cancelled.",
					Collections.singletonMap( "ru", "Действие отменено." ) );
		}
		return MultivariantString.getString( "Restarted already",
				Collections.singletonMap( "ru", "Пререзапуск уже осуществлен" ) );
	}
	
	@Override
	public ControlCommandset getCommands() {
		return Control.createOptionsSingleton( FormClearSharings.RESTART );
	}
	
	@Override
	public ControlFieldset<?> getFieldset() {
		return ControlFieldset.createFieldset( "confirmation" )
				.addField( ControlFieldFactory.createFieldBoolean( "confirmation",
						MultivariantString.getString( "yes, i do.", Collections.singletonMap( "ru", "однозначно!" ) ),
						false ) );
	}
}
