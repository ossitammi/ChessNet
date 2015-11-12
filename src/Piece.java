// Class for chess pieces
// Design and implementation Ossi Tammi 2015

public class Piece {
	// Starting position ('A1')
	private String name;
	// white 'w' or black 'b'
	private char colour;
	// row & column
	private int[][] location;
	// Rank of the piece can be one of the six:
	// Rook ('R'), Knight ('K'), Bishop ('B'), Queen ('Q'), King ('X'), Pawn ('P') 
	private char rank;
	
	// Constructor
	public Piece(String name, char colour, int[][] location, char rank){
		this.name = name;
		this.colour = colour;
		this.location = location;
		this.rank = rank;
	}
	
	// Move the piece according to its rank
	public int movePiece(){
		
		return 0;
	}
	
}
