package pcd.ass_single.part1.common;

import com.google.common.base.Strings;
import pcd.ass_single.part1.common.lock.CloseableReentrantLock;

public class Logger {
    private static volatile Logger instance;
    private static final CloseableReentrantLock INSTANCE_LOCK = new CloseableReentrantLock();
    private static final CloseableReentrantLock OUTPUT_LOCK = new CloseableReentrantLock();

    private Logger() {
    }

    public static Logger get() {
        try (var ignored = INSTANCE_LOCK.lockAsResource()) {
            if (instance == null) {
                instance = new Logger();
            }
        }
        return instance;
    }

    public void log(String caller, String msg) {
        String callerName = Strings.isNullOrEmpty(caller) ? "" : "[" + caller + "] ";
        try (var ignored = OUTPUT_LOCK.lockAsResource()) {
            System.out.println(callerName + msg);
        }
    }

    public void debugLog(String caller, String msg) {
        if (Config.isDebugEnabled()) {
            log(caller, msg);
        }
    }

    public void log(String msg) {
        log("", msg);
    }

    public void debugLog(String msg) {
        debugLog("", msg);
    }
}
