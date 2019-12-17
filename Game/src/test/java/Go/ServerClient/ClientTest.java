package Go.ServerClient;

import Go.ServerClient.Client.Bot;
import Go.ServerClient.Client.Client;
import Go.ServerClient.Client.ClientFinalPhase;
import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {
  static Client client;
  static Client bot;
  //UWAGA! Żeby testować tę klasę trzeba najpierw odpalic DeployServerForTest
  @BeforeClass
  public static void prepareClient(){
   client = new ClientExtendToTest(19);
   bot = new Bot(19);
   //To jest czarny gracz
  }
  @AfterClass
  public static void closeConnection(){
    client.sendGiveUp();
    System.out.println("zamykam");
    client.sendExit();
    bot.sendExit();
  }

  @Test
  public void whoseMove() throws InterruptedException, IOException {
    assertEquals(true, client.getIsItMyTurn());
    try {
      client.sendWhoseMove();
    } catch (IOException e) {
      e.printStackTrace();
    }

    client.sendMakeMove(1,1);
    client.sendWhoseMove();
    Thread.sleep(200);
    assertEquals("White", client.getReceived().split(";")[0]);

    Thread.sleep(4000);
    assertEquals("Black", client.getReceived().split(";")[0]);
  }

  @Test
  public void goToFinalStage() throws InterruptedException {
    client.sendPass();
    Thread.sleep(4000);
    ClientFinalPhase clientContinue = new ClientFinalPhaseExtendToTest(client);
    Thread.sleep(4000);
    clientContinue.acceptStage();
    Thread.sleep(4000);
    clientContinue.declineStage();
    Thread.sleep(1000);
    clientContinue.disconnect("!dc");
  }

  @Test
  public void myIdTest(){
    assertNotNull(client.getMyPlayerId());
  }

  @Test
  public void receivedTest(){
    assertNotNull(client.getReceived());
  }

  @Test
  public void colorTest(){ assertNotNull(client.getMyColor());}

  @Test
  public void pointsTest(){assertEquals("0", client.getMyPoints());}

  @Test
  public void socketTest(){assertNotNull(client.getSocket());}

  @Test
  public void enemyPlayerIdTest() {assertNotNull(client.getEnemyPlayerId());}

  @Test
  public void boardSize() {assertEquals(19, client.getBoardSize());}


}
