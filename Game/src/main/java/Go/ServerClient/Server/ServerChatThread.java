package Go.ServerClient.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static Go.ServerClient.Server.Server.chatOutputs;

/**
 * Klasa odpowiadająca za przyjmowanie wiadomości na chacie od klienta i wysyłąjąca ją do odbiorcy.
 */
class ServerChatThread extends Thread {
	private DataInputStream dis;
	private DataOutputStream dos;
	/**
	 * Pobranie input z socketa chatu, aby móc otrzymywać wiadomości od gracza
	 * @param socket Socket chatu.
	 */
	public ServerChatThread(Socket socket){
		try {
			this.dis = new DataInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
		} catch(IOException ex){ex.printStackTrace();}
	}
	//funkcjonalnosc chatu
	@Override
	public void run() {
		while(true) {
			try {
				/**
				 * Przyjmowanie wiadomości od gracza
				 */
				String received = dis.readUTF();
				String recipient = received.split(";")[0];
				String message = received.split(";")[1];
				/**
				 * Rozłączenie tego wątku, gdy gracz wyłącza klienta
				 */
				if(message.equals("!dc")) {
					new DataOutputStream(chatOutputs.get(recipient)).writeUTF("!dc");
					dos.writeUTF("!dc");
					break;
				}
				/**
				 * Rozłączenie chatu tylko w cliencie
				 */
				else if(message.equals("!dctemporary")) {
					new DataOutputStream(chatOutputs.get(recipient)).writeUTF("!dc");
					continue;
				}
				/**
				 * Wysłanie wiadomości do gracza poprzez mapę (chatOutputs) id i outputStreamów w klasie Server
				 */
				DataOutputStream recipientsStream = chatOutputs.get(recipient);
				if (recipientsStream != null)
					recipientsStream.writeUTF(message);
			} catch (IOException ex) {
				ex.printStackTrace();
				break;
			}
		}
	}
}
