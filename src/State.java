import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

class PathNode implements Comparable<PathNode>
{
	P pA; //position of the greedy player
	P pB; //position that is adjacent to a box, and that is free space
	int s;
	StringBuilder path = new StringBuilder(); // Optional
	P pushDir; //Needs to recreate path, optional

	@Override
	public int compareTo(PathNode b)
	{
		if(s < b.s) return -1;
		if(s > b.s) return +1;
		return 0;
	}
}


public class State
{
	public class N implements Comparable<N>
	{
		P pA; //position of the greedy player
		P PB; //position that is adjacent to a box, and that is free space
		int s;
		StringBuilder path; // Optional
		P pushDir; //Needs to recreate path, optional

		@Override
		public int compareTo(N b)
		{
			if(s < b.s) return -1;
			if(s > b.s) return +1;
			return 0;
		}
	}

	public static P[] adjacent_lrud = new P[]{new P(-1, 0), new P(+1, 0), new P(0, -1), new P(0, +1)}; //left right up down
	public static P[] adjacent_lu = new P[]{new P(-1, 0), new P(0, -1)}; //left up
	public int rows, columns;
	public Set<P> walls = new HashSet<P>();
	public Set<P> goals = new HashSet<P>();
	public P player; //player position
	public Set<P> boxes = new HashSet<P>();
	private static Set<P> visited = new HashSet<P>();
	public static int hashCollissionCounter = 0;
	public P boxMoved; //This is the new position of the box that was moved. OBS! Only for forward search
	private P leftUpperP;
	
	public State()
	{
	}

	public State(char[][] m)
	{
		rows = m.length;
		columns = Integer.MIN_VALUE;

		for(int iy=0; iy<m.length; iy++)
		{
			for(int ix=0; ix<m[iy].length; ix++)
			{
				if(m[iy].length > columns)
					columns = m[iy].length;

				char c = m[iy][ix];

				if(c == C.wall)
					walls.add(new P(ix, iy));
				else if(c == C.box)
					boxes.add(new P(ix, iy));
				else if(c == C.goal)
					goals.add(new P(ix, iy));
				else if(c == C.player){
					player = new P(ix, iy);
				}
				else if(c == C.boxOnGoal)
				{
					P p = new P(ix, iy);
					boxes.add(p);
					goals.add(p);
				}
				else if(c == C.playerOnGoal)
				{
					P p = new P(ix, iy);
					goals.add(p);
					player = p;
				}
				//else empty
			}
		}
		setLeftUpperPosition();

	}

	public State copyState()
	{
		//copy only when modified
		State newState = new State();
		newState.boxes.addAll(this.boxes);
		newState.walls.addAll(this.walls);
		newState.goals.addAll(this.goals);
		newState.player = this.player;
		newState.rows = this.rows;
		newState.columns = this.columns;
		setLeftUpperPosition();
		return newState;
	}

	public static int ManhattanDistance(P a, P b)
	{
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
	public boolean GreedyDFSWrapper(P pA, P pB){
		visited.clear();
		N first = new N();
		first.pA = pA;
		first.PB = pB;
		return GreedyDFS(first, 0);
	}
	private boolean GreedyDFS(N parentNode, int depth)
	{
		//returns true if a path was found returns false if no path was found, dont revisit positions
		{
			if(visited.contains(parentNode.pA))
				return false;

			visited.add(parentNode.pA);
		}
		//simple case
		if(parentNode.pA.x == parentNode.PB.x && parentNode.pA.y == parentNode.PB.y)
		{
			return true;
		}

		Queue<N> queue = new PriorityQueue<N>(4);

		//add all possible adjacent paths to queue
		for(P a : adjacent_lrud)
		{
			P newPos = new P(parentNode.pA.x + a.x, parentNode.pA.y + a.y);

			if(walls.contains(newPos))
				continue;
			if(boxes.contains(newPos))
				continue;

			N childNode = new N();
			childNode.pA = newPos;
			childNode.PB = parentNode.PB;
			childNode.s = ManhattanDistance(childNode.pA, childNode.PB);
			queue.add(childNode);
		}

		//fetch best one and recurse
		while(queue.size() != 0)
		{
			N childNode = queue.poll();
			if(GreedyDFS(childNode, depth+1))
			{
				return true;
			}
		}

		return false;
	}

	public static String getPath2(State fromState, State toState)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("");

		for(P toBox : toState.boxes)
		{
			if(!fromState.boxes.contains(toBox))
			{
				//found the moved box
				P movedBox = toBox;

				for(P fromBox : fromState.boxes)
				{
					if(!toState.boxes.contains(fromBox))
					{
						P movedBoxPrevious = fromBox;
						P directionPush = new P(movedBox.x - movedBoxPrevious.x, movedBox.y - movedBoxPrevious.y);
						P playerBeforePush = new P(movedBoxPrevious.x - directionPush.x, movedBoxPrevious.y - directionPush.y);
						String pathstring = path(fromState, fromState.player, playerBeforePush);
						sb.append(pathstring);
						sb.append(pushDirection(directionPush));

						toState.player = movedBoxPrevious; //sneak modify the toState to fix a replay bug
						return sb.toString();
					}
				}
			}
		}

		sb.append(path(fromState, fromState.player, toState.player));
		return sb.toString();
	}

	private static String path(State state, P from, P to){
		StringBuilder sb = new StringBuilder();

		Queue<PathNode> queue = new PriorityQueue<PathNode>();
		Set<P> visited = new HashSet<P>();

		{
			PathNode firstNode = new PathNode();
			firstNode.pA = from;
			firstNode.pB = to;
			firstNode.s = ManhattanDistance(from, to);
			queue.add(firstNode);
		}

		while(!queue.isEmpty()){
			PathNode nextNode = queue.poll();
			visited.add(nextNode.pA);

			if(nextNode.pA.x == to.x && nextNode.pA.y == to.y)
			{
				return nextNode.path.toString();
			}

			for(P direction: adjacent_lrud){

				P playerNext = new P(nextNode.pA.x + direction.x, nextNode.pA.y + direction.y);

				if(state.boxes.contains(playerNext))
					continue;
				if(state.walls.contains(playerNext))
					continue;
				if(visited.contains(playerNext))
					continue;

				{
					PathNode childNode = new PathNode();
					childNode.pA = playerNext;
					childNode.pB = to;
					childNode.path.append(nextNode.path.toString());
					childNode.path.append(pushDirection(direction));
					childNode.s = ManhattanDistance(playerNext, to);
					queue.add(childNode);
				}
			}
		}

		return sb.toString();
	}

	static private String pushDirection(P direction){
		if(direction.equals(adjacent_lrud[0])){
			return "L";
		}
		else if(direction.equals(adjacent_lrud[1])){
			return "R";
		}
		else if(direction.equals(adjacent_lrud[2])){
			return "U";
		}
		else if (direction.equals(adjacent_lrud[3])){
			return "D";
		}
		else{
			System.err.println("Direction func does not work");
		}
		return "";

	}


	public void Push(P p, P d)
	{
		//p player position
		//d player push direction
		P pushed = new P(p.x + d.x, p.y + d.y);
		P behindPushed = new P(p.x + d.x*2, p.y + d.y*2);

		//assumes its okay to do so
		if(boxes.contains(pushed))
		{
			boxes.remove(pushed);
			boxes.add(behindPushed);
		}
		player = pushed;
		boxMoved = behindPushed;
		setLeftUpperPosition();
	}

	public void pull(P p, P d)
	{
		//p player position
		//d direction the box will be pulled
		P box = new P(p.x-d.x*2, p.y-d.y*2);
		P adjacent = new P(p.x-d.x, p.y-d.y);

		//assumes its okay to do so
		boxes.remove(box);
		boxes.add(adjacent);
		player = p;
		setLeftUpperPosition();
	}

	//TODO player has to move to the position next to the box
	public void PossibleBox(Collection<State> c, P box)
	{
		//somewhat expensive call

		//check in which direction the box and be pushed
		for(P a : adjacent_lu)
		{
			P ap = new P(box.x + a.x, box.y + a.y); //adjacent position
			P op = new P(box.x - a.x, box.y - a.y); //opposite position

			//skip all boxes that are not free on both sides
			{
				if(walls.contains(ap))
					continue;
				else if(boxes.contains(ap))
					continue;

				if(walls.contains(op))
					continue;
				else if(boxes.contains(op))
					continue;
			}

			//check if there's a path to the free space next to the box
			{
//					if(!unsafePositions.contains(op)){

					N n = new N();
					n.PB = ap; // the position next to the box that the
										// player should move to
					n.pA = player;
					visited = new HashSet<P>();

					if (GreedyDFS(n, 0)) {
						State childState = this.copyState();
						childState.player = ap;
						childState.Push(n.PB, new P(-a.x, -a.y));
						// cs.Print();
//							if(!childState.isDynamicDeadlocks()){
							c.add(childState);
//							}

//						}
				}
			}

			//check the other side too
			{
//					if (!unsafePositions.contains(ap)) {
					N n = new N();
					n.PB = op;
					n.pA = player;
					visited = new HashSet<P>();

					if (GreedyDFS(n, 0)) {
						State cs = this.copyState();
						cs.player = op;
						cs.Push(n.PB, a);
						// cs.Print();
//							if(cs.isDynamicDeadlocks()){
							c.add(cs);
//							}

//						}
				}
			}
		}
	}

	public void reversePossibleBox(Collection<State> c, P box)
	{
		//somewhat expensive call

		//check all direction around the box in which direction the box and be pulled
		for(P a : adjacent_lrud)
		{
			P ap = new P(box.x + a.x, box.y + a.y); //adjacent position
			P aap = new P(box.x + a.x*2, box.y + a.y*2); //adjacent to the adjacent position, the position where the player will stand on

			//skip all boxes that do not have two free spaces on all sides
			{
				if(walls.contains(ap))
					continue;
				else if(boxes.contains(ap))
					continue;

				if(walls.contains(aap))
					continue;
				else if(boxes.contains(aap))
					continue;
			}

			if(GreedyDFSWrapper(player, aap))
			{
				State childState = this.copyState();
				childState.pull(aap, a);
				c.add(childState);
			}
		}
	}

	public void PossibleAdvanced(Collection<State> c)
	{
		//expensive call

		//seek all states where you can push a box
		for(P box : boxes)
			PossibleBox(c, box);
	}

	public void reversePossibleAdvanced(Collection<State> c)
	{
		//expensive call

		//seek all states where you can pull a box
		for(P box : boxes)
			reversePossibleBox(c, box);
	}

	public void Print()
	{
		for(int iy=0; iy<rows; iy++)
		{
			for(int ix=0; ix<columns; ix++)
			{
				P xy = new P(ix, iy);
				boolean w = walls.contains(xy);
				boolean b = boxes.contains(xy);
				boolean g = goals.contains(xy);
				boolean p = xy.x == player.x && xy.y == player.y;

				if(w && !b && !g && !p)
					System.err.print(C.wall);
				else if(!w && b && !g && !p)
					System.err.print(C.box);
				else if(!w && !b && g && !p)
					System.err.print(C.goal);
				else if(!w && !b && !g && p)
					System.err.print(C.player);
				else if(!w && !b && !g && !p)
					System.err.print(C.empty);
				else if(!w && b && g && !p)
					System.err.print(C.boxOnGoal);
				else if(!w && !b && g && p)
					System.err.print(C.playerOnGoal);
				else
					System.err.print("?"); //throw exception?
			}
			System.err.println();
		}
	}

	public boolean isWin()
	{
		for(P goal : goals)
		{
			if(!boxes.contains(goal))
				return false;
		}

		return true;
	}



	@Override
	public boolean equals(Object b){
		hashCollissionCounter++;
		State s = (State)b;

		for(P box : boxes)
		{
			if(!s.boxes.contains(box))
				return false;
		}

		//if all the boxes have the same position and the player can find a path to the other player in the other "reality" then the state of the "realitys" must be the same
//		if(GreedyDFSWrapper(s.player, this.player)){
//			return true;
//		}
		if(leftUpperP==null){
			setLeftUpperPosition();
		}
		
		if(this.leftUpperP.x == s.leftUpperP.x && this.leftUpperP.y == s.leftUpperP.y)
			return true;

		return false;
	}

	@Override
	public int hashCode(){
		int hash = 0;

		for(P box : boxes)
		{
			hash += box.x * 5234544;
			hash += box.y * 6463553;
		}
		if(leftUpperP==null){
			setLeftUpperPosition();
		}
//		hash+= leftUpperP.x*153;
//		hash+= leftUpperP.y*2642;
		return hash;
	}

	public void setLeftUpperPosition(){
		// P.x is column and P.y is row
		Queue<P> queue = new LinkedList<P>();
		HashSet<P> visited = new HashSet<>();
		P startPos = this.player;
		P tempLeftMost = new P(columns, rows); // find smaller rows and columns,
												// in first hand smaller columns
												// (smaller x)
		queue.add(startPos);

		while (!queue.isEmpty()) {
			P next = queue.poll();
			visited.add(next);
			if (tempLeftMost.x > next.x) {
				tempLeftMost = new P(next.x, next.y);

			} else if (tempLeftMost.x == next.x) {
				if (tempLeftMost.y > next.y) {
					tempLeftMost = new P(next.x, next.y);
				}
			}
		}
		System.err.println("set new leftupper");
		System.err.println(leftUpperP==null ? "isnull":"notnull");
		if(leftUpperP == null){
			leftUpperP = new P(0,0);
		}
		leftUpperP.x = tempLeftMost.x;
		leftUpperP.y = tempLeftMost.y;
	}
//	
	public boolean isWin(State desiredState)
	{
		//check that all boxes and player in current state is equivalent to the desired state
		return equals(desiredState);
	}

	public Collection<State> GetAllPossibleEndings()
	{
		LinkedList<State> list = new LinkedList<State>();
		{
			//determine where the player can be, he must be next to a goal, also gives some impossible endings.
			for(P goal : goals)
			{
				for(P direction : adjacent_lrud)
				{
					P adjacentPosition = new P(goal.x + direction.x, goal.y + direction.y);

					if(goals.contains(adjacentPosition))
						continue;
					if(walls.contains(adjacentPosition))
						continue;

					State endState = this.copyState();
					endState.boxes.clear();

					for(P g : goals)
					{
						endState.boxes.add(g);
					}

					endState.player = adjacentPosition;
					list.add(endState);
				}
			}
		}

		return list;
	}
}
