package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.BCMAbstractVisitor;
import java.util.LinkedList;
import java.util.List;

/**
 * Visitor that builds a list of Segments present in the Source of given ParagraphUnits
 */
public class GetSegmentsVisitor extends BCMAbstractVisitor {

    /**
	 * This list will be populated with all the segments as it traverses a tree structure.
	 */
	private List<Segment> segmentsList = new LinkedList<>();

    public GetSegmentsVisitor() {
    	this(VisitorTraversal.SOURCE);
    }

    public GetSegmentsVisitor(VisitorTraversal visitorTraversal) {
    	super(visitorTraversal);
    }

    @Override
    public boolean visitEnter(ParagraphUnit paragraphUnit) {
    	return visitEnterParagraphUnit(paragraphUnit);
    }

	@Override
    public boolean visitEnter(Segment element) {
        segmentsList.add(element);
        //TODO: should this stop after a segment
        // can there be a segment within a segment?
        return false;
    }

    public List<Segment> getSegmentsList() {
        return segmentsList;
    }

}
