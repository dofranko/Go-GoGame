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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colorAlly;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stone other = (Stone) obj;
		if (colorAlly != other.colorAlly)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
