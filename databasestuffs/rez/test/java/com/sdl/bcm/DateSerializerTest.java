package com.sdl.bcm;

import com.sdl.bcm.model.fileskeleton.CommentDefinition;
import com.sdl.bcm.model.fileskeleton.CommentSeverity;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializerTest extends TestBase {
    public static Logger logger = Logger.getLogger(DateSerializerTest.class);

    @Override
    public void log(String message) {
        logger.info(message);
    }

    @Test
    public void testDateDeserializerWithTimeZoneAndNanoseconds() {
        String dateString = "2014-06-16T12:30:10.8345899+03:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomJsonDateDeserializer.DATE_FORMAT_WITH_TIMEZONE);
        CustomJsonDateDeserializer dateDeserializer = new CustomJsonDateDeserializer();
        Date date = dateDeserializer.parseDateString(dateString);
        String newDateString = dateFormat.format(date);
        Assert.isTrue("2014-06-16T12:30:10+03:00".equals(newDateString));
    }

    @Test
    public void testDateDeserializerWithTimeZoneAndNanoseconds2() {
        String dateString = "2014-06-16T12:30:10.834589+03:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomJsonDateDeserializer.DATE_FORMAT_WITH_TIMEZONE);
        CustomJsonDateDeserializer dateDeserializer = new CustomJsonDateDeserializer();
        Date date = dateDeserializer.parseDateString(dateString);
        String newDateString = dateFormat.format(date);
        Assert.isTrue("2014-06-16T12:30:10+03:00".equals(newDateString));
    }

    @Test
    public void testDateDeserializerWithTimeZoneAndNanoseconds3() {
        String dateString = "2014-06-16T12:30:10.83458+03:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomJsonDateDeserializer.DATE_FORMAT_WITH_TIMEZONE);
        CustomJsonDateDeserializer dateDeserializer = new CustomJsonDateDeserializer();
        Date date = dateDeserializer.parseDateString(dateString);
        String newDateString = dateFormat.format(date);
        Assert.isTrue("2014-06-16T12:30:10+03:00".equals(newDateString));
    }

    @Test
    public void testDateDeserializerWithTimeZoneAndNanoseconds4() {
        String dateString = "2014-06-16T12:30:10.8+03:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomJsonDateDeserializer.DATE_FORMAT_WITH_TIMEZONE);
        CustomJsonDateDeserializer dateDeserializer = new CustomJsonDateDeserializer();
        Date date = dateDeserializer.parseDateString(dateString);
        String newDateString = dateFormat.format(date);
        Assert.isTrue("2014-06-16T12:30:10+03:00".equals(newDateString));
    }

    @Test
    public void testDateDeserializer() {
        String dateString = "2014-06-16T12:30:10";
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomJsonDateDeserializer.DATE_FORMAT_WITH_TIMEZONE);
        CustomJsonDateDeserializer dateDeserializer = new CustomJsonDateDeserializer();
        Date date = dateDeserializer.parseDateString(dateString);
        String newDateString = dateFormat.format(date);
        Assert.isTrue("2014-06-16T12:30:10+03:00".equals(newDateString));
    }


    @Test
    public void testDateDeserializerWithTimeZone() {
        String dateString = "2014-06-16T12:30:10+03:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomJsonDateDeserializer.DATE_FORMAT_WITH_TIMEZONE);
        CustomJsonDateDeserializer dateDeserializer = new CustomJsonDateDeserializer();
        Date date = dateDeserializer.parseDateString(dateString);
        String newDateString = dateFormat.format(date);
        Assert.isTrue("2014-06-16T12:30:10+03:00".equals(newDateString));
    }

    @Test
    public void testDateString() {
        CustomJsonDateDeserializer dateDeserializer = new CustomJsonDateDeserializer();
        SimpleDateFormat dateFormat = new SimpleDateFormat(CustomJsonDateDeserializer.DATE_FORMAT_WITH_TIMEZONE);

        Date d1 = dateDeserializer.parseDateString(null);
        Assert.isTrue(d1 == null);

        String dateString = "2014-06-16T12:30:10-04:00";
        Date date = dateDeserializer.parseDateString(dateString);
        String newDateString = dateFormat.format(date);
        Assert.isTrue("2014-06-16T19:30:10+03:00".equals(newDateString));

        String dateString2 = "2014-06-16T12:30:10.834589-04:00";
        Date date2 = dateDeserializer.parseDateString(dateString2);
        String newDateString2 = dateFormat.format(date2);
        Assert.isTrue("2014-06-16T19:30:10+03:00".equals(newDateString2));

        String dateString3 = "2014-06-16T12:30:10+03:00";
        Date date3 = dateDeserializer.parseDateString(dateString3);
        String newDateString3 = dateFormat.format(date3);
        Assert.isTrue("2014-06-16T12:30:10+03:00".equals(newDateString3));

        String dateStringNull = "20AF-06-16T12:30:10.834589-04:00";
        Date dateNull = dateDeserializer.parseDateString(dateStringNull);
        Assert.isTrue(dateNull == null);

    }

    @Test
    public void testCommentSerialize() throws IOException {
        CommentDefinition comment = new CommentDefinition();
        comment.setAuthor("Gogu");
        comment.setCommentSeverity(CommentSeverity.HIGH);
        comment.setDate(new Date());
        comment.setText("text");
        String commentJson = objectMapper.writeValueAsString(comment);
        System.out.println(commentJson);
    }

    @Test
    public void testCommentDeserialize() throws IOException {
        String commentJson = "{\"text\":\"text\",\"author\":\"Gogu\",\"date\":\"2014-06-23T18:56:48.1234567+03:00\",\"commentSeverity\":\"High\",\"metadata\":null}";
        CommentDefinition comment = objectMapper.readValue(commentJson, CommentDefinition.class);
        String commentJsonAdjusted = objectMapper.writeValueAsString(comment);
        System.out.println(commentJsonAdjusted);
    }

}
