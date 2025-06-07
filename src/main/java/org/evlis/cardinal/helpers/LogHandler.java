package org.evlis.cardinal.helpers;

import java.util.logging.ConsoleHandler;

public class LogHandler extends ConsoleHandler {
    public LogHandler() {
        setFormatter(new LogFormatter());
    }
}
