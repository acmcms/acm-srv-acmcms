/**
 * 
 */
package ru.myx.cm5.control.um;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import ru.myx.ae1.access.AccessUser;
import ru.myx.ae3.control.ControlBasic;

final class UsersListing extends AbstractList<ControlBasic<?>> {
	private final List<AccessUser<?>>	users;
	
	UsersListing(final AccessUser<?>[] users) {
		this.users = Arrays.asList( users );
	}
	
	UsersListing(final List<AccessUser<?>> users) {
		this.users = users;
	}
	
	@Override
	public ControlBasic<?> get(final int index) {
		return new UsersListingItem( this.users.get( index ) );
	}
	
	@Override
	public int size() {
		return this.users.size();
	}
	
	@Override
	public List<ControlBasic<?>> subList(final int start, final int end) {
		return new UsersListing( this.users.subList( start, end ) );
	}
	
}
