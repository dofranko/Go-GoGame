package Go.GameMaker;

import static org.junit.Assert.*;

import org.junit.Test;


public class BoardTest {
		
	
	@Test
	public void testInsert() {
		Board b = new Board(19);
		assertEquals(0, b.insert(5, 5, Markers.WHITE));
		assertEquals(0, b.insert(5, 6, Markers.WHITE));
		assertEquals(0, b.insert(5, 7, Markers.WHITE));
		assertEquals(0, b.insert(4, 5, Markers.WHITE));
	}
	@Test
	public void testPreventSuicide() {
		Board b = new Board(19);
		assertEquals(0, b.insert(2, 3, Markers.WHITE));
		assertEquals(0, b.insert(2, 1, Markers.WHITE));
		assertEquals(0, b.insert(1, 2, Markers.WHITE));
		assertEquals(0, b.insert(3, 2, Markers.WHITE));
		
		assertEquals(-1, b.insert(2, 2, Markers.BLACK)); //illegal
		assertEquals(0, b.insert(2, 2, Markers.WHITE));
		
		assertEquals(-1, b.insert(19, 19, Markers.BLACK)); // outside of the board
		
		//b.printBoard(b.board);
		//b.kill(b.listOfStones); //clear table
		//b.printBoard(b.board);
	}
	@Test
	public void testCountBreaths() {
		Board b = new Board(19);
		
		b.insert(1, 1, Markers.WHITE);
		b.insert(1, 2, Markers.WHITE);
		
			
		char[][] copy = b.copyBoard();
		assertEquals(6, b.countLiberties(1, 1, Markers.WHITE.asChar(), copy));
		
		b.insert(2, 2, Markers.WHITE);
		
		copy = b.copyBoard();
		assertEquals(7, b.countLiberties(1, 1, Markers.WHITE.asChar(), copy));
		
		b.insert(0, 0, Markers.WHITE);
		
		copy = b.copyBoard();
		assertEquals(2, b.countLiberties(0, 0, Markers.WHITE.asChar(), copy)); //corner
		//b.printBoard(b.board);
		
				
	}
	@Test
	public void testKill() {
		Board b = new Board(9);
		b.insert(5, 5, Markers.BLACK.getEnemy()); //checking getEnemy method
		b.insert(5, 6, Markers.WHITE);
		b.insert(5, 7, Markers.WHITE);
		b.insert(4, 5, Markers.WHITE);
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[5][5]);
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[5][6]);
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[5][7]);
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[4][5]);
		
		//b.printBoard(b.board);
		
		b.insert(4, 6, Markers.BLACK);
		b.insert(4, 7, Markers.BLACK);
		
		b.insert(6, 5, Markers.BLACK);
		b.insert(6, 6, Markers.BLACK);
		b.insert(6, 7, Markers.BLACK);
		
		b.insert(5, 8, Markers.BLACK);
		b.insert(5, 4, Markers.BLACK);
		b.insert(4, 4, Markers.BLACK);
		
		//b.printBoard(b.board);
		assertEquals(4, b.insert(3, 5, Markers.BLACK)); //scored 4 points
		
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[5][5]);
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[5][6]);
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[5][7]);
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[4][5]);
		//b.printBoard(b.board);
	}
	
	@Test
	public void testKo() {
		Board b = new Board(19);
		b.insert(0, 3, Markers.BLACK);
		b.insert(1, 4, Markers.BLACK);
		b.insert(1, 2, Markers.BLACK);
		b.insert(2, 3, Markers.BLACK);
		
		
		b.insert(2, 4, Markers.WHITE);
		b.insert(2, 2, Markers.WHITE);
		b.insert(3, 3, Markers.WHITE);
		
		assertEquals(1, b.insert(1, 3, Markers.WHITE));
		//b.printBoard(b.board);
		assertEquals(-1, b.insert(2, 3, Markers.BLACK));
		assertEquals(0, b.insert(5, 5, Markers.BLACK));
				
		assertEquals(1, b.insert(2, 3, Markers.BLACK));
		//b.printBoard(b.board);
	
		
	}
	@Test
	public void testToString() {
		Board b = new Board(9);
		b.insert(0, 3, Markers.BLACK);
		b.insert(1, 4, Markers.BLACK);
		b.insert(1, 2, Markers.BLACK);
		b.insert(2, 3, Markers.BLACK);
		
		
		b.insert(2, 4, Markers.WHITE);
		b.insert(2, 2, Markers.WHITE);
		b.insert(3, 3, Markers.WHITE);
		b.printBoard(b.getBoard());
		System.out.println(b.boardToString());
		
	}
	
	

}
