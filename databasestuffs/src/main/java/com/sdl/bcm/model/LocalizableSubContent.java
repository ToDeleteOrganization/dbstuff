package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * A reference to paragraphUnits containing localizable
 * content that appears in the tag content of this tag
 * (e.g. localizable XML attribute values).
 * Example:
 * This is a picture of a dog: <img alt="dog" src="..."/>.
 * in the img tag above, alternative description text "dog" can be translated
 */
@JsonIgnoreProperties(value = { "metadata" })
public class LocalizableSubContent extends MetaData{
    /**
     * The id of the paragraph unit that contains the extracted sub-content for translation.
     */
    private String paragraphUnitId;

    /**
     * The text offset in the tagContent or startTagContent property of the
     * corresponding tag where the source localizable content starts.
     */
    private int sourceTagContentOffset;

    /**
     * The length in characters of the  source localizable content in the
     * tagContent or startTagContent property of the corresponding tag.
     */
    private int length;

    public LocalizableSubContent() {
    }

    public int getSourceTagContentOffset() {
        return sourceTagContentOffset;
    }

    public void setSourceTagContentOffset(int sourceTagContentOffset) {
        this.sourceTagContentOffset = sourceTagContentOffset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getParagraphUnitId() {
        return paragraphUnitId;
    }

    public void setParagraphUnitId(String paragraphUnitId) {
        this.paragraphUnitId = paragraphUnitId;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        LocalizableSubContent that = (LocalizableSubContent) o;
        return new EqualsBuilder()
                .append(sourceTagContentOffset, that.sourceTagContentOffset)
                .append(length, that.length)
                .append(paragraphUnitId, that.paragraphUnitId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + sourceTagContentOffset;
        result = 31 * result + length;
        result = 31 * result + (paragraphUnitId != null ? paragraphUnitId.hashCode() : 0);
        return result;
    }

}
