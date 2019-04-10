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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import core.action.AvailableTask;
import core.action.CompleteTask;
import core.action.Constants;
import core.action.GetTask;
import core.action.Login;
import core.action.Task;
import core.action.Constants.Status;
import core.action.CreateTask;
import core.action.DeleteTask;

/**
 *  
 * @author KRG
 *
 */
public class Client implements Serializable {

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
	
	private int completedTask = 0;

	public static void main(String[] args) throws Exception {

		Client application = new Client();
		application.init();
		application.listenForUpdate();
	}

	/**
	 * Listen from update from server
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void listenForUpdate() throws IOException, ClassNotFoundException {
		while (true) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
				AvailableTask login = new AvailableTask(null, null);
				login.setClientName(name);
				oos.writeObject(login);
	
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
				AvailableTask tasks = (AvailableTask) ois.readObject();
				System.out.println("Message: " + tasks.getTasks());
				showAvailableTasks(tasks);
			} catch (StreamCorruptedException e) {
				System.err.println("Ex in client " + getName() + ". Ex : " + e.getMessage());
			}

		}
	}

	/**
	 * Initialization method
	 * 
	 */
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
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(login);
			System.out.println("Sending request to Socket Server");

			ois = new ObjectInputStream(clientSocket.getInputStream());
			AvailableTask tasks = (AvailableTask) ois.readObject();
			showAvailableTasks(tasks);
		} catch (Exception e) {
			System.out.println("exception: " + e);
		}
	}

	/**
	 * Construct frame with table and buttons and display it
	 * @param avTasks
	 */
	private void showAvailableTasks(AvailableTask avTasks) {
		if (jt != null) {
			frame.remove(sp);
			frame.remove(jt);
			frame.repaint();
		}
		sp = new JPanel();
		sp.removeAll();
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
		JLabel completedTasks = new JLabel("Completed tasks :" + this.completedTask);
		completedTasks.setHorizontalAlignment(2);
		JLabel avUsersLabel = new JLabel("Available user:" + avTasks.getClients().size());
		sp.add(jl);
		sp.add(jt);
		sp.add(avUsersLabel);
		JButton jb = new JButton("Get Task");
		sp.add(jb);
		jb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(tasks.get(jt.getSelectedRow()).getStatus().equals(Status.IN_PROGRESS)) {
						System.err.println("This task is already in progress...");
						return;
					}
					getTask(tasks.get(jt.getSelectedRow()), jt.getSelectedRow());

					completedTask(tasks.get(jt.getSelectedRow()), jt.getSelectedRow());
				} catch (Exception e1) {
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
					if(tasks.size() < jt.getSelectedRow()) {
						return;
					}
					deleteTask(tasks.get(jt.getSelectedRow()), jt.getSelectedRow());
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		sp.add(completedTasks);
		frame.add(sp);
		frame.setSize(300, 400);
		frame.setVisible(true);

	}

	/**
	 *  Delete task method 
	 *  
	 * @param task
	 * @param taskIndex
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected void deleteTask(Task task, int taskIndex) throws IOException, ClassNotFoundException {

		InetAddress host = InetAddress.getLocalHost();
		Socket clientSocket = new Socket(host.getHostName(), Constants.PORT);
		clientSocket.setKeepAlive(true);
		DeleteTask getTaskAction = new DeleteTask(task,this);
		getTaskAction.setTaskIndex(taskIndex);
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(getTaskAction);
		System.out.println("Sending request to Socket Server");
	}

	/**
	 * Create task method
	 */
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
			System.out.println("Inget " + isInteger(duration));
			System.out.println("socket = " + clientSocket);
			Task t = new Task("", Integer.valueOf(duration));
			CreateTask login = new CreateTask(this, t);
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			oos.writeObject(login);
			System.out.println("Sending request to Socket Server");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *  Util method for check if is integer
	 *  
	 * @param duration
	 * @return
	 */
	private boolean isInteger(String duration) {
		try{			
			Integer.valueOf(duration);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Get task method. Marks tasks and create request to the server
	 * 
	 * @param task2
	 * @param taskIndex
	 * @throws Exception
	 */
	public void getTask(Task task2, int taskIndex) throws Exception {

		InetAddress host = InetAddress.getLocalHost();
		Socket clientSocket = new Socket(host.getHostName(), Constants.PORT);
		GetTask getTaskAction = new GetTask(this);
		getTaskAction.setTask(task2);
		getTaskAction.setTaskIndex(taskIndex);
		ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(getTaskAction);
		System.out.println("Sending request to Socket Server");
	}

	/**
	 *  Complete task
	 *  
	 * @param task2
	 * @param taskIndex
	 * @throws Exception
	 */
	private void completedTask(Task task2, int taskIndex) throws Exception {

		InetAddress host = InetAddress.getLocalHost();
		Thread.sleep(task2.getDuration());
		Socket clientSocket = new Socket(host.getHostName(), Constants.PORT);
		CompleteTask complTask = new CompleteTask(task2, taskIndex, this);
		ObjectOutputStream oos = null;
		Thread.sleep(task2.getDuration());
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(complTask);
		System.out.println("Sending request to Socket Server");

		completedTask++;
	}

}