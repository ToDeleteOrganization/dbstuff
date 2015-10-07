package com.sdl.bcm;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Comments from Studio have timestamps in the following format: 2014-06-16T12:30:10.8345899+03:00
 * In java, standard date format does not include nanoseconds, so Custom deserializer will ignore nanoseconds and
 * resulted timestamp will be: 2014-06-16T12:30:10+03:00
 * However, Comments from FPS will have time stamp like: 2014-06-16T12:30:10+03:00 which are ok
 *
 */
public class CustomJsonDateDeserializer extends JsonDeserializer<Date> {
    private static final Logger LOG = Logger.getLogger(CustomJsonDateDeserializer.class);
    public static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ssXXX";

    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_DATEFORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_SIMPLE);
        }
    };

    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_DATEFORMAT_WITH_TZ = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_WITH_TIMEZONE);
        }
    };

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String dateString = jp.getText();
        return parseDateString(dateString);
    }

    public Date parseDateString(String dateString) {
        if(dateString == null) {
            return null;
        }
        Date result;
        try {
            if(dateString.length() == 19) {
                result = THREAD_LOCAL_DATEFORMAT.get().parse(dateString);
            } else {
                result = parseDateStringLongForm(dateString);
            }
        } catch (ParseException e) {
            LOG.error(e);
            return null;
        }
        return result;
    }

    private Date parseDateStringLongForm(String date) throws ParseException {
        String dateString = date;
        Date result = null;
        char c = dateString.charAt(19);
        if(c == '.') {
            String timezone = null;
            int index = 19;
            do {
                c = dateString.charAt(index);
                if(c == '+' || c == '-') {
                    timezone = dateString.substring(index, dateString.length());
                    break;
                }
                index++;
            } while(index < dateString.length());
            if (timezone != null) {
                //we skip nanoseconds
                dateString = dateString.substring(0, 19) + timezone;
                result = THREAD_LOCAL_DATEFORMAT_WITH_TZ.get().parse(dateString);
            } else {
                dateString = dateString.substring(0, 19);
                result = THREAD_LOCAL_DATEFORMAT.get().parse(dateString);
            }
        } else if(c == '+' || c == '-') {
            result = THREAD_LOCAL_DATEFORMAT_WITH_TZ.get().parse(dateString);
        }
        return result;
    }
}
