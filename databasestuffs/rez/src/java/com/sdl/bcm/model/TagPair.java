package com.sdl.bcm.model;

import com.sdl.bcm.ISkeletonItemReference;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.TagPairDefinition;
import com.sdl.bcm.visitor.BCMVisitor;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * A pair of tags (i.e. start and end) in the bilingual content model, with the content enclosed by the tag pair
 */
public class TagPair extends MarkupDataContainer implements ISkeletonItemReference<TagPairDefinition> {
    public static final String TYPE = "tagPair";

    /**
     * Id that identifies this type of tag.
     * When cloning tags, the tag pair definition id remains the same,
     * but the (instance) id changes (inherited from MarkupData),
     * especially when copying tags from source to target.
     */
    private int tagPairDefinitionId;

    /**
     * Indicates whether the tag is allowed to be hidden during editing operations. Default: false.
     */
    private boolean canHide = false;

    public TagPair() {
        setType(TYPE);
    }

    public TagPair(MarkupData parent) {
        super(parent);
        setType(TYPE);
    }

    public TagPair(TagPair tagPair) {
        super(tagPair.getParent());
        tagPair.copyPropertiesTo(this);
    }

    public int getTagPairDefinitionId() {
        return tagPairDefinitionId;
    }

    public void setTagPairDefinitionId(int tagPairDefinitionId) {
        this.tagPairDefinitionId = tagPairDefinitionId;
    }

    public boolean isCanHide() {
        return canHide;
    }

    public void setCanHide(boolean canHide) {
        this.canHide = canHide;
    }

    @Override
    public TagPair duplicateWithoutChildren() {
    	TagPair clone = new TagPair(getParent());
    	copyPropertiesTo(clone);
        return clone;
    }

    public void copyPropertiesTo(MetaData metaData ) {
    	TagPair tagPair = convertType(metaData);
    	if (tagPair != null) {
    		super.copyPropertiesTo(tagPair);
    		tagPair.setTagPairDefinitionId(this.tagPairDefinitionId);
    		tagPair.setCanHide(this.canHide);
    	}
    }

	@Override
	public TagPair deepClone() {
		TagPair clone = duplicateWithoutChildren();
		super.cloneChildrenInto(clone);
		return clone;
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
	public TagPairDefinition getSkeletonDefinition() {
    	TagPairDefinition tagPairDef = null;
    	FileSkeleton fileSkeleton = super.getFileSkeleton();
    	if (fileSkeleton != null) {
    		tagPairDef = new FileReferenceFinder<TagPairDefinition>(fileSkeleton.getTagPairDefinitions(), getTagPairDefinitionId()).find();
    	}
		return tagPairDef;
	}

	@Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TagPair that = (TagPair) o;
        return new EqualsBuilder()
                .append(tagPairDefinitionId, that.tagPairDefinitionId)
                .append(canHide, that.canHide)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + tagPairDefinitionId;
        result = 31 * result + (canHide ? 1 : 0);
        return result;
    }

}
