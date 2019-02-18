package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import client.Client;
import core.Action;
import core.AvailableTask;
import core.CompleteTask;
import core.Constants;
import core.Constants.ActionType;
import core.Constants.Status;
import core.GetTask;
import core.Task;

public class Server {

	private static List<Task> tasks = new ArrayList<Task>();

	private List<Client> clients = new ArrayList<Client>();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		loadTasks();
		ServerSocket server = new ServerSocket(Constants.PORT);
		// keep listens indefinitely until receives 'exit' call or program terminates
		while (true) {
			System.out.println("Waiting for client request");
			// creating socket and waiting for client connection
			Socket socket = server.accept();
			// read from socket to ObjectInputStream object
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Action action = (Action) ois.readObject();
			if (action.getAction().equals(ActionType.GET_TASK)) {
				GetTask getTask = (GetTask) action;
				Task task = ((GetTask) action).getTask();
				Task serverTask = tasks.get(getTask.getTaskIndex());
				serverTask.setStatus(Status.IN_PROGRESS);
				System.out.println("Object Received: " + task.getTitle());
				// create ObjectOutputStream object
				returnAvailableTasks(socket, ois, new AvailableTask(tasks));
			} else if (action.getAction().equals(ActionType.COMPLETE_TASK)) {
				CompleteTask complTask = (CompleteTask) action;
				System.out.println("Object Received: " + complTask.getTask().getTitle());
				// create ObjectOutputStream object
//				 write object to Socket
				complTask.getTask().setStatus(Status.COMPLETED);		// close resources
				tasks.get(complTask.getTaskIndex()).setStatus(Status.COMPLETED);
				returnAvailableTasks(socket, ois, new AvailableTask(tasks));
//				ois.close();
//				oos.close();
			} else if (action.getAction().equals(ActionType.AVAILABLE_TASKS)) {
				returnAvailableTasks(socket, ois, action);
			} else {
				System.out.println("This action is not implemented, yet...: ");
				// create ObjectOutputStream object
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				// write object to Socket
				oos.writeObject(new Object());
				// close resources
				ois.close();
				oos.close();
				socket.close();
			}
			// terminate the server if client sends exit request
		}
	}

	private static void returnAvailableTasks(Socket socket, ObjectInputStream ois, Action action) throws IOException {
		AvailableTask avTasks = new AvailableTask(new ArrayList<Task>(tasks));
		List<Task> tasks = ((AvailableTask) action).getTasks();
		// create ObjectOutputStream object
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		// write object to Socket
		oos.writeObject(avTasks);		// close resources
//		ois.close();
//		oos.close();
	}

	private static void loadTasks() {
		for (int i = 0; i < 5; i++) {
			Task task = new Task("Task " + i, i);
			tasks.add(task);
		}

	}
}
