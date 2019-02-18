package core;

import java.io.Serializable;

import core.Constants.ActionType;

public interface Action extends Serializable {
	
	ActionType getAction();

}
