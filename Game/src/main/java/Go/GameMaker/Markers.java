package Go.GameMaker;

public enum Markers {
	EMPTY(' ', "Empty"), WHITE('W', "White"), BLACK('B', "Black"), DONE('D'), 
	WHITEPASSED("WhitePassed"), BLACKPASSED("BlackPassed"), BOTHPASSED("BothPassed"),
	BLACKWIN("BlackWins"), WHITEWIN("WhiteWins"),
	WHITEDEAD('H'), BLACKDEAD('L'),
	WHITETERRITORY('I'), BLACKTERRITORY('A');

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
	Markers(char asChar){
		this.asChar = asChar;
		this.asString = null;
	}
	Markers(String asString){
		this.asChar = 0;
		this.asString = asString;
	}

}
