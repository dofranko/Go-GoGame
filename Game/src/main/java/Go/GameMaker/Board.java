package Go.GameMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Klasa, w której odbywa się rozgrywka*/
public class Board {
	/** Rozmiar planszy */ /** */
	private int size;
	/** Reprezentacja planszy*/
	private char[][] board;
	/** Lista kamieni rozieszczonych na planszy*/
	private List<Stone> listOfStones;
	/** Zbiór kamieni, które gracze oznaczyli jako martwe*/
	private Set<Stone> stonesVotedDead;
	/** Pomocniczy rejest w celu wykrycia sytucaji ko na planszy*/
	private Stone koCandidate;
	/** Mapa przechowująca liczbę punktów każdego z graczy*/
	private Map<Markers, Integer> totalPointsMap;
	/** Aktualny stan rozgrywki*/
	private Markers gameState;
	/** Znacznik, że gracz zaakceptował stan gry w ostatnij fazie*/
	private Markers playerWhoAccepted;
	private boolean isGameResultAccepted;
	/** Flaga oznaczająca, czy gra się zaczęła*/
	private boolean arePlayersFound;
	/** Tymczasowy rejestr w celu sparowania ID graczy*/
	private String hostID;

	/** Konstruktor, inicjalizacja planszy i pól*/
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
		totalPointsMap = new HashMap<Markers, Integer>();
		totalPointsMap.put(Markers.BLACK, 0);
		totalPointsMap.put(Markers.WHITE, 0);
		playerWhoAccepted = Markers.EMPTY;
		isGameResultAccepted = false;
		arePlayersFound = false;
	}
	
	/** Dodaje kamień na planszę
	 * @return liczba zbitych wrogich kamieni lub -1 jeśli ruch nie jest możliwy
	 * */
	public int insert(int x, int y, Markers playerColor) {
		if (x >= 0 && y >= 0 && x < size && y < size) {
			Stone s = new Stone(x, y, playerColor);
			int pointsScored = possibleInsert(s); 
			if (pointsScored >= 0) {
				board[x][y] = playerColor.asChar();
				listOfStones.add(s);
				int totalPoints = totalPointsMap.get(playerColor) + pointsScored;
				totalPointsMap.replace(playerColor, totalPoints);
				gameState = playerColor.asEnemy();
				return totalPoints;
			}
		}
		return -1; // illegal move

	}

	/** Sprawdza czy ruch jest legalny 
	 * @return liczba zbitych wrogich kamieni lub -1 jeśli ruch nie jest możliwy */ /** */
	private int possibleInsert(Stone stone) {

		if (board[stone.getX()][stone.getY()] != Markers.EMPTY.asChar())
			return -1;

		List<Stone> killList = new ArrayList<Stone>();
		killList.addAll(prepareKillList(stone)); // kamienie, które zostałyby zabite tym ruchem
		int killScore = killList.size();

		if (blockedKo(stone, killScore)) // srawdzanie warunku ko
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
		if (countLiberties(x, y, colorAlly, copyBoard) == 0) // zapobieganie samobojstwom
			return -1;

		return 0;
	}

	/** ZLicza rekurencyjnie wole pola dookoła grupy
	 * @return liczba wolych pól dookoła*/
	public int countLiberties(int posX, int posY, char color, char[][] copyBoard) { 
		int liberties = 0;
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && copyBoard[posX][posY] == color) {
			// oznacza juz odwiedzone pole i zapobiega nieskończonej rekursji
			copyBoard[posX][posY] = Markers.DONE.asChar(); 
			
			if (posX != size - 1 && copyBoard[posX + 1][posY] == Markers.EMPTY.asChar()) {
				copyBoard[posX + 1][posY] = Markers.DONE.asChar();
				liberties++;
			}
			if (posX != 0 && copyBoard[posX - 1][posY] == Markers.EMPTY.asChar()) {
				copyBoard[posX - 1][posY] = Markers.DONE.asChar();
				liberties++;
			}

			if (posY != size - 1 && copyBoard[posX][posY + 1] == Markers.EMPTY.asChar()) {
				copyBoard[posX][posY + 1] = Markers.DONE.asChar();
				liberties++;
			}

			if (posY != 0 && copyBoard[posX][posY - 1] == Markers.EMPTY.asChar()) {
				copyBoard[posX][posY - 1] = Markers.DONE.asChar();
				liberties++;
			}

			liberties += countLiberties(posX + 1, posY, color, copyBoard); // rekurencja w przypadku bycia łancuchem
			liberties += countLiberties(posX - 1, posY, color, copyBoard);
			liberties += countLiberties(posX, posY + 1, color, copyBoard);
			liberties += countLiberties(posX, posY - 1, color, copyBoard);
		}

		return liberties;
	}
	
	/** Przygotowanie zbioru kamieni,które zostałyby zabite tym ruchem*/
	private Set<Stone> prepareKillList(Stone stone) {
		int x = stone.getX();
		int y = stone.getY();
		char colorAlly = stone.getColorAlly();
		List<Stone> listEnemy = getEnemyNeighbours(stone); // sąsiedni wrogowie
		Set<Stone> totalKillList = new HashSet<Stone>();
		for (Stone s : listEnemy) {
			char[][] copyBoard = copyBoard();
			copyBoard[x][y] = colorAlly;
			if (countLiberties(s.getX(), s.getY(), s.getColorAlly(), copyBoard) == 0) { //sprawdza czy zabija 
				copyBoard = copyBoard(); // reset pomocniczej kopii planszy
				copyBoard[x][y] = colorAlly;
				totalKillList.addAll(findChain(s.getX(), s.getY(), s.getColorAlly(), copyBoard));

			}
		}
		return totalKillList;

	}

	/** Zwraca listę wrogich sąsiadów*/
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

	/** łączy rekurencyjnie kamienie będące w grupie 
	 * @return Lista kamieni będacych bezpośrednio obok siebie*/
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

	/** Sprawdza warunke blokady ko*/
	private boolean blockedKo(Stone stone, int kills) {
		if (koCandidate != null) {
			// próba ruchu w to samo pole z warunkiem zabicia jednego pionka
			if (koCandidate.equals(stone) && kills == 1) 
				return true;
			// ruch został wykonany gdzie indziej, reset ko
			else if (koCandidate.getColorAlly() == stone.getColorAlly()) {
				koCandidate = null;
			}
		}
		return false;
	}
	
	/** Oznacza kamienie wybrane przez gracza jako martwe*/
	public void markDeadStones(int x, int y) {
		char color = 0;
		for (Stone s : listOfStones) {
			if (s.getX() == x && s.getY() == y) {
				color = s.getColorAlly(); //określenie koloru jaki wybrał gracz
				break;
			}
		}
		char[][] copy = copyBoard();
		stonesVotedDead.addAll(findChain(x, y, color, copy));
		for (Stone s : stonesVotedDead) {
			if (board[s.getX()][s.getY()] == Markers.WHITE.asChar()) {
				board[s.getX()][s.getY()] = Markers.WHITEDEAD.asChar();
			} else if (board[s.getX()][s.getY()] == Markers.BLACK.asChar()) {
				board[s.getX()][s.getY()] = Markers.BLACKDEAD.asChar();
			}

		}

	}
	
	/** Wywołuje metodę rekurencyjną na zajmowanie terytorium*/
	public void claimTerritory(int x, int y, Markers playerColor) {

		claimRecursive(x, y, playerColor.asClaimTerritory().asChar());
	}
	
	/** Meotda rekurencyjna wypełniająca wolne pola kolorem gracza*/
	private void claimRecursive(int posX, int posY, char color) {
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && board[posX][posY] == Markers.EMPTY.asChar()) {
			board[posX][posY] = color;
			claimRecursive(posX + 1, posY, color);
			claimRecursive(posX - 1, posY, color);
			claimRecursive(posX, posY + 1, color);
			claimRecursive(posX, posY - 1, color);

		}

	}
	
	/** Przywraca planszę do stanu sprzed oznaczania przez graczy*/
	public void restoreBoard() { // w przypadku jak gracze się rozmyślą
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = Markers.EMPTY.asChar(); // empty board
			}
		}
		stonesVotedDead.clear();
		for (Stone s : listOfStones) {
			board[s.getX()][s.getY()] = s.getColorAlly(); //odtworzenie planszy
		}
		isGameResultAccepted = false;
		playerWhoAccepted = Markers.EMPTY;
	}
	
	/** Zatwierdza oznaczenia graczy i przechodzi do następnej fazy*/
	public void confirmChanges() {
		int whiteBonusPoints = 0;
		int blackBonusPoints = 0;
		if (gameState == Markers.BOTHPASSED) {
			List<Stone> list = new ArrayList<Stone>();
			list.addAll(stonesVotedDead);
			for (Stone s : list) {
				if (s.getColorAlly() == Markers.WHITE.asChar())
					blackBonusPoints++;
				else
					whiteBonusPoints++;
			}
			kill(list);
			
		}
		else if (gameState == Markers.TERRITORY) {
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++) {
					if (board[i][j] == Markers.WHITECLAIM.asChar()) {
						board[i][j] = Markers.WHITETERRITORY.asChar();
						whiteBonusPoints++;
					} else if (board[i][j] == Markers.BLACKCLAIM.asChar()) {
						board[i][j] = Markers.BLACKTERRITORY.asChar();
						blackBonusPoints++;
					}
				}
		}
		int totalWhite = totalPointsMap.get(Markers.WHITE) + whiteBonusPoints;
		int totalBlack = totalPointsMap.get(Markers.BLACK) + blackBonusPoints;
		totalPointsMap.replace(Markers.WHITE, totalWhite);
		totalPointsMap.replace(Markers.BLACK, totalBlack);
		gameState = gameState.nextStage();
		isGameResultAccepted = false;
		playerWhoAccepted = Markers.EMPTY;
	}

	/** Usuwa z planszy daną listę kamieni*/
	public void kill(List<Stone> list) {
		for (Stone s : list) {
			board[s.getX()][s.getY()] = Markers.EMPTY.asChar();
		}
		listOfStones.removeAll(list);
	}
	
	/** Wiele metod rekurencyjnych zaznacza odwiedzone przez siebie pola planszy dlatego pracują na jej kopii
	 * @return kopia aktualnej planszy*/
	public char[][] copyBoard() {
		char[][] copy = new char[size][size];
		for (int i = 0; i < size; i++) {
			System.arraycopy(board[i], 0, copy[i], 0, size);
		}
		return copy;
	}
	/** Graficznie prezentuje planszę w konsoli*/
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
	/** Drukuje planszę w postaci ciągu znaków*/
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

	public int getSize() {
		return size;
	}
	public int getPoints(Markers color) {
		return totalPointsMap.get(color);
	}

	public Markers getGameState() {
		return gameState;
	}

	public void setGameState(Markers gameState) {
		this.gameState = gameState;
	}

	public Markers getPlayerWhoAccepted() {
		return playerWhoAccepted; 
	}

	public void setGameResultAccepted(Markers playerAccepted) {
		this.isGameResultAccepted = true;
		this.playerWhoAccepted = playerAccepted;
	}
	public boolean isGameResultAccepted() {
		return isGameResultAccepted;
	}

	public boolean arePlayersFound() {
		return arePlayersFound;
	}

	public void setPlayersFound(boolean bool) {
		this.arePlayersFound = bool;
		this.hostID = ""; // RODO
	}

	public String getHostID() {
		return hostID;
	}

	public void setHostID(String hostID) {
		this.hostID = hostID;
	}

	public char[][] getBoard() {
		return board;
	}

}
