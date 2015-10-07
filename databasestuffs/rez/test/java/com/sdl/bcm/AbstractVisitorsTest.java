package com.sdl.bcm;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.MarkupDataContainer;
import com.sdl.bcm.model.Paragraph;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.ParagraphUnit.TargetParagraph;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.utils.Utils;
import com.sdl.bcm.visitor.BCMCompositeElement;

public abstract class AbstractVisitorsTest extends TestBase {

	private static final String TEST_DATA_PATH = "src/test/resources/testData/";

	protected String getVisitorsTestDataFile(String fileName) {
		return getVisitorsTestDataFileFromPath(TEST_DATA_PATH + fileName);
	}

	protected String getVisitorsTestDataFileFromPath(String fileName) {
		return Utils.readFile(fileName);
	}

	protected Document getBCMDocumentFromFile(String fileName)
			throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(getVisitorsTestDataFile(fileName), Document.class);
	}

	protected Document getBCMDocumentFromFilePath(String filePath)
			throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(getVisitorsTestDataFileFromPath(filePath), Document.class);
	}

	protected TextMarkup createTextMarkup(MarkupDataContainer parent, String text) {
		TextMarkup tm = new TextMarkup(parent);
		tm.setId(UUID.randomUUID().toString());
		tm.setText(text);
		return tm;
	}

	protected Segment createTestingSegment(MarkupDataContainer parent) {
		Segment segment = new Segment(parent);
		segment.setCharacterCount(3);
		segment.setId(UUID.randomUUID().toString());
		segment.setSegmentNumber("322");

		TagPair tp1 = new TagPair(segment);
		tp1.setId("tp1");
		tp1.getChildren().add(createTextMarkup(tp1, "Testing "));

		segment.getChildren().add(tp1);

		TagPair tp11 = new TagPair(tp1);
		tp11.setId("tp11");
		tp11.getChildren().add(createTextMarkup(tp11, "GetSegmentVisitor"));
		tp1.getChildren().add(tp11);

		segment.getChildren().add(createTextMarkup(segment, " functionality"));

		return segment;
	}

	protected Paragraph createParagraph() {
		Paragraph paragraph = new Paragraph(null);
		paragraph.getChildren().add(createTestingSegment(paragraph));
		return paragraph;
	}

	protected ParagraphUnit createParagraphUnit() {
		ParagraphUnit paragraphUnit = new ParagraphUnit();
		paragraphUnit.setSource(createParagraph());

		Paragraph target = createParagraph();
		Segment targetSegment = createTestingSegment(target);
		targetSegment.addChild(new TextMarkup(targetSegment, "New Target Text"));
		target.getChildren().clear();
		target.getChildren().add(targetSegment);
		paragraphUnit.setTarget(target);

		return paragraphUnit;
	}

	@SuppressWarnings("rawtypes")
	protected MarkupData getFirstMarkupDataOfTypeFromSource(String type, BCMCompositeElement bcmComposite) {
		if (bcmComposite instanceof ParagraphUnit) {
			ParagraphUnit pu = (ParagraphUnit)bcmComposite;
			bcmComposite = pu.getSource();
		}
		return getFirstMarkupDataOfType(type, bcmComposite, TargetParagraph.SOURCE_PARAGRAPH);
	}

	@SuppressWarnings("rawtypes")
	protected MarkupData getFirstMarkupDataOfTypeFromTarget(String type, BCMCompositeElement bcmComposite) {
		if (bcmComposite instanceof ParagraphUnit) {
			ParagraphUnit pu = (ParagraphUnit)bcmComposite;
			bcmComposite = pu.getTarget();
		}
		return getFirstMarkupDataOfType(type, bcmComposite, TargetParagraph.TARGET_PARAGRAPH);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MarkupData getFirstMarkupDataOfType(String type, BCMCompositeElement bcmComposite, TargetParagraph paragraphType) {
		List<BCMCompositeElement> children = bcmComposite.getChildren();

		for (BCMCompositeElement bce : children) {
			if (bce instanceof ParagraphUnit) {
				ParagraphUnit pu = (ParagraphUnit)bce;
				return getFirstMarkupDataOfType(type, finParagraphToVisit(pu, paragraphType), paragraphType);

			} else if (!(bce instanceof MarkupData)) {
				return getFirstMarkupDataOfType(type, bce, paragraphType);
			}

			MarkupData md = (MarkupData) bce;
			if (!md.getType().equals(type)) {
				return getFirstMarkupDataOfType(type, md, paragraphType);
			} else {
				return md;
			}
		}

		return null;
	}

	private Paragraph finParagraphToVisit(ParagraphUnit pu, TargetParagraph paragraphType) {
		Paragraph paragraphToVisit = pu.getSource();
		if (ParagraphUnit.TargetParagraph.TARGET_PARAGRAPH.equals(paragraphType)) {
			paragraphToVisit = pu.getTarget();
		}
		
		return paragraphToVisit;
	}
}
