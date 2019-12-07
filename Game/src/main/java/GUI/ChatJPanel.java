package GUI;

import javax.swing.*;
import java.awt.*;

public class ChatJPanel extends JPanel {

  private JTextArea chatJTextArea;
  private JScrollPane jScrollPane;
  private JButton sendMessageJButton;
  private JTextField messageJTextField;

  public ChatJPanel(){
    this.setLayout(null);
    this.setBackground(Color.BLUE);

    this.sendMessageJButton = new JButton("Wyślij");
    this.sendMessageJButton.setSize(100,30);
    this.add(sendMessageJButton);

    chatJTextArea = new JTextArea("Witaj w czacie!",1,10);
   // chatJTextArea.setBounds(0,0,0,0);
    chatJTextArea.setBackground(Color.RED);
    chatJTextArea.setEnabled(false);
    chatJTextArea.setDisabledTextColor(Color.BLACK);

    jScrollPane = new JScrollPane(chatJTextArea);
    jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.add(jScrollPane);

    this.messageJTextField = new JTextField();
    this.messageJTextField.setSize(220, 50);
    this.add(messageJTextField);
    this.setSize(220,200);
  }
  public void appendMessage(String message){ this.chatJTextArea.append(message); }
  public JButton getSendMessageJButton() { return this.sendMessageJButton;}
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
    this.jScrollPane.setBounds(0,0,this.getWidth()-7,this.getHeight()-sendMessageJButton.getHeight()-messageJTextField.getHeight());
  }
}
