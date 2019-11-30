package Go.GameMaker;

import java.util.ArrayList;
import java.util.List;

public class Board {

	int size;
	char[][] board;
	List<Stone> listOfStones;

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
			if (ifPossibleInsert(s)) {
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

	public List<Stone> findChainRecursive(int x, int y, char allayColor, char[][] copyOfBoard) { // Niezbyt przydatne, chyba to usune
		List<Stone> list = new ArrayList<Stone>();
		if (x >= 0 && y >= 0 && x < size && y < size) {
			if (copyOfBoard[x][y] == allayColor) {
				copyOfBoard[x][y] = 'D'; // D like DONE
				for (Stone s : listOfStones) {
					if (s.x == x && s.y == y) {
						list.add(s);
						break;
					}
				}
				list.addAll(findChainRecursive(x + 1, y, allayColor, copyOfBoard));
				list.addAll(findChainRecursive(x - 1, y, allayColor, copyOfBoard));
				list.addAll(findChainRecursive(x, y + 1, allayColor, copyOfBoard));
				list.addAll(findChainRecursive(x, y - 1, allayColor, copyOfBoard));
			}
		}
		return list;
	}

	public int countBreaths(int posX, int posY, char color, char[][] copyBoard) {
		int breath = 0;
		if (posX >= 0 && posY >= 0 && posX < size && posY < size) {
			if (copyBoard[posX][posY] == color) {
				copyBoard[posX][posY] = 'D'; // oznacza juz odwiedzone pole i zapobiega nieskończonej rekursji
				if (copyBoard[posX + 1][posY] == ' ')
					breath++;
				if (copyBoard[posX - 1][posY] == ' ')
					breath++;
				if (copyBoard[posX][posY + 1] == ' ')
					breath++;
				if (copyBoard[posX][posY - 1] == ' ')
					breath++;
				
				breath += countBreaths(posX + 1, posY, color, copyBoard); // rekurencja w przypadku bycia łancuchem
				breath += countBreaths(posX - 1, posY, color, copyBoard);
				breath += countBreaths(posX, posY + 1, color, copyBoard);
				breath += countBreaths(posX, posY - 1, color, copyBoard);
			}
		}
		return breath;
	}

	public boolean ifPossibleInsert(Stone s) { // sprzawdza czy ruch jest legalny

		List<Stone> listEnemy = getEnemyNeighbours(s); // sąsiedni wrogowie
		char[][] copyBoard = copyBoard(); // kopia plaszy
		copyBoard[s.x][s.y] = s.colorAlly;

		int totalKillScore = 0;
		for (Stone st : listEnemy) {
			totalKillScore += possibleKill(st, copyBoard); // sprawdza czy ruch zabija wrogow i zlicza trupy

		}
		if (totalKillScore != 0)
			return true;

		copyBoard = copyBoard();
		copyBoard[s.x][s.y] = s.colorAlly;
		if (countBreaths(s.x, s.y, s.colorAlly, copyBoard) == 0) { // zapobieganie samobojstwom 
			return false;
		}
		return true;
	}

	private List<Stone> getEnemyNeighbours(Stone s) {
		List<Stone> list = new ArrayList<Stone>();
		for (Stone st : listOfStones) {
			if (st.x == s.x + 1 && st.y == s.y && st.colorAlly == s.colorEnemy)
				list.add(st);
			else if (st.x == s.x - 1 && st.y == s.y && st.colorAlly == s.colorEnemy)
				list.add(st);
			else if (st.x == s.x && st.y == s.y + 1 && st.colorAlly == s.colorEnemy)
				list.add(st);
			else if (st.x == s.x && st.y == s.y - 1 && st.colorAlly == s.colorEnemy)
				list.add(st);

		}
		return list;

	}

	private int possibleKill(Stone st, char[][] copyBoard) {
		if (countBreaths(st.x, st.y, st.colorAlly, copyBoard) == 0) {

			return kill(st.x, st.y, st.colorAlly);
		} else
			return 0;
	}

	public int kill(int posX, int posY, char color) { // zabija pionka
		int killScore = 0;
		if (posX >= 0 && posY >= 0 && posX < size && posY < size) {
			if (board[posX][posY] == color) {
				List<Stone> cleaner = new ArrayList<Stone>();
				for (Stone s : listOfStones) {
					if (s.x == posX && s.y == posY) {
						cleaner.add(s);
						board[posX][posY] = ' ';
						killScore++;
					}
				}
				listOfStones.removeAll(cleaner);
				killScore += kill(posX + 1, posY, color); // rekurencja w przypadku bycia łańcuchem
				killScore += kill(posX - 1, posY, color);
				killScore += kill(posX, posY + 1, color);
				killScore += kill(posX, posY - 1, color);
			}
		}
		return killScore;
	}
	public char[][] copyBoard(){
		char[][] copy = new char[size][size];
		for(int i = 0; i < size; i++) {
			System.arraycopy(board[i], 0, copy[i], 0, size);
		}
		return copy;
	}
	
	
}
