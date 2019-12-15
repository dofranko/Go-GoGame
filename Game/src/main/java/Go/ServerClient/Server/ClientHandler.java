package Go.ServerClient.Server;

import Go.GameMaker.Markers;
import Go.GameMaker.TheGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Klasa, która jest "pochodną" servera, która obsługuje każdego klienta z osobna.
 */
class ClientHandler extends Thread {

	final private DataInputStream dis;
	final private DataOutputStream dos;
	final private Socket s;
	final private TheGame gameServer;
	private String color;
	/**
	 * Port socketa jest playerID
	 */
	private String playerID;


	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Markers color) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		this.gameServer = TheGame.getInstance();
		this.color = color.asString();
		this.playerID = s.getPort() + "";
	}

	/**
	 * Cała magia komunikacji servera (clienthandlera) z Grą
	 */
	@Override
	public void run() {
		String received;
		String toReturn;
		boolean exit = false;
		/**
		 * Wysłanie do gracza jego Id i koloru
		 */
		try {
			dos.writeUTF(playerID);
			dos.writeUTF(color);
			if(color.equals("Empty"))
				throw new IOException();
		} catch (IOException ex) { ex.printStackTrace();}
		while (!exit) {
			try {
				/**
				 * Odbieranie wiadomości od clienta
				 */
				received = dis.readUTF();
				toReturn = "UnknownCommand";
				/**
				 * Obsługa gry przez server w zależności, co klient wykonał
				 */
				switch (received) {
					//Rozłączenie klienta
					case "Exit": {
						System.out.println("Client " + this.s + " sends exit. !DC");
						gameServer.exit(playerID);
						this.s.close();
						System.out.println("Connection closed");
						exit = true;
						continue;
					}
					//Klient pyta grę, o stan gry (czyj ruch oraz wygląd planszy)
					case "WhoseMove": {
						toReturn = gameServer.getGameState(this.playerID);
						break;
					}
					//Szukanie nowej gry przez gracza
					case "FindGame": {
						toReturn = gameServer.addPlayer(this.playerID);
						break;
					}
					//Gracz się poddaje
					case "GiveUp": {
						gameServer.exit(this.playerID);
						continue;
					}
					//Gracz pasuje
					case "Pass": {
						gameServer.skip(this.playerID);
						continue;
					}
					//Gracz robi ruch
					case "MakeMove": {
						received = dis.readUTF();
						// tutaj jeśli jest ruch gracza
						toReturn = gameServer.makeMove(this.playerID + "," + received);
						break;
					}
					//Gracz chce otrzymać id przeciwnika
					case "GetEnemyId": {
						toReturn = gameServer.getEnemyID(this.playerID);
						break;
					}
					///FINAL PHASE

					//Gracz wybrał martwy kamień w fazie końcowej
					case "PickDeadStones": {
						received = dis.readUTF();
						gameServer.pickDeadStones(this.playerID + "," + received);
						continue;
					}
					//Gracz wybrał terytorium w fazie końcowej
					case "PickTerritory": {
						received = dis.readUTF();
						gameServer.pickTerritory(this.playerID + "," + received);
						continue;
					}
					//Gracz zaakceptował aktualny wybór obu graczy
					case "AcceptStage": {
						//received = dis.readUTF();
						gameServer.acceptStage(this.playerID);
						continue;
					}
					case "DeclineStage": { // Baby Shark TODO DO DO DO...
						gameServer.cancelVote(this.playerID);
						continue;
					}
					//Gracz odświeża mapę w końcowej fazie
					case "MapRefresh":
						toReturn = gameServer.getGameState(this.playerID); // ta sama metoda so przy whoseMove
						break;
				}
				//Odesłanie odpowiedzi do gracza
				dos.writeUTF(toReturn);

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Ktoś się rozłączył lub wysątpił błąd. Koniec rozgrywki");
				break;
			}
		}
		/**
		 * Zamykanie wszystkiego, gdy wątek kończy pracę
		 */
		try {
			this.dis.close();
			this.dos.close();
		} catch (IOException e) { }

	}
}
