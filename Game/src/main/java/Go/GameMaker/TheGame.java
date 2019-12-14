package Go.GameMaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

//Na razie obsługuje jeden mecz, ale bedzie opcja wielu
public class TheGame {

	private static final Object LOCK = new Object();
	private static volatile TheGame instance;

	// tablica plansz
	private Map<String, Board> boards;

	// licznik graczy
	private int playerCounter;

	// mapa parująca ID clienta z jego ID w TheGame
	private Map<String, Integer> players;

	// mapa parująca ID clienta z jego kolorem
	private Map<String, Markers> colors;

	// mapa parująca graczy ze sobą
	private Map<String, String> playerPairs;

	// tablica punktacji poszczegolnych graczy
	private Map<String, Integer> points;

	
	private TheGame() { // inicializacja
		players = new HashMap<String, Integer>();
		colors = new HashMap<String, Markers>();
		playerPairs = new HashMap<String, String>();
		boards = new HashMap<String, Board>();
		points = new HashMap<String, Integer>();
		playerCounter = 0;
	}

	public static TheGame getInstance() { // double checker metoda statyczna na zwracanie singletona
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
	
	public String addPlayer(String clientID) {

		switch (playerCounter % 2) { // co drugiego gracza tworzy nową rozgrywkę
		case 0: {
			colors.put(clientID, Markers.BLACK); // gracz który pierwszy się połączył jest czarny
			break;

		}
		case 1: { // dobieranie graczy w pary i budowa planszy
			Board b = new Board(19);
			b.setGameState(Markers.BLACK);
			colors.put(clientID, Markers.WHITE);
			boards.put(clientID, b);

			String enemyPlayerID = getKeyByValue(players, playerCounter - 1); // id poprzedniego clienta
			boards.put(enemyPlayerID, b);
			playerPairs.put(enemyPlayerID, clientID); // dobieranie ich w pary
			break;
		}

		}
		players.put(clientID, playerCounter);
		points.put(clientID, 0);
		playerCounter++;
		return "Succes;" + colors.get(clientID).asString();

	}

	public String makeMove(String move) {

		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		Board board = boards.get(clientID);

		if (board.getGameState().equals(Markers.WHITEPASSED) && playerColor.equals(Markers.BLACK)) // kontynuacja gry pomimo pasowania
			board.setGameState(Markers.BLACK);
		else if (board.getGameState().equals(Markers.BLACKPASSED) && playerColor.equals(Markers.WHITE))
			board.setGameState(Markers.WHITE);

		if (board.getGameState().equals(playerColor)) {

			int pointsScored = board.insert(x, y, playerColor.asChar());
			if (pointsScored >= 0) {
				int totalPoints = points.get(clientID) + pointsScored;
				points.replace(clientID, totalPoints);
				
				board.setGameState(playerColor.getEnemy());
				return Integer.toString(totalPoints) + ";" + board.boardToString();
			} else
				return "IllegalMove";
		} else
			return "NotYrMove";

	}

	public String getGameState(String clientID) {
		Board b = boards.get(clientID);
		return b.getGameState().asString() + ";" + b.boardToString();
	}

	

	public void skip(String clientID) {
		Board board = boards.get(clientID);
		Markers playerColor = colors.get(clientID);
		if (!board.getGameState().equals(Markers.WHITEPASSED) && !board.getGameState().equals(Markers.BLACKPASSED)) {
			
			if (playerColor.equals(Markers.WHITE))
				board.setGameState(Markers.WHITEPASSED);
			else
				board.setGameState(Markers.BLACKPASSED);
		} else
			board.setGameState(Markers.BOTHPASSED);

	}

	public void accept(String clientID) {
		Board b = boards.get(clientID);
		if(b.isGameResultAccepted()) 
			applyChangesAndCount(clientID);
					
		else			 
			b.setGameResultAccepted(true);
			
	}
	public void pickDeadStones(String move) {
		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		Board board = boards.get(clientID);
		board.markDeadStones(x, y, playerColor.getEnemy().asChar()); // voting on enemy color

	}
	
	public void pickTerritory(String move) {
		String[] splittedCommand = move.split(","); 
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		Board board = boards.get(clientID);
		board.claimTerritory(x, y, playerColor.asChar());
	}

	private void applyChangesAndCount(String clientID) {
		Markers playerColor = colors.get(clientID);
		String enemyID = getEnemyID(clientID);
		Board board = boards.get(clientID);
		if (playerColor.equals(Markers.WHITE)) {
			int totalAllyPoints = board.getDeadStoneAndTerritoryPoints(Markers.WHITE.asChar()) + points.get(clientID);
			points.replace(clientID, totalAllyPoints);
			int totalEnemyPoints = board.getDeadStoneAndTerritoryPoints(Markers.BLACK.asChar()) + points.get(enemyID);
			points.replace(enemyID, totalEnemyPoints);
			
		} else {
			int totalAllyPoints = board.getDeadStoneAndTerritoryPoints(Markers.BLACK.asChar()) + points.get(clientID);
			points.replace(clientID, totalAllyPoints);
			int totalEnemyPoints = board.getDeadStoneAndTerritoryPoints(Markers.WHITE.asChar()) + points.get(enemyID);
			points.replace(enemyID, totalEnemyPoints);
			
		}
		board.confirmChanges();
	}

	public void cancelVote(String clientID) {
		boards.get(clientID).restoreBoard();
	}

	public String getEnemyID(String clientID) {
		String enemyID;
		if (playerPairs.containsKey(clientID)) {
			enemyID = playerPairs.get(clientID);

		} else if (playerPairs.containsValue(clientID)) {
			enemyID = getKeyByValue(playerPairs, clientID);

		} else
			enemyID = "NoSuchPlayer";
		return enemyID;

	}

		
	public void exit(String clientID) { // czyszczenie map i tablic
		String enemyID = playerPairs.get(clientID);
		Board board = boards.get(clientID);
		players.remove(clientID);
		players.remove(enemyID);
		if (colors.get(clientID).equals(Markers.BLACK)) {
			board.setGameState(Markers.WHITEWIN);
			playerPairs.remove(clientID);
		} else {
			board.setGameState(Markers.BLACKWIN);
			playerPairs.remove(enemyID);
		}

		colors.remove(clientID);
		colors.remove(enemyID);
		players.remove(clientID);
		players.remove(enemyID);

	}

	private <T, E> T getKeyByValue(Map<T, E> map, E value) { // odzyskiwanie klucza z wartości

		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}

		return null;
	}

	public Board getBoard(String clientID) {
		return boards.get(clientID);
	}

	public Map<String, Integer> getPlayers() {
		return players;
	}

	public int getPoints(String clientID) {
		return points.get(clientID);
	}

	public Map<String, Markers> getColors() {
		return colors;
	}

	public Map<String, String> getPlayerPairs() {
		return playerPairs;
	}

	
}
