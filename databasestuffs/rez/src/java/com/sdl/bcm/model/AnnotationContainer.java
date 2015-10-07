package com.sdl.bcm.model;

import org.apache.commons.lang.builder.EqualsBuilder;

public abstract class AnnotationContainer extends MarkupDataContainer {
    /**
     * One logical annotation can consist of multiple annotation containers
     * (wrapping the different parts of content to which the annotation applies).
     * This is an id that can be used to correlate these containers.
     * The containers related to the same annotation should all be of the same annotation type.
     */
    private int annotationId;

    public AnnotationContainer() {
    }

    public AnnotationContainer(MarkupData parent) {
        super(parent);
    }

    public int getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(int annotationId) {
        this.annotationId = annotationId;
    }

    protected void copyPropertiesTo(MetaData clone) {
    	AnnotationContainer annotation = super.convertType(clone);
    	if (annotation != null) {
    		super.copyPropertiesTo(annotation);
    		annotation.setAnnotationId(annotationId);
    	}
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        AnnotationContainer that = (AnnotationContainer) o;
        return new EqualsBuilder()
                .append(annotationId, that.annotationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + annotationId;
        return result;
    }
}
