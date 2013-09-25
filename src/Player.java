import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Player extends AbstractPlayer {
	HashMap<String,Node> visitedStates = new HashMap<String,Node>();
	int counter = 0;
	PriorityQueue<Node> queue;
	@Override
	public String play(GameState state) {
		
		queue = new PriorityQueue<Node>();

		Node startNode = new Node(state, null, 0, heuristic(state));
		
		queue.add(startNode);
		while(!queue.isEmpty()){
			if (queue.peek().state.isWinning()){
				System.err.println(queue.peek().state.toString());
				return makePathString(queue.peek());
			} 
			System.err.println("Loopcount " + counter);
			Node parent = queue.poll();
			if (visitedStates.containsKey(parent.toString())){
				continue;
			}
			visitedStates.put(parent.toString(), parent);
			System.err.println(parent.toString());
			if (parent.state.canPushUp()){
				System.err.println("UP");
				GameState newState = parent.state.pushUp();

				queue.add(new Node(newState, parent, 
						heuristic(newState) + parent.pathCost +1, parent.pathCost+1));

			}

			if (parent.state.canPushDown()){
				System.err.println("DOWN");
				GameState newState2 = parent.state.pushDown();
				queue.add(new Node(newState2, parent, 
						heuristic(newState2) + parent.pathCost +1, parent.pathCost+1));

			}
			if (parent.state.canPushLeft()){
				System.err.println("LEFT");
				GameState newState3 = parent.state.pushLeft();
				queue.add(new Node(newState3, parent, 
						heuristic(newState3) + parent.pathCost +1, parent.pathCost+1));

			}
			if (parent.state.canPushRight()){
				System.err.println("RIGHT");
				GameState newState4 = parent.state.pushRight();
				queue.add(new Node(newState4, parent, 
						heuristic(newState4) + parent.pathCost +1, parent.pathCost+1));

			}
			counter++;
		}
		return "";


	}
	
	// Marks all corners as X and spaces between 2 corners and a wall as £ 
	//TODO finish
	private GameState markUnsafePositions(GameState initialState)
	{
		
		char[][] arr = GameState.copyMatrix(initialState.state);
		for(int i= 0; i < arr.length; i++ )
		{
			for(int j = 0; j < arr[0].length; j++)
			{
				if (arr[i][j] == '#' &&  (i + 1) < arr.length && arr[i + 1][j] == '#' && (j + 1) < arr[0].length  && arr[i][j + 1] == '#' )
				{
					arr[i][j] = 'X';
				}
			}
		}
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
		return sb.toString();
		
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
		System.out.println(goals.size());
		System.out.println(boxes.size());
		//TODO check the box, goal distance between the box and goal closest to each other.
//		for (int i = 0; i < goals.size(); i++) {
//			heur += distance(goals.get(i), boxes.get(i));
//		}
//		for (int i = 0; i < boxes.size(); i++) {
//			heur += distance(state.getPositionNow(),boxes.get(i));
//		}
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
		System.out.println("row: " +aState.getRows());
		System.out.println("col: " +aState.getColumns());
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

