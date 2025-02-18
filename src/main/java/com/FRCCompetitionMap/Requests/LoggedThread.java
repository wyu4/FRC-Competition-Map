package com.FRCCompetitionMap.Requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggedThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedThread.class);

    public LoggedThread(Class<?> owner, Runnable task) {
        super(task);
        LOGGER.info("CREATE THREAD.");
    }
}
