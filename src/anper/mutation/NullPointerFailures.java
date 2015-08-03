package anper.mutation;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class NullPointerFailures {
	
	private int npeFailureCount;
	private int failureCount;
	private List<NullPointerFailure> nullPointerFailures;

	
	public NullPointerFailures() {
		this.failureCount = 0;
		this.npeFailureCount = 0;
		this.nullPointerFailures = new LinkedList<>();
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
					this.npeFailureCount++;
				}
				this.failureCount++;
			}
		}
	}
	
	public List<NullPointerFailure> getNullPointerExceptionFailures(){
		return this.nullPointerFailures;
	}

	public boolean hasFailures() {
		return this.failureCount > 0;
	}
	
	public boolean hasNullPointerExceptionFailures() {
		return this.npeFailureCount > 0;
	}

	public int getFailureCount() {
		return this.failureCount;
	}
	
	public int getNullPointerExceptionFailureCount() {
		return this.npeFailureCount;
	}

}
