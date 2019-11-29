package Go.GameMaker;

public abstract class AbstractPiece {
	int x, y;
	char colorAlly;
	char colorEnemy;
	abstract int countBreaths(char board[][], int x, int y);

}
