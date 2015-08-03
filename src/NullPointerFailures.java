import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class NullPointerFailures {
	
	private int failureCount;
	private List<NullPointerFailure> nullPointerFailures;

	
	public NullPointerFailures() {
		this.failureCount = 0;
		this.nullPointerFailures = new LinkedList<NullPointerFailure>();
	}
	
	public NullPointerFailures(List<Result> results) {
		this();
		for	(Result r : results) {
			add(r);
		}
	}
	
	public void add(Result result) {
		if (result.getFailureCount() > 0){
			for (Failure failure : result.getFailures()){
				if (failure.getTrace().contains("java.lang.NullPointerException")){
					NullPointerFailure f = new NullPointerFailure(failure);
					this.nullPointerFailures.add(f);
					this.failureCount++;
				}
			}
		}
	}
	
	public List<NullPointerFailure> getFailures(){
		return this.nullPointerFailures;
	}

	public boolean hasFailures() {
		return failureCount > 0;
	}

	public int getFailureCount() {
		return failureCount;
	}

}
