package com.sdl.bcm.visitor;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.helper.OrderedList;

import java.util.*;

/**
 * @author vnastase
 * @since 2.5
 * functionality offered for TerminologyProcessor
 */
final class TerminologyProcessorHelper {

    private TerminologyProcessorHelper() {
    }

    /**
     * constructs a TextFragment with a TextMarkup
     *
     * @param originalFragment the original/previous Fragment whose containers will be copied to the new one
     * @param termText         the text of the new TextMarkup part of the fragment
     * @param absStart         the absolute start index of the fragment (in the whole segment text)
     * @param absEnd           the absolute end index of the fragment (in the whole segment text)
     * @return the newly created fragment
     */
    @SuppressWarnings("unchecked")
static TextFragment addTextFragment(TerminologyAnnotationContainer terminologyContainer, int positionInContainers,
                                TextFragment originalFragment, String termText, int absStart, int absEnd) {

        OrderedList<MarkupData> temp = (OrderedList<MarkupData>) originalFragment.getContainers(); //remember location in containers
        OrderedList<MarkupData> termContainers = (OrderedList<MarkupData>) temp.clone();
        termContainers.add(positionInContainers, terminologyContainer); //this containers list will be added (as a clone) to all terminology fragments
        TextMarkup textMarkup = new TextMarkup();
        textMarkup.setText(termText);
        return new TextFragment(absStart, absEnd, termContainers, textMarkup);
    }

    /**
     * returns the collapsedSegment index of the fragment containing the given plain text start index
     */
    static int findFragmentIndexStart(CollapsedSegment segment, int absoluteTermStartIndex) {
        for (int idx = 0; idx < segment.size(); idx++) {
            int start = segment.get(idx).getStartIndex();
            int end = segment.get(idx).getEndIndex();
            if (start <= absoluteTermStartIndex && absoluteTermStartIndex < end) {
                return idx;
            }
        }
        return -1;
    }

    /**
     * returns the collapsedSegment index of the fragment containing the given plain text end index
     */
    static int findFragmentIndexEnd(CollapsedSegment segment, int absoluteTermEndIndex, int fragmentStartIndex) {
        for (int idx = fragmentStartIndex; idx < segment.size(); idx++) {
            int start = segment.get(idx).getStartIndex();
            int end = segment.get(idx).getEndIndex();
            if (start < absoluteTermEndIndex && absoluteTermEndIndex <= end) {
                return idx;
            }
        }
        return -1;
    }

    /**
     * find the position in Fragment containers list where the TerminologyContainer should be inserted
     * this is just under the parent container which contains the whole term
     *      - so the last/deepest common container in the Fragments' location list
     */
    static int findContainerPosition(CollapsedSegment segment, int fragmentStartIndex, int fragmentEndIndex) {
        int min = segment.get(fragmentStartIndex).getContainers().size(); //find the shortest branch in term
        for (int i = fragmentStartIndex; i <= fragmentEndIndex; i++) {
            if (segment.get(i).getContainers().size() < min)
                min = segment.get(i).getContainers().size();
        }
        if (min == 0)
            return min; //slight optimisation: the last common container is the Segment itself
        String id;
        int position; // position in the Fragment's container list; it represents the level of the Segment's tree
        for (position = 0; position < min; position++) { // look for the last common container
            id = segment.get(fragmentStartIndex).getContainers().get(position).getId(); //save first Fragment id
            // test if all containers are the same at this level, or if the term spans multiple containers at position level
            for (int i = fragmentStartIndex + 1; i <= fragmentEndIndex; i++) {
                if (!id.equals(segment.get(i).getContainers().get(position).getId())) // compare id of first Fragment's container with all other ids
                    return position > 0 ? position - 1 : 0;
            }
        }
        return position;
    }

    /**
     * checks if a TextFragment has a LockedContainer or a deletion RevisionContainer in its container list
     */
    static boolean isWithoutTerms(TextFragment tf) {
        for (MarkupData m : tf.getContainers())
            if (m instanceof LockedContentContainer ||
                    ((m instanceof RevisionContainer) && ((RevisionContainer) m).getRevisionType().equals(RevisionType.DELETED)))
                return true;
        return false;
    }

    /**
     * Adjusts the Id fields of containers so that no two different containers have the same Id,
     * since a terminology container can split containers after its creation
     * This is done by generating new Ids for containers inside the terminologyAnnotationContainer
     */
    static void adjustSplitContainerIds(CollapsedSegment segment, int fragmentStartIndex, int fragmentEndIndex, int termLevel) {

        Map<String, MarkupData> containersToUpdate = new HashMap<>();
        Map<String, MarkupData> cloneContainers = new HashMap<>();

        for(int i = fragmentStartIndex; i <= fragmentEndIndex; i++) {
            List<MarkupData> location = segment.get(i).getContainers();
            //go through all nodes in the subtree of the terminologyAnnotationContainer
            for(int idx = termLevel +1; idx < location.size(); idx++) {
                // save these nodes in a set (hash map used because of equals() implementation in model)
                MarkupData container = location.get(idx);
                if(!containersToUpdate.containsKey(container.getId())) {
                    containersToUpdate.put(container.getId(), container);

                    MarkupData clone = container.duplicateWithoutChildren();
                    clone.setId(UUID.randomUUID().toString());
                    cloneContainers.put(container.getId(),clone);
                }
            }
        }
        // replace containers in collapsed segment with their clones w. new ids
        for(int i = fragmentStartIndex; i <= fragmentEndIndex; i++) {
            List<MarkupData> location = segment.get(i).getContainers();
            //go through all nodes in the subtree of the terminologyAnnotationContainer
            for(int idx = termLevel +1; idx < location.size(); idx++) {
                String markupId = location.get(idx).getId();
                location.remove(idx);
                location.add(idx,cloneContainers.get(markupId));
            }
        }

    }

}