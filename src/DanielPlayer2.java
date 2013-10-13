import java.util.*;

class VectorNode implements Comparable<VectorNode>
{
	int distance;
	P goal;

	@Override
	public int compareTo(VectorNode vectorNode)
	{
		if(distance < vectorNode.distance) return -1;
		if(distance > vectorNode.distance) return +1;
		return 0;
	}
}

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
			if(score < b.score) return -1;
    		if(score > b.score) return +1;
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
    boolean vectorMapConstruction = true;

    Stack<N> stack = new Stack<N>(); //for dfs testing

    HashMap<P, Queue<VectorNode>> vectorMap = new HashMap<P, Queue<VectorNode>>();

    private int h0(State s)
    {
    	int score = 0;
    	if(vectorMapConstruction)
    	{
    		for(P goal : s.goals)
    		{
				for(P box : s.boxes)
				{
					score += State.ManhattanDistance(goal, box);
				}
    		}
    		return score;
    	}
////    	for(P goal : s.goals)
////    	{
////    		if(s.boxes.contains(goal))
////    			score += 10;
////    	}

		for(P box : s.boxes){
			Queue<VectorNode> q = vectorMap.get(box);
			if(q.isEmpty()){//Deadlock check of static deadlocks
				score += Integer.MAX_VALUE;
			}
			score += q.peek().distance;
			//TODO If we have time and energy: check that the heuristic is not for distance to goal with box on it.
//			for(VectorNode node : q){
//				if(node.goal.)
//			}
		}

    	return score;
    }
    //TODO Copy paste the hashmap to make a reverse search between init state of boxes and goals.
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
				score += startState.ManhattanDistance(state_box, start_box);
			}
    	}

    	//ignore the player position

    	return score;
    }

    private void BidirectionalAnswer(N forward, N backward, StringBuilder answer)
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

    	N from = null;

    	for(N sequential : list)
    	{
    		if(from != null)
    		{
    			String pathstring = State.getPath2(from.state, sequential.state);
    			System.err.println(pathstring);
				answer.append(pathstring);
    		}

    		from = sequential;
    		sequential.print();
    	}
    }

    public String SearchSolution(State startState)
    {
    	StringBuilder answer = new StringBuilder();

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
				//ending.Print();
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

				if(expanded >= limit)
				{
					System.err.println("broke the limit. limit is " + limit);
					break;
				}

//				if(expanded >= limit - 50)
//					pn.print();
//
//				if(expanded % 10001 == 0)
//					pn.print();

				if(pn.state.isWin())
				{
					System.err.println("found forward solution");
			    	N n;

			    	LinkedList<N> list = new LinkedList();
			    	n = pn;
			    	while(true)
			    	{
			    		if(n == null)
			    			break;

			    		list.addFirst(n);
			    		n = n.parent;
			    	}

			    	N from = null;

			    	for(N sequential : list)
			    	{
			    		if(from != null)
			    		{
							answer.append(State.getPath2(from.state, sequential.state));
			    		}

			    		from = sequential;
			    		sequential.print();
			    	}
					break;
				}

				if(reverseVisited.containsKey(pn.state))
				{
					//pn.print();
					//((N)reverseVisited.get(pn.state)).print();
					BidirectionalAnswer(pn, reverseVisited.get(pn.state), answer);
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

//				if(expanded % 10002 == 0)
//					pn.print();

				if(pn.state.isWin(startState))
				{
					System.err.println("found reverse solution");
					//pn.print();
					//startState.Print();

					LinkedList<N> list = new LinkedList();
					N n;
			    	n = pn;
			    	while(true)
			    	{
			    		if(n == null)
			    			break;

			    		list.addLast(n);
			    		n = n.parent;
			    	}

			    	N from = null;

			    	for(N sequential : list)
			    	{
			    		if(from != null)
			    		{
							answer.append(State.getPath2(from.state, sequential.state));
			    		}

			    		from = sequential;
			    		//sequential.print();
			    	}
			    	break;
				}

				if(visited.containsKey(pn.state))
				{
					//pn.print();
					//((N)visited.get(pn.state)).print();
					BidirectionalAnswer(visited.get(pn.state), pn, answer);
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
		System.err.println("expanded nodes: " + expanded);

		/*
		while(queue.size() != 0)
		{
			N p = (N)queue.poll();
			System.err.println("score: " + p.score);
		}
		*/

		if(queue.size() == 0 && reverseQueue.size() == 0)
		{
			return null; //no solution found
		}

		return answer.toString();
    }

	public void play(State startState)
	{

//		State empty = startState.copyState();
//		empty.boxes.clear();
//		empty.goals.clear();
//		LinkedList<P> reachables = new LinkedList<P>();
//		HashSet<P> bfvisited = new HashSet<P>();
//		Queue<P> queuebf = new LinkedList<P>();
//		queuebf.add(empty.player);
//
//		while(!queuebf.isEmpty())
//		{
//			P playerCurrent = queuebf.poll();
//			bfvisited.add(playerCurrent);
//
//			for(P direction : adjacent_lrud)
//			{
//				P playerNext = new P(playerCurrent.x + direction.x, playerCurrent.y + direction.y);
//
//				if(empty.walls.contains(playerNext))
//					continue;
//				if(bfvisited.contains(playerNext))
//					continue;
//
//				reachables.add(playerNext);
//				queuebf.add(playerNext);
//			}
//		}
//
//		for(P box : reachables)
//		{
//			Queue thisBoxQueue = new PriorityQueue<VectorNode>();
//
//			for(P goal : startState.goals)
//			{
//				//solve simplified sub problem
//				State subState = empty.copyState();
//				subState.boxes.add(box);
//				subState.goals.add(goal);
//
//				String answer = SearchSolution(subState);
//
//				if(answer == null)
//					continue;
//
//				Queue<VectorNode> queuebfs = new PriorityQueue<VectorNode>();
//				bfvisited.clear();
//				VectorNode goalNode = null;
//				VectorNode node;
//				VectorNode firstNode = new VectorNode();
//				firstNode.distance = State.ManhattanDistance(box, goal);
//				firstNode.goal = box;
//				queuebfs.add(firstNode);
//
//				while(!queuebfs.isEmpty())
//				{
//					//OBS! vectornode goal is where you are currently
//					VectorNode next = queuebfs.poll();
//					bfvisited.add(next.goal);
//
//					if(next.goal.equals(goal))
//					{
//						goalNode = next;
//					}
//
//					for(P direction : adjacent_lrud)
//					{
//						P nextPosition = new P(next.goal.x + direction.x, next.goal.y + direction.y);
//
//						if(empty.walls.contains(nextPosition))
//							continue;
//						if(bfvisited.contains(nextPosition))
//							continue;
//
//						VectorNode child = new VectorNode();
//						child.distance = State.ManhattanDistance(nextPosition, goal);
//						child.goal = nextPosition;
//						queuebfs.add(child);
//					}
//				}
//
//				if(goalNode != null)
//					thisBoxQueue.add(goalNode);
//			}
//
//			vectorMap.put(box, thisBoxQueue);
//		}


		System.err.println(SearchSolution(startState));
		System.err.println("State.hashCollissionCounter: " + State.hashCollissionCounter);
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

		State slc_state1 = MapsSLC.LoadMap(1);
		State slc_state2 = MapsSLC.LoadMap(2);

		long start = System.currentTimeMillis();
		player.play(slc_state1);
		//System.err.println(State.getPath2(slc_state1, slc_state2));
//
//		System.err.println("SLC MAP #"+level);
//		System.err.println("rows: " + slc_state.rows + " columns: " + slc_state.columns);
//		slc_state.Print();
//
//
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
//				State next_state =
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
		//player.play(slc_state);

		long end = System.currentTimeMillis();
		long elapsed = end - start;
		System.err.println("elapsed time(ms): " + elapsed);
	}
}
