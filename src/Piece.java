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
	
	// Constructor
	public Piece(String name, char rank){
		this.name = name;
		this.rank = rank;
		this.isSelected = false;
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
	public int movePiece(){
		
		return 0;
	}
	
}
