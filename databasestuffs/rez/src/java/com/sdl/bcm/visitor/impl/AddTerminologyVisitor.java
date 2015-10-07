package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.BCMTransientData;
import com.sdl.bcm.model.*;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.visitor.BCMAbstractVisitor;
import com.sdl.bcm.visitor.BCMCompositeElement;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.helper.TerminologyMarker;

import java.util.*;

/**
 * @deprecated since 2.5; use TerminologyProcessor instead
 */
@Deprecated
@SuppressWarnings("unchecked")
public class AddTerminologyVisitor extends BCMAbstractVisitor {

    private enum Status {
        NONE, TERM_STARTED
    }

    private List<MarkupData> bcmElementsList;
    private Status status;
    private int[] startIndexes;
    private int[] endIndexes;
    private TerminologyData terminologyData;

    //related to adding TerminologyData to FileSkeleton
    private FileSkeleton fileSkeleton;
    private int lastAnnotationId;

    private MarkupDataContainer currentParent;

    //child -> parent
    private Map<MarkupData, MarkupDataContainer> parentsMap;

    //parent -> List of children
    private Map<MarkupDataContainer, List<MarkupData>> childMap;

    //last index in previous visited container
    private int lastTextIndex;
    //current argument index - index in startIndexes/endIndexes, which come in pairs
    private int currentArgumentIndex;

    private TerminologyMarker termMarkerStart;

    private BCMTransientData bcmTransientData;

    public AddTerminologyVisitor(int[] startIndexes, int[] endIndexes,
                                 TerminologyData terminologyData, Segment segment,
                                 FileSkeleton fileSkeleton, BCMTransientData bcmTransientData) {
        this.startIndexes = startIndexes;
        this.endIndexes = endIndexes;
        this.terminologyData = terminologyData;
        lastTextIndex = 0;
        currentArgumentIndex = 0;
        parentsMap = new HashMap();
        childMap = new HashMap();
        bcmElementsList = new LinkedList();
        bcmElementsList.add(segment);
        this.fileSkeleton = fileSkeleton;
        if (fileSkeleton.getTerminologyData() == null) {
            fileSkeleton.setTerminologyData(new LinkedList<TerminologyData>());
        }
        this.bcmTransientData = bcmTransientData;
        status = Status.NONE;
    }

    @Override
    public boolean visit(TextMarkup element) {
    	MarkupData parent = element.getParent();
        String currentText = element.getText();
        switch (status) {
            case NONE: {
                searchAnnotations(parent, currentText);
                break;
            }
            case TERM_STARTED: {
                int currentTextEndIndex = testEndIndexes(currentText);
                if (currentTextEndIndex > 0) {
                    TextMarkup currentTextFragment = new TextMarkup(parent, currentText.substring(0, currentTextEndIndex));
                    addElementToList(currentTextFragment);
                    lastTextIndex += currentTextEndIndex;

                    TerminologyMarker marker = new TerminologyMarker(parent, false, lastAnnotationId);
                    addElementToList(marker);

                    if (currentTextEndIndex <= currentText.length()) {
                        //we get to the next index
                        currentArgumentIndex++;
                        status = Status.NONE;
                        searchAnnotations(parent, currentText.substring(currentTextEndIndex));
                    }
                } else {
                    addElementToList(element);
                    lastTextIndex += element.getText().length();
                }
                break;
            }
            default:
        }
        return true;
    }

    private void searchAnnotations(MarkupData parent, String text) {
        String currentText = text;
        int currentTextStartIndex = testStartIndexes(currentText);
        while (currentTextStartIndex >= 0) {
            int currentTextEndIndex = testEndIndexes(currentText);
            if (currentTextEndIndex > 0) {
                //we have a new TerminologyAnnotation object complete
                if (currentTextStartIndex > 0) {
                    //need to add id and ParagraphUnit to newly created TextMarkups
                    TextMarkup textMarkup = new TextMarkup(parent, currentText.substring(0, currentTextStartIndex));
                    addElementToList(textMarkup);
                }
                //create new target term and add it to current parent
                TextMarkup currentTextFragment = new TextMarkup(parent, currentText.substring(currentTextStartIndex, currentTextEndIndex));
                TerminologyAnnotationContainer termAnnotationContainer = new TerminologyAnnotationContainer(parent);
                termAnnotationContainer.setId(bcmTransientData.getNextContainerId());
                termAnnotationContainer.setAnnotationId(bcmTransientData.getNextAnnotationId());
                termAnnotationContainer.setTerminologyDataId(addTerminologyDataToSkeleton(terminologyData));
                termAnnotationContainer.getChildren().add(currentTextFragment);
                addElementToList(termAnnotationContainer);
                bcmElementsList.add(currentTextFragment);
                addChildToParent(currentTextFragment, termAnnotationContainer);

                //adjust currentText in case there is more text to be processed
                //update lastTextIndex
                lastTextIndex += currentTextEndIndex;
                //we get to the next index
                currentArgumentIndex++;
                if (currentTextEndIndex < currentText.length()) {
                    //we have more text to process
                    currentText = currentText.substring(currentTextEndIndex);
                    currentTextStartIndex = testStartIndexes(currentText);
                } else {
                    //the entire text is already processed
                    currentText = "";
                    currentTextStartIndex = -1;
                }
                //status remains NONE
                status = Status.NONE;
            } else {
                //the end of current TerminologyAnnotation is in one of the following TextMarkups
                //we add the current text to currentFragments
                if (currentTextStartIndex > 0) {
                    //need to add id and ParagraphUnit to newly created TextMarkups
                    TextMarkup textMarkup = new TextMarkup(parent, currentText.substring(0, currentTextStartIndex));
                    addElementToList(textMarkup);
                }
                lastAnnotationId = bcmTransientData.getNextAnnotationId();
                TerminologyMarker marker = new TerminologyMarker(parent, true, lastAnnotationId);
                termMarkerStart = marker;
                addElementToList(marker);

                TextMarkup currentTextFragment = new TextMarkup(parent, currentText.substring(currentTextStartIndex));
                addElementToList(currentTextFragment);
                currentTextStartIndex = -1;
                lastTextIndex += currentText.length();
                currentText = "";
                status = Status.TERM_STARTED;
            }
        }
        if (currentText.length() > 0) {
            TextMarkup textMarkup = new TextMarkup(parent, currentText);
            addElementToList(textMarkup);
            lastTextIndex += currentText.length();
        }
    }

    private void addChildToParent(MarkupData markupData, MarkupDataContainer parent) {
        parentsMap.put(markupData, parent);
        List<MarkupData> children = childMap.get(parent);
        if (children == null) {
            children = new LinkedList();
            childMap.put(parent, children);
        }
        children.add(markupData);
    }

    @Override
    public boolean visitLeave(MarkupDataContainer element) {
        currentParent = parentsMap.get(element);
        return true;
    }

    @Override
    public boolean visit(BCMElement element) {
        return false;
    }

    @Override
    public boolean visitEnter(TagPair element) {
        addChildToParent(element, currentParent);
        currentParent = element;
        bcmElementsList.add(element);
        return true;
    }

    @Override
    public boolean visitLeave(TagPair element) {
        return containerVisitLeave(element);
    }

    private boolean containerVisitLeave(MarkupDataContainer element) {
        currentParent = parentsMap.get(element);
        boolean shouldMoveTermMarkerToUpperLevel = false;
        switch (status) {
            case TERM_STARTED: {
                MarkupDataContainer parent = parentsMap.get(termMarkerStart);
                //if parent of current TermMarker is current element, we check whether or not the TermMarker
                //should be moved to include the parent
                if (parent == element) {
                    List<MarkupData> children = childMap.get(element);
                    if (children != null && !children.isEmpty() && children.get(0) == termMarkerStart) {
                        shouldMoveTermMarkerToUpperLevel = true;
                        children.remove(0);
                        int termMarkupIndex = bcmElementsList.indexOf(termMarkerStart);
                        if (termMarkupIndex > 0) {
                            bcmElementsList.remove(termMarkupIndex);
                            bcmElementsList.add(termMarkupIndex - 1, termMarkerStart);
                            MarkupDataContainer parentOfParent = parentsMap.get(element);
                            if (parentOfParent != null) {
                                List<MarkupData> parentOfParentChildren = childMap.get(parentOfParent);
                                int parentIndex = parentOfParentChildren.indexOf(parent);
                                parentOfParentChildren.add(parentIndex, termMarkerStart);
                            }
                        }
                    }
                }
                break;
            }
            case NONE:
                break;
            default:
        }
        if (shouldMoveTermMarkerToUpperLevel) {
            parentsMap.put(termMarkerStart, currentParent);
        }
        return true;
    }

    @Override
    public boolean visitEnter(Segment element) {
        addChildToParent(element, currentParent);
        currentParent = element;
        return true;
    }

    @Override
    public boolean visitLeave(Segment element) {
        currentParent = parentsMap.get(element);
        return true;
    }

    @Override
    public boolean visitLeave(RevisionContainer element) {
        currentParent = parentsMap.get(element);
        return true;
    }

    @Override
    public boolean visitEnter(TerminologyAnnotationContainer element) {
        addChildToParent(element, currentParent);
        currentParent = element;
        bcmElementsList.add(element);
        return true;
    }

    @Override
    public boolean visitEnter(CommentContainer element) {
        addChildToParent(element, currentParent);
        currentParent = element;
        bcmElementsList.add(element);
        return true;
    }

    @Override
    public boolean visitLeave(CommentContainer element) {
        return containerVisitLeave(element);
    }

    @Override
    public boolean visitLeave(TerminologyAnnotationContainer element) {
        return containerVisitLeave(element);
    }

    public boolean visit(MarkupDataContainer element, BCMCompositeElement parent) {
        addElementToList(element);
        return true;
    }

    @Override
    public boolean visit(PlaceholderTag element) {
        addElementToList(element);
        lastTextIndex++; //we skip the added space
        if (status.equals(Status.TERM_STARTED) && endIndexes[currentArgumentIndex] == lastTextIndex) {
            TerminologyMarker marker = new TerminologyMarker(element.getParent(), false, lastAnnotationId);
            addElementToList(marker);
            status = Status.NONE;
            currentArgumentIndex++;
        }
        return true;
    }

    private int testStartIndexes(String text) {
        int result = -1;
        if (currentArgumentIndex >= startIndexes.length) {
            return -1;
        }
        if (startIndexes[currentArgumentIndex] >= lastTextIndex
                && startIndexes[currentArgumentIndex] < (lastTextIndex + text.length())) {
            result = startIndexes[currentArgumentIndex] - lastTextIndex;
        }
        return result;
    }

    private int testEndIndexes(String text) {
        int result = -1;
        if (currentArgumentIndex >= startIndexes.length) {
            return -1;
        }
        if (endIndexes[currentArgumentIndex] > lastTextIndex
                && endIndexes[currentArgumentIndex] <= (lastTextIndex + text.length())) {
            result = endIndexes[currentArgumentIndex] - lastTextIndex;
        }
        return result;
    }

    private int addTerminologyDataToSkeleton(TerminologyData terminologyData) {
        int termDataId = -1;
        for (TerminologyData termData : fileSkeleton.getTerminologyData()) {
            if (compare(termData, terminologyData)) {
                termDataId = termData.getId();
                break;
            }
        }
        if (termDataId == -1) {
            termDataId = bcmTransientData.getNextTerminologyDataId();
            fileSkeleton.getTerminologyData().add(terminologyData);
            terminologyData.setId(termDataId);
        }
        return termDataId;
    }

    //need to needs revision - better object compare
    private boolean compare(TerminologyData termData1, TerminologyData termData2) {
        if (!termData1.getOrigin().equals(termData2.getOrigin())) {
            return false;
        }
        if (termData1.getTerms().size() != termData2.getTerms().size()) {
            return false;
        }
        for (Term term1 : termData1.getTerms()) {
            boolean found = false;
            for (Term term2 : termData2.getTerms()) {
                if (compare(term1, term2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean compare(Term term1, Term term2) {
        return term1.getText().equals(term2.getText()) && Double.compare(term1.getScore(), term2.getScore()) == 0;
    }

     private void addElementToList(MarkupData markupData) {
        bcmElementsList.add(markupData);
        addChildToParent(markupData, currentParent);
    }

    public List<MarkupData> applyAnnotations() {
        MarkupDataContainer segment = (MarkupDataContainer) bcmElementsList.get(0);
        //clear parent of previous children as they are about to be reconstructed
        for (int i = 1; i < bcmElementsList.size(); i++) {
            MarkupData markupData = bcmElementsList.get(i);
            if (markupData != null) {
                if (markupData instanceof LockedContentContainer) {
                    continue;
                }
                if (markupData instanceof RevisionContainer && ((RevisionContainer) markupData).getRevisionType() != RevisionType.INSERTED) {
                    continue;
                }
                MarkupDataContainer container = (MarkupDataContainer) markupData;
                container.getChildren().clear();
            }
        }
        for (int i = 1; i < bcmElementsList.size(); i++) {
            MarkupData markupData = bcmElementsList.get(i);
            MarkupDataContainer parent = parentsMap.get(markupData);
            if (markupData.getType().equals(TerminologyMarker.TYPE)) {
                TerminologyMarker termMarker = (TerminologyMarker) markupData;
                MarkupDataContainer head = null;
                //holds duplicates: original->clone
                Map<MarkupDataContainer, MarkupDataContainer> parentOriginalToCloneMap = new HashMap();
                //holds duplicates: clone->original
                Map<MarkupDataContainer, MarkupDataContainer> parentCloneToOriginalMap = new HashMap();
                if (termMarker.isTermStart()) {
                    for (int j = i + 1; j < bcmElementsList.size(); j++) {
                        MarkupData termMarkupData = bcmElementsList.get(j);
                        if (termMarkupData.getType().equals(TerminologyMarker.TYPE)) {
                            TerminologyMarker termMarkerEnd = (TerminologyMarker) termMarkupData;

                            if (!termMarkerEnd.isTermStart()) {
                                MarkupDataContainer headOriginalParentContainer = parentCloneToOriginalMap.get(head);
                                TerminologyAnnotationContainer annotation;
                                if (headOriginalParentContainer != null) {
                                    annotation = new TerminologyAnnotationContainer(parent);
                                    annotation.setId(bcmTransientData.getNextContainerId());
                                    annotation.setAnnotationId(termMarkerEnd.getAnnotationId());
                                    annotation.setTerminologyDataId(addTerminologyDataToSkeleton(terminologyData));
                                    annotation.getChildren().addAll(head.getChildren());
                                    headOriginalParentContainer.getChildren().add(annotation);

                                    //fix and tag pair issue
                                    //if parent is Segment fix does not apply as we don't clone Segments
                                    MarkupDataContainer lastParent = parentsMap.get(termMarkupData);
                                    List<MarkupDataContainer> parentsTree = getParentsTree(annotation, lastParent);
                                    List<MarkupDataContainer> duplicatedContainers = new LinkedList<>();
                                    if (parentsTree != null) {
                                        parentsTree.add(annotation);
                                        MarkupData xChild = termMarkupData;
                                        MarkupDataContainer xParent = lastParent;
                                        int k = 0;
                                        while (k < parentsTree.size() - 1) {
                                            List<MarkupData> xParentChildren = childMap.get(xParent);
                                            int xChildIndex = xParentChildren.indexOf(xChild);
                                            MarkupDataContainer xParentOfParent = parentsTree.get(k + 1);
                                            if (xChildIndex < xParentChildren.size() - 1) {
                                                MarkupDataContainer xParentClone = (MarkupDataContainer) xParent.duplicateWithoutChildren();
                                                duplicatedContainers.add(xParent);
                                                List<MarkupData> parentOfParentChildren = childMap.get(xParentOfParent);
                                                if (parentOfParentChildren != null) {
                                                    parentOfParentChildren.add(xParentClone);
                                                }

                                                parentOriginalToCloneMap.put(xParent, xParentClone);
                                                parentCloneToOriginalMap.put(xParentClone, xParent);

                                                xParentClone.getChildren().addAll(xParent.getChildren());
                                                xParentClone.setId(bcmTransientData.getNextContainerId());
                                                xParent.getChildren().clear();
                                                int containerIndex = xParentOfParent.getChildren().indexOf(xParent);
                                                xParentOfParent.getChildren().remove(xParent);
                                                xParentOfParent.getChildren().add(containerIndex, xParentClone);
                                            }
                                            xChild = xParent;
                                            xParent = xParentOfParent;
                                            k++;
                                        }
                                        for (MarkupDataContainer container : duplicatedContainers) {
                                            MarkupDataContainer originalContainerParent = parentsMap.get(container);
                                            originalContainerParent.getChildren().add(container);
                                        }
                                    }
                                    i = j;
                                    break;
                                }
                            }
                        }
                        MarkupDataContainer termMarkupParent = parentsMap.get(termMarkupData);
                        MarkupDataContainer termMarkupParentCopy = parentOriginalToCloneMap.get(termMarkupParent);
                        if (termMarkupParentCopy == null) {
                            termMarkupParentCopy = (MarkupDataContainer) termMarkupParent.duplicateWithoutChildren();
                            if (termMarkupParentCopy instanceof TerminologyAnnotationContainer) {
                                termMarkupParentCopy.setId(bcmTransientData.getNextContainerId());
                            }
                            parentOriginalToCloneMap.put(termMarkupParent, termMarkupParentCopy);
                            parentCloneToOriginalMap.put(termMarkupParentCopy, termMarkupParent);
                        }
                        termMarkupParentCopy.getChildren().add(termMarkupData);
                        if (parentOriginalToCloneMap.get(termMarkupData) == null) {
                        	MarkupDataContainer container = (MarkupDataContainer) termMarkupData;
                            parentOriginalToCloneMap.put(container, container);
                            parentCloneToOriginalMap.put(container, container);
                        }
                        getParentsCopies(termMarkupParentCopy, parentOriginalToCloneMap, parentCloneToOriginalMap);
                        if (head == null) {
                            head = termMarkupParentCopy;
                        } else {
                            head = joinParents(head, termMarkupParentCopy, parentOriginalToCloneMap, parentCloneToOriginalMap);
                        }
                    }
                }
            } else {
                parent.getChildren().add(markupData);
            }
        }

        for (int i = 1; i < bcmElementsList.size(); i++) {
            MarkupData markupData = bcmElementsList.get(i);
            if (markupData != null ) {
                if ( markupData.getChildren().isEmpty()) {
                    MarkupDataContainer parentContainer = parentsMap.get(markupData);
                    if (parentContainer != null) {
                        parentContainer.getChildren().remove(markupData);
                    }
                }
            }
        }
        return segment.getChildren();
    }

    private List<MarkupDataContainer> getParentsTree(MarkupDataContainer annotation, MarkupDataContainer container) {
        if (container == annotation) {
            return new LinkedList<>();
        }
        List<MarkupData> children = annotation.getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            MarkupData markupData = children.get(i);
            if (markupData instanceof MarkupDataContainer) {
                MarkupDataContainer parentContainer = (MarkupDataContainer) markupData;
                List<MarkupDataContainer> parentsTree = getParentsTree(parentContainer, container);
                if (parentsTree != null) {
                    parentsTree.add(parentContainer);
                    return parentsTree;
                }
            }
        }
        return null;
    }

    //Head is always a clone
    private MarkupDataContainer joinParents(MarkupDataContainer head, MarkupDataContainer termMarkupParentCopy,
                                            Map<MarkupDataContainer, MarkupDataContainer> parentOriginalToCloneMap,
                                            Map<MarkupDataContainer, MarkupDataContainer> parentCloneToOriginalMap) {

        List<MarkupDataContainer> headParents = getParentsCopies(head, parentOriginalToCloneMap, parentCloneToOriginalMap);
        List<MarkupDataContainer> termParents = getParentsCopies(termMarkupParentCopy, parentOriginalToCloneMap, parentCloneToOriginalMap);
        int i = 0;
        int headParentsLength = headParents.size() - 1;
        int termParentsLength = termParents.size() - 1;
        while (i <= Math.min(headParentsLength, termParentsLength)
                && headParents.get(headParentsLength - i) == termParents.get(termParentsLength - i)) {
            i++;
        }
        return headParents.get(headParentsLength - i + 1);
    }

    private List<MarkupDataContainer> getParentsCopies(MarkupDataContainer container,
                                                       Map<MarkupDataContainer, MarkupDataContainer> parentOriginalToCloneMap,
                                                       Map<MarkupDataContainer, MarkupDataContainer> parentCloneToOriginalMap) {
        MarkupDataContainer containerCopy = container;
        MarkupDataContainer containerOriginal = parentCloneToOriginalMap.get(containerCopy);
        MarkupDataContainer containerOriginalParent = parentsMap.get(containerOriginal);
        List<MarkupDataContainer> clonedParents = new LinkedList<>();
        clonedParents.add(containerCopy);
        while (containerOriginalParent != null) {
            MarkupDataContainer containerParentCopy = parentOriginalToCloneMap.get(containerOriginalParent);
            if (containerParentCopy == null) {
                containerParentCopy = (MarkupDataContainer) containerOriginalParent.duplicateWithoutChildren();
                containerParentCopy.getChildren().add(containerCopy);
                parentCloneToOriginalMap.put(containerParentCopy, containerOriginalParent);
                parentOriginalToCloneMap.put(containerOriginalParent, containerParentCopy);
            }
            clonedParents.add(containerParentCopy);
            if (containerOriginalParent == parentsMap.get(containerOriginalParent)) {
                break;
            }
            containerOriginalParent = parentsMap.get(containerOriginalParent);
            containerCopy = containerParentCopy;
        }
        return clonedParents;
    }

}
