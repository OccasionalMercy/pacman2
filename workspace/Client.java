

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client {
    
   
    
    
    
    
    public static void main(String[] args) {

        JFrame pacman = new JFrame();
        pacman.add(new Board());
        
        pacman.setTitle("Pacman");
        pacman.setSize(380, 420);
        pacman.setLocationRelativeTo(null);
        pacman.setVisible(true);
        
        try { 
            socket = new Socket(InetAddress.getLocalHost().getHostName(), 9876);
        } catch(Exception exp) {
            System.out.println("Didnt Work");
        }

        

        

        

        
    }

    private static class SendMessages extends Thread {
        public void send(String message, Socket socket) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
            System.out.println("Sent: " + message);

            ObjectOutputStream out = null;
            ObjectInputStream in = null;

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            

            out.flush();
            out.close();
            in.close();
            socket.close();
        }

        public void run() {
            System.out.println("Waiting...");
        }
    }

    private static class MessageReceiver implements Runnable {
        private Socket socket;

        public MessageReceiver(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                String serverMessage = (String) is.readObject();

                while (serverMessage != null) {
                    d.setText(d.getText() + serverMessage);
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            }
        }
    }
}

