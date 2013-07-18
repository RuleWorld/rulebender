package rulebender.simulate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConsoleReader extends Thread {

	/** The input stream. */
	protected final InputStream inStream;

	/**
	 * Instantiates a new stream handler. The output is given to the SimSystem
	 * with the appropriate Level.
	 * 
	 * @param input
	 *          the input stream
	 * @param reportLevel
	 *          the report level
	 */
	public ConsoleReader(InputStream input) {
		inStream = input;
	}

	/**
	 * Starts the reader on the stream. In order to process the line written on
	 * the stream {@link #processLine(String)} is called.
	 */
	@Override
	public void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
		String input = "";
		try {
			while ((input = reader.readLine()) != null) {
				processLine(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the line which was written on the stream. In the default
	 * implementation it is just passed to the SimSystem.
	 * 
	 * @param line
	 *          the line
	 */
	protected void processLine(String line) {
		System.out.println(line);
	}
}