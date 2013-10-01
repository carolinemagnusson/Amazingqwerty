import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class State
{
	private class N implements Comparable<N>
	{
		P player; //position of the greedy player
		P besideBox; //position that is adjacent to a box, and that is free space
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

	private static P[] adjacent_lrud = new P[4];
	private static P[] adjacent_lu = new P[2];
	public static int rows, columns;
	public static Set<P> walls = new HashSet<P>();
	public static Set<P> goals = new HashSet<P>();
	public static Set<P> unsafePositions = new HashSet<P>();
	public P player; //player position
	public Set<P> boxes = new HashSet<P>();
	public P pushDirection; //direction that the player push the box. Used for building path
	private static Set<P> visited;
	
	public State()
	{
	}

	public State(char[][] m)
	{
		adjacent_lrud[0] = new P(-1, 0); //left
		adjacent_lrud[1] = new P(+1, 0); //right
		adjacent_lrud[2] = new P(0, -1); //up
		adjacent_lrud[3] = new P(0, +1); //down

		adjacent_lu[0] = new P(-1, 0); //left
		adjacent_lu[1] = new P(0, -1); //up

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
					pushDirection = new P(0, 0);
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

	private boolean GreedyDFS(N parentNode, int depth)
	{
		//returns true if a path was found
		//returns false if no path was found

		//dont revisit positions
		{
			if(visited.contains(parentNode.player))
				return false;

			visited.add(parentNode.player);
		}

		//System.err.println("Greedy DFS depth:" + depth + " p:" + pn.p.x + "," + pn.p.y + " b:" + pn.b.x + "," + pn.b.y);

		//simple case
		if(parentNode.player.x == parentNode.besideBox.x && parentNode.player.y == parentNode.besideBox.y)
		{
			//System.err.println("greedy path found");
			return true;
		}

		Queue<N> queue = new PriorityQueue<N>(4);

		//add all possible adjacent paths to queue
		for(P a : adjacent_lrud)
		{
			P newPos = new P(parentNode.player.x + a.x, parentNode.player.y + a.y);

			if(walls.contains(newPos))
				continue;
			if(boxes.contains(newPos))
				continue;

			N childNode = new N();
			childNode.player = newPos;
			childNode.besideBox = parentNode.besideBox;
			childNode.s = ManhattanDistance(childNode.player, childNode.besideBox);
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
	
	//TODO finish
	// Find path from the position in parentState to the position in nextState
	//The path is the path from parentstate.player to the position next to the box moved, 
	//to nextState.player where the player is when the box is moved and the player is on the place where the box were
	public static String getPath(State nextState, State parentState){
		Set<P> visited = new HashSet<P>();
		N first = parentState.new N();
		first.player = parentState.player;
		first.besideBox = new P(nextState.player.x -nextState.pushDirection.x, nextState.player.y-nextState.pushDirection.y); 
		//Pasted from the search above.
		Queue<N> queue = new PriorityQueue<N>();
		queue.add(first);
		while (!queue.isEmpty()) {
			N pn = queue.poll();
			P playerBeforePush = new P(nextState.player.x -nextState.pushDirection.x, nextState.player.y-nextState.pushDirection.y);
			if(playerBeforePush.equals(pn.player)){
				//Find which direction the box is moved
				if(!nextState.pushDirection.equals(new P(0,0))){ //if nextState is first state.
					pn.path.append(nextState.pushDirection);
				}
				return pn.path.toString();
			}
			for (P a : adjacent_lrud) {
				P newPos = new P(parentState.player.x + a.x,
						parentState.player.y + a.y);

				if (walls.contains(newPos))
					continue;
				if (parentState.boxes.contains(newPos))
					continue;
					
				N childNode = parentState.new N();
				childNode.player = newPos;
				childNode.besideBox = pn.besideBox;
				childNode.s = ManhattanDistance(childNode.player,childNode.besideBox);
				childNode.path.append(direction(a));
				queue.add(childNode);
				visited.add(newPos);
			}
		}
		return "";
		
	}
	
	static private String direction(P direction){
		if(direction.equals(adjacent_lrud[0])){
			return "L";
		}
		else if(direction.equals(adjacent_lrud[1])){
			return "R";
		}
		else if(direction.equals(adjacent_lrud[2])){
			return "U";
		}
		else{
			System.err.println("Direction func does not work");
		}
		return "D";
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
		pushDirection = d;
		player = pushed;
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
				else if(unsafePositions.contains(ap))
					continue;

				if(walls.contains(op))
					continue;
				else if(boxes.contains(op))
					continue;
				else if(unsafePositions.contains(op))
					continue;
			}

			//check if there's a path to the free space next to the box
			{

				N n = new N();
				n.besideBox = ap;
				n.player = player;
				visited = new HashSet<P>();

				
				if(GreedyDFS(n, 0))
				{
					State childState = new State();
					childState.boxes.addAll(boxes);
					childState.player = player;
					childState.Push(n.besideBox, new P(-a.x, -a.y));
					//cs.Print();
					c.add(childState);
				}
			}

			//check the other side too
			{
				N n = new N();
				n.besideBox = op;
				n.player = player;
				visited = new HashSet<P>();

				if(GreedyDFS(n, 0))
				{
					State cs = new State();
					cs.boxes.addAll(boxes);
					cs.player = box;
					cs.Push(n.besideBox, a);
					//cs.Print();
					c.add(cs);
				}
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
			n.player = player;
			n.besideBox = new P(4, 7);
			n.s = 0;

			System.err.println("px:" + n.player.x + " py:"+ n.player.y + " bx:" + n.besideBox.x + " by:" + n.besideBox.y);
			//System.err.println(GreedyDFS(n, player));
			Collection<State> c = new LinkedList<State>(); //collection
			PossibleBox(c, n.besideBox);
			for(State s : c)
				s.Print();
		}
	}
	public void updateUnsafePositions()
	{
		unsafePositions = unsafePositions();
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
				if (goals.contains(tmpP))
				{
					containsGoal = true;
					break;
				} else if(walls.contains(new P(i,p.y - 1)) && !boxes.contains(tmpP) && !walls.contains(tmpP) && !goals.contains(tmpP))
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
				if (goals.contains(tmpP))
				{
					containsGoal = true;
					break;
				} else if(walls.contains(new P(i,p.y + 1)) && !boxes.contains(tmpP) && !walls.contains(tmpP) && !goals.contains(tmpP))
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
				if (goals.contains(tmpP))
				{
					containsGoal = true;
					break;
				} else if(walls.contains(new P(p.x - 1, i)) && !boxes.contains(tmpP) && !walls.contains(tmpP) && !goals.contains(tmpP))
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
				} else if(walls.contains(new P(p.x + 1, i)) && !boxes.contains(tmpP) && !walls.contains(tmpP) && !goals.contains(tmpP))
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

		//TODO some trick with the player?
		return player.x == s.player.x && player.y == s.player.y;
	}

	@Override
	public int hashCode(){
		int hash = 0;

		for(P box : boxes)
		{
			hash += box.x * 5234544;
			hash += box.y * 6463553;
		}

		hash += player.x * 1000000;
		hash += player.y * 4000000;

		//TODO some trick with the player?
		return hash;
	}
}
