package Go.GameMaker;

public class Stone extends AbstractPiece {

	
	boolean isAlive;

	public Stone(int x, int y, char colorAlly, char colorEnemy) {
		this.x = x;
		this.y = y;
		this.colorAlly = colorAlly;
		this.colorEnemy = colorEnemy;
	}

	@Override
	public int countBreaths(char[][] board, int posX, int posY) {
		int breath = 0;
		
		if (board[posX + 1][posY] == ' ')
			breath++;
		if (board[posX - 1][posY] == ' ')
			breath++;
		if (board[posX][posY + 1] == ' ')
			breath++;
		if (board[posX][posY - 1] == ' ')
			breath++;

		return breath;	
	}

}
