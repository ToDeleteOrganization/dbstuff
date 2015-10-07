package com.sdl.bcm;

import com.sdl.bcm.model.*;
import com.sdl.bcm.model.fileskeleton.CommentDefinition;
import com.sdl.bcm.model.fileskeleton.CommentSeverity;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class FeedbackTest extends TestBase {
    public static Logger logger = Logger.getLogger(FeedbackTest.class);
    private String testDataPath = "src/test/resources/testData/FeedbackTest/";

    private static Date date;

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, Calendar.JANUARY, 1, 12, 15, 23); //2014-01-01T12:15:23
        date = cal.getTime();
    }



    @Override
    public void log(String message) {
        logger.info(message);
    }

    @Test
    public void testFeedbackSerialization() {
        Segment segment = new Segment(null);

        TextMarkup textMarkup = new TextMarkup(segment, "To see ");
        segment.getChildren().add(textMarkup);

        //DELETED FeedbackContainer
        FeedbackContainer feedbackContainer = new FeedbackContainer(segment);
        feedbackContainer.setFeedbackType(FeedbackType.DELETED);
        feedbackContainer.setId("1");
        feedbackContainer.addMetaData("feedback metadata key1", "Feedback metadata value 1");
        feedbackContainer.addMetaData("feedback metadata key2", "Feedback metadata value 2");
        feedbackContainer.setAuthor("Chuck Norris");
        feedbackContainer.setTimestamp(date);
        feedbackContainer.setCategory("feedback category");
        feedbackContainer.setDocumentCategory("Feedback document category");
        feedbackContainer.setSeverity("Feedback severity");
        segment.getChildren().add(feedbackContainer);


        CommentDefinition comment = new CommentDefinition();
        comment.setId(1281);
        comment.setAuthor("Chuck Norris");
        comment.setDate(date);
        comment.setCommentSeverity(CommentSeverity.LOW);
        comment.setText("Deleted Feedback type - This comment belongs to Chuck Norris! Don't change it because if you will, Chuck Norris will also change you!");

        feedbackContainer.setCommentDefinitionId(comment.getId());

        TagPair tagPair1 = new TagPair(feedbackContainer);
        tagPair1.setId("2");
        feedbackContainer.getChildren().add(tagPair1);
        textMarkup = new TextMarkup(tagPair1, "none ");
        tagPair1.getChildren().add(textMarkup);

        //ADDED FeedbackContainer
        feedbackContainer = new FeedbackContainer(segment);
        feedbackContainer.setFeedbackType(FeedbackType.ADDED);
        feedbackContainer.setId("3");
        feedbackContainer.addMetaData("feedback metadata key1", "Feedback metadata value 1");
        feedbackContainer.addMetaData("feedback metadata key2", "Feedback metadata value 2");
        feedbackContainer.setAuthor("Chuck Norris");
        feedbackContainer.setTimestamp(date);
        feedbackContainer.setCategory("feedback category");
        feedbackContainer.setDocumentCategory("Feedback document category");
        feedbackContainer.setSeverity("Feedback severity");
        segment.getChildren().add(feedbackContainer);

        comment = new CommentDefinition();
        comment.setId(1942);
        comment.setAuthor("Chuck Norris");
        comment.setDate(date);
        comment.setCommentSeverity(CommentSeverity.HIGH);
        comment.setText("Added Feedback type - This comment belongs also to Chuck Norris! You should not mess with his comments!");
        feedbackContainer.setCommentDefinitionId(comment.getId());

        TagPair tagPair2 = new TagPair(feedbackContainer);
        tagPair2.setId("4");
        feedbackContainer.getChildren().add(tagPair2);
        textMarkup = new TextMarkup(tagPair2, "all ");
        tagPair2.getChildren().add(textMarkup);

        //COMMENT FeedbackContainer
        feedbackContainer = new FeedbackContainer(segment);
        feedbackContainer.setFeedbackType(FeedbackType.COMMENT);
        feedbackContainer.setId("5");
        feedbackContainer.addMetaData("feedback metadata key1", "Feedback metadata value 1");
        feedbackContainer.addMetaData("feedback metadata key2", "Feedback metadata value 2");
        feedbackContainer.setAuthor("Chuck Norris");
        feedbackContainer.setTimestamp(date);
        feedbackContainer.setCategory("feedback category");
        feedbackContainer.setDocumentCategory("Feedback document category");
        feedbackContainer.setSeverity("Feedback severity");
        segment.getChildren().add(feedbackContainer);

        comment = new CommentDefinition();
        comment.setId(6218);
        comment.setAuthor("Chuck Norris");
        comment.setDate(date);
        comment.setCommentSeverity(CommentSeverity.HIGH);
        comment.setText("Comments Feedback type - This comment belongs to Arnie with approval from Chuck Norris!");
        feedbackContainer.setCommentDefinitionId(comment.getId());

        TagPair tagPair3 = new TagPair(feedbackContainer);
        tagPair3.setId("6");
        textMarkup = new TextMarkup(tagPair3, "letters in alphabet, ");
        tagPair3.getChildren().add(textMarkup);
        feedbackContainer.getChildren().add(tagPair3);


        TagPair tagPair4 = new TagPair(segment);
        tagPair4.setId("7");
        textMarkup = new TextMarkup(tagPair4, "one must use the following sentence: The quick brown fox jumps over the lazy dog!");
        tagPair4.getChildren().add(textMarkup);
        segment.getChildren().add(tagPair4);

        checkJSON(segment, testDataPath + "testFeedbackSerialization-step1.json", "Segment JSON before annotation:\n");

        ExtractPlainTextVisitor extractPlainTextVisitor = new ExtractPlainTextVisitor();
        segment.accept(extractPlainTextVisitor);
        logger.info("Segment Text: " + extractPlainTextVisitor.getText());

        Assert.assertTrue(extractPlainTextVisitor.getText().equals("To see all letters in alphabet, one must use the following sentence: The quick brown fox jumps over the lazy dog!"));
    }


}
