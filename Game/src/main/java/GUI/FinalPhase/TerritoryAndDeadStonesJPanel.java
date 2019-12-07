package GUI.FinalPhase;

import GUI.FirstPhase.GameBoardJPanel;

import javax.swing.*;


public class TerritoryAndDeadStonesJPanel extends GameBoardJPanel {
  final private int[][] stonesAtTheEndOFMatch;

  public TerritoryAndDeadStonesJPanel(int[][] stones, String color){
    super(color);
    this.setStones(stones);
    this.stonesAtTheEndOFMatch = stones;

  }


  @Override
  protected void updateGameBoard() {
    super.updateGameBoard();
    for (int i = 0; i < 19; i++) {
      for (int j = 0; j < 19; j++) {
        if (stones[i][j] == 3)
          stonesJLabels[i][j].setIcon(new ImageIcon("files/white-teritory-pick.png"));
        else if (stones[i][j] == 4)
          stonesJLabels[i][j].setIcon(new ImageIcon("files/black-teritory-pick.png"));
      }
    }
    this.repaint();
  }
}
