package Go.ServerClient;

import Go.ServerClient.Client.Client;
import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {
  static Client client;
  //UWAGA! Żeby testować tę klasę trzeba najpierw odpalic DeployServerForTest
  @BeforeClass
  public static void prepareClient(){
   client = new ClientExtendToTest();
   //To jest czarny gracz
  }
  @AfterClass
  public static void closeConnection(){
    System.out.println("zamykam");
    client.sendExit();
  }

  @Test
  public void whoseMove(){
    System.out.println("tu jesem");
    try {
      client.sendWhoseMove();
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals(true, client.getIsItMyTurn());


    client.sendMakeMove("1,1");
    assertEquals(false, client.getIsItMyTurn());
  }

  @Test
  public void myIdTest(){
    assertNotNull(client.getMyPlayerId());
  }




}
