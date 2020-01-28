package Go.GameMaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import Go.ServerClient.Hibernate.DBManager;

public class TheGame {

	private static final Object LOCK = new Object();
	private static volatile TheGame instance;
	DBManager db;

	/** Mapa parująca ID gracza z planszą, na której gra */
	private Map<String, Board> boards;

	/** Mapa parująca ID clienta z jego kolorem */
	private Map<String, Markers> colors;

	/** Mapa parująca graczy ze sobą */
	private Map<String, String> playerPairs;

	private TheGame() { // inicializacja
		colors = new HashMap<String, Markers>();
		playerPairs = new HashMap<String, String>();
		boards = new HashMap<String, Board>();
		db = new DBManager();
	}

	/** Double checker metoda statyczna na zwracanie singletona */
	public synchronized static TheGame getInstance() {
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

	/**
	 * Dodaje nowego gracza do już istniejącej gry, lub w przeciwnym razie tworzy
	 * nową
	 */
	public String addPlayer(String clientID, int size) {
		for (Entry<String, Board> board : boards.entrySet()) {
			Board b = board.getValue();
			if (b.getSize() == size && !b.arePlayersFound()) {
				boards.put(clientID, b);
				playerPairs.put(clientID, b.getHostID());
				Markers color = Markers.WHITE; // b.getGameState().asEnemy();
				colors.put(clientID, color);
				int boardID = db.insertGame(b.getHostID(), clientID);
				b.setBoardID(boardID);
				b.setPlayersFound(true);
				return "Succes;" + color.asString();
			}
		}
		Board b = new Board(size);
		b.setHostID(clientID);
		Markers playerColor = Markers.BLACK;
		b.setGameState(playerColor);
		boards.put(clientID, b);
		colors.put(clientID, playerColor);
		return "Succes;" + playerColor.asString();

	}

	/**
	 * Wykonanie ruchu przez gracza
	 * 
	 * @return liczba punktów zdobyta tym ruchem + plansza w stringu lub IllegalMove
	 * @param
	 */
	public String makeMove(String move) {

		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		Board board = boards.get(clientID);

		// kontynuacja gry pomimo pasowania
		if (board.getGameState() == playerColor.asEnemy().asPassed())
			board.setGameState(playerColor);

		if (board.getGameState() == playerColor) {
			int pointsScored = board.insert(x, y, playerColor);
			if (pointsScored >= 0) {
				board.incMovesMade();
				db.insertMove(clientID, board.boardToString(), "ruch", board.getMovesMade(), board.getBoardID());
				return Integer.toString(pointsScored) + ";" + board.boardToString();
			} else
				return "IllegalMove";
		} else
			return "NotYrMove";

	}

	/** Zwraca stan gry + plansza w stringu */
	public String getGameState(String clientID) {
		Board b = boards.get(clientID);
		return b.getGameState().asString() + ";" + b.boardToString();
	}

	/** Zwraca kolor gracza, który zaakceptował stan planszy w ostatniej fazie */
	public String getPlayerWhoAccepted(String clientID) {
		Board b = boards.get(clientID);
		return b.getPlayerWhoAccepted().asAccepted().asString();
	}

	/** Pasowanie tury */
	public void skip(String clientID) {
		Board board = boards.get(clientID);
		Markers playerColor = colors.get(clientID);
		board.incMovesMade();
		db.insertMove(clientID, board.boardToString(), "pass", board.getMovesMade(), board.getBoardID());
		if (board.getGameState() != playerColor.asEnemy().asPassed()) {
			board.setGameState(playerColor.asPassed());

		} else
			board.setGameState(Markers.BOTHPASSED);

	}

	/** Akceptowanie stanu planszy */
	public void acceptStage(String clientID) {
		Board b = boards.get(clientID);
		Markers playerColor = colors.get(clientID);
		if (b.getPlayerWhoAccepted() == playerColor.asEnemy()) {
			b.confirmChanges();
			if (b.getGameState() == Markers.THEEND) { // zapis finalnej fazy do bazy i określenie kto wygrał
				int allyPoints = b.getPoints(colors.get(clientID));
				int enemyPoints = b.getPoints(colors.get(getEnemyID(clientID)));
				if (allyPoints > enemyPoints) {
					b.incMovesMade();
					db.insertMove(clientID, b.boardToString(), "zwycięstwo", b.getMovesMade(), b.getBoardID());
				} else if (allyPoints < enemyPoints) {
					b.incMovesMade();
					db.insertMove(getEnemyID(clientID), b.boardToString(), "zwycięstwo", b.getMovesMade(), b.getBoardID());
				} else {
					b.incMovesMade();
					db.insertMove(clientID, b.boardToString(), "remis", b.getMovesMade(), b.getBoardID());
				}
			}
		} else
			b.setGameResultAccepted(playerColor);

	}

	/** Wybór martwych kamieni */
	public void pickDeadStones(String move) {
		String[] splittedCommand = move.split(","); // parsing
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Board board = boards.get(clientID);
		if (!board.isGameResultAccepted())
			board.markDeadStones(x, y);

	}

	/** Wybór terytorium */
	public void pickTerritory(String move) {
		String[] splittedCommand = move.split(",");
		String clientID = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		Markers playerColor = colors.get(clientID);
		Board board = boards.get(clientID);
		if (!board.isGameResultAccepted())
			board.claimTerritory(x, y, playerColor);
	}

	/** Przywróceniu poprzedniego stanu planszy */
	public void cancelVote(String clientID) {
		boards.get(clientID).restoreBoard();
	}

	/** Gracz wychodzi z rozgrywki */
	public void exit(String clientID) {
		giveUp(clientID);
		playerPairs.remove(clientID);
		colors.remove(clientID);
		boards.remove(clientID);

	}

	/** Gracz poddaje się */
	public void giveUp(String clientID) {
		Board board = boards.get(clientID);
		Markers playerColor = colors.get(clientID);
		if (board.getGameState() != playerColor.asWinner() && board.getGameState() != playerColor.asEnemy().asWinner()
				&& board.getGameState() != Markers.THEEND) {
			board.setGameState(playerColor.asEnemy().asWinner());
			board.incMovesMade();
			db.insertMove(clientID, board.boardToString(), "poddanie sie", board.getMovesMade(), board.getBoardID());
		}

	}

	/** Zwraca ID przeciwnika */
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

	/** @return kolor graczy i ich punkty */
	public String getPoints(String clientID) {
		String enemyID = getEnemyID(clientID);
		Markers playerColor = colors.get(clientID);
		Markers enemyColor = colors.get(enemyID);
		int playerPoints = boards.get(clientID).getPoints(playerColor);
		int enemyPoints = boards.get(enemyID).getPoints(enemyColor);
		return playerColor.asString() + ";" + String.valueOf(playerPoints) + ";" + enemyColor.asString() + ";"
				+ String.valueOf(enemyPoints);
	}

	public Map<String, Markers> getColors() {
		return colors;
	}

	public Map<String, String> getPlayerPairs() {
		return playerPairs;
	}

}
