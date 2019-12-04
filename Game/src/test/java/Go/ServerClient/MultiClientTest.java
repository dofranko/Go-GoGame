package Go.ServerClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MultiClientTest {

  ClientExtendToTest player1;
  ClientExtendToTest player2;

  @Before
  public void createPlayers() throws InterruptedException {
    player1 = new ClientExtendToTest();
    Thread.sleep(2000);
    player2 = new ClientExtendToTest();
  }
  @After
  public void closeClients(){
    player2.sendAndReceiveInformation("Exit");
    player1.sendAndReceiveInformation("Exit");
  }

  //TODO dodać metody testujące przebieg rozgrywki, typu ruch jeen ruch drugi,
  //sprawdzenie isItmyTurn
  //meotdy sprawdzające insertowanie kamieni przy dwóch graczach może
  //WAŻNE: clienci pracują na wątkach więc trzeba odczekiwać zanim się zrobic ruch i sprawdzanie

  @Test
  public void simpleInsert() throws InterruptedException {
    Thread.sleep(2000);
    player1.sendAndReceiveInformation("1,1");
    Thread.sleep(2000);
    player2.sendAndReceiveInformation("2,3");
    Thread.sleep(2000);
    player1.sendAndReceiveInformation("0,10");
    Thread.sleep(2000);

    int[][] array = createArrayForGameBoard();
    array[1][1] = 1;
    array[2][3]=2;
    array[0][10] = 1;
    assertEquals(array, player1.gameBoard.getStones());
    assertEquals(array, player2.gameBoard.getStones());
    //player1
    assertEquals(array[1][1], player1.gameBoard.getStones()[1][1]);
    assertEquals(array[2][3], player1.gameBoard.getStones()[2][3]);
    assertEquals(array[0][10], player1.gameBoard.getStones()[0][10]);
    //player2
    assertEquals(array[1][1], player2.gameBoard.getStones()[1][1]);
    assertEquals(array[2][3], player2.gameBoard.getStones()[2][3]);
    assertEquals(array[0][10], player2.gameBoard.getStones()[0][10]);

  }
  //dodac testowanie np zbicia, ko, itp


  private int[][] createArrayForGameBoard() {
    int[][] array = new int[19][19];
    for (int i = 0; i < 19; i++) {
      for (int j = 0; j < 19; j++) {
        array[i][j] = 0;
      }
    }
    return array;
  }
}
