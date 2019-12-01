package Go.GameMaker;

public enum Markers {
	EMPTY(' '), WHITE('W'), BLACK('B'), DONE('D');
	
	 public char asChar() {
        return asChar;
    }

    private final char asChar;

    Markers(char asChar) {
        this.asChar = asChar;
    }

}
