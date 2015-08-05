package anper.mutation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import openjava.ptree.CompilationUnit;
import openjava.ptree.ParseTreeException;
import mujava.OpenJavaException;
import mujava.api.Mutant;
import mujava.api.MutantsInformationHolder;
import mujava.api.Mutation;
import mujava.app.MutationRequest;
import mujava.app.Mutator;
import mujava.op.util.MutantCodeWriter;
import mujava.op.util.OLMO;
import mujava.util.ConfigReader;
import anper.main.Anper;
import anper.utils.Compiler;



/**
 * This class takes a java source file marked with {@code //mutGenLimit 1} annotations
 * and applies all mutations generated via {@code NPER} operator
 * 
 * @author Simón Emmanuel Gutiérrez Brida
 * @author Gastón Scilingo
 * @see mujava.op.NPER
 */
public class NPERMutator {

	
	public static void mutate(String rootDir, String outDir, String binDir, String className, String[] methods) throws Exception {
		MutationRequest mutReq = new MutationRequest(className, methods, new Mutant[]{Mutant.NPER}, rootDir, outDir, false, false);
		Mutator mutator = new Mutator();
		mutator.setRequest(mutReq);
		try {
			Map<String, MutantsInformationHolder> mutations = mutator.obtainMutants();
			OLMO olmo = new OLMO();
			if (mutations.isEmpty()) return;
			CompilationUnit ast = mutations.values().iterator().next().getCompUnit();
			for (Entry<String, MutantsInformationHolder> mutationsPerMethod : mutations.entrySet()) {
				List<Mutation> muts = mutationsPerMethod.getValue().getMutantsIdentifiers();
				String method = mutationsPerMethod.getKey();
				for (Mutation m : muts) {
					olmo.modifyAST(ast, m, method);
				}
			}
			File mutatedFile = new File(getPath(outDir, className)+".java");
			mutatedFile.getParentFile().mkdirs();
			mutatedFile.createNewFile();
			OutputStream os = new FileOutputStream(mutatedFile, true);
			PrintWriter pw = new PrintWriter(os);
			MutantCodeWriter writer = new MutantCodeWriter(null, pw, null);
			ast.accept(writer);
			Compiler compiler = new Compiler(outDir, binDir);
			if (!compiler.compile(getPath("", className))) {
				if (compiler.getError() != null) {
					throw compiler.getError();
				} else {
					return;
				}
			}
			moveMutant(getPath(outDir, className)+".class", getPath(binDir, className)+".class");
		} catch (ClassNotFoundException | OpenJavaException | ParseTreeException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
	
	private static String getPath(String dir, String className) {
		return Anper.addTrailingSeparator(dir) + className.replaceAll("\\.", ConfigReader.getInstance().getFileSeparator());
	}
	
	private static void moveMutant(String mutantPath, String originalPath) throws IOException {
		String fixedMutantPath = mutantPath;
		String fixedOriginalPath = originalPath;
		File mutant = new File(fixedMutantPath);
		if (!mutant.exists()) {
			throw new FileNotFoundException("File : " + fixedMutantPath + " doesn't exist!\n");
		}
		File original = new File(fixedOriginalPath);
		if (original.getParentFile()==null?!original.exists():!original.getParentFile().exists()) {
			throw new FileNotFoundException("File : " + fixedOriginalPath + " doesn't exist!\n");
		}
		try {
			Path dest = original.getParentFile()==null?original.toPath():original.getParentFile().toPath();
			Files.copy(mutant.toPath(), dest.resolve(mutant.toPath().getFileName()), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw e;
		}
	}
	
}
