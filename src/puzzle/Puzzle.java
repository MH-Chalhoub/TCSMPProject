package puzzle;

import java.util.Arrays;
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

	//215 POUET.com 2,2 YSVHYODGRDBYMSYE
	public Puzzle(String strPuzzle) {
		String [] tokens = strPuzzle.split(" ", 0);
		String [] rowcol = tokens[2].split(",", 0);
		
		int r = Integer.parseInt(rowcol[0]);
		int c = Integer.parseInt(rowcol[1]);

		this.puzzle = tokens[3];
		this.row = r;
		this.col = c;
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
	
	// Method to sort a string alphabetically 
    public String sortString(String inputString) 
    { 
        // convert input string to char array 
        char tempArray[] = inputString.toCharArray(); 
          
        // sort tempArray 
        Arrays.sort(tempArray); 
          
        // return new sorted string 
        return new String(tempArray); 
    } 
	
    //To solve the puzzle all we have to do is sort the string
	public boolean checkSolution(String solution) {
		return solution.equals(sortString(puzzle));
		
	}

}
