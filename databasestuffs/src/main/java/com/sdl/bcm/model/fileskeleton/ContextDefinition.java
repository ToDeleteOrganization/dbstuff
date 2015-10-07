package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Definition of a unique set of context properties, referred to from within the context hierarchy.
 */
@JsonPropertyOrder({"id", "isTmContext", "isStructureContext", "typeId", "displayName", "displayCode", "displayColor", "description", "formattingGroupId"})

public class ContextDefinition extends MetaData {

    /**
     * Unique identifier for any definition.
     */
    protected Integer id;

    /**
     * Whether this context should be considered relevant to the translation,
     * i.e. whether the content inside paragraph unit in this context
     * would be translated differently depending on this context
     * (e.g. a Heading versus footnote).
     */
    private boolean isTmContext;

    /**
     * Whether this is a context that should be used to represent the structure
     * (outline) of the document.
     */
    private boolean isStructureContext;

    /**
     * An identifier for this type of context. This allows other systems to
     * compare and match context from different documents. For instance these
     * ids are stored alongside translation units in a translation memory
     * and are used to disambiguate translations by matching the translation
     * unit context with the current document context.
     */
    private String typeId;

    /**
     * A short string to be used as the default display text for the context
     */
    private String displayName;

    /**
     * A very short string (usually one or two letters) to identify the context
     * (shown in Studio in the right hand side column).
     */
    private String displayCode;

    /**
     * The display color associated with the context (background color, etc.).
     * This allows the consistent presentation of contexts.
     */
    private String displayColor;

    /**
     * A description of the project
     */
    private String description;

    /**
     * The optional id of a formatting group, defining the formatting that
     * should be applied to all content within this context.
     */
    private int formattingGroupId;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("isTmContext")
    public boolean isTmContext() {
        return isTmContext;
    }

    @JsonProperty("isTmContext")
    public void setTmContext(boolean isTmContext) {
        this.isTmContext = isTmContext;
    }

    @JsonProperty("isStructureContext")
    public boolean isStructureContext() {
        return isStructureContext;
    }

    @JsonProperty("isStructureContext")
    public void setStructureContext(boolean isStructureContext) {
        this.isStructureContext = isStructureContext;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public String getDisplayColor() {
        return displayColor;
    }

    public void setDisplayColor(String displayColor) {
        this.displayColor = displayColor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFormattingGroupId() {
        return formattingGroupId;
    }

    public void setFormattingGroupId(int formattingGroupId) {
        this.formattingGroupId = formattingGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        ContextDefinition that = (ContextDefinition) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(isTmContext, that.isTmContext)
                .append(isStructureContext, that.isStructureContext)
                .append(typeId, that.typeId)
                .append(displayName, that.displayName)
                .append(displayCode, that.displayCode)
                .append(displayColor, that.displayColor)
                .append(description, that.description)
                .append(formattingGroupId, that.formattingGroupId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (isTmContext ? 1 : 0);
        result = 31 * result + (isStructureContext ? 1 : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (displayCode != null ? displayCode.hashCode() : 0);
        result = 31 * result + (displayColor != null ? displayColor.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + formattingGroupId;
        return result;
    }
}
