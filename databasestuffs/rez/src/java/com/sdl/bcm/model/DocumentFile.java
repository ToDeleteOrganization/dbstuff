package com.sdl.bcm.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.data.annotation.Id;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
@org.springframework.data.mongodb.core.mapping.Document(collection = "fullDocuments")
public class DocumentFile {

    @Id
    private String id;
    private String docJSON;
    private Map<String, Object> properties;

    public DocumentFile(String id, String docJSON) {
        this.id = id;
        this.docJSON = docJSON;
        properties = new HashMap<>();
    }

    public DocumentFile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocJSON() {
        return docJSON;
    }

    public void setDocJSON(String docJSON) {
        this.docJSON = docJSON;
    }

    public Map<String, Object> getProperties() {
        if(properties == null) {
            properties = new HashMap<>();
        }
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(o == null || o.getClass() != getClass())
            return false;
        DocumentFile that = (DocumentFile) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(docJSON, that.docJSON)
                .append(properties, that.properties)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (docJSON != null ? docJSON.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
