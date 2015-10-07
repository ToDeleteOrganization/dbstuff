package com.sdl.bcm.terminology;

import java.io.IOException;

import com.sdl.bcm.VisitorTest;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.sdl.bcm.TestBase;
import com.sdl.bcm.model.LockedContentContainer;
import com.sdl.bcm.model.MarkupDataContainer;
import com.sdl.bcm.model.PlaceholderTag;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.utils.Utils;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;


public class AddTerminologyAcceptanceTest extends TestBase {
    public static Logger logger = Logger.getLogger(AddTerminologyAcceptanceTest.class);
    private String testDataPath = "src/test/resources/testData/AddTerminologyAcceptanceTest/";
    int[] startIndexes = new int[1];
    int[] endIndexes = new int[1];

    @Override
    public void before() {
        super.before();
    }

    private void createTextType(Segment segment, String text){
        TextMarkup textMarkup = new TextMarkup(segment, text);
        segment.getChildren().add(textMarkup);
        textMarkup.setParent(segment);
    }

    private void createTextType(TagPair tagPair, String text){
        TextMarkup textMarkup = new TextMarkup(tagPair, text);
        tagPair.getChildren().add(textMarkup);
        textMarkup.setParent(tagPair);
    }

    private void createTextType(MarkupDataContainer parent, String text){
        TextMarkup textMarkup = new TextMarkup(parent, text);
        parent.getChildren().add(textMarkup);
        textMarkup.setParent(parent);
    }

    private TagPair createTagPairType(MarkupDataContainer parent, String tagPairId, int tagPairDefId){
        TagPair tagPair = new TagPair(parent);
        tagPair.setId(tagPairId);
        tagPair.setTagPairDefinitionId(tagPairDefId);
        parent.getChildren().add(tagPair);
        tagPair.setParent(parent);
        return tagPair;
    }

    private TagPair createTagPairType(MarkupDataContainer parent, String text, String tagPairId, int tagPairDefId){
        TagPair tagPair = new TagPair(parent);
        tagPair.setId(tagPairId);
        tagPair.setTagPairDefinitionId(tagPairDefId);
        parent.getChildren().add(tagPair);
        TextMarkup textMarkup = new TextMarkup(tagPair, text);
        tagPair.getChildren().add(textMarkup);
        textMarkup.setParent(tagPair);
        return tagPair;
    }

    private void createPlaceholderType(MarkupDataContainer parent, String placeholderId, int tagPairDefId){
        PlaceholderTag placeholder = new PlaceholderTag(parent);
        placeholder.setId(placeholderId);
        placeholder.setPlaceholderTagDefinitionId(tagPairDefId);
        parent.getChildren().add(placeholder);
        placeholder.setParent(parent);
    }


    private LockedContentContainer createLockedType(MarkupDataContainer parent, String tagPairId){
        LockedContentContainer lockedContentElement = new LockedContentContainer(parent);
        lockedContentElement.setId(tagPairId);
        parent.getChildren().add(lockedContentElement);
        lockedContentElement.setParent(parent);
        return lockedContentElement;
    }


    private void setIndexes(Integer startIndex, Integer endIndex){
        startIndexes[0] = startIndex;
        endIndexes[0] = endIndex;
    }


    private void validateJson(Segment segment, String jsonFilePath){
        String dataReference = Utils.readFile(jsonFilePath);
        try {
            //String segmentJSON = objectMapper.writeValueAsString(segment);
            Segment reference = objectMapper.readValue(dataReference, Segment.class);
            Assert.assertTrue(segment.equals(reference));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testTerminologyIndexValidation() throws VisitorException {
        Segment segment = new Segment(null);
        FileSkeleton fileSkeleton = new FileSkeleton();
        createTextType(segment, "This is a bit more rich.");
        TerminologyData terminologyData = setTerminologyData(0,"a bit", "termid1", 1);

        startIndexes[0] = 8;
        endIndexes[0] = 13;

        try {//first index null
            segment.addTerminologyData(null, endIndexes, terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation indexes are either null or empty!"));
        }
        try {//first index empty
            segment.addTerminologyData(new int[0], endIndexes, terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation indexes are either null or empty!"));
        }
        try {//second index null
            segment.addTerminologyData(startIndexes, null, terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation indexes are either null or empty!"));
        }
        try {//second index empty
            segment.addTerminologyData(startIndexes, new int[0], terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation indexes are either null or empty!"));
        }
        try {//different lengths
            segment.addTerminologyData(new int[2], new int[3], terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation start/end indexes have different lengths!"));
        }
        startIndexes[0] = -1;
        try {//start index < 0
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation start indexes exceeds current segment text: -1"));
        }
        startIndexes[0] = 82;
        try {//start index > text end
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation start indexes exceeds current segment text: 82"));
        }
        startIndexes[0] = 8;
        endIndexes[0] = 84;
        try {//end index > text end
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation end indexes exceeds current segment text: 84"));
        }
        endIndexes[0] = 13;

        //terminologyData
        try {//null terminologyData
            segment.addTerminologyData(startIndexes, endIndexes, null, fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Null Terminology Annotation!"));
        }
        try {//no defined terms
            segment.addTerminologyData(startIndexes, endIndexes, new TerminologyData(), fileSkeleton);
            Assert.fail("Index validation: Exception not thrown correctly.");
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("TerminologyData has no Terms defined!"));
        }

    }

    @Test
    public void testDuplicateAnnotationOnPlainText() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"This is a bit more rich.");

        validateJson(segment, testDataPath + "testMultipleTermsOnSameText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(8,13);

        TerminologyData terminologyData1 = setTerminologyData(0,"a bit", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        setIndexes(8,13);
        TerminologyData terminologyData2 = setTerminologyData(1,"a bit", "termid2", 2);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment, testDataPath + "testMultipleTermsOnSameText-after.json");

    }


    @Test
    public void testMultipleOverlappingAnnotationsOnPlainText() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"With a couple of paragraphs.");

        validateJson(segment, testDataPath + "testMultipleAnnotationOnPlainText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,13);
        TerminologyData terminologyData1 = setTerminologyData(0, "With a couple", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        //second terminology
        setIndexes(17,27);
        TerminologyData terminologyData2 = setTerminologyData(1, "paragraphs", "termid2", 2);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);

        //third terminology
        setIndexes(7, 27);
        TerminologyData terminologyData3 = setTerminologyData(2, "couple of paragraphs", "termid3", 2);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData3, fileSkeleton);

        validateJson(segment,testDataPath + "testMultipleAnnotationOnPlainText-after.json");
    }


    @Test
    public void testTermAnnotationWrappedAroundSegmentOnPlainText() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"Terminology test");

        validateJson(segment, testDataPath + "testTermAnnotationWrappedAroundSegmentOnPlainText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,11);
        TerminologyData terminologyData1 = setTerminologyData(0, "Terminology", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);
        logSegmentJSON(segment);

        setIndexes(12, 16);
        TerminologyData terminologyData2 = setTerminologyData(1, "test", "termid2", 2);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);

        setIndexes(0,16);
        TerminologyData terminologyData3 = setTerminologyData(2, "Terminology test", "termid3", 2);

        validateJson(segment, testDataPath +"testTermAnnotationWrappedAroundSegmentOnPlainText-after.json");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData3, fileSkeleton);

        validateJson(segment,testDataPath + "testTermAnnotationWrappedAroundSegmentOnPlainText-after2.json");


    }

    @Test
    public void testMultipleTermAnnotationsNotOverlappingOnPlainText() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment, "Terminology annotation test");

        validateJson(segment, testDataPath + "testMultipleAnnotationOnPlainTextNotOverlapping-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,11);
        TerminologyData terminologyData1 = setTerminologyData(0, "Terminology", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);
        setIndexes(12,22);
        TerminologyData terminologyData2 = setTerminologyData(1, "annotation", "termid2", 2);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);

        setIndexes(23, 27);
        TerminologyData terminologyData3 = setTerminologyData(2, "test", "termid3", 2);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData3, fileSkeleton);

        validateJson(segment,testDataPath + "testMultipleAnnotationOnPlainTextNotOverlapping-after.json");
    }


    @Test
    public void testSimpleTermAnnotationsOutsideAndInsideTagPairs() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"To see ");
        createTagPairType(segment, "all", "1", 1);
        createTextType(segment, " words.");


        //checkJsonBeforeAnnotation(segment);
        validateJson(segment, testDataPath + "testSimpleTermAnnotationsOutsideAndInsideTagPairs-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(3,16);

        TerminologyData terminologyData1 = setTerminologyData(0, "see all words", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        setIndexes(7,10);
        TerminologyData terminologyData2 = setTerminologyData(1, "all", "termid2", 2);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);


        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testSimpleTermAnnotationsOutsideAndInsideTagPairs-after.json");

    }

    @Test
    public void testSimpleCaseClonedTagPairs() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"ITS defines ");
        createTagPairType(segment,"data category", "1", 1);
        createTextType(segment, " as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testSimpleCaseClonedTagPairs-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(4,16);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment, testDataPath + "testSimpleCaseClonedTagPairs-after.json");
    }

    //defect 488
    @Test
    public void testEmbeddedTagPairsNotCloned() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"ITS defines ");
        TagPair firsttag = createTagPairType(segment,"data ", "1", 1);
        createTagPairType(firsttag,"category", "2", 2);
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testEmbeddedTagPairsNotCloned-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(4,25);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data category", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());


        validateJson(segment,testDataPath + "testEmbeddedTagPairsNotCloned-after.json");
    }

    //defect 489
    @Test
    public void testEmbeddedTagPairsCloned() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"ITS defines ");
        TagPair tagpair1 = createTagPairType(segment,"data ", "1", 1);
        createTagPairType(tagpair1,"as category", "2", 2);
        createTextType(segment," or as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testEmbeddedTagPairsCloned-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(4,19);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testEmbeddedTagPairsCloned-after.json");
    }

    //@Test //defect 639
    public void testTextTagTagTextCtag() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"ITS defines ");
        //TagPair tagpair1 = createTagPairType(segment,"data ", "1", 1);
        TagPair tagpair1 = createTagPairType(segment,"1", 1);
        createTagPairType(tagpair1,"data as category", "2", 2);
        createTextType(tagpair1, " or as an abstract concept");
        createTextType(segment," for a particular type of information");

        validateJson(segment, testDataPath + "testTextTagTagTextTagTag-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,28);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTextTagTagTextTag.json");
    }

    @Test
    public void testTagTagTextCtagText() throws VisitorException {
        Segment segment = new Segment(null);

        TagPair tagpair1 = createTagPairType(segment,"1", 1);
        createTagPairType(tagpair1,"ITS", "2", 2);
        createTextType(tagpair1, " defines data as category");
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testTagTagTextCtagText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,11);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTagTagTextCtagText.json");
    }

    @Test
    public void testTextCtagCtagText() throws VisitorException {
        Segment segment = new Segment(null);

        TagPair tagpair1 = createTagPairType(segment,"1", 1);
        createTextType(tagpair1, "ITS ");
        createTagPairType(tagpair1, "defines data as category", "2", 2);
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testTextCtagCtagText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(12,31);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTextCtagCtagText-after.json");
    }

    @Test
    public void testTextTagTextCtagCtagText() throws VisitorException {
        Segment segment = new Segment(null);

        TagPair tagpair1 = createTagPairType(segment, "ITS defines ","1", 1);
        createTagPairType(tagpair1,"data as category", "2", 2);
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testTextTagTextCtagCtagText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(4,51);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTextTagTextCtagCtagText-after.json");
    }

    @Test
    public void testTagTextCtagCtagText() throws VisitorException {
        Segment segment = new Segment(null);

        TagPair tagpair1 = createTagPairType(segment, "ITS defines ","1", 1);
        createTagPairType(tagpair1,"data as category", "2", 2);
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testTextTagTextCtagCtagText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(12,51);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTagTextCtagCtagText-after.json");
    }

    @Test
    public void testTextTagTextTagText() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"ITS ");

        TagPair tagpair1 = createTagPairType(segment, "defines ","1", 1);
        createTagPairType(tagpair1,"data as category", "2", 2);
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testTextTagTextTagText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,16);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTextTagTextTagText-after.json");
    }

    @Test
    public void testTagTextTagText() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"ITS ");

        TagPair tagpair1 = createTagPairType(segment, "defines ","1", 1);
        createTagPairType(tagpair1,"data as category", "2", 2);
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testTextTagTextTagText-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(4,16);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTagTextTagText-after.json");
    }

    @Test
    public void  testTextTagTextTagTag() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"ITS ");

        TagPair tagpair1 = createTagPairType(segment, "defines ","1", 1);
        TagPair tagpair2 = createTagPairType(tagpair1,"2", 2);
        createTagPairType(tagpair2,"data as", "3", 3);
        createTextType(tagpair2, " category");
        createTextType(segment," as an abstract concept for a particular type of information");

        validateJson(segment, testDataPath + "testTextTagTextTagTag-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,16);
        TerminologyData terminologyData1 = setTerminologyData(0, "defines data as", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTextTagTextTagTag-after.json");
    }


    @Test
     public void testTerminologyClosesAtTheSamePositionAsTagPair() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"And ");
        createTagPairType(segment, "stuff", "1", 1);

        validateJson(segment, testDataPath + "testTerminologyClosesAtTheSamePositionAsTagPair-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,9);
        TerminologyData terminologyData1 = setTerminologyData(0,"And stuff", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTerminologyClosesAtTheSamePositionAsTagPair-after.json");

    }


    //defect 488
    @Test
    public void testTerminologyBeginsAtTheSamePositionAsTagPair() throws VisitorException {
        Segment segment = new Segment(null);

        createTagPairType(segment, "And ", "1", 1);
        createTextType(segment, "stuff");

        validateJson(segment, testDataPath + "testTerminologyBeginsAtTheSamePositionAsTagPair-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,9);
        TerminologyData terminologyData1 = setTerminologyData(0,"And stuff", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTerminologyBeginsAtTheSamePositionAsTagPair-after.json");
    }

    @Test
    public void testTerminologyTermEndIndexEqualWithPlaceholder() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"Text ");
        createPlaceholderType(segment, "30", 2);
        createTextType(segment,"then we have a placeholder ");
        createPlaceholderType(segment, "31", 3);
        createTextType(segment," and some text.");

        validateJson(segment, testDataPath + "testTerminologyTermEndIndexEqualWithPlaceholder-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,5);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTerminologyTermEndIndexEqualWithPlaceholder-after.json");
    }

    @Test
    public void testTerminologyMarkupContainsPlaceholderAndTag() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"Text ");
        TagPair tagpair1 = createTagPairType(segment, "1", 1);
        createPlaceholderType(tagpair1, "30", 2);
        createTextType(tagpair1, "then we have a placeholder ");
        createPlaceholderType(segment, "31", 3);
        createTextType(segment, " and some text.");

        validateJson(segment, testDataPath + "testTerminologyMarkupContainsPlaceholderAndTag-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0, 10);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment, testDataPath + "testTerminologyMarkupContainsPlaceholderAndTag-after.json");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData1));
    }

    // placeholderTag should not be included in TerminologyContainer if placed as last index of term
    //@Test
    public void testTerminologyTermEndIndexAfterPlaceholder() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment, "Text ");
        createPlaceholderType(segment, "30", 2);
        createTextType(segment, "then we have a placeholder ");
        createPlaceholderType(segment, "31", 3);
        createTextType(segment, " and some text.");

        validateJson(segment, testDataPath + "testTerminologyTermEndIndexEqualWithPlaceholder-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0, 5);

        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment, testDataPath + "testTerminologyTermEndIndexAfterPlaceholder-after.json");
    }

    // placeholderTag should not be included in TerminologyContainer if placed as last index of term
    //@Test
    public void testTerminologyTermEndIndexAfterSecondPlaceholder() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"Text ");
        TagPair tagpair1 = createTagPairType(segment, "1", 1);
        createPlaceholderType(tagpair1, "30", 2);
        createTextType(tagpair1, "then we have a placeholder ");
        createPlaceholderType(segment, "31", 3);
        createTextType(segment," and some text.");

        validateJson(segment, testDataPath + "testTerminologyMarkupContainsPlaceholderAndTag-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        //setIndexes(0,34);
        setIndexes(0,32);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testTerminologyTermEndIndexAfterSecondPlaceholder.json");
    }

    @Test
    public void testPlaceholdersAndClonedTagPair() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"Text ");
        createPlaceholderType(segment, "30", 2);
        createTextType(segment," then ");
        TagPair tagpair1 = createTagPairType(segment,"we have a placeholder" ,"1", 1);

        createPlaceholderType(tagpair1, "31", 3);
        createTextType(tagpair1," and some text.");

        validateJson(segment, testDataPath + "testPlaceholdersAndClonedTagPair.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0,32);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testPlaceholdersAndClonedTagPair-after.json");
    }


    @Test
    public void testLockedContentOutsideTerminology() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"looked out from the shelter of ");
        LockedContentContainer lockedElement = createLockedType(segment, "1");
        TagPair tagpair1 = createTagPairType(lockedElement,"1", 1);
        createTextType(tagpair1, "Gandalf");
        createTextType(lockedElement,"'s cloak.");

        validateJson(segment, testDataPath + "testLockedContent-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(20,27);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testLockedContent-after.json");
    }

    @Test
    public void testLockedContentInsideTerminology() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"looked out from the shelter of ");
        LockedContentContainer lockedElement = createLockedType(segment, "1");
        TagPair tagpair1 = createTagPairType(lockedElement,"1", 1);
        createTextType(tagpair1,"Gandalf");
        createTextType(lockedElement,"'s cloak.");

        validateJson(segment, testDataPath + "testLockedContent-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(20,27);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testLockedContent-after.json");
    }

    @Test
    public void testPlaceholderBeforeTag() throws VisitorException {
        Segment segment = new Segment(null);

        createPlaceholderType(segment, "30", 1);
        TagPair tagpair1 = createTagPairType(segment, "ITS defines ", "1", 1);
        createTagPairType(tagpair1,"data as category" ,"2", 1);
        createTextType(tagpair1," as an abstract concept");

        validateJson(segment, testDataPath + "testPlaceholderBeforeTag-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0, 17);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testPlaceholderBeforeTag-after.json");
    }

    //@Test // replace json files
    public void testComplexStructure() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment, "Text ");
        createPlaceholderType(segment, "30", 2);
        createTextType(segment, " then ");
        TagPair tagpair1 = createTagPairType(segment, "we have a placeholder", "1", 1);

        createPlaceholderType(tagpair1, "31", 3);
        createTextType(tagpair1," and some text.");

        validateJson(segment, testDataPath + "testMultipleTermsOnSameText-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0, 33);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment,testDataPath + "testMultipleTermsOnSameText-after.json");
    }


    @Test
    public void testTerminologyNotClosingProperly() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment,"Microsoft ");
        TagPair tagpair1 = createTagPairType(segment,"1", 1);
        createTagPairType(tagpair1, "windows", "2", 2);
        createTextType(tagpair1, " is a series of graphical interface operating systems developed, marketed, and sold by Microsoft but ");
        createTagPairType(tagpair1, "Windows is a 1980 thriller", "3", 3);

       // validateJson(segment, testDataPath + "testSimpleCaseClonedTagPairs-before.json" );

        FileSkeleton fileSkeleton = new FileSkeleton();

        //setIndexes(21,22);
        //TerminologyData terminologyData1 = setTerminologyData(0, "a", "termid1", 1);

        //segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        setIndexes(0, 18);
        TerminologyData terminologyData2 = setTerminologyData(1, "Microsoft windows", "termid2", 2);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        //validateJson(segment, testDataPath + "testSimpleCaseClonedTagPairs-after.json");
    }

    @Test
    public void testContainerIdDuplication() throws VisitorException {
        Segment segment = new Segment(null);

        createPlaceholderType(segment, "30", 1);
        TagPair tagpair1 = createTagPairType(segment,"ITS defines " ,"1", 1);
        createTagPairType(tagpair1,"data as category" ,"2", 1);
        createTextType(tagpair1," as an abstract concept");

        validateJson(segment, testDataPath + "testContainerIdDuplication-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(0, 17);
        TerminologyData terminologyData1 = setTerminologyData(0,"Text ", "termid1", 1);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());
        logSegmentAsJson(segment);


        validateJson(segment, testDataPath + "testContainerIdDuplication-after.json");

        // these ids should be different, since they're no longer the same container; (split by terminologyContainer)
        // the validateJson call before should guarantee that this segment structure exists in this form
        String tagPairId1 = segment.getChildren().get(1).getChildren().get(0).getChildren().get(1).getId();
        String tagPairId2 = segment.getChildren().get(1).getChildren().get(1).getId();

        Assert.assertFalse(tagPairId1.equals(tagPairId2));

    }

    private void logSegmentAsJson(Segment segment) {
        // writing segment as a JSON to console
        String serializedSegment = "";
        ObjectWriter writer = objectMapper.writer();
        try {
            serializedSegment = writer.writeValueAsString(segment);
        } catch (IOException e) {
            logger.info("string could not be serialized");
            e.printStackTrace();
        }
        logger.info("Segment json: " + serializedSegment);
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }
}
