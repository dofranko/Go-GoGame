package GUI.FinalPhase;

import GUI.ChatJPanel;
import GUI.FirstPhase.ClientGUI;
import Go.ServerClient.Client.ClientFinalPhase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;

public class FinalPhaseGUI extends ClientFinalPhase {
  /**
   * Etap rozgrywki. Wybieranie martwych kamieni - wybieranie terytorium - koniec
   */
  public enum Stage{DEADSTONES, TERRITORY, THEEND}

  private FinalBoardJPanel boardJPanel;
  private ChatJPanel chatJPanel;

  protected ClientGUI parentGame;




  public FinalPhaseGUI(int[][] actualStones, String color, final ClientGUI parent, Socket socket, Socket chatSocket,
                       ChatJPanel chatJPanel){
    super(color,  socket, chatSocket);
    this.boardJPanel = new FinalBoardJPanel(actualStones, color);
    //this.boardJPanel.setSize(900,800);
    JPanel mainJPanel = new JPanel();
    mainJPanel.setLayout(null);
    mainJPanel.add(boardJPanel);
    this.add(mainJPanel);
    JButton acceptStageJButton = new JButton("Akceptuj");
    acceptStageJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        acceptStage();
      }
    });
    acceptStageJButton.setBounds(this.boardJPanel.getX() + this.boardJPanel.getWidth() + 20,
            450, 200,30);
    mainJPanel.add(acceptStageJButton);

    JButton declineStageJButton = new JButton("Odrzuć");
    declineStageJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        declineStage();
      }
    });
    declineStageJButton.setBounds(acceptStageJButton.getX(),
            acceptStageJButton.getY() + acceptStageJButton.getHeight() + 30,
            acceptStageJButton.getWidth(),acceptStageJButton.getHeight());
    mainJPanel.add(declineStageJButton);
    this.chatJPanel = chatJPanel;
    this.chatJPanel.setLocation(650, 5);
    this.chatJPanel.setSize(220,400);
    mainJPanel.add(this.chatJPanel);
    this.parentGame = parent;
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        stage = Stage.THEEND;
        chatJPanel.sendChatMessage("!dc");
        disconnect("!dc");
      }
    });
    //this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    this.setSize(new Dimension(this.getInsets().left + this.getInsets().right + boardJPanel.getWidth() ,
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
    this.setSize(909,657);
    this.setResizable(false);
    this.chatJPanel.setSize(220,400);
    startRefreshingMapThread();
  }

  @Override
  protected void  updateGameBoard(String stonesInString){
    int[][] stones = convertStonesToIntFromString(stonesInString);
    this.boardJPanel.setStones(stones);
  }

  @Override
  protected void disconnect(String status) {
    if(status.equals("!dc"))
      this.parentGame.finishGame();
    super.disconnect(status);
  }

  @Override
  protected void resumeGame() {
    this.parentGame.resumeGame(this.socket);
    super.resumeGame();
  }
}


