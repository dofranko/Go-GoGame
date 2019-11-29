package Go;

import java.io.*;
import java.net.*;

public class Server {

  private ServerSocket server = null;
  private Socket client = null;
 
  private RequestHandler threadList[] = new RequestHandler[2];
  private Socket clientList[] = new Socket[2];

  Server() {
    try {
      server = new ServerSocket(2137);

    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Brak połączenia na porcie 44444");
      System.exit(-1);
    }
  }

  public final void listenSocket() {
    int counter = 0;
    while (true) {
      try {
        System.out.println(counter);
        if (counter <= 2) {
          client = server.accept();
          clientList[counter] = client;
          threadList[counter] = new RequestHandler(client);
          threadList[counter].start();
          counter++;

        } else {
          System.out.println("Brak możliwości połączenia");
          System.exit(1);
        }
      } catch (IOException e) {
        System.out.println("Nieudane połączenie: 4444");
        System.exit(-1);

      }
    }

  }

  protected final void finalize() {
    try {
      // client.close();
      server.close();
    } catch (IOException e) {
      System.out.println("Nie udało się zamknąć");
      System.exit(-1);
    }
  }

}
