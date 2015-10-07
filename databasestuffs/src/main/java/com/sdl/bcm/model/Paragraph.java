package com.sdl.bcm.model;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdl.bcm.visitor.BCMVisitor;
import com.sdl.bcm.visitor.impl.GetSegmentsVisitor;

/**
 * Represents the contents of a paragraph unit in one language.
 * A paragraph is a markup data container, but does not implement AbstractMarkupData.
 * It acts as the root container for the source or target content in a paragraph.
 * Contains all the segments from a paragraph along with formatting
 * Equivalent to <seg-source/target .../> tags from xliff
 */
public class Paragraph extends MarkupDataContainer {
    /**
     * Type is used for polymorphic deserialization
     */
    public static final String TYPE = "paragraph";

    public Paragraph() {
        this(null);
    }

    public Paragraph(ParagraphUnit parent) {
        setParagraphUnit(parent);
        setType(TYPE);
    }

    /**
     * Updates a segment in this Paragraph with the new segment provided as parameter
     * The segment to be replaced is identified by the segmentNumber field of the parameter segment
     * If the segment number is not found, the structure is not changed
     * @param segment the new segment to be updated in this paragraph
     */
    public void updateSegment(Segment segment) {
        for (Segment mseg : getSegments()) {
            if (StringUtils.equalsIgnoreCase(mseg.getSegmentNumber(), segment.getSegmentNumber())) {
            	segment.copyPropertiesTo(mseg);
            	mseg.setChildren(segment.getACopyOfChildrenList());
            	break;
            }
        }
    }

    /**
     * Retrieves all the segments in the Source paragraphs of all ParagraphUnits in the file
     * @return a list of all source Segment objects
     */
    // TODO: this is present in the Document class as well, find a common place
    @JsonIgnore
    public List<Segment> getSegments() {
    	GetSegmentsVisitor getSegmentsVisitor = new GetSegmentsVisitor();
        this.accept(getSegmentsVisitor);  //maybe use getChildren instead of a visitor here? it might be more efficient
        return getSegmentsVisitor.getSegmentsList();
    }

    @Override
    public MarkupDataContainer duplicateWithoutChildren() {
        Paragraph clone = new Paragraph(parentParagraphUnit);
        copyPropertiesTo(clone);
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
}