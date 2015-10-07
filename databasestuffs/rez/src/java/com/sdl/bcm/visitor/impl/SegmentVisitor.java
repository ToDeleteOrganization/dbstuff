package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.BCMAbstractVisitor;

public class SegmentVisitor extends BCMAbstractVisitor {
    private boolean parsingSource = false;
    private int lastSourceIndex = 1;
    private int lastTargetIndex = 1;

    @Override
    public boolean visitEnter(ParagraphUnit element) {
        parsingSource = true;
        lastTargetIndex = lastSourceIndex;
        Paragraph paragraph = element.getSource();
        if (paragraph != null) {
            paragraph.accept(this);
        }
        parsingSource = false;
        paragraph = element.getTarget();
        if (paragraph != null) {
            paragraph.accept(this);
        }
        return true;
    }

    @Override
    public boolean visitEnter(Paragraph element) {
        return true;
    }

    @Override
    public boolean visitEnter(Segment segment) {
        if(segment.getSegmentNumber()==null) {
            if (parsingSource) {
                segment.setSegmentNumber("" + (lastSourceIndex++));
            } else {
                segment.setSegmentNumber("" + lastTargetIndex++);
            }
        }
        return true;
    }

    @Override
    public boolean visitEnter(TagPair element) {
        //To change body of implemented methods use File | Settings | File Templates.
        return true;
    }

    @Override
    public boolean visitEnter(PlaceholderTag element) {
        return true;
    }

    @Override
    public boolean visitEnter(TextMarkup element) {
        return true;
    }

    @Override
    public boolean visitEnter(LockedContentContainer element) {
        return true;
    }

}
