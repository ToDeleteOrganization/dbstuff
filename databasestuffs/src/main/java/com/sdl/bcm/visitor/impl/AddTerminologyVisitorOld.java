package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.model.*;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.visitor.BCMAbstractVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @deprecated since 2.5; use TerminologyProcessor instead
 */
@Deprecated
public class AddTerminologyVisitorOld extends BCMAbstractVisitor {
    private enum Status {
        NONE, TERM_STARTED
    }

    private int[] startIndexes;
    private int[] endIndexes;
    private Status status = Status.NONE;
    private TerminologyData terminologyData;
    private List<MarkupData> currentFragments;
    private Map<MarkupData, List<MarkupData>> savedFragments;//holds node->fragments
    private MarkupDataContainer termAnnotationFragment;
    private MarkupDataContainer currentParent;
    private Map<MarkupDataContainer, MarkupDataContainer> parentsMap; //used for backwards navigation

    //related to adding TerminologyData to FileSkeleton
    private FileSkeleton fileSkeleton;
    private int lastTerminologyDataId = 1;


    //last index in previous visited container
    private int lastTextIndex;

    //current argument index - index in startIndexes/endIndexes, which come in pairs
    private int currentArgumentIndex;

    public AddTerminologyVisitorOld(int[] startIndexes, int[] endIndexes, TerminologyData terminologyData) {
        this.startIndexes = startIndexes;
        this.endIndexes = endIndexes;
        this.terminologyData = terminologyData;
        lastTextIndex = 0;
        currentArgumentIndex = 0;
        currentFragments = new ArrayList<>();
        savedFragments = new HashMap<>();
    }

    @Override
    public boolean visit(TextMarkup element) {
    	MarkupData parent = element.getParent();
        String currentText = element.getText();
        switch (status) {
            case NONE: {
                int currentTextStartIndex = testStartIndexes(currentText);
                while (currentTextStartIndex >= 0) {
                    int currentTextEndIndex = testEndIndexes(currentText);
                    if (currentTextEndIndex > 0) {
                        //we have a new TerminologyAnnotation object complete
                        if (currentTextStartIndex > 0) {
                            //need to add id and ParagraphUnit to newly created TextMarkups
                            TextMarkup textMarkup = new TextMarkup(parent, currentText.substring(0, currentTextStartIndex));
                            currentParent.getChildren().add(textMarkup);
                        }
                        //create new target term and add it to current parent
                        TextMarkup currentTextFragment = new TextMarkup(parent, currentText.substring(currentTextStartIndex, currentTextEndIndex));
                        TerminologyAnnotationContainer termAnnotationContainer = new TerminologyAnnotationContainer(parent);
                        termAnnotationContainer.setId("123");//need to review this and add incremental value unique across document
                        termAnnotationContainer.setTerminologyDataId(addTerminologyDataToSkeleton(terminologyData));
                        termAnnotationContainer.getChildren().add(currentTextFragment);
                        currentParent.getChildren().add(termAnnotationContainer);

                        //adjust currentText in case there is more text to be processed
                        if (currentTextEndIndex < currentText.length()) {
                            //we have more text to process
                            currentText = currentText.substring(currentTextEndIndex);
                            currentTextStartIndex = testStartIndexes(currentText);
                        } else {
                            //the entire text is already processed
                            currentText = "";
                            currentTextStartIndex = -1;
                        }
                    } else {
                        //the end of current TerminologyAnnotation is in one of the following TextMarkups
                        //we add the current text to currentFragments
                        if (currentTextStartIndex > 0) {
                            //need to add id and ParagraphUnit to newly created TextMarkups
                            TextMarkup textMarkup = new TextMarkup(parent, currentText.substring(0, currentTextStartIndex));
                            currentParent.getChildren().add(textMarkup);
                        }
                        TextMarkup currentTextFragment = new TextMarkup(parent, currentText.substring(currentTextStartIndex));
                        currentFragments.add(currentTextFragment);
                        currentTextStartIndex = -1;
                        currentText = "";
                    }
                    //we get to the next index
                    currentArgumentIndex++;
                }
                if (currentText.length() > 0) {
                    TextMarkup textMarkup = new TextMarkup(parent, currentText);
                    currentParent.getChildren().add(textMarkup);
                }
                break;
            }
            case TERM_STARTED: {
                int currentTextEndIndex = testEndIndexes(currentText);
                if (currentTextEndIndex > 0) {
                    TextMarkup currentTextFragment = new TextMarkup(parent, currentText.substring(0, currentTextEndIndex));
                    currentFragments.add(currentTextFragment);
                }
                break;
            }
        }
        return true;
    }

    /**
     * Test whether start argument index is in current text
     */
    private int testStartIndexes(String text) {
        int result = -1;
        if (startIndexes[currentArgumentIndex] > lastTextIndex
                && startIndexes[currentArgumentIndex] < (lastTextIndex + text.length())) {
            result = startIndexes[currentArgumentIndex] - lastTextIndex;
        }
        return result;
    }

    private int testEndIndexes(String text) {
        int result = -1;
        if (endIndexes[currentArgumentIndex] > lastTextIndex
                && endIndexes[currentArgumentIndex] < (lastTextIndex + text.length())) {
            result = endIndexes[currentArgumentIndex] - lastTextIndex;
        }
        return result;
    }

    @Override
    public boolean visitEnter(TagPair element) {
        switch (status) {
            case NONE: {
                //adding the new composite in the tree
                TagPair tagPair = new TagPair(element);
                currentParent.getChildren().add(tagPair);
                parentsMap.put(element, currentParent);
                currentParent = tagPair;
                break;
            }
            case TERM_STARTED: {
                savedFragments.put(element, currentFragments);
                currentFragments = new ArrayList<>();
                break;
            }
        }
        return true;
    }

    @Override
    public boolean visitLeave(TagPair element) {
        switch (status) {
            case NONE: {

                break;
            }
            case TERM_STARTED: {
                break;
            }
        }
        if (!currentFragments.isEmpty() && termAnnotationFragment == null) {
            //we have children for current container
            //we clone the current parent and add currentFragments to it as part of the new TerminologyAnnotation
            TagPair tagPair = new TagPair(element);
            tagPair.setId(tagPair + "a");
            tagPair.getChildren().addAll(currentFragments);
            currentFragments.clear();
            termAnnotationFragment = tagPair;
            if (status == Status.NONE) {
                TerminologyAnnotationContainer termAnnotationContainer = new TerminologyAnnotationContainer(element.getParent());
                termAnnotationContainer.setId("123");
                termAnnotationContainer.setTerminologyDataId(addTerminologyDataToSkeleton(terminologyData));
                termAnnotationContainer.getChildren().add(termAnnotationFragment);
            }
        }
        return super.visitLeave(element);
    }

    @Override
    public boolean visit(PlaceholderTag element) {
        return false;
    }

    private int addTerminologyDataToSkeleton(TerminologyData terminologyData) {
        int existingTermDataId = -1;
        for (TerminologyData termData : fileSkeleton.getTerminologyData()) {
            if (compare(termData, terminologyData)) {
                existingTermDataId = termData.getId();
                break;
            }
        }
        if (existingTermDataId == -1) {
            existingTermDataId = lastTerminologyDataId++;
            fileSkeleton.getTerminologyData().add(terminologyData);
        }
        return existingTermDataId;
    }

    private boolean compare(TerminologyData termDataA, TerminologyData termDataB) {
        if (!termDataA.getOrigin().equals(termDataB.getOrigin())) {
            return false;
        }
        if (termDataA.getTerms().size() != termDataB.getTerms().size()) {
            return false;
        }
        for (Term term1 : termDataA.getTerms()) {
            boolean found = false;
            for (Term term2 : termDataB.getTerms()) {
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

    private boolean compare(Term termA, Term termB) {
        return termA.getText().equals(termB.getText()) && Double.compare(termA.getScore(), termB.getScore()) == 0;
    }
}
