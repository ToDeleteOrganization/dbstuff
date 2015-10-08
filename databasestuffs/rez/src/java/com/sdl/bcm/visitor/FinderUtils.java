package com.sdl.bcm.visitor;

import java.util.List;

import com.sdl.bcm.model.Segment;
import com.sdl.bcm.visitor.impl.GetSegmentsVisitor;

public final class FinderUtils {

	/**
	 * Retrieves all the segments in the Source paragraphs of all ParagraphUnits in the file.
	 * 
	 * @param bcmElement
     * @return a list of all source Segment objects
	 */
	public static List<Segment> getSegmentsFrom(BCMElement bcmElement) {
		 GetSegmentsVisitor getSegmentsVisitor = new GetSegmentsVisitor();
		 bcmElement.accept(getSegmentsVisitor);
	     return getSegmentsVisitor.getSegmentsList();
	}

	private FinderUtils() {
	}
}
