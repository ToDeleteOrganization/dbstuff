package com.sdl.bcm.model.fileskeleton;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.sdl.bcm.model.MetaData;

public abstract class AbstractSkeletonItem extends MetaData {

    /**
     * Unique identifier for any definition.
     */
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	AbstractSkeletonItem abs = convertType(clone);
    	if (abs != null) {
    		super.copyPropertiesTo(clone);
    		abs.setId(id);
    	}
    }
    
    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        AbstractSkeletonItem that = (AbstractSkeletonItem) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
