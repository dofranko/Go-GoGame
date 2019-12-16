package Go.ServerClient.Client;

import GUI.FinalPhase.FinalPhaseGUI;


import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class ClientFinalPhase extends JFrame {
  public enum Stage{DEADSTONES, TERRITORY, THEEND}

  protected String myPoints;
  protected String myColor;

  protected Socket socket;
  protected DataOutputStream dos;
  protected DataInputStream dis;

  protected Socket chatSocket;
  protected DataOutputStream chatos;
  protected DataInputStream chatis;

  //protected ClientGUI parentGame;

  protected Stage stage = Stage.DEADSTONES;

  public ClientFinalPhase(String color,  Socket socket, Socket chatSocket){
    this.myColor = color;
    this.socket = socket;
    this.chatSocket = chatSocket;
    try {
      this.dis = new DataInputStream(socket.getInputStream());
      this.dos = new DataOutputStream(socket.getOutputStream());
      this.chatos = new DataOutputStream(chatSocket.getOutputStream());
      this.chatis = new DataInputStream(chatSocket.getInputStream());
    }
    catch (IOException ex){ex.printStackTrace();}
    this.stage = FinalPhaseGUI.Stage.DEADSTONES;
  }


  /**
   * rozłączenie tego okna
   * @param status
   */
  protected void disconnect(String status){
    this.socket = null;
    this.chatSocket = null;
    this.dos = null;
    this.dis = null;
    this.chatos = null;
    this.chatis = null;
    this.dispose();
  }

  /**
   * wznowienie gry
   */
  protected void resumeGame(){
    disconnect("");
  }

  /**
   * wysłanie wybrangeo pola do swerwera
   * @param move
   */
  protected void sendPickStones(String move){
    String received ="";
    if(stage.equals(FinalPhaseGUI.Stage.DEADSTONES)){
      try {
        dos.writeUTF("PickDeadStones");
        dos.writeUTF(move);
      } catch (IOException e) { e.printStackTrace(); }
    }
    else if(stage.equals(FinalPhaseGUI.Stage.TERRITORY)){
      try {
        dos.writeUTF("PickTerritory");
        dos.writeUTF(move);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println(received);
  }

  /**
   * aktualizacja planszy gry
   * @param stonesInString
   */
  protected abstract void  updateGameBoard(String stonesInString);

  /**
   * Zamiana String w tablice int[][] kamieni
   * @param stonesInString kamienie w Stringu
   * @return tablica int[][] kamieni
   */
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

  /**
   * Akceptacja przez gracza aktualnego stanu
   */
  protected void acceptStage(){
    try {
      dos.writeUTF("AcceptStage");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  protected void declineStage(){
    try {
      dos.writeUTF("DeclineStage");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  protected void getPoints() {
	  try {
	      dos.writeUTF("GetPoints");
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
  }

  /**
   * Wątek pobierający informacje o planszy i odświeżający ją. (@see waitingForTurnThread)
   */
  protected void startRefreshingMapThread(){
    Thread refresh = new Thread(){
      @Override
      public void run() {
        String stones;
        while(!stage.equals(FinalPhaseGUI.Stage.THEEND)){
          try{
            sleep(1000);
            dos.writeUTF("MapRefresh");
            stones = dis.readUTF();
          }
          catch (InterruptedException ex){ex.printStackTrace(); break;}
          catch (IOException ex) {ex.printStackTrace(); break;}
          catch (Exception ex) {ex.printStackTrace(); break;}
          System.out.println("Hi:"+stones);
          String status;
          try{
            int trying = Integer.parseInt(stones.split(";")[0]);
            updateGameBoard(stones);
          }catch(Exception ex) {
            status =  stones.split(";")[0];
            stones = stones.substring(status.length() + 1);
            if(status.equals("PickingTerritory")) 
            	stage = Stage.TERRITORY;
            else if(status.equals("End")) 
            	stage = Stage.THEEND;
            updateGameBoard(stones);
          }
        }
      }
    };
    refresh.start();
  }
}
