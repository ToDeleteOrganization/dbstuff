package com.sdl.bcm.model.fileskeleton;

import com.sdl.bcm.model.MetaData;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * A file skeleton for a file in the document collection.
 * The file skeleton contains definitions of entities that
 * are referred throughout the content in the paragraph units collection.
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "skeleton")
public class FileSkeleton extends MetaData {
    /**
     * The ID of the file to which this skeleton belongs.
     */
    @Id
    private String fileId;

    /**
     * A List with commentDefinitions made on parent File
     */
    private List<CommentDefinition> commentDefinitions;

    /**
     * List with Context definitions which are referred throughout the contexts List
     */
    private List<ContextDefinition> contextDefinitions;

    /**
     * List with Contexts which are referred throughout the content in paragraph units
     */
    private List<Context> contexts;

    /**
     * FormattingGroup list which is referred in paragraph units.
     * FormattingGroup objects contain text style information
     * (like font, color, font style, etc)
     */
    private List<FormattingGroup> formattingGroups;

    /**
     * TagPairDefinition List is referred from paragraph units content and contains
     * template TagPair definitions
     */
    private List<TagPairDefinition> tagPairDefinitions;

    /**
     * PlaceholderTagDefinition List is referred from paragraph units content and contains
     * template PlaceholderTagDefinition definitions
     */
    private List<PlaceholderTagDefinition> placeholderTagDefinitions;

    /**
     * StructureTagDefinition List is referred from structure paragraph units content and contains
     * template StructureTagDefinition definitions
     */
    private List<StructureTagDefinition> structureTagDefinitions;

    /**
     * Terminology data holder. These Structures are referred from ParagraphUnits terminology containers
     */
    private List<TerminologyData> terminologyData;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public List<CommentDefinition> getCommentDefinitions() {
        return commentDefinitions;
    }

    public void setCommentDefinitions(List<CommentDefinition> commentDefinitions) {
        this.commentDefinitions = commentDefinitions;
    }

    public List<ContextDefinition> getContextDefinitions() {
        return contextDefinitions;
    }

    public void setContextDefinitions(List<ContextDefinition> contextDefinitions) {
        this.contextDefinitions = contextDefinitions;
    }

    public List<Context> getContexts() {
        return contexts;
    }

    public void setContexts(List<Context> contexts) {
        this.contexts = contexts;
    }

    public List<FormattingGroup> getFormattingGroups() {
        return formattingGroups;
    }

    public void setFormattingGroups(List<FormattingGroup> formattingGroups) {
        this.formattingGroups = formattingGroups;
    }

    public List<TagPairDefinition> getTagPairDefinitions() {
        return tagPairDefinitions;
    }

    public void setTagPairDefinitions(List<TagPairDefinition> tagPairDefinitions) {
        this.tagPairDefinitions = tagPairDefinitions;
    }

    public List<PlaceholderTagDefinition> getPlaceholderTagDefinitions() {
        return placeholderTagDefinitions;
    }

    public void setPlaceholderTagDefinitions(List<PlaceholderTagDefinition> placeholderTagDefinitions) {
        this.placeholderTagDefinitions = placeholderTagDefinitions;
    }

    public List<StructureTagDefinition> getStructureTagDefinitions() {
        return structureTagDefinitions;
    }

    public void setStructureTagDefinitions(List<StructureTagDefinition> structureTagDefinitions) {
        this.structureTagDefinitions = structureTagDefinitions;
    }

    public List<TerminologyData> getTerminologyData() {
        return terminologyData;
    }

    public void setTerminologyData(List<TerminologyData> terminologyData) {
        this.terminologyData = terminologyData;
    }

    protected void copyPropertiesTo(MetaData clone) {
    	FileSkeleton fileSkeleton = convertType(clone);
    	if (fileSkeleton != null) {
    		fileSkeleton.setFileId(fileId);
    		List<CommentDefinition> m = new ArrayList<CommentDefinition>();
    		for (CommentDefinition cd : commentDefinitions) {
    			CommentDefinition csd = cd.deepClone();
    			m.add(csd);
    		}
    	}
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        FileSkeleton that = (FileSkeleton) o;
        return new EqualsBuilder()
                .append(fileId, that.fileId)
                .append(commentDefinitions, that.commentDefinitions)
                .append(contextDefinitions, that.contextDefinitions)
                .append(contexts, that.contexts)
                .append(formattingGroups, that.formattingGroups)
                .append(structureTagDefinitions, that.structureTagDefinitions)
                .append(tagPairDefinitions, that.tagPairDefinitions)
                .append(placeholderTagDefinitions, that.placeholderTagDefinitions)
                .append(terminologyData, that.terminologyData)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fileId != null ? fileId.hashCode() : 0);
        result = 31 * result + (commentDefinitions != null ? commentDefinitions.hashCode() : 0);
        result = 31 * result + (contextDefinitions != null ? contextDefinitions.hashCode() : 0);
        result = 31 * result + (contexts != null ? contexts.hashCode() : 0);
        result = 31 * result + (formattingGroups != null ? formattingGroups.hashCode() : 0);
        result = 31 * result + (structureTagDefinitions != null ? structureTagDefinitions.hashCode() : 0);
        result = 31 * result + (tagPairDefinitions != null ? tagPairDefinitions.hashCode() : 0);
        result = 31 * result + (placeholderTagDefinitions != null ? placeholderTagDefinitions.hashCode() : 0);
        result = 31 * result + (terminologyData != null ? terminologyData.hashCode() : 0);
        return result;
    }
}
