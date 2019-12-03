package Go.GameMaker;

public enum Markers {
	EMPTY(' ', "Empty"), WHITE('W', "White"), BLACK('B', "Black"), DONE('D', "Done");

	public char asChar() {
		return asChar;
	}
	public String asString() {
		return asString;
	}

	public Markers getEnemy() {
		if (this == WHITE)
			return BLACK;
		else if (this == BLACK)
			return WHITE;
		else 
			return null;
	}

	private final char asChar;
	private final String asString;

	Markers(char asChar, String asString) {
		this.asChar = asChar;
		this.asString = asString;
	}

}
