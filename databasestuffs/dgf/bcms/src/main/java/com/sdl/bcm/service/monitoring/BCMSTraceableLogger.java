package com.sdl.bcm.service.monitoring;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This is used to simply log a message (with the traceId from the request), for multiple log levels.
 * For exceptions, the stack trace will also be printed.
 *
 */
@Component
public class BCMSTraceableLogger extends TraceableLogger {

	private static final Logger LOG = Logger.getLogger(BCMSTraceableLogger.class);

	public void logExceptionMessage(String message, Exception ex) {
		LOG.error(appendTraceIdIfPresent(message));
	}

	public void logException(String message, Exception ex) {
		LOG.error(appendTraceIdIfPresent(message), ex);
	}

	public void logWarn(String warnMessage) {
		LOG.warn(appendTraceIdIfPresent(warnMessage));
	}

	public void logInfo(String infoMessage) {
		if (LOG.isInfoEnabled()) {
			LOG.info(appendTraceIdIfPresent(infoMessage));
		}
	}

	public void logTrace(String warnMessage) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(appendTraceIdIfPresent(warnMessage));
		}
	}

	public void logDebug(String debugMessage) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(appendTraceIdIfPresent(debugMessage));
		}
	}

}
