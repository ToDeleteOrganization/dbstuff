package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.manager.TerminologyManager;
import com.sdl.bcm.model.*;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.visitor.BCMAbstractVisitor;
import com.sdl.bcm.visitor.BCMElement;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The Terminology visitor locates the terms in the BCM model based on the provided index3es and creates the appropriate Terminology Containers.
 * It holds state in between visits as a term could span multiple containers (such as when a comment is placed on a part of the term)
 *
 * @author Dan Tarba
 * @since 2.4
 * @deprecated since 2.5; use TerminologyProcessor instead, for applying terminology
 */
@Deprecated
public class TerminologyVisitor extends BCMAbstractVisitor {

    /**
     * NO_TERM - there is no term or part of a term in current visited container
     * TERM_STARTED - the term starts in this container but ends in another one
     * TERM_CONTINUED - the term starts in a previous visited container and end in another container. the content of this container is part of the term
     * TERM_END - the term ends in this container (starts in this or a previous visited container
     */
    private enum Status {
        NO_TERM, TERM_START, TERM_CONTINUED, TERM_END
    }

    private int[] startIndexes;
    private int[] endIndexes;
    private TerminologyData terminologyData;
    //related to adding TerminologyData to FileSkeleton
    private FileSkeleton fileSkeleton;

    private Status status;

    //start index of the visited text in the full original text
    private int currentTextIndex;

    // holds state for terms that end span multiple containers
    protected TerminologyAnnotationContainer terminologyContainer;
    protected TextMarkup terminologyTextMarkup;

    public TerminologyVisitor(int[] startIndexes, int[] endIndexes, TerminologyData terminologyData, FileSkeleton fileSkeleton) {
        this.startIndexes = startIndexes;
        this.endIndexes = endIndexes;
        this.terminologyData = terminologyData;
        this.fileSkeleton = fileSkeleton;
        if (fileSkeleton.getTerminologyData() == null) {
            fileSkeleton.setTerminologyData(new ArrayList<TerminologyData>());
        }
        status = Status.NO_TERM;
        terminologyContainer = null;
    }

    @Override
    public boolean visit(TextMarkup element) {
        int currentTextLength = element.getText().length();
        for (int i = 0; i < startIndexes.length; i++) {
            process(element, startIndexes[i], endIndexes[i]);
        }
        currentTextIndex += currentTextLength;
        return true;
    }

    private void process(TextMarkup element, int startIndex, int endIndex) {

        String currentText = element.getText();
        // determine the index relative to the position of the current text fragment in the original text
        int startIndexInsideCurrentText = startIndex - currentTextIndex;
        int endIndexInsideCurrentText = endIndex - currentTextIndex;
        Status currentStatus = determineCurrentStatus(currentText, startIndex, endIndex);
        switch (currentStatus) {
            case TERM_START: // term starts in this container but ends in a different one
                terminologyContainer = TerminologyManager.createTerminologyContainer(resolveParent(element), terminologyData, fileSkeleton);
                handleNonTerminology(element, startIndexInsideCurrentText);
                handleTerminology(element, currentText, startIndexInsideCurrentText, -1);
                break;
            case TERM_END:
                // here there are 2 cases (term surely ends in this container):
                // 1. the term starts in this container (terminologyContainer is null)
                // 2. the term started in a previous visited container (terminologyContainer != null)
                if (this.terminologyContainer == null) {
                    terminologyContainer = TerminologyManager.createTerminologyContainer(resolveParent(element), terminologyData, fileSkeleton);
                    handleNonTerminology(element, startIndexInsideCurrentText);
                    handleTerminology(element, currentText, startIndexInsideCurrentText, endIndexInsideCurrentText);
                    handleNonTerminology(element, currentText, -1, endIndexInsideCurrentText);
                    terminologyContainer = null;
                } else {
                    // the terminology container was already added when the term was found (start detected)
                    handleTerminology(element, currentText, startIndexInsideCurrentText, endIndexInsideCurrentText);
                    handleNonTerminology(element, currentText, -1, endIndexInsideCurrentText);
                    terminologyContainer = null;
                }
                break;
            case TERM_CONTINUED:
                // just append the text to the terminology container and move on
                handleTerminology(element, currentText, -1, -1);
                break;
            default: // NO_TERM - just continue
        }
    }

    private Status determineCurrentStatus(String currentText, int termStartIndex, int termEndIndex) {
        int endOfCurrentTextIndex = currentTextIndex + currentText.length();
        boolean termStartIsInTheCurrentText = termStartIndex >= currentTextIndex && termStartIndex < endOfCurrentTextIndex;
        boolean termEndIsInTheCurrentText = termEndIndex > currentTextIndex && termEndIndex <= endOfCurrentTextIndex;

        status = Status.NO_TERM;
        if (termStartIsInTheCurrentText) { // we found a term start
            if (termEndIsInTheCurrentText) {  // the term ends in this container
                status = Status.TERM_END;
            } else { // the term ends in one of the following containers
                status = Status.TERM_START;
            }
        } else {
            if (termEndIsInTheCurrentText) {
                status = Status.TERM_END;
            }
            // the term does not start or end in the current text but the current text is contained in the term limits
            else if (termStartIndex < currentTextIndex && termEndIndex > endOfCurrentTextIndex) {
                status = Status.TERM_CONTINUED;
            }
        }
        return status;
    }

    /**
     * Splits a container that contains a full term. The result is 2 containers that will hold the text from the beginning to the start fo the term
     * and another containing the text from the end of the term to the end of the text.
     */
    private void handleNonTerminology(TextMarkup element, String currentText, int startIndex, int endIndex) {
        handleNonTerminology(element, startIndex);
        if (endIndex < currentText.length()) {
            String newText = currentText.substring(endIndex);

            MarkupData parent = element.getParent();

            TextMarkup textMarkup;
            if (startIndex == 0) { // if current text starts with terminology remove current element (a new one will be created for the end text if necessary)
                parent.getChildren().remove(element);
            }

            textMarkup = new TextMarkup(parent);
            parent.getChildren().add(textMarkup);
            textMarkup.setText(newText);
            textMarkup.setId(UUID.randomUUID().toString());

            if (endIndex == 0) {
                parent.getChildren().remove(element);
            }
        }
    }

    /**
     * Splits a container that ends with a term (or part of a term that is continued in the process element). The handleNonTerminology will extract
     * one container containing the text from the beginning to the start of the term.
     */
    private void handleNonTerminology(TextMarkup element, int startIndex) {
        if (startIndex == 0) {
            element.getParent().getChildren().remove(element);
        } else if (startIndex > 0) {

            // remove the terminology from the text element
            String currentText = element.getText();
            String newText = currentText.substring(0, startIndex);
            element.setText(newText);
        }
    }

    private void handleTerminology(TextMarkup element, String originalText, int startIndex, int endIndex) {
        MarkupData parent = resolveParent(element);

        String termText;
        if (startIndex >= 0) {
            termText = endIndex >= 0 ? originalText.substring(startIndex, endIndex) : originalText.substring(startIndex);
        } else {
            termText = endIndex >= 0 ? originalText.substring(0, endIndex) : originalText;
        }

        // if there is a terminology container already created (this means we have a start term and this is a continuation or a term end)
        if (this.terminologyTextMarkup != null) {
            if (parentIsContainerOrTag(element)) {
                MarkupData terminologyElement = createTextMarkupFromSource(termText, element);
                terminologyContainer.getChildren().add(terminologyElement);
                terminologyElement.setParent(terminologyContainer);
            } else {
                terminologyTextMarkup.setText(terminologyTextMarkup.getText() + termText);
            }
        } else {
            MarkupData textMarkupFromSource = createTextMarkupFromSource(termText, element);
            terminologyContainer.getChildren().add(textMarkupFromSource);
            textMarkupFromSource.setParent(terminologyContainer);
            parent.getChildren().add(terminologyContainer);
        }
        markForCleanup(element, startIndex, endIndex);
    }

    private void markForCleanup(TextMarkup element, int startIndex, int endIndex) {
        if (startIndex <= 0 && (endIndex < 0 || endIndex == element.getText().length())) {
            element.setText(null); //mark this for cleanup
        }
    }

    /**
     * Creates a text a new markup element and keeps formatting and annotations of the source text markup intact.
     * Used when splitting.
     */
    public MarkupData createTextMarkupFromSource(String text, TextMarkup source) {

        TextMarkup textMarkup = new TextMarkup();
        textMarkup.setText(text);
        terminologyTextMarkup = textMarkup; // method side effect - adds unexpected state to the class - refactor this
        MarkupData childElement = textMarkup;

        MarkupData parentCopy = null;
        MarkupData currentElement = source;
        while (parentIsContainerOrTag(currentElement.getParent())) {
            currentElement = currentElement.getParent();
            parentCopy = currentElement.duplicateWithoutChildren();
            parentCopy.setId(UUID.randomUUID().toString());
            parentCopy.getChildren().add(childElement);
            childElement.setParent(parentCopy);
            childElement = (MarkupDataContainer) parentCopy;
        }
        return parentCopy != null ? parentCopy : textMarkup;
    }

    /**
     * Resolves the real parent that should contain the new terminology container. This is because the tags , revisions, comments and so on should not be altered on
     * the new term.
     *
     * @return the real parent of this element
     */
    protected MarkupData resolveParent(TextMarkup element) {
        MarkupData parent = element.getParent();
        while(parentIsContainerOrTag(parent)) {
            parent = parent.getParent();
        }
        return parent;
    }

    protected boolean parentIsContainerOrTag(MarkupData parent) {
        if(parent == null)
            return false;
        Boolean condition;
        condition = parent instanceof RevisionContainer;
        condition = condition || parent instanceof FeedbackContainer;
        condition = condition || parent instanceof LockedContentContainer;
        condition = condition || parent instanceof CommentContainer;
        condition = condition || parent instanceof TerminologyAnnotationContainer;
        condition = condition || parent instanceof TagPair;
        condition = condition || parent instanceof TextMarkup;
        return condition;
    }

    @Override
    public boolean visit(BCMElement element) {
        return false;
    }

    @Override
    public boolean visitLeave(Segment element) {
        return true;
    }

    @Override
    public boolean visitEnter(RevisionContainer element) {
        return element.getRevisionType() != RevisionType.DELETED;
    }
}
