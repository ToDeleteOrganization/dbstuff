package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.*;
import com.sdl.bcm.visitor.BCMCompositeElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.data.annotation.Transient;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Paragraph.class, name = Paragraph.TYPE),
        @JsonSubTypes.Type(value = TextMarkup.class, name = TextMarkup.TYPE),
        @JsonSubTypes.Type(value = Segment.class, name = Segment.TYPE),
        @JsonSubTypes.Type(value = TagPair.class, name = TagPair.TYPE),
        @JsonSubTypes.Type(value = PlaceholderTag.class, name = PlaceholderTag.TYPE),
        @JsonSubTypes.Type(value = LockedContentContainer.class, name = LockedContentContainer.TYPE),
        @JsonSubTypes.Type(value = CommentContainer.class, name = CommentContainer.TYPE),
        @JsonSubTypes.Type(value = RevisionContainer.class, name = RevisionContainer.TYPE),
        @JsonSubTypes.Type(value = StructureTag.class, name = StructureTag.TYPE),
        @JsonSubTypes.Type(value = TerminologyAnnotationContainer.class, name = TerminologyAnnotationContainer.TYPE),
        @JsonSubTypes.Type(value = FeedbackContainer.class, name = FeedbackContainer.TYPE)
})
public abstract class MarkupData extends MetaData implements BCMCompositeElement<MarkupData, MarkupData> {
    protected String id;

    @JsonIgnore
    protected String type;

    @Transient
    protected MarkupData parent;

    @Transient
    @JsonIgnore
    protected ParagraphUnit parentParagraphUnit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getKey() {
        return id;
    }

    /**
     * Searches for the parent paragraph unit of which this <i>MarkupData</i> belongs to. <br />
     * If it is not a direct child of a <i>ParagraphUnit</i> (ex.  a <i>Paragraph</i>) it will ask <br />
     * it's direct <i>MarkupData</i> parent.
     * 
     * @return The parent <i>ParagraphUnit</i> of which this <i>MarkupData</i> belongs to.
     */
    @JsonIgnore
    public ParagraphUnit getParagraphUnit() {
    	if ((parentParagraphUnit == null) && (parent != null)) {
    		parentParagraphUnit = parent.getParagraphUnit();
    	}
        return parentParagraphUnit;
    }

    public void setParagraphUnit(ParagraphUnit paragraphUnit) {
        this.parentParagraphUnit = paragraphUnit;
    }

    /**
     * Searches for it's parent <i>File</i> through it's <i>ParagraphUnit</i> parent. 
     * <br />
     * <i>See: </i> {@link #getParagraphUnit()}.
     * 
     * @return The parent <i>File</i> of which this <i>MarkupData</i> belongs to.
     */
    @JsonIgnore
    public File getParentFile() {
    	ParagraphUnit paragraphUnit = getParagraphUnit();
    	return (paragraphUnit != null) ? paragraphUnit.getParent() : null;
    }

    /**
     * Searches for it's parent <i>Document</i> through it's <i>File</i> parent.
     * <br />
     * <i>See: </i> {@link #getParentFile()}.
     * 
     * @return The parent <i>Document</i> of which this <i>MarkupData</i> belongs to.
     */
    @JsonIgnore
    public Document getParentDocument() {
    	File parentFile = getParentFile();
    	return (parentFile != null) ? parentFile.getParent() : null;
    }

    @Transient
    public void setParent(MarkupData parent) {
        this.parent = parent;
    }

    @Transient
    public MarkupData getParent() {
        return parent;
    }

    protected void copyPropertiesTo(MetaData clone) {
    	MarkupData markupData = convertType(clone);
    	if (markupData != null) {
    		super.copyPropertiesTo(markupData);
    		markupData.setId(id);
    		markupData.setType(type);
    		// TODO: add parent and paragraph unit
    	}
    }

    /**
     * Clones this <i>MarkupData</i> without it's children (if it has any).
     * <br />
     * @return A clone of this element without it's children.
     */
    // TODO: this should be moved in MarkupDataContainer
    public abstract MarkupData duplicateWithoutChildren();

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        MarkupData that = (MarkupData) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id;
    }
}
