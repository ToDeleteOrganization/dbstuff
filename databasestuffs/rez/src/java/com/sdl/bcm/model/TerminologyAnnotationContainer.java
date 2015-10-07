package com.sdl.bcm.model;

import com.sdl.bcm.ISkeletonItemReference;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.BCMVisitor;

import org.apache.commons.lang.builder.EqualsBuilder;

public class TerminologyAnnotationContainer extends AnnotationContainer implements ISkeletonItemReference<TerminologyData> {
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
    public TerminologyAnnotationContainer duplicateWithoutChildren() {
        TerminologyAnnotationContainer clone = new TerminologyAnnotationContainer(getParent());
        copyPropertiesTo(clone);
        return clone;
    }

    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	TerminologyAnnotationContainer termAnnContainer = convertType(clone);
    	if (termAnnContainer != null) {
    		super.copyPropertiesTo(termAnnContainer);
    		termAnnContainer.setTerminologyDataId(terminologyDataId);
    	}
    	super.copyPropertiesTo(clone);
    }
    
    @Override
    public TerminologyAnnotationContainer deepClone() {
    	TerminologyAnnotationContainer termAnn = duplicateWithoutChildren();
    	cloneChildrenInto(termAnn);
    	return termAnn;
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
	public TerminologyData getSkeletonDefinition() {
    	TerminologyData termData = null;
    	FileSkeleton fileSkeleton = super.getFileSkeleton();
    	if (fileSkeleton != null) {
    		termData = new FileReferenceFinder<TerminologyData>(fileSkeleton.getTerminologyData(), getTerminologyDataId()).find();
    	}
		return termData;
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
    	return "TerminologyAnnotationContainer{" + getAnnotationId() + "}";
    }

}
