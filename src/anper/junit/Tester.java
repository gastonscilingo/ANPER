package anper.junit;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import mujava.loader.Reloader;

/**
 * This class takes a list of tests and runs them giving a {@code NullPointerFailures} object
 * 
 * 
 * @author Simón Emmanuel Gutiérrez Brida
 * @author Gastón Scilingo
 *
 */
public class Tester {
	
	private Reloader reloader;
	private String testBinDir;
	private String binDir;
	private Set<String> packagesToReload;
	private Set<String> tests;
	private JUnitCore juCore;
	
	public Tester(String testBinDir, String binDir, Set<String> packagesToReload, Set<String> tests) {
		this.binDir = binDir;
		this.testBinDir = testBinDir;
		this.packagesToReload = packagesToReload;
		this.tests = tests;
		List<String> classpath = Arrays.asList(new String[]{this.binDir, this.testBinDir});
		this.reloader = new Reloader(classpath,Thread.currentThread().getContextClassLoader());
		this.reloader.markEveryClassInFolderAsReloadable(this.binDir, this.packagesToReload);
		this.reloader.markEveryClassInFolderAsReloadable(this.testBinDir, this.packagesToReload);
		this.juCore = new JUnitCore();
	}
	
	public NullPointerFailures runTests(Set<String> fixableClasses) {
		NullPointerFailures failures = new NullPointerFailures(fixableClasses);
		Class<?> testToRun = null;
		for (String test : this.tests) {
			this.reloader = this.reloader.getLastChild();
			try {
				testToRun = this.reloader.rloadClass(test, true);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Result testResult = this.juCore.run(testToRun);
			failures.add(testResult);
		}
		return failures;
	}

}
