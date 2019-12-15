package GUI.FirstPhase;

import GUI.ChatJPanel;
import GUI.FinalPhase.FinalPhaseJFrame;
import Go.ServerClient.Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;

/**
 * Głowne okno gry. Wygląd
 */
public class ClientGUI extends Client {


  private GameBoardJPanel gameBoardJPanel;
  private JLabel pointsJLabel;
  private JLabel statusJLabel;
  private ChatJPanel chatJPanel;

  private boolean isGameActive = true;
  final private JFrame jFrame;

  public ClientGUI(){
    jFrame = new JFrame();
    if(!getMyColor().equals("Empty"))
      initialize();
    else
      jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
  }
  private void initialize(){
    jFrame.setLayout(null);
    createChatJPanel();

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
        try{
        sendExit();}
        catch(Exception ex){}
        jFrame.dispose();
      }
    });

    addJButtons(mainJPanel);
    createJLabels(mainJPanel);

    addMoustListenerForGameBoard();

    gameBoardJPanel.repaint();
    jFrame.repaint();
    jFrame.pack();
    //insets = rozmiary dla ramki wokól frame
    jFrame.setSize(new Dimension(jFrame.getInsets().left + jFrame.getInsets().right + mainJPanel.getWidth(),
            jFrame.getInsets().top + jFrame.getInsets().bottom + mainJPanel.getHeight()));
    jFrame.setVisible(true);
    jFrame.setResizable(false);


    startWaitingForTurnThread();


  }
  private GameBoardJPanel createGameBoard(){
    return new GameBoardJPanel(getMyColor());
  }

  /**
   * Dodanie listenera na myszkę, który przechwytuje punkt pola (x,y) i podaje go do metody sendMakeMove
   */
  private void addMoustListenerForGameBoard(){

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
        if(isGameActive)
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
  }
  private JPanel createMainBoard() {
    JPanel mainJPanel = new JPanel();
    mainJPanel.setLayout(null);
    mainJPanel.setBounds(0,0,900,800);
    mainJPanel.setOpaque(true);
    mainJPanel.setBackground(Color.LIGHT_GRAY);
    mainJPanel.setMinimumSize(new Dimension(mainJPanel.getWidth(),mainJPanel.getHeight()));
    mainJPanel.setPreferredSize(new Dimension(mainJPanel.getWidth(),mainJPanel.getHeight()));
    return  mainJPanel;
  }

  private void createJLabels(JPanel panel){
    this.statusJLabel = new JLabel("Ruch przeciwnika.");
    this.statusJLabel.setBounds(2,5,400,30);
    this.statusJLabel.setFont(new Font(statusJLabel.getFont().getFontName(), Font.BOLD, 23));
    panel.add(this.statusJLabel);

    this.pointsJLabel = new JLabel("Liczba jeńców: 0");
    this.pointsJLabel.setBounds(450,5,400,30);
    this.pointsJLabel.setFont(new Font(pointsJLabel.getFont().getFontName(), Font.BOLD, 23));
    panel.add(this.pointsJLabel);
  }

  private void addJButtons(JPanel panel){
    JButton giveUpJButton = new JButton("Poddaj się");
    giveUpJButton.setBounds(660,500,100,30);
    giveUpJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(isGameActive)
          sendGiveUp();
      }
    });

    JButton passJButton = new JButton("Spasuj");
    passJButton.setBounds(660,430,100,30);
    passJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(isGameActive)
          sendPass();
      }
    });

    panel.add(giveUpJButton);
    panel.add(passJButton);
  }

  private void createChatJPanel(){
    this.chatJPanel = new ChatJPanel(getChatSocket(), this, getMyPlayerId());
    this.chatJPanel.setLocation(650,5);
    this.chatJPanel.setSize(220,400);
    this.jFrame.add(chatJPanel);
  }

  /**
   * Aktualizacja planszy gry
   * @param stonesInString
   */
  @Override
  public void updateGameBoard(String stonesInString){
      int[][] stones = convertStonesToIntFromString(stonesInString);
      gameBoardJPanel.setStones(stones);
  }

  /**
   * Aktualizacja paska statusu
   * @param info status
   */
  @Override
  protected void updateStatusLabel(String info) {
    switch (info) {
      case "NotYrMove":
        this.statusJLabel.setText("Wciąż jest ruch przeciwnika");
        this.statusJLabel.setForeground(Color.RED);
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
      case "EnemyPassed":
        this.statusJLabel.setText("Przeciwnik pasuje.");
        this.statusJLabel.setForeground(new Color(255, 9, 88));
        JOptionPane.showMessageDialog(jFrame, "Przeciwnik pasuje!");
        break;
      case "BothPassed":
        this.statusJLabel.setText("Przeciwnik również pasuje!");
        this.statusJLabel.setForeground(new Color(30, 26, 255));
        startFinalPhase();
        break;
      case "YouPassed":
        this.statusJLabel.setText("Spasowałeś. Oczekiwanie...");
        this.statusJLabel.setForeground(new Color(105, 16, 64));
        break;
      case "ResumeGame":
        this.statusJLabel.setText("Gra jest kontynuowana.");
        this.statusJLabel.setForeground(new Color(23,95,5));
        break;
      case "EnemyGaveUp":
        this.statusJLabel.setText("Przeciwnik się poddał!");
        this.statusJLabel.setForeground(new Color(255, 199, 35));
        break;
      case "YouWin":
        isGameActive = false;
        this.statusJLabel.setText("Wygrałeś!");
        this.statusJLabel.setForeground(new Color(218, 255, 22));
        break;
      case "YouLose":
        isGameActive = false;
        this.statusJLabel.setText("Przegrałeś. :'(");
        this.statusJLabel.setForeground(new Color(34, 0, 2));
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

  /**
   * Stworzenie nowego okna do zliczania punktów w ostatniej fazie i rozłączenie z socketami i streamami
   */
  @Override
  protected void startFinalPhase() {
    this.chatJPanel.sendChatMessage("Czas na zliczanie punktów ;-)");
    JOptionPane.showMessageDialog(jFrame, "Oboje spasowaliście. Zaraz rozpocznie się etap końcowy");
    this.jFrame.setVisible(false);
    JFrame finalJFrame = new FinalPhaseJFrame(gameBoardJPanel.getStones(), this.getMyColor(),
            this, this.getSocket(), this.getChatSocket(), this.chatJPanel);
    finalJFrame.setVisible(true);
    super.startFinalPhase();
  }

  /**
   * W przypadku wznowienia gry - przywrócenie starych wartości
   * @param socket
   *
   *
   */
  public void resumeGame(Socket socket) {
    super.connect(socket);
    this.jFrame.setVisible(true);
    updateStatusLabel("ResumeGame");

  }

  /**
   * W przypadku, gdy gracz rozłączy się z grą w nowym oknie
   */
  public void finishGame(){
    jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
  }



  /*public JFrame getjFrame(){
    return this.jFrame;
  }*/
}

