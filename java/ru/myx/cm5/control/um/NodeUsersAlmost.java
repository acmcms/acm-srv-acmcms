package ru.myx.cm5.control.um;

import java.util.Collections;

import ru.myx.ae1.access.UserTypes;
import ru.myx.ae1.control.MultivariantString;

/**
 * <p>
 * Title: RT3 adaptor
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */
final class NodeUsersAlmost extends NodeUsers {
	private static final Object	NODE_TITLE	= MultivariantString.getString( "Almost users",
													Collections.singletonMap( "ru", "Пользователи (почти)" ) );
	
	NodeUsersAlmost() {
		super();
		this.MinType = UserTypes.UT_HALF_REGISTERED;
		this.MaxType = UserTypes.UT_HALF_REGISTERED;
	}
	
	protected void chdFill() {
		// empty
	}
	
	@Override
	public String getKey() {
		return "usera";
	}
	
	@Override
	public String getTitle() {
		return NodeUsersAlmost.NODE_TITLE.toString();
	}
}
