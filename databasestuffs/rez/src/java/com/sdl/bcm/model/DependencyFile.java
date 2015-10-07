package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * An additional file, related to the document that is required for various types of processing.
 */
@JsonIgnoreProperties(value = { "metadata" })
public class DependencyFile extends MetaData {

    /**
     * A unique ID to identify the dependency file.
     * This id is unique only across current Document.
     */
    private String id;

    /**
     * Describes what the file is used for.
     */
    private DependencyFileUsage usage;

    /**
     * The URI from where the dependency file's content can be retrieved.
     */
    private String location;

    /**
     * The file name of the dependency file that can be used to store the content on disk.
     */
    private String fileName;

    public DependencyFile() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DependencyFileUsage getUsage() {
        return usage;
    }

    public void setUsage(DependencyFileUsage usage) {
        this.usage = usage;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        return false;
    }

    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	DependencyFile depFile = super.convertType(clone);
    	if (depFile != null) {
    		super.copyPropertiesTo(clone);
    		depFile.setId(id);
    		depFile.setFileName(fileName);
    		depFile.setLocation(location);
    		depFile.setUsage(usage);
    	}
    }

    public DependencyFile deepClone() {
    	DependencyFile depFile = new DependencyFile();
    	copyPropertiesTo(depFile);
    	return depFile;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        DependencyFile that = (DependencyFile) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(usage, that.usage)
                .append(location, that.location)
                .append(fileName, that.fileName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (usage != null ? usage.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }
}
