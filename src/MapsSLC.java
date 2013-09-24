import java.util.*;
import java.io.*;

public class MapsSLC
{
	public LinkedList<GameState> Maps;

	public MapsSLC()
	{
		try
		{
			Maps = new LinkedList<GameState>();
			File file = new File("all-slc/test_file.txt");
			file.createNewFile();
			File slcFile = new File("all-slc/all.slc");
			System.err.println(slcFile);
			Scanner scanner = new Scanner(slcFile);

			int ia = 0;
			LinkedList<String> list = new LinkedList<String>();

			while(true)
			{
				if(!scanner.hasNextLine())	break;

				String line = scanner.nextLine();
				//System.err.println(line);

				if(line.charAt(0) == ';')
				{
					//System.err.println(ia);
					if(list.size() == 0) continue;
					
					int maxLength = 0;
					for(String s: list)
					{
						if (s.length() > maxLength)
							maxLength = s.length();
					}
						
					
					char[][] data = new char[list.size()][maxLength]; //all maps are rectangular
					int ib = 0;
					for(String s : list)
					{
						for(int i = 0; i < s.length(); i++)
						{
							data[ib][i] = s.charAt(i);
						}
						ib++;
					}

					//finish map
					GameState map = new GameState(data);
					Maps.add(map);
					ia++;
					list.clear();
				}
				else
				{
					list.add(line);
				}
			}

			System.err.println("SLC Maps Read:" + ia);
		}
		catch(Exception ex)
		{
			System.err.println(ex);
		}
	}
}
