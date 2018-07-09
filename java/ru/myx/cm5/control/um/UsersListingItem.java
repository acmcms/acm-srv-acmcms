/**
 * 
 */
package ru.myx.cm5.control.um;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.AbstractBasic;

final class UsersListingItem extends AbstractBasic<UsersListingItem> {
	private final BaseObject	data;
	
	private final String		userId;
	
	UsersListingItem(final AccessUser<?> user) {
		this.data = new BaseNativeObject()//
				.putAppend( "login", user.getLogin() )//
				.putAppend( "email", user.getEmail() )//
				.putAppend( "added", Base.forDateMillis( user.getCreated() ) )//
				.putAppend( "logged", Base.forDateMillis( user.getChanged() ) )//
		;
		this.userId = user.getKey();
	}
	
	@Override
	public BaseObject getData() {
		return this.data;
	}
	
	@Override
	public String getIcon() {
		return null;
	}
	
	@Override
	public String getKey() {
		return this.userId;
	}
	
	@Override
	public String getTitle() {
		return Base.getString( this.data, "login", null );
	}
}
