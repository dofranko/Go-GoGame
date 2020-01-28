package GUI;

import GUI.FinalPhase.FinalBoardJPanel;

import javax.swing.*;

public class ReplayGameJBoard extends FinalBoardJPanel {
  public ReplayGameJBoard(int[][] stones) {
    super(stones, "white");

  }

  @Override
  public void setStones(int[][] stones) {
    int[][] copyStones = this.getStones();
    super.setStones(stones);
    int[][] actualStones = this.getStones();
    for (int i = 0; i < stones[0].length; i++) {
      for (int j = 0; j < stones[0].length; j++) {
        if(copyStones[i][j]!=actualStones[i][j])
          copyStones[i][j] = -1;
      }
    }
    for (int i = 0; i < stones[0].length; i++) {
      for (int j = 0; j < stones[0].length; j++) {
        if(copyStones[i][j]==-1){
          switch(actualStones[i][j]){
            case 0:
              stonesJLabels[i][j].setIcon(new ImageIcon("files/no-stone-red.png"));
              break;
            case 1:
              stonesJLabels[i][j].setIcon(new ImageIcon("files/white-stone-red.png"));
              break;
            case 2:
              stonesJLabels[i][j].setIcon(new ImageIcon("files/black-stone-red.png"));
              break;
          }
        }
      }
    }
  }
}
