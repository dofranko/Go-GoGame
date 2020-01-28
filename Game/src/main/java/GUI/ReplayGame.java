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
  private JLabel playersJLabel = new JLabel();
  private JLabel playerColorMoveJLabel;
  private JPanel mainJPanel;
  private int whitePlayerId;
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
    mainJPanel.add(playersJLabel);
    //
    this.add(mainJPanel);
    mainJPanel.setBounds(0,0,900,700);
   // this.boardJPanel.repaint();
    this.setSize(900, 800);
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
        try{
          number = Integer.parseInt(gameIDTextField.getText());
          gameDataMoves = downloadGameData(number);
          gameIDTextField.setText("");
          prepareBoard();
        }
        catch (IllegalArgumentException ex){
          JOptionPane.showMessageDialog(null,"Brak takiej gry");
          gameIDTextField.setText("");
        }
        catch(Exception ex){gameIDTextField.setText("Podaj liczbę");}


      }
    });
    playerColorMoveJLabel = new JLabel();
    mainJPanel.add(playerColorMoveJLabel);
    playerColorMoveJLabel.setBounds(600,10,200,30);
  }

  private void createNextPreviousButtons(){
    JButton nextMove = new JButton(">");
    mainJPanel.add(nextMove);
    nextMove.setBounds(200,10,50,50);

    JButton previousMove = new JButton("<");
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
    if(gameDataMoves.get(moveNumber).getPlayerIdMove().equals(whitePlayerId))
      this.playerColorMoveJLabel.setText("Biały ");
    else
      this.playerColorMoveJLabel.setText("Czarny ");
    switch (gameDataMoves.get(moveNumber).getTypeOfMove()){
      case "ruch":
        this.playerColorMoveJLabel.setText(playerColorMoveJLabel.getText()+"wykonał ruch.");
        break;
      case "pass":
        this.playerColorMoveJLabel.setText(playerColorMoveJLabel.getText()+"spasował.");
        break;
      case "poddanie sie":
        this.playerColorMoveJLabel.setText(playerColorMoveJLabel.getText()+"oddał grę.");
        break;
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

  private List<MovesEntity> downloadGameData(int gameID) throws IllegalArgumentException {
    Session session = getSession();
    List<MovesEntity> moves = session.createQuery("from MovesEntity  where Gameid = " + gameID).list();
    if(moves.size()==0)
      throw new IllegalArgumentException();
    GamesEntity game = (GamesEntity) session.createQuery("from GamesEntity where id = " + gameID).list().get(0);
    playersJLabel.setText("<html><pre>Zawodnicy:\nBiały: " + game.getWhitePlayerId() + "\tCzarny: " + game.getBlackPlayerId() + "</pre></html>");
    playersJLabel.setBounds(300,10,400,60);
    whitePlayerId = game.getWhitePlayerId();
    moveNumber = 0;
    session.close();

    return moves;
  }
  private void prepareBoard(){
    if(boardJPanel!=null)
      mainJPanel.remove(boardJPanel);

      boardJPanel = new ReplayGameJBoard(convertStonesToIntFromString(gameDataMoves.get(0).getBoard()));
      this.boardJPanel.setSize(900,800);
      mainJPanel.add(boardJPanel);
      boardJPanel.setLocation(0, 70);


    boardJPanel.repaint();
  }
  private int[][] getNullStones(){
    int size = gameDataMoves.get(0).getBoard().split(";").length;
    int[][] nullStones = new int[size][size];
    for(int i = 0; i< size; i++)
      for(int j = 0; j<size; j++)
        nullStones[i][j]=0;

      return nullStones;
  }









}
