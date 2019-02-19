package core.action;

import java.util.List;

import core.action.Constants.ActionType;

public class AvailableTask implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7818556928651125676L;
	private List<Task> tasks;

	public AvailableTask(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public List<Task> getTasks() {
		return tasks;
	}

	@Override
	public ActionType getAction() {
		// TODO Auto-generated method stub
		return ActionType.AVAILABLE_TASKS;
	}

}
