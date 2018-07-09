/**
 * 
 */
package ru.myx.cm5.control.personal;

import java.util.Comparator;

final class TaskRecordSorter implements Comparator<TaskRecord> {
	@Override
	public final int compare(final TaskRecord o1, final TaskRecord o2) {
		return o2.amount - o1.amount;
	}
}
