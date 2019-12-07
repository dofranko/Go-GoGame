package Go.ServerClient;

import GUI.FirstPhase.GameBoardJPanel;

public class ClientExtendToTest extends Client {

  public GameBoardJPanel gameBoard = new GameBoardJPanel("Black");

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
