package com.sdl.bcm.service.monitoring;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class StatisticsLogger extends TraceableLogger {

    private static final Logger LOG = Logger.getLogger(StatisticsLogger.class);

    public void logStatistic(String methodName, String documentId, StopWatch stopWatch) {
    	StringBuilder sb = new StringBuilder();
        sb.append(methodName).append(" ");
        sb.append(stopWatch.getLastTaskName());
        sb.append(" with ID " + documentId).append(" ");
        sb.append("took " + stopWatch.getTotalTimeSeconds() + " seconds.");

        sb = appendTraceIdIfPresent(sb);

        LOG.info(sb.toString());
    }

    public void logStatistic(String methodName, StopWatch stopWatch) {
        logStatistic(methodName, null, stopWatch);
    }

}
