package pcd.ass_single.part1.common;

import java.io.File;
import java.util.List;

public record DirectoryContent(List<Directory> directories, List<File> pdfs) {
}
