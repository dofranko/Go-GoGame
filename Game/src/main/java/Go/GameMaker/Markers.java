package Go.GameMaker;

public enum Markers {
	EMPTY('0', "Empty"), WHITE('1', "White"), BLACK('2', "Black"), DONE('D'), // znaczniki planszy
	WHITEPASSED("WhitePassed"), BLACKPASSED("BlackPassed"), BOTHPASSED("BothPassed"), // skipowanie tury
	BLACKWIN("BlackWins"), WHITEWIN("WhiteWins"), // Wygrana
	WHITEDEAD('3'), BLACKDEAD('4'), // oznaczenia martwych kamieni wedlug graczy
	WHITECLAIM('5'), BLACKCLAIM('6'), // oznaczenia terrytorium wedlug graczy
	WHITETERRITORY('7'), BLACKTERRITORY('8'), // oznaczenia zaakceptowanego terrytorium
	DEADSTONES("PickingDeadStones"), TERRITORY("PickingTerritory"), THEEND("End"),// fazy koncowe gry
	WHITEACCEPTED("WhiteAccepted"), BLACKACCEPTED("BlackAccepted"); // sygnały aprobaty

	private final char asChar;
	private final String asString;

	Markers(char asChar, String asString) {
		this.asChar = asChar;
		this.asString = asString;
	}

	Markers(char asChar) {
		this.asChar = asChar;
		this.asString = null;
	}

	Markers(String asString) {
		this.asChar = 0;
		this.asString = asString;
	}

	public char asChar() {
		return asChar;
	}

	public String asString() {
		return asString;
	}

	public Markers asEnemy() {
		if (this == WHITE)
			return BLACK;
		if (this == BLACK)
			return WHITE;
		return null;
	}

	public Markers asClaimTerritory() {
		if(this == WHITE)
			return WHITECLAIM;
		if(this == BLACK)
			return BLACKCLAIM;
		return null;
	}
	public Markers asAccepted() {
		if (this == WHITE)
			return WHITEACCEPTED;
		if (this == BLACK)
			return BLACKACCEPTED;
		return EMPTY;
	}
	public Markers asWinner() {
		if (this == WHITE)
			return WHITEWIN;
		if (this == BLACK)
			return BLACKWIN;
		return null;
	}
	public Markers asPassed() {
		if (this == WHITE)
			return WHITEPASSED;
		if (this == BLACK)
			return BLACKPASSED;
		return null;
	}
	public Markers nextStage() {
		if (this == BOTHPASSED)
			return TERRITORY;
		if (this == TERRITORY)
			return THEEND;
		return null;
	}
	public static Markers getColor(int i) {
		if(i == 0)
			return WHITE;
		else
			return BLACK;
	}

	
}
