package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.BCMAbstractVisitor;

public class ExtractPlainTextVisitor extends BCMAbstractVisitor {
    private StringBuilder sb = new StringBuilder();

    @Override
    public boolean visit(TextMarkup element) {
        sb.append(element.getText());
        return true;
    }

    @Override
    public boolean visitEnter(ParagraphUnit element) {
    	element.getSource().accept(this);
    	// extract text only from source
    	return false;
    }

    @Override
    public boolean visitEnter(LockedContentContainer element) {
		// text from within a 'LockedContentContainer' should not be taken in
		// consideration
        return false;
    }

    @Override
    public boolean visitEnter(RevisionContainer element) {
    	RevisionType revisionType = element.getRevisionType();
        return RevisionType.INSERTED.equals(revisionType) || RevisionType.UNCHANGED.equals(revisionType);
    }

	@Override
	public boolean visitEnter(FeedbackContainer element) {
		FeedbackType feedbackType = element.getFeedbackType();
		return FeedbackType.ADDED.equals(feedbackType) || FeedbackType.COMMENT.equals(feedbackType);
	}

    public String getText() {
        return sb.toString();
    }
}
