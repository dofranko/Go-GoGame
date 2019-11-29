package GUI;

import Go.ServerClient.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClienGUI extends Client {

  private JLabel[][] pionki;

  public ClienGUI(){
    initialize();
  }
  private void initialize(){
    final JFrame jFrame = new JFrame();
    jFrame.setLayout(null);
    JPanel mainJPanel = createMainBoard();
    mainJPanel.add(createGameBoard());

    jFrame.add(mainJPanel);
    jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        sendAndReceiveInformation("playerIdMove");
        System.out.println(received);
        try {sendAndReceiveInformation("Exit");}
        catch (Exception ex){}
        jFrame.dispose();
      }
    });
    jFrame.pack();
    Insets insets = jFrame.getInsets();
    jFrame.setSize(new Dimension(insets.left + insets.right + 800,
            insets.top + insets.bottom + 650));
    jFrame.setVisible(true);
    jFrame.setResizable(false);
  }
  private JPanel createGameBoard(){
    JPanel gameBoardJPanel = new JPanel();
    gameBoardJPanel.setLayout(null);
    gameBoardJPanel.setBounds(20,20,576,576);
    gameBoardJPanel.setOpaque(true);
    gameBoardJPanel.setBackground(Color.GREEN);
    gameBoardJPanel.setMinimumSize(new Dimension(gameBoardJPanel.getWidth(),gameBoardJPanel.getHeight()));
    gameBoardJPanel.setPreferredSize(new Dimension(gameBoardJPanel.getWidth(),gameBoardJPanel.getHeight()));
    for(int i=0; i<3; i++) {
      for(int j=0; j<3; j++) {
        JLabel dot = new JLabel();
        dot.setBounds(91+i*32*6,91+j*32*6,11, 11);
        dot.setIcon(new ImageIcon("files/go-board-dot.png"));
        gameBoardJPanel.add(dot);
      }
    }
    for(int i=0; i<18; i++) {
      for(int j=0; j<18; j++) {
        JLabel field = new JLabel();
        field.setBounds(i*32,j*32,32, 32);
        field.setIcon(new ImageIcon("files/go-board-field.png"));
        gameBoardJPanel.add(field);
      }
    }
    return gameBoardJPanel;
  }
  private JPanel createMainBoard() {
    JPanel mainJPanel = new JPanel();
    mainJPanel.setLayout(null);
    mainJPanel.setBounds(0,0,800,800);
    mainJPanel.setOpaque(true);
    mainJPanel.setBackground(Color.RED);
    mainJPanel.setMinimumSize(new Dimension(mainJPanel.getWidth(),mainJPanel.getHeight()));
    mainJPanel.setPreferredSize(new Dimension(mainJPanel.getWidth(),mainJPanel.getHeight()));
    return  mainJPanel;
  }
}
