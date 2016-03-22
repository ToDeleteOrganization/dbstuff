package com.sdl.bcm.service.monitoring;

import org.apache.commons.lang.StringUtils;

import com.sdl.bcm.rest.http.WebContext;
import com.sdl.bcm.rest.util.TraceableLoggerUtil;

/**
 * Base class for traceable log messages.
 */
public abstract class TraceableLogger {

	protected StringBuilder appendTraceIdIfPresent(StringBuilder logMessageBuilder) {
		String traceId = findTraceId();

		if (StringUtils.isNotEmpty(traceId) &&(logMessageBuilder != null)) {
			logMessageBuilder.append(getTraceIdFormat(traceId));
		}
		return logMessageBuilder;
	}

	protected String appendTraceIdIfPresent(String logMessage) {
		String traceId = findTraceId();

		if (StringUtils.isNotEmpty(traceId)) {
			String traceIdFormat = getTraceIdFormat(traceId);
			logMessage = (logMessage != null) ? logMessage.concat(traceIdFormat) : traceIdFormat;
		}
		return logMessage;
	}

	private String findTraceId() {
		WebContext ctx = WebContext.getInstance();
		return (ctx != null) ? ctx.getRequest().getHeader(TraceableLoggerUtil.TRACE_ID_HEADER_NAME) : null;
	}

	private String getTraceIdFormat(String traceId) {
		return TraceableLoggerUtil.getTraceIdLogFormat(traceId);
	}
}
