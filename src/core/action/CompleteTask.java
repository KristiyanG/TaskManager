package core.action;

import client.Client;
import core.action.Constants.ActionType;

public class CompleteTask implements Action {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 348816548207924539L;
	private Task task;
	private int taskIndex;
	private Client client;
	public CompleteTask(Task task, int taskIndex, Client client) {
		this.client = client;
		// TODO Auto-generated constructor stub
		this.task = task;
		this.taskIndex = taskIndex;
	}
	
	public Task getTask() {
		return task;
	}
	
	public int getTaskIndex() {
		return taskIndex;
	}

	@Override
	public ActionType getAction() {
		// TODO Auto-generated method stub
		return ActionType.COMPLETE_TASK;
	}

	@Override
	public String getClientName() {
		return client.getName();
	}


}
