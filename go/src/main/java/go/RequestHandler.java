package go;

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
			System.out.println("B³¹d I/O");
			return;
		}
		try {
			String line = br.readLine();
			pw.println(line);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
