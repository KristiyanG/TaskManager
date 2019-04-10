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

public class Server {

	private static List<Task> tasks = new ArrayList<Task>();
	
	private static Map<String,Socket> sockets = new HashMap<String,Socket>();
	

    private static int PORT;

    private static boolean SILENT_MODE_ON;
    

    public static int getPORT() {
        return PORT;
    }

    public static boolean isSilentModeOn() {
        return SILENT_MODE_ON;
    }

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		loadTasks();
		ServerSocket server = new ServerSocket(Constants.PORT);
		while (true) {
			System.out.println("Waiting for client request");
			Socket socket = server.accept();
//			socket.setKeepAlive(true);
			// read from socket to ObjectInputStream object
			clientRequest(socket);
		}
	}

	private static void clientRequest(Socket socket) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Action action = (Action) ois.readObject();
		String clientName = action.getClientName();
		System.out.println("Client request - " + clientName);
		if(sockets.get(clientName) == null) {
			sockets.put(clientName, socket);
		}
		if (action.getAction().equals(ActionType.GET_TASK)) {
			GetTask getTask = (GetTask) action;
			Task task = ((GetTask) action).getTask();
			Task serverTask = tasks.get(getTask.getTaskIndex());
			serverTask.setStatus(Status.IN_PROGRESS);
			System.out.println("Object Received: " + task.getTitle() + " from " + clientName);
			// create ObjectOutputStream object
			updateClients(clientName);
			returnAvailableTasks(socket);
		} else if (action.getAction().equals(ActionType.COMPLETE_TASK)) {
			CompleteTask complTask = (CompleteTask) action;
			System.out.println("Object Received: " + complTask.getTask().getTitle());
			// create ObjectOutputStream object
//				 write object to Socket
			complTask.getTask().setStatus(Status.COMPLETED);		// close resources
//			tasks.get(complTask.getTaskIndex()).setStatus(Status.COMPLETED);
			tasks.remove(complTask.getTaskIndex());
			updateClients(clientName);
			returnAvailableTasks(socket);
		} else if (action.getAction().equals(ActionType.LOGIN)) {
			Login login = (Login) action;
			System.out.println("Login from : " + login.getClientName());
			// create ObjectOutputStream object
//				 write object to Socket
			returnAvailableTasks(socket);
			updateClients(clientName);
		}  else if (action.getAction().equals(ActionType.CREATE_TASK)) {
			CreateTask login = (CreateTask) action;
			System.out.println("Create task : " + login.getClientName());
			addTask(login.getTask());
			updateClients(clientName);
			returnAvailableTasks(socket);
		} else if (action.getAction().equals(ActionType.DELETE_TASK)) {
			DeleteTask login = (DeleteTask) action;
			System.out.println("Delete task : " + login.getClientName());
			tasks.remove(login.getTaskIndex());
			updateClients(clientName);
			returnAvailableTasks(socket);
		} else if (action.getAction().equals(ActionType.AVAILABLE_TASKS)) {
			updateClients(clientName);
			returnAvailableTasks(socket);
		} else {
			System.out.println("This action is not implemented, yet...: ");
			// create ObjectOutputStream object
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			// write object to Socket
			oos.writeObject(new Object());
			// close resources
			ois.close();
			oos.close();
		}
//		socket.close();
	}

	private static void addTask(Task task) {
		if(task.getTitle().isEmpty()) {
			task.setTitle("Task" + tasks.size()+1);
		}
		tasks.add(task);
	}

	private static void returnAvailableTasks(Socket socket) throws IOException {
		if(socket.isClosed()) {
			System.err.println("Client socket is closed...");
			return;
		}
		AvailableTask avTasks = new AvailableTask(new ArrayList<Task>(tasks),new ArrayList<String>(sockets.keySet()));
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(avTasks);	
		oos.flush();// close resources
	}
	
	public static void updateClients(String clientName) {
		for(java.util.Map.Entry<String, Socket> entry : sockets.entrySet()) {
			/*if(entry.getKey().equals(clientName)) {
				System.out.println("Skip client update.." + clientName);
				continue;
			}*/
			try {
				returnAvailableTasks(entry.getValue());
				System.out.println(entry.getKey() + " was updated.");
			} catch (Exception e) {
				System.out.println("Can not update client  : " + entry.getKey() + ". Reason : "  + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static void loadTasks() {
		for (int i = 0; i < 5; i++) {
			Task task = new Task("Task " + i, i);
			tasks.add(task);
		}

	}
}
