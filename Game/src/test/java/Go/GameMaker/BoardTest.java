package Go.GameMaker;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class BoardTest {

	@Test
	public void testFindChains() {
		Board b = new Board(19);
		b.insert(1, 1, 'C', 'B');
		b.insert(1, 2, 'C', 'B');
		b.insert(1, 3, 'C', 'B');
		b.insert(5, 5, 'C', 'B');
		
		char[][] mask = b.board;
		Chain c = new Chain();
		c.setChain(b.findChain(1, 1, 'C', mask));
		Stone s = c.getChain().get(2);
		assertEquals(3, s.y);
		Stone s2 = b.listOfStones.get(0);
		assertEquals(5, s2.x);
	}

}
