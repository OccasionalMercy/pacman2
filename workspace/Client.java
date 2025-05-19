

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client {
    ObjectOutputStream out;
    ObjectInputStream in;
    Board theBoard;


    


  



    public Client(){
        Socket socket;
        JFrame pacman = new JFrame();
        
        pacman.setTitle("Pacman");
        pacman.setSize(380, 420);
        pacman.setLocationRelativeTo(null);
        JLabel waiting = new JLabel("waiting for the other player");
        pacman.add(waiting);
        pacman.setVisible(true);
       
        try { 
            socket = new Socket(InetAddress.getLocalHost().getHostName(), 3333);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            while(!((String)in.readObject()).equals("connected")){
                System.out.println("got an unexpected message");
            }
          
            pacman.remove(waiting);
            theBoard = new Board(out);
            
            pacman.add(theBoard);
            
            // theBoard.initGame();
            // pacman.revalidate();
            // pacman.repaint();
            
            while(true){
                theBoard.ghost_x = (int[])(in.readObject());
                theBoard.ghost_y = (int[])(in.readObject());
                theBoard.ghost_dx = (int[])(in.readObject());
                theBoard.ghost_dy = (int[])(in.readObject());
                theBoard.ghostSpeed = (int[])(in.readObject()); 
                theBoard.pacman_x = (int[])(in.readObject());
                theBoard.pacman_y = (int[])(in.readObject());
                theBoard.pacmand_x = (int[])(in.readObject());
                theBoard.pacmand_y = (int[])(in.readObject());
                
                
                theBoard.levelData = (short[])(in.readObject());
                pacman.repaint();

            }
            



        } catch(Exception exp) {
            System.out.println(exp.getStackTrace());
        }
    }
    
    
    public static void main(String[] args) {
           new Client();
        
    }
}

