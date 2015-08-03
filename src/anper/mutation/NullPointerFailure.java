package anper.mutation;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;


public class NullPointerFailure {
	
	private String testSuiteClassName;
	private String testMethodName;
	private String testedClassName;
	private String testedMethodName;
	private int failureLine;
	private String outputTrace;
	private String testedFileName;
	
	
	public NullPointerFailure(Failure failure) {
		// parse failure information
		outputTrace = failure.getTrace();
		Description description = failure.getDescription();
		testSuiteClassName = description.getClassName();
		testMethodName = description.getMethodName();
		
		String [] output = outputTrace.split("at ");
		String traceLine = output[1]; /* eg. MainExample.foo2(MainExample.java:19) */
		
		int i1 = traceLine.indexOf('.');
		int i2 = traceLine.indexOf('(', i1);
		int i3 = traceLine.indexOf(':');
		int i4 = traceLine.indexOf(')', i3);
		
		testedFileName = traceLine.substring(i2+1, i3);
		testedClassName = traceLine.substring(0, i1);
		testedMethodName = traceLine.substring(i1+1, i2);
		failureLine = Integer.valueOf(traceLine.substring(i3+1, i4)).intValue();
	}

	/**
	 * @return the testSuiteClassName
	 */
	public String getTestSuiteClassName() {
		return testSuiteClassName;
	}

	/**
	 * @return the testMethodName
	 */
	public String getTestMethodName() {
		return testMethodName;
	}

	/**
	 * @return the testedClassName
	 */
	public String getTestedClassName() {
		return testedClassName;
	}

	/**
	 * @return the testedMethodName
	 */
	public String getTestedMethodName() {
		return testedMethodName;
	}
	
	/**
	 * @return the failureLine
	 */
	public int getFailureLine() {
		return failureLine;
	}

	/**
	 * @return the testedFileName
	 */
	public String getTestedFileName() {
		return testedFileName;
	}
	
	/**
	 * @return the outputTrace
	 */
	public String getOutputTrace() {
		return outputTrace;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NullPointerFailure [testSuiteClassName=" + testSuiteClassName
				+ ", testMethodName=" + testMethodName + ", testedClassName="
				+ testedClassName + ", testedMethodName=" + testedMethodName
				+ ", failureLine=" + failureLine + ", testedFileName="
				+ testedFileName + "]";
	}
	

}
