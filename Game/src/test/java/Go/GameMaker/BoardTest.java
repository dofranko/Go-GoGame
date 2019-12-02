package Go.GameMaker;

import static org.junit.Assert.*;

import org.junit.Test;


public class BoardTest {
		
	
	@Test
	public void testInsert() {
		Board b = new Board(19);
		assertEquals(0, b.insert(5, 5, Markers.WHITE.asChar()));
		assertEquals(0, b.insert(5, 6, Markers.WHITE.asChar()));
		assertEquals(0, b.insert(5, 7, Markers.WHITE.asChar()));
		assertEquals(0, b.insert(4, 5, Markers.WHITE.asChar()));
	}
	@Test
	public void testPreventSuicide() {
		Board b = new Board(19);
		assertEquals(0, b.insert(2, 3, Markers.WHITE.asChar()));
		assertEquals(0, b.insert(2, 1, Markers.WHITE.asChar()));
		assertEquals(0, b.insert(1, 2, Markers.WHITE.asChar()));
		assertEquals(0, b.insert(3, 2, Markers.WHITE.asChar()));
		
		assertEquals(-1, b.insert(2, 2, Markers.BLACK.asChar())); //illegal
		assertEquals(0, b.insert(2, 2, Markers.WHITE.asChar()));
		
		assertEquals(-1, b.insert(19, 19, Markers.BLACK.asChar())); // outside of the board
		
		//b.printBoard(b.board);
		//b.kill(b.listOfStones); //clear table
		//b.printBoard(b.board);
	}
	@Test
	public void testCountBreaths() {
		Board b = new Board(19);
		
		b.insert(1, 1, Markers.WHITE.asChar());
		b.insert(1, 2, Markers.WHITE.asChar());
		
			
		char[][] copy = b.copyBoard();
		assertEquals(6, b.countBreaths(1, 1, Markers.WHITE.asChar(), copy));
		
		b.insert(2, 2, Markers.WHITE.asChar());
		
		copy = b.copyBoard();
		assertEquals(7, b.countBreaths(1, 1, Markers.WHITE.asChar(), copy));
		
		b.insert(0, 0, Markers.WHITE.asChar());
		
		copy = b.copyBoard();
		assertEquals(2, b.countBreaths(0, 0, Markers.WHITE.asChar(), copy)); //corner
		//b.printBoard(b.board);
		
				
	}
	@Test
	public void testKill() {
		Board b = new Board(9);
		b.insert(5, 5, Markers.BLACK.getEnemy().asChar()); //checking getEnemy method
		b.insert(5, 6, Markers.WHITE.asChar());
		b.insert(5, 7, Markers.WHITE.asChar());
		b.insert(4, 5, Markers.WHITE.asChar());
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[5][5]);
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[5][6]);
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[5][7]);
		assertEquals(Markers.WHITE.asChar(), b.getBoard()[4][5]);
		
		//b.printBoard(b.board);
		
		b.insert(4, 6, Markers.BLACK.asChar());
		b.insert(4, 7, Markers.BLACK.asChar());
		
		b.insert(6, 5, Markers.BLACK.asChar());
		b.insert(6, 6, Markers.BLACK.asChar());
		b.insert(6, 7, Markers.BLACK.asChar());
		
		b.insert(5, 8, Markers.BLACK.asChar());
		b.insert(5, 4, Markers.BLACK.asChar());
		b.insert(4, 4, Markers.BLACK.asChar());
		
		//b.printBoard(b.board);
		assertEquals(4, b.insert(3, 5, Markers.BLACK.asChar())); //scored 4 points
		
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[5][5]);
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[5][6]);
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[5][7]);
		assertEquals(Markers.EMPTY.asChar(), b.getBoard()[4][5]);
		//b.printBoard(b.board);
	}
	
	@Test
	public void testKo() {
		Board b = new Board(19);
		b.insert(0, 3, Markers.BLACK.asChar());
		b.insert(1, 4, Markers.BLACK.asChar());
		b.insert(1, 2, Markers.BLACK.asChar());
		b.insert(2, 3, Markers.BLACK.asChar());
		
		
		b.insert(2, 4, Markers.WHITE.asChar());
		b.insert(2, 2, Markers.WHITE.asChar());
		b.insert(3, 3, Markers.WHITE.asChar());
		
		assertEquals(1, b.insert(1, 3, Markers.WHITE.asChar()));
		//b.printBoard(b.board);
		assertEquals(-1, b.insert(2, 3, Markers.BLACK.asChar()));
		assertEquals(0, b.insert(5, 5, Markers.BLACK.asChar()));
				
		assertEquals(1, b.insert(2, 3, Markers.BLACK.asChar()));
		//b.printBoard(b.board);
	
		
	}
	@Test
	public void testToString() {
		Board b = new Board(9);
		b.insert(0, 3, Markers.BLACK.asChar());
		b.insert(1, 4, Markers.BLACK.asChar());
		b.insert(1, 2, Markers.BLACK.asChar());
		b.insert(2, 3, Markers.BLACK.asChar());
		
		
		b.insert(2, 4, Markers.WHITE.asChar());
		b.insert(2, 2, Markers.WHITE.asChar());
		b.insert(3, 3, Markers.WHITE.asChar());
		b.printBoard(b.getBoard());
		System.out.println(b.boardToString());
		
	}
	
	

}
