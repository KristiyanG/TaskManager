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
	
	public Client() {
		InetAddress host;
		try {
			host = InetAddress.getLocalHost();
			clientSocket = new Socket(host.getHostName(), Constants.PORT);
			clientSocket.setKeepAlive(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		
		Client application = new Client();
		application.init();
		while (true) {
			try {
				ObjectInputStream ois = new ObjectInputStream(application.clientSocket.getInputStream());
				Action action = (Action) ois.readObject();
//				if (action != null && action.getAction().equals(ActionType.AVAILABLE_TASKS)) {
					ois = new ObjectInputStream(application.clientSocket.getInputStream());
					AvailableTask tasks = (AvailableTask) ois.readObject();
					System.out.println("Message: " + tasks.getTasks());
					application.showAvailableTasks(tasks);
//				}
			} catch (StreamCorruptedException e) {
				System.err.println("Ex in client " + application.getName() + ". Ex : " + e.getMessage());
			}

		}
	}

	public void init() {
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		try {

			do {
				name = JOptionPane.showInputDialog("Please enter your name");
			} while ((name == null) || (name.length() == 0));
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
//		frame = new JFrame();
		if(jt!=null) {
			frame.remove(sp);
			frame.remove(jt);
			frame.repaint();
		}
		sp = new JPanel();
		List<Task> tasks = avTasks.getTasks();

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

	public void getTask(Task task2, int taskIndex) throws Exception {

		InetAddress host = InetAddress.getLocalHost();
		clientSocket = new Socket(host.getHostName(), Constants.PORT);
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
		Thread.sleep(task2.getDuration());
		System.out.println("Message: " + tasks.getTasks());
		// close resources
		completedTask(task2, taskIndex);
	}

	private void completedTask(Task task2, int taskIndex) throws Exception {

		InetAddress host = InetAddress.getLocalHost();
		Thread.sleep(task2.getDuration());
		clientSocket = new Socket(host.getHostName(), Constants.PORT);
		CompleteTask complTask = new CompleteTask(task2, taskIndex,this);
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(complTask);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(clientSocket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		showAvailableTasks(tasks);
		System.out.println("Working for task period...");
		System.out.println("Message: " + tasks.getTasks());
		// close resources
		ois.close();
		oos.close();
	}

}