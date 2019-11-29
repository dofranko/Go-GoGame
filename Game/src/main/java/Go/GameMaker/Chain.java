package Go.GameMaker;

import java.util.ArrayList;
import java.util.List;

public class Chain extends AbstractPiece {

	public List<Stone> chain;

	public Chain() {
		chain = new ArrayList<Stone>();
	}

	public List<Stone> getChain() {
		return chain;
	}
	public void setChain(List<Stone> list) {
		this.chain = list;
	}

	void add(Stone s) {
		chain.add(s);
		this.x = s.x;
		this.y = s.y;
	}

	void removeChain() {
		chain.clear();
	}

	@Override
	public int countBreaths(char[][] board, int x0, int y0) {
		int breath = 0;
		for(Stone s : chain) {
			breath += countIndividually(board, s);
		}
		return breath;
	}

	int countIndividually(char[][] board, Stone s) {
		int breath = 0;

		if (board[s.x + 1][s.y] == ' ')
			breath++;
		if (board[s.x - 1][s.y] == ' ')
			breath++;
		if (board[s.x][s.y + 1] == ' ')
			breath++;
		if (board[s.x][s.y - 1] == ' ')
			breath++;

		return breath;
	}

}
