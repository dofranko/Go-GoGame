package GUI;

import GUI.FinalPhase.FinalBoardJPanel;
import GUI.FirstPhase.FirstPhaseGUI;
import Go.ServerClient.Client.ClientFinalPhase;
import Go.ServerClient.Hibernate.GamesEntity;
import Go.ServerClient.Hibernate.MovesEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ReplayGame extends JFrame {

  private static final SessionFactory ourSessionFactory;
  static {
    try {
      Configuration configuration = new Configuration();
      configuration.configure();

      ourSessionFactory = configuration.buildSessionFactory();
    } catch (Throwable ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static Session getSession() throws HibernateException {
    return ourSessionFactory.openSession();
  }








  private ReplayGameJBoard boardJPanel;
  private JLabel pointsJLabel;
  private JPanel mainJPanel;
  private int whitePlayerID;
  private int blackPlayerID;
  private int moveNumber = 0;
  private List<MovesEntity> gameDataMoves;

  public static void main( String[] args )
  {
    ReplayGame replay = new ReplayGame();
    replay.setVisible(true);
  }

  public ReplayGame() {
    this.setLayout(null);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //this.boardJPanel = new JPanel();
    //
    mainJPanel = new JPanel();
    mainJPanel.setLayout(null);
    //
    this.add(mainJPanel);
    mainJPanel.setBounds(0,0,900,700);
   // this.boardJPanel.repaint();
    this.setSize(900, 700);
    this.setResizable(false);

    JTextField gameIDTextField = new JTextField();
    mainJPanel.add(gameIDTextField);
    gameIDTextField.setBounds(800,20,80,20);

    JButton downloadGameButton = new JButton("Wybierz");
    mainJPanel.add(downloadGameButton);
    downloadGameButton.setBounds(800,50,80,20);

    createNextPreviousButtons();

    downloadGameButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int number = 0;
        try{ number = Integer.parseInt(gameIDTextField.getText()); }
        catch(Exception ex){gameIDTextField.setText("Podaj liczbe");}
        gameDataMoves = downloadGameData(number);
        gameIDTextField.setText("");

        prepareBoard();
      }
    });
  }

  private void createNextPreviousButtons(){
    JButton nextMove = new JButton(">");
    mainJPanel.add(nextMove);
    nextMove.setBounds(200,10,50,50);

    JButton previousMove = new JButton(">");
    mainJPanel.add(previousMove);
    previousMove.setBounds(140,10,50,50);

    nextMove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(moveNumber<gameDataMoves.size()-1) {
          moveNumber++;
          updateGameBoard(gameDataMoves.get(moveNumber).getBoard());
        }
      }
    });
    previousMove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(moveNumber>0) {
          moveNumber--;
          updateGameBoard(gameDataMoves.get(moveNumber).getBoard());
        }
      }
    });
  }

  protected void  updateGameBoard(String stonesInString){
    int[][] stones = convertStonesToIntFromString(stonesInString);
    this.boardJPanel.setStones(stones);
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

  private List<MovesEntity> downloadGameData(int gameID) {
    Session session = getSession();
    List<MovesEntity> moves = session.createQuery("from MovesEntity  where Gameid = " + gameID).list();
    GamesEntity game = (GamesEntity) session.createQuery("from GamesEntity where id = " + gameID).list().get(0);
    whitePlayerID = game.getWhitePlayerId();
    blackPlayerID = game.getBlackPlayerId();
    moveNumber = 1;
    session.close();

    return moves;
  }
  private void prepareBoard(){
    if(boardJPanel == null){
      boardJPanel = new ReplayGameJBoard(convertStonesToIntFromString(gameDataMoves.get(0).getBoard()));
      this.boardJPanel.setSize(900,800);
      mainJPanel.add(boardJPanel);
      boardJPanel.setLocation(0, 70);
    }
    else
      boardJPanel.setStones(convertStonesToIntFromString(gameDataMoves.get(0).getBoard()));
    boardJPanel.repaint();
  }










}
