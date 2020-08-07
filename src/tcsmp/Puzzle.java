package tcsmp;

import java.util.Random;

public class Puzzle {
	private int row;
	private int col;
	String puzzle;
	
	public Puzzle(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public Puzzle(String puzzle, int row, int col) {
		this.puzzle = puzzle;
		this.row = row;
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getPuzzle() {
		return puzzle;
	}

	public void setPuzzle(String puzzle) {
		this.puzzle = puzzle;
	}

	public String generatePuzzle()
	{
		int nbPieceIdentique = (col-1)*2*row + (row-1)*2*col;
		char[] s = new char[row*col*4];
		Random r = new Random();
		for(int i=0; i<row*col*4; i++) {
			char c = (char)(r.nextInt(26) + 'A');
			s[i] = c;
		}
		for(int i=0; i<nbPieceIdentique; i += 2) {
			s[i] = s[i+1];
		}
		shuffle(s);
		return new String(s);
	}
	
	private char[] shuffle(char[] characters) {
	    for (int i = 0; i < characters.length; i++) {
	        int randomIndex = (int)(Math.random() * characters.length);
	        char temp = characters[i];
	        characters[i] = characters[randomIndex];
	        characters[randomIndex] = temp;
	    }
	    return characters;
	}
	
	public boolean checkSolution(String solution) {
		return true;
		
	}

}
