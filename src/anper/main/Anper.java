package anper.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import main.api.DependencyScanner;
import mujava.OpenJavaException;
import openjava.ptree.ParseTreeException;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import anper.junit.Tester;
import anper.mutation.NPERClassToMutate;
import anper.mutation.NPERMutator;
import anper.mutation.NullPointerFailure;
import anper.mutation.NullPointerFailures;
import anper.utils.MutGenLimitMarker;
import examples.foo.ExampleTestSuite;

public class Anper {

	public static void main(String[] args) throws ClassNotFoundException, OpenJavaException, ParseTreeException, IOException {
		String relativePath = "/Users/gaston/Documents/EclipseWorkspace/AutomaticSoftwareRepairCourse/";
		String srcPath = relativePath+"src/";
		String binPath = relativePath+"bin/";
		String testSuitesPath = relativePath+"test/";
		String outputPath = relativePath+"outputMutants/";
		//Class<?> testToRun = ExampleTestSuite.class;
		Set<String> tests = new TreeSet<>();
		tests.add("examples.foo.ExampleTestSuite");
		
		Set<String> classesInOriginalSrcDir = scanBinPath(binPath);
		Set<String> packagesToReload = new TreeSet<>();
		packagesToReload.add("examples.foo");
		
		Tester tester = new Tester("bin/", "bin/", packagesToReload, tests);
		NullPointerFailures failures = tester.runTests();
		int originalFailCount = failures.getFailureCount();
		Map<String, NPERClassToMutate> classesToMutate = new TreeMap<>();
		if (failures.hasFailures()) {
			for (NullPointerFailure npef : failures.getNullPointerExceptionFailures()) {
				int line = npef.getFailureLine();
				String failingClass = npef.getTestedClassName();
				String method = npef.getTestedMethodName();
				NPERClassToMutate classToMutate = null;
				if (classesToMutate.containsKey(failingClass)) {
					classToMutate = classesToMutate.get(failingClass);
				} else {
					classToMutate = new NPERClassToMutate(failingClass);
					classesToMutate.put(failingClass, classToMutate);
				}
				classToMutate.addLine(line);
				classToMutate.addMethod(method);
			}
			for (NPERClassToMutate ctm : classesToMutate.values()) {
				MutGenLimitMarker mglMarker = new MutGenLimitMarker(null, ctm.getLines()); //TOOD: replace null with the path to file
				mglMarker.writeLines();
				NPERMutator.mutate(srcPath, outputPath, ctm.getClassName(), ctm.getMethods());
			}
			failures = tester.runTests();
			if (!failures.hasFailures()) {
				System.out.println("YAY!");
			} else if (!failures.hasNullPointerExceptionFailures()) {
				System.out.println("YAY!?");
			} else if (failures.getFailureCount() < originalFailCount) {
				System.out.println("MMKAY!");
			} else {
				System.out.println("NEY!");
			}
		}
		
//		try {
//			//MuJavaJunitTestRunner testRunner = new MuJavaJunitTestRunner(testToRun, false);
//			//Result result = testRunner.run();
//			Result result = JUnitCore.runClasses(testToRun);
//
//			
//			if(!result.wasSuccessful()){
//				System.out.println("Testing failures : "+result.getFailureCount());
//				NullPointerFailures npetr = new NullPointerFailures();
//				npetr.add(result);
//				if (npetr.hasFailures()){
//					List<NullPointerFailure> failures = npetr.getNullPointerExceptionFailures();
//
//					for (NullPointerFailure failure : failures){
//						String className =  failure.getTestedClassName();
//						String methodName = failure.getTestedMethodName();
//						int failureLine = failure.getFailureLine(); 
//						System.out.println(failure.toString());
//						//System.out.println(failure.getOutputTrace());
//
//						if (classesInOriginalSrcDir.contains(className)){
//							// try to repair
//							System.out.println("contains!! "+className);
//						}
//						
//						File file = null;
//						Path path = new File(outputPath+failure.getTestedFileName()).toPath();
//						if (!Files.exists(path, java.nio.file.LinkOption.NOFOLLOW_LINKS) ){
//							file = new File(srcPath+failure.getTestedFileName());
//						}else{
//							file = new File(outputPath+failure.getTestedFileName());
//						}
//							
//						List<String> lines;
//						try {
//							lines = Files.readAllLines(file.toPath());
//							String line = lines.get(failureLine-1);
//							lines.set(failureLine-1, line+" //mutGenLimit 1");
//							
//							file = new File(outputPath+failure.getTestedFileName());
//							Files.write(file.toPath(), lines);
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						
//
//						/*
//						addMutGenLimit(program, className, method, line)
//						mutate(program, className, method)
//						*/						
//					}
//					/*
//					results = test(program,tests)
//					if (results.successful) print(YAY!)
//					else if (!results.exceptions.contains(NullPointerException)) print(YAY!?)
//					else print(NEY!)
//					*/
//				}
//			}
//
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		}

	}
	
	
	/*
	 * Return class names in bin path
	 */
	private static Set<String> scanBinPath(String binPath) {
		Set<String> classesInOriginalSrcDir = null;
		DependencyScanner depScanner;
		try {
			System.out.println(new File(binPath).toPath());
			depScanner = new DependencyScanner(new File(binPath).toPath());
			classesInOriginalSrcDir = new TreeSet<String>();
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
