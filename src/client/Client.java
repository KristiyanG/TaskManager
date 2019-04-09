package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import core.action.Action;
import core.action.AvailableTask;
import core.action.CompleteTask;
import core.action.Constants;
import core.action.GetTask;
import core.action.Login;
import core.action.Task;
import core.action.Constants.ActionType;
import core.action.CreateTask;
import core.action.DeleteTask;

public class Client implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3195074717605829379L;

	private String name;

	public String getName() {
		return name;
	}

	private transient JFrame frame = new JFrame();

	private transient JTable jt;

	private transient Socket clientSocket;

	private transient JPanel sp;

	private transient List<Task> tasks;

	public Client() {
		InetAddress host;
		try {
//			host = InetAddress.getLocalHost();
//			clientSocket = new Socket(host.getHostName(), Constants.PORT);
//			clientSocket.setKeepAlive(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		Client application = new Client();
		application.init();
		application.listenForUpdate();
	}

	private void listenForUpdate() throws IOException, ClassNotFoundException {
		while (true) {
			try {
//				InetAddress host = InetAddress.getLocalHost();
//				clientSocket = new Socket(host.getHostName(), Constants.PORT);
//				clientSocket.setKeepAlive(true);
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				AvailableTask login = new AvailableTask(null, null);
				login.setClientName(name);
				oos.writeObject(login);
	
				// if (action != null &&
				// action.getAction().equals(ActionType.AVAILABLE_TASKS)) {
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				AvailableTask tasks = (AvailableTask) ois.readObject();
				System.out.println("Message: " + tasks.getTasks());
				if(this.tasks.size() == tasks.getTasks().size()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
				}
				showAvailableTasks(tasks);
				// }
			} catch (StreamCorruptedException e) {
				System.err.println("Ex in client " + getName() + ". Ex : " + e.getMessage());
			}

		}
	}

	public void init() {
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		try {

			do {
				name = JOptionPane.showInputDialog("Please enter your name");
			} while ((name == null) || (name.length() == 0));
			InetAddress host = InetAddress.getLocalHost();
			clientSocket = new Socket(host.getHostName(), Constants.PORT);
			clientSocket.setKeepAlive(true);
			InetAddress addr = InetAddress.getLocalHost();
			System.out.println("addr = " + addr);
			System.out.println("socket = " + clientSocket);
			Login login = new Login(this);
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			// establish socket connection to server
			// write to socket using ObjectOutputStream
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(login);
			System.out.println("Sending request to Socket Server");

			// read the server response message
			ois = new ObjectInputStream(clientSocket.getInputStream());
			AvailableTask tasks = (AvailableTask) ois.readObject();
			showAvailableTasks(tasks);
		} catch (Exception e) {
			System.out.println("exception: " + e);
		}
	}

	private void showAvailableTasks(AvailableTask avTasks) {
		// frame = new JFrame();
		if (jt != null) {
			frame.remove(sp);
			frame.remove(jt);
			frame.repaint();
		}
		sp = new JPanel();
		tasks = avTasks.getTasks();

		String data[][] = new String[tasks.size()][];

		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			String[] arr = { task.getTitle(), String.valueOf(task.getDuration()), String.valueOf(task.getStatus()) };
			data[i] = arr;
		}

		String column[] = { "DURATION", "TITLE", "STATUS" };
		jt = new JTable(data, column);
		jt.invalidate();
		jt.repaint();
		jt.setBounds(30, 40, 200, 300);
		JLabel jl = new JLabel("User:" + this.getName());
		JLabel avUsersLabel = new JLabel("Available user:" + avTasks.getClients().size());
		sp.removeAll();
		sp.add(jl);
		sp.add(jt);
		sp.add(avUsersLabel);
		JButton jb = new JButton("Get Task");
		sp.add(jb);
		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					getTask(tasks.get(jt.getSelectedRow()), jt.getSelectedRow());

					completedTask(tasks.get(jt.getSelectedRow()), jt.getSelectedRow());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		JButton createTask = new JButton("Create Task");
		sp.add(createTask);
		createTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					createTask();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		JButton updateTasks = new JButton("Delete Tasks");
		sp.add(updateTasks);
		updateTasks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					deleteTask(tasks.get(jt.getSelectedRow()), jt.getSelectedRow());
					// close resources
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		frame.add(sp);
		frame.setSize(300, 400);
		frame.setVisible(true);

	}

	protected void deleteTask(Task task, int taskIndex) throws IOException, ClassNotFoundException {

		InetAddress host = InetAddress.getLocalHost();
		Socket clientSocket = new Socket(host.getHostName(), Constants.PORT);
		clientSocket.setKeepAlive(true);
		DeleteTask getTaskAction = new DeleteTask(task,this);
		getTaskAction.setTaskIndex(taskIndex);
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(getTaskAction);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(clientSocket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		showAvailableTasks(tasks);
	}

	protected void createTask() {
		String duration = "0";
		do {
			duration = JOptionPane.showInputDialog("Please enter task duration");
		} while ((duration == null) || (!isInteger(duration)));
		try {
			InetAddress addr = InetAddress.getLocalHost();
			Socket clientSocket = new Socket(addr.getHostName(), Constants.PORT);
			clientSocket.setKeepAlive(true);
			System.out.println("addr = " + addr);
			System.out.println("socket = " + clientSocket);
			Task t = new Task("", Integer.valueOf(duration));
			CreateTask login = new CreateTask(this, t);
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			// establish socket connection to server
			// write to socket using ObjectOutputStream
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(login);
			System.out.println("Sending request to Socket Server");

			// read the server response message
			ois = new ObjectInputStream(clientSocket.getInputStream());
			AvailableTask tasks = (AvailableTask) ois.readObject();
			showAvailableTasks(tasks);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean isInteger(String duration) {
		try{
			
			Integer.getInteger(duration);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void getTask(Task task2, int taskIndex) throws Exception {

		InetAddress host = InetAddress.getLocalHost();
		Socket clientSocket = new Socket(host.getHostName(), Constants.PORT);
		GetTask getTaskAction = new GetTask(this);
		getTaskAction.setTask(task2);
		getTaskAction.setTaskIndex(taskIndex);
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(getTaskAction);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(clientSocket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		System.out.println("Working for task period...");
		System.out.println("Message: " + tasks.getTasks());
//		showAvailableTasks(tasks);
	}

	private void completedTask(Task task2, int taskIndex) throws Exception {

		InetAddress host = InetAddress.getLocalHost();
		Thread.sleep(task2.getDuration());
		Socket clientSocket = new Socket(host.getHostName(), Constants.PORT);
		CompleteTask complTask = new CompleteTask(task2, taskIndex, this);
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		// write to socket using ObjectOutputStream

		Thread.sleep(task2.getDuration());
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(complTask);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(clientSocket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		showAvailableTasks(tasks);
		System.out.println("Working for task period...");
		System.out.println("Message: " + tasks.getTasks());

		showAvailableTasks(tasks);
		// close resources
//		ois.close();
//		oos.close();
	}

}