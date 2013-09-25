import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Main {
	public static void main(String[] args) throws IOException {
		// TODO Add switch for debug purposes. Use standard error.
//		Vector<String> board = new Vector<String>();
//
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//		String line;
//		while (br.ready()) {
//			line = br.readLine();
//			board.add(line);
//		} // End while
//		GameState firstState = new GameState(board);
//		
		AbstractPlayer player = new Player();
//		player.play(state)
		MapsSLC maps = new MapsSLC();
		GameState gs;
		for (int i = 0;i<1; i++)
		{
			gs = maps.Maps.poll();
			for(int j = 0; j < gs.getRows(); j++)
			{
				for (int k = 0; k < gs.getColumns(); k++)
				{
					System.err.print(gs.getCharAt(j, k));
				}
				System.err.println();
			}
			player.play(gs);
		}
		//System.out.println(player.play(firstState));
		// Access
		// char = board.get(row).charAt(col);

//		System.out.println("U R R U");
	}
}
