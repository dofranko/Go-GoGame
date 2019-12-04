package Go.ServerClient;

import GUI.GameBoardJPanel;

public class ClientExtendToTest extends Client {

  public GameBoardJPanel gameBoard = new GameBoardJPanel("Black");

  @Override
  public void updateGameBoard(String stonesInString) {

  }

  @Override
  protected void updateStatusLabel(String info) {

  }

  @Override
  protected void updatePointsLabel() {

  }


}
