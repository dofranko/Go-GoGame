package Go.ServerClient;

import Go.ServerClient.Server.Server;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class DeployServerForTests {

  @Ignore
  @Test
  public void launchServer() throws InterruptedException {
    Server server = new Server();
    String[] args = {""};
    Thread serverThread = new Thread() {
      @Override
      public void run() {
        try {
          server.main(args);
        } catch (IOException e) {  }
        catch (InterruptedException e) { }
      }
    };
    serverThread.start();
    Thread.sleep(30000);

    assertTrue(true);

  }
}
