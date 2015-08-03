import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import junit.framework.JUnit4TestCaseFacade;
import main.api.DependencyScanner;

import org.junit.internal.builders.JUnit4Builder;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.JUnit4;

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
		
		String relativePath = "/Users/gaston/Documents/EclipseWorkspace/AutomaticSoftwareRepairCourse/";
		String srcPath = relativePath+"src/";
		String binPath = relativePath+"bin/";
		String testSuitesPath = relativePath+"test/";
		String outputPath = relativePath+"outputMutants/";
		Class testToRun = ExampleTestSuite.class;

		
		List<String> classesInOriginalSrcDir = scanBinPath(binPath);
		for (int i = 0; i< classesInOriginalSrcDir.size(); i++){
			System.out.println(classesInOriginalSrcDir.get(i));
		}
		
		try {
			//MuJavaJunitTestRunner testRunner = new MuJavaJunitTestRunner(testToRun, false);
			//Result result = testRunner.run();
			Result result = JUnitCore.runClasses(testToRun);

			
			if(!result.wasSuccessful()){
				System.out.println("Testing failures : "+result.getFailureCount());
				NullPointerFailures npetr = new NullPointerFailures();
				npetr.add(result);
				if (npetr.hasFailures()){
					List<NullPointerFailure> failures = npetr.getFailures();

					for (NullPointerFailure failure : failures){
						String className =  failure.getTestedClassName();
						String methodName = failure.getTestedMethodName();
						int failureLine = failure.getFailureLine(); 
						System.out.println(failure.toString());
						//System.out.println(failure.getOutputTrace());

						if (classesInOriginalSrcDir.contains(className)){
							// try to repair
							System.out.println("contains!! "+className);
						}
						
						File file = null;
						Path path = new File(outputPath+failure.getTestedFileName()).toPath();
						if (!Files.exists(path, java.nio.file.LinkOption.NOFOLLOW_LINKS) ){
							file = new File(srcPath+failure.getTestedFileName());
						}else{
							file = new File(outputPath+failure.getTestedFileName());
						}
							
						List<String> lines;
						try {
							lines = Files.readAllLines(file.toPath());
							String line = lines.get(failureLine-1);
							lines.set(failureLine-1, line+" //mutGenLimit 1");
							
							file = new File(outputPath+failure.getTestedFileName());
							Files.write(file.toPath(), lines);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

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

	
	
	/*
	 * Return class names in bin path
	 */
	private static List<String> scanBinPath(String binPath) {
		List<String> classesInOriginalSrcDir = null;
		DependencyScanner depScanner;
		try {
			System.out.println(new File(binPath).toPath());
			depScanner = new DependencyScanner(new File(binPath).toPath());
			classesInOriginalSrcDir = new LinkedList<String>();
			classesInOriginalSrcDir.addAll(depScanner.getDependencyMap().getClasses());
			
			System.out.println("classes in bin path : "+classesInOriginalSrcDir.size());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classesInOriginalSrcDir;
	}

}
