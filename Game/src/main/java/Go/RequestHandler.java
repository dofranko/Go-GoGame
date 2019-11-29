package Go;

import java.io.*;
import java.net.Socket;

public class RequestHandler extends Thread {

	private Socket client;
	private BufferedReader br;
	private PrintWriter pw;

	public RequestHandler(final Socket client) {
		this.client = client;
	}

	public final void run() {

		try {
			br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			pw = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Błąd I/O");
			return;
		}
		try {
			String line = br.readLine();
			if (line.equals("quit")) {
				System.out.println("To ja spadam");
				System.exit(0);
			} else
				pw.println(line);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
