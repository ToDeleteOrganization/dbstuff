package com.sdl.bcm.terminology;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sdl.bcm.TestBase;
import com.sdl.bcm.model.CommentContainer;
import com.sdl.bcm.model.LockedContentContainer;
import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.Paragraph;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.PlaceholderTag;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;
import com.sdl.bcm.model.TerminologyAnnotationContainer;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.utils.Utils;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;

public class AddTerminologyTest extends TestBase {

    public static Logger logger = Logger.getLogger(AddTerminologyTest.class);
    private static String testDataPath = "src/test/resources/testData/AddTerminologyTest/";

    @Override
    public void before() {
        super.before();
    }

    @Override
    public void after() {
        super.after();
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }

    @Test
    public void testAnnotationSpansOverMultipleTagPairs() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "To see ");
        segment.getChildren().add(textMarkup);

        TagPair tagPair1 = new TagPair(segment);
        tagPair1.setId("1");
        segment.getChildren().add(tagPair1);
        textMarkup = new TextMarkup(tagPair1, "all ");
        tagPair1.getChildren().add(textMarkup);

        TagPair tagPair2 = new TagPair(tagPair1);
        tagPair2.setId("2");
        tagPair1.getChildren().add(tagPair2);
        textMarkup = new TextMarkup(tagPair2, "letters in alphabet, ");
        tagPair2.getChildren().add(textMarkup);

        TagPair tagPair3 = new TagPair(tagPair2);
        tagPair3.setId("3");
        tagPair2.getChildren().add(tagPair3);
        textMarkup = new TextMarkup(tagPair3, "one must use the following sentence: The ");
        tagPair3.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair2, "quick ");
        tagPair2.getChildren().add(textMarkup);

        TagPair tagPair4 = new TagPair(tagPair1);
        tagPair4.setId("4");
        tagPair1.getChildren().add(tagPair4);
        textMarkup = new TextMarkup(tagPair4, "brown ");
        tagPair4.getChildren().add(textMarkup);

        TagPair tagPair5 = new TagPair(tagPair4);
        tagPair5.setId("5");
        tagPair4.getChildren().add(tagPair5);
        textMarkup = new TextMarkup(tagPair5, "fox ");
        tagPair5.getChildren().add(textMarkup);

        TagPair tagPair6 = new TagPair(tagPair5);
        tagPair6.setId("6");
        tagPair5.getChildren().add(tagPair6);
        textMarkup = new TextMarkup(tagPair6, "jumps ");
        tagPair6.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair4, "over ");
        tagPair4.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair1, "the ");
        tagPair1.getChildren().add(textMarkup);

        TagPair tagPair7 = new TagPair(segment);
        tagPair7.setId("7");
        segment.getChildren().add(tagPair7);
        textMarkup = new TextMarkup(tagPair7, "lazy ");
        tagPair7.getChildren().add(textMarkup);

        TagPair tagPair8 = new TagPair(tagPair7);
        tagPair8.setId("8");
        tagPair7.getChildren().add(tagPair8);
        textMarkup = new TextMarkup(tagPair8, "sleepy ");
        tagPair8.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "dog. These are true wise words!");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotationSpansOverMultipleTagPairs-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("The quick brown fox jumps over the lazy sleepy dog.");
        int[][] indexes = getIndexes(69, 120);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testAnnotationSpansOverMultipleTagPairs-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
    }

    @Test
    public void testAnnotationEndsInTagPair() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "To see ");
        segment.getChildren().add(textMarkup);

        TagPair tagPair1 = new TagPair(segment);
        tagPair1.setId("1");
        segment.getChildren().add(tagPair1);
        textMarkup = new TextMarkup(tagPair1, "all ");
        tagPair1.getChildren().add(textMarkup);

        TagPair tagPair2 = new TagPair(tagPair1);
        tagPair2.setId("2");
        tagPair1.getChildren().add(tagPair2);
        textMarkup = new TextMarkup(tagPair2, "letters in alphabet, ");
        tagPair2.getChildren().add(textMarkup);

        TagPair tagPair3 = new TagPair(tagPair2);
        tagPair3.setId("3");
        tagPair2.getChildren().add(tagPair3);
        textMarkup = new TextMarkup(tagPair3, "one must use the following sentence: The ");
        tagPair3.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair2, "quick ");
        tagPair2.getChildren().add(textMarkup);

        TagPair tagPair4 = new TagPair(tagPair1);
        tagPair4.setId("4");
        tagPair1.getChildren().add(tagPair4);
        textMarkup = new TextMarkup(tagPair4, "brown ");
        tagPair4.getChildren().add(textMarkup);

        TagPair tagPair5 = new TagPair(tagPair4);
        tagPair5.setId("5");
        tagPair4.getChildren().add(tagPair5);
        textMarkup = new TextMarkup(tagPair5, "fox ");
        tagPair5.getChildren().add(textMarkup);

        TagPair tagPair6 = new TagPair(tagPair5);
        tagPair6.setId("6");
        tagPair5.getChildren().add(tagPair6);
        textMarkup = new TextMarkup(tagPair6, "jumps ");
        tagPair6.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair4, "over ");
        tagPair4.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair1, "the ");
        tagPair1.getChildren().add(textMarkup);

        TagPair tagPair7 = new TagPair(segment);
        tagPair7.setId("7");
        segment.getChildren().add(tagPair7);
        textMarkup = new TextMarkup(tagPair7, "lazy ");
        tagPair7.getChildren().add(textMarkup);

        TagPair tagPair8 = new TagPair(tagPair7);
        tagPair8.setId("8");
        tagPair7.getChildren().add(tagPair8);
        textMarkup = new TextMarkup(tagPair8, "sleepy ");
        tagPair8.getChildren().add(textMarkup);

        TagPair tagPair9 = new TagPair(segment);
        tagPair9.setId("9");
        segment.getChildren().add(tagPair9);
        textMarkup = new TextMarkup(tagPair9, "dog. These are true wise words!");
        tagPair9.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotationEndsInTagPair-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("The quick brown fox jumps over the lazy sleepy dog.");
        int[][] indexes = getIndexes(69, 120);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testAnnotationEndsInTagPair-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
    }

    @Test
    public void testAnnotationAtSegmentEnd() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "To see ");
        segment.getChildren().add(textMarkup);

        TagPair tagPair1 = new TagPair(segment);
        tagPair1.setId("1");
        segment.getChildren().add(tagPair1);
        textMarkup = new TextMarkup(tagPair1, "all ");
        tagPair1.getChildren().add(textMarkup);

        TagPair tagPair2 = new TagPair(tagPair1);
        tagPair2.setId("2");
        tagPair1.getChildren().add(tagPair2);
        textMarkup = new TextMarkup(tagPair2, "letters in alphabet, ");
        tagPair2.getChildren().add(textMarkup);

        TagPair tagPair3 = new TagPair(tagPair2);
        tagPair3.setId("3");
        tagPair2.getChildren().add(tagPair3);
        textMarkup = new TextMarkup(tagPair3, "one must use the following sentence: The ");
        tagPair3.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair2, "quick ");
        tagPair2.getChildren().add(textMarkup);

        TagPair tagPair4 = new TagPair(tagPair1);
        tagPair4.setId("4");
        tagPair1.getChildren().add(tagPair4);
        textMarkup = new TextMarkup(tagPair4, "brown ");
        tagPair4.getChildren().add(textMarkup);

        TagPair tagPair5 = new TagPair(tagPair4);
        tagPair5.setId("5");
        tagPair4.getChildren().add(tagPair5);
        textMarkup = new TextMarkup(tagPair5, "fox ");
        tagPair5.getChildren().add(textMarkup);

        TagPair tagPair6 = new TagPair(tagPair5);
        tagPair6.setId("6");
        tagPair5.getChildren().add(tagPair6);
        textMarkup = new TextMarkup(tagPair6, "jumps ");
        tagPair6.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair4, "over ");
        tagPair4.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair1, "the ");
        tagPair1.getChildren().add(textMarkup);

        TagPair tagPair7 = new TagPair(segment);
        tagPair7.setId("7");
        segment.getChildren().add(tagPair7);
        textMarkup = new TextMarkup(tagPair7, "lazy ");
        tagPair7.getChildren().add(textMarkup);

        TagPair tagPair8 = new TagPair(tagPair7);
        tagPair8.setId("8");
        tagPair7.getChildren().add(tagPair8);
        textMarkup = new TextMarkup(tagPair8, "sleepy ");
        tagPair8.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "dog.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotationAtSegmentEnd-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("The quick brown fox jumps over the lazy sleepy dog.");
        int[][] indexes = getIndexes(69, 120);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testAnnotationAtSegmentEnd-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
    }

    @Test
    public void testAnnotationAtSegmentEnd_LCC_460() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "And ");
        segment.getChildren().add(textMarkup);

        TagPair tagPair1 = new TagPair(segment);
        tagPair1.setId("1");
        segment.getChildren().add(tagPair1);
        textMarkup = new TextMarkup(tagPair1, "stuff");
        tagPair1.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotationAtSegmentEnd_LCC_460-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("stuff");
        int[][] indexes = getIndexes(4, 9);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testAnnotationAtSegmentEnd_LCC_460-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
    }

    @Test
    public void testAnnotateTextMarkup() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "SQL Server is a database provider.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotateTextMarkup-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("database provider");
        int[][] indexes = getIndexes(16, 33);
        FileSkeleton fileSkeleton = new FileSkeleton();

        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testAnnotateTextMarkup-after.json", "Segment JSON after annotation:\n");
        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
    }

    @Test
    public void testAnnotateTextMarkupTwice() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "SQL Server is a database provider.");
        segment.getChildren().add(textMarkup);

        String dataReferenceBefore = Utils.readFile(testDataPath + "testAnnotateTextMarkup-before.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String segmentJSON = objectMapper.writeValueAsString(segment);
            logger.info("Segment JSON before annotation:\n" + segmentJSON);
            Assert.assertTrue(segmentJSON.equals(dataReferenceBefore));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("database provider");
        int[][] indexes = getIndexes(16, 33);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testAnnotateTextMarkup-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));

        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testAnnotateTextMarkup-secondAnnotation-after.json", "Segment JSON after second annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
    }

    @Test
    public void testAnnotateTextMarkupIndexExceedsSegmentBoundaries() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "SQL Server is a database provider.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotateTextMarkup-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("database provider");
        int[][] indexes = getIndexes(16, 40);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation end indexes exceeds current segment text: 40"));
        }

        checkJSON(segment, testDataPath + "testAnnotateTextMarkup-before.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() == null);
    }

    @Test
    public void testAnnotateSegmentWithLockedContainer() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "In my sentence I have ASP ");
        segment.getChildren().add(textMarkup);

        LockedContentContainer lockedContainer = new LockedContentContainer(segment);
        lockedContainer.setId("1");
        segment.getChildren().add(lockedContainer);

        TagPair tagPair = new TagPair(lockedContainer);
        tagPair.setId("2");
        lockedContainer.getChildren().add(tagPair);

        textMarkup = new TextMarkup(tagPair, "is a technology used on ");
        tagPair.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "server");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotateSegmentWithLockedContainer-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("is a technology used on ");
        int[][] indexes = getIndexes(22, 32);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation end indexes exceeds current segment text: 49"));
        }

        checkJSON(segment, testDataPath + "testAnnotateSegmentWithLockedContainer-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
    }


    @Test
    public void testAnnotateSegmentWithLockedContainerExceedsBoundaries() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "In my sentence I have ASP ");
        segment.getChildren().add(textMarkup);

        LockedContentContainer lockedContainer = new LockedContentContainer(segment);
        lockedContainer.setId("1");
        segment.getChildren().add(lockedContainer);

        TagPair tagPair = new TagPair(lockedContainer);
        tagPair.setId("2");
        lockedContainer.getChildren().add(tagPair);

        textMarkup = new TextMarkup(tagPair, "is a technology used on ");
        tagPair.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "server");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testAnnotateSegmentWithLockedContainer-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("is a technology used on ");
        int[][] indexes = getIndexes(26, 49);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            Assert.assertTrue(e.getMessage().equals("Annotation end indexes exceeds current segment text: 49"));
        }

        Assert.assertTrue(fileSkeleton.getTerminologyData() == null);
    }


    @Test
    public void testAnnotateSegmentWithLockedContainer_LCC_450() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "In my sentence I have ASP ");
        segment.getChildren().add(textMarkup);

        LockedContentContainer lockedContainer = new LockedContentContainer(segment);
        lockedContainer.setId("1");
        segment.getChildren().add(lockedContainer);

        TagPair tagPair = new TagPair(lockedContainer);
        tagPair.setId("2");
        lockedContainer.getChildren().add(tagPair);

        textMarkup = new TextMarkup(tagPair, "is a technology used on ");
        tagPair.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "server");
        segment.getChildren().add(textMarkup);


        checkJSON(segment, testDataPath + "testAnnotateSegmentWithLockedContainer-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("is a technology used on ");
        int[][] indexes = getIndexes(22, 32);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testAnnotateSegmentWithLockedContainer-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
    }

    @Test
    public void testSplitTerminologyContainer() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "This ");
        segment.getChildren().add(textMarkup);

        TerminologyAnnotationContainer t1 = new TerminologyAnnotationContainer(segment);
        t1.setId("1");
        t1.setAnnotationId(1);
        t1.setTerminologyDataId(1);
        segment.getChildren().add(t1);

        textMarkup = new TextMarkup(t1, "text is supposed ");
        t1.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "to test ");
        segment.getChildren().add(textMarkup);

        TerminologyAnnotationContainer t2 = new TerminologyAnnotationContainer(segment);
        t2.setId("2");
        t2.setAnnotationId(2);
        t2.setTerminologyDataId(2);
        segment.getChildren().add(t2);

        textMarkup = new TextMarkup(t2, "terminology");
        t2.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, " splitting.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testSplitTerminologyContainerLCC-481-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("supposed to test terminology");
        int[][] indexes = getIndexes(13, 41);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testSplitTerminologyContainerLCC-481-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
    }

    @Test
    public void testOverlappingTerms_LCC_362() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "My SQL Server is a database provider.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testOverlappingTerms_LCC_362-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("SQL Server is a database");
        int[][] indexes = getIndexes(3, 27);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testOverlappingTerms_LCC_362-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().size() == 1);

        terminologyData = getTerminologyData("database provider");
        indexes = getIndexes(19, 36);

        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testOverlappingTerms_LCC_362-after1.json", "Segment JSON after annotation:\n");
        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().size() == 2);
    }

    @Test
    public void testSpecialCharacters_LCC_524() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "This is a test Copyright© and this is a test Trademark™");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testSpecialCharacters_LCC_524-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("©");
        int[][] indexes = getIndexes(24, 25);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        terminologyData = getTerminologyData("™");
        indexes = getIndexes(54, 55);

        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testSpecialCharacters_LCC_524-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().size() == 2);
    }

    @Test
    public void testNestedTerminologyAnnotations_LCC_483() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "This is a new Terminology test.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testNestedTerminologyAnnotations_LCC_483-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("Terminology test");
        int[][] indexes = getIndexes(10, 30);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        terminologyData = getTerminologyData("Terminology");
        indexes = getIndexes(14, 25);

        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testNestedTerminologyAnnotations_LCC_483-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().size() == 2);
    }

    @Test
    public void testNestedTerminologyAnnotations_WithSameStartIndex_LCC_483() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "This is a new Terminology test.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testNestedTerminologyAnnotations_LCC_483-before.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("Terminology test");
        int[][] indexes = getIndexes(14, 30);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        terminologyData = getTerminologyData("Terminology");
        indexes = getIndexes(14, 25);

        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testNestedTerminologyAnnotations_WithSameStartIndex_LCC_483-after.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().size() == 2);
    }

    @Test
    public void testCloneTags() throws IOException {
        Segment segment = new Segment(null);

        TagPair tagPair1 = new TagPair(segment);
        tagPair1.setId("1");
        tagPair1.setTagPairDefinitionId(1);
        segment.getChildren().add(tagPair1);

        TagPair tagPair2 = new TagPair(tagPair1);
        tagPair2.setId("2");
        tagPair2.setTagPairDefinitionId(2);
        tagPair1.getChildren().add(tagPair2);

        TagPair tagPair3 = new TagPair(tagPair2);
        tagPair3.setId("3");
        tagPair3.setTagPairDefinitionId(3);
        tagPair2.getChildren().add(tagPair3);

        TextMarkup textMarkup = new TextMarkup(tagPair3, "Terminology ");
        tagPair3.getChildren().add(textMarkup);

        TagPair tagPair4 = new TagPair(segment);
        tagPair4.setId("4");
        tagPair4.setTagPairDefinitionId(4);
        segment.getChildren().add(tagPair4);

        textMarkup = new TextMarkup(tagPair4, "test");
        tagPair4.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testCloneTags-before.json", "Segment JSON after annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("Terminology test");
        int[][] indexes = getIndexes(0, 16);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testCloneTags-after.json", "Segment JSON after annotation:\n");
    }

    // placeholderTag should not be part of termContainer, as decided
    //@Test
    public void testPlaceholder_LCC_490() throws IOException {
        Segment segment = new Segment(null);

        TagPair tagPair = new TagPair(segment);
        tagPair.setId("1");
        segment.getChildren().add(tagPair);

        TextMarkup textMarkup = new TextMarkup(tagPair, "Placeholder terminology");
        tagPair.getChildren().add(textMarkup);

        PlaceholderTag placeholderTag = new PlaceholderTag(tagPair);
        tagPair.getChildren().add(placeholderTag);

        placeholderTag = new PlaceholderTag(segment);
        segment.getChildren().add(placeholderTag);

        textMarkup = new TextMarkup(segment, "test.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testPlaceholder_LCC_490-before.json", "Segment JSON after annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("Terminology test");
        //int[][] indexes = getIndexes(0, 24);
        int[][] indexes = getIndexes(0, 23);

        FileSkeleton fileSkeleton = new FileSkeleton();
        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "testPlaceholder_LCC_490-after.json", "Segment JSON after annotation:\n");
    }

    @Test
    public void testGetPlainTextSourceFromPU() throws IOException {

        ParagraphUnit paragraphUnit = new ParagraphUnit();
        Paragraph sourceParagraph = new Paragraph(paragraphUnit);
        Paragraph targetParagraph = new Paragraph(paragraphUnit);
        paragraphUnit.setSource(sourceParagraph);
        paragraphUnit.setTarget(targetParagraph);

        LinkedList<MarkupData> sourceChildren = new LinkedList<>();
        Segment segment = new Segment(sourceParagraph);
        TextMarkup textMarkup = new TextMarkup(segment, "The horizon ");
        segment.getChildren().add(textMarkup);
        TagPair tagPair = new TagPair(segment);
        tagPair.setId("1");
        segment.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "leans forward, ");
        tagPair.getChildren().add(textMarkup);
        textMarkup = new TextMarkup(segment, "offering space");
        segment.getChildren().add(textMarkup);
        sourceChildren.add(segment);

        textMarkup = new TextMarkup(null, " ");
        sourceChildren.add(textMarkup);

        segment = new Segment(sourceParagraph);
        textMarkup = new TextMarkup(segment, "to place new steps ");
        segment.getChildren().add(textMarkup);
        tagPair = new TagPair(segment);
        tagPair.setId("1");
        segment.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "of change ");
        tagPair.getChildren().add(textMarkup);
        textMarkup = new TextMarkup(segment, "into the future");
        segment.getChildren().add(textMarkup);
        sourceChildren.add(segment);

        textMarkup = new TextMarkup(sourceParagraph, ".");
        sourceChildren.add(textMarkup);

        sourceParagraph.setChildren(sourceChildren);

        LinkedList<MarkupData> targetChildren = new LinkedList<>();
        segment = new Segment(targetParagraph);
        textMarkup = new TextMarkup(segment, "L'horizon ");
        segment.getChildren().add(textMarkup);
        tagPair = new TagPair(segment);
        tagPair.setId("1");
        segment.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "se penche en avant, ");
        tagPair.getChildren().add(textMarkup);
        textMarkup = new TextMarkup(segment, "offre de l'espace");
        segment.getChildren().add(textMarkup);
        targetChildren.add(segment);

        textMarkup = new TextMarkup(targetParagraph, " ");
        targetChildren.add(textMarkup);

        segment = new Segment(targetParagraph);
        textMarkup = new TextMarkup(segment, "pour placer la nouvelle procédure ");
        segment.getChildren().add(textMarkup);
        tagPair = new TagPair(segment);
        tagPair.setId("1");
        segment.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "de modification ");
        tagPair.getChildren().add(textMarkup);
        textMarkup = new TextMarkup(segment, "dans l'avenir");
        segment.getChildren().add(textMarkup);
        targetChildren.add(segment);

        textMarkup = new TextMarkup(targetParagraph, ".");
        targetChildren.add(textMarkup);

        targetParagraph.setChildren(targetChildren);

        String sourceText = "The horizon leans forward, offering space to place new steps of change into the future.";
        String targetText = "L'horizon se penche en avant, offre de l'espace pour placer la nouvelle procédure de modification dans l'avenir.";

        String sourceTextExtracted = paragraphUnit.getPlainText(ParagraphUnit.TargetParagraph.SOURCE_PARAGRAPH);
        Assert.assertTrue(sourceText.equals(sourceTextExtracted));

        String targetTextExtracted = paragraphUnit.getPlainText(ParagraphUnit.TargetParagraph.TARGET_PARAGRAPH);
        Assert.assertTrue(targetText.equals(targetTextExtracted));

    }

    @Test
    public void test_LCC_639() throws IOException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "ITS defines ");
        segment.getChildren().add(textMarkup);

        TagPair tagPair1 = new TagPair(segment);
        tagPair1.setId("1");
        tagPair1.setTagPairDefinitionId(1);
        segment.getChildren().add(tagPair1);

        TagPair tagPair2 = new TagPair(tagPair1);
        tagPair2.setId("2");
        tagPair2.setTagPairDefinitionId(2);
        tagPair1.getChildren().add(tagPair2);

        textMarkup = new TextMarkup(tagPair2, "data as category");
        tagPair2.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(tagPair1, " as an abstract concept ");
        tagPair1.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "for a particular type of information");
        segment.getChildren().add(textMarkup);


        checkJSON(segment, testDataPath + "test_LCC_639_1.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("Terminology test");
        int[][] indexes = getIndexes(0, 28);

        FileSkeleton fileSkeleton = new FileSkeleton();

        try {
            segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        } catch (VisitorException e) {
            e.printStackTrace();
        }

        checkJSON(segment, testDataPath + "test_LCC_639_2.json", "Segment JSON after annotation:\n");
    }

    @Test
    public void test_LCC_1995() throws IOException, VisitorException {
        String json = Utils.readFile(testDataPath + "test_LCC_1995_AboutWithStudioComments.json");

        ObjectReader reader = objectMapper.reader(Segment.class);
        Segment segment = reader.readValue(json);

        TerminologyData terminologyData = getTerminologyData("Warranty");
        int[][] indexes = getIndexes(0, 8);
        FileSkeleton fileSkeleton = new FileSkeleton();

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        Assert.assertNotNull(segment.getChildren());
        Assert.assertTrue(segment.getChildren().get(0) instanceof CommentContainer);

        CommentContainer commentContainer = (CommentContainer) segment.getChildren().get(0);

        Assert.assertTrue(commentContainer.getCommentDefinitionId().equals(1));

        checkJSON(segment, testDataPath + "test_LCC_1995_AboutWithStudioComments_after.json", "Segment JSON after annotation:\n");
    }

    @Test
    public void test_LCC_1995_2() throws IOException, VisitorException {
        String json = Utils.readFile(testDataPath + "test_LCC_1995_TextComm_before.json");

        ObjectReader reader = objectMapper.reader(Segment.class);
        Segment segment = reader.readValue(json);

        TerminologyData terminologyData = getTerminologyData("ITS defines");
        int[][] indexes = getIndexes(0, 12);
        FileSkeleton fileSkeleton = new FileSkeleton();

        logSegmentAsJson(segment);
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);
        logSegmentAsJson(segment);

        checkJSON(segment, testDataPath + "test_LCC_1995_TextComm_after.json", "Segment JSON after annotation:\n");
    }

    @Test
    public void test_LCC_821() throws IOException, VisitorException {

        String json = Utils.readFile(testDataPath + "test_LCC_821_PlaceholderStartingSegment_before.json");
        ObjectReader reader = objectMapper.reader(Segment.class);
        Segment segment = reader.readValue(json);

        TerminologyData terminologyData = getTerminologyData("xxxxxx");
        int[][] indexes = getIndexes(0, 6);
        FileSkeleton fileSkeleton = new FileSkeleton();

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        Assert.assertNotNull(segment.getChildren());

        ObjectWriter writer = objectMapper.writerFor(Segment.class);
        String segmentWithTerminology = writer.writeValueAsString(segment);
        logger.info("Segment Json before: " + json);
        logger.info("Segment Json after : " + segmentWithTerminology);
        checkJSON(segment, testDataPath + "test_LCC_821_PlaceholderStartingSegment_after.json", "Segment JSON after annotation:\n");

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

    private static TerminologyData getTerminologyData(String text) {
        TerminologyData terminologyData = new TerminologyData();
        terminologyData.setId(1);
        terminologyData.setOrigin("Terminology JUnit Test");

        List<Term> terms = new LinkedList<Term>();
        Term term = new Term();
        term.setId("termId1");
        term.setScore(1);
        term.setText(text);
        terms.add(term);

        terminologyData.setTerms(terms);
        return terminologyData;
    }

    private static int[][] getIndexes(int... indexes) {
        int[][] result = new int[2][indexes.length / 2];
        int indexCount = 0;
        for (int i = 0; i < indexes.length; i++) {
            result[0][indexCount] = indexes[i++];
            result[1][indexCount++] = indexes[i];
        }
        return result;
    }
}
