package Go.ServerClient;

import GUI.FirstPhase.GameBoardJPanel;
import Go.ServerClient.Client.Client;

public class ClientExtendToTest extends Client {

  public GameBoardJPanel gameBoard = new GameBoardJPanel("Black", 19);

  public ClientExtendToTest(int size) {
    super(size);
  }

  @Override
  public void updateGameBoard(String stonesInString) {
    this.gameBoard.setStones(convertStonesToIntFromString(stonesInString));
  }

  @Override
  protected void updateStatusLabel(String info) {

  }

  @Override
  protected void updatePointsLabel() {

  }

  @Override
  protected void startFinalPhase() {

  }


}
