package core.action;

import client.Client;
import core.action.Constants.ActionType;

public class DeleteTask implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2879146176695599757L;
	private Task task;
	private int taskIndex;

	public void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
	}

	public int getTaskIndex() {
		return this.taskIndex;
	}

	private Client client;

	public DeleteTask(Task task, Client client) {
		this.task = task;
		this.client = client;
	}

	public Task getTask() {
		return task;
	}

	@Override
	public ActionType getAction() {
		return ActionType.DELETE_TASK;
	}

	@Override
	public String getClientName() {
		return client.getName();
	}
}
