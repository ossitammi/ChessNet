// ChessNet client
// Design and implementation: Ossi Tammi 2015

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.net.*;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.Toolkit;

public class ChessClient extends JApplet implements Runnable, GameConstants {
	// Check out whos turn it is
	private boolean isMyTurn = false;
	// Continue the game
	private boolean continueGame = true;
	// Wait for the player to make his move
	private boolean makingMove = true;
	
	// Run ChessNet as an applet
	private boolean isStandAlone = false;
	
	// Indicate the colour of the player
	private String myColour = "";
	// .. And the opponent
	private String oppColour = ""; 
	
	// Initialize chessboard TODO
	private Cell[][] cell = new Cell[8][8];
	
	// Initialize title and status labels
	private JLabel jlabelTitle = new JLabel();
	private JLabel jlabelStatus = new JLabel();
	
	// Indicate selected row and column of current move TODO
	private int rowSelected;
	private int columnSelected;
	
	// Create IO for server communication
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	// Host name
	private String host = "localhost";
	
	// Build up the game TODO
	@Override 
	public void init(){
		// Panel to hold cells TODO
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(8, 8, 0, 0));
		
		// TODO
		for(int i = 0; i < 8; ++i){
			for(int j = 0; j < 8; ++j){
				panel.add(cell[i][j] = new Cell(i, j));
			}
		}
		
		// Border properties
		panel.setBorder(new LineBorder(Color.orange, 40));
		jlabelTitle.setHorizontalAlignment(JLabel.CENTER);
		jlabelTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
		jlabelTitle.setBorder(new LineBorder(Color.black, 1));
		jlabelStatus.setBorder(new LineBorder(Color.black, 1));
		setSize(600, 600);
		
		// Place labels and panels on the applet
		add(jlabelTitle, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(jlabelStatus, BorderLayout.SOUTH);
		
		// Connect to server
		connectToServer();
	}
	
	private void connectToServer(){
		try{
			// Create a connection socket to the server
			Socket socket;
			if(isStandAlone){
				// Run as stand-alone
				socket = new Socket(host, 8000);
			}
			// Run as an applet
			else{
				socket = new Socket(getCodeBase().getHost(), 8000);
			}
			
			// IO from and to the server
			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch(Exception ex){
			System.err.println(ex);
		}
		
		// Thread for game control
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run(){
		try{
			// Get player number from the server
			int player = fromServer.readInt();
			
			// Check if you are player 1 or 2
			if(player == PLAYER1){
				myColour = "white";
				oppColour = "black";
				jlabelTitle.setText("ChessNet - White player");
				jlabelStatus.setText("Waiting for your opponent to join");
				
				// Get startup notification from the server
				fromServer.readInt();
				
				// Player 2 joins the game
				jlabelStatus.setText("Start the game!");
				
				// Player 1 starts the game
				isMyTurn = true;
			}
			else if(player == PLAYER2){
				myColour = "black";
				oppColour = "white";
				jlabelTitle.setText("ChessNet - Black player");
				jlabelStatus.setText("Waiting for white to move");
			}
			
			// Continue game
			while(continueGame){
				if(player == PLAYER1){
					waitMove();
					sendMove();
					receiveGameStatus();
				}
				else if(player == PLAYER2){
					receiveGameStatus();
					waitMove();
					sendMove();
				}
			}
		}
		catch(Exception ex){
		}
	}
	
	// Wait for the player to move
	private void waitMove() throws InterruptedException {
		while(makingMove){
			Thread.sleep(100);
		}
		makingMove = true;
	}
	
	// Send players move to the server
	private void sendMove() throws IOException {
		toServer.writeInt(rowSelected);
		toServer.writeInt(columnSelected);
	}
	
	// Receive game status from the server
	private void receiveGameStatus() throws IOException {
		int status = fromServer.readInt();
		// Player 1 has won, game ends
		if(status == PLAYER1_WON){
			continueGame = false;
			if(myColour == "white"){
				jlabelStatus.setText("You won!");
			}
			else{
				jlabelStatus.setText("White player wins");
				receiveMove();
			}
		}
		// Player 2 has won, game ends
		else if(status == PLAYER2_WON){
			continueGame = false;
			if(myColour == "black"){
				jlabelStatus.setText("You won!");
			}
			else{
				jlabelStatus.setText("Black player wins");
				receiveMove();
			}
		}
		// Stalemate, game ends
		else if(status == DRAW){
			continueGame = false;
			jlabelStatus.setText("Stalemate, it's a draw!");
			
			// White moves before black...
			if(myColour == "black"){
				receiveMove();
			}
		}
		else{
			receiveMove();
			jlabelStatus.setText("Your turn");
			isMyTurn = true;
		}
	}
	
	// Get opponents move
	private void receiveMove() throws IOException {
		int row = fromServer.readInt();
		int column = fromServer.readInt();
		cell[row][column].setMove(oppColour);
	}
	
	// Inner class for a cell
	public class Cell extends JPanel {
		private int row;
		private int column;
		
		// TODO
		private String token = " ";
		
		public Cell(int row, int column){
			this.row = row;
			this.column = column;
			// Cells border
			//setBorder(new LineBorder(Color.black, 1));
			addMouseListener(new ClickListener());
			// Set background color for cells
			if((row + column) % 2 != 0){
				this.setBackground(Color.gray);
			}
		}
		
		// Return token TODO
		public String getMove(){
			return token;
		}
		
		// Set a new token TODO
		public void setMove(String s){
			token = s;
			repaint();
		}
		
		@Override
		// Paint the move TODO
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			
			//Graphics2D g2 = (Graphics2D) g;
			Image pieceImg = Toolkit.getDefaultToolkit().getImage("b_king.png");
			//int height = pieceImg.getHeight(null);
			//int width = pieceImg.getWidth(null);
			//g2.drawImage(pieceImg, 10, 10, this);
			//g2.finalize();
			if(token == "white"){
				g.drawImage(pieceImg, 10, 10, this);
				// g.drawLine(10, 10, getWidth() - 10, getHeight() - 10);
				// g.drawLine(getWidth() - 10, 10, 10, getHeight() - 10);
			}
			else if(token == "black"){
				// g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
				// g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(pieceImg, 10, 10, this);
			}
		}
		
		// Handle a mouse click TODO
		private class ClickListener	extends MouseAdapter {
			@Override
			public void mouseClicked(MouseEvent e){
				if(token == " " && isMyTurn){
					setMove(myColour);
					isMyTurn = false;
					rowSelected = row;
					columnSelected = column;
					jlabelStatus.setText("Waiting for opponents move");
					makingMove = false;
				}
			}
		}
	}
	
}
