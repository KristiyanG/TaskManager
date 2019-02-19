package client;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import core.action.Action;
import core.action.AvailableTask;
import core.action.Constants;
import core.action.GetTask;
import core.action.Login;
import core.action.Constants.ActionType;
import core.action.Task;

public class ClientGUI extends JFrame {

	/**
	 * 
	 */
	private String name;
	private static final long serialVersionUID = -8877474517268367965L;
	private static JFrame frame = new JFrame();
	private static JLabel label = new JLabel("Default text");
	
	static {
		frame.add(label);
	}
	private Socket clientSocket;
	
	public ClientGUI(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void init(){
        try{
            do{
                name = JOptionPane.showInputDialog("Please enter your name");
            } while((name == null)|| (name.length()==0));
            String server = null;
            InetAddress addr = InetAddress.getByName(server);
            System.out.println("addr = " + addr);
            System.out.println("socket = " + clientSocket);
            Client cl = new Client();
            cl.setName(name);
            Login login = new Login(new Client());
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
        }catch (Exception e){
            System.out.println("exception: "+e);
        }
    }

	public void showAvailableTasks(AvailableTask avTasks) {
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
					Client.getTask(tasks.get(jt.getSelectedRow()), jt.getSelectedRow());
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

	private boolean toClose;
	
	public void waitForMessage() throws ClassNotFoundException {
        while (true) {
            try {
                if (toClose) {
                    clientSocket.close();
                    break;
                }
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        		Action action = (Action) ois.readObject();
                do {
                    if (action != null) {
                    	if(action.getAction().equals(ActionType.AVAILABLE_TASKS)){
                    		showAvailableTasks((AvailableTask)action);                    		
                    	}
                    }
                } while (action != null);
            } catch (IOException exception) {
            	label.setText(exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

}
