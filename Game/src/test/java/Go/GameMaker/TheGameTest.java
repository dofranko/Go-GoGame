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
		int boardID = game.getBoardsID().get("playerOne");
		assertEquals(0, boardID);
		boardID = game.getBoardsID().get("playerTwo");
		assertEquals(0, boardID);
		boardID = game.getBoardsID().get("playerThree");
		assertEquals(1, boardID);
		
		
		
	}
	
	@Test
	public void testMakeMove() {
		TheGame game = TheGame.getInstance(); //ten sam singleton
		String command = "playerOne,2,2";
		
		assertEquals("Black", game.whoseMove("playerOne").split(";")[0]);
		assertEquals("0", game.makeMove(command).split(";")[0]); //pierwsza liczba ze stringa zwrotnego
		assertEquals(Markers.BLACK.asChar(), game.getBoard(0).getBoard()[2][2]);
		assertEquals("White", game.whoseMove("playerOne").split(";")[0]);
		
		command = "playerTwo,2,3";
		assertEquals("0", game.makeMove(command).split(";")[0]);
		assertEquals("NotYrMove", game.makeMove(command).split(";")[0]);
		assertEquals(Markers.WHITE.asChar(), game.getBoard(0).getBoard()[2][3]);
		
		command = "playerOne,2,3";
		assertEquals("Black", game.whoseMove("playerOne").split(";")[0]);
		assertEquals("IllegalMove", game.makeMove(command).split(";")[0]); //illegal move
		
	}
	

}
