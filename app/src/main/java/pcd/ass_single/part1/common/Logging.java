package pcd.ass_single.part1.common;

import com.google.common.base.Strings;

public class Logging {
    private Logging() {
    }

    public static void log(String msg) {
        log("", msg);
    }

    public static void debugLog(String msg) {
        debugLog("", msg);
    }

    public static void log(String caller, String msg) {
        String callerName = Strings.isNullOrEmpty(caller) ? "" : "[" + caller + "] ";
        synchronized (System.out) {
            System.out.println(callerName + msg);
        }
    }

    public static void debugLog(String caller, String msg) {
        if (Config.isDebugEnabled()) {
            log(caller, msg);
        }
    }
}
