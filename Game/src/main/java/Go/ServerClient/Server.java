package Go.ServerClient;


// Java implementation of  Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import Go.GameMaker.TheGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Server class
public class Server
{
  private static int clientCounter = 1;
  public static void main(String[] args) throws IOException, InterruptedException {
    // server is listening on port 5056
    ServerSocket ss = new ServerSocket(5056);
    final TheGame gameServer = new TheGame();

    // running infinite loop for getting
    // client request
    while (true)
    {
      if(clientCounter <= 20 /*CHANGE TO 2 when done*/) {
        Socket s = null;

        try {
          // socket object to receive incoming client requests
          s = ss.accept();
          clientCounter++;
          System.out.println("A new client is connected : " + s);
          System.out.println("port: " + s.getPort());
          gameServer.addPlayer(s.getPort());
          // obtaining input and out streams
          DataInputStream dis = new DataInputStream(s.getInputStream());
          DataOutputStream dos = new DataOutputStream(s.getOutputStream());

          System.out.println("Assigning new thread for this client");

          // create a new thread object
          Thread t = new ClientHandler(s, dis, dos, gameServer);

          // Invoking the start() method
          t.start();


        } catch (Exception e) {
          s.close();
          e.printStackTrace();

        }
      } else {
        System.out.println("za duża ilość połączeń");
        Thread.sleep(Long.MAX_VALUE);
      }
    }
  }
}

// ClientHandler class
class ClientHandler extends Thread
{

  final DataInputStream dis;
  final DataOutputStream dos;
  final Socket s;
  final TheGame gameServer;


  // Constructor
  public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, TheGame gameServer)
  {
    this.s = s;
    this.dis = dis;
    this.dos = dos;
    this.gameServer = gameServer;
  }

  @Override
  public void run()
  {
    String received;
    String toReturn;
    try{
      dos.writeUTF("Your port: " + s.getPort());
    }
    catch (IOException ex){}
    while (true)
    {
      try {

        // receive the answer from client
        received = dis.readUTF();

        if (received.equals("Exit")) {
          System.out.println("Client " + this.s + " sends exit...");
          System.out.println("Closing this connection.");
          this.s.close();
          System.out.println("Connection closed");
          break;
        }
        else if(received.equals("playerIdMove")) {
          toReturn = gameServer.whoseMove();
        }
        else {
          //tutaj jeśli jest ruch gracza
          toReturn = gameServer.makeMove(received);
        }
        dos.writeUTF(toReturn);

      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Ktoś się rozłączył lub wysątpił błąd. Koniec rozgrywki");
        break;
      }
    }

    try
    {
      // closing resources
      this.dis.close();
      this.dos.close();

    }catch(IOException e){
      e.printStackTrace();
    }
  }
}

