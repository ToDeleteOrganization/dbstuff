package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdl.bcm.manager.Utils;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.BCMVisitor;

import java.util.*;

public abstract class MarkupDataContainer extends MarkupData {

    /**
     * Current container list of MarkupData children
     */
	@JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    private List<MarkupData> children = new LinkedList<>();

    public MarkupDataContainer() {
    }

    public MarkupDataContainer(MarkupData parent) {
        this.parent = parent;
    }

    @Override
    public List<MarkupData> getChildren() {
        return children;
    }

    @JsonIgnore
    public List<MarkupData> getACopyOfChildrenList() {
        return new LinkedList<>(getChildren());
    }

    public void setChildren(List<MarkupData> children) {
        this.children = children;
    }

    public void addChild(MarkupData child) {
        if(children == null) {
            setChildren(new LinkedList<MarkupData>());
        }
        children.add(child);
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (BCMElement elementChild : getACopyOfChildrenList()) {
                if (!elementChild.accept(visitor)) {
                    break;
                }
            }
        }
        return visitor.visitLeave(this);
    }

    protected void copyPropertiesTo(MetaData clone) {
    	// children won't be copied here
    	super.copyPropertiesTo(clone);
    }

    protected void cloneChildrenInto(MetaData clone) {
    	MarkupDataContainer markupDataContainer = convertType(clone);
    	if (markupDataContainer != null) {
    		Utils.deepCloneList(children);
    	}
    }

	@Override
    public boolean deepEquals(Object o) {
        if(! equals(o))
            return false;
        MarkupDataContainer that = (MarkupDataContainer) o;
        return Utils.deepEqualsCollection(this.getChildren(), that.getChildren());
    }

}
