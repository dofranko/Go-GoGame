package Go.ServerClient;

// Java implementation for a client
// Save file as Client.java

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

// Client class
public abstract class Client {
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket socket;
	private Socket chatSocket;
	private DataOutputStream chatos;
	private DataInputStream chatis;
	protected String received = "";
	private final String myPlayerId;
	private String enemyPlayerId = "";
	private boolean isItMyTurn = false;
	private String myColor;
	private String myPoints = "0";
	private boolean didIPass = false;
	Thread waitingForTurnThread = createWaitingForTurnThread();

	// private Thread waitForMove = createWaitingForTurnThread();

	public Client() {
		String playerIdToSet = "";
		try {
			// Ip lokalne hosta
			InetAddress ip = InetAddress.getByName("localhost");

			/*
			 * InetAddress inetAddress = InetAddress.getLocalHost();
			 * inetAddress.getHostAddress()); inetAddress.getHostName());
			 */ // crossdevice

			// połączenie się na porcie: 8523
			socket = new Socket(ip, 8523);
			chatSocket = new Socket(ip, 8524);
			chatos = new DataOutputStream(chatSocket.getOutputStream());
			chatis = new DataInputStream(chatSocket.getInputStream());
			// pobranie DataInputStream i DataOutputSteam do komunikacji z serwerem(socketem)
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			// pobranie DataInputStream i DataOutputSteam do komunikacji z
			// serwerem(socketem)
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			// odczytanie swojego id gracza ( = port socketa)
			received = dis.readUTF();
			System.out.println("Moje id: " + received);
			playerIdToSet = received;

			// odczytanie koloru gracza
			received = dis.readUTF();
			System.out.println("Mój color: " + received);
			myColor = received;
			if (myColor.equals("Black"))
				isItMyTurn = true;
			// sendAndReceiveInformation("WhoseMove");
			else if (myColor.equals("Empty"))
				disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// nie mozna przypisać w try catch
		myPlayerId = playerIdToSet;
	}

	public String getMyPlayerId() {
		return myPlayerId;
	}

	/*
	 * public void setReceived(String received){ this.received = received; }
	 */
	public String getReceived() {
		return received;
	}

	public boolean getIsItMyTurn() {
		return isItMyTurn;
	}

	public void sendExit() {
		try {
			dos.writeUTF("Exit");
		} catch (IOException e) {
			e.printStackTrace();
		}
		disconnect();
		System.out.println("Connection closed");
	}

	public String sendWhoseMove() throws IOException { // tutaj dopasuj sobie komunikaty zwracane przez TheGame
		dos.writeUTF("WhoseMove");
		received = dis.readUTF();
		String toReturn = received;
		String colorMove = toReturn.split(";")[0];
		System.out.println(received);

		if (colorMove.equals(this.myColor)) { //tura tego gracza
			isItMyTurn = true;
			didIPass = false;
			if (toReturn.length() > colorMove.length() + 5 && colorMove.equals(myColor)) {
				updateGameBoard(toReturn.substring(colorMove.length() + 1));
				updateStatusLabel("YrMove");
			}
		}
		//przeciwnik spasował
		switch (colorMove){
			case "BlackPassed":
			case "WhitePassed":
				if(!didIPass){
					isItMyTurn = true;
					updateStatusLabel("EnemyPassed");
				}
				else
					updateStatusLabel("YouPassed");
				break;
			case "BothPassed":
				updateStatusLabel("BothPassed");
				break;
			case "Empty":
				updateStatusLabel("EnemyGaveUp");
				break;
			case "BlackWins":
			case "WhiteWins":
				if(colorMove.contains(this.myColor)) {
					updateStatusLabel("YouWin");
					isItMyTurn = false;
				}
				else
					updateStatusLabel("YouLose");
				break;
		}

		return toReturn;
	}

	public void sendMakeMove(String move) {
		if (isItMyTurn) {
			try {
				dos.writeUTF("MakeMove");
				dos.writeUTF(move);
				received = dis.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println(received);
			// warunki rozdzieli się potem na labele
			if (!received.equals("NotYrMove") && !received.equals("IllegalMove")) {
				isItMyTurn = false;
				updateStatusLabel("MoveMade");
				// pierwsze kilka znakó to punkty gracza
				this.myPoints = received.split(";")[0];
				updatePointsLabel();
				String arrayOfStonesToUpdate = received.substring(myPoints.length() + 1);
				updateGameBoard(arrayOfStonesToUpdate);
				// stworzenie i urchomienie wątku czekającego na turę gracza
				waitingForTurnThread = createWaitingForTurnThread();
				startWaitingForTurnThread();
			} else if (received.equals("NotYrMove")) {
				this.isItMyTurn = false;
				updateStatusLabel("NotYrMove");
				System.out.println(received);
			} else if (received.equals("IllegalMove"))
				updateStatusLabel("IllegalMove");
		} else
			updateStatusLabel("NotYrMove");
	}

	public void sendPass() {
		if(isItMyTurn) {
			try {
				dos.writeUTF("Pass");
				didIPass = true;
				isItMyTurn = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
			startWaitingForTurnThread();
		}
		else
			updateStatusLabel("NotYrMove");
	}

	public void sendGiveUp() {
		try {
			dos.writeUTF("GiveUp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendChatMessage(String message) {
		try {
			if (enemyPlayerId.isEmpty()) {
				dos.writeUTF("GetEnemyId");
				enemyPlayerId = dis.readUTF();
			}
			if(message.equals("!dc"))
				chatos.writeUTF(myPlayerId+";"+"!dc");
			else
				chatos.writeUTF(enemyPlayerId + ";" + message);


		} catch (IOException ex) { ex.printStackTrace(); }
	}

	public void disconnect() {
		try {
			chatos.writeUTF(myPlayerId+";"+"!dc");
			dis.close();
			dos.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void updateGameBoard(String stonesInString);

	public String getMyColor() {
		return this.myColor;
	}

	protected void startWaitingForTurnThread() {
		if (!waitingForTurnThread.isAlive()) {
			waitingForTurnThread = createWaitingForTurnThread();
			waitingForTurnThread.start();
		}
	}

	protected int[][] convertStonesToIntFromString(String stonesInString) {
		int[][] stones;
		String[] columns = stonesInString.split(";");
		stones = new int[columns.length][columns.length];
		int i = 0;
		int j = 0;
		for (String column : columns) {
			String[] fields = column.split(",");
			for (String field : fields) {
				stones[i][j] = Integer.parseInt(field);
				j++;
			}
			j = 0;
			i++;
		}
		return stones;
	}

	private Thread createWaitingForTurnThread() {
		return new Thread() {
			public void run() {
				String decision = getMyColor();
				String whoseMove = "";
				do {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					try {
						whoseMove = sendWhoseMove();
						decision = whoseMove.split(";")[0];
					} catch (IOException e) {
						e.printStackTrace();
						disconnect();
						break;
					}
					System.out.println(whoseMove);
					System.out.println("Watki:" + Thread.activeCount());


					if (decision.equals("BothPassed"))
						break;
					//przeciwnik pasuje
					if ((decision.equals("BlackPassed") || decision.equals("WhitePassed")) && didIPass == false)
						break;
					//przeciwnik się poddał
					if(decision.equals("WhiteWins") || decision.equals("BlackWins"))
						break;

				} while (!isItMyTurn);

			}
		};
	}

	protected abstract void updateStatusLabel(String info);

	protected abstract void updatePointsLabel();

	protected void startFinalPhase(){
		this.socket = null;
		this.dis = null;
		this.dos = null;
		this.chatos = null;
		this.chatis = null;
	}

	public String getMyPoints() {
		return myPoints;
	}

	public Socket getSocket() {
		return this.socket;
	}

	protected void connect(Socket socket, Socket chatSocket) {
		this.socket = socket;
		this.chatSocket = chatSocket;
		try {
			this.dos = new DataOutputStream(socket.getOutputStream());
			this.dis = new DataInputStream(socket.getInputStream());
			this.chatos = new DataOutputStream(chatSocket.getOutputStream());
			this.chatis = new DataInputStream(chatSocket.getInputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	protected Socket getChatSocket(){
		return this.chatSocket;
	}
	protected DataInputStream getChatis(){
		return this.chatis;
	}
}
