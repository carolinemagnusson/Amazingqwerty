import java.util.*;

public class DanielPlayer extends AbstractPlayer
{
	class DNode implements Comparable<DNode>
	{
		public GameState gs; //game state
		public DNode p; //parent
		public int s; //score
		
		@Override
		public int compareTo(DNode b)
		{
			if(s > b.s) return -1;
    		if(s < b.s) return +1;
    		return 0;
		}
		
		public void print()
		{
			System.err.println("score: " + s);
			System.err.println(gs.toString());
		}
	}
	
    //Queue<GameState> queue = new LinkedList<GameState>();
    HashMap<String, DNode> visited = new HashMap<String, DNode>();
    Queue<DNode> queue = new PriorityQueue<DNode>();
    
    public int h0(GameState gs)
    {
    	int score = 0;
    	
    	//goals without boxes on
    	for(int iy=0; iy<gs.getRows(); iy++)
    	{
    		for(int ix=0; ix<gs.getColumns(); ix++)
    		{
    			char c = gs.getCharAt(iy, ix);
    			if(c == C.box)
    				score -= 10;
    		}
    	}
    	
    	return score;
    }
	
	public String play(GameState gs)
	{
		{
			DNode n = new DNode();
			n.gs = gs;
			n.p = null;
			n.s = h0(gs);
			queue.add(n);
		}
		
		int expanded = 0;
		
		while(queue.size() != 0)
		{
			DNode p = (DNode)queue.poll();
			expanded++;
			
			int limit = 1900;
			if(expanded >= limit) break;
			if(expanded >= limit - 100) p.print();
			
			if(p.gs.isWinning())
			{
				System.err.println("found solution");
				break;
			}
			
			//expanded all child states
			Collection<GameState> c = new LinkedList<GameState>();
			p.gs.getPossibleStates(c);
			
			for(GameState cgs : c)
			{
				if(visited.containsKey(cgs.toString()))
					continue;
					
				visited.put(cgs.toString(), null);
				
				DNode n = new DNode();
				n.gs = cgs;
				n.p = p;
				n.s = h0(cgs);
				queue.add(n);
			}
		}
		
		System.err.println("queue size: " + queue.size());
		/*
		while(queue.size() != 0)
		{
			DNode p = (DNode)queue.poll();
			System.err.println("score: " + p.s);
		}
		*/
		
		System.err.println("expanded nodes: " + expanded);
		return "";
	}
	
	public static void main(String args[])
	{
		DanielPlayer player = new DanielPlayer();
		MapsSLC maps = new MapsSLC();
		GameState gs = maps.Maps.get(0);
		System.err.println("SLC MAP #"+0);
		System.err.println("rows: " + gs.getRows() + " columns: " + gs.getColumns());
		System.err.println(gs.toString());
		long start = System.currentTimeMillis();
		player.play(gs);
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		System.err.println("elapsed time(ms): " + elapsed);
	}
}
