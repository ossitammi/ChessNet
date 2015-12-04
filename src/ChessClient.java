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
import java.util.Vector;
import java.util.*;

public class ChessClient extends JApplet implements Runnable, GameConstants {
	// Check out whos turn it is
	private boolean isMyTurn = false;
	// Continue the game
	private boolean continueGame = true;
	// Wait for the player to make his move
	private boolean makingMove = true;
	// Number of move
	private int numberOfTurn = 0;
	// Boolean to notify game of check status
	private boolean KingInCheck = false;
	
	// Run ChessNet as an applet
	private boolean isStandAlone = false;
	
	// Indicate the colour of the player
	private char myColour = ' ';
	// .. And the opponent
	private char oppColour = ' '; 
	
	// Initialize chessboard TODO
	private Square[][] board = new Square[8][8];
	private Square[][] virtualBoard = new Square[8][8];
	private Square activeSquare = null;
	private Vector<Piece> whitey = new Vector<Piece>(0, 1);
	private Vector<Piece> virtualW = new Vector<Piece>(0, 1);
	private Vector<Piece> blackie = new Vector<Piece>(0, 1);
	private Vector<Piece> virtualB = new Vector<Piece>(0, 1);
	
	// Initialize title and status labels
	private JLabel jlabelTitle = new JLabel();
	private JLabel jlabelStatus = new JLabel();
	
	// Indicate selected row and column of current move TODO
	private int newRow;
	private int newCol;
	private char movedRank;
	private int prevRow;
	private int prevCol;
	
	// Create IO for server communication
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	// Host name
	private String host = "localhost";
	
	// Build up the game TODO
	@Override 
	public void init(){
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
		// start() -method calls the run method
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
			
			// Initialize the board
			JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(8, 8, 0, 0));

			// Place the pieces on board
			for(int i = 0; i < 8; ++i){
				for(int j = 0; j < 8; ++j){
					// Place your pieces on front, opponents pieces back
					if(myColour == 'w'){
						panel.add(board[i][j] = new Square(i, j));
						virtualBoard[i][j] = new Square(i, j);
					}else{
						panel.add(board[7 - i][7 - j] = new Square(7 - i, 7 - j));
						virtualBoard[i][j] = new Square(7 - i, 7 - j);
					}
				}
			}
						
			// To the battlefield!
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
			
			// Store pieces
			whitey.add(a1); whitey.add(b1);	whitey.add(c1); whitey.add(d1);
			whitey.add(e1); whitey.add(f1);	whitey.add(g1); whitey.add(h1);
			whitey.add(a2); whitey.add(b2);	whitey.add(c2); whitey.add(d2);
			whitey.add(e2); whitey.add(f2);	whitey.add(g2); whitey.add(h2);

			blackie.add(a8); blackie.add(b8); blackie.add(c8); blackie.add(d8);
			blackie.add(e8); blackie.add(f8); blackie.add(g8); blackie.add(h8);
			blackie.add(a7); blackie.add(b7); blackie.add(c7); blackie.add(d7);
			blackie.add(e7); blackie.add(f7); blackie.add(g7); blackie.add(h7);
			
			// Black pieces
			board[0][0].setPiece(a8);
			board[0][1].setPiece(b8);
			board[0][2].setPiece(c8);
			board[0][3].setPiece(d8);
			board[0][4].setPiece(e8);
			board[0][5].setPiece(f8);
			board[0][6].setPiece(g8);
			board[0][7].setPiece(h8);
			// Pawns...
			board[1][0].setPiece(a7);
			board[1][1].setPiece(b7);
			board[1][2].setPiece(c7);
			board[1][3].setPiece(d7);
			board[1][4].setPiece(e7);
			board[1][5].setPiece(f7);
			board[1][6].setPiece(g7);
			board[1][7].setPiece(h7);
			
			// White pieces
			board[7][0].setPiece(a1);
			board[7][1].setPiece(b1);
			board[7][2].setPiece(c1);
			board[7][3].setPiece(d1);
			board[7][4].setPiece(e1);
			board[7][5].setPiece(f1);
			board[7][6].setPiece(g1);
			board[7][7].setPiece(h1);
			
			// Pawns...
			board[6][0].setPiece(a2);
			board[6][1].setPiece(b2);
			board[6][2].setPiece(c2);
			board[6][3].setPiece(d2);
			board[6][4].setPiece(e2);
			board[6][5].setPiece(f2);
			board[6][6].setPiece(g2);
			board[6][7].setPiece(h2);
			
			++numberOfTurn;
			
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
					++numberOfTurn;
				}
			}
		}
		catch(Exception ex){
			// TODO
		}
	}
	
	// Game status: check, checkmate, stalemate
	private int gameStatus() throws IOException{
		if(Check()){
			KingInCheck = true;
			System.out.println("SHAKKI!?!");
			// return CONTINUE;
		}
		if(Checkmate()){
			return CHECKMATE; // SEND THIS TO SERVU, game ends
		}
		else if(Stalemate()){
			return STALEMATE; // SEND THIS TO SERVU, game ends
		}
		
		return CONTINUE; // Game continues
	}
	
	// Check if current players king is in check
	private boolean Check(){
		// King is in check if one of the enemy pieces can move to where the king is
		// Go through all the enemy pieces
		Piece king;
		int index = 0;
		Enumeration<Piece> oppEnum;
		Enumeration<Piece> ownEnum;
		if(myColour == 'w'){
			oppEnum = blackie.elements();
			ownEnum = whitey.elements();
		}else{
			oppEnum = whitey.elements();
			ownEnum = blackie.elements();
		}
				
		// Search out for your king
		while(ownEnum.hasMoreElements()){
			System.out.println("Ollaan");
			if(Character.toLowerCase(ownEnum.nextElement().getRank()) == 'k'){
				break;
			}
			++index;
		}
		
		if(myColour == 'w'){
			king = whitey.get(index);
		} 
		else {
			king = blackie.get(index);
		}

		Piece checker;
		while(oppEnum.hasMoreElements()){
			checker = oppEnum.nextElement();
			if(Utilities.isAbleToKill(checker, king.getRow(), king.getCol(), 
					checker.getRow(), checker.getCol(), whitey, blackie)){
				return true;
			}		
		}
		
		// King is not in check
		KingInCheck = false;
		return false;
	}
	
	// Check if this player is in checkmate
	private boolean Checkmate(){
		return false;
	}
	
	// Check if the situation on board is a stalemate
	private boolean Stalemate(){
		return false;
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
		toServer.writeInt(newRow);
		toServer.writeInt(newCol);
		toServer.writeChar(movedRank);
		toServer.writeInt(prevRow);
		toServer.writeInt(prevCol);
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
		int nRow = fromServer.readInt();
		int nCol = fromServer.readInt();
		char rank = fromServer.readChar();
		int pRow = fromServer.readInt();
		int pCol = fromServer.readInt();
				
		// What piece the opponent moved?
		// TODO: Tarviiko nappuloilla olla nimeä?!?
		Piece oppPiece = board[pRow][pCol].getPiece();
		
		// Check if the square had one of your own pieces
		if(board[nRow][nCol].getPiece() != null){
			board[nRow][nCol].removePiece();
		}
		
		// Add the opponents piece to its new location
		board[nRow][nCol].setOppPiece(oppPiece, pRow, pCol);
		
		// Check game status: check, checkmate, stalemate
		//toServer.writeInt(gameStatus());
		gameStatus();
	}
	
	// Inner class for a square
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
			addMouseListener(new ClickListener());
			// Set background color for squares
			if((row + column) % 2 != 0){
				this.setBackground(Color.gray);
			}
		}
		
		public int getRow(){
			return this.row;
		}
		
		public int getColumn(){
			return this.column;
		}
		
		// Highlight active square
		public void select(){
			this.setBorder(new LineBorder(Color.ORANGE, 7));
			this.getPiece().select();
		}
		
		// Remove highlight from square
		public void unselect(){
			this.setBorder(null);
			this.getPiece().unselect();
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
		
		public boolean setPiece(Piece piece, int prevRow, int prevCol, boolean isAttack){
			// If the move is possible to make, do it
			// Get the position for every piece on board
			Coordinates[] pieceCoords = new Coordinates[33];
			int index = 0;
			for(int i = 0; i < 8; ++i){
				for(int j = 0; j < 8; ++j){
					if(board[i][j].getPiece() != null){
						pieceCoords[index] = new Coordinates(i, j);
						++index;
					}
				}
			}
			if(piece.movePiece(row, column, prevRow, prevCol, isAttack, pieceCoords)){
				if(isAttack){
					if(myColour == 'w'){
						blackie.remove(this.piece);
					}
					else {
						whitey.remove(this.piece);
					}
					// Print rest of the enemy pieces
					Enumeration<Piece> vEnum = blackie.elements();
					// TODO REMOVE
					while(vEnum.hasMoreElements()){
						Piece t = vEnum.nextElement();
						System.out.println(t.getName() + " " + t.getRow() + " : " + t.getCol());
					}
					
				}
				this.piece = piece;
				this.piece.setRow(row);
				this.piece.setCol(column);
				this.setMove(piece.getRank());
				this.piece.outOfBase();
				return true;
			}
			return false;
		}
		
		public void setOppPiece(Piece piece, int prevRow, int prevCol){
			this.piece = piece;
			this.piece.setRow(row);
			this.piece.setCol(column);
			this.setMove(piece.getRank());
			this.piece.outOfBase();
			board[prevRow][prevCol].removePiece();
		}
		
		// For initializing the chess board
		public void setPiece(Piece piece){
			this.piece = piece;
			this.piece.setRow(row);
			this.piece.setCol(column);
			this.setMove(piece.getRank());
		}
		
		public Piece getPiece(){
			return piece;
		}
		
		// WTF?!?
		public void removePiece(){
			if(this.piece != null){
				this.unselect();
				this.piece = null;
				this.setMove('E');	
			}
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
			if(Character.isLowerCase(token)){
				imgLocation += "_b.png";
			}else{
				imgLocation += "_w.png";
			}
			
			Image pieceImg = Toolkit.getDefaultToolkit().getImage(imgLocation);
			int x = (this.getWidth() - pieceImg.getWidth(null)) / 2;
			int y = (this.getHeight() - pieceImg.getHeight(null)) / 2;
			// TODO: remove...
			if(token == 'E'){
				g.drawImage(null, x, y, this);
			}
			else if(token != ' '){
				g.drawImage(pieceImg, x, y, this);
			}
			
		}
		
		// Handle a mouse click TODO
		private class ClickListener	extends MouseAdapter {
			@Override
			public void mouseClicked(MouseEvent e){
				
				if(isMyTurn){		
					// Clicked on empty square on board or an enemy piece
					if(board[row][column].getPiece() == null || 
							!Utilities.isMyPiece(myColour, board[row][column].getPiece())){
						// If an own chess piece was selected before, try to...
						// .. move it on the empty square
						if(activeSquare != null){
							Piece activePiece = activeSquare.getPiece();
							Piece oppPiece = null;
							int initialRow = activeSquare.getRow();
							int initialCol = activeSquare.getColumn();
							
							// If you pressed an opponents piece and you have
							// already activated your own piece, it is an attack
							boolean isAttack = false;
							if(board[row][column].getPiece() != null){
								isAttack = true;
								oppPiece = new Piece(board[row][column].getPiece());
							}
							
							// Your king is in check:
							if(true){
								// Create a virtual board
								for(int i = 0; i < 8; ++i){
									for(int j = 0; j < 8; ++j){
										virtualBoard[i][j].removePiece();
										if(board[i][j].getPiece() != null){
											virtualBoard[i][j].setPiece(new Piece(board[i][j].getPiece()));
										}
									}
								}

								// Perform the requested action on the virtual board: 
								// If after the move you are no longer in check, GG.
								if(board[row][column].setPiece(activePiece, 
										initialRow, initialCol, isAttack)){
									board[initialRow][initialCol].removePiece();
									// We are still in check, remove performed actions
									if(Check()){
										restoreItems();
									}
									// Not in check anymore
									else {
										endTurn(row, column, activePiece.getRank(), 
												initialRow, initialCol);
									}
								}
								// TODO POISTO
								Enumeration<Piece> we = whitey.elements();
								Enumeration<Piece> be = blackie.elements();
								while(we.hasMoreElements()){
									Piece p = we.nextElement();
									System.out.println("WHITE: "+p.getCol()+":"+p.getRow()+":"+p.getRank());
								}
								while(be.hasMoreElements()){
									Piece p = be.nextElement();
									System.out.println("WHITE: "+p.getCol()+":"+p.getRow()+":"+p.getRank());
								} // END TODO
							} // King in check ends
							/*
							// Create a new piece on the destination square ...
							// and remove the old one
							else if(board[row][column].setPiece(activePiece, 
									initialRow, initialCol, isAttack)){
								board[initialRow][initialCol].removePiece();
								endTurn(row, column, activePiece.getRank(), 
										initialRow, initialCol);
							}		*/		
						}
					}
					
					
					// If own button clicked, highlight it
					else if(Utilities.isMyPiece(myColour, board[row][column].getPiece())){
						// If the clicked square is already highlighted, remove highlighting
						if(board[row][column].getPiece().isSelected()){
							board[row][column].unselect();
							activeSquare = null;
						}
						// Square was not highlighted beforehand
						else{						
							// Remove selection from possible previously selected piece
							for(int i = 0; i < 8; ++i){
								for(int j = 0; j < 8; ++j){
									// Do not check empty squares
									if(board[i][j].getPiece() != null){
										// Do not unselect the just selected piece...
										if(board[i][j].getPiece().isSelected()){
											board[i][j].unselect();
										}	
									}
								}
							}
							// Select the new square and its piece
							board[row][column].select();
							activeSquare = board[row][column];
						}
					}
				}
			}
		}
	}
	
	// Restore board and pieces where they were before trying to end check
	private void restoreItems(){
		whitey.removeAllElements();
		blackie.removeAllElements();
		for(int i = 0; i < 8; ++i){
			for(int j = 0; j < 8; ++j){
				board[i][j].removePiece();
				if(virtualBoard[i][j].getPiece() != null){
					board[i][j].setPiece(new Piece(virtualBoard[i][j].getPiece()));
					if(Character.isLowerCase(board[i][j].getPiece().getRank())){
						blackie.addElement(board[i][j].getPiece());
					}else{
						whitey.addElement(board[i][j].getPiece());
					}
				}
			}
		}
	}
	
	// Pull the switches to end your turn
	private void endTurn(int row, int column, char rank, int initRow, int initCol){
		newRow = row;
		newCol = column;
		movedRank = rank;
		prevRow = initRow;
		prevCol = initCol;
		activeSquare = null;
		isMyTurn = false;
		jlabelStatus.setText("Waiting for opponents move");
		makingMove = false;	
	}
	
}
