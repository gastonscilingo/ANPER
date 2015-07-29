import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class Prototype {
	
	
	/*
	 * for (NullPointerException npe : results.exceptions.filter(NullPointerException)) {
		className = npe.getClassName
		method = npe.getMethod
		line = npe.getLine
		addMutGenLimit(program, className, method, line)
		mutate(program, className, method)
	}
	results = test(program,tests)
	if (results.successful) print(YAY!)
	else if (!results.exceptions.contains(NullPointerException)) print(YAY!?)
	else print(NEY!)
	*/

	public static void main(String[] args) {
		
		Class testToRun = ExampleTestSuite.class;
		
		try {
			//MuJavaJunitTestRunner testRunner = new MuJavaJunitTestRunner(testToRun, false);
			//Result result = testRunner.run();
			Result result = JUnitCore.runClasses(testToRun);
			
			if(!result.wasSuccessful()){
				System.out.println("Testing failures : "+result.getFailureCount());
				NullPointerFailures npetr = new NullPointerFailures(result);
				
				if (npetr.hasFailures()){
					
					for (int i =0; i<npetr.getFailureCount() ; i++){
						NullPointerFailure failure = npetr.getFailure(i);
						String className =  failure.getTestedClassName();
						String methodName = failure.getTestedMethodName();
						int failureLine = failure.getFailureLine(); 
						System.out.println(failure.toString());
						/*
						addMutGenLimit(program, className, method, line)
						mutate(program, className, method)
						*/
						
					}
					/*
					results = test(program,tests)
					if (results.successful) print(YAY!)
					else if (!results.exceptions.contains(NullPointerException)) print(YAY!?)
					else print(NEY!)
					*/
					
				}
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

}
