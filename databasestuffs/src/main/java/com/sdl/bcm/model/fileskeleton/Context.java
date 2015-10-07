package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Tree structure that represents the inherent structure of the original document.
 * This can be used to show this context to the user while editing the document and
 * is also used to disambiguate translation memory matches
 * (i.e. the same text may have different translations depending on the context).
 */
@JsonIgnoreProperties(value = { "metadata" })
public class Context extends MetaData{

    /**
     * Unique identifier for the context.
     */
    private int id;

    /**
     * The id of the context definition that defines the properties of this context.
     */
    private int contextDefinitionId;

    /**
     * The parent Context Id
     */
    private int parentContextId;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getContextDefinitionId() {
        return contextDefinitionId;
    }
    public void setContextDefinitionId(int contextDefinitionId) {
        this.contextDefinitionId = contextDefinitionId;
    }

    public int getParentContextId() {
        return parentContextId;
    }
    public void setParentContextId(int parentContextId) {
        this.parentContextId = parentContextId;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        Context that = (Context) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(contextDefinitionId, that.contextDefinitionId)
                .append(parentContextId, that.parentContextId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        result = 31 * result + contextDefinitionId;
        result = 31 * result + parentContextId;
        return result;
    }
}
