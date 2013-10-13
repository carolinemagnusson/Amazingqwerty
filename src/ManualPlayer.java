import java.util.*;

public class ManualPlayer
{
	public static void main(String[] args)
	{
		//RUURRULLLDLURULL
		State slc_state1 = MapsSLC.LoadMap(1);
		long start = System.currentTimeMillis();

		LinkedList<State> history = new LinkedList<State>();
		history.push(slc_state1);
		State s = slc_state1;
		LinkedList<String> move_history = new LinkedList<String>();
		s.Print();
		Scanner input = new Scanner(System.in);

		while(!s.isWin())
		{
			String string_input = input.next();

			if(string_input.equals("z") || string_input.equals("Z"))
			{
				System.err.println("undo");
				s = history.pollLast();
				move_history.pollLast();
				if(history.size() == 0)
					history.push(slc_state1);
			}
			else if(string_input.equals("a") || string_input.equals("L"))
			{
				System.err.println("left");
				State next_state = s.copyState();
				next_state.Push(new P(s.player.x, s.player.y), State.adjacent_lrud[0]);
				history.add(s);
				s = next_state;
				move_history.push("L");
			}
			else if(string_input.equals("d") || string_input.equals("R"))
			{
				System.err.println("right");
				State next_state = s.copyState();
				next_state.Push(new P(s.player.x, s.player.y), State.adjacent_lrud[1]);
				history.add(s);
				s = next_state;
				move_history.push("R");
			}
			else if(string_input.equals("w") || string_input.equals("U"))
			{
				System.err.println("up");
				State next_state = s.copyState();
				next_state.Push(new P(s.player.x, s.player.y), State.adjacent_lrud[2]);
				history.add(s);
				s = next_state;
				move_history.push("U");
			}
			else if(string_input.equals("s") || string_input.equals("D"))
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
		
		while(move_history.size() != 0)
		{
			System.err.print(move_history.pollFirst());
		}
		System.err.println();
		
		long end = System.currentTimeMillis();
		long elapsed = end - start;
		System.err.println("elapsed time(ms): " + elapsed);
	}
}