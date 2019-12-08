package Go.GameMaker;

import static org.junit.Assert.*;

import org.junit.Test;

public class TheGame2Test {

	@Test
	public void test() {
		TheGame game = TheGame.getInstance();
		game.addPlayer("playerOne");
		game.addPlayer("playerTwo");
		
	}

}
