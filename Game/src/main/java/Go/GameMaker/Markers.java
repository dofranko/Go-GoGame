package Go.GameMaker;

public enum Markers {
	EMPTY('0', "Empty"), WHITE('1', "White"), BLACK('2', "Black"), DONE('D'),  // znaczniki planszy
	WHITEPASSED("WhitePassed"), BLACKPASSED("BlackPassed"), BOTHPASSED("BothPassed"), // skipowanie tury
	BLACKWIN("BlackWins"), WHITEWIN("WhiteWins"), // poddanie walkowerem
	WHITEDEAD('3'), BLACKDEAD('4'), // oznaczenia martwych kamieni wedlug graczy
	WHITEVOTE('5'), BLACKVOTE('6'), // oznaczenia terrytorium wedlug graczy
	WHITETERRITORY('7'), BLACKTERRITORY('8'), // oznaczenia zaakceptowanego terrytorium
	DEADSTONES("PickingDeadStones"), TERRITORY("PickingTerritory"); // fazy koncowe gry

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
