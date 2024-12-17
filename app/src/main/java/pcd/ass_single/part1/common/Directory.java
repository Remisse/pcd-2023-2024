package pcd.ass_single.part1.common;

import java.io.File;
import java.net.URI;
import java.util.*;

public class Directory {
    private final File asFile;

    public Directory(URI uri) {
        asFile = new File(uri);
        if (!asFile.isDirectory()) {
            throw new IllegalArgumentException("Not a directory");
        }
    }

    public List<Directory> nestedDirectories() {
        String[] nestedDirs = asFile.list((f, unused) -> f.isDirectory());
        Objects.requireNonNull(nestedDirs);
        return Arrays.stream(nestedDirs)
                .map(URI::create)
                .map(Directory::new)
                .toList();
    }

    public List<Directory> nestedDirectoriesRecursive() {
    }

    public List<File> filesOfType(final String type) {
        String[] pdfs = asFile.list((f, n) -> f.isFile() && n.endsWith("." + type));
        Objects.requireNonNull(pdfs);
        return Arrays.stream(pdfs)
                .map(File::new)
                .toList();
    }
}
