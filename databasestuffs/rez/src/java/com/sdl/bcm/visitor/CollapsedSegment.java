package com.sdl.bcm.visitor;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.helper.OrderedList;
import com.sdl.bcm.visitor.helper.OrderedListInterface;

import static com.sdl.bcm.visitor.CollapsedSegmentHelper.*;

/**
 * @author vnastase
 * @since 2.5
 * A representation of a Segment as a collapsed tree, in the form of a List of TextFragments
 * The list is ordered to maintain the original Segment's plain text
 * Each TextFragment has indices representing its absolute position in the Segment's plain text
 * Each TextFragment has an associated location given by its ancestors ordered in a list - see TextFragment.containers
 */
 class CollapsedSegment {

    private OrderedListInterface<TextFragment> collapsedTree; // the collapsed tree representation of the provided segment
    private Segment updatedSegment;
    private OrderedListInterface<MarkupData> containerList = new OrderedList<>(); // keep track of the containers traversed
    private int indexCounter; // counts all visible characters in segment (excludes locked/deleted ones); used for setting fragment indices
    private boolean termless; // status of current leaf - is it free of terms (does it belong to a locked/deleted container)

    CollapsedSegment(MarkupData segment) {
        this.updatedSegment = (Segment)segment.duplicateWithoutChildren();
        this.updatedSegment.setId(segment.getId());
        this.collapsedTree = new OrderedList<>();
        collapseSegment(segment); //create the collapsed segment from the original segment provided
        indexCounter = 0;
    }

    /**
     * operations allowed on the CollapsedSegment:
     * get(int) - retrieves TextFragment at a given index
     * size() - returns the number of TextFragments in the CollapsedSegment
     * add(int, TextFragment) - adds a TextFragment at a specified index
     * remove(int) - removes a TextFragment from the specified index
     */
    TextFragment get(int idx) {
        return collapsedTree.get(idx);
    }
    int size() {
        return collapsedTree.size();
    }
    void add(int idx, TextFragment fragment) {
        collapsedTree.add(idx, fragment);
    }
    void remove(int idx) {
        collapsedTree.remove(idx);
    }

    /**
     * transforms the segment from a tree-like structure to a linked list, maintaining all data
     */
    private void collapseSegment(MarkupData parent) {

        for(MarkupData child : parent.getChildren()) {

            if(isLeaf(child)) { // check if current node can have children; recursion return condition

                addFragment(child, indexCounter, termless, containerList, collapsedTree);
                indexCounter = collapsedTree.getLast().getEndIndex(); // update the counter with the text length of the last fragment

            } else {
                containerList.add(child); // maintain the list of current ancestors
                if(isWithoutTerms(child))
                    termless = true; // children can be aware if they are part of a term-less container

                collapseSegment(child); // !! recursive call !!

                containerList.removeLast();
                if(isWithoutTerms(child))
                    termless = false; // clear term-less status after coming back from recursion
            }
        }
    }

    /**
     * expands the collapsed tree into a Segment object with the same hierarchy
     * @return Segment object with the same structure as the collapsedTree
     */
    Segment expand() {

        for(TextFragment textFragment : collapsedTree) {

                //remember position in object hierarchy; start with the Segment as root
            MarkupData currentParent = updatedSegment;

                // expand location: go through Fragment's location containers and create them in a hierarchy if they don't exist
            currentParent = expandTree(textFragment.getContainers(), currentParent);

                // create the leaf in Segment tree, under currentParent found/built before
            MarkupData leaf = textFragment.getData();
            leaf.setParent(currentParent);
            currentParent.getChildren().add(leaf);
        }
        return updatedSegment;
    }
}
