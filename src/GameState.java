

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
//	public boolean canGoDown(){
//		return (state.get(positionNow.row +1 ).charAt(positionNow.column) == empty);
//	}
//	public boolean canGoLeft(){
//		return (state.get(positionNow.row).charAt(positionNow.column-1) == empty);
//	}
//	public boolean canGoRight(){
//		return (state.get(positionNow.row).charAt(positionNow.column+1) == empty);
//	}
	public GameState goUp(){
		char charUp = state[positionNow.row -1][positionNow.column];
		if(canGoUp() == false){
			throw new Exception("Cannot go up");
		}
		char twoCharUp = state[positionNow.row -2][positionNow.column];
		if(charUp== empty){
			return new GameState(this, );
		}
		return 
				( charUp== empty|| 
				((charUp == box || charUp == boxOnGoal) && (twoCharUp == empty|| twoCharUp == goal)));
	}
	public static char[][] copyMatrix(char[][] array){
		char[][] copy = new char[array.length][];
		for (int i = 0; i < array.length; i++) {
			copy[i] = array[i].clone();
		}
		return copy;
	}
	
//	public static copyArray(ArrayList<String> array){
//		ArrayList<String> clone = new ArrayList<String>();
//		for (int i = 0; i < array.size(); i++) {
//			clone.add(array.get(i))
//		}
//	}
}
