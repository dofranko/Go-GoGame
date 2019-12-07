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
		String[] splittedCommand = move.split(","); //parsing
		String idGracza = splittedCommand[0];
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

	public String whoseMove() {
				
		return whoseMove.asString() + ";" + board.boardToString();
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
		players.put(playerID, counter);
		points[counter] = 0;
		counter++;
		return "Succes;" + colors.get(counter-1).asString();

	}
	
	public void skip(String playerID) {
		if(!playerAlreadySkipped) {
			playerAlreadySkipped = true;
			int id = players.get(playerID);
			Markers playerColor = colors.get(id);
			whoseMove = playerColor.getEnemy();
			//return "EnemyWantsToContinue";
		}
		//else
			//return "EnemyPassedToo";
	
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

	public String pickDeadStones(String move){
		return "0";
	}
	public String pickTerritory(String move){
		return "1";
	}
}
