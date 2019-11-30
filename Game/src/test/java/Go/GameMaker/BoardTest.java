package Go.GameMaker;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class BoardTest {
	char BLACK = 'B';
	char WHITE = 'W';

	@Test
	public void testInsert() {
		Board b = new Board(19);
		assertTrue(b.insert(5, 5, WHITE, BLACK));
		assertTrue(b.insert(5, 6, WHITE, BLACK));
		assertTrue(b.insert(5, 7, WHITE, BLACK));
		assertTrue(b.insert(4, 5, WHITE, BLACK));
	}
	@Test
	public void testPreventSuicide() {
		Board b = new Board(19);
		assertTrue(b.insert(2, 3, WHITE, BLACK));
		assertTrue(b.insert(2, 1, WHITE, BLACK));
		assertTrue(b.insert(1, 2, WHITE, BLACK));
		assertTrue(b.insert(3, 2, WHITE, BLACK));
		assertFalse(b.insert(2, 2, BLACK, WHITE));
		assertTrue(b.insert(2, 2, WHITE, BLACK));
		assertFalse(b.insert(19, 19, BLACK, WHITE));
		//b.printBoard(b.board);
		//b.kill(b.listOfStones); //clear table
		//System.out.println("---------------------------------------------------------");
		//b.printBoard(b.board);
	}
	@Test
	public void testCountBreaths() {
		Board b = new Board(19);
		
		b.insert(1, 1, WHITE, BLACK);
		b.insert(1, 2, WHITE, BLACK);
		
			
		char[][] copy = b.copyBoard();
		assertEquals(6, b.countBreaths(1, 1, WHITE, copy));
		
		b.insert(2, 2, WHITE, BLACK);
		
		copy = b.copyBoard();
		assertEquals(7, b.countBreaths(1, 1, WHITE, copy));
		
		b.insert(0, 0, WHITE, BLACK);
		
		copy = b.copyBoard();
		assertEquals(2, b.countBreaths(0, 0, WHITE, copy));
		//b.printBoard(b.board);
		
				
	}
	@Test
	public void testKill() {
		Board b = new Board(9);
		b.insert(5, 5, WHITE, BLACK);
		b.insert(5, 6, WHITE, BLACK);
		b.insert(5, 7, WHITE, BLACK);
		b.insert(4, 5, WHITE, BLACK);
		assertEquals('W', b.board[5][5]);
		assertEquals('W', b.board[5][6]);
		assertEquals('W', b.board[5][7]);
		assertEquals('W', b.board[4][5]);
		//b.printBoard(b.board);
		
	
		b.insert(4, 6, BLACK, WHITE);
		b.insert(4, 7, BLACK, WHITE);
		
		
		b.insert(6, 5, BLACK, WHITE);
		b.insert(6, 6, BLACK, WHITE);
		b.insert(6, 7, BLACK, WHITE);
		
		b.insert(5, 8, BLACK, WHITE);
		b.insert(5, 4, BLACK, WHITE);
		b.insert(4, 4, BLACK, WHITE);
		b.insert(3, 5, BLACK, WHITE);
		
		
		assertEquals(' ', b.board[5][5]);
		assertEquals(' ', b.board[5][6]);
		assertEquals(' ', b.board[5][7]);
		assertEquals(' ', b.board[4][5]);
		//b.printBoard(b.board);
	}
	
	@Test
	public void testKo() {
		Board b = new Board(19);
		b.insert(0, 3, BLACK, WHITE);
		b.insert(1, 4, BLACK, WHITE);
		b.insert(1, 2, BLACK, WHITE);
		b.insert(2, 3, BLACK, WHITE);
		
		
		b.insert(2, 4, WHITE, BLACK);
		b.insert(2, 2, WHITE, BLACK);
		b.insert(3, 3, WHITE, BLACK);
		
		assertTrue(b.insert(1, 3, WHITE, BLACK));
		b.printBoard(b.board);
		assertFalse(b.insert(2, 3, BLACK, WHITE));
		assertTrue(b.insert(5, 5, BLACK, WHITE));
				
		assertTrue(b.insert(2, 3, BLACK, WHITE));
		b.printBoard(b.board);
	
		
	}
	
	

}
