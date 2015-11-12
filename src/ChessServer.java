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
	
	// TODO: Create and initialize chessboard
	private char[][] cell = new char[10][10];
	
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
		
		// TODI : Initialize chessboard
		for(int i = 0; i < 8; ++i){
			for(int j = 0; j < 8; ++j){
				cell[i][j] = ' ';
			}
		}
	}
	
	// Implement run() method from Runnable
	@Override 
	public void run(){
		try{
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
				// Receive the move from player 1 TODO
				int row = fromPlayer1.readInt();
				int column = fromPlayer1.readInt();
				cell[row][column] = 'X';
				
				// Check if it is a checkmate
				if(checkmate('X')){
					toPlayer1.writeInt(PLAYER1_WON);
					toPlayer2.writeInt(PLAYER1_WON);
					movePiece(toPlayer2, row, column);
					break;
				}
				// Stalemate means immediate draw
				else if(stalemate()){ 
					toPlayer1.writeInt(DRAW);
					toPlayer2.writeInt(DRAW);
					movePiece(toPlayer2, row, column);
					break;
				}
				else{
					// Game continues (player 2's turn)
					toPlayer2.writeInt(CONTINUE);
					movePiece(toPlayer2, row, column);
				}
				
				// Player 2 is on
				row = fromPlayer2.readInt();
				column = fromPlayer2.readInt();
				cell[row][column] = 'O';
				
				// Check if the player 2 has won
				if(checkmate('O')){
					toPlayer1.writeInt(PLAYER2_WON);
					toPlayer2.writeInt(PLAYER2_WON);
					movePiece(toPlayer2, row, column);
					break;
				}
				else if(stalemate()){
					toPlayer1.writeInt(DRAW);
					toPlayer2.writeInt(DRAW);
					movePiece(toPlayer2, row, column);
					break;
				}
				else{
					// Game continues (player 1's turn)
					toPlayer1.writeInt(CONTINUE);
					movePiece(toPlayer1, row, column);
				}
			}
		}
		catch(IOException ex){
			System.err.println(ex);
		}
	}

	// Move a piece on the chess board TODO
	private void movePiece(DataOutputStream out, int row, int column) throws IOException {
		out.writeInt(row);
		out.writeInt(column);
	}
	
	// Stalemate situation TODO
	private boolean stalemate(){
		// Not a stalemate
		return false;
	}
	
	// Check if the player has entered checkmate TODO
	private boolean checkmate(char token){
		for(int i=0; i < 8; i++){
			if ((cell[i][0] == token)
			&& (cell[i][1] == token)
			&& (cell[i][2] == token)) {
				return true; 
			}
		}
		for (int j =0;j<8;j++){ 
			if ((cell[0][j] == token)
			&& (cell[1][j] == token)
			&& (cell[2][j] == token)) { 
				return true;
			}
		}
		
		if ((cell[0][0] == token)
				&& (cell[1][1] == token)
				&& (cell[2][2] == token)) {
				return true;
		}
		
		if ((cell[0][2] == token)
				&& (cell[1][1] == token)
				&& (cell[2][0] == token)) {
				return true;
		}
		return false;
		
	}
}


