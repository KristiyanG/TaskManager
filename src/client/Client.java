package client;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import javax.swing.JScrollPane;

import core.AvailableTask;
import core.CompleteTask;
import core.Constants;
import core.GetTask;
import core.Task;

public class Client {

	private Task task;

	private static JFrame frame = new JFrame();

	public static void main(String[] args) throws Exception {

		Thread updateThread = new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						getAvailableTask();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
//		serverConnect();
		getAvailableTask();
		((Thread) updateThread).start();
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

	protected static void getTask(Task task2, int taskIndex) throws Exception {
		GetTask getTaskAction = new GetTask();
		getTaskAction.setTask(task2);
		getTaskAction.setTaskIndex(taskIndex);
		InetAddress host = InetAddress.getLocalHost();
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		socket = new Socket(host.getHostName(), Constants.PORT);
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(getTaskAction);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(socket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		showAvailableTasks(tasks);
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
		InetAddress host = InetAddress.getLocalHost();
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		socket = new Socket(host.getHostName(), Constants.PORT);
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(complTask);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(socket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		showAvailableTasks(tasks);
		System.out.println("Working for task period...");
		System.out.println("Message: " + tasks.getTasks());
		// close resources
		ois.close();
		oos.close();
	}

	private static void serverConnect()
			throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
		InetAddress host = InetAddress.getLocalHost();
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		socket = new Socket(host.getHostName(), Constants.PORT);
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(new Task("Name", 2000));
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(socket.getInputStream());
		Task taskFromClient = (Task) ois.readObject();
		System.out
				.println("Message: " + taskFromClient.getTitle() + " and task status - " + taskFromClient.getStatus());
		// close resources
		ois.close();
		oos.close();
		Thread.sleep(100);
		socket.close();
	}

	private static void getAvailableTask() throws Exception {
		AvailableTask avTasks = new AvailableTask(null);
		InetAddress host = InetAddress.getLocalHost();
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		// establish socket connection to server
		socket = new Socket(host.getHostName(), Constants.PORT);
		// write to socket using ObjectOutputStream
		oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(avTasks);
		System.out.println("Sending request to Socket Server");

		// read the server response message
		ois = new ObjectInputStream(socket.getInputStream());
		AvailableTask tasks = (AvailableTask) ois.readObject();
		System.out.println("Message: " + tasks.getTasks());
		// close resources
		ois.close();
		oos.close();
		socket.close();
		showAvailableTasks(tasks);

	}

}