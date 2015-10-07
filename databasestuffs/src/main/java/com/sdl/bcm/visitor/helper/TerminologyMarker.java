package com.sdl.bcm.visitor.helper;

import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.MarkupDataContainer;
import com.sdl.bcm.model.MetaData;
import com.sdl.bcm.visitor.BCMVisitor;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * @author Dan Tarba
 * @deprecated since 2.5; this was used in terminology annotation by AddTerminologyVisitor
 *  use TerminologyProcessor instead
 */
@Deprecated
public class TerminologyMarker extends MarkupDataContainer {
    public static final String TYPE = "terminologyMarker";

    private boolean termStart;

    private int annotationId = -1;

    public TerminologyMarker(MarkupData parent) {
        super(parent);
    }

    public TerminologyMarker(MarkupData parent, boolean termStart, int annotationId) {
        super(parent);
        setType(TYPE);
        this.termStart = termStart;
        this.annotationId = annotationId;
    }

    public boolean isTermStart() {
        return termStart;
    }

    public int getAnnotationId() {
        return annotationId;
    }

    @Override
    public MarkupDataContainer duplicateWithoutChildren() {
        return this;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        return false;
    }

    protected void copyPropertiesTo(MetaData metaData) {
    	TerminologyMarker termMarker = super.convertType(metaData);
    	if (termMarker != null) {
    		super.copyPropertiesTo(termMarker);
    		termMarker.termStart = this.termStart;
    		termMarker.annotationId = this.annotationId;
    	}
    }
    
    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TerminologyMarker that = (TerminologyMarker) o;
        return new EqualsBuilder()
                .append(termStart, that.termStart)
                .append(annotationId, that.annotationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (termStart ? 1 : 0);
        result = 31 * result + annotationId;
        return result;
    }
}