package Go.GameMaker;

public class Stone {

	private int x, y;
	private char colorAlly;
	private char colorEnemy;
	//boolean isAlive;

	public Stone(int x, int y, char colorAlly) {
		this.x = x;
		this.y = y;
		//this.isAlive = true;
		this.colorAlly = colorAlly;
		if (colorAlly == Markers.WHITE.asChar())
			this.colorEnemy = Markers.BLACK.asChar();
		else
			this.colorEnemy = Markers.WHITE.asChar();
		
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public char getColorAlly() {
		return colorAlly;
	}

	public char getColorEnemy() {
		return colorEnemy;
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
