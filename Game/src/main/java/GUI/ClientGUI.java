package GUI;

import Go.ServerClient.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends Client {

  private JLabel[][] pionki;
  private GameBoardJPanel gameBoardJPanel;
  private JLabel pointsJLabel;
  private JLabel statusJLabel;

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
        sendExit();
        jFrame.dispose();
      }
    });


    addJButtons(mainJPanel);
    createJLabels(mainJPanel);

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
        sendMakeMove(posX+","+posY);
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

  private void createJLabels(JPanel panel){
    this.statusJLabel = new JLabel("Label statusu");
    this.statusJLabel.setBounds(5,5,400,30);
    panel.add(this.statusJLabel);

    this.pointsJLabel = new JLabel("Liczba jeńców: 0");
    this.pointsJLabel.setBounds(450,5,400,30);
    panel.add(this.pointsJLabel);
  }

  private void addJButtons(JPanel panel){
    JButton giveUpJButton = new JButton("Poddaj się");
    giveUpJButton.setBounds(660,300,100,30);
    giveUpJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendGiveUp();
      }
    });

    JButton passJButton = new JButton("Spasuj");
    passJButton.setBounds(660,200,100,30);
    passJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendPass();
      }
    });

    panel.add(giveUpJButton);
    panel.add(passJButton);
  }

  @Override
  public void updateGameBoard(String stonesInString){
      int[][] stones = convertStonesToIntFromString(stonesInString);
      gameBoardJPanel.setStones(stones);
  }

  @Override
  protected void updateStatusLabel(String info) {
    switch (info) {
      case "NotYrMove":
        this.statusJLabel.setText("Wciąż jest ruch przeciwnika");
        break;
      case "IllegalMove":
        this.statusJLabel.setText("Wykonałeś nieporpawny ruch.");
        this.statusJLabel.setForeground(Color.YELLOW);
        break;
      case "YrMove":
        this.statusJLabel.setText("Twój ruch. Wykorzystaj go mądrze.");
        this.statusJLabel.setForeground(new Color(23,95,5));
        break;
      case "MoveMade":
        this.statusJLabel.setText("Ruch przeciwnika.");
        this.statusJLabel.setForeground(Color.RED);
        break;
      default:
        this.statusJLabel.setText("Jeśli to widzisz to zgłoś się do programisty ;-;");
        break;
    }
  }

  @Override
  protected void updatePointsLabel() {
    this.pointsJLabel.setText("Liczba jeńców: " + getMyPoints());
  }


}
