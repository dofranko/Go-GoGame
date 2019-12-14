package GUI.FinalPhase;

import GUI.ChatJPanel;
import GUI.FirstPhase.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FinalPhaseJFrame extends JFrame {
  /**
   * Etap rozgrywki. Wybieranie martwych kamieni - wybieranie terytorium - koniec
   */
  public enum Stage{DEADSTONES, TERRITORY, THEEND}

  private FinalStageJPanel boardJPanel;
  private ChatJPanel chatJPanel;

  private String myPoints;
  private String myColor;

  private ClientGUI parentGame;

  private Socket socket;
  private DataOutputStream dos;
  private DataInputStream dis;

  private Socket chatSocket;
  private DataOutputStream chatos;
  private DataInputStream chatis;

  private Stage stage = Stage.DEADSTONES;

  public FinalPhaseJFrame(int[][] actualStones, String color, final ClientGUI parent,  Socket socket, Socket chatSocket,
                          ChatJPanel chatJPanel){
    this.boardJPanel = new FinalStageJPanel(actualStones, color);
    //this.boardJPanel.setSize(900,800);
    this.add(boardJPanel);
    this.chatJPanel = chatJPanel;
    this.myColor = color;
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
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        stage = Stage.THEEND;
        try {
          chatos.writeUTF("!dc");
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        disconnect("!dc");
      }
    });
    //this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    this.setSize(new Dimension(this.getInsets().left + this.getInsets().right + boardJPanel.getWidth(),
            this.getInsets().top + this.getInsets().bottom + boardJPanel.getHeight()));
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
    this.boardJPanel.repaint();
    this.pack();
    startRefreshingMapThread();
  }

  /**
   * rozłączenie tego okna
   * @param status
   */
  private void disconnect(String status){
    if(status.equals("!dc"))
      this.parentGame.finishGame();

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
  private void resumeGame(){
    this.parentGame.resumeGame(this.socket);
    disconnect("");
  }

  /**
   * wysłanie wybrangeo pola do swerwera
   * @param move
   */
  private void sendPickStones(String move){
    String received ="";
    if(stage.equals(Stage.DEADSTONES)){
      try {
        dos.writeUTF("PickDeadStones");
        dos.writeUTF(move);
      } catch (IOException e) { e.printStackTrace(); }
    }
    else if(stage.equals(Stage.TERRITORY)){
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
  public void updateGameBoard(String stonesInString){
    int[][] stones = convertStonesToIntFromString(stonesInString);
    this.boardJPanel.setStones(stones);
  }

  /**
   * Zamiana String w tablice int[][] kamieni
   * @param stonesInString kamienie w Stringu
   * @return tablica int[][] kamieni
   */
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

  /**
   * Akceptacja przez gracza aktualnego stanu
   */
  private void acceptStage(){
    try {
      dos.writeUTF("AcceptStage");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Wątek pobierający informacje o planszy i odświeżający ją. (@see waitingForTurnThread)
   */
  private void startRefreshingMapThread(){
    Thread refresh = new Thread(){
      @Override
      public void run() {
        String stones;
        while(!stage.equals(Stage.THEEND)){
          try{
            sleep(1000);
            dos.writeUTF("MapRefresh");
            stones = dis.readUTF();
          }
          catch (InterruptedException ex){ex.printStackTrace(); break;}
          catch (IOException ex) {ex.printStackTrace(); break;}
          catch (Exception ex) {ex.printStackTrace(); break;}
          System.out.println("Hi:"+stones);
          if(!stones.split(";")[0].equals("WhiteAccepted") && !stones.split(";")[0].equals("BlackAccepted"))
            updateGameBoard(stones);
          else{
            stones = stones.substring(stones.split(";")[0].length()+1);
            updateGameBoard(stones);
          }
        }
      }
    };
    refresh.start();
  }
}


