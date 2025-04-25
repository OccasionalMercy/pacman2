import java.net.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JFrame;


public class Server {

	public static final int port = 9876;
	static ArrayList<ObjectOutputStream> ostreams;

	public static void main(String[] args) {
		


		ServerSocket listener; 
		

		try {
			listener = new ServerSocket(port);
			int idNum = 0;
			ostreams = new ArrayList<ObjectOutputStream>();
			System.out.println("Your on port: " + port);
			while (true) {

				Socket connection = listener.accept();
				ConnectionHandler a = new ConnectionHandler(connection, idNum);
				idNum++;
				ostreams.add(new ObjectOutputStream(connection.getOutputStream()));
				a.start();
			}
		} catch (Exception exp) {
			System.out.println("Shut down");
			System.out.println("Error:  " + exp);
			return;
		}

	} 

	

	private static class ConnectionHandler extends Thread {
		Socket client;
		int id;

		ConnectionHandler(Socket socket, int myID) {
			client = socket;
			id = myID;
		}

		public void run() {
			String add = client.getInetAddress().toString();
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(client.getInputStream());
			} catch (IOException exp) {

				exp.printStackTrace();
			}
			String message = null;

			while (true) {
				try {
					try {
						message = (String)(ois.readObject());
					} catch (ClassNotFoundException | IOException exp) {
						exp.printStackTrace();
					}
					if(message.equals("##SECRETCODE:EXIT")) {
						for(int i = 0; i < ostreams.size(); i++) {
							if(ostreams.get(i).equals(client.getOutputStream())) {
								ostreams.get(i).close();
								ostreams.remove(i);
							}
						}
						ois.close();
						client.close();
						return;
					}
					for(int i = 0; i < ostreams.size(); i++) {
						ostreams.get(i).writeObject(message);
					}
				} catch (Exception e) {
					System.out.println("Error on connection with: " + add + ": " + e);
				}
			}
		}
	}

}



