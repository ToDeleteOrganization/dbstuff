package com.sdl.bcm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sdl.bcm.CustomJsonDateSerializer;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;

public class FeedbackContainer extends MarkupDataContainer {
    public static final String TYPE = "feedback";
    /**
     * The type of change
     */
    private FeedbackType feedbackType;

    /**
     * The name or user id of the author who made the change.
     */
    private String author;

    /**
     * The timestamp when the change was made.
     */
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Date timestamp;

    /**
     * Feedback category
     */
    private String category;

    /**
     * Document category
     */
    private String documentCategory;

    /**
     * Feedback severity
     */
    private String severity;

    /**
     * Feedback comment
     */
    private Integer commentDefinitionId;

    public FeedbackContainer() {
        setType(TYPE);
    }

    public FeedbackContainer(MarkupData parent) {
        super(parent);
        setType(TYPE);
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getTimestamp() {
        return timestamp;
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDocumentCategory() {
        return documentCategory;
    }

    public void setDocumentCategory(String documentCategory) {
        this.documentCategory = documentCategory;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getCommentDefinitionId() {
        return commentDefinitionId;
    }

    public void setCommentDefinitionId(Integer commentDefinitionId) {
        this.commentDefinitionId = commentDefinitionId;
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
    public MarkupDataContainer duplicateWithoutChildren() {
        FeedbackContainer clone = new FeedbackContainer(getParent());
        copyPropertiesTo(clone);
        return clone;
    }

    protected void copyPropertiesTo(MetaData clone) {
    	FeedbackContainer feedbackContainer = convertType(clone);
    	if (feedbackContainer != null) {
    		super.copyPropertiesTo(feedbackContainer);
    		feedbackContainer.setFeedbackType(feedbackType);
    		feedbackContainer.setAuthor(author);
    		feedbackContainer.setTimestamp(timestamp);
    		feedbackContainer.setCategory(category);
    		feedbackContainer.setDocumentCategory(documentCategory);
    		feedbackContainer.setSeverity(severity);
    		feedbackContainer.setCommentDefinitionId(commentDefinitionId);
    	}
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        FeedbackContainer that = (FeedbackContainer) o;
        return new EqualsBuilder()
                .append(author, that.author)
                .append(timestamp, that.timestamp)
                .append(category, that.category)
                .append(documentCategory, that.documentCategory)
                .append(severity, that.severity)
                .append(commentDefinitionId, that.commentDefinitionId)
                .append(feedbackType, that.feedbackType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (documentCategory != null ? documentCategory.hashCode() : 0);
        result = 31 * result + (severity != null ? severity.hashCode() : 0);
        result = 31 * result + (commentDefinitionId != null ? commentDefinitionId.hashCode() : 0);
        result = 31 * result + (feedbackType != null ? feedbackType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FeedbackContainer{" + feedbackType + "," + severity +  '}';
    }

}
