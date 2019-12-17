package Go.ServerClient;

import org.junit.*;

import static org.junit.Assert.assertEquals;

public class MultiClientTest {

  static ClientExtendToTest player1;
  static ClientExtendToTest player2;

  @BeforeClass
  public static void createPlayers() throws InterruptedException {
    player1 = new ClientExtendToTest(19);
    Thread.sleep(5000);
    player2 = new ClientExtendToTest(19);
  }
  @AfterClass
  public static void closeClients(){
    player2.sendExit();
    player1.sendExit();
  }


  @Test
  public void simpleInsert() throws InterruptedException {
    Thread.sleep(2000);
    System.out.println("Ruch robi gracz 1: 1,1");
    player1.sendMakeMove(1,1);
    Thread.sleep(5000);
    player2.sendMakeMove(2,3);
    System.out.println("Ruch robi gracz 2: 2,3");
    Thread.sleep(5000);
    player1.sendMakeMove(0,10);
    System.out.println("Ruch robi gracz 1: 0,10");
    Thread.sleep(5000);

    int[][] array = createArrayForGameBoard();
    array[2][3]= 1 ;
    array[0][10] = 2;
    int[][] st = player1.gameBoard.getStones();
    for(int i=0; i<19; i++) {
      for (int j = 0; j < 19; j++) {
        System.out.print(st[i][j] +",") ;

      }
      System.out.println();
    }

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
