import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Main {
	public static void main(String[] args) throws IOException {
		// TODO Add switch for debug purposes. Use standard error.
		Vector<String> board = new Vector<String>();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String line;
		while (br.ready()) {
			line = br.readLine();
			board.add(line);
		} // End while

		// Access
		// char = board.get(row).charAt(col);

		System.out.println("U R R U");
	}
}
