package Go;

import java.io.*;
import java.net.*;

public class Client {

  private Socket socket = null;
  private BufferedReader br = null;
  private PrintWriter pw = null;
  public String line = null;
  private String message = null;


  public void listenSocket() {
    try {
      socket = new Socket("localhost", 2137);
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      pw = new PrintWriter(socket.getOutputStream(), true);
      pw.println(message);

      line = br.readLine();
      //socket.close();

    } catch (UnknownHostException e) {
      System.out.println("Nieznany host: localhost");
      System.exit(1);
    } catch (IOException e) {
      System.out.println("Błąd I/O");
      System.exit(1);
    }

  }


  public void setMessage(String string) {
    message = string;

  }

}
