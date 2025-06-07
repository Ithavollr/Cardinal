package org.evlis.cardinal.helpers;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String pluginString = "\033[38;5;203m[Cardinal] ";
        String levelColor;
        switch (record.getLevel().getName()) {
            case "INFO":
                levelColor = "\u001B[37m"; // Gray
                break;
            case "WARNING":
                levelColor = "\033[38;5;208m"; // Orange
                break;
            case "SEVERE":
                levelColor = "\033[38;5;196m"; // Red
                break;
            default:
                levelColor = "\u001B[37m"; // Gray
        }
        return String.format(
                "%s%s%s\033[0m\n", // Colorize message and reset color
                pluginString,
                levelColor,
                record.getMessage()
        );
    }
}
