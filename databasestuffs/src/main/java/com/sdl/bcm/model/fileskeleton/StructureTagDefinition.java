package com.sdl.bcm.model.fileskeleton;

import com.sdl.bcm.model.LocalizableSubContent;
import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.List;

/**
 * Structure tags are used by filters to represent constructs in the original
 * file format that are required to rebuild the final document.
 * - Structure tags can only occur in structure paragraph units.
 * - Structure paragraph units/tags are never shown to the user,
 * so should probably not be requested by the editor control.
 */
public class StructureTagDefinition extends MetaData {
    /**
     * Id that identifies this tag.
     */
    private int id;

    /**
     * Text to represent the tag. Typically this is the name of the tag
     * in the original format, without brackets (<>).
     * Note that this info should purely for display purposes.
     */
    private String displayText;

    /**
     * More descriptive text to represent the tag.
     * Typically this is the original tag content as the tag appears
     * in the source document ("<a href="..." />").
     * Note that this info should purely for display purposes.
     */
    private String tagContent;

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

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getTagContent() {
        return tagContent;
    }

    public void setTagContent(String tagContent) {
        this.tagContent = tagContent;
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
        StructureTagDefinition that = (StructureTagDefinition) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(displayText, that.displayText)
                .append(subContent, that.subContent)
                .append(tagContent, that.tagContent)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        result = 31 * result + (displayText != null ? displayText.hashCode() : 0);
        result = 31 * result + (subContent != null ? subContent.hashCode() : 0);
        result = 31 * result + (tagContent != null ? tagContent.hashCode() : 0);
        return result;
    }
}
