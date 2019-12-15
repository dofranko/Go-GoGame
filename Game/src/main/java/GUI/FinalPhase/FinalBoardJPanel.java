package GUI.FinalPhase;

import GUI.FirstPhase.GameBoardJPanel;

import javax.swing.*;


public class FinalBoardJPanel extends GameBoardJPanel {
 // final private int[][] stonesAtTheEndOFMatch;

  public FinalBoardJPanel(int[][] stones, String color){
    super(color);
    this.setStones(stones);
    this.stones = stones;
  }

  /**
   * Aktualizacja planszy
   */
  @Override
  protected void updateGameBoard() {
    super.updateGameBoard();
    for (int i = 0; i < 19; i++) {
      for (int j = 0; j < 19; j++) {
        switch (stones[i][j]){
          case 0:
          case 1:
          case 2:
            break;
          case 3:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/white-dead-pick.gif"));
            break;
          case 4:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/black-dead-pick.gif"));
            break;
          case 5:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/white-territory-pick.gif"));
            break;
          case 6:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/black-territory-pick.gif"));
            break;
          case 7:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/white-territory-done.gif"));
            break;
          case 8:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/black-territory-done.gif"));
            break;
        }
      }
    }
    this.repaint();
  }
}
