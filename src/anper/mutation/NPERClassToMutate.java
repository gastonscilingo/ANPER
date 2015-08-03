package anper.mutation;

import java.util.Set;
import java.util.TreeSet;

public class NPERClassToMutate {
	
	private Set<Integer> lines;
	private String className;
	private Set<String> methods;
	
	public NPERClassToMutate(String className) {
		this.className = className;
		this.lines = new TreeSet<>();
		this.methods = new TreeSet<>();
	}
	
	public void addMethod(String method) {
		this.methods.add(method);
	}
	
	public void addLine(int line) {
		this.lines.add(line);
	}
	
	public String[] getMethods() {
		return this.methods.toArray(new String[this.methods.size()]);
	}
	
	public Set<Integer> getLines() {
		return this.lines;
	}
	
	public String getClassName() {
		return this.className;
	}

}
