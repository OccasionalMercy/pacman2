import java.net.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;


public class Server implements ActionListener {

	public static final int port = 3333;
	static ArrayList<ObjectOutputStream> ostreams;
	Timer timer;
	private ServerSocket listener; 
	private Socket connection;
	private ConnectionHandler[] h;


	 private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int PACMAN_SPEED = 6;


	private int[] dx, dy;
	private final int MAX_GHOSTS = 12;
	private int N_GHOSTS = 6;
	private int currentSpeed = 3;
	private int score;
	private short[] screenData;
	private int[] req_dx, req_dy;
    



	
	private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;
	private int[] pacman_x, pacman_y, pacmand_x, pacmand_y;
	private final int maxSpeed = 6;
	private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
	private boolean dying = false;
    private final short levelData[] = {
        19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
        25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
        1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
        1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
        1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
        1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
        9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };


	private void movePacman1(int i) {

        int pos;
        short ch;

        if (req_dx[i] == -pacmand_x[i] && req_dy[i] == -pacmand_y[i]) {
            pacmand_x[i] = req_dx[i];
            pacmand_y[i] = req_dy[i];
            
        }

        if (pacman_x[i] % BLOCK_SIZE == 0 && pacman_y[i] % BLOCK_SIZE == 0) {
            pos = pacman_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y[i] / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx[i] != 0 || req_dy[i] != 0) {
                if (!((req_dx[i] == -1 && req_dy[i] == 0 && (ch & 1) != 0)
                        || (req_dx[i] == 1 && req_dy[i] == 0 && (ch & 4) != 0)
                        || (req_dx[i] == 0 && req_dy[i] == -1 && (ch & 2) != 0)
                        || (req_dx[i] == 0 && req_dy[i] == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    
                }
            }

            // Check for standstill
            if ((pacmand_x[i] == -1 && pacmand_y[i] == 0 && (ch & 1) != 0)
                    || (pacmand_x[i] == 1 && pacmand_y[i] == 0 && (ch & 4) != 0)
                    || (pacmand_x[i] == 0 && pacmand_y[i] == -1 && (ch & 2) != 0)
                    || (pacmand_x[i] == 0 && pacmand_y[i] == 1 && (ch & 8) != 0)) {
                pacmand_x[i] = 0;
                pacmand_y[i] = 0;
            }
        }
        pacman_x[i] = pacman_x[i] + PACMAN_SPEED * pacmand_x[i];
        pacman_y[i] = pacman_y[i] + PACMAN_SPEED * pacmand_y[i];
    }


	private void moveGhosts(){
 short i;
        int pos;
        int count;

        for (i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);}

			if (pacman_x[0] > (ghost_x[i] - 12) && pacman_x[0] < (ghost_x[i] + 12)
                    && pacman_y[0] > (ghost_y[i] - 12) && pacman_y[0] < (ghost_y[i] + 12)
                    ) {

                dying = true;
            }

            if (pacman_x[1] > (ghost_x[i] - 12) && pacman_x[1] < (ghost_x[i] + 12)
                    && pacman_y[1] > (ghost_y[i] - 12) && pacman_y[1] < (ghost_y[i] + 12)
                    ) {

                dying = true;
            }

	}
	private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        int random;

        for (i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x[0] = 7 * BLOCK_SIZE;
        pacman_y[0] = 11 * BLOCK_SIZE;
        pacmand_x[0] = 0;
        pacmand_y[0] = 0;
        req_dx[0] = 0;
        req_dy[0] = 0;

        pacman_x[1] = 7 * BLOCK_SIZE;
        pacman_y[1] = 11 * BLOCK_SIZE;
        pacmand_x[1] = 0;
        pacmand_y[1] = 0;
        req_dx[1] = 0;
        req_dy[1] = 0;
        
        dying = false;
    }
	public Server(){

        req_dx = new int [2];
        req_dy = new int [2];

        pacman_x = new int [2];
        pacman_y = new int [2];

        pacmand_x = new int [2];
        pacmand_y = new int [2];
		    
        req_dx[0] = 5;
        req_dx[1] = 5;
        req_dy[0] = 5;
        req_dy[1] = 5;

        screenData = new short[N_BLOCKS * N_BLOCKS];


        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];

        dx = new int[4];
        dy = new int[4];


		h = new ConnectionHandler[2];

		try {
			listener = new ServerSocket(port);
			
			ostreams = new ArrayList<ObjectOutputStream>();
			System.out.println("Your on port: " + port);
			int connections =0;
			while (connections<2) {
				
				connection = listener.accept();
				h[connections] = new ConnectionHandler(connection, connections);
                
				connections ++;
				
			}
			for(ConnectionHandler handler: h){
				ostreams.add(handler.oos);
				handler.oos.writeObject("connected");
				handler.start();
			}


		} catch (Exception exp) {
			System.out.println("Shut down");
			System.out.println("Error:  " + exp);
		}

		timer = new Timer(40, this);
		timer.start();

	}
	public static void main(String[] args) {
		new Server();

	} 

	

	private class ConnectionHandler extends Thread {
		public ObjectInputStream ois;
		public ObjectOutputStream oos;
		public int playerNum;

		ConnectionHandler(Socket socket, int num) {
			playerNum = num;
			try{
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			}
			catch(Exception e){}
		}

        public int get(){
            return playerNum;
        }

        public void run(){
            while(true){
                try{
                    System.out.println((KeyEvent)ois.readObject());
                }
                catch (Exception e){

                }
            }
        }

		}


		


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("sending");
		//loop through connections and send the state (map data, pacman location data, etc.)
		//to each client.

		for(ConnectionHandler handler: h){
				movePacman1(handler.get());
				moveGhosts();
				checkMaze();
				try {
					handler.oos.writeObject(ghost_x);
					handler.oos.writeObject(ghost_y);
					handler.oos.writeObject(ghost_dx);
					handler.oos.writeObject(ghost_dy);
					handler.oos.writeObject(ghostSpeed);
					handler.oos.writeObject(pacman_x);
					handler.oos.writeObject(pacman_y);
					handler.oos.writeObject(pacmand_x);
					handler.oos.writeObject(pacmand_y);
					handler.oos.writeObject(levelData);
					handler.oos.writeObject(dying);



				
				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

	}
}}



