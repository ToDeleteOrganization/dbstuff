package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * A translation for a specific source term.
 */
@JsonIgnoreProperties(value = { "metadata" })
public class TermTranslation extends MetaData {
    private String id;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	TermTranslation termTrans = convertType(clone);
    	if (termTrans != null) {
    		super.copyPropertiesTo(clone);
    		termTrans.setId(id);
    		termTrans.setText(text);
    	}
    }

    @Override
    public TermTranslation deepClone() {
    	TermTranslation termTranslation = new TermTranslation();
    	copyPropertiesTo(termTranslation);
    	return termTranslation;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TermTranslation that = (TermTranslation) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(text, that.text)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

}
