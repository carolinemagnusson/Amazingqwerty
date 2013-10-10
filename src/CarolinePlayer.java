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
    
    //TODO use Hungarian algorithm to get score. Uses distance to shortest path to goal it can go to
    private int score(State s)
    {
    	//TODO: add cost for moving each box, use manhattan distance
    	int score = 0;
    	
    	for(P box : s.boxes)
    	{
    		int tempMinDistance = State.ManhattanDistance(new P(0,0), new P(s.columns, s.rows));
    		if(!s.goals.contains(box)){
    			for (P goal: s.goals){
					int boxGoalDist= State.ManhattanDistance(box, goal);
					if(boxGoalDist < tempMinDistance){
						tempMinDistance = boxGoalDist;
					}
				}
    		}
    		score += tempMinDistance;
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
		
		
		//Make a test
//		Collection<State> c = new LinkedList<State>(); //collection
////		Collection<State> cn = new LinkedList<State>();
////		startState.PossibleAdvanced(c);
////		State notEqualState = startState;
//		Queue<State> qn = new LinkedList<State>();
//		qn.add(startState);
//		int counter = 0;
//		HashMap<State, State> hs = new HashMap<State, State>();
//		while(!qn.isEmpty()){
//			State child = qn.poll();
//			hs.put(child, child);
//			if(hs.containsKey(child)){
//				System.err.println("This:");
//				child.Print();
//				System.err.println("Is same as this:");
//				hs.get(child).Print();
//				System.err.println(counter);
//				
//			}
//			if(child.equals(startState)){
//				child.Print();
//				System.err.println("count:" + counter);
//			}
//			c.clear();
//			child.PossibleAdvanced(c);
//			qn.addAll(c);
//			counter++;
//			counter =+ c.size();
//		}
//		System.err.println("counter:" + counter);
		
		//End of test
		
		
		while(queue.size() != 0)
		{
			N pn = queue.poll(); //parent node
			if (queue.isEmpty()){
				pn.state.printUnsafePositions();
				System.err.println("QUEUE is empty!");
			}
			expanded++;
			
			int limit = 100000;
			if(expanded >= limit)
				break;
			if(expanded >= limit - 50)
				pn.print();
			
			
			//expanded all child states
			Collection<State> c = new LinkedList<State>(); //collection
			pn.state.PossibleAdvanced(c);
			for(State cs : c) //child node
			{
					
				if (!visited.contains(cs)) {

					// visitedNotHashed.put(cs.hashCode(), cs);
					visited.add(cs);
					cs.Print();
					if (cs.isWin()) {
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
				} else {
					// Just continue;
				}
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
		int level = 3;
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
		System.exit(0);
	}
}
