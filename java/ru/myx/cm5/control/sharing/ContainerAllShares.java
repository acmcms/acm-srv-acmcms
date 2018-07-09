/*
 * Created on 08.06.2004
 */
package ru.myx.cm5.control.sharing;

import java.util.Collections;

import ru.myx.ae1.control.Control;
import ru.myx.ae1.control.MultivariantString;
import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractContainer;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;

/** @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments */
final class ContainerAllShares extends AbstractContainer<ContainerAllShares> {
	
	private final ShareListing shareList;

	ContainerAllShares(final ShareListing shareList) {
		
		this.shareList = shareList;
	}

	@Override
	public Object getCommandResult(final ControlCommand<?> command, final BaseObject arguments) throws Exception {
		
		if ("edit".equals(command.getKey())) {
			final String key = Base.getString(command.getAttributes(), "key", null);
			final Share<?> share = this.shareList.get(key);
			if (share != null) {
				return new FormShareProperties(share.getPath(), this.shareList, key);
			}
			return key + " was not found!";
		}
		if ("delete".equals(command.getKey())) {
			final String key = Base.getString(command.getAttributes(), "key", null);
			final Share<?> share = this.shareList.get(key);
			if (share != null) {
				this.shareList.delete(share.getAlias());
			}
			return null;
		}
		return super.getCommandResult(command, arguments);
	}

	@Override
	public ControlCommandset getContentCommands(final String key) {
		
		final ControlCommandset result = Control.createOptions();
		result.add(
				Control.createCommand("edit", MultivariantString.getString("Properties", Collections.singletonMap("ru", "Свойства"))).setCommandPermission("$modify_sharing")
						.setCommandIcon("command-edit").setAttribute("key", key));
		result.add(
				Control.createCommand("delete", MultivariantString.getString("Delete", Collections.singletonMap("ru", "Удалить"))).setCommandPermission("$modify_sharing")
						.setCommandIcon("command-delete").setAttribute("key", key));
		return result;
	}
}
