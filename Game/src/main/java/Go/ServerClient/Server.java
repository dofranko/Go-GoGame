package Go.ServerClient;


// Java implementation of  Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import Go.GameMaker.Markers;
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
    ServerSocket serverSocket = new ServerSocket(5056);
    final TheGame gameServer = TheGame.getInstance();
    gameServer.setBoard(19);

    // running infinite loop for getting
    // client request
    while (true) {
      //częśc kodu jeśli ograniczamy iloś graczy
      //if(clientCounter <= 200 /*CHANGE TO 2 when done*/) {
      Socket socket = null;

      try {
        // socket object to receive incoming client requests
        socket = serverSocket.accept();
        clientCounter++;
        String result = gameServer.addPlayer(socket.getPort()+"");
        String[] resultSet = result.split(";");
        Markers color = Markers.EMPTY;
        if(resultSet[0].equals("Succes")){
          if(resultSet[1].equals("White"))
            color = Markers.WHITE;
          else if(resultSet[1].equals("Black"))
            color = Markers.BLACK;
        }
        else
          System.out.println("jakiś błąd"); // tu będzie własny exception

        System.out.println("A new client is connected : " + socket);
        System.out.println("port: " + socket.getPort());
        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        System.out.println("Assigning new thread for client: " + socket.getPort());

        // create a new thread object
        Thread t = new ClientHandler(socket, dis, dos, color);

        // Invoking the start() method
        t.start();


      } catch (Exception e) {
        socket.close();
        e.printStackTrace();

      }
    } //else{ //częśc kodu jeśli ograniczamy iloś graczy
    //System.out.println("za duża ilość połączeń");
    //Thread.sleep(Long.MAX_VALUE);
    //}
    //}
  }
}

// ClientHandler class
class ClientHandler extends Thread
{

  final DataInputStream dis;
  final DataOutputStream dos;
  final Socket s;
  final TheGame gameServer;
  private Markers color;


  // Constructor
  public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Markers color)
  {
    this.s = s;
    this.dis = dis;
    this.dos = dos;
    this.gameServer = TheGame.getInstance();
    this.color = color;
  }

  @Override
  public void run()
  {
    String received;
    String toReturn;
    try{
      //port jest IdGracza
      dos.writeUTF(s.getPort() + "");
      dos.writeUTF(color.asChar()+"");
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
        else if(received.equals("findGame")){
          toReturn = gameServer.addPlayer(s.getPort() + "");
        }
        else {
          //tutaj jeśli jest ruch gracza
          toReturn = gameServer.makeMove(s.getPort() + "," + received);
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

