package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.*;
import com.sdl.bcm.visitor.BCMCompositeElement;
import com.sdl.bcm.visitor.BCMVisitor;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A continuous unit of source language content within a single context,
 * and (if applicable) the localized target language version of the content.
 * This often corresponds to a paragraph of text in a native file.
 * <p/>
 * There are two types of paragraph units: those that contain localizable content,
 * and those that do not contain localizable content. The latter is referred to
 * as a structure paragraph unit, and typically only contains structure
 * tags and associated whitespace.
 * <p/>
 * The isStructure property indicates whether this paragraph unit is a structure paragraph unit.
 * <p/>
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "paragraphs")
@JsonPropertyOrder({"metadata", "id", "parentFileId", "index", "source", "target", "contextId", "contextList", "structureContextId","isLocked", "isStructure"})
public class ParagraphUnit extends MetaData implements BCMCompositeElement<File, Paragraph> {

    public enum TargetParagraph {
        SOURCE_PARAGRAPH, TARGET_PARAGRAPH
    }

    /**
     * Globally Unique ID of the paragraph unit.
     */
    @Id
    private String id;

    /**
     * The unique id of the file to which this paragraph unit belongs
     */
    private String parentFileId;

    /**
     * The index of the paragraph unit within the document.
     */
    private int index;

    /**
     * Transient field that is populated only in memory.
     * Parent file object is not saved in Mongo and is not serialized.
     */
    @Transient
    @JsonBackReference
    private File parentFile;

    /**
     * Source paragraph unit that have to be translated
     */
    private Paragraph source;

    /**
     * Target paragraphs unit the source paragraph have to be translated to
     */
    private Paragraph target;

    /**
     * Whether this is a structure or a localizable paragraph unit.
     */
    private boolean isStructure;

    /**
     * Whether this PU is locked, i.e. whether its contents can be changed by the user.
     * If this flag is true, then this paragraph cannot be altered by Client. However,
     * this paragraph will be shown to com.sdl.bcm.api.client in read-only mode
     */
    private boolean isLocked;

    private int contextId;

    private List<Integer> contextList;

    private List<Integer> commentDefinitionIds;

    private Integer structureContextId;

    public ParagraphUnit() {
    }

    private ParagraphUnit(File parentFile) {
    	this.parentFile = parentFile;
    }

    //================= UUID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //================= ParentID (File ID)
    public String getParentFileId() {
        return parentFileId;
    }

    public void setParentFileId(String parentFileId) {
        this.parentFileId = parentFileId;
    }

    //================= Source
    public Paragraph getSource() {
        return source;
    }

    public void setSource(Paragraph source) {
        this.source = source;
        source.setParagraphUnit(this);
    }

    //================= Target
    public Paragraph getTarget() {
        return target;
    }

    public void setTarget(Paragraph target) {
        this.target = target;
        target.setParagraphUnit(this);
    }

    public File getParentFile() {
        return parentFile;
    }

    public void setParentFile(File parentFile) {
        this.parentFile = parentFile;
    }

    /**
     * {@inheritDoc}
     * <br />
     * 
     * This works the same as {@link #getParentFile()}.
     * <br />
     */
    @Override
	public File getParent() {
		return parentFile;
	}

	/**
	 * This returns a list of this <i>ParagraphUnits</i> paragraphs. <br/>
	 * The first element in the list will be the source paragraph and 
	 * the second element will be the target paragraph.
	 * 
	 * @return The paragraphs contained by this <i>Paragraph Unit</i>.
	 */
	@Override
	@JsonIgnore
	public List<Paragraph> getChildren() {
		List<Paragraph> children = new LinkedList<Paragraph>();
		children.add(getSource());
		children.add(getTarget());
		return children;
	}

	public void updateSegment(Segment segment) {
        target.updateSegment(segment);
    }

    @JsonProperty("isStructure")
    public boolean isStructure() {
        return isStructure;
    }

    @JsonProperty("isStructure")
    public void setStructure(boolean isStructure) {
        this.isStructure = isStructure;
    }

    @JsonProperty("isLocked")
    public boolean isLocked() {
        return isLocked;
    }

    @JsonProperty("isLocked")
    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getContextId() {
        return contextId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    public List<Integer> getContextList() {
        return contextList;
    }

    public void setContextList(List<Integer> contextList) {
        this.contextList = contextList;
    }

    public List<Integer> getCommentDefinitionIds() {
        return commentDefinitionIds;
    }

    public void setCommentDefinitionIds(List<Integer> commentDefinitionIds) {
        this.commentDefinitionIds = commentDefinitionIds;
    }

    public Integer getStructureContextId() {
        return structureContextId;
    }

    public void setStructureContextId(Integer structureContextId) {
        this.structureContextId = structureContextId;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            if(source != null)
                source.accept(visitor);
            if(target != null)
                target.accept(visitor);
        }
        return visitor.visitLeave(this);
    }

    @JsonIgnore
    public String getPlainText(TargetParagraph targetParagraph) {
        ExtractPlainTextVisitor visitor = new ExtractPlainTextVisitor();
        if(targetParagraph.equals(TargetParagraph.TARGET_PARAGRAPH)) {
            getTarget().accept(visitor);
        } else {
            getSource().accept(visitor);
        }
        return visitor.getText();
    }

    /**
     * This will also create the source and the target paragraphs
     */
    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	ParagraphUnit pu = convertType(clone);
    	if (pu != null) {
    		super.copyPropertiesTo(clone);
    		pu.setId(id);
    		pu.setParentFileId(parentFileId);
    		pu.setIndex(index);
    		pu.setSource(source.deepClone());
    		pu.setTarget(target.deepClone());
    		pu.setStructure(isStructure);
    		pu.setLocked(isLocked);
    		pu.setContextId(contextId);
    		pu.setStructureContextId(structureContextId);
    		if (contextList != null) {
    			pu.setContextList(new ArrayList<>(contextList));
    		}
    		if (commentDefinitionIds != null) {
    			pu.setContextList(new ArrayList<>(commentDefinitionIds));
    		}
    	}
    }

    @Override
    public ParagraphUnit deepClone() {
    	ParagraphUnit pu = new ParagraphUnit(parentFile);
    	copyPropertiesTo(pu);
    	return pu;
    }
    
    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        ParagraphUnit that = (ParagraphUnit) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(parentFileId, that.parentFileId)
                .append(isStructure, that.isStructure)
                .append(isLocked, that.isLocked)
                .append(structureContextId, that.structureContextId)
                .append(contextList, that.contextList)
                .append(index, that.index)
                .append(commentDefinitionIds, that.commentDefinitionIds)
                .isEquals();
    }

    @Override
    public boolean deepEquals(Object o) {
        if (!this.equals(o))
            return false;
        ParagraphUnit that = (ParagraphUnit) o;
        Boolean eq;
        eq = (source != null ? source.deepEquals(that.source) : that.source == null);
        eq = eq && (target != null ? target.deepEquals(that.target) : that.target == null);
        return eq;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (parentFileId != null ? parentFileId.hashCode() : 0);
        result = 31 * result + (isStructure ? 1 : 0);
        result = 31 * result + (isLocked ? 1 : 0);
        result = 31 * result + (structureContextId != null ? structureContextId.hashCode() : 0);
        result = 31 * result + (contextList != null ? contextList.hashCode() : 0);
        result = 31 * result + index;
        result = 31 * result + (commentDefinitionIds != null ? commentDefinitionIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id;
    }
}
