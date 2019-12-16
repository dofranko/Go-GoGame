package Go.ServerClient.Client;

import GUI.ChatJPanel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Bot extends Client{
  private int[][] stones;
  private Thread waitingToMoveThread;
  private boolean isItEnd = false;
  private ChatJPanel chatJPanel;
  private int myLastPoints = 0;
  private List<String> bestMoves = new LinkedList<>();


  public Bot(int size){
    super(size);
    stones = new int[size][size];
    createAndRunWaitingToMoveThread();
    this.chatJPanel = new ChatJPanel(getChatSocket(), this, getMyPlayerId());
  }

  /**
   * Wątek, który sprawdza cały czas, czy jego ruch i jesli tak to robi ruch.
   */
  private void createAndRunWaitingToMoveThread(){
    waitingToMoveThread = new Thread(){
      @Override
      public void run() {
        startWaitingForTurnThread();
        while(!isItEnd){
          try {
            sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          if(received.split(";")[0].contains("Wins")) {
            isItEnd = true;
            System.out.println("Koniec gry. Kończymy na dzisiaj.");
            disconnect();
            break;
          }
          else if(received.split(";")[0].equals("BothPassed")){
            startFinalPhase();
            break;
          }
          else if(received.split(";")[0].contains("Passed")) {
            sendPass();
            continue;
          }
          if(getIsItMyTurn()){
            String myMove = decideTheBestMove();
            if(myMove.equals("Pass"))
              sendPass();
            else
              sendMakeMove(myMove);
            System.out.println("moj ruch: " + myMove);
            chatJPanel.sendChatMessage("Ha!:(" + myMove + ")");
          }
          if(received.equals("NotYrMove")) {
            try { sendWhoseMove(); } catch (IOException e) { break; }
          }
        }
      }
    };
    waitingToMoveThread.start();
  }

  /**
   * metoda sprawdzająca najlepszy możliwy ruch
   * @return
   */
  private String decideTheBestMove(){
    bestMoves = new LinkedList<>();
    int actualHighScore =0;
    int myColorNumber;
    if(getMyColor().equals("Black"))
      myColorNumber = 2;
    else
      myColorNumber = 1;

    //i to rząd (góra dół), j to kolumna
    for(int i=0; i < stones[0].length; i++){
      for(int j =0; j < stones[0].length; j++){
        int scoreForMove = 0;
        if(stones[i][j]!=0)
          continue;

        //Sprawdzanie czy są nasi obok
        scoreForMove += countFriendsPoints(myColorNumber, i, j);
        scoreForMove += countEnemiesPoints(myColorNumber, i, j);
        //TODO metoda na sprawdzanie bicia. Tak samo jak powyzsze metody
        if(scoreForMove > actualHighScore) {
          actualHighScore = scoreForMove;
          bestMoves = new LinkedList<>();
          bestMoves.add(i + "," + j);
        }
        else if (scoreForMove == actualHighScore)
          bestMoves.add(i + "," + j);
      }
    }
    if(bestMoves.size()==0)
      return "Pass";
    return bestMoves.get(new Random().nextInt(bestMoves.size()));
  }

  /**
   * Metoda pomocna do decydowania ruchu. Przyznaje punkty w zależności od liczby przyjaciół
   * @param myColorNumber mój kolor w postaci liczby
   * @param i wiersz sprawdzanego pola
   * @param j kolumna sprawdzanego pola
   * @return liczba punktów
   */
  private int countFriendsPoints(int myColorNumber, int i, int j) {
    int friendsNear = 0;
    int scoreForMove = 0;
    for (int k = -1; k <= 1; k++) {
      if (i + k < 0 || i + k >= stones[0].length)
        continue;
      for (int l = -1; l <= 1; l++) {
        if (j + l < 0 || j + l >= stones[0].length || (k == 0 && l == 0))
          continue;
        if (stones[i+k][l+j] == myColorNumber)
          friendsNear++;
      }
    }
    switch (friendsNear) {
      case 0:
        break;
      case 1:
        scoreForMove = 2;
        break;
      case 2:
        scoreForMove = 5;
        break;
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
        scoreForMove = friendsNear * -2;
        break;

    }
    return scoreForMove;
  }

  /**
   * Metoda pomocna do sprawdzania najlepszego ruchu. Przyznaje punkty w zależnośći od przeciwników
   * @param myColorNumber mój kolor w postaci liczby
   * @param i wiersz sprawdzanego pola
   * @param j kolumna sprawdzanego pola
   * @return liczba punktów
   */
  private int countEnemiesPoints(int myColorNumber, int i, int j){
    int enemiesNear =0;
    int enemiesNumber = 0;
    if(myColorNumber == 1)
      enemiesNumber = 2;
    else
      enemiesNumber = 1 ;
    int scoreForMove=0;
    for(int k=-1; k <= 1; k++) {
      if(i+k <0 || i+k >= stones[0].length)
        continue;
      for (int l = -1; l <= 1; l++) {
        if(j+l <0 || j+l >= stones[0].length || (k == 0 && l == 0))
          continue;
        if(stones[i+k][l+j] == enemiesNumber)
          enemiesNear++;
      }
    }
    switch (enemiesNear){
      case 0:
        break;
      case 1:
        scoreForMove = 3;
        break;
      case 2:
        scoreForMove = 6;
        break;
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
        scoreForMove = enemiesNear * -2;
        break;
    }
    return scoreForMove;
  }

  private int countKills(int myColorNumber, int i, int j){
    int points =0;
    //TODO metoda zliczajaca ilosc zbic. Kazde zbicie to 10 pkt.

    return  points;
  }

  @Override
  protected void startFinalPhase() {
    Thread thread = new Thread(){
      @Override
      public void run() {
        int lengthOfChat = chatJPanel.getChatJTextAreaText().length();
        while(!received.contains("Wins")){
          try { sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
          String chatText = chatJPanel.getChatJTextAreaText();
          if(chatText.length() > lengthOfChat){
            String lastLine = chatText.substring(lengthOfChat);
            System.out.println(lastLine);
            lengthOfChat = chatText.length();
            if(lastLine.contains("Akceptuję!") && lastLine.contains("Enemy"))
              acceptStage();
            else if(lastLine.contains("!dc"))
              break;
          }

        }
      }
    };
    thread.run();
  }
  /**
   * Akceptacja przez gracza aktualnego stanu
   */
  private void acceptStage(){
    try {
      new DataOutputStream(getSocket().getOutputStream()).writeUTF("AcceptStage");
      chatJPanel.sendChatMessage("Akceptuję!");
    }
    catch (IOException e) { e.printStackTrace();}

  }

      @Override
  public void updateGameBoard(String stonesInString) {
    stones = convertStonesToIntFromString(stonesInString);
  }

  @Override
  protected void updateStatusLabel(String info) {

  }

  @Override
  protected void updatePointsLabel() {
    if (myLastPoints < Integer.parseInt(getMyPoints()))
      this.chatJPanel.sendChatMessage("Ha! Mam już " + getMyPoints() + " punktów!");
    myLastPoints = Integer.parseInt(getMyPoints());
  }
}
