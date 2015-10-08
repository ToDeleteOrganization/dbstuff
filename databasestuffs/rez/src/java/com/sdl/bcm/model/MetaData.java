package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class with support for adding String properties.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public abstract class MetaData implements BCMElement, Serializable {

	 public static final String AUTO_CLONED_TAG_PAIR_KEY = "SDL:AutoCloned";

	 // TODO: check why is this used as an implementation and not as an abstraction (HashMap)
	@JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
	protected HashMap<String, String> metadata = new HashMap<>();

    public Map<String, String> getMetadata() {
        return metadata;
    }

	public void setMetadata(Map<String, String> metadata) {
        if (metadata instanceof HashMap) {
            this.metadata = (HashMap)metadata;
        }
    }

    public void addMetaData(String propertyName, String propertyValue) {
        metadata.put(propertyName, propertyValue);
    }

    public String getMetaData(String propertyName) {
        return metadata.get(propertyName);
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        return true;
    }

	/**
	 * Deep clones an element.
	 * 
	 * @return A subclass of <i>MetaData</i> which represents the clone;
	 */
	public abstract MetaData deepClone();

	/**
	 * Populates a clone with all fields specific for a certain class, except
	 * for it's children.<br />
	 * 
	 * The callers is responsible for populating the children.
	 * @param clone
	 */
    // TODO: add check sa vezi daca clone e de aceeasi clasa... this.class = clone.class
    protected void copyPropertiesTo(MetaData clone) {
    	Map<String, String> mapForClone = new HashMap<String, String>(this.metadata);
    	mapForClone.put(AUTO_CLONED_TAG_PAIR_KEY, "true");
    	clone.setMetadata(mapForClone);
    }

    @SuppressWarnings("unchecked")
	protected <T extends MetaData> T convertType(MetaData metaData) {
    	if (metaData == null) {
    		throw new NullPointerException("Please provide a referenced argument, not a null one.");
    	}

    	if (getClass() != metaData.getClass()) {
    		throw new ClassCastException("You need to pass a " + getClass().getName() + " argument, not a " + metaData.getClass().getName());
    	}

    	return (T)metaData;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(o == this)
            return true;
        if(o.getClass() != getClass())
            return false;
        MetaData that = (MetaData) o;
        Map<String, String> thisMetadata = removeAutoCloneParameter(metadata);
        Map<String, String> thatMetadata = removeAutoCloneParameter(that.metadata);
        return new EqualsBuilder()
                .append(thisMetadata, thatMetadata)
                .isEquals();
    }

    private Map<String, String> removeAutoCloneParameter(HashMap<String, String> objeMetadata) {
    	Map<String, String> newMetaData = new HashMap<String, String>(objeMetadata);

    	if (newMetaData.containsKey(AUTO_CLONED_TAG_PAIR_KEY)) {
    		// this metadata prop. should be ignored when comparing
    		newMetaData.remove(AUTO_CLONED_TAG_PAIR_KEY);
    	}
    	return newMetaData;
	}

	public boolean deepEquals(Object o) {
        return this.equals(o);
    }

    @Override
    public int hashCode() {
        return metadata != null ? metadata.hashCode() : 0;
    }

    protected class SkelItemInfo {
    	public SkelItemInfo() {
    		
    	}
    }
}
