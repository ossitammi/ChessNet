// Class for chess pieces
// Design and implementation Ossi Tammi 2015

public class Piece {
	// Starting position ('A1')
	private String name;
	// Rank of the piece can be one of the six:
	// Rook ('R'), Knight ('N'), Bishop ('B'), Queen ('Q'), King ('K'), Pawn ('P') 
	// Rook ('r'), Knight ('n'), Bishop ('b'), Queen ('q'), King ('k'), Pawn ('p') 
	private char rank;
	private boolean isSelected;
	private boolean isInBase;
	
	// Constructor
	public Piece(String name, char rank){
		this.name = name;
		this.rank = rank;
		this.isSelected = false;
		this.isInBase = true;
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
	public boolean movePiece(int destRow, int destCol, int prevRow, int prevCol, boolean isAttack){
		// See if the piece can be moved on chosen square
		Coordinates[] possibleCoords = this.possibleMoves(prevRow, prevCol, isAttack);
		if(possibleCoords.length != 64){
			for(int i = 0; i < possibleCoords.length; ++i){
				// If destination square is one of the possible coordinates,
				// you are free to move there
				if(possibleCoords[i].x_coord() == destRow &&
						possibleCoords[i].y_coord() == destCol){
					System.out.println("LIIKKUMINEN ONNISTUUUU!");

					return true;
				}
			}
		}
		return false;
	}
	
	// Method to return all possible places a chosen piece can move
	public Coordinates[] possibleMoves(int currentRow, int currentCol, boolean isAttack){
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
			
		}
		
		// Movement of the Rook
		else if(Character.toLowerCase(this.getRank()) == 'r'){
			
		}
		
		// Movement of the Queen
		else if(Character.toLowerCase(this.getRank()) == 'q'){
					
		}
		
		// Movement of the King
		else if(Character.toLowerCase(this.getRank()) == 'k'){
					
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
