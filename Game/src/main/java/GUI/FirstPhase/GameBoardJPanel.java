package GUI.FirstPhase;

import Go.GameMaker.Markers;

import javax.swing.*;
import java.awt.*;


public class GameBoardJPanel extends JPanel {

  protected int[][] stones;
  protected JLabel[][] stonesJLabels;

  public GameBoardJPanel(String color) {
    this.setLayout(null);
    this.setBounds(0, 0, 608, 608);
    this.setOpaque(true);
    this.setBackground(Color.GRAY);
    this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
    this.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
    //kolejność następnych komponentow jest wazna
    stonesJLabels = new JLabel[19][19];
    for (int i = 0; i < 19; i++) {
      for (int j = 0; j < 19; j++) {
        JLabel stone = new JLabel();
        stone.setBounds(32 * i, 32 * j, 32, 32);
        stone.setIcon(new ImageIcon("files/no-stone.png"));
        this.add(stone);
        stonesJLabels[i][j] = stone;
      }
    }
    //kropki orientacyjne na planszy
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        JLabel dot = new JLabel();
        dot.setBounds(16 + 91 + i * 32 * 6, 16 + 91 + j * 32 * 6, 11, 11);
        dot.setIcon(new ImageIcon("files/go-board-dot.png"));
        this.add(dot);
      }
    }
    //kwadraty planszy
    for (int i = 0; i < 18; i++) {
      for (int j = 0; j < 18; j++) {
        JLabel field = new JLabel();
        field.setBounds(16 + i * 32, 16 + j * 32, 32, 32);
        field.setIcon(new ImageIcon("files/go-board-field.png"));
        this.add(field);
      }
    }
    if(color.equals("Black")) {
      this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
              new ImageIcon("files/cursor-black.cur").getImage(),
              new Point(16, 16), "Stone Cursor"));
    }
    else if(color.equals("White")) {
      this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
              new ImageIcon("files/cursor-white.cur").getImage(),
              new Point(16, 16), "Stone Cursor"));
    }
    createStartingStones();
  }

  public int[][] getStones() {
    return this.stones;
  }

  public void setStones(int[][] stones) {
    this.stones = stones;
    updateGameBoard();
  }

  /* public JLabel[][] getStonesJLabels(){
     return this.stonesJLabels;
   }
   public void setStonesJLabels(JLabel[][] stonesJLabels){
     this.stonesJLabels = stonesJLabels;
   }*/
  protected void updateGameBoard() {
    for (int i = 0; i < 19; i++) {
      for (int j = 0; j < 19; j++) {
        switch (stones[i][j]) {
          case 1:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/white-stone.png"));
            break;
          case 2:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/black-stone.png"));
            break;
          default:
            stonesJLabels[i][j].setIcon(new ImageIcon("files/no-stone.png"));
            break;
        }
      }
    }
    this.repaint();
  }

  private void createStartingStones() {
    this.stones = new int[19][19];
    for (int i = 0; i < 19; i++) {
      for (int j = 0; j < 19; j++) {
        this.stones[i][j] = 0;
      }
    }
  }
}



