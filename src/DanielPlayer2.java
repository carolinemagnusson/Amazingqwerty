import java.util.*;

public class DanielPlayer2
{
	private class N implements Comparable<N>
	{
		State state;
		N parent;
		int score;
		
		@Override
		public int compareTo(N b)
		{
			if(score > b.score) return -1;
    		if(score < b.score) return +1;
    		return 0;
		}
		
		public void print()
		{
			System.err.println("score: " + score);
			state.Print();
		}
	}
	
	HashMap<State, N> visited = new HashMap<State, N>();
    Queue<N> queue = new PriorityQueue<N>();
    
    private int h0(State s)
    {
    	int score = 0;
    	
    	for(P goal : s.goals)
    	{
    		if(!s.boxes.contains(goal))
    			score -= 10;
    	}
    	
    	return score;
    }
	
	public void play(State startState)
	{
		{
			N startNode = new N();
			startNode.state = startState;
			startNode.parent = null;
			startNode.score = h0(startNode.state);
			queue.add(startNode);
		}
		
		int expanded = 0;
		
		while(queue.size() != 0)
		{
			N pn = (N)queue.poll(); //parent node
			expanded++;
			
			int limit = 10000;
			if(expanded >= limit)
				break;
			if(expanded >= limit - 50)
				pn.print();
			
			if(pn.state.isWin())
			{
				System.err.println("found solution");
				break;
			}
			
			//expanded all child states
			Collection<State> c = new LinkedList<State>(); //collection
			pn.state.PossibleAdvanced(c);
			
			for(State cs : c) //child node
			{
				{
					if(visited.containsKey(cs))
						continue;
					
					visited.put(cs, null);
				}
				//cs.Print();
				
				N cn = new N();
				cn.state = cs;
				cn.parent = pn;
				cn.score = h0(cn.state);
				queue.add(cn);
			}
		}
		
		System.err.println("queue size: " + queue.size());

		/*
		while(queue.size() != 0)
		{
			N p = (N)queue.poll();
			System.err.println("score: " + p.score);
		}
		*/
		
		System.err.println("expanded nodes: " + expanded);
	}
	
	public static void main(String args[])
	{
		DanielPlayer2 player = new DanielPlayer2();
		int level = 1;
		State s = MapsSLC.LoadMap(level);
		System.err.println("SLC MAP #"+level);
		System.err.println("rows: " + s.rows + " columns: " + s.columns);
		s.Print();
		long start = System.currentTimeMillis();
		player.play(s);
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		System.err.println("elapsed time(ms): " + elapsed);
	}
}
