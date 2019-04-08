package core.action;

import client.Client;
import core.action.Constants.ActionType;

public class CreateTask implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2879146176695599757L;
	private Task task;
	
	private Client client;
	
	public CreateTask(Client cl, Task task) {
		this.task = task;
		this.client = cl;
	}
	
	public Task getTask() {
		return task;
	}
	
	@Override
	public ActionType getAction() {
		return ActionType.CREATE_TASK;
	}

	@Override
	public String getClientName() {
		return client.getName();
	}
}
