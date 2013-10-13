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

	private static P[] adjacent_lrud = new P[]{new P(-1, 0), new P(+1, 0), new P(0, -1), new P(0, +1)}; //left right up down
	private static P[] adjacent_lu = new P[]{new P(-1, 0), new P(0, -1)}; //left up
	public int rows, columns;
	public Set<P> walls = new HashSet<P>();
	public Set<P> goals = new HashSet<P>();
	public Set<P> unsafePositions = new HashSet<P>();
	public P player; //player position
	public Set<P> boxes = new HashSet<P>();
	private static Set<P> visited = new HashSet<P>();

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

		// Create initial unsafe positions, these may need to be updated when box is place on goal
		updateUnsafePositions();
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
		return newState;
	}

	/*
	public void PossibleBasic(Collection<State> c)
	{
		//todo check if adjacent_lrud cells are pushable
	}

	public boolean CanPush(P p, P d)
	{
		//p player position
		//d player push direction
		P pushed = new P(p.x + d.x, p.y + d.y);
		P behindPushed = new P(p.x + d.x*2, p.y + d.y*2);

		if(walls.contains(pushed))
			return false;
		if(boxes.contains(pushed))
		{
			if(walls.contains(behindPushed))
				return false;
			if(boxes.contains(behindPushed))
				return false;
		}
		return true;
	}
	 */

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
		//returns true if a path was found
		//returns false if no path was found

		//dont revisit positions
		{
			if(visited.contains(parentNode.pA))
				return false;

			visited.add(parentNode.pA);
		}

		//System.err.println("Greedy DFS depth:" + depth + " p:" + pn.p.x + "," + pn.p.y + " b:" + pn.b.x + "," + pn.b.y);

		//simple case
		if(parentNode.pA.x == parentNode.PB.x && parentNode.pA.y == parentNode.PB.y)
		{
			//System.err.println("greedy path found");
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
		updateUnsafePositions();
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

	public void test()
	{
		//for(P box : boxes)
		{
			N n = new N();
			n.pA = player;
			n.PB = new P(4, 7);
			n.s = 0;

			System.err.println("px:" + n.pA.x + " py:"+ n.pA.y + " bx:" + n.PB.x + " by:" + n.PB.y);
			//System.err.println(GreedyDFS(n, player));
			Collection<State> c = new LinkedList<State>(); //collection
			PossibleBox(c, n.PB);
			for(State s : c)
				s.Print();
		}
	}
	public void updateUnsafePositions()
	{
		unsafePositions = unsafePositions();
	}

	//TODO this should also print dynamic deadlocks and otherwise unsafe positions
	public void printUnsafePositions()
	{
		for(int iy=0; iy<rows; iy++)
		{
			for(int ix=0; ix<columns; ix++)
			{
				P xy = new P(ix, iy);
				boolean w = walls.contains(xy);
				boolean b = boxes.contains(xy);
				boolean g = goals.contains(xy);
				boolean isUnsafe = unsafePositions.contains(xy);
				boolean p = xy.x == player.x && xy.y == player.y;
				if(isUnsafe)
					System.err.print('X');
				else
				{
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

			}
			System.err.println();
		}
	}
	private boolean isDynamicDeadlocks()
	{
		P position;
		for(int i = 0 ; i < this.rows; i++)
		{
			for (int j = 0; j < this.columns; j++)
			{
				position = new P(j,i);
				// Top left
				P p1 = new P(position.x -1, position.y);
				P p2 = new P(position.x - 1, position.y - 1);
				P p3 = new P(position.x, position.y - 1);
				if (boxes.contains(p1) && boxes.contains(p2) && boxes.contains(p3))
					return true;

				// Top right
				p1 = new P(position.x, position.y - 1);
				p2 = new P(position.x + 1, position.y - 1);
				p3 = new P(position.x + 1 , position.y);
				if (boxes.contains(p1) && boxes.contains(p2) && boxes.contains(p3))
					return true;

				// Bottom left
				p1 = new P(position.x - 1, position.y);
				p2 = new P(position.x -1, position.y + 1);
				p3 = new P(position.x , position.y + 1);
				if (boxes.contains(p1) && boxes.contains(p2) && boxes.contains(p3))
					return true;

				// Bottom right
				// Top right
				p1 = new P(position.x + 1, position.y);
				p2 = new P(position.x + 1, position.y + 1);
				p3 = new P(position.x , position.y + 1);
				if (boxes.contains(p1) && boxes.contains(p2) && boxes.contains(p3))
					return true;


			}
		}
		return false;
	}
	//TODO Should unsafe positions change when a box is placed on a goal between to corners ?? YES!
	private Set<P> unsafePositions()
	{
		Set<P> unsafePositions = new HashSet<P>();
		P possibleUnsafe,tmp1,tmp2;
		// Find corners
		for (int row = 0; row < rows; row++)
		{
			for(int col = 0; col < columns; col++)
			{
				possibleUnsafe = new P(col, row);
				if (!boxes.contains(possibleUnsafe) && !walls.contains(possibleUnsafe) && !goals.contains(possibleUnsafe))
				{
					// Check top left corner type
					tmp1 = new P(col - 1, row);
					tmp2 = new P(col, row - 1);

					if (walls.contains(tmp1) && walls.contains(tmp2))
						unsafePositions.add(possibleUnsafe);

					// Check for top right corner type
					tmp1 = new P(col + 1, row);
					tmp2 = new P(col, row - 1);
					if (walls.contains(tmp1) && walls.contains(tmp2))
						unsafePositions.add(possibleUnsafe);

					// Check bottom left corner type
					tmp1 = new P(col - 1, row);
					tmp2 = new P(col, row + 1);
					if (walls.contains(tmp1) && walls.contains(tmp2))
						unsafePositions.add(possibleUnsafe);

					// Check bottom right corner type
					tmp1 = new P(col + 1, row);
					tmp2 = new P(col , row + 1);
					if (walls.contains(tmp1) && walls.contains(tmp2))
						unsafePositions.add(possibleUnsafe);
				}

			}
		}
		HashSet<P> tmpSet = new HashSet<P>();
		HashSet<P> tmpFound = new HashSet<P>();
		boolean containsGoal = false, foundSecondCorner = false;
		P tmpP;
		for (P p : unsafePositions)
		{
			// Check right direction for horizontal unsafe states
			for(int i = p.x + 1; i < columns; i++)
			{
				tmpP = new P(i, p.y);
				if (goals.contains(tmpP) && !boxes.contains(tmpP))
				{
					containsGoal = true;
					break;
				} else if(walls.contains(new P(i,p.y - 1)) && !walls.contains(tmpP))
					tmpSet.add(tmpP);


				if (unsafePositions.contains(tmpP))
				{
					foundSecondCorner = true;
					break;
				}

			}
			if(!containsGoal && foundSecondCorner)
				tmpFound.addAll(tmpSet);

			tmpSet.clear();
			containsGoal = false;
			foundSecondCorner = false;

			for(int i = p.x + 1; i < columns; i++)
			{
				tmpP = new P(i, p.y);
				if (goals.contains(tmpP) && !boxes.contains(tmpP))
				{
					containsGoal = true;
					break;
				} else if(walls.contains(new P(i,p.y + 1)) && !walls.contains(tmpP))
					tmpSet.add(tmpP);

				if (unsafePositions.contains(tmpP))
				{
					foundSecondCorner = true;
					break;
				}

			}
			if(!containsGoal && foundSecondCorner)
				tmpFound.addAll(tmpSet);

			tmpSet.clear();
			containsGoal = false;
			foundSecondCorner = false;

			// Check down direction for vertical unsafe states
			for(int i = p.y + 1; i < rows; i++)
			{
				tmpP = new P(p.x, i);
				if (goals.contains(tmpP) && !boxes.contains(tmpP))
				{
					containsGoal = true;
					break;
				} else if(walls.contains(new P(p.x - 1, i)) && !walls.contains(tmpP))
					tmpSet.add(tmpP);

				if (unsafePositions.contains(tmpP))
				{
					foundSecondCorner = true;
					break;
				}

			}
			if(!containsGoal && foundSecondCorner)
				tmpFound.addAll(tmpSet);

			tmpSet.clear();
			containsGoal = false;
			foundSecondCorner = false;

			for(int i = p.y + 1; i < rows; i++)
			{
				tmpP = new P(p.x, i);
				if (goals.contains(tmpP))
				{
					containsGoal = true;
					break;
				} else if(walls.contains(new P(p.x + 1, i)) && !walls.contains(tmpP))
					tmpSet.add(tmpP);

				if (unsafePositions.contains(tmpP))
				{
					foundSecondCorner = true;
					break;
				}

			}
			if(!containsGoal && foundSecondCorner)
				tmpFound.addAll(tmpSet);

			tmpSet.clear();
			containsGoal = false;
			foundSecondCorner = false;


		}
		unsafePositions.addAll(tmpFound);
		return unsafePositions;
	}

	@Override
	public boolean equals(Object b){
		State s = (State)b;

		for(P box : boxes)
		{
			if(!s.boxes.contains(box))
				return false;
		}

		//if all the boxes have the same position and the player can find a path to the other player in the other "reality" then the state of the "realitys" must be the same
		if(GreedyDFSWrapper(s.player, this.player)){
			return true;
		}

		//return player.x == s.player.x && player.y == s.player.y;
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

		//dont hash the player, a check will be performed in equals()
		//hash += player.x * 1000000;
		//hash += player.y * 4000000;
		return hash;
	}

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
					//endState.Print();
				}
			}
		}

		return list;
	}
}
