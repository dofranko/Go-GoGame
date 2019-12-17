package GUI;

import Go.ServerClient.Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Panel chatu gracza
 */
public class ChatJPanel extends JPanel {

  private JTextArea chatJTextArea;
  private JScrollPane jScrollPane;
  private JButton sendMessageJButton;
  private JTextField messageJTextField;
  private Thread chatThread;
  private Socket chatSocket;
  private DataOutputStream chatdos;
  private DataInputStream chatdis;
  private String enemyPlayerId = "";
  private String myPlayerId = "";
  private Client parentFrame;

  /**
   * Stworzenie wyglądu panelu chatu
   */
  public ChatJPanel(Socket chatSocket, Client parentFrame, String playerID){
    this.chatSocket = chatSocket;
    this.myPlayerId = playerID;
    this.parentFrame = parentFrame;
    try {
      this.chatdis = new DataInputStream(chatSocket.getInputStream());
      this.chatdos = new DataOutputStream(chatSocket.getOutputStream());
    }catch (IOException ex){ex.printStackTrace();}
    this.setLayout(null);
    this.setBackground(new Color(100, 99, 98, 177));

    this.sendMessageJButton = new JButton("Wyślij");
    this.sendMessageJButton.setSize(100,30);
    this.add(sendMessageJButton);

    chatJTextArea = new JTextArea("Witaj w czacie!",1,10);
    chatJTextArea.setBackground(new Color(255, 255, 231));
    chatJTextArea.setEnabled(false);
    chatJTextArea.setDisabledTextColor(Color.BLACK);
    chatJTextArea.setLineWrap(true);
    chatJTextArea.setWrapStyleWord(true);

    jScrollPane = new JScrollPane(chatJTextArea);
    jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.add(jScrollPane);

    this.messageJTextField = new JTextField();
    this.messageJTextField.setSize(220, 30);
    this.add(messageJTextField);
    this.setSize(220,200);

    this.sendMessageJButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String message = getMessage();
        if(!message.equals("")) {
          sendChatMessage(message);
        }
      }
    });
    startChatThread();
  }

  /**
   * Pobranie wiadomości z textfield
   * @return
   */
  public String getMessage() {
    String message = this.messageJTextField.getText();
    this.messageJTextField.setText("");
    return message;
  }

  @Override
  public void setSize(int width, int height) {
    super.setSize(width, height);
    this.sendMessageJButton.setLocation(2, this.getHeight()-sendMessageJButton.getHeight());
    this.messageJTextField.setLocation(0, sendMessageJButton.getY()-messageJTextField.getHeight());
    this.messageJTextField.setSize(this.getWidth(), messageJTextField.getHeight());
    this.jScrollPane.setBounds(0,0,this.getWidth()-5,this.getHeight()-sendMessageJButton.getHeight()-messageJTextField.getHeight());
  }

  /**
   * Włączenie wątku odpowiadającego za przyjmowanie wiadomości
   */
  private void startChatThread(){
    chatThread = new Thread() {
      @Override
      public void run() {
        String message = "";
        while(!message.equals("Exit")){
          try {
            message = chatdis.readUTF();
          } catch (IOException ex){ex.printStackTrace(); break;}
          if(message.equals("!dc")) {
            updateChatArea("!dc");
            break;
          }
          updateChatArea("\nEnemy: " + message);
        }
      }
    };
    chatThread.start();
  }

  /**
   * Aktualizacja textArea
   * @param message
   */
  private void updateChatArea(String message){
    if(message.equals("!dc")) {
      this.chatJTextArea.append("DISCONNECTED\n");
      this.sendMessageJButton.setEnabled(false);
    }
    this.chatJTextArea.append(message);
  }

  /**
   * Wysłanie wiadomości
   * @param message wiadomość
   */
  public void sendChatMessage(String message) {
    try {
      if (enemyPlayerId.isEmpty()) {
        enemyPlayerId = parentFrame.getEnemyPlayerId();
      }
      if(message.equals("!dc"))
        chatdos.writeUTF(enemyPlayerId+";"+"!dc");
      else if (message.equals("!dctemporary"))
        chatdos.writeUTF(myPlayerId+";"+"!dctemporary");
      else
        chatdos.writeUTF(enemyPlayerId + ";" + message);
      if(!enemyPlayerId.equals("NoSuchPlayer"))
       updateChatArea("\nMe: " + message);
      else{
        updateChatArea("\nPrzeciwnik wciąż nie dołączył");
        enemyPlayerId = "";
      }

    } catch (IOException ex) { ex.printStackTrace(); }
  }

  public String getChatJTextAreaText(){
    return this.chatJTextArea.getText();
  }
}
