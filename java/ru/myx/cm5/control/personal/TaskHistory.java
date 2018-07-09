/**
 * 
 */
package ru.myx.cm5.control.personal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import ru.myx.ae1.control.Control;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseArray;
import ru.myx.ae3.base.BaseArrayDynamic;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.control.ControlContainer;
import ru.myx.ae3.control.command.ControlCommand;
import ru.myx.ae3.control.command.ControlCommandset;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.produce.Produce;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.xml.Xml;

final class TaskHistory {
	private static final Comparator<TaskRecord>	SORTER	= new TaskRecordSorter();
	
	private final TaskRecord[]					tasks	= new TaskRecord[256];
	
	private int									head;
	
	TaskHistory(final BaseObject data) {
		this.head = Convert.MapEntry.toInt( data, "head", 0 );
		final BaseObject history = data.baseGet( "history", BaseObject.UNDEFINED );
		assert history != null : "NULL java value";
		if (history != BaseObject.UNDEFINED) {
			final BaseArray array = history.baseArray();
			if (array != null) {
				final int length = array.length();
				for (int i = 0; i < length; ++i) {
					final BaseObject historyMap = array.baseGet( i, BaseObject.UNDEFINED );
					this.append( Base.getString( historyMap, "tid", "" ).trim(), Base.getString( historyMap, "xml", "" )
							.trim() );
				}
			} else {
				this.append( Base.getString( history, "tid", "" ).trim(), Base.getString( history, "xml", "" ).trim() );
			}
		}
	}
	
	void append(final String taskID, final String taskXML) {
		this.tasks[this.head++ & 0xFF] = new TaskRecord( taskID, taskXML );
	}
	
	ControlCommandset chart(final int limit, final ControlCommandset result) {
		final Map<String, TaskRecord> temp = new TreeMap<>();
		for (int i = 255; i >= 0; --i) {
			final TaskRecord current = this.tasks[i];
			if (current != null) {
				final String id = current.taskID + '_' + current.taskXML;
				final Object object = temp.get( id );
				if (object == null) {
					temp.put( id, new TaskRecord( current.taskID, current.taskXML ) );
				} else {
					((TaskRecord) object).amount++;
				}
			}
		}
		final TaskRecord[] records = temp.values().toArray( new TaskRecord[temp.size()] );
		Report.debug( "PERSONAL", "TASK_CHART: len=" + records.length );
		Arrays.sort( records, TaskHistory.SORTER );
		for (int i = 0, count = 0; count < limit && i < records.length; ++i) {
			try {
				final BaseObject attributes = Xml.toBase( "personalHistory", records[i].taskXML, null, null, null );
				final ControlContainer<?> container = Produce.object( ControlContainer.class,
						records[i].taskID,
						attributes,
						null );
				Report.debug( "PERSONAL", "TASK_CHART_ITEM: id=" + records[i].taskID + ", container=" + container );
				if (container != null) {
					final ControlCommandset options = container.getCommands();
					Report.debug( "PERSONAL", "TASK_CHART_OPTIONS: id=" + records[i].taskID + ", options=" + options );
					if (options != null && !options.isEmpty()) {
						final ControlCommand<?> command = options.get( 0 );
						Report.debug( "PERSONAL", "TASK_CHART_COMMAND: id="
								+ records[i].taskID
								+ ", command="
								+ command );
						if (command != null) {
							if (count == 0) {
								result.add( Control.createCommandSplitter() );
							}
							count++;
							final String id = records[i].taskID
									+ '_'
									+ Integer.toHexString( records[i].taskXML.hashCode() );
							result.add( Control.createCommand( "", "" ).setAttributes( command.getAttributes() )
									.setAttribute( "quick_tasks_id", records[i].taskID )
									.setAttribute( "quick_tasks_container", container ).setAttribute( "id", id ) );
						}
					}
				}
			} catch (final Throwable t) {
				Report.exception( "PERSONAL", "TASK_CHART_EXCEPTION (skipped): id=" + records[i].taskID, t );
			}
		}
		return result;
	}
	
	void store(final BaseObject map) {
		final long value = this.head;
		map.baseDefine("head", value);
		final BaseArrayDynamic<Object> history = BaseObject.createArray();
		for (int i = 255; i >= 0; --i) {
			final TaskRecord current = this.tasks[i];
			if (current != null) {
				final BaseObject temp = new BaseNativeObject()//
						.putAppend( "tid", current.taskID )//
						.putAppend( "xml", current.taskXML )//
				;
				history.baseDefaultPush( temp );
			}
		}
		map.baseDefine("history", history);
	}
}
