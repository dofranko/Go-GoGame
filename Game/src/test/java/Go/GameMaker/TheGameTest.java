package Go.GameMaker;

import static org.junit.Assert.*;

import org.junit.Test;

public class TheGameTest {


	String playerOne = "playerOne";
	String playerTwo = "playerTwo";
	String playerThree = "playerThree";
	String playerFour = "playerFour";

	@Test
	public void test() {
		TheGame game = TheGame.getInstance(); 
		
		assertEquals("Succes;Black", game.addPlayer(playerOne, 19));
		assertEquals("Succes;White", game.addPlayer(playerTwo, 19));
		assertEquals("Succes;Black", game.addPlayer(playerThree, 9));
		assertEquals("Succes;White", game.addPlayer(playerFour, 9));

		assertEquals("Black", game.getGameState(playerOne).split(";")[0]);
		assertEquals("0", game.makeMove(playerOne+",2,2").split(";")[0]);
		assertEquals(Markers.BLACK.asChar(), game.getBoard(playerOne).getBoard()[2][2]);
		assertEquals(Markers.BLACK.asChar(), game.getBoard(playerTwo).getBoard()[2][2]);
		assertEquals("White", game.getGameState(playerOne).split(";")[0]);

		
		assertEquals("0", game.makeMove(playerTwo+",2,3").split(";")[0]);
		assertEquals("NotYrMove", game.makeMove(playerTwo+",2,3").split(";")[0]);
		assertEquals(Markers.WHITE.asChar(), game.getBoard(playerOne).getBoard()[2][3]);

		
		assertEquals("Black", game.getGameState(playerOne).split(";")[0]);
		assertEquals("IllegalMove", game.makeMove(playerOne+",2,3").split(";")[0]); 
		
		
		assertEquals("Black", game.getGameState(playerOne).split(";")[0]);
		game.skip(playerThree);
		assertEquals("BlackPassed", game.getGameState(playerThree).split(";")[0]);
		assertEquals("0", game.makeMove(playerFour + ",4,4").split(";")[0]);
		assertEquals("Black", game.getGameState(playerFour).split(";")[0]);
		
		assertEquals(playerOne, game.getEnemyID(playerTwo));
		assertEquals(playerFour, game.getEnemyID(playerThree));
		game.giveUp(playerFour);
		assertEquals("BlackWins", game.getGameState(playerThree).split(";")[0]);
		
		
	}
	

}
