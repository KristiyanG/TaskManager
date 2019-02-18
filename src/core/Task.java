package core;

import java.io.Serializable;

import core.Constants.Status;

public class Task implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private int duration;

	private Status status;
	
	public Task(String title, int duration) {
		this.duration = duration;
		this.title = title;
		this.status = Status.NEW;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDuration() {
		return duration*1000;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status s) {
		this.status = s;
	}
	
	
	


}
