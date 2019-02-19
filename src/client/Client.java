package client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import core.action.Action;
import core.action.AvailableTask;
import core.action.CompleteTask;
import core.action.Constants;
import core.action.GetTask;
import core.action.Task;

import javax.swing.JScrollPane;

public class Client implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3195074717605829379L;

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	private Task task;

	private boolean toClose;

	private static JFrame frame = new JFrame();

	private static Socket clientSocket;

	public static void main(String[] args) throws Exception {
		InetAddress host = InetAddress.getLocalHost();
		clientSocket = new Socket(host.getHostName(), Constants.PORT);
		ClientGUI application = new ClientGUI(clientSocket);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.init();
		application.showAvailableTasks(getAvailableTask());
		application.waitForMessage();
	}

	private static void showAvailableTasks(AvailableTask avTasks) {
//		frame.removeAll();
//		frame = new JFrame();
		List<Task> tasks = avTasks.getTasks();

		String data[][] = new String[tasks.size()][];

		for (int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			String[] arr = { task.getTitle(), String.valueOf(task.getDuration()), String.valueOf(task.getStatus()) };
			data[i] = arr;
		}

		String column[] = { "DURATION", "TITLE", "STATUS" };
		JTable jt = new JTable(data, column);
		jt.invalidate();
		jt.repaint();
		jt.setBounds(30, 40, 200, 300);
		JPanel sp = new JPanel();
		sp.removeAll();
		sp.add(jt);
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

	public static void getTask(Task task2, int taskIndex) throws Exception {
		GetTask getTaskAction = new GetTask();
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
		ois.close();
		oos.close();
		completedTask(task2, taskIndex);
	}

	private static void completedTask(Task task2, int taskIndex) throws Exception {
		// TODO Auto-generated method stub
		Thread.sleep(task2.getDuration());
		CompleteTask complTask = new CompleteTask(task2, taskIndex);
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
	
	private static AvailableTask getAvailableTask() throws Exception {
		AvailableTask avTasks = new AvailableTask(null);
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(clientSocket.getOutputStream());
		oos.writeObject(avTasks);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(clientSocket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		System.out.println("Message: " + tasks.getTasks());
		return tasks;

	}
	
    
}