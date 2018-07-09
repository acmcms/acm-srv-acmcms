/*
 * Created on 15.03.2005
 */
package ru.myx.srv.acm;

import java.io.File;

import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.vfs.Storage;
import ru.myx.ae3.vfs.filesystem.StorageImplFilesystem;
import ru.myx.cm5.control.lfs.NodeFileSystem;

class ServerDomainController extends ServerDomain {
	ServerDomainController(final String serverId, final BaseObject attributes) {
		super( serverId, attributes );
		final BaseArray folders = Convert.MapEntry.toCollection( attributes, "folder", null );
		if (folders != null) {
			final int length = folders.length();
			for (int i = 0; i < length; ++i) {
				final BaseObject folder = folders.baseGet( i, BaseObject.UNDEFINED );
				assert folder != null : "NULL java value";
				final String key = Base.getString( folder, "key", "" ).trim();
				if (key.length() == 0) {
					continue;
				}
				final String path = Base.getString( folder, "path", "" ).trim();
				if (path.length() == 0) {
					continue;
				}
				final File root = new File( path );
				if (!root.exists()) {
					continue;
				}
				final BaseObject title = folder.baseGet( "title", BaseObject.UNDEFINED );
				assert title != null : "NULL java value";
				this.getControlRoot().bind( new NodeFileSystem( this.getControlRoot(),
						Storage.createRoot( new StorageImplFilesystem( root, false ) ).toContainer(),
						key,
						title == BaseObject.UNDEFINED
								? Base.forString( key )
								: title ) );
			}
		}
	}
	
	@Override
	public boolean isControllerServer() {
		return true;
	}
}
