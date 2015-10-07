package com.sdl.bcm.terminology;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sdl.bcm.AbstractVisitorsTest;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.File;
import com.sdl.bcm.model.Paragraph;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;

import junit.framework.Assert;

public class CustomTest extends AbstractVisitorsTest {

	@Test
	@Ignore
	public void testDocument() {

		Document doc = new Document();
		doc.setId("documentId");
		doc.setModelVersion("modelVersion");
		doc.setName("documentName");
		doc.setSourceLanguageCode("slang");
		doc.setTargetLanguageCode("tlang");

		// test getFile
		File f1 = new File();
		f1.setId("file1Id");
		File f2 = new File();
		f2.setId("file2Id");
		doc.addFile(f1);
		doc.addFile(f2);

		Assert.assertTrue(f1.equals(doc.getFile("file1Id")));
		Assert.assertTrue(f2.equals(doc.getFile("file2Id")));
		Assert.assertFalse(f1.equals(doc.getFile("invalidID")));

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
		p1s.addChild(ss1);
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

		List<Segment> segments = doc.getSegments();
		Segment seg = null;
		seg = segments.get(0);
		seg = segments.get(1);
		seg = segments.get(2);
		seg = segments.get(3);
		Assert.assertTrue(segments.get(0).equals(ss1));
		Assert.assertTrue(segments.get(1).equals(ss2));
		Assert.assertTrue(segments.get(2).equals(ss3));
		Assert.assertTrue(segments.get(3).equals(ss32));
		Assert.assertFalse(segments.contains(st2));

	}

	@Test
	public void testSmth() throws JsonParseException, JsonMappingException, IOException {
		Document doc = getBCMDocumentFromFile("testGetSegmentFromDocument.json");
		
		TagPair tagPair = (TagPair)doc.getChildren().get(0).getChildren().get(0).getSource().getChildren().get(0).getChildren().get(0);
		System.out.println(tagPair);
		
		
		TagPair tp = tagPair.deepClone();
		System.out.println(tp);
		
		System.out.println(tp.deepEquals(tagPair));
		System.out.println(tp == tagPair);
		System.out.println(tp.getParent() == tagPair.getParent());


//		System.out.println(tp == tagPair);
//		System.out.println(tp.getParent() == tagPair.getParent());
		
	}

	@Override
	public void log(String message) {
		// TODO Auto-generated method stub

	}
}
