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
		ExampleSourceCode example = new ExampleSourceCode();
		example.foo();
		assertTrue(true);
	}
	
	@Test
	public void test2() {
		ExampleSourceCode example = new ExampleSourceCode();
		example.foo2();
		assertTrue(true);
	}
	
	@Test
	public void test3() {
		ExampleSourceCode example = new ExampleSourceCode();
		example.foo3();
		assertTrue(true);
	}
	
	@Test
	public void test4() {
		ExampleSourceCode example = new ExampleSourceCode();
		example.foo4();
		assertTrue(true);
	}

}
