package pcd.ass_single.part1.vt.controller;

import pcd.ass_single.part1.common.Directory;

import java.io.File;
import java.util.List;

public record DirectoryContent(List<Directory> directories, List<File> pdfs) {
}
