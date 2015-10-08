package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.visitor.BCMVisitor;
import com.sdl.bcm.visitor.TerminologyProcessor;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A Segment stands for a list of text and markups
 * Equivalent tag in XLIFF: <mrk .../>
 * This class represents a "text unit", containing the text along with text style/formatting
 * <p/>
 * A segment is a piece of localizable content (text and tags etc.) for which existing translations can
 * possibly be re-used.  What exactly counts as a segment is determine by the segmentation engine.
 * In practice, a segment is typically a sentence.
 * There is always a 1-to-1 relationship between source and target segments in a paragraph unit.
 * <p/>
 * Segments cannot contain other segments.
 */
public class Segment extends MarkupDataContainer {

    public static final String TYPE = "segment";

    /**
     * The approval level of the translation for a target segment pair.
     * This property does not apply to source segments.
     * NOT_TRANSLATED is the default value.
     * To minimize size of JSON, default value will be not serialized.
     * Missing confirmationLevel in JSON means that the actual value for
     * confirmationLevel is NOT_TRANSLATED. If confirmationLevel has
     * other values than NOT_TRANSLATED, these values will be serialized.
     */
    private ConfirmationLevel confirmationLevel = ConfirmationLevel.NOT_TRANSLATED;

    /**
     * Whether this PU is locked, i.e. whether its contents can be changed by the user.
     */
    private boolean isLocked;

    /**
     * An optional wordCount for the content of a segment
     * (typically only populated for source segments).
     */
    private int wordCount;

    /**
     * An optional character count for the content of a segment
     * (typically only populated for source segments).
     */
    private int characterCount;

    /**
     * An automatically generated number that can be used by the translator to refer to the segment.
     * Initially segment numbers are sequential, starting with 1.
     * Certain operation can modify segment numbers:
     * - Splitting segment X, results in two new segment, numbered X.a and X.b respectively.
     * - Merging segments X and Y, effectively results in segment Y being deleted;
     * segment X remains segment X (with added content of Y) and no other segment
     * numbers are changed in the document.
     */
    private String segmentNumber;

    /**
     * Translation origin details
     */
    private TranslationOrigin translationOrigin;

    @JsonIgnore
    private String plainText;

    public Segment() {
        this(null);
    }

    public Segment(MarkupData parent) {
        this(parent, null);
    }

    public Segment(MarkupData parent, String id) {
        super(parent);
        setType(TYPE);
        setId(id);
    }

    @JsonProperty("isLocked")
    public boolean isLocked() {
        return isLocked;
    }

    @JsonProperty("isLocked")
    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public ConfirmationLevel getConfirmationLevel() {
        return confirmationLevel;
    }

    @JsonInclude(value = JsonInclude.Include.NON_DEFAULT)
    public void setConfirmationLevel(ConfirmationLevel confirmationLevel) {
        this.confirmationLevel = confirmationLevel;
    }

    public TranslationOrigin getTranslationOrigin() {
        return translationOrigin;
    }

    public void setTranslationOrigin(TranslationOrigin translationOrigin) {
        this.translationOrigin = translationOrigin;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(int characterCount) {
        this.characterCount = characterCount;
    }

    public String getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(String segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    @Override
    public Segment duplicateWithoutChildren() {
        Segment clone = new Segment(getParent());
        copyPropertiesTo(clone);
        return clone;
    }

    public void copyPropertiesTo(MetaData metaData) {
    	Segment segment = convertType(metaData);
    	if (segment != null) {
    		super.copyPropertiesTo(segment);
    		segment.setConfirmationLevel(confirmationLevel);
    		segment.setLocked(isLocked);
    		segment.setWordCount(wordCount);
    		segment.setCharacterCount(characterCount);
    		segment.setSegmentNumber(segmentNumber);
    		segment.setTranslationOrigin(translationOrigin);
    	}
    }

    @Override
    public Segment deepClone() {
    	Segment segment = duplicateWithoutChildren();
    	cloneChildrenInto(segment);
    	return segment;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (MarkupData elementChild : getACopyOfChildrenList()) {
                if (!elementChild.accept(visitor)) {
                    break;
                }
            }
        }
        return visitor.visitLeave(this);
    }

    @JsonIgnore
    public String getPlainText() {
        findPlainText();
        return plainText;
    }

    private void findPlainText() {
    	ExtractPlainTextVisitor visitor = new ExtractPlainTextVisitor();
        this.accept(visitor);
        plainText = visitor.getText();		
	}

	/**
     * Adds terminology data to the segment by creating terminology container tags around the terms.
     * Also the file which includes this segment has its skeleton information updated to include the terminology data provided.
     *
     * Output: The segment will have its structure altered to include TerminologyAnnotationContainers that will contain the terms.
     * These containers are set such that they include a minimal number of other containers or tags.
     * The TerminologyAnnotationContainers will split the containers and tags that include both term and non-term text,
     * into separate containers containing exclusively term or non-term text.
     * This behavior excludes LockedContainers and deleted RevisionContainers.
     *
     * @param startIndexes int[] array of start indices for the terms in the segment text.
     * @param endIndexes int[] array of end indices for terms in the segment text
     *                     Multiple terms can be processed at once, each term being identified by the index in both these lists.
     *                     The lists need to be of the same length, and the startIndex of a term needs to be less than its endIndex.
     *                     All the indices provided should point within the segment's text bounds.
     * @param terminologyData TerminologyData object containing terminology information to be annotated
     * @param fileSkeleton the FileSkeleton to be updated with terminology data
     * @throws VisitorException for non valid parameters
     */
    @JsonIgnore
    public void addTerminologyData(int[] startIndexes, int[] endIndexes, TerminologyData terminologyData, FileSkeleton fileSkeleton) throws VisitorException {

        validateAnnotationParameters(startIndexes, endIndexes, terminologyData);    //parameter validation - this can throw VisitorException
        TerminologyProcessor terminologyProcessor = new TerminologyProcessor(this, startIndexes, endIndexes, terminologyData, fileSkeleton);
        Segment segmentWithTerminology = terminologyProcessor.applyTerminology();   // apply terminology specified in constructor to original segment
                                                                                    // result is a new segment with terminology containers
        this.setChildren(segmentWithTerminology.getChildren()); // make the new tree structure available to the original segment
    }

    private void validateAnnotationParameters(int[] startIndexes, int[] endIndexes, TerminologyData terminologyData) throws VisitorException {
        validateIndexesFormally(startIndexes, endIndexes);
        populatePlainText();
        validateIndexesLogically(startIndexes, endIndexes);
        validateTerminologyData(terminologyData);
    }

    private void populatePlainText() {
        if(plainText == null) {
        	findPlainText();
        }
    }

    private void validateIndexesFormally(int[] startIndexes, int[] endIndexes) throws VisitorException {
        if(startIndexes == null || startIndexes.length == 0
                || endIndexes == null || endIndexes.length == 0) {
            throw new VisitorException("Annotation indexes are either null or empty!");
        }
        if(startIndexes.length != endIndexes.length) {
            throw new VisitorException("Annotation start/end indexes have different lengths!");
        }
    }

    private void validateIndexesLogically(int[] startIndexes, int[] endIndexes) throws VisitorException {
        int lastStartIndex;
        int lastEndIndex;
        for(int i = 0; i < startIndexes.length; i++) {
            lastStartIndex = startIndexes[i];
            if(lastStartIndex < 0 || lastStartIndex > plainText.length()) {
                throw new VisitorException("Annotation start indexes exceeds current segment text: " + lastStartIndex);
            }
            lastEndIndex = endIndexes[i];

            if(lastStartIndex >= lastEndIndex) {
                throw new VisitorException("Annotation start index is lower than end index: " + lastEndIndex);
            }
            if(lastEndIndex < 0 || lastEndIndex > plainText.length()) {
                throw new VisitorException("Annotation end indexes exceeds current segment text: " + lastEndIndex);
            }
        }
    }

    private void validateTerminologyData(TerminologyData terminologyData) throws VisitorException {
        if(terminologyData == null) {
            throw new VisitorException("Null Terminology Annotation!");
        }
        if(terminologyData.getTerms() == null || terminologyData.getTerms().isEmpty()) {
            throw new VisitorException("TerminologyData has no Terms defined!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        Segment that = (Segment) o;
        return new EqualsBuilder()
                .append(confirmationLevel, that.confirmationLevel)
                .append(isLocked, that.isLocked)
                .append(wordCount, that.wordCount)
                .append(characterCount, that.characterCount)
                .append(segmentNumber, that.segmentNumber)
                .append(translationOrigin, that.translationOrigin)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (confirmationLevel != null ? confirmationLevel.hashCode() : 0);
        result = 31 * result + (isLocked ? 1 : 0);
        result = 31 * result + wordCount;
        result = 31 * result + characterCount;
        result = 31 * result + (segmentNumber != null ? segmentNumber.hashCode() : 0);
        result = 31 * result + (translationOrigin != null ? translationOrigin.hashCode() : 0);
        return result;
    }

    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("Segment[");
    	if (confirmationLevel != null) {
    		builder.append("confirmationLevel=" + confirmationLevel.name() + ", ");
    	}
    	if (translationOrigin != null) {
    		builder.append("translationOrigin=" + translationOrigin + ", ");
    	}
    	builder.append("wordCount=" + wordCount + ", ");
    	builder.append("characterCount=" + characterCount + ", ");
    	builder.append("segmentNumber=" + segmentNumber);
    	builder.append("]");
    	return builder.toString();
    }
}
