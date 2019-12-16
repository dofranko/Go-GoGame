package Go;

import GUI.FirstPhase.ClientGUI;
import Go.ServerClient.Client.Bot;

/**
 * Hello world!
 *
 */
public class LaunchClients
{
    public static void main( String[] args )
    {
       // System.out.println( "Hello World!" );
    	Bot bot = new Bot(19);
        ClientGUI gui = new ClientGUI(19);
        //ClientGUI gui2 = new ClientGUI(19);
        //Bot bot1 = new Bot(19);
       /* ClientGUI gui3 = new ClientGUI();
        ClientGUI gui4 = new ClientGUI();
        ClientGUI gui5 = new ClientGUI();
        ClientGUI gui6 = new ClientGUI();
        ClientGUI gui7 = new ClientGUI();
        ClientGUI gui8 = new ClientGUI();
        ClientGUI gui9 = new ClientGUI();
        ClientGUI gui10 = new ClientGUI(); // 10 NARAZ !!!
        */

    }
}
