package Go.GameMaker;

import java.util.HashMap;
import java.util.Map;

public class TheGame {
	// private static final TheGame instance;
	private static TheGame instance = null;
	private Board board = null;
	private Map<String, Integer> players;
	private Map<Integer, Character> colors;
	private int points[]; 
	private int counter;

	private TheGame() {
		players = new HashMap<String, Integer>();
		colors = new HashMap<Integer, Character>();
		points = new int[2];
		counter = 0;
		
		//bedzie też wkrótce kontrola wykonywania ruchów na zmiane
	}

	public static TheGame getInstance() { //double checker metoda statyczna na zwracanie singletona
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
		String[] splittedCommand = move.split(",");
		String idGracza = splittedCommand[0];
		int x = Integer.parseInt(splittedCommand[1]);
		int y = Integer.parseInt(splittedCommand[2]);
		int id = players.get(idGracza);
		
		int pointsScored = board.insert(x, y, colors.get(id));
		if (pointsScored >= 0) {
			points[id] += pointsScored;
			return Integer.toString(points[id]) + ";" + board.boardToString();
		} else 
			return "-1" + ";" + board.boardToString(); //illegal move
		
	}

	public String whoseMove() {
		// TODO tutaj jest metoda pomocnicza dla GUI, która sprawdza czyj ruch
		// póki co nie wymyśliłem jak powiadomić wszystkich klientó o ruchu
		// więc będzie na razie coś typu: jeśli client oczekuje na ruch przeciwnika to
		// sprawdza co chwilę
		// tą metodą czy już może zrobić ruch; jak coś wymyślisz ciekawszego to daj znać
		return "69420";
	}

	public String addPlayer(String playerID) {
		switch (counter) {
		case 0: {
			players.put(playerID, counter);
			colors.put(counter, Markers.BLACK.asChar()); // gracz który pierwszy się połączył jest czarny
			counter++;
			return "Succes;Black";
		}
		case 1: {
			players.put(playerID, counter);
			colors.put(counter, Markers.WHITE.asChar());
			counter++;
			return "Succes;White";
		}
		default: 
			return "Error";
		
		}

	}

	
	public Board getBoard() {
		return board;
	}

	
	public Map<String, Integer> getPlayers() {
		return players;
	}

	
	public Map<Integer, Character> getColors() {
		return colors;
	}

	
	public int[] getPoints() {
		return points;
	}
}
