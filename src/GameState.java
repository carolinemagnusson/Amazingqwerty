import java.util.Vector;



public class GameState {
	//TODO Create game state board. Array with strings.
	//Functions for isLegalMove/isMovable(box) etc. PossibleGameStates function
	private char[][] state;
	
	private char wall = '#';
	private char empty = ' ';
	private char goal = '.';
	private char player = '@';
	private char playerOnGoal = '+';
	private char box = '$';
	private char boxOnGoal = '*';
	
	private Position positionNow;
	
	public GameState(Vector<String> board){
		//Check longest column size
		int col = 0;
		for (int i = 0; i < board.size(); i++) {
			if(col< board.get(i).length()){
				col = board.get(i).length();
			}
		}
		//create the board
		state = new char[board.size()][];
		for (int i = 0; i < board.size(); i++) {
			state[i] = board.get(i).toCharArray();
		}
		for (int i = 0; i < state.length; i++) {
			for (int j = 0; j < state.length; j++) {
				if(state[i][j] == player ||state[i][j] ==playerOnGoal){
					positionNow = new Position(i, j);
				}
			}
		}
	}
	public GameState(char[][] firstState){
		state = firstState;
		
		for (int i = 0; i < firstState.length; i++) {
			for (int j = 0; j < firstState.length; j++) {
				if(firstState[i][j] == player ||firstState[i][j] ==playerOnGoal){
					positionNow = new Position(i, j);
				}
			}
		}
	}
	public GameState(GameState stateBefore, Move move){
		char[][] state = copyMatrix(stateBefore.state);
		
		
		
		if(move == Move.Up){
			 //TODO Extend for all moves. Make a "positionBeforeX", "positionBeforeY" etc to avoid duplicate code.
			positionNow = new Position(stateBefore.positionNow.row-1, stateBefore.positionNow.column);
			if(state[positionNow.row][positionNow.column] == goal){
				state[positionNow.row][positionNow.column] = playerOnGoal;
			}
			else if(state[positionNow.row][positionNow.column] == box){
				state[positionNow.row+1][positionNow.column] = empty;
				state[positionNow.row][positionNow.column] = player;
				if(state[positionNow.row-1][positionNow.column] == goal){
					state[positionNow.row-1][positionNow.column] = boxOnGoal;
				}else{
					state[positionNow.row-1][positionNow.column] = box;
				}
			}
			else if(state[positionNow.row][positionNow.column] == boxOnGoal){
				
			}
			else if(state[positionNow.row][positionNow.column] == empty){
				state[positionNow.row+1][positionNow.column] = empty;
				state[positionNow.row][positionNow.column] = player;
			}
		}	
		
	}
	
	public char getCharAt(int row, int col){
		return state[row][col];
	}
	public int getRows(){
		return state.length;
	}
	public int getColumns(){
		return state[0].length;
	}
	
	public boolean canGoUp(){
		char charUp = state[positionNow.row -1][positionNow.column];
		if(charUp == wall){
			return false;
		}
		char twoCharUp = state[positionNow.row -2][positionNow.column];
		return ( charUp== empty|| charUp == goal ||
				((charUp == box || charUp == boxOnGoal) && (twoCharUp == empty|| twoCharUp == goal)));
	}
	public boolean canGoDown(){
		return true;
//		return (state.get(positionNow.row +1 ).charAt(positionNow.column) == empty);
	}
	public boolean canGoLeft(){
		return true;
//		return (state.get(positionNow.row).charAt(positionNow.column-1) == empty);
	}
	public boolean canGoRight(){
		return true;
//		return (state.get(positionNow.row).charAt(positionNow.column+1) == empty);
	}
	public GameState goUp(){
		return null;
//		char charUp = state[positionNow.row -1][positionNow.column];
//		if(canGoUp() == false){
//			throw new Exception("Cannot go up");
//		}
//		char twoCharUp = state[positionNow.row -2][positionNow.column];
//		if(charUp== empty){
//			return new GameState(this, );
//		}
//		return 
//				( charUp== empty|| 
//				((charUp == box || charUp == boxOnGoal) && (twoCharUp == empty|| twoCharUp == goal)));
	}
	public GameState goDown(){
		return null;
	}
	public GameState goLeft(){
		return null;
	}
	public GameState goRight(){
		return null;
	}
	
	public boolean isWinning(){
		return true;
	}
	
	
	public static char[][] copyMatrix(char[][] array){
		char[][] copy = new char[array.length][];
		for (int i = 0; i < array.length; i++) {
			copy[i] = array[i].clone();
		}
		return copy;
	}
	

}
