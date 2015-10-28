package com.test.testing;

import com.sdl.bcm.model.MarkupDataContainer;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.FinderUtils;

public class ChangeTagPair extends Test {

	public void test() throws VisitorException {
		MarkupDataContainer mdc = getSegment();
		showJsonFormat(mdc);

		TagPair italic = new TagPair();
		italic.addMetaData("italic", "italic");
		italic.setTagPairDefinitionId(24); // italic definition
		mdc.insertContainer(new int[] { 5 }, new int[] { 17 }, italic); // insert the italic tag

		showJsonFormat(mdc);
		
		// change existing text
		TextMarkup testingTM = mdc.getTextMarkupAtOffset(10);
		testingTM.setText("TESTING");

		showJsonFormat(mdc);
		
		int offsetToInsertAt = 9;
		mdc.splitTextMarkupAtOffset(offsetToInsertAt);
		TextMarkup tmAtOffset = mdc.getTextMarkupAtOffset(offsetToInsertAt);
		tmAtOffset.getParent().addChildBeforeElement(tmAtOffset, new TextMarkup(" wonderfull"));
		
		showJsonFormat(mdc);
		
		Segment targetSegment = new Segment();
		targetSegment.addChild(new TextMarkup(FinderUtils.getTextFromElement(mdc)));
	}

	private MarkupDataContainer getSegment() {
		MarkupDataContainer seg = new Segment();

		seg.addChild(new TextMarkup("This is a "));

		TagPair bold = new TagPair();
		bold.setId("bold");
		bold.addMetaData("bold", "bold");
		bold.setTagPairDefinitionId(24); // bold definition
		bold.addChild(new TextMarkup("testing test"));

		seg.addChild(bold);
		seg.addChild(new TextMarkup("."));

		return seg;
	}

}
