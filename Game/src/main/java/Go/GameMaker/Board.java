package Go.GameMaker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {

	private int size;
	private char[][] board;
	private List<Stone> listOfStones;
	private Set<Stone> stonesVotedDead;
	private Stone koCandidate;
	private int[] deadStoneAndTerritoryPoints;
	private Markers gameState;
	private boolean isGameResultAccepted;

	public Board(int size) {
		this.size = size;
		board = new char[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = Markers.EMPTY.asChar(); // empty board
			}
		}
		listOfStones = new ArrayList<Stone>();
		stonesVotedDead = new HashSet<Stone>();
		deadStoneAndTerritoryPoints = new int[2];
		deadStoneAndTerritoryPoints[0] = 0;
		deadStoneAndTerritoryPoints[1] = 0;
		isGameResultAccepted = false;
	}

	public int insert(int x, int y, char allyColor) {
		if (x >= 0 && y >= 0 && x < size && y < size) {
			Stone s = new Stone(x, y, allyColor);
			int pointsScored = possibleInsert(s);
			if (board[x][y] == Markers.EMPTY.asChar() && pointsScored >= 0) {
				board[x][y] = allyColor;
				listOfStones.add(s);
				return pointsScored;
			}
		}
		return -1; // illegal move

	}

	// sprawdza czy ruch jest legalny
	private int possibleInsert(Stone stone) {

		List<Stone> killList = prepareKillList(stone); // kamienie, które zostałyby zabite tym ruchem
		int killScore = killList.size();

		if (blockedKo(stone, killScore))
			return -1;

		if (killScore == 1)
			koCandidate = killList.get(0); // ko wystepuje tylko jak zbity zostal jeden kamien

		if (killScore > 0) {
			kill(killList);
			return killScore;
		}

		int x = stone.getX();
		int y = stone.getY();
		char colorAlly = stone.getColorAlly();
		char[][] copyBoard = copyBoard(); // kopia plaszy
		copyBoard[x][y] = colorAlly;
		if (countBreaths(x, y, colorAlly, copyBoard) == 0) // zapobieganie samobojstwom
			return -1;

		return 0;
	}

	// zlicza wolne pola dookoła struktury
	public int countBreaths(int posX, int posY, char color, char[][] copyBoard) {
		int breath = 0;
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && copyBoard[posX][posY] == color) {
			copyBoard[posX][posY] = Markers.DONE.asChar(); // oznacza juz odwiedzone pole i zapobiega nieskończonej
															// rekursji
			if (posX != size - 1 && copyBoard[posX + 1][posY] == Markers.EMPTY.asChar()) {
				copyBoard[posX + 1][posY] = Markers.DONE.asChar();
				breath++;
			}
			if (posX != 0 && copyBoard[posX - 1][posY] == Markers.EMPTY.asChar()) {
				copyBoard[posX - 1][posY] = Markers.DONE.asChar();
				breath++;
			}

			if (posY != size - 1 && copyBoard[posX][posY + 1] == Markers.EMPTY.asChar()) {
				copyBoard[posX][posY + 1] = Markers.DONE.asChar();
				breath++;
			}

			if (posY != 0 && copyBoard[posX][posY - 1] == Markers.EMPTY.asChar()) {
				copyBoard[posX][posY - 1] = Markers.DONE.asChar();
				breath++;
			}

			breath += countBreaths(posX + 1, posY, color, copyBoard); // rekurencja w przypadku bycia łancuchem
			breath += countBreaths(posX - 1, posY, color, copyBoard);
			breath += countBreaths(posX, posY + 1, color, copyBoard);
			breath += countBreaths(posX, posY - 1, color, copyBoard);
		}

		return breath;
	}

	private List<Stone> prepareKillList(Stone stone) {
		int x = stone.getX();
		int y = stone.getY();
		char colorAlly = stone.getColorAlly();
		List<Stone> listEnemy = getEnemyNeighbours(stone); // sąsiedni wrogowie
		List<Stone> totalKillList = new ArrayList<Stone>();
		for (Stone s : listEnemy) {
			char[][] copyBoard = copyBoard();
			copyBoard[x][y] = colorAlly;
			if (countBreaths(s.getX(), s.getY(), s.getColorAlly(), copyBoard) == 0) { // sprawdza czy ruch zabija wrogow
																						// i zlicza trupy
				copyBoard = copyBoard(); // reset pomocniczej kopii planszy
				copyBoard[x][y] = colorAlly;
				totalKillList.addAll(findChain(s.getX(), s.getY(), s.getColorAlly(), copyBoard));

			}
		}
		return totalKillList;

	}

	// lista wrogich sąsiadów
	private List<Stone> getEnemyNeighbours(Stone stone) {
		int x = stone.getX();
		int y = stone.getY();
		char colorAlly = stone.getColorAlly();
		List<Stone> list = new ArrayList<Stone>();
		for (Stone s : listOfStones) {

			if (s.getX() == x + 1 && s.getY() == y && s.getColorEnemy() == colorAlly)
				list.add(s);
			else if (s.getX() == x - 1 && s.getY() == y && s.getColorEnemy() == colorAlly)
				list.add(s);
			else if (s.getX() == x && s.getY() == y + 1 && s.getColorEnemy() == colorAlly)
				list.add(s);
			else if (s.getX() == x && s.getY() == y - 1 && s.getColorEnemy() == colorAlly)
				list.add(s);

		}
		return list;

	}

	// łączy rekurencyjnie pionki będącę w grupie
	private List<Stone> findChain(int posX, int posY, char color, char[][] copyBoard) {
		List<Stone> chain = new ArrayList<Stone>();
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && copyBoard[posX][posY] == color) {
			copyBoard[posX][posY] = Markers.DONE.asChar();
			for (Stone s : listOfStones) {
				if (s.getX() == posX && s.getY() == posY) {
					chain.add(s);
					break;

				}
			}
			chain.addAll(findChain(posX + 1, posY, color, copyBoard)); // rekurencja w przypadku bycia łańcuchem
			chain.addAll(findChain(posX - 1, posY, color, copyBoard));
			chain.addAll(findChain(posX, posY + 1, color, copyBoard));
			chain.addAll(findChain(posX, posY - 1, color, copyBoard));

		}
		return chain;
	}

	// warunek blokady ko
	private boolean blockedKo(Stone stone, int kills) {
		if (koCandidate != null) {
			if (koCandidate.equals(stone) && kills == 1) // próba ruchu w to samo pole z warunkiem zabicia jednego
															// pionka
				return true;
			else if (koCandidate.getColorAlly() == stone.getColorAlly()) {// ruch został wykonany gdzie indziej, reset
																			// ko
				koCandidate = null;
			}
		}
		return false;
	}

	public void markDeadStones(int x, int y, char color) {
		char[][] copy = copyBoard();
		stonesVotedDead.addAll(findChain(x, y, color, copy));
		for (Stone s : stonesVotedDead) {
			if (board[s.getX()][s.getY()] == Markers.WHITE.asChar()) {
				board[s.getX()][s.getY()] = Markers.WHITEDEAD.asChar();
				deadStoneAndTerritoryPoints[0]++;
			} else if (board[s.getX()][s.getY()] == Markers.BLACK.asChar()) {
				board[s.getX()][s.getY()] = Markers.BLACKDEAD.asChar();
				deadStoneAndTerritoryPoints[1]++;
			}

		}

	}

	public void claimTerritory(int x, int y, char color) {
		
		if(color == Markers.WHITE.asChar())
			deadStoneAndTerritoryPoints[0] += claimRecursive(x, y, Markers.WHITEVOTE.asChar());
		else
			deadStoneAndTerritoryPoints[1] += claimRecursive(x, y, Markers.BLACKVOTE.asChar());
	}

	private int claimRecursive(int posX, int posY, char color) {
		int totalTerritory = 0;
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && board[posX][posY] == Markers.EMPTY.asChar()) {
			board[posX][posY] = color;
			totalTerritory++;
			totalTerritory += claimRecursive(posX + 1, posY, color);
			totalTerritory += claimRecursive(posX - 1, posY, color);
			totalTerritory += claimRecursive(posX, posY + 1, color);
			totalTerritory += claimRecursive(posX, posY - 1, color);

		}
		
		return totalTerritory;
	}

	public void restoreBoard() { // w przypadku jak gracze się rozmyślą
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = Markers.EMPTY.asChar(); // empty board
			}
		}
		stonesVotedDead.clear();
		for (Stone s : listOfStones) {
			board[s.getX()][s.getY()] = s.getColorAlly();
		}
		deadStoneAndTerritoryPoints[0] = 0;
		deadStoneAndTerritoryPoints[1] = 0;
		isGameResultAccepted = false;
	}

	public void confirmChanges() {
		List<Stone> list = new ArrayList<Stone>();
		list.addAll(stonesVotedDead);
		kill(list);
		deadStoneAndTerritoryPoints[0] = 0;
		deadStoneAndTerritoryPoints[1] = 0;
		isGameResultAccepted = false;
		for(int i =0; i < size; i++)
			for(int j = 0; j < size; j++) {
				if(board[i][j] == Markers.WHITEVOTE.asChar()) 
					board[i][j] = Markers.WHITETERRITORY.asChar();
				else if(board[i][j] == Markers.BLACKVOTE.asChar())
					board[i][j] = Markers.BLACKTERRITORY.asChar();	
			}
		
	}

	// pozostałe metody są self explanatory
	public void kill(List<Stone> list) {
		for (Stone s : list) {
			board[s.getX()][s.getY()] = Markers.EMPTY.asChar();
		}
		listOfStones.removeAll(list);
	}

	public char[][] copyBoard() {
		char[][] copy = new char[size][size];
		for (int i = 0; i < size; i++) {
			System.arraycopy(board[i], 0, copy[i], 0, size);
		}
		return copy;
	}

	public void printBoard(char[][] arr) {
		for (int i = 0; i < size; i++) {
			String s = "";
			for (int j = 0; j < size; j++) {
				if (arr[i][j] == Markers.EMPTY.asChar()) {
					s += "[ ]";
				} else
					s += (" " + String.valueOf(arr[i][j]) + " ");
			}
			System.out.println(s);
		}
		System.out.println("+-----------------------------+");
	}

	public String boardToString() {
		String string = "";
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				string += (String.valueOf(board[i][j]) + ",");
			}
			string += ";";

		}
		return string;

	}

	public int getDeadStoneAndTerritoryPoints(char color) {
		if (color == Markers.WHITE.asChar())
			return deadStoneAndTerritoryPoints[0];
		else
			return deadStoneAndTerritoryPoints[1];
	}

	public Markers getGameState() {
		return gameState;
	}

	public void setGameState(Markers gameState) {
		this.gameState = gameState;
	}

	public boolean isGameResultAccepted() {
		return isGameResultAccepted;
	}
	public void setGameResultAccepted(boolean bool) {
		this.isGameResultAccepted = bool;
	}

	public char[][] getBoard() {
		return board;
	}

}
