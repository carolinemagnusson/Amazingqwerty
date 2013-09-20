import java.util.ArrayList;
import java.util.PriorityQueue;


public class CarolinePlayer extends AbstractPlayer{

	PriorityQueue<Node> queue;
	@Override
	String play(GameState state) {
		queue = new PriorityQueue<Node>();
		
		Node startNode = new Node(state, null, 0, heuristic(state));
		queue.add(startNode);
		while(!queue.isEmpty() && !queue.peek().state.isWinning()){
			Node parent = queue.poll();
			
			if (parent.state.canPushUp()){
				GameState newState = state.pushUp();
					queue.add(new Node(newState, parent, 
							heuristic(newState) + parent.pathCost +1, parent.pathCost+1));
				
			}
			else if (parent.state.canPushDown()){
				GameState newState2 = state.pushDown();
					queue.add(new Node(newState2, parent, 
							heuristic(newState2) + parent.pathCost +1, parent.pathCost+1));
				
			}
			else if (parent.state.canPushLeft()){
				GameState newState3 = state.pushLeft();
					queue.add(new Node(newState3, parent, 
							heuristic(newState3) + parent.pathCost +1, parent.pathCost+1));
				
			}
			else if (parent.state.canPushRight()){
				GameState newState4 = state.pushRight();
					queue.add(new Node(newState4, parent, 
							heuristic(newState4) + parent.pathCost +1, parent.pathCost+1));
				
			}
			
		}
		
		
		
	}
	
	
	//if deadlock, give minus inifinty (check if it is possible to push the box in all the directions). 
	//Can also check if a box is in a corner. Or if it can't advance towards any goal.
	//for example, give some minus points if the box is along the wall or next to another box
	
	//give a total score for the distance between the goals and the boxes.
	//give infinity score if all boxes on goal
	// give high score if a box is placed in a position where it is "out of the way" from the others
	//heuristic function measuring distance to goal by pythagoras
	static double distance(Position p, Position goal){
		return Math.sqrt(Math.pow((p.column-goal.column), 2) + Math.pow((p.row-goal.row), 2));
	}
	
	static double heuristic(GameState state){
		ArrayList<Position> goals = findGoal(state);
		ArrayList<Position> boxes = findBoxes(state);
		double heur = 0;
		//TODO check the box, goal distance between the box and goal closest to each other.
		for (int i = 0; i < goals.size(); i++) {
			heur += distance(goals.get(i), boxes.get(i));
		}
		for (int i = 0; i < boxes.size(); i++) {
			heur += distance(state.getPositionNow(),boxes.get(i));
		}
		return heur;
	}
	
//	double totalCost(Position p, Position goal, int stepsThisFar){
//		return heuristic(p, goal) + stepsThisFar;
//	}
	
	//With no box on it
	static ArrayList<Position> findGoal(GameState aState){
		ArrayList<Position> pos = new ArrayList<Position>();
		for (int i = 0; i < aState.getRows(); i++) {
			for (int j = 0; j < aState.getRows(); j++) {
				if(aState.getGameBoard()[i][j] == '.'||aState.getGameBoard()[i][j] == '+'){ //change so const is used
					pos.add(new Position(i, j));
				}
			}
		}
		return pos;
	}
	static ArrayList<Position> findBoxes(GameState aState){
		ArrayList<Position> pos = new ArrayList<Position>();
		for (int i = 0; i < aState.getRows(); i++) {
			for (int j = 0; j < aState.getRows(); j++) {
				if(aState.getGameBoard()[i][j] == '$'){ //change to const
					pos.add(new Position(i, j));
				}
			}
		}
		return pos;
	}

}
 // need to keep track of how we went there. Think of how!
class Node implements Comparable<Node>{
	GameState state;
	Node myParent;
	//total score of start to goal
	double score;
	//cost to go to this state
	double pathCost;
	Node(GameState g, Node parent, double s, double p){
		state = g;
		myParent = parent;
		score = s;
		double pathCost = p;
	}
	@Override
	public int compareTo(Node arg0) {
		if(this.score < arg0.score){
			return -1;
		}
		else if(this.score>arg0.score){
			return 1;
		}
		return 0;
	}
}
