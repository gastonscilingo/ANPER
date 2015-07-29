import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ExampleTestSuite {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		MainExample example = new MainExample();
		example.foo();
		assertTrue(true);
	}
	
	@Test
	public void test2() {
		MainExample example = new MainExample();
		example.foo2();
		assertTrue(true);
	}

}
