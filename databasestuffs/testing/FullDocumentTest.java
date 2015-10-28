package com.test.testing;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.File;
import com.sdl.bcm.model.Paragraph;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.SegmentationHint;
import com.sdl.bcm.model.fileskeleton.TagPairDefinition;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.FinderUtils;

public class FullDocumentTest extends Test {

	public  void test() throws VisitorException {
		Document doc = createDocument(); // creates/reads the bmc document
		showJsonFormat(doc, "Before");
		
		ParagraphUnit paragraphUnit = getFirstSourceParagraph(doc); // get first paragraph unit from first file
		Paragraph source = paragraphUnit.getSource(); // get the source segment
		
		List<Segment> segmentsFromSource = FinderUtils.getSegmentsFrom(source); // get all segments from source
		
		List<Segment> translatedSegments = translateSegments(segmentsFromSource); // translate all source segments
		
		FileSkeleton skeleton = source.getParentFile().getSkeleton(); // get the associated file skeleton
		// add a test container for all translated segments
		for (Segment seg : translatedSegments) {
			TagPairDefinition tpDef = getTagPairDefinitionData(); // get new tag pair definition
			seg.insertTagPairWithDefinition(new int[] {5}, new int[] {14}, tpDef, skeleton); // insert the new container with the definition

			TagPair tpFirstChild = new TagPair();
			tpFirstChild.setTagPairDefinitionId(tpDef.getId());
			tpFirstChild.addChild(new TextMarkup("New target child."));
			seg.addChildAtIndex(0, tpFirstChild);
		}
		
		Paragraph targetParagraph = new Paragraph(); // create a new paragraph
		for (Segment targetSegment : translatedSegments) {
			targetParagraph.addChild(targetSegment); // add the translated segment 
		}
		
		paragraphUnit.setTarget(targetParagraph); // add the new paragraph as the target paragraph
		
		showJsonFormat(doc, "After");
	}

	private  ParagraphUnit getFirstSourceParagraph(Document doc) {
		File firstFile = doc.getFiles().get(0); // get the file after a given index
		ParagraphUnit paragraphUnit = firstFile.getParagraphUnits().get(0); // gets the paragraph unit after a given index.
		
		return paragraphUnit;
	}

	private  List<Segment> translateSegments(List<Segment> segmentsFromSource) {
		List<Segment> translated = new ArrayList<Segment>();
		
		for (Segment s : segmentsFromSource) {
			Segment newSegment = s.cloneWithoutChildren(); // clone, the children will be added later.
			newSegment.setParent(null);
			String segmentText = FinderUtils.getTextFromElement(s); // get all text from segment
			newSegment.addChild(new TextMarkup("translated[" + segmentText + "]translated")); // add a new text markup child with the translated text.

			translated.add(newSegment);
		}
		
		return translated;
	}

	private  Document createDocument() {
		Document doc = new Document();
		doc.setId("documentId");
		doc.setModelVersion("modelVersion");
		doc.setName("documentName");
		doc.setSourceLanguageCode("slang");
		doc.setTargetLanguageCode("tlang");

		// test getFile
		File f1 = new File();
		f1.setSkeleton(new FileSkeleton());
		f1.setId("file1Id");
		File f2 = new File();
		f2.setId("file2Id");
		doc.addFile(f1);
		doc.addFile(f2);

		// test getSegments
		ParagraphUnit pu1 = new ParagraphUnit();
		pu1.setId("parUnit1Id");
		ParagraphUnit pu2 = new ParagraphUnit();
		pu2.setId("parUnit2Id");
		f1.addParagraphUnit(pu1);
		f1.addParagraphUnit(pu2);
		ParagraphUnit pu3 = new ParagraphUnit();
		pu3.setId("parUnit3Id");
		f2.addParagraphUnit(pu3);

		Paragraph p1s = new Paragraph();
		p1s.setId("p1sID");

		Segment ss1 = new Segment(p1s, "ss1ID");
		ss1.addChild(new TextMarkup("This is a test text"));

		Segment ss12 = new Segment(p1s, "ss12ID");
		ss12.addChild(new TextMarkup("Another testing text."));
		
		p1s.addChild(ss1);
		p1s.addChild(ss12);
		Paragraph p2s = new Paragraph();

		p2s.setId("p2sID");
		Segment ss2 = new Segment(p2s, "ss2ID");
		p2s.addChild(ss2);
		Paragraph p2t = new Paragraph();
		p2t.setId("p2tID");
		Segment st2 = new Segment(p2t, "st2ID");
		p2t.addChild(st2);

		Paragraph p3s = new Paragraph();
		p3s.setId("p3sID");
		Segment ss3 = new Segment(p3s, "ss3ID");
		p3s.addChild(ss3);
		Segment ss32 = new Segment(p3s, "ss32ID");
		p3s.addChild(ss32);

		pu1.setSource(p1s);
		pu2.setSource(p2s);
		pu2.setTarget(p2t);
		pu3.setSource(p3s);

		return doc;
	}

	private  TagPairDefinition getTagPairDefinitionData() {
		TagPairDefinition tpd = new TagPairDefinition();
		tpd.setCanHide(true);
		tpd.setStartTagContent("<end>");
		tpd.setEndTagContent("</end>");
		tpd.setFormattingGroupId(3);
		tpd.setId(25);
		tpd.setSegmentationHint(SegmentationHint.EXCLUDE);
		tpd.setStartTagDisplayText("dispText");
		tpd.setEndTagDisplayText("endDispTag");
		return tpd;
	}
	
}
