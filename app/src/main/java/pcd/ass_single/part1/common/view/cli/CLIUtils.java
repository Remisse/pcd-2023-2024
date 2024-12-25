package pcd.ass_single.part1.common.view.cli;

import pcd.ass_single.part1.common.Logger;

public class CLIUtils {
    private CLIUtils() {
    }

    public static void usage() {
        Logger.get().log("Usage: java <jar-file> <path-to-directory> <search-term>");
        System.exit(-1);
    }
}
