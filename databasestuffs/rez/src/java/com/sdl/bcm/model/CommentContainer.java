package com.sdl.bcm.model;

import com.sdl.bcm.ISkeletonItemReference;
import com.sdl.bcm.model.fileskeleton.CommentDefinition;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.BCMVisitor;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CommentContainer extends AnnotationContainer implements ISkeletonItemReference<CommentDefinition> {
    public static final String TYPE = "comment";

    private Integer commentDefinitionId;

    public CommentContainer() {
        setType(TYPE);
    }

    public CommentContainer(MarkupData parent) {
        super(parent);
        setType(TYPE);
    }

    public Integer getCommentDefinitionId() {
        return commentDefinitionId;
    }

    public void setCommentDefinitionId(Integer commentDefinitionId) {
        this.commentDefinitionId = commentDefinitionId;
    }

    @Override
    public CommentContainer duplicateWithoutChildren() {
        CommentContainer clone = new CommentContainer(getParent());
        copyPropertiesTo(clone);
        return clone;
    }

    protected void copyPropertiesTo(MetaData metaData) {
    	CommentContainer comment = convertType(metaData);
    	if (comment != null) {
    		super.copyPropertiesTo(comment);
    		comment.setCommentDefinitionId(commentDefinitionId);
    	}
    }

    @Override
    public CommentContainer deepClone() {
    	CommentContainer cc = duplicateWithoutChildren();
    	cloneChildrenInto(cc);
    	return cc;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (BCMElement bcmElement : getACopyOfChildrenList()) {
                if (!bcmElement.accept(visitor)) {
                    break;
                }
            }
        }
        return visitor.visitLeave(this);
    }

    @Override
	public CommentDefinition getSkeletonDefinition() {
    	CommentDefinition commentDef = null;
    	FileSkeleton fileSkeleton = super.getFileSkeleton();
    	if (fileSkeleton != null) {
    		commentDef = new FileReferenceFinder<CommentDefinition>(fileSkeleton.getCommentDefinitions(), getCommentDefinitionId()).find();
    	}
		return commentDef;
	}

    @Override
    public boolean equals(Object o) {
        if(! super.equals(o))
            return false;
        CommentContainer that = (CommentContainer) o;
        return new EqualsBuilder()
                .append(commentDefinitionId, that.commentDefinitionId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (commentDefinitionId != null ? commentDefinitionId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
    	ToStringBuilder tsb = new ToStringBuilder(this);
    	tsb.append("[commentDefinitionId = " + commentDefinitionId + ", annotationId = " + getAnnotationId() + "]");
    	return tsb.toString();
    }

}
