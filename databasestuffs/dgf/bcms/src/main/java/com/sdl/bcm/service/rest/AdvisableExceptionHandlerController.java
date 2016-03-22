package com.sdl.bcm.service.rest;

import java.text.ParseException;

import javax.servlet.http.HttpServletResponse;

import com.sdl.bcm.service.exception.DocumentNotFoundException;
import com.sdl.bcm.service.exception.EndPointNotFoundException;
import com.sdl.bcm.service.monitoring.BCMSTraceableLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * General advisable controller to handle exceptions.
 *
 * Created by mariuscocoi on 2/1/2016.
 */
@ControllerAdvice
class AdvisableExceptionHandlerController {

    @Autowired
    protected BCMSTraceableLogger bcmsTraceableLogger;

    /**
     * General spring exception handler method.
     *
     * @param ex
     *  The throne exception exception.
     *
     * @param response
     *  The HttpServletResponse response.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Sorry for this, you can help us reporting the use case generating the error")
    public void handleException(Exception ex, HttpServletResponse response) {
        bcmsTraceableLogger.logException(ex.getMessage(), ex);
    }

    /**
     * Handles custom 'DocumentNotFoundException' exceptions.
     *
     * @param dnfe
     *  The throne BCMResourceNotFoundException exception.
     *
     * @param response
     *  The HttpServletResponse response.
     */
    @ExceptionHandler(DocumentNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Document Not Found.")
    public void handleBCMResourceNotFoundException(DocumentNotFoundException dnfe, HttpServletResponse response) {
        bcmsTraceableLogger.logInfo(dnfe.getCustomReason());
    }

    /**
     * Handles custom 'EndPointNotFoundException' exceptions.
     *
     * @param epnfe
     *  The throne BCMResourceNotFoundException exception.
     *
     * @param response
     *  The HttpServletResponse response.
     */
    @ExceptionHandler(EndPointNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not Found.")
    public void handleEndPointNotFoundException(EndPointNotFoundException epnfe, HttpServletResponse response) {
        bcmsTraceableLogger.logInfo(epnfe.getCustomReason());
    }

    /**
     * Handles 'MissingServletRequestParameterException' exceptions.
     *
     * @param ex
     *  The throne MissingServletRequestParameterException exception.
     *
     * @param response
     *  The HttpServletResponse response.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "MissingServletRequestParameterException, needed: 'date' and 'tz' parameters")
    public void handleMissingServletRequestParameterException(MissingServletRequestParameterException ex,
                                                              HttpServletResponse response) {
    	bcmsTraceableLogger.logException("MissingServletRequestParameterException: " + ex.getMessage(), ex);
    }

    /**
     * Handles 'ParseException' exceptions.
     *
     * @param ex
     *  The throne ParseException exception.
     *
     * @param response
     *  The HttpServletResponse response.
     */
    @ExceptionHandler(ParseException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Date should be in the following format: yyyy.MM.dd HH:mm:ss:SS")
    public void handleParseException(ParseException ex, HttpServletResponse response) {
    	bcmsTraceableLogger.logException("ParseException: " + ex.getMessage(), ex);
    }

    /**
     * Handles 'IllegalArgumentException' exceptions.
     *
     * @param ex
     *  The throne IllegalArgumentException exception.
     *
     * @param response
     *  The HttpServletResponse response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Unrecognized timezone. For the list of recognized timezones try a GET at: .../timezone-web/tz/ids")
    public void handleIllegalArgumentException(IllegalArgumentException ex, HttpServletResponse response) {
    	bcmsTraceableLogger.logException("IllegalArgumentException: " + ex.getMessage(), ex);
    }

}
