package Go.ServerClient.Client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Klasa clienta. Logika clienta.
 */
public abstract class Client {

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	private Socket chatSocket;

	private String myPlayerId;

	private String myColor;
	private String myPoints = "0";
	protected String received = "";

	private boolean isItMyTurn = false;
	private boolean didIPass = false;
	/**
	 * Wątek, który wyczekuje ruchu przeciwnika odpytując serwer o aktualizację danych.
	 */
	private Thread waitingForTurnThread = createWaitingForTurnThread();

	public Client() {
		String playerIdToSet = "";
		try {
			// Ip lokalne hosta
			InetAddress ip = InetAddress.getByName("localhost");

			//Zamiast ip mozna podac ip sieci lokalnej do gry między urządzeniami

			/**
			 * Połączenia z socketami
			 */
			socket = new Socket(ip, 8523);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());

			chatSocket = new Socket(ip, 8524);


			/**
			 * Odczytanie id gracza od serwera
			 */
			received = dis.readUTF();
			System.out.println("Moje id: " + received);
			playerIdToSet = received;

			/**
			 * Odczytanie koloru gracza od serwera
			 */
			received = dis.readUTF();
			System.out.println("Mój color: " + received);
			myColor = received;
			if (myColor.equals("Black"))
				isItMyTurn = true;
			else if (myColor.equals("Empty"))
				disconnect();
		} catch (Exception e) { e.printStackTrace(); }
		/**
		 * Przypisanie id gracza
		 */
		myPlayerId = playerIdToSet;
	}

	public String getMyPlayerId() {
		return myPlayerId;
	}

	/* Stary setter.
	 * public void setReceived(String received){ this.received = received; }
	 */

	/**
	 *
	 * @return zwraca ostatnią otrzymaną wiadomość
	 */
	public String getReceived() {
		return received;
	}

	/**
	 *
	 * @return zwraca czy jest to ruch tego gracza
	 */
	public boolean getIsItMyTurn() {
		return isItMyTurn;
	}

	/**
	 * Metoda rozłączająca klienta z serwerem i kończąca pracę
	 */
	public void sendExit() {
		try {
			received = "Exit";
			dos.writeUTF("Exit");

		} catch (Exception e) {
			e.printStackTrace();
		}
		disconnect();
		System.out.println("Connection closed");
	}

	/**
	 * Metoda odpowiadająca za odpytanie serwera czyj ruch oraz stan planszy
	 * @return odpowiedź serwera
	 * @throws IOException
	 */
	public String sendWhoseMove() throws IOException { // tutaj dopasuj sobie komunikaty zwracane przez TheGame
		dos.writeUTF("WhoseMove");
		received = dis.readUTF();
		String toReturn = received;
		String colorMove = toReturn.split(";")[0];
		System.out.println(received);

		/**
		 * Jeśli właśnie następuje tura tego gracza.
		 */
		if (colorMove.equals(this.myColor)) {
			isItMyTurn = true;
			didIPass = false;
			if (toReturn.length() > colorMove.length() + 5 && colorMove.equals(myColor)) {
				updateGameBoard(toReturn.substring(colorMove.length() + 1));
				updateStatusLabel("YrMove");
			}
		}
		/**
		 * Gdy ktoś spasował
		 */
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

	/**
	 * Gracz wysyła swój ruch
	 * @param move (x,y) punkt ruchu
	 */
	public void sendMakeMove(String move) {
		if (isItMyTurn) {
			try {
				dos.writeUTF("MakeMove");
				dos.writeUTF(move);
				received = dis.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(received); //Tylko do debugowania TODO
			if (!received.equals("NotYrMove") && !received.equals("IllegalMove")) {
				isItMyTurn = false;
				updateStatusLabel("MoveMade");

				/**
				 * Pierwsze kilka znaków to punkty gracza
				 */
				this.myPoints = received.split(";")[0];
				updatePointsLabel();
				String arrayOfStonesToUpdate = received.substring(myPoints.length() + 1);
				updateGameBoard(arrayOfStonesToUpdate);

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

	/**
	 * Gracz wysyła, że pasuje
	 */
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

	/**
	 * Gracz wysyła, że się poddaje
	 */
	public void sendGiveUp() {
		try {
			dos.writeUTF("GiveUp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateStatusLabel("YouLose");
	}

	/**
	 * Metoda rozłączająca clienta i chat
	 */
	public void disconnect() {
		try {
			dis.close();
			dos.close();
			socket.close();
		} catch (Exception e) { e.printStackTrace(); }
		try {new DataOutputStream(chatSocket.getOutputStream()).writeUTF(myPlayerId+";"+"!dc");}
		catch(Exception e){e.printStackTrace();}
	}

	/**
	 * Metoda do odświeżenia planszy
	 * @param stonesInString
	 */
	public abstract void updateGameBoard(String stonesInString);

	/**
	 *
	 * @return kolor gracza
	 */
	public String getMyColor() {
		return this.myColor;
	}

	/**
	 * Włączenie wątku odpytującego serwera o aktualizację danych
	 */
	protected void startWaitingForTurnThread() {
		if (!waitingForTurnThread.isAlive()) {
			waitingForTurnThread = createWaitingForTurnThread();
			waitingForTurnThread.start();
		}
	}

	/**
	 * Metoda konwertująca String na tablicę int kamieni
	 * @param stonesInString
	 * @return tablicę int[][] kamieni
	 */
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

	/**
	 * Stworzenie wątku odpytującego serwer o aktualizację danych
	 * @return
	 */
	private Thread createWaitingForTurnThread() {
		return new Thread() {
			public void run() {
				String decision;
				String whoseMove;
				do {
					try { sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
					try {
						if(socket.isClosed())
							throw new IOException();
						whoseMove = sendWhoseMove();
						decision = whoseMove.split(";")[0];
					} catch (IOException e) {
						e.printStackTrace();
						disconnect();
						break;
					}
					System.out.println(whoseMove);
					System.out.println("Watki:" + Thread.activeCount());//Debugowanie TODO


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

	/**
	 * Rozłączenie się z socketami i streamami, żeby nie przeszkadzać w trakcie ostatniego etapu
	 * w nowym oknie
	 */
	protected void startFinalPhase(){
		this.socket = null;
		this.dis = null;
		this.dos = null;
	}

	public String getMyPoints() {
		return myPoints;
	}

	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * Ponowne połączenie się z socketami w przypadku wznowienia rozgrywki
	 * @param socket
	 *
	 */
	protected void connect(Socket socket) {
		this.socket = socket;
		try {
			this.dos = new DataOutputStream(socket.getOutputStream());
			this.dis = new DataInputStream(socket.getInputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	protected Socket getChatSocket(){
		return this.chatSocket;
	}
	public String getEnemyPlayerId(){
		String enemyId = "";
		try {
			dos.writeUTF("GetEnemyId");
			 enemyId = dis.readUTF();
		}catch (IOException ex){ex.printStackTrace();}
		return enemyId;
	}
}
