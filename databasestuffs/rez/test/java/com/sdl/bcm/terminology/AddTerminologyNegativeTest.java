package com.sdl.bcm.terminology;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.bcm.TestBase;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.utils.Utils;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;


public class AddTerminologyNegativeTest extends TestBase {
    public static Logger logger = Logger.getLogger(AddTerminologyNegativeTest.class);
    private String testDataPath = "src/test/resources/testData/testDataAddTerminologyNegativeTest/";
    int[] startIndexes = new int[1];
    int[] endIndexes = new int[1];

    private void createTextType(Segment segment, String text) {
        TextMarkup textMarkup = new TextMarkup(segment, text);
        segment.getChildren().add(textMarkup);
    }

    private TagPair createTagPairType(Segment segment, String text, String tagPairId, int tagPairDefId) {
        TagPair tagPair = new TagPair(segment);
        tagPair.setId(tagPairId);
        tagPair.setTagPairDefinitionId(tagPairDefId);
        segment.getChildren().add(tagPair);
        TextMarkup textMarkup = new TextMarkup(tagPair, text);
        tagPair.getChildren().add(textMarkup);
        return tagPair;
    }

    private TagPair createTagPairType(TagPair parentTagPair, String text, String tagPairId, int tagPairDefId) {
        TagPair tagPair = new TagPair(parentTagPair);
        tagPair.setId(tagPairId);
        tagPair.setTagPairDefinitionId(tagPairDefId);
        parentTagPair.getChildren().add(tagPair);
        TextMarkup textMarkup = new TextMarkup(tagPair, text);
        tagPair.getChildren().add(textMarkup);
        return tagPair;
    }

    private void setIndexes(Integer startIndex, Integer endIndex) {
        startIndexes[0] = startIndex;
        endIndexes[0] = endIndex;
    }


    private void checkJsonBeforeAnnotation(Segment segment) {
        //        String dataReferenceBefore = Utils.readFile("src/test/resources/testData/testAnnotationEndsInTagPair-before.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String segmentJSON = objectMapper.writeValueAsString(segment);
            logger.info("Segment JSON before annotation:\n" + segmentJSON);
//            Assert.assertTrue(segmentJSON.equals(dataReferenceBefore));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());
    }


    private void validateJson(Segment segment, String jsonFilePath) {
        //        String dataReferenceBefore = Utils.readFile("src/test/resources/testData/testAnnotationEndsInTagPair-before.json");
        String dataReferenceBefore = Utils.readFile(jsonFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        //String segmentJSON;
        try {
//            String segmentJSON = objectMapper.writeValueAsString(segment);
//            logger.info("Segment JSON:\n" + segmentJSON);
            Segment reference = objectMapper.readValue(dataReferenceBefore, Segment.class);
            Assert.assertTrue(segment.equals(reference));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testTerminologyMarkupStartIndex() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment, "This is a bit more rich.");

        validateJson(segment, testDataPath + "PlainText.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        String message = "";

        setIndexes(-1, 2);
        TerminologyData terminologyData1 = setTerminologyData(0, "This is a bit more rich.", "termid1", 1);
        try {
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);
        } catch (VisitorException e) {
            message = e.toString();
            logger.info(message);
        }

        Assert.assertTrue(message.contains("Annotation start indexes exceeds current segment text: -1"));

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment, testDataPath + "PlainText.json");

    }


    @Test
    public void testTerminologyMarkupInvalidIndexes() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment, "This is a bit more rich.");

        validateJson(segment, testDataPath + "PlainText.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        setIndexes(10, 5);
        TerminologyData terminologyData1 = setTerminologyData(0, "This is a bit more rich.", "termid1", 1);

        String message = "";
        try {
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);
        } catch (VisitorException e) {
            message = e.toString();
            logger.info(message);
        }

        Assert.assertTrue(message.contains("Annotation start index is lower than end index: 5"));

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        validateJson(segment, testDataPath + "PlainText.json");

    }


    @Test
    public void testTerminologyMarkupInvalidEndIndex() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment, "And ");
        createTagPairType(segment, "stuff", "1", 1);

        validateJson(segment, testDataPath + "OneTagsPair.json");

        FileSkeleton fileSkeleton = new FileSkeleton();
        setIndexes(0, 10);
        TerminologyData terminologyData1 = setTerminologyData(0, "And stuff", "termid1", 1);

        String message = "";
        try {
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);
        } catch (VisitorException e) {
            //e.printStackTrace();
            message = e.toString();
            logger.info(message);
        }

        Assert.assertTrue(message.contains("Annotation end indexes exceeds current segment text: 10"));

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        //check that the segment is not changed
        validateJson(segment, testDataPath + "OneTagsPair.json");

    }


    //@Test
    public void testAnnotationOnPlainText() throws VisitorException {
        Segment segment = new Segment(null);

        createTextType(segment, "With a couple of paragraphs.");

        //checkJsonBeforeAnnotation(segment);
        validateJson(segment, "src/test/resources/testData/testMultipleAnnotationOnPlainText-before.json");

        FileSkeleton fileSkeleton = new FileSkeleton();

        TerminologyData terminologyData = getTerminologyData("With a couple");
        setIndexes(0, 13);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData, fileSkeleton);

        //second terminology
        TerminologyData terminologyData2 = getTerminologyData("paragraphs");
        setIndexes(17, 27);

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);

        //third terminology
        TerminologyData terminologyData3 = getTerminologyData("couple of paragraphs");
        setIndexes(7, 27);

        //validateJson(segment, "src/test/resources/testData/testMultipleAnnotationOnPlainText-after.json");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData3, fileSkeleton);

        validateJson(segment, "src/test/resources/testData/testMultipleAnnotationOnPlainText-after.json");
    }

    //@Test
    public void testAnnotationInitial() throws VisitorException {
        Segment segment = new Segment(null);

        //TextMarkup textMarkup = new TextMarkup("With a couple of paragraphs.");
        //segment.getChildren().add(textMarkup);
        createTextType(segment, "With a couple of paragraphs.");

//        String dataReferenceBefore = Utils.readFile("src/test/resources/testData/testAnnotationEndsInTagPair-before.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String segmentJSON = objectMapper.writeValueAsString(segment);
            logger.info("Segment JSON before annotation:\n" + segmentJSON);
//            Assert.assertTrue(segmentJSON.equals(dataReferenceBefore));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        TerminologyData terminologyData = getTerminologyData("With a couple");
        //int[] startIndexes = new int[1];
        //startIndexes[0] = 0;
        //int[] endIndexes = new int[1];
        //endIndexes[0] = 13;
        setIndexes(0, 13);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData, fileSkeleton);

        //second terminology
        TerminologyData terminologyData2 = getTerminologyData("paragraphs");
        startIndexes[0] = 17;
        endIndexes[0] = 27;

        segment.addTerminologyData(startIndexes, endIndexes, terminologyData2, fileSkeleton);

        //second terminology
        TerminologyData terminologyData3 = getTerminologyData("couple of paragraphs");
        startIndexes[0] = 7;
        endIndexes[0] = 27;

        //FileSkeleton fileSkeleton = new FileSkeleton();

//        AddTerminologyVisitor terminologyVisitor = new AddTerminologyVisitor(startIndexes, endIndexes, terminologyData, segment, fileSkeleton);
        segment.addTerminologyData(startIndexes, endIndexes, terminologyData3, fileSkeleton);

//        segment.accept(terminologyVisitor);

//        printVisitor.clean();
//        segment.accept(printVisitor);
//        System.out.println(printVisitor.getStringStructure());

//        String dataReferenceAfter= Utils.readFile("src/test/resources/testData/testAnnotationEndsInTagPair-after.json");

        try {
            String segmentJSON = objectMapper.writeValueAsString(segment);
            logger.info("Segment JSON after annotation:\n" + segmentJSON);
//            Assert.assertTrue(segmentJSON.equals(dataReferenceAfter));

            Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
            Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
            Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void log(String message) {
        logger.info(message);
    }
}
