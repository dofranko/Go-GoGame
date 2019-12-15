package Go.GameMaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

		
	private TheGame() { // inicializacja
		players = new HashMap<String, Integer>();
		colors = new HashMap<String, Markers>();
		playerPairs = new HashMap<String, String>();
		boards = new HashMap<String, Board>();
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

		if (board.getGameState() == Markers.WHITEPASSED && playerColor == Markers.BLACK) // kontynuacja gry pomimo pasowania
			board.setGameState(Markers.BLACK);
		else if (board.getGameState() == Markers.BLACKPASSED && playerColor == Markers.WHITE)
			board.setGameState(Markers.WHITE);

		if (board.getGameState() == playerColor) {

			int pointsScored = board.insert(x, y, playerColor);
			if (pointsScored >= 0) {
				board.setGameState(playerColor.getEnemy());
				return Integer.toString(pointsScored) + ";" + board.boardToString();
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
		if (board.getGameState() != Markers.WHITEPASSED && board.getGameState() != Markers.BLACKPASSED) {
			
			if (playerColor == Markers.WHITE)
				board.setGameState(Markers.WHITEPASSED);
			else
				board.setGameState(Markers.BLACKPASSED);
		} else
			board.setGameState(Markers.BOTHPASSED);

	}

	public void acceptStage(String clientID) {
		Board b = boards.get(clientID);
		if(b.isGameResultAccepted()) 
			b.confirmChanges();
		else			 
			b.setGameResultAccepted(true);
			
	}
	public void pickDeadStones(String move) {
		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Board board = boards.get(clientID);
		board.markDeadStones(x, y); 

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

		
	public void exit(String clientID) { // czyszczenie map 
		String enemyID = playerPairs.get(clientID);
		Board board = boards.get(clientID);
		players.remove(clientID);
		players.remove(enemyID);
		if (colors.get(clientID).equals(Markers.BLACK)) {
			board.setGameState(Markers.WHITEWIN);
			//playerPairs.remove(clientID);
		} else {
			board.setGameState(Markers.BLACKWIN);
			//playerPairs.remove(enemyID);
		}

		colors.remove(clientID);
		colors.remove(enemyID);
		
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
		Markers color = colors.get(clientID);
		return boards.get(clientID).getDeadStoneAndTerritoryPoints(color);
	}

	public Map<String, Markers> getColors() {
		return colors;
	}

	public Map<String, String> getPlayerPairs() {
		return playerPairs;
	}

	
}
