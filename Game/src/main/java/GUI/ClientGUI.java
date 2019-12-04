package GUI;

import Go.ServerClient.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends Client {

  private JLabel[][] pionki;
  private GameBoardJPanel gameBoardJPanel;

  public ClientGUI(){
    initialize();
  }
  private void initialize(){
    final JFrame jFrame = new JFrame();
    jFrame.setLayout(null);

    JPanel mainJPanel = createMainBoard();
    GameBoardJPanel gameBoardJPanel = createGameBoard();
    gameBoardJPanel.setLocation(30,30);
    this.gameBoardJPanel = gameBoardJPanel;
    mainJPanel.add(gameBoardJPanel);
    //jFrame właściwości
    jFrame.add(mainJPanel);
    jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        sendAndReceiveInformation("Exit");
        try {sendAndReceiveInformation("Exit");}
        catch (Exception ex){}
        jFrame.dispose();
      }
    });

    JButton giveUpJButton = new JButton("Poddaj się");
    giveUpJButton.setBounds(660,300,100,30);
    giveUpJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendAndReceiveInformation("GiveUp");
      }
    });
    JButton passJButton = new JButton("Spasuj");
    passJButton.setBounds(660,200,100,30);
    passJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendAndReceiveInformation("Pass");
      }
    });
    mainJPanel.add(passJButton);
    mainJPanel.add(giveUpJButton);
    gameBoardJPanel.addMouseListener(new MouseListener() {
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
        sendAndReceiveInformation(posX+","+posY);
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

    gameBoardJPanel.repaint();
    jFrame.repaint();
    jFrame.pack();
    //insets = rozmiary dla ramki wokól frame
    jFrame.setSize(new Dimension(jFrame.getInsets().left + jFrame.getInsets().right + 800,
            jFrame.getInsets().top + jFrame.getInsets().bottom + 650));
    jFrame.setVisible(true);
    jFrame.setResizable(false);
  }
  private GameBoardJPanel createGameBoard(){
    return new GameBoardJPanel(getColor());
  }
  private JPanel createMainBoard() {
    JPanel mainJPanel = new JPanel();
    mainJPanel.setLayout(null);
    mainJPanel.setBounds(0,0,800,800);
    mainJPanel.setOpaque(true);
    mainJPanel.setBackground(Color.LIGHT_GRAY);
    mainJPanel.setMinimumSize(new Dimension(mainJPanel.getWidth(),mainJPanel.getHeight()));
    mainJPanel.setPreferredSize(new Dimension(mainJPanel.getWidth(),mainJPanel.getHeight()));
    return  mainJPanel;
  }

  @Override
  public void updateGameBoard(String stonesInString){
      int[][] stones = convertStonesToIntFromString(stonesInString);
      if(stones[0][0]!=3)
        gameBoardJPanel.setStones(stones);

  }



}
