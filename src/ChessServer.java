// ChessNet server
// Design and implementation: Ossi Tammi 2015

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class ChessServer extends JFrame implements GameConstants {
	// The main function
	public static void main(String[] args){
		ChessServer chessboard = new ChessServer();
	}
	
	public ChessServer(){
		// Text area for logging server information
		JTextArea serverLog = new JTextArea();
		// Hold text area
		JScrollPane scollPane = new JScrollPane(serverLog);
		// Add scrollpane to frame
		add(scollPane, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setTitle("ChessNet Server");
		setVisible(true);
		
		try{
			// Create a new server socket
			ServerSocket serverSocket = new ServerSocket(8000);
			serverLog.append(new Date() + 
					": ChessNet server started at socket 8000\n");
			
			// Number of session
			int sessionNum = 1;
			
			// Create a new session when two players have arrived
			while(true){
				serverLog.append(new Date() + 
						": Waiting for players to join session " + sessionNum + "\n");
			
				// Connection socket for player 1
				Socket player1 = serverSocket.accept();
				serverLog.append(new Date() +
						" Player 1 has joined game session " + sessionNum + "\n");
				serverLog.append("Player 1's host name is " + 
						player1.getInetAddress().getHostName() + ", IP: " + 
						player1.getInetAddress().getHostAddress() + "\n");
				
				// Shout player 1 his identification number (1)
				new DataOutputStream(
						player1.getOutputStream()).writeInt(PLAYER1);
				
				// Connection socket for player 2
				Socket player2 = serverSocket.accept();
				serverLog.append(new Date() + 
						" Player 2 has joined game session " + sessionNum + "\n");
				serverLog.append("Player 2's host name is " + 
						player2.getInetAddress().getHostName() + ", IP: " +
						player2.getInetAddress().getHostAddress() + "\n");
				
				// Shout player 2 his identification number (2)
				new DataOutputStream(
						player2.getOutputStream()).writeInt(PLAYER2);
				
				// Display current session and increase session number
				serverLog.append(new Date() + ": Starting a thread for session: " + 
						sessionNum++ + "\n");
			
				// Create a thread for current session of two player
				SessionHandler session = new SessionHandler(player1, player2);
				
				// Start the game thread
				new Thread(session).start();
			}
		}
		catch(IOException ex){
			System.err.println(ex);
		}
	}
}

// Class for handling a chess session of two players
class SessionHandler implements Runnable, GameConstants {
	private Socket player1;
	private Socket player2;
	
	// IO from and to players
	private DataInputStream fromPlayer1;
	private DataOutputStream toPlayer1;
	private DataInputStream fromPlayer2;
	private DataOutputStream toPlayer2;
	
	// Boolean to check wether the game should continue
	private boolean continueGame = true;
	
	// Constuctor for the thread
	public SessionHandler(Socket player1, Socket player2){
		this.player1 = player1;
		this.player2 = player2;
	}
	
	// Implement run() method from Runnable
	@Override 
	public void run(){
		try{
			int gameStatus;
			// IO streams
			DataInputStream fromPlayer1 = new DataInputStream(
					player1.getInputStream());
			DataOutputStream toPlayer1 = new DataOutputStream(
					player1.getOutputStream());
			DataInputStream fromPlayer2 = new DataInputStream(
					player2.getInputStream());
			DataOutputStream toPlayer2 = new DataOutputStream(
					player2.getOutputStream());
			
			// Notify player 1 to start TODO
			toPlayer1.writeInt(1);
			
			// Take turns and notify players about the state of the game
			while(true){
				// Player 1, Go!
				int newRow = fromPlayer1.readInt();
				int newCol = fromPlayer1.readInt();
				char rank = fromPlayer1.readChar();
				int prevRow = fromPlayer1.readInt();
				int prevCol = fromPlayer1.readInt();
				
				// Player 2 is on and receives move
				movePiece(toPlayer2, newRow, newCol, rank, prevRow, prevCol);
				// Player 2 sends status to server
				gameStatus = fromPlayer2.readInt();
				// What is the game status?
				if(gameStatus == CONTINUE || gameStatus == CHECK){
					toPlayer2.writeInt(CONTINUE);
					toPlayer1.writeInt(OPPTURN);
				}
				// Checkmate or stalemate
				else if(gameStatus == CHECKMATE){
					toPlayer2.writeInt(PLAYER1_WON);
					toPlayer1.writeInt(PLAYER1_WON);
				}
				else if(gameStatus == STALEMATE){
					toPlayer2.writeInt(DRAW);
					toPlayer1.writeInt(DRAW);
				}
							
				// Player 2 thinks of move. 
				// This might take a lot of milk and cookies.
				newRow = fromPlayer2.readInt();
				newCol = fromPlayer2.readInt();
				rank = fromPlayer2.readChar();
				prevRow = fromPlayer2.readInt();
				prevCol = fromPlayer2.readInt();
				
				// Player 1 is on and receives a move
				movePiece(toPlayer1, newRow, newCol, rank, prevRow, prevCol);
				// Player 1 sends status
				gameStatus = fromPlayer1.readInt();
				// What is the game status?
				if(gameStatus == CONTINUE || gameStatus == CHECK){
					toPlayer1.writeInt(CONTINUE);
					toPlayer2.writeInt(OPPTURN);
				}
				else if(gameStatus == CHECKMATE){
					toPlayer1.writeInt(PLAYER2_WON);
					toPlayer2.writeInt(PLAYER2_WON);
				}
				else if(gameStatus == STALEMATE){
					toPlayer1.writeInt(DRAW);
					toPlayer2.writeInt(DRAW);
				}
				
				// Merry-goes-around				
			}
		}
		catch(IOException ex){
			System.err.println(ex);
		}
	}

	// Move a piece on the chess board TODO
	private void movePiece(DataOutputStream out, int row, int column, char rank, 
			int prevRow, int prevCol) throws IOException {
		out.writeInt(row);
		out.writeInt(column);
		out.writeChar(rank);
		out.writeInt(prevRow);
		out.writeInt(prevCol);
	}
}