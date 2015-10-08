package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.visitor.BCMAbstractVisitor;

import java.util.LinkedList;
import java.util.List;

public class GetTextMarkupVisitor extends BCMAbstractVisitor {

    /**
	 * This list will be populated with all the text markup's as it traverses a tree structure.
	 */
	private final List<TextMarkup> textMarkupList = new LinkedList<>();

    public GetTextMarkupVisitor() {
    	this(VisitorTraversal.SOURCE);
    }

    public GetTextMarkupVisitor(VisitorTraversal visitorTraversal) {
    	super(visitorTraversal);
    }

	@Override
    public boolean visitEnter(ParagraphUnit element) {
		return visitEnterParagraphUnit(element);
    }

    @Override
    public boolean visit(TextMarkup element) {
        textMarkupList.add(element);
        return true;
    }

    public List<TextMarkup> getTextMarkupList() {
        return textMarkupList;
    }
}
