package com.sdl.bcm.model;

import com.sdl.bcm.visitor.BCMVisitor;

/**
 * A container that contains content that should not be modified during editing.
 */
public class LockedContentContainer extends MarkupDataContainer {
    public static final String TYPE = "locked";

    public LockedContentContainer() {
        setType(TYPE);
    }

    public LockedContentContainer(MarkupData parent) {
        super(parent);
        setType(TYPE);
    }

    @Override
    public LockedContentContainer duplicateWithoutChildren() {
        LockedContentContainer clone = new LockedContentContainer(getParent());
        copyPropertiesTo(clone);
        return clone;
    }

    @Override
    public LockedContentContainer deepClone() {
    	LockedContentContainer locked = duplicateWithoutChildren();
    	cloneChildrenInto(locked);
    	return locked;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (MarkupData element : getChildren()) {
                if (!element.accept(visitor)) {
                    break;
                }
            }
        }
        return visitor.visitLeave(this);
    }
}
