package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdl.bcm.model.*;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.List;

/**
 * A standalone inline tag that appears inside the source
 * or target content in a localizable ParagraphUnit in the bilingual content model.
 */
public class PlaceholderTagDefinition extends MetaData {

    /**
     * Id that identifies this tag. When cloning tags, the tag id remains the same,
     * but the (instance) id changes, especially when copying tags from source to target.
     * Tags with the same id are considered identical, so should have the same properties and meta-data.
     */
    private int id;

    /**
     * Text to represent the tag. Typically this is the name of the tag in the original format,
     * without brackets (<>).
     */
    private String displayText;

    /**
     * More descriptive text to represent the tag. Typically this is the original tag content
     * as the tag appears in the source document ("<a href="..." />").
     */
    private String tagContent;

    /**
     * May contain the linguistic text equivalent for the tag, if one exists.
     * If the tag should be treated linguistically similar to a piece of text,
     * this property can be set to that text. If a tag has no linguistic text equivalent
     * (e.g. if it can be a marker for a location or represent the start or end of formatting),
     * this property should be <c>null</c> or an empty string.
     * Note that text stored in this property is typically not the exact equivalent of the tag.
     * (If it were, there would be no reason to use a tag in the first place.)
     * However, from a linguistic processing point of view this text can be used to determine
     * grammatical properties of the tag that may affect the surrounding text, e.g. gender,
     * numerical and singular/plural form.
     * Default: null
     */
    private String textEquivalent;

    /**
     * Segmentation hint that informs the segmentation engine of how to treat this tag.
     */
    private SegmentationHint segmentationHint = SegmentationHint.MAY_EXCLUDE;

    /**
     * List of localizable content that appears in the tag content of this tag (e.g. localizable XML attribute values).
     */
    private List<LocalizableSubContent> subContent;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTagContent() {
        return tagContent;
    }
    public void setTagContent(String tagContent) {
        this.tagContent = tagContent;
    }

    public String getDisplayText() {
        return displayText;
    }
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getTextEquivalent() {
        return textEquivalent;
    }
    public void setTextEquivalent(String textEquivalent) {
        this.textEquivalent = textEquivalent;
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public SegmentationHint getSegmentationHint() {
        return segmentationHint;
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public void setSegmentationHint(SegmentationHint segmentationHint) {
        this.segmentationHint = segmentationHint;
    }

    public List<LocalizableSubContent> getSubContent() {
        return subContent;
    }

    public void setSubContent(List<LocalizableSubContent> subContent) {
        this.subContent = subContent;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        PlaceholderTagDefinition that = (PlaceholderTagDefinition) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(displayText, that.displayText)
                .append(tagContent, that.tagContent)
                .append(textEquivalent, that.textEquivalent)
                .append(segmentationHint, that.segmentationHint)
                .append(subContent, that.subContent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        result = 31 * result + (displayText != null ? displayText.hashCode() : 0);
        result = 31 * result + (tagContent != null ? tagContent.hashCode() : 0);
        result = 31 * result + (textEquivalent != null ? textEquivalent.hashCode() : 0);
        result = 31 * result + (segmentationHint != null ? segmentationHint.hashCode() : 0);
        result = 31 * result + (subContent != null ? subContent.hashCode() : 0);
        return result;
    }
}
