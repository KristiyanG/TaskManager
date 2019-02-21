package core.action;

import java.io.Serializable;

import core.action.Constants.ActionType;

public interface Action extends Serializable {
	
	ActionType getAction();
	
	String getClientName();

}
