import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

public class CarolinePlayer
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
	
	HashSet<State> visited = new HashSet<State>();
	Map<Integer, State> visitedNotHashed = new TreeMap<Integer, State>();
    Queue<N> queue = new PriorityQueue<N>();
    
    private int score(State s)
    {
    	int score = 0;
    	
    	for(P box : s.boxes)
    	{
    		if(!s.goals.contains(box)){
    			for (P goal: s.goals){
					score += State.ManhattanDistance(box, goal);
				}
    		}   			
    	}
    	//TODO add minus score for box on goal in corner
    	return score;
    }
	
	public void play(State startState)
	{
		{
			N startNode = new N();
			startNode.state = startState;
			startNode.parent = null;
			startNode.score = score(startNode.state);
			queue.add(startNode);
		}
		
		int expanded = 0;
		
		while(queue.size() != 0)
		{
			N pn = queue.poll(); //parent node
			if (queue.isEmpty()){
				pn.state.printUnsafePositions();
			}
			expanded++;
			
			int limit = 10000;
			if(expanded >= limit)
				break;
			if(expanded >= limit - 50)
				pn.print();
			
			
			//expanded all child states
			Collection<State> c = new LinkedList<State>(); //collection
			pn.state.PossibleAdvanced(c);
			for(State cs : c) //child node
			{
				{
					
					if(visited.contains(cs)){
//						System.err.println("Hashed map already found:");
//						visitedNotHashed.get(cs.hashCode()).Print();
//						System.err.println("New state thought to be the same:");
//						cs.Print();

						continue;
					}
//					visitedNotHashed.put(cs.hashCode(), cs);
					visited.add(cs);
				}
				cs.Print();
				if(cs.isWin())
				{
					System.err.println("found solution");
					String path = buildPath(pn);
					System.out.println(path);
					return;
				}
				N cn = new N();
				cn.state = cs;
				cn.parent = pn;
				cn.score = score(cn.state);
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
	//TODO finish this when I can think
	String buildPath(N endNode){
		return "";
		
	}
//	private String buildBetween(N next, N parent){
//		return BuildBetween() + State.getPath(next.state, parent.state);
//	}
	
	public static void main(String args[])
	{
		CarolinePlayer player = new CarolinePlayer();
		int level = 2;
		State s = MapsSLC.LoadMap(level);
		s.printUnsafePositions();
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
