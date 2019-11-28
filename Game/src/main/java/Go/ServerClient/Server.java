package Go.ServerClient;


// Java implementation of  Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Server class
public class Server
{
  private static int clientCounter = 1;
  public static void main(String[] args) throws IOException
  {
    // server is listening on port 5056
    ServerSocket ss = new ServerSocket(5056);

    // running infinite loop for getting
    // client request
    while (true)
    {
      if(clientCounter <= 2) {
        Socket s = null;

        try {
          // socket object to receive incoming client requests
          s = ss.accept();
          clientCounter++;
          System.out.println("A new client is connected : " + s);

          // obtaining input and out streams
          DataInputStream dis = new DataInputStream(s.getInputStream());
          DataOutputStream dos = new DataOutputStream(s.getOutputStream());

          System.out.println("Assigning new thread for this client");

          // create a new thread object
          Thread t = new ClientHandler(s, dis, dos);

          // Invoking the start() method
          t.start();


        } catch (Exception e) {
          s.close();
          e.printStackTrace();
        }
      } else
        System.out.println("za duża ilość połączeń");
    }
  }
}

// ClientHandler class
class ClientHandler extends Thread
{

  final DataInputStream dis;
  final DataOutputStream dos;
  final Socket s;


  // Constructor
  public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)
  {
    this.s = s;
    this.dis = dis;
    this.dos = dos;
  }

  @Override
  public void run()
  {
    String received;
    String toreturn;
    while (true)
    {
      try {

        // Ask user what he wants
        dos.writeUTF("What do you want?[Date | Time]..\n" +
                "Type Exit to terminate connection.");

        // receive the answer from client
        received = dis.readUTF();

        if (received.equals("Exit")) {
          System.out.println("Client " + this.s + " sends exit...");
          System.out.println("Closing this connection.");
          this.s.close();
          System.out.println("Connection closed");
          break;
        }


        // write on output stream based on the
        // answer from the
        /*
        * TUTAJ TRZEBA PRZEKAZAC DANE DO KLASY SPRAWDZAJACEJ
        *
        * */
        toreturn = "wypisuję zwrot";
        dos.writeUTF(toreturn);


      } catch (IOException e) {
        e.printStackTrace();
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

