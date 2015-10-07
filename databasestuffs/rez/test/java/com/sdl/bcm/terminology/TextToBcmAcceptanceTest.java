package com.sdl.bcm.terminology;


import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.sdl.bcm.BCMSerializer;
import com.sdl.bcm.TestBase;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.File;
import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.utils.Utils;
import com.sdl.bcm.visitor.VisitorException;

/**
 *  Created by amironescu on 2/26/14.
 */
public class TextToBcmAcceptanceTest extends TestBase {


    private String testDataPath = "src/test/resources/testData/TextToBcmAcceptanceTest/";
    public static Logger logger = Logger.getLogger(TextToBcmAcceptanceTest.class);

    private Document testDeserialized(String jsonContent){

        Document document = null;

        try {
            document = BCMSerializer.deserializeBCM(jsonContent);
        } catch (IOException e) {
            logger.info("string could not be deserialized");
            e.printStackTrace();
        }

        return document;
    }

    private String getTextFromSourceParagraph (Document document){

        List<File> files = document.getFiles();
        File firstFile = (File) files.get(0);
        List<ParagraphUnit> paragraphUnits = firstFile.getParagraphUnits();
        ParagraphUnit paragraphUnit = (ParagraphUnit) paragraphUnits.get(0);
        String sourceTextExtracted = paragraphUnit.getPlainText(ParagraphUnit.TargetParagraph.SOURCE_PARAGRAPH);

        return sourceTextExtracted;
    }

    private String getTextFromSourceDocument (Document document) {
        String sourceTextExtracted = "";
        List<ParagraphUnit> paragraphUnits = document.getFiles().get(0).getParagraphUnits();
        for( ParagraphUnit paragraphUnit : paragraphUnits) {
            sourceTextExtracted += paragraphUnit.getPlainText(ParagraphUnit.TargetParagraph.SOURCE_PARAGRAPH);
        }
        return sourceTextExtracted;
    }

    private Document applyTerminology(Document document, int start, int end, String term, int segmentnumber){
        List<File> files = document.getFiles();
        File firstFile = (File) files.get(0);
        List<ParagraphUnit> paragraphUnits = firstFile.getParagraphUnits();
        FileSkeleton fileSkeleton = firstFile.getSkeleton();
        ParagraphUnit paragraphUnit = (ParagraphUnit) paragraphUnits.get(0);

        List<MarkupData> paragraphContent = paragraphUnit.getSource().getChildren();
        Segment segment = (Segment) paragraphContent.get(segmentnumber);

        int[] startIndexes = new int[1];
        int[] endIndexes = new int[1];
        startIndexes[0] = start;
        endIndexes[0] = end;
        TerminologyData terminologyData1 = setTerminologyData(0, term, "termid1", 1);

        try {
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);
        } catch (VisitorException e) {
            logger.info("Terminoloy markup not added");
            e.printStackTrace();
        }
        return document;
    }

    private Document applyTerminology(Document document, int start, int end, String term, int paragraphNumber, int segmentnumber){
        List<File> files = document.getFiles();
        File firstFile = (File) files.get(0);
        List<ParagraphUnit> paragraphUnits = firstFile.getParagraphUnits();
        FileSkeleton fileSkeleton = firstFile.getSkeleton();
        ParagraphUnit paragraphUnit = (ParagraphUnit) paragraphUnits.get(paragraphNumber);

        List<MarkupData> paragraphContent = paragraphUnit.getSource().getChildren();
        Segment segment = (Segment) paragraphContent.get(segmentnumber);

        int[] startIndexes = new int[1];
        int[] endIndexes = new int[1];
        startIndexes[0] = start;
        endIndexes[0] = end;
        TerminologyData terminologyData1 = setTerminologyData(0, term, "termid1", 1);

        try {
            segment.addTerminologyData(startIndexes, endIndexes, terminologyData1, fileSkeleton);
        } catch (VisitorException e) {
            logger.info("Terminoloy markup not added");
            e.printStackTrace();
        }
        return document;
    }


    private String testSerialize (Document document){
        String serializedBack = null;
        try {
            serializedBack = BCMSerializer.serializeBCM(document);
        } catch (IOException e) {
            logger.info("document could not be serialized");
            e.printStackTrace();
        }
        return serializedBack;
    }

    @Test
    public void testNewLines() throws IOException {

        //deserialize
        String docJSON = Utils.readFile(testDataPath + "testNewLines.json" );
        Document desDocument = testDeserialized(docJSON);

        // get text
        String sourceTextExtracted = getTextFromSourceParagraph(desDocument);

        String expectedText = "DE getting started\r\n" +
                "Finding a location for your photo printer\r\n" +
                "§ Place the photo printer on a flat, clean and dust-free surface, in a dry location, and out of direct sunlight.\r\n";

        System.out.println("source plain text");
        System.out.println(sourceTextExtracted);
        System.out.println(expectedText);
        Assert.assertTrue("Recreated text doesn't match expected", sourceTextExtracted.equals(expectedText));

        //apply terminology
        Document docWithTerminologyApplied = applyTerminology(desDocument, 48, 61, "photo printer", 0);

        //serialize
        String serializedBack = testSerialize(docWithTerminologyApplied);
        System.out.println("serialized document");
        System.out.println(serializedBack);

        String expectedSerializedBcm = Utils.readFile(testDataPath + "testNewLines_Serialized.json" );
        System.out.println(expectedSerializedBcm);

        Document reference = BCMSerializer.deserializeBCM(expectedSerializedBcm);

        Assert.assertTrue("Document was not serialized as expected:", reference.equals(docWithTerminologyApplied));
    }


    @Test
    public void testMultipleSegments() throws IOException {

        //deserialize
        String docJSON = Utils.readFile(testDataPath + "testMultipleSegments.json" );
        Document desDocument = testDeserialized(docJSON);

        // get text
        String sourceTextExtracted = getTextFromSourceParagraph(desDocument);

        String expectedText = "I will arise and go now, and go to Innisfree,\r\n" +
                "And a small cabin build there, of clay and wattles made:\r\n" +
                "Nine bean-rows will I have there, a hive for the honeybee,\r\n" +
                "And live alone in the bee-loud glade.\r\n" +
                "\r\n" +
                "I will arise and go now, for always night and day\r\n" +
                "I hear lake water lapping with low sounds by the shore;\r\n" +
                "While I stand on the roadway, or on the pavements gray,\r\n" +
                "I hear it in the deep heart's core.";

        System.out.println("source plain text:");
        System.out.println(sourceTextExtracted);
        System.out.println(expectedText);
        Assert.assertTrue("Recreated text doesn't match expected", sourceTextExtracted.equals(expectedText));

        //apply terminology
        Document docWithTerminologyApplied = applyTerminology(desDocument, 35, 44, "Innisfree", 0);

        //apply terminology
        Document docWithTerminologyAppliedTwice = applyTerminology(docWithTerminologyApplied, 53, 64, "small cabin", 0);

        //serialize
        String serializedBack = testSerialize(docWithTerminologyAppliedTwice);
        System.out.println("serialized document");
        System.out.println(serializedBack);

        String expectedSerializedBcm = Utils.readFile(testDataPath + "testMultipleSegments_Serialized.json" );
        System.out.println(expectedSerializedBcm);

        Document reference = BCMSerializer.deserializeBCM(expectedSerializedBcm);
        Assert.assertTrue("document not serialized as expected", reference.equals(docWithTerminologyAppliedTwice));
    }

    @Test
    public void testTabs() throws IOException {

        //deserialize
        String docJSON = Utils.readFile(testDataPath + "testTabs.json" );
        Document desDocument = testDeserialized(docJSON);

        // get text
        String sourceTextExtracted = getTextFromSourceParagraph(desDocument);

        String expectedText = "\tThis topic describes how to edit a batch task sequence.\r\n" +
                "\tTo edit a task sequence: Select File >\tSetup\t>\tTask Sequences from the menu bar. The Task Sequences dialog box is displayed. Select the sequence you want to edit and click Edit. The selected sequence is displayed in the Edit Task Sequence dialog box.";

        System.out.println("source plain text:");
        System.out.println(sourceTextExtracted);
        System.out.println(expectedText);
        Assert.assertTrue("Recreated text doesn't match expected", sourceTextExtracted.equals(expectedText));

        //apply terminology
        Document docWithTerminologyApplied = applyTerminology(desDocument, 0, 11, "Select File", 4);

        //serialize
        String serializedBack = testSerialize(docWithTerminologyApplied);
        System.out.println("serialized document");
        System.out.println(serializedBack);

        String expectedSerializedBcm = Utils.readFile(testDataPath + "testTabs_Serialized.json" );
        System.out.println(expectedSerializedBcm);

        Document reference = BCMSerializer.deserializeBCM(expectedSerializedBcm);
        Assert.assertTrue("document not serialized as expected", reference.equals(docWithTerminologyApplied));

    }

    @Test
    public void testJapanese() throws IOException {

        //deserialize
        String docJSON = Utils.readFile(testDataPath + "testJapanese.json" );
        Document desDocument = testDeserialized(docJSON);

        // get text
        String sourceTextExtracted = getTextFromSourceParagraph(desDocument);

        //original json file doesn't actually contain this text; created a corrected json source with this text in
        String expectedText = "時ラモ役音オソサユ菱今ナチノヒ品将ぎ市転テタヌ面公フチ行法ょがじ掲索しどド能訳リわ悪囲ク載厳ぐぶべ内開リ外謙極ドね。3職将ハ産美スオキサ起65何半ヱ職知年わ駅部ハロ芸弘ょ華手クぜう性林抑態玉だんス。動中ぼぶイ左質もぜる更朝活を道嫌禁のぶづ計治分クヲマ代経ン洋希サ妥35実せ司15使らざ主毎助系促こレめに。";

        System.out.println("source plain text:");
        System.out.println(sourceTextExtracted);
        System.out.println(expectedText);
        Assert.assertTrue("Recreated text doesn't match expected", sourceTextExtracted.equals(expectedText));

        //serialize
        String serializedBack = testSerialize(desDocument);
        System.out.println("serialized document");
        System.out.println(serializedBack);

        String expectedSerializedBcm = Utils.readFile(testDataPath + "testJapanese.json");
        System.out.println(expectedSerializedBcm);

        //avoid string comparisons:
//        Assert.assertTrue("document not serialized as expected", serializedBack.equals(expectedSerializedBcm));

        Document referenceDoc = BCMSerializer.deserializeBCM(expectedSerializedBcm);
        Document reSerializedDoc = BCMSerializer.deserializeBCM(serializedBack);
        Assert.assertTrue("document not serialized as expected", referenceDoc.equals(reSerializedDoc));
    }

    @Test
    public void testFrench() throws IOException {

        //deserialize
        String docJSON = Utils.readFile(testDataPath + "testFrench.json");
        Document desDocument = testDeserialized(docJSON);

        // get text
        String sourceTextExtracted = getTextFromSourceParagraph(desDocument);

        String expectedText = "Le gaz de schiste, nouvelle manne de l’énergie exploitée en premier lieu aux Etats-Unis, n’a pas seulement dopé l’industrie locale : il pourrait bouleverser la géographie énergétique mondiale. / États-Unis, Golfe, Russie, Énergie, Géopolitique, Mondialisation, Technologie, Gaz naturel – (…) / États-Unis, Golfe, Russie, Énergie, Géopolitique, Mondialisation, Technologie, Gaz naturel – 2013/08 Source";

        System.out.println("source plain text:");
        System.out.println(sourceTextExtracted);
        System.out.println(expectedText);
        Assert.assertTrue("Recreated text doesn't match expected", sourceTextExtracted.equals(expectedText));

        //serialize
        String serializedBack = testSerialize(desDocument);
        System.out.println("serialized document");
        System.out.println(serializedBack);

        //String expectedSerializedBcm = Utils.readFile(testDataPath + "testFrench_Serialized.json");
        String expectedSerializedBcm = Utils.readFile(testDataPath + "testFrench.json");
        System.out.println(expectedSerializedBcm);

        //avoid direct string comparisons
//        Assert.assertTrue("document not serialized as expected", serializedBack.equals(expectedSerializedBcm));

        Document referenceDoc = BCMSerializer.deserializeBCM(expectedSerializedBcm);
        Document reSerializedDoc = BCMSerializer.deserializeBCM(serializedBack);
        Assert.assertTrue("document not serialized as expected", referenceDoc.equals(reSerializedDoc));
    }

    @Test
    public void test_LCC_821_fullDocument() throws IOException {

        //deserialize
        String docJSON = Utils.readFile(testDataPath + "test_LCC_821_PlaceholderStartingSegment_original.json");
        Document desDocument = testDeserialized(docJSON);

        // get text
        String sourceTextExtracted = getTextFromSourceDocument(desDocument);
        String expectedText = "Spread formor bold charctaers.xxxxxx";

        System.out.println("source plain text");
        System.out.println(sourceTextExtracted);
        System.out.println(expectedText);
        Assert.assertTrue("Recreated text doesn't match expected", sourceTextExtracted.equals(expectedText));

        //check TerminologyData in FileSkeleton before terminology application
        Assert.assertTrue(desDocument.getFiles().get(0).getSkeleton().getTerminologyData().size() == 0);

        //apply terminology
        Document docWithTerminologyApplied = applyTerminology(desDocument, 0, 6, "xxxxxx", 5, 0);//pargraph 5, segment 0

        //check TerminologyData in FileSkeleton after terminology application
        Assert.assertNotNull(docWithTerminologyApplied.getFiles());
        Assert.assertTrue(docWithTerminologyApplied.getFiles().size() != 0);
        Assert.assertNotNull(docWithTerminologyApplied.getFiles().get(0).getSkeleton().getTerminologyData());
        Assert.assertTrue(docWithTerminologyApplied.getFiles().get(0).getSkeleton().getTerminologyData().size() != 0);

        TerminologyData terminologyData = docWithTerminologyApplied.getFiles().get(0).getSkeleton().getTerminologyData().get(0);
        Assert.assertTrue(terminologyData.getId() == 1);
        Assert.assertNotNull(terminologyData.getTerms().get(0));
        Term term = terminologyData.getTerms().get(0);
        Assert.assertTrue(term.getId().equals("termid1"));
        Assert.assertTrue(term.getText().equals("xxxxxx"));
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }
}
