package Go.ServerClient;

// Java implementation for a client
// Save file as Client.java

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

// Client class
public abstract class Client
{
  DataInputStream dis;
  DataOutputStream dos;
  Socket s;
  protected String received = "";

  public Client(){
    try {
      // getting localhost ip
      InetAddress ip = InetAddress.getByName("localhost");
      // establish the connection with server port 5056
      s = new Socket(ip, 5056);

      // obtaining input and out streams
      dis = new DataInputStream(s.getInputStream());
      dos = new DataOutputStream(s.getOutputStream());

      // the following loop performs the exchange of
      // information between client and client handler
      System.out.println(dis.readUTF());

      // If client sends exit,close this connection
      // and then break from the while loop


      // printing date or time as requested by client

      // closing resources
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
    public void sendAndReceiveInformation(String toSend) {
      try {
        dos.writeUTF(toSend);
        if (toSend.equals("Exit")) {
          s.close();
          System.out.println("Connection closed");
          endGame();
        }
        else {
          received = dis.readUTF();
          System.out.println(received);
        }

      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    public void endGame(){
      try {
        dis.close();
        dos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

}
