package core.action;

import client.Client;
import core.action.Constants.ActionType;

public class GetTask implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2879146176695599757L;
	private Task task;
	private int taskIndex;

	private Client client;
	
	public GetTask(Client cl) {
		this.client = cl;
	}
	
	public void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
	}
	
	public Task getTask() {
		return task;
	}
	
	public int getTaskIndex() {
	  return this.taskIndex;
	}
	
	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public ActionType getAction() {
		return ActionType.GET_TASK;
	}

	@Override
	public String getClientName() {
		return client.getName();
	}

}
