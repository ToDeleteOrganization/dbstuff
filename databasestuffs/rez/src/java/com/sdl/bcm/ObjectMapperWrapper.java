package com.sdl.bcm;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.bcm.model.MarkupData;

import java.io.IOException;

/**
 *  Created by dtarba on 6/24/2015.
 */
public class ObjectMapperWrapper  extends ObjectMapper {

    @Override
    public <T> T readValue(String json, Class<T> clazz) throws IOException, JsonParseException, JsonMappingException {
        T object = super.readValue(json, clazz);
        if(object instanceof MarkupData) {
            MarkupData parent = (MarkupData) object;
            setParents(parent);
        }
        return object;
    }

    private void setParents(MarkupData parent) {
        if(parent.getChildren() != null && !parent.getChildren().isEmpty()) {
            for(MarkupData child : parent.getChildren()) {
                child.setParent(parent);
                setParents(child);
            }
        }
    }
}
