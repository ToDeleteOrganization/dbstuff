package com.sdl.bcm;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.Paragraph;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.visitor.impl.GetSegmentsVisitor;
import com.sdl.bcm.visitor.impl.VisitorTraversal;

public class GetSegmentVisitorTest extends AbstractVisitorsTest {

	public static Logger logger = Logger.getLogger(GetSegmentVisitorTest.class);

	@Test
	public void testGetSegmentsFromSegment() {
		GetSegmentsVisitor getSegmentvisitor = new GetSegmentsVisitor();

		Segment segment = createTestingSegment(null);
		segment.accept(getSegmentvisitor);

		List<Segment> segmentsList = getSegmentvisitor.getSegmentsList();
		Assert.isTrue(!segmentsList.isEmpty());
		Assert.isTrue(segmentsList.get(0).equals(segment)); //same instance
	}

	@Test
	public void testGetSegmentsFromParagraph() {
		GetSegmentsVisitor getSegmentvisitor = new GetSegmentsVisitor();

		Paragraph paragraph = createParagraph();
		paragraph.accept(getSegmentvisitor);

		List<Segment> segmentsList = getSegmentvisitor.getSegmentsList();
		Assert.isTrue(!segmentsList.isEmpty());

		MarkupData markupDataSegment = getFirstMarkupDataOfTypeFromSource(Segment.TYPE, paragraph);
		Segment childSegment = segmentsList.get(0);

		Assert.isTrue(markupDataSegment.equals(childSegment));
	}

	@Test
	public void testGetSegmentsFromParagraphUnit() {
		GetSegmentsVisitor gsv = new GetSegmentsVisitor();
		ParagraphUnit paragraphUnit = createParagraphUnit();

		// source paragraph
		paragraphUnit.accept(gsv);
		List<Segment> sourceSegmentsList = gsv.getSegmentsList();
		Assert.isTrue(!sourceSegmentsList.isEmpty());

		MarkupData segmentSource = getFirstMarkupDataOfTypeFromSource(Segment.TYPE, paragraphUnit);
		Segment segmentFromSource = sourceSegmentsList.get(0);
		Assert.isTrue(segmentSource.deepEquals(segmentFromSource));

		// target paragraph
		gsv = new GetSegmentsVisitor(VisitorTraversal.TARGET);
		paragraphUnit.accept(gsv);
		List<Segment> targetSegmentsList = gsv.getSegmentsList();
		Assert.isTrue(!targetSegmentsList.isEmpty());

		MarkupData segmentTarget = getFirstMarkupDataOfTypeFromTarget(Segment.TYPE, paragraphUnit);
		Segment segmentFromTarget = targetSegmentsList.get(0);
		Assert.isTrue(segmentTarget.deepEquals(segmentFromTarget));

		Assert.isTrue(!segmentSource.deepEquals(segmentTarget));
	}

	@Test
	public void testGetSegmentsFromDocument() throws JsonParseException, JsonMappingException, IOException {
		Document doc = getBCMDocumentFromFile("testGetSegmentFromDocument.json");

		// 'source' paragraph
		GetSegmentsVisitor gsv = new GetSegmentsVisitor();
		doc.accept(gsv);
		List<Segment> sourceSegmentsList = gsv.getSegmentsList();
		Assert.isTrue(!sourceSegmentsList.isEmpty());

		MarkupData sourceSegment = getFirstMarkupDataOfTypeFromSource(Segment.TYPE, doc);
		Segment sourceSegmentFromVisitor = sourceSegmentsList.get(0);
		Assert.isTrue(sourceSegment.deepEquals(sourceSegmentFromVisitor));

		// 'target' paragraph
		gsv = new GetSegmentsVisitor(VisitorTraversal.TARGET);
		doc.accept(gsv);
		List<Segment> targetSegmentsList = gsv.getSegmentsList();
		Assert.isTrue(!targetSegmentsList.isEmpty());

		MarkupData targetSegment = getFirstMarkupDataOfTypeFromTarget(Segment.TYPE, doc);
		Segment targetSegmentFromVisitor = targetSegmentsList.get(0);
		Assert.isTrue(targetSegment.deepEquals(targetSegmentFromVisitor));

		Assert.isTrue(!sourceSegment.deepEquals(targetSegment));
		
		
		String serializeBCM = BCMSerializer.serializeBCM(doc);
		System.out.println(serializeBCM);
		
	}

	@Override
	public void log(String message) {
		 logger.info(message);
	}
}
