package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class takes a java source file path and a set of line numbers and adds " //mutGenLimit 1"
 * at the end of each line in the set
 * 
 * 
 * @author Simón Emmanuel Gutiérrez Brida
 * @author Gastón Scilingo
 *
 */
public class MutGenLimitMarker {
	private Set<Integer> lines;
	private String path;
	
	public MutGenLimitMarker(String path, Set<Integer> lines) {
		this.lines = lines;
		this.path = path;
	}
	
	public MutGenLimitMarker(String path) {
		this(path, new TreeSet<Integer>());
	}
	
	public void addLine(Integer line) {
		this.lines.add(line);
	}
	
	public void writeLines() throws IOException {
		File origFile = new File(this.path);
		File destFile = new File(this.path + ".modified");
		destFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(destFile);
        Scanner scan = new Scanner(origFile);
        scan.useDelimiter("\n");
        String str = null;
        int currentLine = -1;
        while(scan.hasNext()){
        	currentLine++;
            str = scan.next();
            if (this.lines.contains(currentLine)) {
            	fos.write((trimTrailing(str) + " //mutGenLimit 1" + "\n").getBytes(Charset.forName("UTF-8")));
            } else {
            	fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
            }
        }
        if (fos != null) fos.close();
        scan.close();
        delete(this.path);
        rename(this.path+".backup", this.path);
	}
	
	private void rename(String path1, String path2) {
		File toRename = new File(path1);
		toRename.renameTo(new File(path2));
	}
	
	private void delete(String path) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}
	}
	
	private String trimTrailing(String orig) {
		String trimmed = orig.trim();
		int trimmedStart = orig.indexOf(trimmed);
		trimmed = orig.subSequence(0, trimmedStart) + trimmed;
		return trimmed;
	}
	
	
}
