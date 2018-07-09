/**
 * 
 */
package ru.myx.cm5.control.personal;

final class TaskRecord {
	final String	taskID;
	
	final String	taskXML;
	
	int				amount;
	
	TaskRecord(final String taskID, final String taskXML) {
		this.taskID = taskID;
		this.taskXML = taskXML;
		this.amount = 1;
	}
}
