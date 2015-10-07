package com.sdl.bcm;

import com.fasterxml.jackson.databind.*;
import com.sdl.bcm.model.Document;

import java.io.IOException;
import java.nio.charset.Charset;

public class BCMSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private BCMSerializer() {
    }

    public static String serializeBCM(Document doc) throws IOException {
        ObjectWriter writer = objectMapper.writerFor(Document.class);
        return writer.writeValueAsString(doc);
    }

    public static String serializeObject(Object obj) throws IOException {
        ObjectWriter writer = objectMapper.writer();
        return writer.writeValueAsString(obj);
    }

    public static Document deserializeBCM(String docJSON) throws IOException {
        ObjectReader reader = objectMapper.reader(Document.class);
        return reader.readValue(docJSON);
    }

    public static Document deserializeBCM(byte[] docJSONBytes) throws IOException {
        String docJSON = new String(docJSONBytes, UTF8);
        ObjectReader reader = objectMapper.reader(Document.class);
        return reader.readValue(docJSON);
    }
}
