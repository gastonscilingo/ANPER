import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class Prototype {

	public static void main(String[] args) {
		
		Class testToRun = ExampleTestSuite.class;
		
		try {
			//MuJavaJunitTestRunner testRunner = new MuJavaJunitTestRunner(testToRun, false);
			//Result result = testRunner.run();
			Result result = JUnitCore.runClasses(testToRun);
			System.out.println(result.getFailures().get(0).getTrace());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		

	}

}
