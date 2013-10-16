package tests;
import java.util.*;

public class Replayer
{
	public static void main(String[] args)
	{
		String replay_string = "RUURRULLLDLURULL"; //must match the correct test map
		State slc_state1 = MapsSLC.LoadMap(1);
		
		long start = System.currentTimeMillis();
		
		LinkedList<State> history = new LinkedList<State>();
		history.push(slc_state1);
		State s = slc_state1;
		LinkedList<String> move_history = new LinkedList<String>();
		s.Print();

		for(int ia=0; ia<replay_string.length(); ia++)
		{
			String string_input = replay_string.charAt(ia) + "";

			if(string_input.equals("Z"))
			{
				System.err.println("undo");
				s = history.pollLast();
				move_history.pollLast();
				if(history.size() == 0)
					history.push(slc_state1);
			}
			else if(string_input.equals("L"))
			{
				System.err.println("left");
				State next_state = s.copyState();
				next_state.Push(new P(s.player.x, s.player.y), State.adjacent_lrud[0]);
				history.add(s);
				s = next_state;
				move_history.push("L");
			}
			else if(string_input.equals("R"))
			{
				System.err.println("right");
				State next_state = s.copyState();
				next_state.Push(new P(s.player.x, s.player.y), State.adjacent_lrud[1]);
				history.add(s);
				s = next_state;
				move_history.push("R");
			}
			else if(string_input.equals("U"))
			{
				System.err.println("up");
				State next_state = s.copyState();
				next_state.Push(new P(s.player.x, s.player.y), State.adjacent_lrud[2]);
				history.add(s);
				s = next_state;
				move_history.push("U");
			}
			else if(string_input.equals("D"))
			{
				System.err.println("down");
				State next_state = s.copyState();
				next_state.Push(new P(s.player.x, s.player.y), State.adjacent_lrud[3]);
				history.add(s);
				s = next_state;
				move_history.push("D");
			}

			s.Print();
		}
		
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		System.err.println("elapsed time(ms): " + elapsed);
	}
}