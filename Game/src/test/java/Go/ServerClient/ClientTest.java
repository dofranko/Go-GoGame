package Go.ServerClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {
  Client client;
  //UWAGA! Żeby testować tę klasę trzeba najpierw odpalic DeployServerForTest
  @Before
  public void prepareClient(){
   client = new ClientExtendToTest();
   /*client = mock(ClientExtendToTest.class);
    doNothing().when(client).setReceived("testingMethod");
    doNothing().when(client).sendAndReceiveInformation("Exit");
    when(client.getReceived()).thenReturn("testingMethod");
    when(client.getMyPlayerId()).thenReturn("1");
    when(client.getIsItmyTurn()).thenReturn(false);*/
  }
  @After
  public void closeConnection(){
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
    assertEquals(false, client.getIsItMyTurn());


    client.sendMakeMove("1,1");
    assertEquals(false, client.getIsItMyTurn());
  }

  @Test
  public void getSetReceived(){
    //client.setReceived("testingMethod");
    System.out.println(client.getMyPlayerId());
    //assertEquals("testingMethod", client.getReceived() );
  }


  @Test
  public void myIdTest(){
    assertNotNull(client.getMyPlayerId());
  }




}
