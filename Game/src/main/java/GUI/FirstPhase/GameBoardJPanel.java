package GUI.FirstPhase;

import Go.GameMaker.Markers;

import javax.swing.*;
import java.awt.*;

/**
 * Panel planszy gry
 */
public class GameBoardJPanel extends JPanel {

  protected int[][] stones;
  protected JLabel[][] stonesJLabels;

  public GameBoardJPanel(String color, int size) {
    this.setLayout(null);
    this.setBounds(0, 0, 608, 608);
    this.setOpaque(true);
    this.setBackground(Color.GRAY);
    this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
    this.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
    //kolejność następnych komponentow jest wazna
    stonesJLabels = new JLabel[size][size];
    /**
     * Ustawienie jLabeli kamieni (biały, czarny, brak)
     */
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        JLabel stone = new JLabel();
        stone.setBounds(32 * i, 32 * j, 32, 32);
        stone.setIcon(new ImageIcon("files/no-stone.png"));
        this.add(stone);
        stonesJLabels[i][j] = stone;
      }
    }
    /**
     * Kropki orientacyjne na planszy
     */
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        JLabel dot = new JLabel();
        dot.setBounds(16 + 91 + i * 32 * 6, 16 + 91 + j * 32 * 6, 11, 11);
        dot.setIcon(new ImageIcon("files/go-board-dot.png"));
        this.add(dot);
      }
    }
    /**
     * Kwadraty pola planszy
     */
    for (int i = 0; i < size-1; i++) {
      for (int j = 0; j < size-1; j++) {
        JLabel field = new JLabel();
        field.setBounds(16 + i * 32, 16 + j * 32, 32, 32);
        field.setIcon(new ImageIcon("files/go-board-field.png"));
        this.add(field);
      }
    }
    /**
     * Ustawienie kursora
     */
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
    createStartingStones(size);
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

  /**
   * Aktualizacja planszy
   */
  protected void updateGameBoard() {
    for (int i = 0; i < stones[0].length; i++) {
      for (int j = 0; j < stones[0].length; j++) {
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

  /**
   * Wypełnienie podstawowej planszy zerami
   */
  private void createStartingStones(int size) {
    this.stones = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        this.stones[i][j] = 0;
      }
    }
  }
}



