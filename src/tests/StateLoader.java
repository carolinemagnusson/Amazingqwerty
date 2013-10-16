package tests;
import java.util.*;
import java.io.*;

public class StateLoader
{
	public static State LoadState(int level)
	{
		try
		{
			File test_file = new File("state_loader_test.txt");
			test_file.createNewFile();
			File file = new File("path_to_solution.txt");
			System.err.println(file);
			Scanner scanner = new Scanner(file);
			int level_counter = 0;

			while(true)
			{
				if(!scanner.hasNextLine())
					break;

				String line = scanner.nextLine();

				if(line.length() > 1 && line.charAt(0) == ';')
				{
					level_counter++;

					if(level_counter == level)
					{
						LinkedList<String> list = new LinkedList<String>();

						while(true)
						{
							line = scanner.nextLine();
							//System.err.println(line);

							//assumes that well meet a semicolon sooner or later
							if(line.charAt(0) == ';')
							{
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
							else
							{
								list.add(line);
							}
						}
					}
				}
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
		State state = StateLoader.LoadState(121);
		state.Print();
	}
}

