package Go.GameMaker;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Board {

	private int size;
	private char[][] board;
	private List<Stone> listOfStones;
	private Stone koCandidate;

	public Board(int size) {
		this.size = size;
		board = new char[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = ' '; // empty board
			}
		}
		listOfStones = new ArrayList<Stone>();
	}

	public int insert(int x, int y, char allyColor) {
		if (x >= 0 && y >= 0 && x < size && y < size) {
			Stone s = new Stone(x, y, allyColor);
			int pointsScored = possibleInsert(s);
			if (board[x][y] == ' ' && pointsScored >= 0) {
				board[x][y] = allyColor;
				listOfStones.add(s);
				return pointsScored;
			}
		}
		return -1; // illegal move

	}

	public void simpleInsert(int x, int y, char allyColor) {
		if (x >= 0 && y >= 0 && x < size && y < size) {
			Stone s = new Stone(x, y, allyColor);
			board[x][y] = allyColor;
			listOfStones.add(s);
		}

	}

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

	private int possibleInsert(Stone stone) { // sprzawdza czy ruch jest legalny

		if (blockedKo(stone))
			return -1;
		int x = stone.getX();
		int y = stone.getY();
		char colorAlly = stone.getColorAlly();

		List<Stone> listEnemy = getEnemyNeighbours(stone); // sąsiedni wrogowie

		char[][] copyBoard = copyBoard(); // kopia plaszy
		copyBoard[x][y] = colorAlly;

		List<Stone> totalKillScore = new ArrayList<Stone>();
		for (Stone s : listEnemy) {
			copyBoard = copyBoard();
			copyBoard[x][y] = colorAlly;
			if (countBreaths(s.getX(), s.getY(), s.getColorAlly(), copyBoard) == 0) { // sprawdza czy ruch zabija wrogow
																						// i zlicza trupy
				copyBoard = copyBoard();
				copyBoard[x][y] = colorAlly;
				totalKillScore.addAll(killList(s.getX(), s.getY(), s.getColorAlly(), copyBoard));

			}
		}

		if (totalKillScore.size() == 1)
			koCandidate = totalKillScore.get(0);

		if (totalKillScore.size() != 0) {
			kill(totalKillScore);
			return totalKillScore.size();
		}

		copyBoard = copyBoard();
		copyBoard[x][y] = colorAlly;
		if (countBreaths(x, y, colorAlly, copyBoard) == 0) // zapobieganie samobojstwom
			return -1;

		return 0;
	}

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

	private Set<Stone> killList(int posX, int posY, char color, char[][] copyBoard) { // dodaje pionka do killListy
		Set<Stone> cleaner = new LinkedHashSet<Stone>();
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && copyBoard[posX][posY] == color) {

			copyBoard[posX][posY] = Markers.DONE.asChar();
			for (Stone s : listOfStones) {
				if (s.getX() == posX && s.getY() == posY) {
					cleaner.add(s);
					break;

				}
			}
			cleaner.addAll(killList(posX + 1, posY, color, copyBoard)); // rekurencja w przypadku bycia łańcuchem
			cleaner.addAll(killList(posX - 1, posY, color, copyBoard));
			cleaner.addAll(killList(posX, posY + 1, color, copyBoard));
			cleaner.addAll(killList(posX, posY - 1, color, copyBoard));

		}
		return cleaner;
	}

	private boolean blockedKo(Stone stone) {
		if (koCandidate != null) {
			if (koCandidate.getX() == stone.getX() && koCandidate.getY() == stone.getY()
					&& koCandidate.getColorAlly() == stone.getColorAlly()) // próba ruchu w to samo pole
				return true;
			else if (koCandidate.getColorAlly() == stone.getColorAlly()) {// ruch został wykonany gdzie indziej, reset ko
				koCandidate = null;
			}
		}
		return false;
	}

	public void kill(List<Stone> list) {
		for (Stone s : list) {
			board[s.getX()][s.getY()] = ' ';
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
				String mark = "";
				if (board[i][j] == Markers.EMPTY.asChar())
					mark = "0";
				else if (board[i][j] == Markers.WHITE.asChar())
					mark = "1";
				else
					mark = "2";
				string += ("x" + mark + String.valueOf(i) + "," + "y" + mark + String.valueOf(j) + ",");
			}
			string += ";";

		}
		return string;

	}

	public int getSize() {
		return size;
	}

	public char[][] getBoard() {
		return board;
	}

	public List<Stone> getListOfStones() {
		return listOfStones;
	}

}
