package Go.GameMaker;

import static org.junit.Assert.*;

import org.junit.Test;

public class TheGameTest {


	@Test
	public void testAddPlayer() {
		TheGame game = TheGame.getInstance();
		//game.setBoard(19);
		assertEquals("Succes;Black", game.addPlayer("playerOne"));
		assertEquals("Succes;White", game.addPlayer("playerTwo"));
		assertEquals("Succes;Black", game.addPlayer("playerThree"));
		assertEquals("Succes;White", game.addPlayer("playerFour"));
				
	}
	
	@Test
	public void testMakeMove() {
		TheGame game = TheGame.getInstance(); //ten sam singleton
		String command = "playerOne,2,2";
		
		assertEquals("Black", game.getGameState("playerOne").split(";")[0]);
		assertEquals("0", game.makeMove(command).split(";")[0]); 
		assertEquals(Markers.BLACK.asChar(), game.getBoard("playerOne").getBoard()[2][2]);
		assertEquals(Markers.BLACK.asChar(), game.getBoard("playerTwo").getBoard()[2][2]);
		assertEquals("White", game.getGameState("playerOne").split(";")[0]);
		
		command = "playerTwo,2,3";
		assertEquals("0", game.makeMove(command).split(";")[0]);
		assertEquals("NotYrMove", game.makeMove(command).split(";")[0]);
		assertEquals(Markers.WHITE.asChar(), game.getBoard("playerOne").getBoard()[2][3]);
		
		command = "playerOne,2,3";
		assertEquals("Black", game.getGameState("playerOne").split(";")[0]);
		assertEquals("IllegalMove", game.makeMove(command).split(";")[0]); //illegal move
		
	}
	

}
