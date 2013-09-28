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
	
	private class N implements Comparable<N>
	{
		P p, b;
		int s;
		
		@Override
		public int compareTo(N b)
		{
			if(s < b.s) return -1;
    		if(s > b.s) return +1;
    		return 0;
		}
	}
	
	private static P[] adjacent;
	public static int rows, columns;
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
	}
	
	public void PossibleBasic(Collection<State> c)
	{
		//todo check if adjacent cells are pushable
	}
	
	private static int ManhattanDistance(P a, P b)
	{
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
	
	private boolean GreedyDFS(N parent)
	{
		/*
		Queue<N> queue = new PriorityQueue<N>();

		for(P a : adjacent)
		{
			N cn = new N();
			cn.p = new P(p.x + a.x, p.y + a.y);
			
			//if(cn.p.x == cn.)
			
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
			*/
			return null;
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
	
	public boolean Push(P p, P d)
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
		
		player.x = pushed.x;
		player.y = pushed.y;
		return true;
	}
	
	public void PossibleBox(Collection<State> c, P box)
	{
		//check in which direction the box and be pushed
		for(P a : adjacent)
		{
			P ap = new P(box.x + a.x, box.y + a.y); //adjacent
			P op = new P(box.x - a.x, box.y - a.y); //opposite
			
			if(walls.contains(ap))
				continue;
			else if(boxes.contains(ap))
				continue;
			
			if(walls.contains(op))
				continue;
			else if(boxes.contains(op))
				continue;
			
			//both sides are either emtpy spaces or goals or player
			{
				N n = new N();
				n.b = ap;
				n.p = player;
				
				if(GreedyDFS(n))
				{
					State cs = new State();
					cs.boxes.addAll(boxes);
					cs.player = player;
					cs.Push(n.p, n.b);
				}
			}
			
		}
	}
	
	public void PossibleAdvanced(Collection<State> c)
	{
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
					System.err.println(C.wall);
				else if(!w && b && !g && !p)
					System.err.println(C.box);
				else if(!w && !b && g && !p)
					System.err.println(C.goal);
				else if(!w && !b && !g && p)
					System.err.println(C.player);
				else if(!w && !b && !g && !p)
					System.err.println(C.empty);
				else if(!w && b && g && !p)
					System.err.println(C.boxOnGoal);
				else if(!w && !b && g && p)
					System.err.println(C.playerOnGoal);
				else
					System.err.println("?"); //throw exception?
			}
			System.err.println();
		}
	}
	
	public int GetHash()
	{
		//todo just sum everything multiply a something and use modulo
		return 0;
	}
}
