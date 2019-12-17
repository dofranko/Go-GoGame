package Go.ServerClient;

import Go.ServerClient.Client.Client;
import Go.ServerClient.Client.ClientFinalPhase;

public class ClientFinalPhaseExtendToTest extends ClientFinalPhase {

  public ClientFinalPhaseExtendToTest(Client client){
    super(19, client.getMyColor(), client.getSocket(), client.getChatSocket());
    startRefreshingMapThread();
  }

  @Override
  protected void updateGameBoard(String stonesInString) {

  }

  @Override
  protected void updatePointsLabel(int points) {

  }
}
