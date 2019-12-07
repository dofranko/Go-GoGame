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
  private DataInputStream dis;
  private DataOutputStream dos;
  private Socket socket;
  protected String received = "";
  private final String myPlayerId;
  private boolean isItMyTurn = false;
  private String color;
  private String myPoints = "0" ;
  private boolean didIPass = false;
  Thread waitingForTurnThread = createWaitingForTurnThread();

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
      if(color.equals("Black"))
        isItMyTurn = true;
      //sendAndReceiveInformation("WhoseMove");
      else if(color.equals("Error"))
        disconnect();

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

    public void sendExit(){
      try {
        dos.writeUTF("Exit");
      } catch (IOException e) {
        e.printStackTrace();
      }
      disconnect();
      System.out.println("Connection closed");
    }

    public void sendWhoseMove() throws IOException {
      dos.writeUTF("WhoseMove");
      received = dis.readUTF();
      System.out.println(received);
      String color = received.split(";")[0];
      if(color.equals(this.color) || color.equals("EnemyWantsToContinue")){
        isItMyTurn = true;
      }
      if(color.equals("EnemyWantsToConitnue")){
        updateStatusLabel("EnemyWantsToContinue");
      }
      if(color.equals("EnemyWantsToPass")){
        updateStatusLabel("EnemyWantsToPass");
      }

    }

    public void sendMakeMove(String move){
      if(isItMyTurn) {
        try {
          dos.writeUTF("MakeMove");
          dos.writeUTF(move);
          received = dis.readUTF();
        }
        catch (IOException e){e.printStackTrace();}

        System.out.println(received);
        //warunki rozdzieli się potem na labele
        if(!received.equals("NotYrMove") && !received.equals("IllegalMove")){
          isItMyTurn = false;
          updateStatusLabel("MoveMade");
          //pierwsze kilka znakó to punkty gracza
          this.myPoints = received.split(";")[0];
          updatePointsLabel();
          String arrayOfStonesToUpdate = received.substring(myPoints.length()+1);
          updateGameBoard(arrayOfStonesToUpdate);
          //stworzenie i urchomienie wątku czekającego na turę gracza
          waitingForTurnThread = createWaitingForTurnThread();
          startWaitingThread();
        }
        else if(received.equals("NotYrMove")) {
          this.isItMyTurn = false;
          updateStatusLabel("NotYrMove");
          System.out.println(received);
        }
        else if(received.equals("IllegalMove"))
          updateStatusLabel("IllegalMove");
      }
      else
        updateStatusLabel("NotYrMove");
    }

    public void sendPass(){
      didIPass = true;
      try {
        dos.writeUTF("Pass");
      }
      catch (IOException e){ e.printStackTrace(); }
      startWaitingThread();
    }

    public void sendGiveUp(){
      try {
        dos.writeUTF("GiveUp");
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

    protected void startWaitingThread(){
      if(!waitingForTurnThread.isAlive()) {
        waitingForTurnThread = createWaitingForTurnThread();
        waitingForTurnThread.start();
      }
    }

    protected int[][] convertStonesToIntFromString(String stonesInString) {
      int[][] stones;
      String[] columns = stonesInString.split(";");
      stones = new int[columns.length][columns.length];
        int i = 0;
        int j = 0;
        for (String column : columns) {
          String[] fields = column.split(",");
          for (String field : fields) {
            stones[i][j] = Integer.parseInt(field);
            j++;
          }
          j = 0;
          i++;
        }
        return stones;
        }
    
    private Thread createWaitingForTurnThread(){
     return new Thread(){
        public void run(){
          String colorMove = getColor();
          String whoseMove = "";
          do{
            try {sleep(1000); }
            catch (InterruptedException e) { e.printStackTrace(); }

            try {
              sendWhoseMove();
              whoseMove = received;
              colorMove = whoseMove.split(";")[0];
            }
            catch (IOException e) { e.printStackTrace(); disconnect(); break; }
            System.out.println(whoseMove);
            System.out.println("Watki:" +Thread.activeCount());
            //updateGameBoard(whoseMove.substring(colorMove.length() + 1));
            if(colorMove.equals("BothPassed")) {
              updateStatusLabel("BothPassed");
              break;
            }
            if(colorMove.equals("EnemyWantsToPass") && didIPass == false){
              updateStatusLabel("EnemyWantsToPass");
              break;
            }

          }while(!colorMove.equals(color));
          if(whoseMove.length() > colorMove.length() + 5) {
            updateGameBoard(whoseMove.substring(colorMove.length() + 1));
            updateStatusLabel("YrMove");
          }

        }
      };
    }

    protected abstract void updateStatusLabel(String info);

    protected abstract void updatePointsLabel();

    protected abstract void startFinalPhase();

    public String getMyPoints() {
    return myPoints;
  }

  public Socket getSocket(){
      return this.socket;
  }
  protected void connect(Socket socket){
      this.socket = socket;
      try {
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.dis = new DataInputStream(socket.getInputStream());
      }catch (IOException ex){ex.printStackTrace();}
  }

}
