/**
 * 
 */
package ru.myx.cm5.control.sharing;

import java.util.Comparator;

import ru.myx.ae1.sharing.Share;

final class ShareAliasComparator implements Comparator<Share<?>> {
	@Override
	public final int compare(final Share<?> o1, final Share<?> o2) {
		final String s1 = o1.getKey();
		final String s2 = o2.getKey();
		int len1 = s1.length();
		int len2 = s2.length();
		int n = Math.min( len1, len2 );
		
		for (; n > 0; n--) {
			final char c1 = s1.charAt( --len1 );
			final char c2 = s2.charAt( --len2 );
			if (c1 != c2) {
				return c1 - c2;
			}
		}
		return len1 - len2;
	}
}
