package Go.ServerClient.Server;

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

import static Go.ServerClient.Server.Server.chatOutputs;

// Server class
public class Server {
	/**
	 * Mapa przetrzymująca id graczy oraz odpowiadające im outputStreamy do chatów
	 */
	public static Map<String, DataOutputStream> chatOutputs = new HashMap<>();

	public static void main(String[] args) throws IOException, InterruptedException {
		/**
		 * Stworzenie server socketa na porcie: 8523
 		 */
		ServerSocket serverSocket = new ServerSocket(8523);
		/**
		 * Stworzenie chat server socketa na porcie 8524
 		 */
		ServerSocket chatServerSocket = new ServerSocket(8524);
		final TheGame gameServer = TheGame.getInstance();

		System.out.println("Server has started");
		// gameServer.setBoard(19);		
		// TODO na razie TheGame tworzy domyślnie planszę 19x19, w przyszłości należy mu przekazac rozmiar w addPlayer()

		//
		/**
		 * Tutaj server oczekuje cały czas na nowych klientów, których ackeptuje.
		 */
		while (true) {
			// częśc kodu jeśli ograniczamy iloś graczy
			Socket socket = null;
			Socket chatSocket = null;

			try {
				// Ackeptowanie nowych klientów i chatów klientów
				socket = serverSocket.accept();

				chatSocket = chatServerSocket.accept();
				//dodanie nowego czatu wraz z id do mapy
				chatOutputs.put(socket.getPort()+"", new DataOutputStream(chatSocket.getOutputStream()));
				Thread chat = new ServerChatThread(chatSocket);
				chat.start();
				System.out.println("A new client is connected : " + socket);
				System.out.println("port: " + socket.getPort());
				// Pobranie input i output streamu
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

				System.out.println("Assigning new thread for client: " + socket.getPort());

				int boardSize = Integer.parseInt(dis.readUTF());
				//Dodanie nowego gracza i nadanie mu koloru
				String result = gameServer.addPlayer(socket.getPort() + "", boardSize);
				String[] resultSet = result.split(";");
				Markers color = Markers.EMPTY;
				if (resultSet[0].equals("Succes")) {
					if (resultSet[1].equals("White"))
						color = Markers.WHITE;
					else if (resultSet[1].equals("Black"))
						color = Markers.BLACK;
				} else
					color = Markers.EMPTY;



				// Stworzenie nowego wątku osbługującego klienta
				Thread t = new ClientHandler(socket, dis, dos, color);
				t.start();

			} catch (Exception e) {
				socket.close();
				chatSocket.close();
				e.printStackTrace();
			}
		}
	}
}

