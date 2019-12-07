package Go.ServerClient;

// Java implementation of  Server side
// It contains two classes : Server and ClientHandler
// Save file as Server.java

import Go.GameMaker.Markers;
import Go.GameMaker.TheGame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static Go.ServerClient.Server.chatOutputs;

// Server class
public class Server {
	public static Map<String, DataOutputStream> chatOutputs = new HashMap<>();

	public static void main(String[] args) throws IOException, InterruptedException {
		// Stworzenie servera socketa na porcie: 8523
		ServerSocket serverSocket = new ServerSocket(8523);
		ServerSocket chatServerSocket = new ServerSocket(8524);
		final TheGame gameServer = TheGame.getInstance();
		System.out.println("Server has started");
		// gameServer.setBoard(19);		
		// TODO na razie TheGame tworzy domyślnie planszę 19x19, w przyszłości należy mu przekazac rozmiar w addPlayer()

		//
		// Dołączanie kolejnych klientów
		while (true) {
			// częśc kodu jeśli ograniczamy iloś graczy
			Socket socket = null;
			Socket chatSocket = null;

			try {
				// Ackeptowanie nowych klientów
				socket = serverSocket.accept();
				chatSocket = chatServerSocket.accept();
				chatOutputs.put(socket.getPort()+"", new DataOutputStream(chatSocket.getOutputStream()));
				Thread chat = new ChatThread(chatSocket);
				chat.start();

				String result = gameServer.addPlayer(socket.getPort() + "");
				String[] resultSet = result.split(";");
				Markers color = Markers.EMPTY;
				if (resultSet[0].equals("Succes")) {
					if (resultSet[1].equals("White"))
						color = Markers.WHITE;
					else if (resultSet[1].equals("Black"))
						color = Markers.BLACK;
				} else
					color = Markers.EMPTY;

				System.out.println("A new client is connected : " + socket);
				System.out.println("port: " + socket.getPort());
				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

				System.out.println("Assigning new thread for client: " + socket.getPort());

				// create a new thread object
				Thread t = new ClientHandler(socket, dis, dos, color);

				// Invoking the start() method
				t.start();

			} catch (Exception e) {
				socket.close();
				e.printStackTrace();

			}
		}
	}
}

class ChatThread extends Thread {
	private Socket socket;
	private DataInputStream dis;

	public ChatThread(Socket socket){
		this.socket = socket;
		try {
			this.dis = new DataInputStream(socket.getInputStream());
		} catch(IOException ex){ex.printStackTrace();}
	}
	//funkcjonalnosc chatu
	@Override
	public void run() {
		while(true) {
			try {
				String received = dis.readUTF();
				String recipient = received.split(";")[0];
				String message = received.split(";")[1];
				if(message.equals("!dc")) {
					new DataOutputStream(chatOutputs.get(recipient)).writeUTF("!dc");
					break;
				}

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

// ClientHandler class
class ClientHandler extends Thread {

	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	final TheGame gameServer;
	private String color;
	private String playerID;

	// Constructor
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, Markers color) {
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		this.gameServer = TheGame.getInstance();
		this.color = color.asString();
		this.playerID = s.getPort() + "";
	}

	@Override
	public void run() {
		String received;
		String toReturn;
		boolean exit = false;
		try {
			// port jest IdGracza
			dos.writeUTF(playerID);
			dos.writeUTF(color);
			if(color.equals("Empty"))
				throw new IOException();
		} catch (IOException ex) {
		}
		while (!exit) {
			try {

				// receive the answer from client
				received = dis.readUTF();
				toReturn = "UnknownCommand";
				switch (received) {
					case "Exit": {
						System.out.println("Client " + this.s + " sends exit...");
						System.out.println("Closing this connection.");
						gameServer.exit(playerID);
						this.s.close();
						System.out.println("Connection closed");
						exit = true;
						continue;
					}
					case "WhoseMove": {
						toReturn = gameServer.whoseMove(this.playerID);
						break;
					}
					case "FindGame": {
						toReturn = gameServer.addPlayer(this.playerID);
						break;
					}
					case "GiveUp": {
						gameServer.exit(this.playerID);
						continue;
					}
					case "Pass": {
						gameServer.skip(this.playerID);
						continue;
					}
					case "MakeMove": {
						received = dis.readUTF();
						// tutaj jeśli jest ruch gracza
						toReturn = gameServer.makeMove(this.playerID + "," + received);
						break;
					}
					case "PickDeadStones": {
						received = dis.readUTF();
						gameServer.pickDeadStones(this.playerID + "," + received);
						continue;
					}
					case "PickTerritory": {
						received = dis.readUTF();
						gameServer.pickTerritory(this.playerID + "," + received);
						continue;
					}
					case "GetEnemyId": {
						toReturn = gameServer.getEnemyID(this.playerID);
					}
				}

				dos.writeUTF(toReturn);
//				if (received.equals("Exit")) {
//					System.out.println("Client " + this.s + " sends exit...");
//					System.out.println("Closing this connection.");
//					gameServer.exit(playerID);
//					this.s.close();
//					System.out.println("Connection closed");
//					break;
//				} else if (received.equals("WhoseMove")) {
//					toReturn = gameServer.whoseMove(this.playerID);
//				} else if (received.equals("FindGame")) {
//					toReturn = gameServer.addPlayer(this.playerID);
//				} else if (received.equals("GiveUp")) {
//					gameServer.exit(this.playerID);
//					continue;
//				} else if (received.equals("Pass")) {
//					gameServer.skip(this.playerID);
//					continue;
//				} else if (received.equals("MakeMove")) {
//					received = dis.readUTF();
//					// tutaj jeśli jest ruch gracza
//					toReturn = gameServer.makeMove(this.playerID + "," + received);
//				} else if (received.equals("PickDeadStones")) {
//					received = dis.readUTF();
//					toReturn = gameServer.pickDeadStones(this.playerID + "," + received);
//				} else if (received.equals("PickTerritory")) {
//					received = dis.readUTF();
//					toReturn = gameServer.pickTerritory(this.playerID + "," + received);
//				}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Ktoś się rozłączył lub wysątpił błąd. Koniec rozgrywki");
				break;
			}
		}
		try {
			// closing resources
			this.dis.close();
			this.dos.close();


		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
