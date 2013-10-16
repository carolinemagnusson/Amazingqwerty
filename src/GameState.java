import java.util.Vector;
import java.util.Collection;

public class GameState {
	//TODO Create game matrix board. Array with strings.
	//Functions for isLegalMove/isMovable(C.box) etc. PossibleGameStates function
	private char[][] matrix;
	private Position positionNow;
	private boolean lastMovePushMove;

	public GameState(Vector<String> board){
		//Check longest column size
		int col = 0;
		for (int i = 0; i < board.size(); i++) {
			if(col< board.get(i).length()){
				col = board.get(i).length();
			}
		}
		
		//create the board
		matrix = new char[board.size()][];
		for (int i = 0; i < board.size(); i++) {
			matrix[i] = board.get(i).toCharArray();
		}
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if(matrix[i][j] == C.player || matrix[i][j] == C.playerOnGoal){
					positionNow = new Position(i, j);
				}
			}
		}
	}
	
	public GameState(char[][] firstState){
		matrix = firstState;

		for (int i = 0; i < firstState.length; i++) {
			for (int j = 0; j < firstState[0].length; j++) {
				if(firstState[i][j] == C.player || firstState[i][j] == C.playerOnGoal){
					positionNow = new Position(i, j);
				}
			}
		}
	}
	
	public char[][] getGameBoard()
	{
		return matrix; 
	}
	public Position getPositionNow()
	{
		return positionNow;
	}

	public char getCharAt(int row, int col){
		return matrix[row][col];
	}
	public int getRows(){
		return matrix.length;
	}
	public int getColumns(){
		return matrix[0].length;
	}

	private boolean canPush(int x, int y) //x and y must be either (-1, 0, +1)
	{
		int py = positionNow.row;
		int px = positionNow.column;
		//check the cell that are going to be pushed
		if(py+y< getRows() && px+x < getColumns()){
			char c = matrix[py+y][px+x];
			if(c == C.wall) {
				return false;
			}
			else if (c == C.empty|| c == C.goal){
				return true;
			}
		} else {
			return false;
		}			

		//check the cell behind the pushed object
		if(py+y*2< matrix.length && px+x*2 < matrix[0].length){
			char c = matrix[py+y*2][px+x*2];
			if(c == C.wall || c == C.box || c == C.boxOnGoal){
				return false;
			}
			else if(c==C.empty|| c ==C.goal){
				return true;
			}			
		}
		
		return false;
	}

	public boolean canPushUp(){
		return canPush(0, -1);
	}
	public boolean canPushDown(){
		return canPush(0, +1);
	}
	public boolean canPushLeft(){
		return canPush(-1, 0);
	}
	public boolean canPushRight(){
		return canPush(+1, 0);
	}

	private GameState push(int x, int y) //x and y must be either (-1, 0, +1)
	{
		int px = positionNow.column;
		int py = positionNow.row;
//		System.err.println("Pushing to row = " + (py + y) + " and col " + (px+x));
		char[][] childState = copyMatrix(matrix);

		//update the cell on the players current postion
		{
			char c = childState[py][px];
			if(c == C.player) 
				childState[py][px] = C.empty;
			else if(c == C.playerOnGoal ) 
				childState[py][px] = C.goal;
		}

		//update the cell where the pushed object goes, if there is a pushed object
		{
			char c = childState[py+y][px+x];
			if(c == C.box || c == C.boxOnGoal){
				lastMovePushMove = true;
				char cc = childState[py+y*2][px+x*2];
				if(cc == C.empty){
					childState[py+y*2][px+x*2] = C.box;
					
				}
				else if(cc == C.goal){
					childState[py+y*2][px+x*2] = C.boxOnGoal;
					
				}
				if(c == C.boxOnGoal){
					childState[py+y][px+x] = C.goal;
				} else if(c== C.box){
					childState[py+y][px+x] = C.empty;
				}else{
					System.err.println("Wrong in push1, gamestate");
				}
				//else throw exception invalid push
			}
			else{
				lastMovePushMove = false;
			}
		}

		//update the pushed cell
		{
			char c = childState[py+y][px+x];
			if(c == C.goal) {
				childState[py+y][px+x] = C.playerOnGoal;
			}
			else if (c== C.empty){
				childState[py+y][px+x] = C.player;
			}
			else{
				System.err.println("Wrong in push2, gamestate");
			}
		}
		GameState newState = new GameState(childState);
		newState.positionNow.row = py + y;
		newState.positionNow.column = px + x;
		return newState;
	}

	public GameState pushUp(){
		return push(0,-1);
	}
	public GameState pushDown(){
		return push(0,1);
	}
	public GameState pushLeft(){
		return push(-1,0);
	}
	public GameState pushRight(){
		return push(1,0);
	}

	public boolean isWinning(){
		for(int iy=0; iy<matrix.length; iy++)
		{
			for(int ix=0; ix<matrix[iy].length; ix++)
			{
				if(matrix[iy][ix] == C.box) return false;
			}
		}
		return true;
	}

	public static char[][] copyMatrix(char[][] array){
		char[][] copy = new char[array.length][];
		for (int i = 0; i < array.length; i++) {
			copy[i] = array[i].clone();
		}
		return copy;
	}
	public boolean isLastMovePush(){
		return lastMovePushMove;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		
		for(int iy = 0 ; iy < getRows(); iy++)
		{
			for (int ix = 0 ; ix < getColumns(); ix++)
			{
				char c = matrix[iy][ix];
				if(c != 0) s += c;
			}
			s += "\n";
		}
		
		return s;
	}
	
	public void getPossibleStates(Collection c)
	{
		if(canPushLeft())
			c.add(pushLeft());
		if(canPushRight())
			c.add(pushRight());
		if(canPushUp())
			c.add(pushUp());
		if(canPushDown())
			c.add(pushDown());
	}
}
