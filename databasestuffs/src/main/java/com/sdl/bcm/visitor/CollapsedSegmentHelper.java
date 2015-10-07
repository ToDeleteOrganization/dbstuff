package com.sdl.bcm.visitor;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.helper.OrderedList;
import com.sdl.bcm.visitor.helper.OrderedListInterface;

import java.util.*;

/**
 * @author vnastase
 * @since 2.5
 * functionality offered for the CollapsedSegment class
 */
final class CollapsedSegmentHelper {
    private CollapsedSegmentHelper() {
    }

    /**
     * finds and returns the index of a given MarkupData element in a provided list; returns -1 if not found
     * currently this only compares ids. (they should be identical when cloning containers.)
     * used for expanding a CollapsedSegment
     */
    static int getIndexInChildList(List<MarkupData> list, MarkupData element) {
        int idx = -1;
        for (int i = 0; i < list.size(); i++)
            if( (list.get(i).getId() != null) // some MarkupData doesn't use ids (textMarkups for ex.)
                && (list.get(i).getId().equals(element.getId())) )
                    idx = i;
        return idx;
    }

    /**
     * filter for MarkupData that cannot have children in segment tree
     */
    static boolean isLeaf(MarkupData obj) {
        return obj instanceof TextMarkup ||
                obj instanceof PlaceholderTag;
    }

    /**
     * checks if the MarkupData object are free of terms in their text (they cannot contain terms as per BCM model specification)
     * currently, it checks if it is a LockedContentContainer, or a deletion RevisionContainer, which are not searched for terms
     */
    static boolean isWithoutTerms(MarkupData child) {
        if(child instanceof LockedContentContainer)
            return true;
        if( (child instanceof RevisionContainer)
            && (((RevisionContainer) child).getRevisionType().equals(RevisionType.DELETED)) )
                return true;
        return false;
    }

    /**
     * creates and adds a new Fragment to the collapsed segment list
     * @param child the data of the fragment; can be TextMarkup or PlaceholderTag
     * @param startIdx the absolute index in the whole segment text where this fragment starts
     * @param termless flag that shows if this fragment can contain terms or not (term-less containers: locked, deleted)
     * @param containerList the ancestors of the child parameter in descending order (the last one is the parent)
     * @param collapsedSegment the list of Fragments that the new Fragment will be added to
     */
    @SuppressWarnings("unchecked")
    static void addFragment(MarkupData child, int startIdx, boolean termless,
                                         OrderedListInterface<MarkupData> containerList, OrderedListInterface<TextFragment> collapsedSegment) {
        int endIdx = startIdx;
        OrderedList<MarkupData> temp = (OrderedList<MarkupData>)containerList;
        OrderedList<MarkupData> containers = (OrderedList<MarkupData>) temp.clone();

        MarkupData markupClone = child.duplicateWithoutChildren();

        if(child instanceof PlaceholderTag) {
            markupClone.setId(child.getId());

        } else if (child instanceof TextMarkup) {
            String text = ((TextMarkup) child).getText();
            if(!termless)
                endIdx = startIdx + text.length(); // fragment not locked/deleted, so end index calculated normally
        }
        TextFragment textFragment = new TextFragment(startIdx,endIdx,containers,markupClone);
        collapsedSegment.add(textFragment);
    }

    /**
     * expands a branch of the segment object from the collapsed tree representation
     * essentially, it takes a TextFragment's address in the tree (the list of containers) and it either
     *      finds the parent of the leaf (textFragment.data) if it already exists
     *      or creates the parents of the leaf in order by copying the last containers in the address and adding them to the Segment tree
     */
    static MarkupData expandTree(List<MarkupData> markupContainers, MarkupData parent) {
        MarkupData currentParent = parent;
        for(MarkupData container : markupContainers) {

            int indexOfContainer = CollapsedSegmentHelper.getIndexInChildList(currentParent.getChildren(), container); //index in object's children list
            if(indexOfContainer != -1) {        // container already exists as child of currentParent at the indexOfContainer position
                currentParent = currentParent.getChildren().get(indexOfContainer);

            } else {                            //container has to be added to segment structure
                MarkupData containerClone = container.duplicateWithoutChildren();
                containerClone.setId(container.getId());
                currentParent.getChildren().add(containerClone);
                containerClone.setParent(currentParent);
                currentParent = containerClone; //go one level deeper
            }
        }
        return currentParent;
    }

}