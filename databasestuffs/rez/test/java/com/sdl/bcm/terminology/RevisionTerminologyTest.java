package com.sdl.bcm.terminology;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.sdl.bcm.TestBase;
import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.RevisionContainer;
import com.sdl.bcm.model.RevisionType;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TagPair;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;

public class RevisionTerminologyTest extends TestBase {

    public static Logger logger = Logger.getLogger(RevisionTerminologyTest.class);
    private static String testDataPath = "src/test/resources/testData/RevisionTerminologyTest/";
    private static Date date;


    static {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, Calendar.JANUARY, 1, 12, 15, 23); //2014-01-01T12:15:23
        date = cal.getTime();
    }

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
    public void testSimpleRevision() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "This text is the ");
        segment.getChildren().add(textMarkup);

        RevisionContainer revisionContainer = createRevisionContainer(segment, RevisionType.DELETED, "Gogu", date, "1");
        segment.getChildren().add(revisionContainer);

        TagPair tagPair = new TagPair(revisionContainer);
        tagPair.setId("2");
        revisionContainer.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "first ");
        tagPair.getChildren().add(textMarkup);

        revisionContainer = createRevisionContainer(segment, RevisionType.INSERTED, "Gogu", date, "3");
        segment.getChildren().add(revisionContainer);

        tagPair = new TagPair(revisionContainer);
        tagPair.setId("4");
        revisionContainer.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "second ");
        tagPair.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "to use for revision terminology annotation.");
        segment.getChildren().add(textMarkup);

        checkJSON(segment, testDataPath + "testSimpleRevision-step1.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        Assert.assertTrue("This text is the second to use for revision terminology annotation.".equals(extractPlainTextVisitor.getText()));

        TerminologyData terminologyData = getTerminologyData("second");
        int[][] indexes = getIndexes(17, 23);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testSimpleRevision-step2.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));

    }

    @Test
    public void testTermSplitByDeletedRevision() throws VisitorException {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "This text is the second ");
        segment.getChildren().add(textMarkup);

        RevisionContainer revisionContainer = createRevisionContainer(segment, RevisionType.DELETED, "Gogu", date, "1");
        segment.getChildren().add(revisionContainer);

        TagPair tagPair = new TagPair(revisionContainer);
        tagPair.setId("1");
        revisionContainer.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "one ");
        tagPair.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "to use for revision terminology annotation.");
        segment.getChildren().add(textMarkup);



        checkJSON(segment, testDataPath + "testTermSplitByDeletedRevision-step1.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        Assert.assertTrue("This text is the second to use for revision terminology annotation.".equals(extractPlainTextVisitor.getText()));

        TerminologyData terminologyData = getTerminologyData("second");
        int[][] indexes = getIndexes(17, 30);

        FileSkeleton fileSkeleton = new FileSkeleton();
        segment.addTerminologyData(indexes[0], indexes[1], terminologyData, fileSkeleton);

        checkJSON(segment, testDataPath + "testTermSplitByDeletedRevision-step2.json", "Segment JSON after annotation:\n");

        Assert.assertTrue(fileSkeleton.getTerminologyData() != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0) != null);
        Assert.assertTrue(fileSkeleton.getTerminologyData().get(0).equals(terminologyData));
    }

    private RevisionContainer createRevisionContainer(MarkupData parent, RevisionType revisionType, String author, Date timestamp, String containerId) {
        RevisionContainer revisionContainer = new RevisionContainer(parent);
        revisionContainer.setAuthor(author);
        revisionContainer.setRevisionType(revisionType);
        revisionContainer.setTimestamp(timestamp);
        revisionContainer.setId(containerId);
        return revisionContainer;
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
