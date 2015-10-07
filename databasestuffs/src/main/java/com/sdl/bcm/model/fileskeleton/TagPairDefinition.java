package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdl.bcm.model.*;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.List;

/**
 * A unique tag pair definition, referred to from tag pair objects in the paragraph unit collection.
 */
public class TagPairDefinition extends MetaData {

    /**
     * Id that identifies this tag. When cloning tags, the tag id remains the same,
     * but the (instance) id changes, especially when copying tags from source to target.
     * Tags with the same id are considered identical, so should have the same properties and meta-data.
     */
    private int id;

    /**
     * Text to represent the start tag of the tag pair.
     * Typically this is the name of the tag in the original format, without brackets (<>).
     * Note that this info should purely for display purposes.
     */
    private String startTagDisplayText;

    /**
     * More descriptive text to represent the start tag.
     * Typically this is the original tag content as the
     * tag appears in the source document ("<a href="..." />").
     * Note that this info should purely for display purposes.
     */
    private String startTagContent;

    /**
     * Text to represent the end tag of the tag pair.
     * Typically this is the name of the tag in the original format, without brackets (<>).
     * Note that this info should purely for display purposes.
     */
    private String endTagDisplayText;

    /**
     * More descriptive text to represent the end tag.
     * Typically this is the original tag content as the tag appears
     * in the source document ("<a href="..." />").
     * Note that this info should purely for display purposes.
     */
    private String endTagContent;

    /**
     * Indicates whether the tag is allowed to be hidden during editing operations. Default: false.
     */
    private boolean canHide;

    /**
     * Segmentation hint that informs the segmentation engine of how to treat this tag.
     */
    private SegmentationHint segmentationHint = SegmentationHint.MAY_EXCLUDE;

    /**
     * The optional id of a formatting group, defining the formatting that
     * should be applied to all content within this tag pair.
     */
    private int formattingGroupId;

    /**
     * List of LocalizableSubContent objects
     */
    private List<LocalizableSubContent> subContent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTagDisplayText() {
        return startTagDisplayText;
    }

    public void setStartTagDisplayText(String startTagDisplayText) {
        this.startTagDisplayText = startTagDisplayText;
    }

    public String getStartTagContent() {
        return startTagContent;
    }

    public void setStartTagContent(String startTagContent) {
        this.startTagContent = startTagContent;
    }

    public String getEndTagDisplayText() {
        return endTagDisplayText;
    }

    public void setEndTagDisplayText(String endTagDisplayText) {
        this.endTagDisplayText = endTagDisplayText;
    }

    public String getEndTagContent() {
        return endTagContent;
    }

    public void setEndTagContent(String endTagContent) {
        this.endTagContent = endTagContent;
    }

    public boolean isCanHide() {
        return canHide;
    }

    public void setCanHide(boolean canHide) {
        this.canHide = canHide;
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public SegmentationHint getSegmentationHint() {
        return segmentationHint;
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public void setSegmentationHint(SegmentationHint segmentationHint) {
        this.segmentationHint = segmentationHint;
    }

    public int getFormattingGroupId() {
        return formattingGroupId;
    }

    public void setFormattingGroupId(int formattingGroupId) {
        this.formattingGroupId = formattingGroupId;
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
        TagPairDefinition that = (TagPairDefinition) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(startTagDisplayText, that.startTagDisplayText)
                .append(startTagContent, that.startTagContent)
                .append(endTagDisplayText, that.endTagDisplayText)
                .append(endTagContent, that.endTagContent)
                .append(canHide, that.canHide)
                .append(segmentationHint, that.segmentationHint)
                .append(formattingGroupId, that.formattingGroupId)
                .append(subContent, that.subContent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        result = 31 * result + (startTagDisplayText != null ? startTagDisplayText.hashCode() : 0);
        result = 31 * result + (startTagContent != null ? startTagContent.hashCode() : 0);
        result = 31 * result + (endTagDisplayText != null ? endTagDisplayText.hashCode() : 0);
        result = 31 * result + (endTagContent != null ? endTagContent.hashCode() : 0);
        result = 31 * result + (canHide ? 1 : 0);
        result = 31 * result + (segmentationHint != null ? segmentationHint.hashCode() : 0);
        result = 31 * result + formattingGroupId;
        result = 31 * result + (subContent != null ? subContent.hashCode() : 0);
        return result;
    }
}
