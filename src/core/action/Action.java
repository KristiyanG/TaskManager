package core.action;

import java.io.Serializable;

import core.action.Constants.ActionType;

/**
 * This interface is used for communication between client and server.
 * 
 * @author KRG
 *
 */
public interface Action extends Serializable {
	
	/**
	 * Getter action name
	 * 
	 * @return Action type
	 */
	ActionType getAction();
	
	/**
	 * Getter for client name
	 * 
	 * @return client name
	 */
	String getClientName();

}
