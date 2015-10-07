package com.sdl.bcm.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * A text container.
 */
@JsonIgnoreProperties(value = { "id","children" })
public class TextMarkup extends MarkupData {
    public static final String TYPE = "text";

    /**
     * Actual text
     */
    private String text;

    public TextMarkup() {
        this(null);
    }

    public TextMarkup(MarkupData parent) {
        this(parent, null);
    }

    public TextMarkup(MarkupData parent, String text) {
    	super.setParent(parent);
        this.text = text;
        setType(TYPE);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * {@inheritDoc}
     * <br /> <br />
     * <i>This will be removed in future versions, from TextMarkup.</i>
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
    public boolean accept(BCMVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public TextMarkup duplicateWithoutChildren() {
        return deepClone();
    }

    @Override
    public TextMarkup deepClone() {
    	TextMarkup clone = new TextMarkup(getParent());
        copyPropertiesTo(clone);
        return clone;
    }

    public void copyPropertiesTo(MetaData metaData ) {
    	TextMarkup textMarkup = convertType(metaData);
    	if (textMarkup != null) {
    		textMarkup.setText(text);
    	}
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TextMarkup that = (TextMarkup) o;
        return new EqualsBuilder()
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public boolean deepEquals(Object o) {
        return equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return text;
    }

}
