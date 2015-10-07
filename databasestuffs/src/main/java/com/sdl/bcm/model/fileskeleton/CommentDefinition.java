package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sdl.bcm.CustomJsonDateDeserializer;
import com.sdl.bcm.CustomJsonDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Date;

/**
 * A comment related to a paragraph units of to a range of
 * markup data (identified by a comment container).
 * This type of comment can be used by translators or reviewers of the
 * document to add information related to the translation process to the document.
 * These comments will not be reflected in the final target document.
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CommentDefinition extends MetaData {

    /**
     * Unique identifier for any definition.
     */
    protected Integer id;

    /**
     * The comment text.
     */
    private String text;

    /**
     * The user who created the comment.
     */
    private String author;

    /**
     * The date and time when the comment was created.
     */
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private Date date;

    /**
     * The severity, expressing the nature of the comment.
     */
    private CommentSeverity commentSeverity;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
            return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @JsonSerialize(using = CustomJsonDateSerializer.class)
    public Date getDate() {
        return date;
    }
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public void setDate(Date date) {
        this.date = date;
    }

    public CommentSeverity getCommentSeverity() {
        return commentSeverity;
    }
    public void setCommentSeverity(CommentSeverity commentSeverity) {
        this.commentSeverity = commentSeverity;
    }

    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	CommentDefinition cd = convertType(clone);
    	if (cd != null) {
    		super.copyPropertiesTo(clone);
    		cd.setId(id);
    		cd.setText(text);
    		cd.setAuthor(author);
    		cd.setCommentSeverity(commentSeverity);
    		cd.setDate(date);
    	}
    }

    @Override
    public CommentDefinition deepClone() {
    	CommentDefinition cd = new CommentDefinition();
    	copyPropertiesTo(cd);
    	return cd;
    }
    
    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        CommentDefinition that = (CommentDefinition) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(text, that.text)
                .append(author, that.author)
                .append(date, that.date)
                .append(commentSeverity, that.commentSeverity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (commentSeverity != null ? commentSeverity.hashCode() : 0);
        return result;
    }
}