package core.action;

import client.Client;
import core.action.Constants.ActionType;

public class Login implements Action{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2896824237487992425L;
	private Client client;
	
	public Login(Client cl) {
		this.client = cl;
	}
	
	public Client getClient() {
		return client;
	}

	@Override
	public ActionType getAction() {
		return ActionType.LOGIN;
	}

	@Override
	public String getClientName() {
		return client.getName();
	}

}
