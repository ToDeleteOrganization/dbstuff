package com.sdl.bcm.model;

import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.BCMVisitor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class TerminologyAnnotationContainer extends AnnotationContainer {
    public static final String TYPE = "terminology";

    /**
     * The id of a TerminologyData object in the file skeleton that contains the information about
     * the terminology matches for the annotated piece of content.
     */
    private int terminologyDataId;

    public TerminologyAnnotationContainer() {
        setType(TYPE);
    }

    public TerminologyAnnotationContainer(MarkupData parent) {
        super(parent);
        setType(TYPE);
    }

    public int getTerminologyDataId() {
        return terminologyDataId;
    }

    public void setTerminologyDataId(int terminologyDataId) {
        this.terminologyDataId = terminologyDataId;
    }

    @Override
    public MarkupDataContainer duplicateWithoutChildren() {
        TerminologyAnnotationContainer clone = new TerminologyAnnotationContainer(getParent());
        clone.setTerminologyDataId(getTerminologyDataId());
        clone.setAnnotationId(getAnnotationId());
        copyPropertiesTo(clone);
        return clone;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (BCMElement child : getACopyOfChildrenList()) {
                if (!child.accept(visitor)) {
                    break;
                }
            }
        }
        return visitor.visitLeave(this);
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TerminologyAnnotationContainer that = (TerminologyAnnotationContainer) o;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(terminologyDataId, that.terminologyDataId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + terminologyDataId;
        return result;
    }

    @Override
    public String toString() {
    	ToStringBuilder tsb = new ToStringBuilder(this);
    	tsb.append("[terminologyDataId = " + terminologyDataId + ", annotationId = " + getAnnotationId() + "]");
    	return tsb.toString();
    }

}
