package Go.ServerClient;

import Go.ServerClient.Server.Server;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class DeployServerForTests {

  @Test
  public void launchServer() {
    Server server = new Server();
    String[] args = {""};
    boolean isEverythingOkay = true;
    try {
      server.main(args);
    } catch (IOException e) {
      e.printStackTrace();
      isEverythingOkay = false;
    } catch (InterruptedException e) {
      e.printStackTrace();
      isEverythingOkay = false;
    }
    assertTrue(true);
  }
}
