import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Main {
	public static void main(String[] args) throws IOException {
		
		//Call debugMode or kattisMode depending on if you want to send to kattis or test yourself
		
		debugMode();
//		kattisMode();
		
	}
	static void kattisMode() throws IOException{
		Vector<String> board = new Vector<String>();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String line;
		while (br.ready()) {
			line = br.readLine();
			board.add(line);
			
		} // End while
		
		GameState firstState = new GameState(board);
		
		AbstractPlayer player = new Player();
		System.out.println(player.play(firstState));
	}
	
	static void debugMode(){
		AbstractPlayer player = new Player();
		
		MapsSLC maps = new MapsSLC();
		GameState gs;
		
		for(int i=0; i<1; i++)
		{
			gs = maps.Maps.poll();
			System.err.println("SLC MAP #"+i);
			System.err.println("rows: " + gs.getRows() + " columns: " + gs.getColumns());
			System.err.println(gs.toString());
			String path = player.play(gs);
			
			if(path.equals(""))
			{
				System.out.println("No path");
			}
			else
			{
				System.out.println(path);
			}
		}
	}
}
