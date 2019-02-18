package core;

public class Constants {
	
	public static final int PORT = 8088;

	public enum Status {
		NEW, IN_PROGRESS, COMPLETED
	}
	
	public enum ActionType {
		GET_TASK, CREATE_TASK,DELETE_TASK,COMPLETE_TASK, AVAILABLE_TASKS
	}
}
