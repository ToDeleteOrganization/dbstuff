package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * A standalone inline tag that appears inside the source or target content
 * in a localizable ParagraphUnit in the bilingual content model.
 */
@JsonIgnoreProperties(value = {"children"})
public class PlaceholderTag extends MarkupData {
    public static final String TYPE = "placeholderTag";

    /**
     * Id that identifies this tag. When cloning tags, the tag id remains the same,
     * but the (instance) id changes, especially when copying tags from source to target.
     * Tags with the same id are considered identical,
     * so should have the same properties and meta-data.
     */
    private int placeholderTagDefinitionId;

    public PlaceholderTag() {
        this(null);
    }

    public PlaceholderTag(MarkupData parent) {
        setParent(parent);
        setType(TYPE);
    }

    public int getPlaceholderTagDefinitionId() {
        return placeholderTagDefinitionId;
    }

    public void setPlaceholderTagDefinitionId(int placeholderTagDefinitionId) {
        this.placeholderTagDefinitionId = placeholderTagDefinitionId;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public PlaceholderTag duplicateWithoutChildren() {
    	return deepClone();
    }

    @Override
    public PlaceholderTag deepClone() {
    	PlaceholderTag clone = new PlaceholderTag(getParent());
    	copyPropertiesTo(clone);
    	return clone;
    }

    public void copyPropertiesTo(MetaData metaData ) {
    	PlaceholderTag placeHolderTag = convertType(metaData);
    	if (placeHolderTag != null) {
    		placeHolderTag.setPlaceholderTagDefinitionId(this.placeholderTagDefinitionId);
    	}
    }

    /**
     * {@inheritDoc}
     * <br /> <br />
     * 
     * <i>This will be removed in future versions, from PlaceholderTag.</i>
     * <br />
     * 
     * @return An empty list.
     */
	@Override
	@JsonIgnore
	public List<MarkupData> getChildren() {
		return new LinkedList<MarkupData>();
	}

	@Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        PlaceholderTag that = (PlaceholderTag) o;
        return new EqualsBuilder()
                .append(placeholderTagDefinitionId, that.placeholderTagDefinitionId)
                .isEquals();
    }

    @Override
    public boolean deepEquals(Object o) {
        return equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + placeholderTagDefinitionId;
        return result;
    }
}
