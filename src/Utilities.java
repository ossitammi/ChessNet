// Utilities class for... software utilities.
// Design and implementation Ossi Tammi 2015

public class Utilities {
	// Function to check out whose piece is at hand
	public static boolean isMyPiece(char myColour, Piece piece){
		// If myColour is white, then ranks of the pieces should be UPPERCASE
		if(myColour == 'w'){
			// Is UPPERCASE
			if(!Character.isLowerCase(piece.getRank())){
				return true;
			}
		}
		// myColour is black, own pieces should have LOWERCASE rank
		else{
			// Is LOWERCASE
			if(Character.isLowerCase(piece.getRank())){
				return true;
			}
		}
		
		// Something went wrong, or did it? This is for the compiler
		return false;
	}
	
	// Function to find all squares blocked by pieces
	public static boolean[][] blockedSquares(Coordinates[] pieceCoords, int pRow, int pCol){
		boolean[][] blockedSquares = new boolean[8][8];
		int index = 0;
		while(pieceCoords[index] != null){
			// Column <-> y_coordinate, row <-> x_coordinate
			// Pieces on the same row
			if(pRow == pieceCoords[index].x_coord()){
				// If the piece is on the right side of you, its blocking that side
				if(pCol < pieceCoords[index].y_coord()){
					for(int j = pieceCoords[index].y_coord() + 1; j < 8; ++j){
						blockedSquares[pRow][j] = true;
					}
				}
				// If the piece is on the left side of you, its blocking that side
				else if(pCol > pieceCoords[index].y_coord()){
					for(int j = pieceCoords[index].y_coord() - 1; 0 <= j; --j){
						blockedSquares[pRow][j] = true;
					}
				}
			}
			// Pieces on the same column
			else if(pCol == pieceCoords[index].y_coord()){
				// If the piece is above you, its blocking everything there
				if(pRow < pieceCoords[index].x_coord()){
					for(int i = pieceCoords[index].x_coord() + 1; i < 8; ++i){
						blockedSquares[i][pCol] = true;
					}
				}
				// Piece is below you
				else if(pRow > pieceCoords[index].x_coord()){
					for(int i = pieceCoords[index].x_coord() - 1; 0 <= i; --i){
						blockedSquares[i][pCol] = true;
					}
				}
			}
			
			// Pieces which are diagonal to your location
			else if(Math.abs(pRow - pieceCoords[index].x_coord()) == 
					Math.abs(pCol - pieceCoords[index].y_coord())){
				if(pCol < pieceCoords[index].y_coord() && 
						pRow > pieceCoords[index].x_coord()){
					for(int i = pieceCoords[index].x_coord() - 1; 0 <= i; --i){
						for(int j = pieceCoords[index].y_coord() + 1; j < 8; ++j){
							if(Math.abs(pRow - i) == Math.abs(pCol - j)){
								blockedSquares[i][j] = true;
							}
						}
					}
				}
				else if(pCol > pieceCoords[index].y_coord() &&
						pRow > pieceCoords[index].x_coord()){
					for(int i = pieceCoords[index].x_coord() - 1; 0 <= i; --i){
						for(int j = pieceCoords[index].y_coord() - 1; 0 <= j; --j){
							if(Math.abs(pRow - i) == Math.abs(pCol - j)){
								blockedSquares[i][j] = true;
							}
						}
					}
				}
				else if(pCol > pieceCoords[index].y_coord() &&
						pRow < pieceCoords[index].x_coord()){
					for(int i = pieceCoords[index].x_coord() + 1; i < 8; ++i){
						for(int j = pieceCoords[index].y_coord() - 1; 0 <= j; --j){
							if(Math.abs(pRow - i) == Math.abs(pCol - j)){
								blockedSquares[i][j] = true;
							}
						}
					}
				}
				else if(pCol < pieceCoords[index].y_coord() &&
						pRow < pieceCoords[index].x_coord()){
					for(int i = pieceCoords[index].x_coord() + 1; i < 8; ++i){
						for(int j = pieceCoords[index].y_coord() + 1; j < 8; ++j){
							if(Math.abs(pRow - i) == Math.abs(pCol - j)){
								blockedSquares[i][j] = true;
							}
						}
					}
				}
			}
			
			++index;
		}
		return blockedSquares;
	}
	
}
