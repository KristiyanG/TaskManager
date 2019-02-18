package core;

import core.Constants.ActionType;

public class CreateTask implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Task task;

	public Task getTask() {
		return task;
	}

	@Override
	public ActionType getAction() {
		// TODO Auto-generated method stub
		return ActionType.CREATE_TASK;
	}

}
