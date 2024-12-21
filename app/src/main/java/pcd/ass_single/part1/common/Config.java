package pcd.ass_single.part1.common;

public class Config {
    private static boolean debug = false;

    private Config() {
    }

    public static void disablePdfBoxWarnings()  {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    public static void setDebugLoggingEnabled(boolean value) {
        debug = value;
    }

    public static boolean isDebugEnabled() {
        return debug;
    }
}
