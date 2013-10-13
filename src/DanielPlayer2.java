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
    
    HashMap<State, N> reverseVisited = new HashMap<State, N>();
    Queue<N> reverseQueue = new PriorityQueue<N>();
    State startState;
    
    Stack<N> stack = new Stack<N>(); //for dfs testing
    
    private int h0(State s)
    {
    	int score = 0;
    	
//    	for(P goal : s.goals)
//    	{
//    		if(s.boxes.contains(goal))
//    			score += 10;
//    	}
		for(P goal : s.goals)
    	{
			for(P box : s.boxes)
			{
				score -= State.ManhattanDistance(goal, box);
			}
    	}
    	
    	return score;
    }
    
    private int ReverseH0(State s)
    {
    	int score = 0;
    	
//    	for(P box : s.boxes)
//    	{
//    		if(startState.boxes.contains(box))
//    			score += 10;
//    	}
    	
    	for(P state_box : s.boxes)
    	{
			for(P start_box : startState.boxes)
			{
				score -= startState.ManhattanDistance(state_box, start_box);
			}
    	}
    	
    	//ignore the player position
    	
    	return score;
    }
    
    private void BidirectionalAnswer(N forward, N backward)
    {
    	System.err.println("bidirectional answer");
    	N n;
    	
    	LinkedList<N> list = new LinkedList();
    	n = forward;
    	while(true)
    	{
    		if(n == null)
    			break;
    		
    		list.addFirst(n);
    		n = n.parent;
    	}
    	
    	n = backward;
    	while(true)
    	{
    		if(n == null)
    			break;
    		
    		list.addLast(n);
    		n = n.parent;
    	}
    	
    	for(N sequential : list)
    	{
    		sequential.print();
    	}
    }
	
	public void play(State startState)
	{
		//forward queue
		{
			//add start state
			this.startState = startState;
			N startNode = new N();
			startNode.state = startState;
			startNode.parent = null;
			startNode.score = h0(startNode.state);
			queue.add(startNode);
		}
		
		//backward queue
		{
			//add all possible end states
			Collection<State> endings = startState.GetAllPossibleEndings();
			
			for(State ending : endings)
			{
				//ending.Print();
				
				//dont add thoose that are equivalent to any node in the list already
				{
					if(reverseVisited.containsKey(ending))
						continue;
					
					reverseVisited.put(ending, null);
				}
				
				N endNode = new N();
				endNode.state = ending;
				endNode.parent = null;
				endNode.score = ReverseH0(endNode.state);
				reverseQueue.add(endNode);
				//stack.push(endNode);
			}
			
			reverseVisited.clear();
		}
		
		int expanded = 0;
		int limit = Integer.MAX_VALUE;
		
		while(true)
		{
			//forward search----------------------------------------------------------------------
			if(queue.size() == 0)
			{
				System.err.println("no forward solution found");
				break;
			}
		
			{
				N pn = (N)queue.poll(); //parent node
				expanded++;
				//pn.print();
				
//				if(expanded >= limit)
//				{
//					System.err.println("broke the limit. limit is " + limit);
//					break;
//				}
//				
//				if(expanded >= limit - 50)
//					pn.print();
					
				if(expanded % 10001 == 0)
					pn.print();
				
				if(pn.state.isWin())
				{
					System.err.println("found forward solution");
					break;
				}
				
				if(reverseVisited.containsKey(pn.state))
				{
					pn.print();
					((N)reverseVisited.get(pn.state)).print();
					BidirectionalAnswer(pn, reverseVisited.get(pn.state));
					System.err.println("found bidirection solution, from forward search");
					break;
				}
				
				//expanded all child states
				Collection<State> c = new LinkedList<State>(); //collection
				//if macro move do that otherwize backup one step and try possible advanced
				pn.state.PossibleAdvanced(c);
				
				for(State cs : c) //child node
				{
					if(visited.containsKey(cs))
						continue;
						
					N cn = new N();
					cn.state = cs;
					cn.parent = pn;
					cn.score = h0(cn.state);
					queue.add(cn);
					visited.put(cs, cn);
					//cn.print();
				}
			}
			
			//reverse search----------------------------------------------------------------------------------------------
			if(reverseQueue.size() == 0)
			{
				System.err.println("no reverse solution found");
				break;
			}
			
			{
				N pn = (N)reverseQueue.poll(); //parent node
				//N pn = (N)stack.pop();
				expanded++;
				//pn.print();
				
//				if(expanded >= limit)
//				{
//					System.err.println("broke the limit. limit is " + limit);
//					break;
//				}
					
//				if(expanded >= limit - 50)
//					pn.print();
					
				if(expanded % 10002 == 0)
					pn.print();
				
				if(pn.state.isWin(startState))
				{
					System.err.println("found reverse solution");
					break;
				}
				
				if(visited.containsKey(pn.state))
				{
					pn.print();
					((N)visited.get(pn.state)).print();
					BidirectionalAnswer(visited.get(pn.state), pn);
					System.err.println("found bidirection solution, from backward search");
					break;
				}
				
				//expanded all possible child states
				Collection<State> c = new LinkedList<State>(); //collection
				pn.state.reversePossibleAdvanced(c);
				
				for(State cs : c) //child node
				{
					if(reverseVisited.containsKey(cs))
						continue;
					
					N cn = new N();
					cn.state = cs;
					cn.parent = pn;
					cn.score = ReverseH0(cn.state);
					reverseVisited.put(cs, cn);
					reverseQueue.add(cn);
					//stack.push(cn);
					//cn.print();
				}
			}
		}
		
		System.err.println("queue size: " + queue.size());
		System.err.println("reverse queue size: " + reverseQueue.size());

		/*
		while(queue.size() != 0)
		{
			N p = (N)queue.poll();
			System.err.println("score: " + p.score);
		}
		*/
		
		System.err.println("expanded nodes: " + expanded);
	}
	
	private class GreedyDFSBoxNode
	{
		
	}
	
	private HashMap<State, GreedyDFSBoxNode> GreedyDFSBox_visited = new HashMap<State, GreedyDFSBoxNode>();
	private static P[] adjacent_lrud = new P[]{new P(-1, 0), new P(+1, 0), new P(0, -1), new P(0, +1)}; //left right up down
	private static P[] adjacent_lu = new P[]{new P(-1, 0), new P(0, -1)}; //left up
	
	private void GreedyDFSBox()
	{
		Queue<GreedyDFSBoxNode> GreedyDFSBox_queue = new PriorityQueue<GreedyDFSBoxNode>();	
		
		
	}
	
	public void GreedyDFSBoxSearch()
	{
		
	}
	
	public boolean reverse_macro(State state, P move_box, P start_box)
    {
    	//pull a box to it's starting position without moving other boxes
    	for(P adjacent : adjacent_lrud)
    	{
    		P move_box_next = new P(move_box.x + adjacent.x, move_box.y + adjacent.y);
    		P player_next = new P(move_box.x + adjacent.x * 2, move_box.y + adjacent.y * 2);
    		
    		if(state.boxes.contains(move_box_next))
    			return false;
    		if(state.walls.contains(move_box_next))
    			return false;
    		if(state.boxes.contains(player_next))
    			return false;
    		if(state.walls.contains(player_next))
    			return false;
		
			//todo search a path to the next player position
			
			
    		//pull boxes towards target position
    		state.pull(player_next, adjacent);
    	}
    	
    	return false;
    }
    
    public void reverse_macro(State state)
    {
    	//move as many boxes as possible directly to goal
    	for(P box : state.boxes)
    	{
    		//skip boxes that already have a goal
    		if(state.goals.contains(box))
    			continue;
    			
    		for(P goal : state.goals)
    		{
    			//skip goals that already have a box
	    		if(state.boxes.contains(goal))
	    			continue;
    			
    			reverse_macro(state, box, goal);
    		}
    	}
    }
	
	public static void main(String args[])
	{
		DanielPlayer2 player = new DanielPlayer2();
		int level = 1;
		State slc_state = MapsSLC.LoadMap(level);
		System.err.println("SLC MAP #"+level);
		System.err.println("rows: " + slc_state.rows + " columns: " + slc_state.columns);
		slc_state.Print();
		long start = System.currentTimeMillis();
		
//		Scanner input = new Scanner(System.in);
//		LinkedList<State> history = new LinkedList<State>();
//		history.push(slc_state);
//		State s = slc_state;
//		LinkedList<String> move_history = new LinkedList<String>();
//		
//		while(!s.isWin())
//		{
//			String string_input = input.next();
//			System.err.println(string_input);
//			
//			if(string_input.equals("z"))
//			{
//				System.err.println("undo");
//				s = history.pollLast();
//				move_history.pollLast();
//				if(history.size() == 0)
//					history.push(slc_state);
//			}
//			else if(string_input.equals("a"))
//			{
//				System.err.println("left");
//				State next_state = new State();
//				next_state.boxes.addAll(s.boxes);
//				next_state.Push(new P(s.player.x, s.player.y), adjacent_lrud[0]);
//				history.add(s);
//				s = next_state;
//				move_history.push("L");
//			}
//			else if(string_input.equals("d"))
//			{
//				System.err.println("right");
//				State next_state = new State();
//				next_state.boxes.addAll(s.boxes);
//				next_state.Push(new P(s.player.x, s.player.y), adjacent_lrud[1]);
//				history.add(s);
//				s = next_state;
//				move_history.push("R");
//			}
//			else if(string_input.equals("w"))
//			{
//				System.err.println("up");
//				State next_state = new State();
//				next_state.boxes.addAll(s.boxes);
//				next_state.Push(new P(s.player.x, s.player.y), adjacent_lrud[2]);
//				history.add(s);
//				s = next_state;
//				move_history.push("U");
//			}
//			else if(string_input.equals("s"))
//			{
//				System.err.println("down");
//				State next_state = new State();
//				next_state.boxes.addAll(s.boxes);
//				next_state.Push(new P(s.player.x, s.player.y), adjacent_lrud[3]);
//				history.add(s);
//				s = next_state;
//				move_history.push("D");
//			}
//			
//			s.Print();
//		}
//		
//		while(move_history.size() != 0)
//		{
//			System.err.print(move_history.pollFirst());
//		}
		player.play(slc_state);
		
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		System.err.println("elapsed time(ms): " + elapsed);
	}
}
