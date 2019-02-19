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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import client.Client;
import core.action.Action;
import core.action.AvailableTask;
import core.action.CompleteTask;
import core.action.Constants;
import core.action.GetTask;
import core.action.Login;
import core.action.Task;
import core.action.Constants.ActionType;
import core.action.Constants.Status;

public class Server {

	private static List<Task> tasks = new ArrayList<Task>();

	private static Set<Socket> sockets = new HashSet<Socket>();
	
	private static final boolean SERVER_UP = true;

    private static int PORT;

    private static boolean SILENT_MODE_ON;
    
    private static boolean update = false;

    public static void init(int port, boolean silentModeOn) {
        try {
            PORT = port;
            SILENT_MODE_ON = silentModeOn;
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println(String.format("Server accept requests on %d port.", PORT));
            //Runtime.getRuntime().availableProcessors()
            Executor exec = Executors.newCachedThreadPool();
            while (SERVER_UP) {
//                CompletableFuture.runAsync(new ClientSession(serverSocket.accept()), exec);
            }
        } catch (IOException ex) {
            System.out.println(String.format("Server could not listen on port %s. " +
                    "Please run the server with another port.", PORT));
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public static int getPORT() {
        return PORT;
    }

    public static boolean isSilentModeOn() {
        return SILENT_MODE_ON;
    }

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		loadTasks();
		ServerSocket server = new ServerSocket(Constants.PORT);
		new UpdateClient().start();
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
				returnAvailableTasks(socket);
				update = true;
			} else if (action.getAction().equals(ActionType.COMPLETE_TASK)) {
				CompleteTask complTask = (CompleteTask) action;
				System.out.println("Object Received: " + complTask.getTask().getTitle());
				// create ObjectOutputStream object
//				 write object to Socket
				complTask.getTask().setStatus(Status.COMPLETED);		// close resources
				tasks.get(complTask.getTaskIndex()).setStatus(Status.COMPLETED);
				update = true;
				returnAvailableTasks(socket);
			} else if (action.getAction().equals(ActionType.LOGIN)) {
				Login login = (Login) action;
				sockets.add(socket);
				System.out.println("Login from : " + login.getClient().getName());
				// create ObjectOutputStream object
//				 write object to Socket
				returnAvailableTasks(socket);
			} else if (action.getAction().equals(ActionType.AVAILABLE_TASKS)) {
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
				socket.close();
			}
			// terminate the server if client sends exit request
		}
	}

	private static void returnAvailableTasks(Socket socket) throws IOException {
		AvailableTask avTasks = new AvailableTask(new ArrayList<Task>(tasks));
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(avTasks);		// close resources
		
	}

	private static void loadTasks() {
		for (int i = 0; i < 5; i++) {
			Task task = new Task("Task " + i, i);
			tasks.add(task);
		}

	}
	
	static class UpdateClient extends Thread {
		@Override
		public void run() {
			while(true){
				if(!update){
					continue;
				}
			for(Socket s : sockets) {
				try {
					returnAvailableTasks(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			update = false;
			}
					
		}
	}
}
