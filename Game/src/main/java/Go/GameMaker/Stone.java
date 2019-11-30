package Go.GameMaker;

public class Stone {
	int x, y;
	char colorAlly;
	char colorEnemy;
	boolean isAlive;

	public Stone(int x, int y, char colorAlly, char colorEnemy) {
		this.x = x;
		this.y = y;
		this.colorAlly = colorAlly;
		this.colorEnemy = colorEnemy;
		this.isAlive = true;
	}

}
