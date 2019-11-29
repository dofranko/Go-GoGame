package Go.GameMaker;

import java.util.ArrayList;
import java.util.List;

public class Board {

	int size;
	char[][] board;
	List<Stone> listOfStones;
	List<Chain> listOfChains;

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

	public void insert(int x, int y, char allyColor, char enemyColor) {
		board[x][y] = allyColor;
		listOfStones.add(new Stone(x, y, allyColor, enemyColor));

	}

	List<Stone> findChain(int x, int y, char allayColor, char[][] mask) {
		List<Stone> list = new ArrayList<Stone>();
		if (x >= 0 && y >= 0 && x < size && y < size) {
			if (mask[x][y] == allayColor) {
				mask[x][y] = 'D'; // D like DONE
				for (Stone s : listOfStones) {
					if (s.x == x && s.y == y) {
						list.add(s);
						listOfStones.remove(s);
						break;
					}
				}
				list.addAll(findChain(x + 1, y, allayColor, mask));
				list.addAll(findChain(x - 1, y, allayColor, mask));
				list.addAll(findChain(x, y + 1, allayColor, mask));
				list.addAll(findChain(x, y - 1, allayColor, mask));
			}
			
		}
		return list;
	}

	boolean ifPossibleInsert(AbstractPiece s) {
		if (possibleKill(s))
			return true;
		if (s.countBreaths(board, s.x, s.y) == 0)
			return false;
		return true;
	}

	private boolean possibleKill(AbstractPiece s) {
		if (s.countBreaths(board, s.x + 1, s.y) == 0)
			return true;
		return false;

	}

}
