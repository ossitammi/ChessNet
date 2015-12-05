import java.util.Enumeration;
import java.util.Vector;

// Utilities class for... software utilities.
// Design and implementation Ossi Tammi 2015

public class Utilities {
	
	// Function to check out if a piece is able to kill king on its next turn
	public static boolean isAbleToKill(Piece piece, int newRow, int newCol,
			int prevRow, int prevCol, Vector<Piece> whitey, Vector<Piece> blackie){
		Coordinates[] pieceCoords = new Coordinates[33];
		Enumeration<Piece> wEnum = whitey.elements();
		Enumeration<Piece> bEnum = blackie.elements();
		int index = 0;
		Piece iterator;
		while(wEnum.hasMoreElements()){
			iterator = wEnum.nextElement();
			pieceCoords[index] = new Coordinates(iterator.getRow(),
					iterator.getCol());
			++index;
		}
		while(bEnum.hasMoreElements()){
			iterator = bEnum.nextElement();
			pieceCoords[index] = new Coordinates(iterator.getRow(),
					iterator.getCol());
			++index;
		}
		
		if(piece.movePiece(newRow, newCol, prevRow, prevCol, true, pieceCoords)){
			return true;
		}
		return false;
	}
	
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
		// Blockers row and column
		int bRow, bCol;
		while(pieceCoords[index] != null){
			bRow = pieceCoords[index].x_coord();
			bCol = pieceCoords[index].y_coord();
			
			// Column <-> y_coordinate, row <-> x_coordinate
			// Pieces on the same row
			if(pRow == bRow){
				// If the piece is on the right side of you, its blocking that side
				if(pCol < bCol){
					for(int j = bCol + 1; j < 8; ++j){
						blockedSquares[pRow][j] = true;
					}
				}
				// If the piece is on the left side of you, its blocking that side
				else if(pCol > bCol){
					for(int j = bCol - 1; 0 <= j; --j){
						blockedSquares[pRow][j] = true;
					}
				}
			}
			// Pieces on the same column
			else if(pCol == bCol){
				// If the piece is above you, its blocking everything there
				if(pRow < bRow){
					for(int i = bRow + 1; i < 8; ++i){
						blockedSquares[i][pCol] = true;
					}
				}
				// Piece is below you
				else if(pRow > bRow){
					for(int i = bRow - 1; 0 <= i; --i){
						blockedSquares[i][pCol] = true;
					}
				}
			}
			
			// Pieces which are diagonal to your location
			else if(Math.abs(pRow - bRow) == Math.abs(pCol - bCol)){
				// Blocker is in the I sector in Cartesian coordinates
				if(pCol < bCol && pRow > bRow){
					for(int i = bRow - 1; 0 <= i; --i){
						for(int j = bCol + 1; j < 8; ++j){
							if(Math.abs(pRow - i) == Math.abs(pCol - j)){
								blockedSquares[i][j] = true;
							}
						}
					}
				}
				// Blocker is in the II sector in Cartesian coordinates
				else if(pCol > bCol && pRow > bRow){
					for(int i = bRow - 1; 0 <= i; --i){
						for(int j = bCol - 1; 0 <= j; --j){
							if(Math.abs(pRow - i) == Math.abs(pCol - j)){
								blockedSquares[i][j] = true;
							}
						}
					}
				}
				// Blocker is in the III sector in Cartesian coordinates
				else if(pCol > bCol && pRow < bRow){
					for(int i = bRow + 1; i < 8; ++i){
						for(int j = bCol - 1; 0 <= j; --j){
							if(Math.abs(pRow - i) == Math.abs(pCol - j)){
								blockedSquares[i][j] = true;
							}
						}
					}
				}
				// Blocker is in the IV sector in Cartesian coordinates
				else if(pCol < bCol && pRow < bRow){
					for(int i = bRow + 1; i < 8; ++i){
						for(int j = bCol + 1; j < 8; ++j){
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
