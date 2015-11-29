// Class to handle coordinates of chess board
// Design and implementation Ossi Tammi 2015

public class Coordinates {
	private int x;	// x-coordinate
	private int y;  // y-coordinate
	
	// Constructor
	public Coordinates(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	// Get the x-coordinate
	public int x_coord(){
		return x;
	}
	
	// Get the y-coordinate
	public int y_coord(){
		return y;
	}
}
