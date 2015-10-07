package com.sdl.bcm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sdl.bcm.CustomJsonDateSerializer;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;

/**
 * A container that identifies a piece of content that was changed
 * (insert, deleted) while change tracking was on.
 */
public class RevisionContainer extends MarkupDataContainer {

	public static final String TYPE = "revision";

    /**
     * The type of change
     */
    private RevisionType revisionType;

    /**
     * The name or user id of the author who made the change.
     */
    private String author;

    /**
     * The timestamp when the change was made.
     */
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Date timestamp;

    public RevisionContainer() {
        setType(TYPE);
    }

    public RevisionContainer(MarkupData parent) {
        super(parent);
        setType(TYPE);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getTimestamp() {
        return timestamp;
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public RevisionType getRevisionType() {
        return revisionType;
    }

    public void setRevisionType(RevisionType revisionType) {
        this.revisionType = revisionType;
    }

    @Override
    public RevisionContainer duplicateWithoutChildren() {
        RevisionContainer clone = new RevisionContainer(getParent());
        copyPropertiesTo(clone);
        return clone;
    }

    protected void copyPropertiesTo(MetaData clone) {
    	RevisionContainer revisionContainer = super.convertType(clone);
    	if (revisionContainer != null) {
    		super.copyPropertiesTo(revisionContainer);
    		revisionContainer.setAuthor(author);
    		revisionContainer.setRevisionType(revisionType);
    		revisionContainer.setTimestamp(timestamp);
    	}
    }

    @Override
    public RevisionContainer deepClone() {
    	RevisionContainer rc = duplicateWithoutChildren();
    	cloneChildrenInto(rc);
    	return rc;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (MarkupData elementChild : getChildren()) {
                if (!elementChild.accept(visitor)) {
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
        RevisionContainer that = (RevisionContainer) o;
        return new EqualsBuilder()
                .append(revisionType, that.revisionType)
                .append(author, that.author)
                .append(timestamp, that.timestamp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (revisionType != null ? revisionType.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
