package Go.GameMaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Board {

	int size;
	char[][] board;
	List<Stone> listOfStones;
	Stone koCandidate;

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

	public boolean insert(int x, int y, char allyColor, char enemyColor) {
		if (x >= 0 && y >= 0 && x < size && y < size) {
			Stone s = new Stone(x, y, allyColor, enemyColor);
			if (board[x][y] == ' ' && possibleInsert(s)) {
				board[x][y] = allyColor;
				listOfStones.add(s);
				return true;
			}
		}
		return false;

	}

	public void simpleInsert(int x, int y, char allyColor, char enemyColor) {
		if (x >= 0 && y >= 0 && x < size && y < size) {
			Stone s = new Stone(x, y, allyColor, enemyColor);
			board[x][y] = allyColor;
			listOfStones.add(s);
		}

	}

	public int countBreaths(int posX, int posY, char color, char[][] copyBoard) {
		int breath = 0;
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && copyBoard[posX][posY] == color) {
			copyBoard[posX][posY] = 'D'; // oznacza juz odwiedzone pole i zapobiega nieskończonej rekursji
			if (posX != size - 1 && copyBoard[posX + 1][posY] == ' ') {
				copyBoard[posX + 1][posY] = 'D';
				breath++;
			}
			if (posX != 0 && copyBoard[posX - 1][posY] == ' ') {
				copyBoard[posX - 1][posY] = 'D';
				breath++;
			}

			if (posY != size - 1 && copyBoard[posX][posY + 1] == ' ') {
				copyBoard[posX][posY + 1] = 'D';
				breath++;
			}

			if (posY != 0 && copyBoard[posX][posY - 1] == ' ') {
				copyBoard[posX][posY - 1] = 'D';
				breath++;
			}

			breath += countBreaths(posX + 1, posY, color, copyBoard); // rekurencja w przypadku bycia łancuchem
			breath += countBreaths(posX - 1, posY, color, copyBoard);
			breath += countBreaths(posX, posY + 1, color, copyBoard);
			breath += countBreaths(posX, posY - 1, color, copyBoard);
		}

		return breath;
	}

	private boolean possibleInsert(Stone stone) { // sprzawdza czy ruch jest legalny

		if(blockedKo(stone))
			return false;

		List<Stone> listEnemy = getEnemyNeighbours(stone); // sąsiedni wrogowie

		char[][] copyBoard = copyBoard(); // kopia plaszy
		copyBoard[stone.x][stone.y] = stone.colorAlly;

		List<Stone> totalKillScore = new ArrayList<Stone>();
		for (Stone s : listEnemy) {
			copyBoard = copyBoard();
			copyBoard[stone.x][stone.y] = stone.colorAlly;
			if (countBreaths(s.x, s.y, s.colorAlly, copyBoard) == 0) { // sprawdza czy ruch zabija wrogow i zlicza trupy
				copyBoard = copyBoard();
				copyBoard[stone.x][stone.y] = stone.colorAlly;
				totalKillScore.addAll(killList(s.x, s.y, s.colorAlly, copyBoard));

			}
		}
		
		if (totalKillScore.size() == 1) 
			koCandidate = totalKillScore.get(0);
		
		if (totalKillScore.size() != 0) {
			kill(totalKillScore);
			return true;
		}

		copyBoard = copyBoard();
		copyBoard[stone.x][stone.y] = stone.colorAlly;
		if (countBreaths(stone.x, stone.y, stone.colorAlly, copyBoard) == 0)  // zapobieganie samobojstwom
			return false;
		
		return true;
	}

	private List<Stone> getEnemyNeighbours(Stone stone) {
		List<Stone> list = new ArrayList<Stone>();
		for (Stone s : listOfStones) {
			if (s.x == stone.x + 1 && s.y == stone.y && s.colorAlly == stone.colorEnemy)
				list.add(s);
			else if (s.x == stone.x - 1 && s.y == stone.y && s.colorAlly == stone.colorEnemy)
				list.add(s);
			else if (s.x == stone.x && s.y == stone.y + 1 && s.colorAlly == stone.colorEnemy)
				list.add(s);
			else if (s.x == stone.x && s.y == stone.y - 1 && s.colorAlly == stone.colorEnemy)
				list.add(s);

		}
		return list;

	}

	private Set<Stone> killList(int posX, int posY, char color, char[][] copyBoard) { // dodaje pionka do killListy
		Set<Stone> cleaner = new LinkedHashSet<Stone>();
		if (posX >= 0 && posY >= 0 && posX < size && posY < size && copyBoard[posX][posY] == color) {

			copyBoard[posX][posY] = 'D';
			for (Stone s : listOfStones) {
				if (s.x == posX && s.y == posY) {
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
			if (koCandidate.x == stone.x && koCandidate.y == stone.y && koCandidate.colorAlly == stone.colorAlly) // próba ruchu w to samo pole
				return true;
			else if(koCandidate.colorAlly == stone.colorAlly) {// ruch został wykonany gdzie indziej
				koCandidate = null;
			}
		}
		return false;
	}

	public void kill(List<Stone> list) {
		for (Stone s : list) {
			board[s.x][s.y] = ' ';
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
		for (int i = size - 1; i >= 0; i--) {
			String s = "";
			for (int j = 0; j < size - 1; j++) {
				if (arr[j][i] == ' ') {
					s += "[ ]";
				} else
					s += (" " + String.valueOf(arr[j][i]) + " ");
			}
			System.out.println(s);
		}
	}

}
