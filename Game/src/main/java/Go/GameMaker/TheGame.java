package Go.GameMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//Na razie obsługuje jeden mecz, ale bedzie opcja wielu
public class TheGame {

	private static final Object LOCK = new Object();
	private static volatile TheGame instance;

	// tablica plansz
	private Board[] boards;

	// licznik plansz
	private int boardCounter;

	// licznik graczy
	private int playerCounter;

	// mapa parująca ID clienta z jego ID w TheGame
	private Map<String, Integer> players;

	// mapa parująca ID clienta z jego kolorem
	private Map<String, Markers> colors;

	// mapa parująca graczy ze sobą
	private Map<String, String> playerPairs;

	// mapa parująca graczy z ID planszy
	private Map<String, Integer> boardsID;

	// tablica punktacji poszczegolnych graczy
	private int[] points;

	// tablica kontrolujaca czyj jest ruch w kazdej planszy
	private Markers[] whoseMove;

	// tablica kontrolujaca czy w danej grze ktos spasowal
	private boolean[] playerAlreadySkipped;

	private TheGame() { // inicializacja
		players = new HashMap<String, Integer>();
		colors = new HashMap<String, Markers>();
		playerPairs = new HashMap<String, String>();
		boardsID = new HashMap<String, Integer>();
		boards = new Board[5];
		points = new int[10];
		playerCounter = 0;
		boardCounter = 0;
		whoseMove = new Markers[5];
		playerAlreadySkipped = new boolean[5];

	}

	public synchronized static TheGame getInstance() { // double checker metoda statyczna na zwracanie singletona
		TheGame result = instance;
		if (result == null) {
			synchronized (LOCK) {
				result = instance;
				if (result == null)
					instance = result = new TheGame();
			}
		}
		return result;
	}

	public void setBoard(int id, int size) {
		boards[id] = new Board(size);
	}

	public String makeMove(String move) {

		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		int playerID = players.get(clientID);
		Markers playerColor = colors.get(clientID);
		int boardID = boardsID.get(clientID);

		if (playerAlreadySkipped[boardID]) { // przeciwnik kontynnuje gre pomimio pasu
			if (whoseMove[boardID].equals(Markers.WHITEPASSED) && playerColor.equals(Markers.BLACK))
				whoseMove[boardID] = Markers.BLACK;
			else if (whoseMove[boardID].equals(Markers.BLACKPASSED) && playerColor.equals(Markers.WHITE))
				whoseMove[boardID] = Markers.WHITE;
		}

		if (whoseMove[boardID].equals(playerColor)) {
			playerAlreadySkipped[boardID] = false;

			int pointsScored = boards[boardID].insert(x, y, playerColor.asChar());
			if (pointsScored >= 0) {
				points[playerID] += pointsScored;
				whoseMove[boardID] = playerColor.getEnemy();
				return Integer.toString(points[playerID]) + ";" + boards[boardID].boardToString();
			} else
				return "IllegalMove";
		} else
			return "NotYrMove";

	}

	public String whoseMove(String clientID) {
		int boardID = boardsID.get(clientID);

		return whoseMove[boardID].asString() + ";" + boards[boardID].boardToString();
	}
	public String mapRefresh(String clientID) {
		int boardID = boardsID.get(clientID);
		return boards[boardID].boardToString(); // tu bedzie wysyłac plansze z odwpowidnimi znacznikami
	}

	public String addPlayer(String clientID) {

		if (playerCounter > 9) // max 5 plansz czyli 10 graczy
			return "ServerOverload";

		switch (playerCounter % 2) { // co drugiego gracza tworzy nową rozgrywkę
		case 0: {
			colors.put(clientID, Markers.BLACK); // gracz który pierwszy się połączył jest czarny
			break;

		}
		case 1: { // dobieranie graczy w pary i budowa planszy
			colors.put(clientID, Markers.WHITE);
			boards[boardCounter] = new Board(19);

			String enemyPlayerID = getKeyByValue(players, playerCounter - 1); // id poprzedniego clienta
			playerPairs.put(enemyPlayerID, clientID); // dobieranie ich w pary
			boardsID.put(clientID, boardCounter); // przypisanie ID planszy do graczy
			boardsID.put(enemyPlayerID, boardCounter);
			whoseMove[boardCounter] = Markers.BLACK;
			boardCounter++;
			break;
		}

		}
		players.put(clientID, playerCounter);
		points[playerCounter] = 0;
		playerCounter++;
		return "Succes;" + colors.get(clientID).asString();

	}

	public void skip(String clientID) {
		int boardID = boardsID.get(clientID);
		if (!playerAlreadySkipped[boardID]) {
			playerAlreadySkipped[boardID] = true;
			Markers playerColor = colors.get(clientID);
			if (playerColor.equals(Markers.WHITE))
				whoseMove[boardID] = Markers.WHITEPASSED;
			else
				whoseMove[boardID] = Markers.BLACKPASSED;
		} else
			whoseMove[boardID] = Markers.BOTHPASSED;

	}

	//TODO Trzeba coś zrobić, bo jest problem. Gdy gracz, którego ruch NIE jest wyjdzie z gry
	//to gracz, którego JEST tura dostaje freeza, gdy zrobi ruch, bo makeMove() nic mu nie odpowie
	//i okienko się zawiesza czekając na odpowiedź. Jakieś propozycje?

	public void exit(String clientID) { // czyszczenie map i tablic
		String enemyID = playerPairs.get(clientID);
		int boardID = boardsID.get(clientID);
		players.remove(clientID);
		players.remove(enemyID);
		if (colors.get(clientID).equals(Markers.BLACK)) {
			whoseMove[boardID] = Markers.WHITEWIN;
			playerPairs.remove(clientID);
		} else {
			whoseMove[boardID] = Markers.BLACKWIN;
			playerPairs.remove(enemyID);
		}

		colors.remove(clientID);
		colors.remove(enemyID);
		players.remove(clientID);
		players.remove(enemyID);

	}
	
	public void pickDeadStones(String move) {
		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		int boardID = boardsID.get(clientID);
		boards[boardID].markDeadStones(x, y, playerColor.getEnemy().asChar()); //voting on enemy color
		
		
	}
	public void countPoints(String clientID) {
		int boardID = boardsID.get(clientID);
		int playerID = players.get(clientID);
		Markers playerColor = colors.get(clientID);
		int enemyPlayerID = players.get(getEnemyID(clientID));
		countPoints(playerID, enemyPlayerID, boardID, playerColor);
				
	}

	public void pickTerritory(String move) {
		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		int boardID = boardsID.get(clientID);
		boards[boardID].claimTerritory(x, y, playerColor.asChar());
	}
	
	public String getEnemyID(String clientID) {
		String enemyID;
		if (playerPairs.containsKey(clientID)) {
			 enemyID = playerPairs.get(clientID);
			
		}
		else if(playerPairs.containsValue(clientID)) {
			enemyID = getKeyByValue(playerPairs, clientID);
			
		}
		else
			enemyID = "NoSuchPlayer";
		return enemyID;

	}
	private void countPoints(int playerID, int enemyPlayerID, int boardID, Markers playerColor) {
		if(playerColor.equals(Markers.WHITE)) { 
			points[playerID] += boards[boardID].getDeadStoneAndTerritoryPoints(Markers.WHITE.asChar());
			points[enemyPlayerID] += boards[boardID].getDeadStoneAndTerritoryPoints(Markers.BLACK.asChar()); 
		}
		else {
			points[playerID] += boards[boardID].getDeadStoneAndTerritoryPoints(Markers.BLACK.asChar()); 
			points[enemyPlayerID] +=  boards[boardID].getDeadStoneAndTerritoryPoints(Markers.WHITE.asChar());
		}
		boards[boardID].sweepDeadStones();
	}

	private <T, E> T getKeyByValue(Map<T, E> map, E value) { // odzyskiwanie klucza z wartości

		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}

		return null;
	}

	public Board getBoard(int id) {
		return boards[id];
	}

	public Map<String, Integer> getPlayers() {
		return players;
	}

	
	public int[] getPoints() {
		return points;
	}


	public Map<String, Markers> getColors() {
		return colors;
	}

	public Map<String, String> getPlayerPairs() {
		return playerPairs;
	}

	public Map<String, Integer> getBoardsID() {
		return boardsID;
	}

	
}
