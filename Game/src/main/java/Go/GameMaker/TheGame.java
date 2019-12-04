package Go.GameMaker;

import java.util.HashMap;
import java.util.Map;

//Na razie obsługuje jeden mecz, ale bedzie opcja wielu
public class TheGame {
	
	private static volatile TheGame instance = null;
	private Board board = null;
	private Map<String, Integer> players;
	private Map<Integer, Markers> colors;
	private int points[];
	private int counter;
	private Markers whoseMove;
	private boolean playerAlreadySkipped;

	private TheGame() {
		players = new HashMap<String, Integer>();
		colors = new HashMap<Integer, Markers>();
		points = new int[2];
		counter = 0;
		whoseMove = Markers.BLACK;
		playerAlreadySkipped = false;

	}

	public static TheGame getInstance() { // double checker metoda statyczna na zwracanie singletona
		if (instance == null) {
			synchronized (TheGame.class) {
				if (instance == null)
					instance = new TheGame();
			}
		}
		return instance;
	}

	public void setBoard(int size) {
		board = new Board(size);
	}

	public String makeMove(String move) {
		String[] splittedCommand = move.split(","); //parsing
		String idGracza = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		int id = players.get(idGracza);
		Markers playerColor = colors.get(id);
		playerAlreadySkipped = false;

		if (whoseMove.equals(playerColor)) {

			int pointsScored = board.insert(x, y, playerColor.asChar());
			if (pointsScored >= 0) {
				points[id] += pointsScored;
				whoseMove = playerColor.getEnemy();
				return Integer.toString(points[id]) + ";" + board.boardToString();
			} else
				return "IllegalMove";
		} else
			return "NotYrMove"; 

	}

	public String whoseMove() {
				
		return whoseMove.asString() + ";" + board.boardToString();
	}

	public String addPlayer(String playerID) {
		switch (counter) {
		case 0: {
			colors.put(counter, Markers.BLACK); // gracz który pierwszy się połączył jest czarny
			break;
						
		}
		case 1: {
			colors.put(counter, Markers.WHITE);
			break;
		}
		default:
			return "Error";

		}
		players.put(playerID, counter);
		points[counter] = 0;
		counter++;
		return "Succes;" + colors.get(counter-1).asString();

	}
	
	public String skip(String playerID) {
		if(!playerAlreadySkipped) {
			playerAlreadySkipped = true;
			int id = players.get(playerID);
			Markers playerColor = colors.get(id);
			whoseMove = playerColor.getEnemy();
			return "EnemyWantsToContinue";
		}
		else
			return "EnemyPassedToo";	
	
	}
	public void exit(String playerID) {
		players.clear();
		colors.clear();
		points = new int[2];
		whoseMove = Markers.EMPTY;
		
	}

	public Board getBoard() {
		return board;
	}

	public Map<String, Integer> getPlayers() {
		return players;
	}

	public Map<Integer, Markers> getColors() {
		return colors;
	}

	public int[] getPoints() {
		return points;
	}
}
