package com.sdl.bcm.visitor;

import com.sdl.bcm.model.*;

public interface BCMVisitor {

    //BCM Composites
    boolean visitEnter(Paragraph element);

    boolean visitLeave(Paragraph element);

    boolean visitEnter(Segment element);

    boolean visitLeave(Segment element);

    boolean visitEnter(TagPair element);

    boolean visitLeave(TagPair element);

    boolean visitEnter(LockedContentContainer element);

    boolean visitLeave(LockedContentContainer element);

    boolean visitEnter(RevisionContainer element);

    boolean visitLeave(RevisionContainer element);

    boolean visitEnter(FeedbackContainer element);

    boolean visitLeave(FeedbackContainer element);

    boolean visitEnter(CommentContainer commentContainer);

    boolean visitLeave(CommentContainer commentContainer);

    boolean visitEnter(TerminologyAnnotationContainer terminologyAnnotationContainer);

    boolean visitLeave(TerminologyAnnotationContainer terminologyAnnotationContainer);

    //Leaves
    boolean visit(ParagraphUnit element);

    boolean visit(PlaceholderTag element);

    boolean visit(TextMarkup element);

    boolean visit(StructureTag element);


    //Markups
    boolean visitEnter(MarkupDataContainer compositeElement);

    boolean visitLeave(MarkupDataContainer compositeElement);

    boolean visitEnter(Document element);

    boolean visitLeave(Document element);

    boolean visitEnter(File element);

    boolean visitLeave(File element);

    boolean visitEnter(ParagraphUnit element);

    boolean visitLeave(ParagraphUnit element);



    //Default
    boolean visitEnter(BCMCompositeElement compositeElement);

    boolean visit(BCMElement element);

    boolean visitLeave(BCMCompositeElement compositeElement);


    boolean visitEnterDefault(BCMCompositeElement compositeElement);
    boolean visitEnterDefault(Document compositeElement);
    boolean visitDefault(BCMElement element);
    boolean visitLeaveDefault(BCMCompositeElement compositeElement);
    boolean visitLeaveDefault(Document document);

}
