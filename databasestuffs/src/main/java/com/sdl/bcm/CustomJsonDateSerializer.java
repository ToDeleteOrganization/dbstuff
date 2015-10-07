package com.sdl.bcm;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom serialization class used for serialize date from Comments.
 * Date format used for serialization is "yyyy-MM-dd'T'HH:mm:ssXXX"
 */
public class CustomJsonDateSerializer extends JsonSerializer<Date> {

    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL_DATEFORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        }
    };

    @Override
    public void serialize(Date aDate, JsonGenerator aJsonGenerator, SerializerProvider aSerializerProvider) throws IOException {
        String dateString = THREAD_LOCAL_DATEFORMAT.get().format(aDate);
        aJsonGenerator.writeString(dateString);
    }
}
