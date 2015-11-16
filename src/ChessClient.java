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
	private char myColour = ' ';
	// .. And the opponent
	private char oppColour = ' '; 
	
	// Initialize chessboard TODO
	private Square[][] board = new Square[8][8];
	
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
				panel.add(board[i][j] = new Square(i, j));
			}
		}
		
		// Soldiers, onward to battlefield!
		Piece a1 = new Piece("a1", 'R');
		Piece a2 = new Piece("a2", 'P');
		Piece b1 = new Piece("b1", 'N');
		Piece b2 = new Piece("b2", 'P');
		Piece c1 = new Piece("c1", 'B');
		Piece c2 = new Piece("c2", 'P');
		Piece d1 = new Piece("d1", 'Q');
		Piece d2 = new Piece("d2", 'P');
		Piece e1 = new Piece("e1", 'K');
		Piece e2 = new Piece("e2", 'P');
		Piece f1 = new Piece("f1", 'B');
		Piece f2 = new Piece("f2", 'P');
		Piece g1 = new Piece("g1", 'N');
		Piece g2 = new Piece("g2", 'P');
		Piece h1 = new Piece("h1", 'R');
		Piece h2 = new Piece("h2", 'P');
		Piece a7 = new Piece("a7", 'p');
		Piece a8 = new Piece("a8", 'r');
		Piece b7 = new Piece("b7", 'p');
		Piece b8 = new Piece("b8", 'n');
		Piece c7 = new Piece("c7", 'p');
		Piece c8 = new Piece("c8", 'b');
		Piece d7 = new Piece("d7", 'p');
		Piece d8 = new Piece("d8", 'q');
		Piece e7 = new Piece("e7", 'p');
		Piece e8 = new Piece("e8", 'k');
		Piece f7 = new Piece("f7", 'p');
		Piece f8 = new Piece("f8", 'b');
		Piece g7 = new Piece("g7", 'p');
		Piece g8 = new Piece("g8", 'n');
		Piece h7 = new Piece("h7", 'p');
		Piece h8 = new Piece("h8", 'r');
		
		// Black pieces
		board[0][0].setPiece(a8);
		board[0][0].setMove(a8.getRank());
		board[0][1].setPiece(b8);
		board[0][1].setMove(b8.getRank());
		board[0][2].setPiece(c8);
		board[0][2].setMove(c8.getRank());
		board[0][3].setPiece(d8);
		board[0][3].setMove(d8.getRank());
		board[0][4].setPiece(e8);
		board[0][4].setMove(e8.getRank());
		board[0][5].setPiece(f8);
		board[0][5].setMove(f8.getRank());
		board[0][6].setPiece(g8);
		board[0][6].setMove(g8.getRank());
		board[0][7].setPiece(h8);
		board[0][7].setMove(h8.getRank());
		// Pawns...
		board[1][0].setPiece(a7);
		board[1][0].setMove(a7.getRank());
		board[1][1].setPiece(b7);
		board[1][1].setMove(b7.getRank());
		board[1][2].setPiece(c7);
		board[1][2].setMove(c7.getRank());
		board[1][3].setPiece(d7);
		board[1][3].setMove(d7.getRank());
		board[1][4].setPiece(e7);
		board[1][4].setMove(e7.getRank());
		board[1][5].setPiece(f7);
		board[1][5].setMove(f7.getRank());
		board[1][6].setPiece(g7);
		board[1][6].setMove(g7.getRank());
		board[1][7].setPiece(h7);
		board[1][7].setMove(h7.getRank());
		
		// White peaces
		board[7][0].setPiece(a1);
		board[7][0].setMove(a1.getRank());
		board[7][1].setPiece(b1);
		board[7][1].setMove(b1.getRank());
		board[7][2].setPiece(c1);
		board[7][2].setMove(c1.getRank());
		board[7][3].setPiece(d1);
		board[7][3].setMove(d1.getRank());
		board[7][4].setPiece(e1);
		board[7][4].setMove(e1.getRank());
		board[7][5].setPiece(f1);
		board[7][5].setMove(f1.getRank());
		board[7][6].setPiece(g1);
		board[7][6].setMove(g1.getRank());
		board[7][7].setPiece(h1);
		board[7][7].setMove(h1.getRank());
		// Pawns...
		board[6][0].setPiece(a2);
		board[6][0].setMove(a2.getRank());
		board[6][1].setPiece(b2);
		board[6][1].setMove(b2.getRank());
		board[6][2].setPiece(c2);
		board[6][2].setMove(c2.getRank());
		board[6][3].setPiece(d2);
		board[6][3].setMove(d2.getRank());
		board[6][4].setPiece(e2);
		board[6][4].setMove(e2.getRank());
		board[6][5].setPiece(f2);
		board[6][5].setMove(f2.getRank());
		board[6][6].setPiece(g2);
		board[6][6].setMove(g2.getRank());
		board[6][7].setPiece(h2);
		board[6][7].setMove(h2.getRank());
		
		
		// Border properties
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 40));
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
				myColour = 'w';
				oppColour = 'b';
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
				myColour = 'b';
				oppColour = 'w';
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
	
	// Send players move to the server TODO
	private void sendMove() throws IOException {
		toServer.writeInt(rowSelected);
		toServer.writeInt(columnSelected);
		toServer.writeChar(myColour);
	}
	
	// Receive game status from the server
	private void receiveGameStatus() throws IOException {
		int status = fromServer.readInt();
		// Player 1 has won, game ends
		if(status == PLAYER1_WON){
			continueGame = false;
			if(myColour == 'w'){
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
			if(myColour == 'b'){
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
			if(myColour == 'b'){
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
		char rank = fromServer.readChar();
		board[row][column].setMove(rank);
	}
	
	// Inner class for a cell
	public class Square extends JPanel {
		private int row;
		private int column;
		private Piece piece;
		
		// TODO
		private char token = ' ';
		
		// Constructor
		public Square(int row, int column){
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
		public char getMove(){
			return token;
		}
		
		// Set a new token TODO
		public void setMove(char rank){
			token = rank;
			repaint();
		}
		
		public void setPiece(Piece piece){
			this.piece = piece;
		}
		
		public Piece getPiece(){
			return piece;
		}
		
		// WTF?!?
		public void removePiece(){
			this.piece = null;
		}
		
		@Override
		// Paint the move TODO
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			
			char charArray[] = {token};
			// Physical location of the piece images
			String strToken = new String(charArray);
			String imgLocation = new String(strToken.toLowerCase());
			// Colour and piece selector
			// TODO check this from token ....
			if(row <= 1){
				imgLocation += "_b.png";
			}else{
				imgLocation += "_w.png";
			}
			
			Image pieceImg = Toolkit.getDefaultToolkit().getImage(imgLocation);
			int x = (this.getWidth() - pieceImg.getWidth(null)) / 2;
			int y = (this.getHeight() - pieceImg.getHeight(null)) / 2;
			if(token == 'E'){
				g.clearRect(0, 0, getWidth(), getHeight());
			}
			else if(token != ' '){
				g.drawImage(pieceImg, x, y, this);
			}
			
		}
		
		// Handle a mouse click TODO
		private class ClickListener	extends MouseAdapter {
			@Override
			public void mouseClicked(MouseEvent e){
				if(token == ' ' && isMyTurn && false){
					setMove(myColour);
					isMyTurn = false;
					rowSelected = row;
					columnSelected = column;
					jlabelStatus.setText("Waiting for opponents move");
					makingMove = false;
				}
				if(isMyTurn){
					Piece newPiece = board[row][column].getPiece();
					board[row][column].removePiece();
					board[row][column].setPiece(newPiece);
					
					setMove('E');
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
