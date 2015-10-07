package com.sdl.bcm;

import com.sdl.bcm.model.Document;
import com.sdl.bcm.utils.Utils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 *  Created by dtarba on 7/7/2015.
 */
public class SerializerToleranceTest extends TestBase {

    public static Logger logger = Logger.getLogger(SerializerToleranceTest.class);
    private static String testDataPath = "src/test/resources/testData/SerializationTest/";

    @Test
    public void testDeserializationWithMissingFields() {
        String testFilePath = testDataPath + "sourceFieldsMissing.json";
        Document document = null;
        try {
            document = getDocument(testFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error deseializing document: " + e.getMessage());
        }
        Assert.assertNotNull(document);
       // Assert.assertTrue();
    }

    @Test
    public void testDeserializationWithExtraFields() {

    }

    @Test
    public void testExtraSkeletonCommentsField() {
        String testFilePath = testDataPath + "testComment.json";
        Document document = null;
        try {
            document = getDocument(testFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error deseializing document: " + e.getMessage());
        }
        Assert.assertNotNull(document);
        // Assert.assertTrue();
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }

    private Document getDocument(String filename) throws IOException, ClassCastException {
        String dataReference = Utils.readFile(filename);
        return BCMSerializer.deserializeBCM(dataReference);
    }
}
