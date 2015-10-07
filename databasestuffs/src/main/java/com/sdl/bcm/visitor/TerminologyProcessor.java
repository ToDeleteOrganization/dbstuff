package com.sdl.bcm.visitor;

import com.sdl.bcm.manager.TerminologyManager;
import com.sdl.bcm.model.*;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.TerminologyData;

import java.util.LinkedList;
import java.util.UUID;
import static com.sdl.bcm.visitor.TerminologyProcessorHelper.*;

/**
 * @author vnastase
 * @since 2.5
 *  Class that handles terminology application on a segment
 *  (creates TerminologyContainers and adds them to the given Segment object in the appropriate positions)
 *  TerminologyContainers are created from the provided TerminologyData linked to a FileSkeleton
 *  startIndexes and endIndexes are provided for each term; these indexes are relative to the whole segment text
 *
 *  For applying terminology, the CollapsedSegment intermediary representation of a segment is used:
 *  the original segment is collapsed into a list of TextFragments, terminology is applied on this list structure,
 *  then the CollapsedSegment is expanded back to a tree and returned as a Segment
 */
public class TerminologyProcessor {

    private CollapsedSegment collapsedSegment;
    private int[] startIndexes;
    private int[] endIndexes;
    private TerminologyData terminologyData;
    private FileSkeleton fileSkeleton; //related to adding TerminologyData to FileSkeleton
    private TerminologyAnnotationContainer terminologyContainer; //used to build a new terminology container
    private int levelOfTermContainer; // the level where the new terminology container has to be added

    public TerminologyProcessor(Segment segment, int[] startIndexes, int[] endIndexes, TerminologyData terminologyData, FileSkeleton fileSkeleton) {

        this.collapsedSegment = new CollapsedSegment(segment);
        this.startIndexes = startIndexes;
        this.endIndexes = endIndexes;
        this.terminologyData = terminologyData;
        this.fileSkeleton = fileSkeleton;
        if (fileSkeleton.getTerminologyData() == null) {
            fileSkeleton.setTerminologyData(new LinkedList<TerminologyData>());
        }
        this.terminologyContainer = null;
        this.levelOfTermContainer = 0;
    }

    /**
     * creates and adds terminology containers to the collapsed representation of the segment
     * returns the updated and expanded segment with terminology containers applied
     */
    public Segment applyTerminology() {

        for (int i = 0; i < startIndexes.length; i++) { // go through each term provided

            int termStartIndex = startIndexes[i];
            int termEndIndex = endIndexes[i];
            int startingFragmentIndex = findFragmentIndexStart(collapsedSegment, termStartIndex);
            int endingFragmentIndex = findFragmentIndexEnd(collapsedSegment, termEndIndex, startingFragmentIndex);
                // create the TerminologyContainer:
            terminologyContainer = TerminologyManager.createTerminologyContainer(terminologyData, fileSkeleton);
            terminologyContainer.setId(UUID.randomUUID().toString());
                // find the location in the Containers list of the Fragment, where the TerminologyContainer has to be inserted:
            levelOfTermContainer = findContainerPosition(collapsedSegment, startingFragmentIndex, endingFragmentIndex);

            if(startingFragmentIndex != endingFragmentIndex) {  // term starts and ends in different fragments
                if(handleTermStart(startingFragmentIndex, termStartIndex)) {
                    // since a Fragment was added before the Term data, the Term data now starts/ends one position later
                    endingFragmentIndex ++;
                    startingFragmentIndex ++;
                }
                for(int idx = startingFragmentIndex + 1; idx < endingFragmentIndex; idx++) {
                    handleContainedTerm(idx);
                }
                handleTermEnd(endingFragmentIndex, termEndIndex);
            } else {                                            // term starts and ends in the same fragment
                handleTermInsideFragment(startingFragmentIndex, termStartIndex, termEndIndex);
            }
            terminologyContainer = null; // prepare for the next provided term

            startingFragmentIndex = findFragmentIndexStart(collapsedSegment, termStartIndex);
            endingFragmentIndex = findFragmentIndexEnd(collapsedSegment, termEndIndex, startingFragmentIndex);
            adjustSplitContainerIds(collapsedSegment,startingFragmentIndex,endingFragmentIndex, levelOfTermContainer);
        }
        return collapsedSegment.expand();
    }

    /**
     *  adds terminology to fragment list when the given fragment contains the start of a term only
     *  returns true if it added a new container; false if it replaced a container/text - checks if the TextFragment list grew or not
     */
    private boolean handleTermStart(int indexOfFragment, int absoluteTermStartIndex) {
        int fragmentIndex = indexOfFragment;
        boolean replaced = false;
        TextFragment originalFragment = collapsedSegment.get(fragmentIndex);      // original fragment which contains the term
        int originalFragmentStart = originalFragment.getStartIndex();
        int originalFragmentEnd = originalFragment.getEndIndex();
        int localTermStartIndex = absoluteTermStartIndex - originalFragmentStart; // local index of term start in current text fragment
        TextMarkup originalTextMarkup = (TextMarkup)originalFragment.getData();
        String termText = originalTextMarkup.getText().substring(localTermStartIndex);
        String nonTermText = originalTextMarkup.getText().substring(0, localTermStartIndex);

        if(localTermStartIndex == 0) {      // term starts at the beginning of fragment
            collapsedSegment.remove(fragmentIndex);
            replaced = true;
        } else {                            // term starts in the middle of the fragment
            originalTextMarkup.setText(nonTermText);
            originalFragment.setEndIndex(originalFragmentStart);
            fragmentIndex += 1; // Term Fragment should be inserted after this fragment
        }
        TextFragment termFragment = addTextFragment(terminologyContainer, levelOfTermContainer,
                originalFragment, termText, absoluteTermStartIndex, originalFragmentEnd);
        collapsedSegment.add(fragmentIndex, termFragment);
        return !replaced;
    }

    /**
     *  adds terminology to fragment list when the fragment is fully contained in the term
     */
    private void handleContainedTerm(int fragmentIndex) {

        if(isWithoutTerms(collapsedSegment.get(fragmentIndex))) {
                // a term-less container will split the term container that holds it, so create a new term container for the next part of the term
            terminologyContainer = TerminologyManager.createTerminologyContainer(terminologyData, fileSkeleton);
            terminologyContainer.setId(UUID.randomUUID().toString());
        } else {
                // non locked/deleted containers are simply included in term container
            TextFragment originalFragment = collapsedSegment.get(fragmentIndex); // original fragment which contains the term
            originalFragment.getContainers().add(levelOfTermContainer, terminologyContainer);
        }
    }

    /**
     *  adds terminology to fragment list when the given fragment contains the end of a term only
     */
    private void handleTermEnd(int fragmentIndex, int absoluteTermEndIndex) {

        TextFragment originalFragment = collapsedSegment.get(fragmentIndex); // original fragment which contains the term
        int originalFragmentStart = originalFragment.getStartIndex();
        int originalFragmentEnd = originalFragment.getEndIndex();
        int localTermEndIndex = absoluteTermEndIndex - originalFragmentStart; //local index of term end in current text fragment
        TextMarkup originalTextMarkup = (TextMarkup)originalFragment.getData();
        String termText = originalTextMarkup.getText().substring(0, localTermEndIndex);
        String nonTermText = originalTextMarkup.getText().substring(localTermEndIndex);

        if(absoluteTermEndIndex == originalFragmentEnd) {
                                            // term ends at the end of fragment
            collapsedSegment.remove(fragmentIndex);
        } else {                            // term ends in the middle of the fragment
            originalTextMarkup.setText(nonTermText);
            originalFragment.setStartIndex(absoluteTermEndIndex);
        }
        TextFragment termFragment = addTextFragment(terminologyContainer, levelOfTermContainer,
                originalFragment, termText, originalFragmentStart, absoluteTermEndIndex);
        collapsedSegment.add(fragmentIndex, termFragment);
    }

    /**
     *  adds terminology to fragment list when the term is fully contained in the given fragment
     */
    private void handleTermInsideFragment(int indexOfFragment, int absoluteTermStartIndex, int absoluteTermEndIndex) {
        int fragmentIndex = indexOfFragment;
        TextFragment originalFragment = collapsedSegment.get(fragmentIndex);  // original fragment which contains the term
        int originalFragmentStart = originalFragment.getStartIndex();
        int originalFragmentEnd = originalFragment.getEndIndex();
        int localTermStartIndex = absoluteTermStartIndex - originalFragmentStart;
        int localTermEndIndex = absoluteTermEndIndex - originalFragmentStart;

        TextMarkup originalTextMarkup = (TextMarkup)originalFragment.getData();
        String termText = originalTextMarkup.getText().substring(localTermStartIndex, localTermEndIndex);
        String textBeforeTerm = originalTextMarkup.getText().substring(0, localTermStartIndex);
        String textAfterTerm = originalTextMarkup.getText().substring(localTermEndIndex);

        if(localTermStartIndex == 0 && absoluteTermEndIndex == originalFragmentEnd) { //term is whole fragment
            collapsedSegment.remove(fragmentIndex);

        }else if(localTermStartIndex == 0){                //term starts at beginning of fragment, ends in middle
            originalTextMarkup.setText(textAfterTerm);
            originalFragment.setStartIndex(absoluteTermEndIndex);

        }else if(absoluteTermEndIndex == originalFragmentEnd){  //term starts in middle of fragment, ends at fragment end
            originalTextMarkup.setText(textBeforeTerm);
            originalFragment.setEndIndex(absoluteTermStartIndex);
            fragmentIndex += 1; // Term Fragment should be inserted after this fragment

        } else {                                //term starts and ends in the middle of the fragment
                // text fragment before
            originalTextMarkup.setText(textBeforeTerm);
            originalFragment.setEndIndex(absoluteTermStartIndex);
                // text fragment after
            fragmentIndex += 1; // Term Fragment should be inserted after the original fragment, and before the after non term

            TextFragment nonTermFragment = addTextFragment(terminologyContainer, levelOfTermContainer,
                    originalFragment, textAfterTerm, absoluteTermEndIndex, originalFragmentEnd);
            collapsedSegment.add(fragmentIndex, nonTermFragment);
            nonTermFragment.getContainers().removeLast(); //this is not part of the term container
        }
        TextFragment termFragment = addTextFragment(terminologyContainer, levelOfTermContainer,
                originalFragment, termText, absoluteTermStartIndex, absoluteTermEndIndex);
        collapsedSegment.add(fragmentIndex, termFragment);
    }

}
