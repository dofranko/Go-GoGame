package Go.ServerClient;

import Go.ServerClient.Client.Bot;
import Go.ServerClient.Client.ClientFirstPhase;
import Go.ServerClient.Client.ClientFinalPhase;
import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {
  static ClientFirstPhase client;
  static ClientFirstPhase bot;
  //UWAGA! Żeby testować tę klasę trzeba najpierw odpalic DeployServerForTest
  @BeforeClass
  public static void prepareClient() throws InterruptedException {
   client = new ClientExtendToTest(19);
   Thread.sleep(1000);
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

    try {
      client.sendWhoseMove();
    } catch (IOException e) {
      e.printStackTrace();
    }
    String expectedColor = "white";
    if(client.getMyColor().equals("White"))
      expectedColor = "Black";
    client.sendMakeMove(1,1);
    client.sendWhoseMove();
    Thread.sleep(200);
    assertEquals(expectedColor, client.getReceived().split(";")[0]);

    assertEquals(false, client.getIsItMyTurn());
    Thread.sleep(6000);
    assertEquals(client.getMyColor(), client.getReceived().split(";")[0]);
  }

  @Test
  public void goToFinalStage() throws InterruptedException {
    client.sendPass();
    Thread.sleep(4000);
    ClientFinalPhase clientContinue = new ClientFinalPhaseExtendToTest(client);
    Thread.sleep(4000);
    clientContinue.sendPickStones(1,1);
    Thread.sleep(2000);
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
