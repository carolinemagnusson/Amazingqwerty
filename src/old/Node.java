package old;
public class Node implements Comparable<Node>{
	GameState state;
	Node parent;
	//total score of start to goal
	double score;
	//cost to go to this state
	double pathCost;
	Node(GameState g, Node parent, double s, double p){
		this.state = g;
		this.parent = parent;
		this.score = s;
		this.pathCost = p;
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
	@Override
	public String toString()
	{
		return state.toString() + " " + score + " " + pathCost;
	}

}