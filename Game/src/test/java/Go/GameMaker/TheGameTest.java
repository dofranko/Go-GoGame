package Go.GameMaker;

import static org.junit.Assert.*;

import org.junit.Test;

public class TheGameTest {


	@Test
	public void testAddPlayer() {
		TheGame game = TheGame.getInstance();
		game.setBoard(19);
		assertEquals("Succes;Black", game.addPlayer("playerOne"));
		assertEquals("Succes;White", game.addPlayer("playerTwo"));
		assertEquals("Error", game.addPlayer("playerThree"));
		
		
	}
	
	@Test
	public void testMakeMove() {
		TheGame game = TheGame.getInstance(); //ten sam singleton
		String command = "playerOne,2,2";
		
		assertEquals("Black", game.whoseMove());
		assertEquals("0", game.makeMove(command).split(";")[0]); //pierwsza liczba ze stringa zwrotnego
		assertEquals(Markers.BLACK.asChar(), game.getBoard().getBoard()[2][2]);
		assertEquals("White", game.whoseMove());
		
		command = "playerTwo,2,3";
		assertEquals("0", game.makeMove(command).split(";")[0]);
		assertEquals("NotYrMove", game.makeMove(command).split(";")[0]);
		assertEquals(Markers.WHITE.asChar(), game.getBoard().getBoard()[2][3]);
		
		command = "playerOne,2,3";
		assertEquals("Black", game.whoseMove());
		assertEquals("-1", game.makeMove(command).split(";")[0]); //illegal move
		
	}

}
