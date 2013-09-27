import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Player extends AbstractPlayer {
	HashMap<String,Node> visitedStates = new HashMap<String,Node>();
	int counter = 0;
	PriorityQueue<Node> queue;
	GameState unsafePosMap;
	@Override
	public String play(GameState state) {
		
		queue = new PriorityQueue<Node>();
		//Create a map that marks unsafe positions
		unsafePosMap = markUnsafePositions(state);
		
		Node startNode = new Node(state, null, 0, heuristic(state));
		
		queue.add(startNode);
		while(!queue.isEmpty()){
			
			if (queue.peek().state.isWinning()){
				System.err.println(queue.peek().state.toString());
				return makePathString(queue.peek());
			} 
			System.err.println("The state has heuristic: " + queue.peek().score);
			System.err.println(queue.peek().state.toString());
			System.err.println("Loopcount " + counter);
			Node parent = queue.poll();
			if (visitedStates.containsKey(parent.toString())){
				continue;
			}
//			GameState riskPositions = markUnsafePositions(parent.state);
			visitedStates.put(parent.toString(), parent);
//			System.err.println(parent.toString());
			if (parent.state.canPushUp()){
//				System.err.println("UP");
				GameState newState = parent.state.pushUp();
				//Check that, if box moved to unsafe place, it will not be added to the queue.
				if(newState.isLastMovePush()){
					int boxY = newState.getPositionNow().row+1;
					int boxX = newState.getPositionNow().column;
					if(safePosition(boxX, boxY)){
						queue.add(new Node(newState, parent, 
								heuristic(newState) + parent.pathCost +1, parent.pathCost+1));
					}
				}else{
					queue.add(new Node(newState, parent, 
							heuristic(newState) + parent.pathCost +1, parent.pathCost+1));
				}
			}

			if (parent.state.canPushDown()){
//				System.err.println("DOWN");
				GameState newState2 = parent.state.pushDown();
				if(newState2.isLastMovePush()){
					int boxY = newState2.getPositionNow().row-1;
					int boxX = newState2.getPositionNow().column;
					if(safePosition(boxX, boxY)){
							queue.add(new Node(newState2, parent, 
									heuristic(newState2) + parent.pathCost +1, parent.pathCost+1));
					}
				}else{
					queue.add(new Node(newState2, parent, 
							heuristic(newState2) + parent.pathCost +1, parent.pathCost+1));
				}

			}
			if (parent.state.canPushLeft()){
//				System.err.println("LEFT");
				GameState newState3 = parent.state.pushLeft();
				if(newState3.isLastMovePush()){
					int boxY = newState3.getPositionNow().row;
					int boxX = newState3.getPositionNow().column-1;
					if(safePosition(boxX, boxY)){
						queue.add(new Node(newState3, parent, 
								heuristic(newState3) + parent.pathCost +1, parent.pathCost+1));
					}
				}else{
					queue.add(new Node(newState3, parent, 
							heuristic(newState3) + parent.pathCost +1, parent.pathCost+1));
				}

			}
			if (parent.state.canPushRight()){
//				System.err.println("RIGHT");
				GameState newState4 = parent.state.pushRight();
				if(newState4.isLastMovePush()){
					int boxY = newState4.getPositionNow().row;
					int boxX = newState4.getPositionNow().column+1;
					if(safePosition(boxX, boxY)){
						queue.add(new Node(newState4, parent, 
								heuristic(newState4) + parent.pathCost +1, parent.pathCost+1));
					}
				}else{
					queue.add(new Node(newState4, parent, 
							heuristic(newState4) + parent.pathCost +1, parent.pathCost+1));
				}
			}
			counter++;
		}
		System.err.println("no path found");
		return "";
		

	}
	
	//TODO Improve so it checks for more than just corners
	private boolean safePosition(int row, int col) {
		if (unsafePosMap.getCharAt(row, col) == 'X'){
			return false;
		}else{
			return true;
		}
		
//			if (state.getCharAt(row - 1, col) == '#') {
//				if (state.getCharAt(row, col - 1) == '#' || state.getCharAt(row, col + 1) == '#') {
//					return false;
//				}
//			}
//			// if lower corner
//			if (state.getCharAt(row + 1, col) == '#') {
//				if (state.getCharAt(row, col - 1) == '#' || state.getCharAt(row, col + 1) == '#') {
//					return false;
//				}
//			}
//		return true;
		
	}
	
	// Marks all corners as X and spaces between 2 corners and a wall as Y 
	//TODO finish. Have to mark Y. Marking corners is implemented
	private GameState markUnsafePositions(GameState initialState)
	{
		
		char[][] arr = GameState.copyMatrix(initialState.getGameBoard());
//		for(int i= 0; i < arr.length; i++ )
//		{
//			for(int j = 0; j < arr[0].length; j++)
//			{
//				if (arr[i][j] == '#' &&  (i + 1) < arr.length && arr[i + 1][j] == '#' && 
//						(j + 1) < arr[0].length  && arr[i][j + 1] == '#' )
//				{
//					arr[i][j] = 'X';
//				}
//			}
//		}
		//Note the index used Mark corners with X
		for (int i = 1; i < arr.length-1; i++) {
			for (int j = 1; j < arr[0].length-1; j++) {
				if(arr[i-1][j] == '#'){
					if(arr[i][j-1] == '#'||arr[i][j+1] == '#'){
						arr[i][j] = 'X';
					}
				}else if(arr[i+1][j] == '#'){
					if(arr[i][j-1] == '#'||arr[i][j+1] == '#'){
						arr[i][j] = 'X';
					}
				}
			}
			
		}
//		Mark deadlock walls with 'Y'

		return new GameState(arr);
	}

	private String makePathString(Node endNode)
	{
		Node tmp = endNode;
		StringBuilder sb = new StringBuilder();
		while(tmp.parent != null)
		{
			if (tmp.state.getPositionNow().row > tmp.parent.state.getPositionNow().row && tmp.state.getPositionNow().column == tmp.parent.state.getPositionNow().column)
				sb.append("D");
			else if (tmp.state.getPositionNow().row < tmp.parent.state.getPositionNow().row && tmp.state.getPositionNow().column == tmp.parent.state.getPositionNow().column)
				sb.append("U");
			else if (tmp.state.getPositionNow().row == tmp.parent.state.getPositionNow().row && tmp.state.getPositionNow().column < tmp.parent.state.getPositionNow().column)
				sb.append("L");
			else if (tmp.state.getPositionNow().row == tmp.parent.state.getPositionNow().row && tmp.state.getPositionNow().column > tmp.parent.state.getPositionNow().column)
				sb.append("R");

			tmp = tmp.parent;
		}
		return sb.reverse().toString();
		
	}


	//if deadlock, give minus inifinty (check if it is possible to push the box in all the directions). 
	//Can also check if a box is in a corner. Or if it can't advance towards any goal.
	//for example, give some minus points if the box is along the wall or next to another box

	//give a total score for the distance between the goals and the boxes.
	//give infinity score if all boxes on goal
	// give high score if a box is placed in a position where it is "out of the way" from the others
	//heuristic function measuring distance to goal by pythagoras
	private static double distance(Position p, Position goal){
		return Math.sqrt(Math.pow((p.column-goal.column), 2) + Math.pow((p.row-goal.row), 2));
	}

	private static double heuristic(GameState state){
		ArrayList<Position> goals = findGoal(state);
		ArrayList<Position> boxes = findBoxes(state);
		double heur = 0;
		if(goals.isEmpty() || boxes.isEmpty()){
			return heur;
		}
		if(state.isWinning()){
			return Integer.MAX_VALUE;
		}
		if(state.isLastMovePush()){
			return heur -= 3;
		}
		for (int i = 0; i < goals.size(); i++) {
			for (int j = 0; j < boxes.size(); j++) {
				heur += distance(goals.get(i), boxes.get(j));
				heur += 100*distance(boxes.get(j), state.getPositionNow());
			}
		}
		return heur;
	}

	//	double totalCost(Position p, Position goal, int stepsThisFar){
	//		return heuristic(p, goal) + stepsThisFar;
	//	}

	//With no box on it
	private static ArrayList<Position> findGoal(GameState aState){
		ArrayList<Position> pos = new ArrayList<Position>();
		for (int i = 0; i < aState.getRows(); i++) {
			for (int j = 0; j < aState.getColumns(); j++) {
				if(aState.getCharAt(i, j) == C.goal||aState.getCharAt(i, j) == C.playerOnGoal){ 
					pos.add(new Position(i, j));
				}
			}
		}
		return pos;
	}
	private static ArrayList<Position> findBoxes(GameState aState){
		ArrayList<Position> pos = new ArrayList<Position>();
		for (int i = 0; i < aState.getRows(); i++) {
			for (int j = 0; j < aState.getColumns(); j++) {
				if(aState.getCharAt(i, j) == C.box){ 
					pos.add(new Position(i, j));
				}
			}
		}
		return pos;
	}

}

