package Go.ServerClient;

// Java implementation for a client
// Save file as Client.java

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

// Client class
public abstract class Client
{
  DataInputStream dis;
  DataOutputStream dos;
  Socket s;
  private String received = "";
  private final String myPlayerId;
  private boolean isItMyTurn = false;


  public Client(){
    String playerIdToSet="";
    try {
      // getting localhost ip
      InetAddress ip = InetAddress.getByName("localhost");
      // establish the connection with server port 5056
      s = new Socket(ip, 5056);

      // obtaining input and out streams
      dis = new DataInputStream(s.getInputStream());
      dos = new DataOutputStream(s.getOutputStream());

      // the following loop performs the exchange of
      // information between client and client handler
      //odczytanie swojego id gracza
      received = dis.readUTF();
      System.out.println("Moje id: " + received);
      playerIdToSet = received;

      // closing resources
    } catch (Exception e) {
      e.printStackTrace();
    }
    //nie mozna przypisać w try catch
      myPlayerId = playerIdToSet;

  }
    public String getMyPlayerId(){
      return myPlayerId;
    }
    public void setReceived(String received){
      this.received = received;
    }
    public String getReceived(){
      return received;
    }

    public void sendAndReceiveInformation(String toSend) {
      try {

        if (toSend.equals("Exit")) {
          dos.writeUTF(toSend);
          s.close();
          System.out.println("Connection closed");
          endGame();
        }
        else if(toSend.equals("whoseMove")){
          dos.writeUTF(toSend);
          received = dis.readUTF();
          System.out.println(received);
          if(received == myPlayerId){
            isItMyTurn = true;
          }
        }
        //wykouje ruch jeśli to jego tura
        //true dla testów ! TODO
        else if(isItMyTurn || true) {
          dos.writeUTF(toSend);
          received = dis.readUTF();
          System.out.println(received);
          if(received.substring(0,1) == "1" || true){
            isItMyTurn = false;
            updateGameBoard(received.substring(2));
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    public void endGame(){
      try {
        dis.close();
        dos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    public abstract void updateGameBoard(String stonesInString);

}
