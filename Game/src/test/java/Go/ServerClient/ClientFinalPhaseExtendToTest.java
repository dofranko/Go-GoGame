package Go.ServerClient;

import Go.ServerClient.Client.ClientFirstPhase;
import Go.ServerClient.Client.ClientFinalPhase;

public class ClientFinalPhaseExtendToTest extends ClientFinalPhase {

  public ClientFinalPhaseExtendToTest(ClientFirstPhase client){
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
