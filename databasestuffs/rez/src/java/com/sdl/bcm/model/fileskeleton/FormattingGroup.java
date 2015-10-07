package com.sdl.bcm.model.fileskeleton;

import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * A unique set of formatting items (name-value pairs), identified by a unique id.
 * Supported formatting types:
 * - TextColor: "r,g,b"
 * - BackgroundColor: "r,g,b"
 * - Bold: true|false
 * - FontName: name of font family
 * - FontSize: float, size in points.
 * - Italic: true|false
 * - Strikethrough: true|false
 * - TextDirection: Inherit|LeftToRight|RightToLeft
 * - TextPosition: Normal|Superscript|Subscript
 * - Underline: true|false
 * <p/>
 * Formatting groups are referred to from context and tag pair definitions.
 */
public class FormattingGroup extends AbstractSkeletonItem {

    /**
     * Map that contains properties as pairs of key-value
     */
    private Map<String, String> items;

    public FormattingGroup() {
        items = new HashMap<>();
    }

    public Map<String, String> getItems() {
        return items;
    }

    public void addItem(String name, String value) {
        items.put(name, value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setItems(Map<String, String> items) {
        if(items instanceof HashMap)
            this.items = (HashMap)items;
    }

    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	FormattingGroup formattingGroup = convertType(clone);
    	if (formattingGroup != null) {
    		super.copyPropertiesTo(clone);
    		formattingGroup.setItems(new HashMap<>(items));;
    	}
    }

    @Override
    public FormattingGroup deepClone() {
    	FormattingGroup formattingGroup = new FormattingGroup();
    	copyPropertiesTo(formattingGroup);
    	return formattingGroup;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        FormattingGroup that = (FormattingGroup) o;
        return new EqualsBuilder()
                .append(items, that.items)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
