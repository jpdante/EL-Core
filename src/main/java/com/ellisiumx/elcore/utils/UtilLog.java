package com.ellisiumx.elcore.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilLog {

    private static Logger logger = UtilServer.getServer().getLogger();

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    public static void log(Level level, String message, Object data) {
        logger.log(level, message, data);
    }

}
