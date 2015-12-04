// Class for chess pieces
// Design and implementation Ossi Tammi 2015


public class Piece {
	// Starting position (like 'A1')
	private String name;
	// Rank of the piece can be one of the six:
	// Rook ('R'), Knight ('N'), Bishop ('B'), Queen ('Q'), King ('K'), Pawn ('P') 
	// Rook ('r'), Knight ('n'), Bishop ('b'), Queen ('q'), King ('k'), Pawn ('p') 
	private char rank;
	private boolean isSelected;
	private boolean isInBase;
	// Location of the piece on the board
	private int row;
	private int column;
	
	// Constructor
	public Piece(String name, char rank){
		this.name = name;
		this.rank = rank;
		this.isSelected = false;
		this.isInBase = true;
	}
	
	public Piece(Piece p){
		this.name = p.name;
		this.rank = p.rank;
		this.isSelected = p.isSelected;
		this.isInBase = p.isInBase;
		this.row = p.row;
		this.column = p.column;
	}
	
	public void setRow(int row){
		this.row = row;
	}
	
	public void setCol(int col){
		this.column = col;
	}
	
	public int getRow(){
		return this.row;
	}
	
	public int getCol(){
		return this.column;
	}
	// The piece has moved out of its initial position
	public void outOfBase(){
		this.isInBase = false;
	}
	
	public void unselect(){
		this.isSelected = false;
	}
	
	public void select(){
		this.isSelected = true;
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public String getName(){
		return name;
	}
	
	public char getRank(){
		return rank;
	}
	
	
	// Move the piece according to its rank
	public boolean movePiece(int destRow, int destCol, int prevRow, int prevCol, 
			boolean isAttack, Coordinates[] pieceCoords){
		// See if the piece can be moved on chosen square
		Coordinates[] possibleCoords = this.possibleMoves(prevRow, prevCol, isAttack);
		boolean[][] piecesBlocking = Utilities.blockedSquares(pieceCoords, prevRow, prevCol);
		if(possibleCoords.length != 64){
			for(int i = 0; i < possibleCoords.length; ++i){
				// If destination square is one of the possible coordinates,
				// you are free to move there
				if(possibleCoords[i].x_coord() == destRow &&
						possibleCoords[i].y_coord() == destCol){
					// Check if there is no one blocking the way
					if(Character.toLowerCase(this.getRank()) == 'r' || 
							Character.toLowerCase(this.getRank()) == 'b' || 
							Character.toLowerCase(this.getRank()) == 'q'){
						if(piecesBlocking[destRow][destCol]){
							return false;
						}
					}
					
					System.out.println("LIIKKUMINEN ONNISTUUUU!");

					return true;
				}
			}
		}
		return false;
	}
	
	// Method to return all possible places a chosen piece can move
	private Coordinates[] possibleMoves(int currentRow, int currentCol, boolean isAttack){
		Coordinates[] coordinates = new Coordinates[64];
		int index = 0;
		
		// Movement of the Pawn
		if(Character.toLowerCase(this.getRank()) == 'p'){
			// If the pawn is in base, it can move one OR two squares forward
			int direction = 1;
			// White pawns move the other way
			if(this.getRank() == 'P'){
				direction = -1;
			}
			
			// Not an attack
			if(!isAttack){
				coordinates[index] = new Coordinates(currentRow + 1 * direction, currentCol);
				++index;
				// From base you can move two squares forward
				if(this.isInBase){
					coordinates[index] = new Coordinates(currentRow + 2 * direction, currentCol);
					++index;
				}
			}
			// Is an attack
			else if(isAttack){
				coordinates[index] = new Coordinates(currentRow + 1 * direction, currentCol + 1);
				++index;
				coordinates[index] = new Coordinates(currentRow + 1 * direction, currentCol - 1);
				++index;
			}
			
		}
		
		// Movement of the Knight
		else if(Character.toLowerCase(this.getRank()) == 'n'){
			
			// Jump two squares to one direction, then one square orthogonal			
			for(int i = -2; i <= 2; ++i){
				for(int j = -2; j <= 2; ++j){
					if(i != 0 && j != 0 && (i + j) % 2 != 0){
						if(0 <= (currentRow + i) && (currentRow + i) < 8 && 0 <= (currentCol + j)
								&& (currentCol + j) < 8){
							coordinates[index] = new Coordinates(currentRow + i, currentCol + j);
							System.out.println("HEPO: X:" + (currentRow + i) + " Y: " + (currentCol + j));
							++index;
						}
					}
				}
			}
		}
		
		// Movement of the Bishop
		else if(Character.toLowerCase(this.getRank()) == 'b'){
			// Move diagonally to all four directions
			for(int i = -7; i < 8; ++i){
				for(int j = - 7; j < 8; ++j){
					// Diagonally...
					if(Math.abs(i) == Math.abs(j) && i != 0){
						// Don't go over the board
						if(0 <= (currentRow + i) && (currentRow + i) < 8 && 0 <= (currentCol + j)
						&& (currentCol + j) < 8){
							coordinates[index] = new Coordinates(currentRow + i, currentCol + j);
							++index;
						}
					}
				}
			}
		}
		
		// Movement of the Rook
		else if(Character.toLowerCase(this.getRank()) == 'r'){
			// Move horizontally/vertically to all four directions
			for(int i = -7; i < 8; ++i){
				if(i != 0){
					if(0 <= (currentRow + i) && (currentRow + i) < 8){
						coordinates[index] = new Coordinates(currentRow + i, currentCol);
						++index;
					}
					if(0 <= (currentCol + i) && (currentCol + i) < 8){
						coordinates[index] = new Coordinates(currentRow, currentCol + i);
						++index;
					}
				}
			}
		}
		
		// Movement of the Queen
		else if(Character.toLowerCase(this.getRank()) == 'q'){
			// Combine the movement of the rook and bishop
			
			// Move diagonally to all four directions
			for(int i = -7; i < 8; ++i){
				for(int j = - 7; j < 8; ++j){
					// Diagonally...
					if(Math.abs(i) == Math.abs(j) && i != 0){
						// Don't go over the board
						if(0 <= (currentRow + i) && (currentRow + i) < 8 && 0 <= (currentCol + j)
						&& (currentCol + j) < 8){
							coordinates[index] = new Coordinates(currentRow + i, currentCol + j);
							++index;
						}
					}
				}
			}
			
			// Move horizontally/vertically to all four directions
			for(int i = -7; i < 8; ++i){
				if(i != 0){
					if(0 <= (currentRow + i) && (currentRow + i) < 8){
						coordinates[index] = new Coordinates(currentRow + i, currentCol);
						++index;
					}
					if(0 <= (currentCol + i) && (currentCol + i) < 8){
						coordinates[index] = new Coordinates(currentRow, currentCol + i);
						++index;
					}
				}
			}
		}
		
		// Movement of the King
		else if(Character.toLowerCase(this.getRank()) == 'k'){
			// Move into any of the 8 adjacent squares (basicly same as queen..)
			for(int i = -1; i <= 1; ++i){
				for(int j = -1; j <=1; ++j){
					if(0 <= (currentRow + i) && (currentRow + i) < 8 && 0 <= (currentCol + j)
						&& (currentCol + j) < 8){
						coordinates[index] = new Coordinates(currentRow + i, currentCol + j);
						++index;
					}
				}
			}
		}
		
		if(index != 0){
			// Assemble the result onto a new list
			Coordinates[] resultCoords = new Coordinates[index];
			for(int i = 0; i < resultCoords.length; ++i){
				resultCoords[i] = coordinates[i];
				System.out.println("MAHDRUUDUT: " + "X: " + resultCoords[i].x_coord() + 
						" Y: " + resultCoords[i].y_coord() + "\n");
			}	
			return resultCoords;
		}		
		
		// This one has 64 items in it, it is considered as a failure in getting movement
		return coordinates;
	}	
}
