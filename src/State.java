import java.util.*;

public class State
{
	private class P
	{
		public int x, y;
		
		public P(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}
	
	private class N
	{
		P p, b;
		int s;
		
		@Override
		public int compareTo(DNode b)
		{
			if(s < b.s) return -1;
    		if(s > b.s) return +1;
    		return 0;
		}
	}
	
	private static P[] adjacent;
	
	public static Set<P> walls = new HashSet<P>();
	public static Set<P> goals = new HashSet<P>();
	public P player; //player position
	public Set<P> boxes = new HashSet<P>();
	
	public State()
	{
	}
	
	public State(char[][] m)
	{
		adjacent = new P[4];
		adjacent[0] = new P(-1, 0); //left
		adjacent[1] = new P(+1, 0); //right
		adjacent[2] = new P(0, -1); //up
		adjacent[3] = new P(0, +1); //down
		
		for(int iy=0; iy<m.length; iy++)
		{
			for(int ix=0; ix<m[iy].length; ix++)
			{
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
	}
	
	public void PossibleBasic(Collection<State> c)
	{
		//todo check if adjacent cells are pushable
	}
	
	private static int ManhattanDistance(P a, P b)
	{
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
	
	private P GreedyDFS(N parent)
	{
		private Queue<N> queue = new PriorityQueue<N>();

		for(P a : adjacent)
		{
			N cn = new N();
			cn.p = new P(p.x + a.x, p.y + a.y);
			
			if(cn.p.x == cn.)
			
			cn.b = b;
			cn.s = ManhattanDistance(cn.p, cn.b);
			queue.add(cn);
		}
		
		while(queue.size() != 0)
		{
			if(GreedyDFS(queue.poll()) != null)
			{
			}
		}
		
		if(boxes.contains(p))
			return p;
	}
	
	public void PossibleAdvanced(Collection<State> c)
	{
		//seek all states where you can push a box
		for(P b : boxes)
		{
			//seek a free path to box
			
			
			//greedy dfs search
			
			//todo bidirectional search?
			
			if(true)
			{
				State s = new State();
				s.player = b;
				s.boxes = boxes;
			}
		}
	}
}
