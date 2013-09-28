import java.util.*;

public class State
{
	private class N implements Comparable<N>
	{
		P p; //position of the greedy player
		P b; //position that is adjacent to a box, and that is free space
		int s;

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
				else if(c == C.player)
					player = new P(ix, iy);
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

	private static int ManhattanDistance(P a, P b)
	{
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}

	private boolean GreedyDFS(N pn, int depth)
	{
		//pn parent node
		//returns true if a path was found
		//returns false if no path was found

		//dont revisit positions
		{
			if(visited.contains(pn.p))
				return false;

			visited.add(pn.p);
		}

		//System.err.println("Greedy DFS depth:" + depth + " p:" + pn.p.x + "," + pn.p.y + " b:" + pn.b.x + "," + pn.b.y);

		//simple case
		if(pn.p.x == pn.b.x && pn.p.y == pn.b.y)
		{
			//System.err.println("greedy path found");
			return true;
		}

		Queue<N> queue = new PriorityQueue<N>(4);

		//add all possible adjacent paths to queue
		for(P a : adjacent_lrud)
		{
			P np = new P(pn.p.x + a.x, pn.p.y + a.y);

			if(walls.contains(np))
				continue;
			if(boxes.contains(np))
				continue;

			N cn = new N(); //cn child node
			cn.p = np;
			cn.b = pn.b;
			cn.s = ManhattanDistance(cn.p, cn.b);
			queue.add(cn);
		}

		//fetch best one and recurse
		while(queue.size() != 0)
		{
			N cn = queue.poll();
			if(GreedyDFS(cn, depth+1))
			{
				return true;
			}
		}

		return false;		
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
	}

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

			//check if theres a path to the free space next to the box
			{

				N n = new N();
				n.b = ap;
				n.p = player;
				visited = new HashSet<P>();

				if(GreedyDFS(n, 0))
				{
					State cs = new State();
					cs.boxes.addAll(boxes);
					cs.player = player;
					cs.Push(n.b, new P(-a.x, -a.y));
					//cs.Print();
					c.add(cs);
				}
			}

			//check the other side too
			{
				N n = new N();
				n.b = op;
				n.p = player;
				visited = new HashSet<P>();

				if(GreedyDFS(n, 0))
				{
					State cs = new State();
					cs.boxes.addAll(boxes);
					cs.Push(n.b, a);
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
			n.p = player;
			n.b = new P(4, 7);
			n.s = 0;

			System.err.println("px:" + n.p.x + " py:"+ n.p.y + " bx:" + n.b.x + " by:" + n.b.y);
			//System.err.println(GreedyDFS(n, player));
			Collection<State> c = new LinkedList<State>(); //collection
			PossibleBox(c, n.b);
			for(State s : c)
				s.Print();
		}
	}
	public void updateUnsafePositions()
	{
		unsafePositions = unsafePositions();
	}
	
	//TODO Should unsafe positions change when a box is placed on a goal between to corners ??
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

		//todo some trick with the player?
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

		//todo some trick with the player?
		return hash;
	}
}
