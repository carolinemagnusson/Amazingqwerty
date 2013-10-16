import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class TestStateLoader
{
	public static State LoadState(int level)
	{
		try
		{
			DecimalFormat d = new DecimalFormat("000");
			String filename = "test_maps/test" + d.format(level) + ".in";
			File test_file = new File("test_maps/state_loader_test.txt");
			test_file.createNewFile();
			File file = new File(filename);
			Scanner scanner = new Scanner(file);
			int level_counter = 0;

			while(true)
			{
				if(!scanner.hasNextLine())
					break;

				String line;


				LinkedList<String> list = new LinkedList<String>();
				while(scanner.hasNextLine())
				{
					line = scanner.nextLine();
					if(!line.trim().equals(""))
							list.add(line);
				}

					char[][] m = new char[list.size()][];

					for(int iy=0; iy<list.size(); iy++)
					{
						String s = list.get(iy);
						//System.err.println(s);
						m[iy] = new char[s.length()];

						for(int ix=0; ix<s.length(); ix++)
						{
							m[iy][ix] = s.charAt(ix);
						}
					}

					return new State(m);
				}


	throw new Exception("level not found");
}
catch(Exception ex)
{
	System.err.println(ex);
}

return null;
}

public static void main(String[] args)
{
	DanielPlayer2 player;
	for(int i  = 0 ; i < 100 ; i++)
	{
		System.err.println("Map # " +i );
		State state = TestStateLoader.LoadState(i);
		state.Print();
		player = new DanielPlayer2();
		player.play(state);
		
	}


}
}

