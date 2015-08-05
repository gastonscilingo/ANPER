package anper.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Compiler {
	
	private Exception lastError = null;
	private String mutDir = null;
	private String originalBinFolder = null;
	
	public Compiler(String mutDir, String originalBinFolder) {
		this.mutDir = mutDir;
		this.originalBinFolder = originalBinFolder;
	}

	public boolean compile(String classNameAsPath) {
		this.lastError = null;
		File fileToCompile = new File(mutDir+classNameAsPath+".java");
		if (!fileToCompile.exists() || !fileToCompile.isFile() || !fileToCompile.getName().endsWith(".java")) {
			this.lastError = new FileNotFoundException(fileToCompile.toString() + " doesn't exist");
			return false;
		}
		File[] files = new File[]{fileToCompile};
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
		boolean compileResult = compiler.getTask(null, fileManager, null, Arrays.asList(new String[] {"-classpath", originalBinFolder}), null, compilationUnit).call();
		return compileResult;
	}
	
	public Exception getError() {
		return this.lastError;
	}
	
}
