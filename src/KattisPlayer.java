import java.io.*;
import java.util.*;

public class KattisPlayer
{
	public static void main(String[] args) throws Exception
	{
		DanielPlayer2 player = new DanielPlayer2();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		LinkedList<String> list = new LinkedList<String>();
		String line;

		while(br.ready())
		{
			line = br.readLine();
			list.add(line);
		}

		char[][] input_data = new char[list.size()][];

		for(int row = 0; row<list.size(); row++)
		{
			input_data[row] = list.get(row).toCharArray();
		}

		State input_state = new State(input_data);
		player.play(input_state);
	}
}