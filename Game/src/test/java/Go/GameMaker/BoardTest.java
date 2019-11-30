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
	public void testInsert2() {
		Board b = new Board(19);
		assertTrue(b.insert(2, 3, WHITE, BLACK));
		assertTrue(b.insert(2, 1, WHITE, BLACK));
		assertTrue(b.insert(1, 2, WHITE, BLACK));
		assertTrue(b.insert(3, 2, WHITE, BLACK));
		assertFalse(b.insert(2, 2, BLACK, WHITE));
	}
	@Test
	public void testCountBreaths() {
		Board b = new Board(19);
		
		b.insert(1, 1, WHITE, BLACK);
		b.insert(1, 2, WHITE, BLACK);
		
			
		char[][] copy = b.copyBoard();
		assertEquals(6, b.countBreaths(1, 1, WHITE, copy));
				
	}
	

}
