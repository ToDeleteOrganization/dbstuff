package com.sdl.bcm.visitor;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.impl.VisitorTraversal;

public abstract class BCMAbstractVisitor implements BCMVisitor {

	protected final VisitorTraversal visitorTraversal;

	protected BCMAbstractVisitor() {
		this(VisitorTraversal.SOURCE);
	}

	protected BCMAbstractVisitor(VisitorTraversal visitorTraversal) {
		this.visitorTraversal = visitorTraversal;
	}

	@Override
	public boolean visitEnter(Paragraph element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(Paragraph element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(Segment element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(Segment element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(TagPair element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(TagPair element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visit(PlaceholderTag element) {
		return visitDefault(element);
	}

	@Override
	public boolean visit(TextMarkup element) {
		return visitDefault(element);
	}

	@Override
	public boolean visit(StructureTag element) {
		return visitDefault(element);
	}

	@Override
	public boolean visitEnter(MarkupDataContainer compositeElement) {
		return visitEnterDefault(compositeElement);
	}

	@Override
	public boolean visit(ParagraphUnit element) {
		return visitDefault(element);
	}

	@Override
	public boolean visitLeave(MarkupDataContainer compositeElement) {
		return visitLeaveDefault(compositeElement);
	}

	@Override
	public boolean visitEnter(Document element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(Document element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(File element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(File element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(LockedContentContainer element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(LockedContentContainer element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(RevisionContainer element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(RevisionContainer element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(FeedbackContainer element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(FeedbackContainer element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(CommentContainer element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(CommentContainer element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(TerminologyAnnotationContainer element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(TerminologyAnnotationContainer element) {
		return visitLeaveDefault(element);
	}

	public boolean visitEnter(PlaceholderTag element) {
		return visitEnterDefault(element);
	}

	public boolean visitLeave(PlaceholderTag element) {
		return visitLeaveDefault(element);
	}

	public boolean visitEnter(TextMarkup element) {
		return visitEnterDefault(element);
	}

	public boolean visitLeave(TextMarkup element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(ParagraphUnit element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visitLeave(ParagraphUnit element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnter(BCMCompositeElement element) {
		return visitEnterDefault(element);
	}

	@Override
	public boolean visit(BCMElement element) {
		return visitDefault(element);
	}

	@Override
	public boolean visitLeave(BCMCompositeElement element) {
		return visitLeaveDefault(element);
	}

	@Override
	public boolean visitEnterDefault(BCMCompositeElement compositeElement) {
		return true;
	}

	@Override
	public boolean visitEnterDefault(Document compositeElement) {
		return true;
	}

	@Override
	public boolean visitDefault(BCMElement element) {
		return true;
	}

	@Override
	public boolean visitLeaveDefault(BCMCompositeElement compositeElement) {
		return true;
	}

	@Override
	public boolean visitLeaveDefault(Document document) {
		return true;
	}

	protected boolean visitEnterParagraphUnit(ParagraphUnit paragraphUnit) {
		if (VisitorTraversal.ALL.equals(visitorTraversal)) {
			return true;
		}

		Paragraph paragraphToVisit = paragraphUnit.getSource();
		if (VisitorTraversal.TARGET.equals(visitorTraversal)) {
			paragraphToVisit = paragraphUnit.getTarget();
		}

		if (paragraphToVisit != null) {
			paragraphToVisit.accept(this);
		}

		return false;
	}
}
