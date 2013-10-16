package old;
import java.util.LinkedList;

import org.junit.Test;
public class StateClassTest {
	char[][] charstate1 = {{'#','#','#','#'},{'#', ' ', ' ',  '#'}, {'#', '@', '$',  '#'}, {'#', ' ', '.', ' ', '#'}, {'#', ' ', '.', '$',  '#'}, {'#', ' ', '.',  '#'}, {'#','#','#','#'}};

	char[][] charstate2 = {{'#','#','#','#'}, {'#', ' ', ' ',  '#'},{'#', '$', '$',  '#'}, {'#', ' ', '.',' ',  '#'}, {'#', ' ', '.',' ',  '#'}, {'#', '@', '.',  '#'},  {'#','#','#','#'}};;
	@Test
	public void test() {
		
		State state1 = new State(charstate1);
		State state2 = new State(charstate2);
		state1.setLeftUpperPosition();
		state2.setLeftUpperPosition();
		
		assert(!state1.equals(state2));
		
		state1.Print();
		state2.Print();
		
		LinkedList<State> possibleStates = new LinkedList<>();
		state1.PossibleAdvanced(possibleStates);
		state1 = possibleStates.get(0);
		state1.Print();
		state2.Print();
		
//		assert(state1.equals(state2));
		
		
		int level = 3;
		State state3 = MapsSLC.LoadMap(level);
		State state4;
		possibleStates.clear();
		state3.PossibleAdvanced(possibleStates);
		
		state4 = possibleStates.get(0);
		for (int i = 0; i < possibleStates.size(); i++) {
			possibleStates.get(i).Print();
			System.err.println(possibleStates.get(i).player.y);
			
		}
		state3.Print();
		System.err.println(state3.player.y);
//		state4.Print();

		assert(state3 == state4);
	}

}
