package core.action;

import java.util.List;

import core.action.Constants.ActionType;

public class AvailableTask implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7818556928651125676L;
	private List<Task> tasks;
	private List<String> clients;
	private String clientName;

	public AvailableTask(List<Task> tasks, List<String> clients) {
		this.tasks = tasks;
		this.clients = clients;
	}
	
	public List<Task> getTasks() {
		return tasks;
	}

	@Override
	public ActionType getAction() {
		// TODO Auto-generated method stub
		return ActionType.AVAILABLE_TASKS;
	}

	@Override
	public String getClientName() {
		// TODO Auto-generated method stub
		return clientName;
	}
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public List<String> getClients() {
		return clients;
	}

}
