package pcd.ass_single.part1.common;

import java.util.regex.Pattern;

public class Ops {
    private Ops() {}

    public static Pattern makeWordRegex(final String word) {
        return Pattern.compile(".*\\b" + word + "\\b.*");
    }

    public static boolean findWord(final String text, final Pattern regex) {
        return regex.matcher(text).matches();
    }
}
