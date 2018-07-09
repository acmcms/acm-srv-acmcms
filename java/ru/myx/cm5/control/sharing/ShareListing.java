/*
 * Created on 08.06.2004
 */
package ru.myx.cm5.control.sharing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import ru.myx.ae1.sharing.Share;
import ru.myx.ae3.help.Convert;

/** @author myx
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments */
final class ShareListing {

	private final Set<String> toDelete = new TreeSet<>();
	
	private final Set<String> toInsert = new TreeSet<>();
	
	private final Map<String, Share<?>> currentMap = new TreeMap<>();
	
	private final Map<String, Share<?>> originalMap = new TreeMap<>();
	
	private List<Share<?>> ready = null;
	
	ShareListing(final Share<?>[] shares) {

		if (shares != null) {
			for (int i = shares.length - 1; i >= 0; --i) {
				this.originalMap.put(shares[i].getAlias(), shares[i]);
			}
		}
		this.currentMap.putAll(this.originalMap);
	}
	
	void add(final Share<?> share) {

		if (this.originalMap.containsKey(share.getAlias())) {
			this.toDelete.add(share.getAlias());
		}
		this.toInsert.add(share.getAlias());
		this.currentMap.put(share.getAlias(), share);
		this.ready = null;
	}
	
	void clear() {

		this.toDelete.addAll(this.originalMap.keySet());
		this.toInsert.clear();
		this.currentMap.clear();
		this.ready = null;
	}
	
	void delete(final String key) {

		if (this.originalMap.containsKey(key)) {
			this.toDelete.add(key);
		}
		this.toInsert.remove(key);
		this.currentMap.remove(key);
		this.ready = null;
	}
	
	Share<?> get(final String key) {

		final Share<?> found = this.currentMap.get(key);
		if (found != null) {
			return found;
		}
		final int index = Convert.Any.toInt(key, -1);
		if (index < 0) {
			return null;
		}
		final int length = this.currentMap.size();
		if (index >= length) {
			return null;
		}
		return this.getListing().get(index);
	}
	
	Share<?> getByIndex(final int index) {

		return this.getListing().get(index);
	}
	
	Share<?> getByKey(final String key) {

		return this.currentMap.get(key);
	}
	
	Share<?>[] getCreated() {

		if (this.toInsert.isEmpty()) {
			return null;
		}
		final List<Share<?>> result = new ArrayList<>();
		for (final String current : this.toInsert) {
			result.add(this.currentMap.get(current));
		}
		return result.toArray(new Share[result.size()]);
	}
	
	String[] getDeleted() {

		return this.toDelete.isEmpty()
			? null
			: (String[]) this.toDelete.toArray(new String[this.toDelete.size()]);
	}
	
	List<Share<?>> getListing() {

		if (this.ready != null) {
			return this.ready;
		}
		final List<Share<?>> result = new ArrayList<>();
		for (final Share<?> share : this.currentMap.values()) {
			result.add(share);
		}
		Collections.sort(result, Sharing.SHARE_ALIAS_COMPARATOR);
		return this.ready = result;
	}
	
	boolean isEmpty() {

		return this.currentMap.isEmpty();
	}
	
	void replace(final Share<?>[] shares) {

		this.toDelete.clear();
		this.toInsert.clear();
		this.originalMap.clear();
		this.currentMap.clear();
		if (shares != null) {
			for (int i = shares.length - 1; i >= 0; --i) {
				this.originalMap.put(shares[i].getAlias(), shares[i]);
			}
		}
		this.currentMap.putAll(this.originalMap);
		this.ready = null;
	}
}
