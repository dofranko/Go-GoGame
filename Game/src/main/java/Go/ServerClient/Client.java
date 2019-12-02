package Go.ServerClient;

// Java implementation for a client
// Save file as Client.java

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

// Client class
public abstract class Client
{
  DataInputStream dis;
  DataOutputStream dos;
  private Socket socket;
  protected String received = "";
  private final String myPlayerId;
  private boolean isItMyTurn = true;
  private String color;
  //private Thread waitForMove = createWaitingForTurnThread();

  public Client(){
    String playerIdToSet="";
    try {
      // Ip lokalne hosta
      InetAddress ip = InetAddress.getByName("localhost");
      /*InetAddress inetAddress = InetAddress.getLocalHost();
      inetAddress.getHostAddress());
      inetAddress.getHostName()); */ //crossdevice
      // połączenie się na porcie: 8523
      socket = new Socket(ip, 8523);

      // pobranie DataInputStream i DataOutputSteam do komunikacji z serwerem(socketem)
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());


      //odczytanie swojego id gracza ( = port socketa)
      received = dis.readUTF();
      System.out.println("Moje id: " + received);
      playerIdToSet = received;

      //odczytanie koloru gracza
      received = dis.readUTF();
      System.out.println("Mój color: " + received);
      color = received;
      //sendAndReceiveInformation("WhoseMove");

    } catch (Exception e) {
      e.printStackTrace();
    }
    //nie mozna przypisać w try catch
      myPlayerId = playerIdToSet;

  }
    public String getMyPlayerId(){
      return myPlayerId;
    }

   /* public void setReceived(String received){
      this.received = received;
    }*/
    public String getReceived(){
      return received;
    }

    public boolean getIsItMyTurn() {
      return isItMyTurn;
    }

    public void sendAndReceiveInformation(String toSend) {
      try {
        //waitForMove.start();
        //Client jeszcze sprawdza, co chce GUI wysłać i czy musi podjąć jakieś działania
        if (toSend.equals("Exit")) {
          dos.writeUTF(toSend);
          disconnect();
          System.out.println("Connection closed");
        }
        //możliwe że do wyrzucenia
        else if(toSend.equals("WhoseMove")){
          dos.writeUTF(toSend);
          received = dis.readUTF();
          System.out.println(received);
          String color = received.split(";")[0];
          if(color == this.color){
            isItMyTurn = true;
          }
        }
        //wykouje ruch jeśli to jego tura
        //true dla testów ! TODO
        else if(isItMyTurn) {
          dos.writeUTF(toSend);
          received = dis.readUTF();
          System.out.println(received);
          //warunki rozdzieli się potem na labele
          if(!received.equals("NotYrMove") && !received.equals("-1")){
            isItMyTurn = false;
            updateGameBoard(received.substring(2));
            Thread waitForMove = createWaitingForTurnThread();
            waitForMove.start();
          }
          else if(received.equals("NotYrMove")) {
            this.isItMyTurn = false;
            System.out.println(received);
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    public void disconnect(){
      try {
        socket.close();
        dis.close();
        dos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    public abstract void updateGameBoard(String stonesInString);
    public String getColor(){
      return this.color;
    }

    protected int[][] convertStonesToIntFromString(String stonesInString){
      int[][] stones;
      String[] columns = stonesInString.split(";");
      stones = new int[columns.length][columns.length];

      int i=0;
      int j=0;
      for(String column : columns){
        String[] fields = column.split(",");
        for(String field : fields){
          stones[i][j] = Integer.parseInt(field);
          j++;
        }
        j=0;
        i++;
      }
      return stones;
    }
    private Thread createWaitingForTurnThread(){
     return new Thread(){
        public String canIMove(){
          try {
            dos.writeUTF("WhoseMove");
            return received = dis.readUTF();
          } catch (IOException e) {
            e.printStackTrace();
            return "KILL!";
          }
        }
        public void run(){
          String whoseMove = canIMove();
          String colorMove = whoseMove.split(";")[0];
          while(!colorMove.equals(color) ){
            String fields="";
            System.out.println(whoseMove);
            try {
              sleep(500);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            whoseMove = canIMove();
            colorMove = whoseMove.split(";")[0];
            if(whoseMove.equals("KILL!"))
              break;
            System.out.println(Thread.activeCount());
          }
          isItMyTurn = true;
          updateGameBoard(whoseMove.substring(colorMove.length()+1));
        }
      };
    }

}
