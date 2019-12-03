package Go.ServerClient;

import GUI.GameBoardJPanel;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertEquals;

public class GameBoardJPanelTest {

  GameBoardJPanel gameBoardJPanel;

  @Before
  public void createGameBoardJPanel(){
    this.gameBoardJPanel = new GameBoardJPanel("Black");
  }

  @Test
  public void getSetStones(){
    int[][] testStones = new int[19][19];
    for(int i=0; i<19; i++){
      for(int j=0; j<19; j++){
        testStones[i][j]=0;
      }
    }
    this.gameBoardJPanel.setStones(testStones);
    for(int i=0; i<19; i++){
      for(int j=0; j<19; j++){
        assertEquals(0, this.gameBoardJPanel.getStones()[i][j]);
      }
    }

    testStones[1][1] = 1;
    testStones[2][3] = 2;
    testStones[4][0] =1 ;
    this.gameBoardJPanel.setStones(testStones);
    assertEquals(0, this.gameBoardJPanel.getStones()[0][0]);
    assertEquals(1, this.gameBoardJPanel.getStones()[1][1]);
    assertEquals(2, this.gameBoardJPanel.getStones()[2][3]);
    assertEquals(1, this.gameBoardJPanel.getStones()[4][0]);

  }

}
