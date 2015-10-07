package com.sdl.bcm;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.VisitorException;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class ExtractPlainTextVisitorTest extends TestBase {
    public static Logger logger = Logger.getLogger(ExtractPlainTextVisitorTest.class);
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

        TextMarkup textMarkup = new TextMarkup(segment, "This ");
        segment.getChildren().add(textMarkup);

        RevisionContainer revisionContainer = createRevisionContainer(segment, RevisionType.UNCHANGED, "Gogu", date, "1");
        segment.getChildren().add(revisionContainer);

        TagPair tagPair = new TagPair(revisionContainer);
        tagPair.setId("2");
        revisionContainer.getChildren().add(tagPair);
        textMarkup = new TextMarkup(tagPair, "text ");
        tagPair.getChildren().add(textMarkup);

        textMarkup = new TextMarkup(segment, "is the ");
        segment.getChildren().add(textMarkup);

        revisionContainer = createRevisionContainer(segment, RevisionType.DELETED, "Gogu", date, "1");
        segment.getChildren().add(revisionContainer);

        tagPair = new TagPair(revisionContainer);
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

        checkJSON(segment, testDataPath + "testMultipleRevisions-step1.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        Assert.assertTrue("This text is the second to use for revision terminology annotation.".equals(extractPlainTextVisitor.getText()));
    }

    private RevisionContainer createRevisionContainer(MarkupData parent, RevisionType revisionType, String author, Date timestamp, String containerId) {
        RevisionContainer revisionContainer = new RevisionContainer(parent);
        revisionContainer.setAuthor(author);
        revisionContainer.setRevisionType(revisionType);
        revisionContainer.setTimestamp(timestamp);
        revisionContainer.setId(containerId);
        return revisionContainer;
    }

}
