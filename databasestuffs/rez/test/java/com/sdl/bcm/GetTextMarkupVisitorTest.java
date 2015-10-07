package com.sdl.bcm;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.util.Assert;

import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.Paragraph;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.visitor.impl.GetTextMarkupVisitor;
import com.sdl.bcm.visitor.impl.VisitorTraversal;

public class GetTextMarkupVisitorTest extends AbstractVisitorsTest  {

	public static Logger logger = Logger.getLogger(GetTextMarkupVisitorTest.class);

	@Test
	public void testGetTextMarkupFromSegment() {
		GetTextMarkupVisitor getTextMarkupVisitor = new GetTextMarkupVisitor();

		Segment segment = createTestingSegment(null);
		segment.accept(getTextMarkupVisitor);

		MarkupData tm = getFirstMarkupDataOfTypeFromSource(TextMarkup.TYPE, segment);
		List<TextMarkup> textMarkupList = getTextMarkupVisitor.getTextMarkupList();
		Assert.isTrue(!textMarkupList.isEmpty());
		Assert.isTrue(textMarkupList.get(0).equals(tm)); //same instance
	}

	@Test
	public void testGetTextMarkupFromParagraph() {
		GetTextMarkupVisitor getTextMarkupVisitor = new GetTextMarkupVisitor();
		
		Paragraph paragraph = createParagraph();
		paragraph.accept(getTextMarkupVisitor);

		MarkupData tm = getFirstMarkupDataOfTypeFromSource(TextMarkup.TYPE, paragraph);
		List<TextMarkup> textMarkupList = getTextMarkupVisitor.getTextMarkupList();
		Assert.isTrue(!textMarkupList.isEmpty());

		Assert.isTrue(textMarkupList.get(0).equals(tm)); //same instance
	}

	@Test
	public void testGetSegmentsFromParagraphUnit() {
		GetTextMarkupVisitor gsv = new GetTextMarkupVisitor();
		ParagraphUnit paragraphUnit = createParagraphUnit();

		// source paragraph
		paragraphUnit.accept(gsv);
		List<TextMarkup> sourceTextMarkupsList = gsv.getTextMarkupList();
		Assert.isTrue(!sourceTextMarkupsList.isEmpty());

		MarkupData textMarkupFromSource = getFirstMarkupDataOfTypeFromSource(TextMarkup.TYPE, paragraphUnit);
		TextMarkup textFromListSource = sourceTextMarkupsList.get(0);
		Assert.isTrue(textMarkupFromSource.equals(textFromListSource));

		// target paragraph
		gsv = new GetTextMarkupVisitor(VisitorTraversal.TARGET);
		paragraphUnit.accept(gsv);
		List<TextMarkup> targetTextMarkupList = gsv.getTextMarkupList();
		Assert.isTrue(!targetTextMarkupList.isEmpty());

		MarkupData textMarkupTarget = getFirstMarkupDataOfTypeFromTarget(TextMarkup.TYPE, paragraphUnit);
		TextMarkup textFromFromTarget = targetTextMarkupList.get(0);
		Assert.isTrue(textMarkupTarget.deepEquals(textFromFromTarget));

		Assert.isTrue(!textMarkupFromSource.deepEquals(textMarkupTarget));
	}

	@Test
	public void testGetSegmentsFromDocument() {
        try {
        	Document doc = getBCMDocumentFromFile("testGetSegmentFromDocument.json");

        	// 'source' paragraph
        	GetTextMarkupVisitor gtmv = new GetTextMarkupVisitor();
        	doc.accept(gtmv);
        	List<TextMarkup> sourceSegmentsList = gtmv.getTextMarkupList();
        	Assert.isTrue(!sourceSegmentsList.isEmpty());

    		MarkupData sourceSegment = getFirstMarkupDataOfTypeFromSource(TextMarkup.TYPE, doc);
    		TextMarkup sourceSegmentFromVisitor = sourceSegmentsList.get(0);
        	Assert.isTrue(sourceSegment.deepEquals(sourceSegmentFromVisitor));

        	// 'target' paragraph
        	gtmv = new GetTextMarkupVisitor(VisitorTraversal.TARGET);
        	doc.accept(gtmv);
        	List<TextMarkup> targetTMList = gtmv.getTextMarkupList();
        	Assert.isTrue(!targetTMList.isEmpty());

        	MarkupData targetTM = getFirstMarkupDataOfTypeFromTarget(TextMarkup.TYPE, doc);
        	TextMarkup targetTMVisitor = targetTMList.get(0);
        	Assert.isTrue(targetTM.equals(targetTMVisitor));

        	Assert.isTrue(!sourceSegment.deepEquals(targetTM));

        } catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void log(String message) {
		logger.info(message);
	}

}
