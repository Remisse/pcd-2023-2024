package pcd.ass_single.part1.common;

import java.io.File;
import java.util.*;

public class Directory {
    private final File asFile;

    public Directory(String path) {
        asFile = new File(path);
        if (!asFile.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
    }

    public List<Directory> nestedDirectories() {
        File[] nestedDirs = asFile.listFiles();
        if (nestedDirs == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(nestedDirs)
                .filter(File::isDirectory)
                .map(f -> new Directory(f.getAbsolutePath()))
                .toList();
    }

    public List<File> filesOfType(final String type) {
        File[] pdfs = asFile.listFiles();
        if (pdfs == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(pdfs)
                .filter(f -> f.isFile() && f.getName().endsWith("." + type))
                .toList();
    }
}
