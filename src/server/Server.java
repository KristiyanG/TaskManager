package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.action.Action;
import core.action.AvailableTask;
import core.action.CompleteTask;
import core.action.Constants;
import core.action.CreateTask;
import core.action.DeleteTask;
import core.action.GetTask;
import core.action.Login;
import core.action.Task;
import core.action.Constants.ActionType;
import core.action.Constants.Status;

/**
 * 
 * @author KRG
 *
 */
public class Server {

	private static List<Task> tasks = new ArrayList<Task>();

	private static Map<String, Socket> sockets = new HashMap<String, Socket>();

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		loadTasks();
		ServerSocket server = new ServerSocket(Constants.PORT);
		while (true) {
			System.out.println("Waiting for client request");
			Socket socket = server.accept();
			clientRequest(socket);
		}
	}

	/**
	 * Parse the client reques
	 * 
	 * @param socket
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void clientRequest(Socket socket) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Action action = (Action) ois.readObject();
		String clientName = action.getClientName();
		System.out.println("Client request - " + clientName);
		if (sockets.get(clientName) == null) {
			sockets.put(clientName, socket);
		}
		if (action.getAction().equals(ActionType.GET_TASK)) {
			GetTask getTask = (GetTask) action;
			Task task = ((GetTask) action).getTask();
			Task serverTask = tasks.get(getTask.getTaskIndex());
			serverTask.setStatus(Status.IN_PROGRESS);
			System.out.println("Object Received: " + task.getTitle() + " from " + clientName);
		} else if (action.getAction().equals(ActionType.COMPLETE_TASK)) {
			CompleteTask complTask = (CompleteTask) action;
			System.out.println("Object Received: " + complTask.getTask().getTitle());
			complTask.getTask().setStatus(Status.COMPLETED); // close resources
			tasks.remove(complTask.getTaskIndex());
		} else if (action.getAction().equals(ActionType.LOGIN)) {
			Login login = (Login) action;
			System.out.println("Login from : " + login.getClientName());
		} else if (action.getAction().equals(ActionType.CREATE_TASK)) {
			CreateTask login = (CreateTask) action;
			System.out.println("Create task : " + login.getClientName());
			addTask(login.getTask());
		} else if (action.getAction().equals(ActionType.DELETE_TASK)) {
			DeleteTask login = (DeleteTask) action;
			System.out.println("Delete task : " + login.getClientName());
			tasks.remove(login.getTaskIndex());
		}

		updateClients(clientName);
		returnAvailableTasks(socket);
	}

	/**
	 * Add task in collection If name is empty generate task name.
	 * 
	 * @param task
	 */
	private static void addTask(Task task) {
		if (task.getTitle().isEmpty()) {
			task.setTitle("Task" + tasks.size() + 1);
		}
		tasks.add(task);
	}

	/**
	 * Send available tasks to the client
	 * @param socket
	 * @return
	 */
	private static boolean returnAvailableTasks(Socket socket) {
		try {
			AvailableTask avTasks = new AvailableTask(new ArrayList<Task>(tasks),
					new ArrayList<String>(sockets.keySet()));
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(avTasks);
			oos.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Update all connected clients on tasks change.
	 * 
	 * @param clientName
	 */
	public static void updateClients(String clientName) {
		List<String> clientForRemove = new ArrayList<>();
		for (java.util.Map.Entry<String, Socket> entry : sockets.entrySet()) {
			try {
				if(!returnAvailableTasks(entry.getValue())) {
					clientForRemove.add(entry.getKey());
				} else{
					System.out.println(entry.getKey() + " was updated.");
				}
			} catch (Exception e) {
				System.out.println("Can not update client  : " + entry.getKey() + ". Reason : " + e.getMessage());
				e.printStackTrace();
			}
		}
		if(!clientForRemove.isEmpty()) {
			for(String client : clientForRemove) {
				sockets.remove(client);
			}
		}
		clientForRemove.clear();
	}

	/**
	 * Load list with tasks on server starting
	 */
	private static void loadTasks() {
		for (int i = 0; i < 5; i++) {
			Task task = new Task("Task " + i, i);
			tasks.add(task);
		}

	}
}
