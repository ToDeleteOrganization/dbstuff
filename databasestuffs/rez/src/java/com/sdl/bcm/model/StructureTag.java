package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.ISkeletonItemReference;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.StructureTagDefinition;
import com.sdl.bcm.visitor.BCMVisitor;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Structure tags are used by filters to represent constructs in the
 * original file format that are required to rebuild the final document.
 * - Structure tags can only occur in structure paragraph units.
 * - Structure paragraph units/tags are never shown to the user,
 * so should probably not be requested by the editor control.
 */
@JsonIgnoreProperties(value = {"children"})
public class StructureTag extends MarkupData implements ISkeletonItemReference<StructureTagDefinition> {
    public static final String TYPE = "structureTag";

    private int structureTagDefinitionId;

    public StructureTag() {
        this(null);
    }

    public StructureTag(MarkupData parent) {
        setParent(parent);
        setType(TYPE);
    }

    public int getStructureTagDefinitionId() {
        return structureTagDefinitionId;
    }

    public void setStructureTagDefinitionId(int structureTagDefinitionId) {
        this.structureTagDefinitionId = structureTagDefinitionId;
    }

    @Override
    public StructureTag duplicateWithoutChildren() {
        return deepClone();
    }

    @Override
    public StructureTag deepClone() {
    	StructureTag clone = new StructureTag(getParent());
    	copyPropertiesTo(clone);
    	return clone;
    }

    @Override
    protected void copyPropertiesTo(MetaData metaData) {
    	StructureTag structureTag = convertType(metaData);
    	if (metaData != null) {
    		super.copyPropertiesTo(structureTag);
    		structureTag.setStructureTagDefinitionId(this.structureTagDefinitionId);
    	}
    }

    /**
     * {@inheritDoc}
     * <br /> <br />
     * 
     * <i>This will be removed in future versions, from StructureTag</i>.
     * <br />
     * 
     * @return An empty list.
     */
	@Override
	@JsonIgnore
	public List<MarkupData> getChildren() {
		return new LinkedList<MarkupData>();
	}

    @Override
    public boolean accept(BCMVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        StructureTag that = (StructureTag) o;
        return new EqualsBuilder()
                .append(structureTagDefinitionId, that.structureTagDefinitionId)
                .isEquals();
    }

    @Override
	public StructureTagDefinition getSkeletonDefinition() {
    	StructureTagDefinition structTag = null;
    	FileSkeleton fileSkeleton = super.getFileSkeleton();
    	if (fileSkeleton != null) {
    		structTag = new FileReferenceFinder<StructureTagDefinition>(fileSkeleton.getStructureTagDefinitions(), getStructureTagDefinitionId()).find();
    	}
		return structTag;
	}

    @Override
    public boolean deepEquals(Object o) {
        return equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + structureTagDefinitionId;
        return result;
    }

}
