package anper.main;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import main.api.DependencyScanner;
import mujava.OpenJavaException;
import openjava.ptree.ParseTreeException;
import anper.config.ConfigReader;
import static anper.config.ConfigReader.Config_key.*;
import anper.junit.NullPointerFailure;
import anper.junit.NullPointerFailures;
import anper.junit.Tester;
import anper.mutation.NPERClassToMutate;
import anper.mutation.NPERMutator;
import anper.utils.MutGenLimitMarker;

public class Anper {

	public static void main(String[] args) throws ClassNotFoundException, OpenJavaException, ParseTreeException, IOException {
		//CONFIG+++
		ConfigReader config = null;
		if (args.length == 0) {
			config = ConfigReader.getInstance();
		} else {
			config = ConfigReader.getInstance(args[0]);
		}
		String srcPath = config.getStringArgument(ORIGINAL_SOURCE_DIR);
		String binPath = config.getStringArgument(ORIGINAL_BIN_DIR);
		String testsBinPath = config.getStringArgument(TESTS_BIN_DIR);
		String outputPath = config.getStringArgument(MUTANTS_DIR);
		Set<String> tests = new TreeSet<>();
		for (String test : config.stringArgumentsAsArray(config.getStringArgument(TESTS))) {
			tests.add(test);
		}
		Set<String> packagesToReload = new TreeSet<>();
		for (String pkg : config.stringArgumentsAsArray(config.getStringArgument(ALLOWED_PACKAGES_TO_RELOAD))) {
			packagesToReload.add(pkg);
		}
		//CONFIG---
		
		
		Set<String> fixableClasses = scanForFixableClasses(binPath, srcPath);
		
		//TESTING+++
		Tester tester = new Tester(testsBinPath, binPath, packagesToReload, tests);
		NullPointerFailures failures = tester.runTests(fixableClasses);
		int originalFailCount = failures.getFailureCount();
		//TESTING---
		
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
				MutGenLimitMarker mglMarker = new MutGenLimitMarker(classJavaFilePath(ctm.getClassName(), srcPath), ctm.getLines());
				mglMarker.writeLines();
				NPERMutator.mutate(srcPath, outputPath, ctm.getClassName(), ctm.getMethods());
			}
			failures = tester.runTests(fixableClasses);
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

	}
	
	
	/*
	 * Return class names in bin path
	 */
	private static Set<String> scanForFixableClasses(String binPath, String srcPath) {
		Set<String> fixableClasses = null;
		DependencyScanner depScanner;
		try {
			System.out.println(new File(binPath).toPath());
			depScanner = new DependencyScanner(new File(binPath).toPath());
			fixableClasses = new TreeSet<String>();
			for (String c : depScanner.getDependencyMap().getClasses()) {
				if (classSourceFileExist(c, srcPath)) {
					fixableClasses.add(c);
				}
			}
			System.out.println("fixable classes : "+fixableClasses.size());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fixableClasses;
	}
	
	private static String classNameAsPath(String className) {
		return className.replaceAll("\\.", ConfigReader.getInstance().getFileSeparator());
	}
	
	private static String classJavaFilePath(String className, String srcPath) {
		String fullPathToFile = addTrailingSeparator(srcPath) + classNameAsPath(className) + ".java";
		return fullPathToFile;
	}
	
	private static boolean classSourceFileExist(String className, String srcPath) {
		File javaFile = new File(classJavaFilePath(className, srcPath));
		return javaFile.exists();
	}
	
	private static String addTrailingSeparator(String original) {
		if (original.endsWith(ConfigReader.getInstance().getFileSeparator())) {
			return original + ConfigReader.getInstance().getFileSeparator();
		} else {
			return original;
		}
	}

}
