import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class NullPointerFailures {
	
	private int failureCount;
	private List<Failure> failures;
	private List<NullPointerFailure> nullPointerFailures;

	
	public NullPointerFailures(Result result) {
		if (result.getFailureCount() > 0){
			int tempFailureCount = result.getFailureCount(); 
			failures = result.getFailures();
			nullPointerFailures = new LinkedList<NullPointerFailure>();

			for (Failure failure : failures){
				if (failure.getTrace().contains("NullPointerException")){
					NullPointerFailure f = new NullPointerFailure(failure);
					nullPointerFailures.add(f);
				}
			}
			failureCount = nullPointerFailures.size();
		}else{
			failureCount = 0;
		}
	}
	
	public NullPointerFailure getFailure(int i){
		if (i<0 || i>=nullPointerFailures.size())
				throw new IndexOutOfBoundsException();
		return nullPointerFailures.get(i);
	}

	public boolean hasFailures() {
		return failureCount > 0;
	}

	public int getFailureCount() {
		return failureCount;
	}

}
