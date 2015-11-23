// Utilities class for... software utilities.
// Design and implementation Ossi Tammi 2015

public class Utilities {
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
}
