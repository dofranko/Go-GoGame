package GUI.FinalPhase;

import GUI.FirstPhase.ClientGUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FinalPhaseJFrame extends JFrame {
  public enum Stage{DEADSTONES, TERRITORY}

  private TerritoryAndDeadStonesJPanel boardJPanel;
  private String myPoints;
  private ClientGUI parentGame;
  private String myColor;
  private Socket socket;
  private Socket chatSocket;
  private DataOutputStream dos;
  private DataInputStream dis;
  private DataOutputStream chatos;
  private DataInputStream chatis;
  private Stage stage = Stage.DEADSTONES;
  private boolean isItmyTurn = true;

  public FinalPhaseJFrame(int[][] actualStones, String color, ClientGUI parent, String points, Socket socket, Socket chatSocket){
    this.boardJPanel = new TerritoryAndDeadStonesJPanel(actualStones, color);
    this.myColor = color;
    this.myPoints = points;
    this.parentGame = parent;
    this.socket = socket;
    this.chatSocket = chatSocket;
    try {
      this.dis = new DataInputStream(socket.getInputStream());
      this.dos = new DataOutputStream(socket.getOutputStream());
      this.chatos = new DataOutputStream(chatSocket.getOutputStream());
      this.chatis = new DataInputStream(chatSocket.getInputStream());
    }
    catch (IOException ex){ex.printStackTrace();}
    this.stage = Stage.DEADSTONES;
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

    JButton acceptJButton = new JButton("Ackeptuj wybór");
    acceptJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        acceptStage();
      }
    });

    boardJPanel.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int posX = 0;
        while(true){
          if(posX*32 <= e.getX() && e.getX() < posX*32+32)
            break;
          else
            posX++;
        }
        int posY = 0;
        while(true){
          if(posY*32 <= e.getY() && e.getY() < posY*32+32)
            break;
          else
            posY++;
        }
        sendPickStones(posX+","+posY);
      }
      @Override
      public void mousePressed(MouseEvent e) {
      }
      @Override
      public void mouseReleased(MouseEvent e) {
      }
      @Override
      public void mouseEntered(MouseEvent e) {
      }
      @Override
      public void mouseExited(MouseEvent e) {
      }
    });
  }

  private void resumeGame(){

    this.parentGame.resumeGame(this.socket, this.chatSocket);
    this.socket = null;
    this.dos = null;
    this.dis = null;
    this.dispose();
  }

  private void sendPickStones(String move){
    String received ="";
    if(stage.equals(Stage.DEADSTONES)){
      try {
        dos.writeUTF("PickDeadStone");
        dos.writeUTF(move);
        received = dis.readUTF();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else if(stage.equals(Stage.TERRITORY)){
      try {
        dos.writeUTF("PickTerritory");
        dos.writeUTF(move);
        received = dis.readUTF();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    received = received.substring(received.split(";")[0].length()+1);
    updateGameBoard(received);
  }
  public void updateGameBoard(String stonesInString){
    int[][] stones = convertStonesToIntFromString(stonesInString);
    this.boardJPanel.setStones(stones);
  }

  private int[][] convertStonesToIntFromString(String stonesInString) {
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

  private void acceptStage(){
    String received="";
    try {
      dos.writeUTF("Accept");
      received = dis.readUTF();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if(received.equals("Accepted")){

    }
  }
}


