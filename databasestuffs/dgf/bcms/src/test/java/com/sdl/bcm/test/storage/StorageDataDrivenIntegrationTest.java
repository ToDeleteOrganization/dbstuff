package com.sdl.bcm.test.storage;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.util.Assert;

import com.sdl.bcm.BCMSerializer;
import com.sdl.bcm.api.client.AbstractRestClient;
import com.sdl.bcm.api.response.AddDocumentResponse;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.model.File;
import com.sdl.bcm.model.MetaData;
import com.sdl.bcm.model.ParagraphUnit;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.rest.logger.DocumentStatisticManager;
import com.sdl.bcm.rest.logger.MonitoringLogger;
import com.sdl.bcm.rest.logger.StatisticsData;
import com.sdl.bcm.rest.util.Utils;
import com.sdl.bcm.test.base.IntegrationTestBase;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;
import com.sdl.bcm.visitor.impl.GetTextMarkupVisitor;

public abstract class StorageDataDrivenIntegrationTest extends IntegrationTestBase {

	public static String TEST_DATA_PATH = "src/test/resources/integrationTests";

	protected Document document;
    protected File file;
    protected FileSkeleton fileSkeleton;
    protected List<ParagraphUnit> paragraphUnits;
    protected String jsonString;
    protected static int testFilesCounter;
    protected static int testFilesCount;

    @BeforeClass
    public static void setUp() {
        IntegrationTestBase.setUp();
    }

    public StorageDataDrivenIntegrationTest(java.io.File jsonFile) {
    	jsonString = Utils.readFile(jsonFile);
        try {
            document = BCMSerializer.deserializeBCM(jsonString);
            if (document.getFiles().size() >= 1) {
                file = document.getFiles().get(0);
                fileSkeleton = file.getSkeleton();
                paragraphUnits = new ArrayList<>(file.getParagraphUnits());
                file.setSkeleton(null);
                file.getParagraphUnits().clear();
            }
            MonitoringLogger.getInstance().registerStatistic(new DocumentStatisticManager());

            StatisticsData statisticsData = new StatisticsData();
            statisticsData.addProperty("fileName", jsonFile.getName());
            statisticsData.addProperty("documentId", document.getId());
            statisticsData.addProperty("paragraphsCount", paragraphUnits.size());
            statisticsData.addProperty("wordCount", getWordCount());
            statisticsData.addProperty("fileId", file.getId());

            MonitoringLogger.getInstance().createStatistic(statisticsData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected int getWordCount() {
        StringBuilder sb = new StringBuilder();
        for (ParagraphUnit paragraphUnit : paragraphUnits) {
            ExtractPlainTextVisitor textExtractorVisitor = new ExtractPlainTextVisitor();
            paragraphUnit.accept(textExtractorVisitor);
            sb.append(textExtractorVisitor.getText());
        }
        String wholeText = sb.toString();
        return wholeText.split("\\s+").length;
    }

    public static Collection<java.io.File[]> getFiles(String testDataPath) {
        java.io.File testDataDir = new java.io.File(testDataPath);
        java.io.File[] files;
        if (testDataDir.isDirectory()) {
            files = testDataDir.listFiles();
            List<java.io.File[]> result = new ArrayList<>();
            if (files != null) {
                for (java.io.File mFile : files) {
                    result.add(new java.io.File[]{mFile});
                }
            }
            testFilesCount = result.size();
            return result;
        }
        return null;
    }

    public static Collection<java.io.File[]> getTestFiles() {
        String[] paths = getTestsConfig();
        List<java.io.File[]> result = new ArrayList<>();
        for (String path : paths) {
            java.io.File testDataDir = new java.io.File(path);
            java.io.File[] files;
            if (testDataDir.isDirectory()) {
                files = testDataDir.listFiles();
                if (files != null) {
                    for (java.io.File mFile : files) {
                        result.add(new java.io.File[]{mFile});
                    }
                }
            }
        }
        testFilesCount = result.size();
        return result;
    }

    private static String[] getTestsConfig() {
        InputStream in = StorageDataDrivenIntegrationTest.class.getResourceAsStream("/integrationTestsDataSource.txt");
        byte[] contentBytes = Utils.readFileBytes(in);
        String content = new String(contentBytes).replace("\r", "").replace("\n", "");
        return content.split(";");
    }

    @Before
    public void before() {
        super.before();

        DocumentFile documentFile = new DocumentFile();
        documentFile.setId(document.getId());
        documentFile.setDocJSON(jsonString);
        HashMap<String, Object> props = new HashMap<>();
        props.put("fileName", document.getName());
        props.put("ID", document.getId());
        props.put("version", 1);
        documentFile.setProperties(props);
        AddDocumentResponse addDocumentResponse = getRestClient().addDocument(documentFile);

        Assert.notNull(addDocumentResponse);
        String documentId = addDocumentResponse.getBcmDocumentId();
        Assert.notNull(documentId);
        Assert.isTrue(documentId.equals(document.getId()));
        Assert.notNull(addDocumentResponse.getLinks());
    }

    @Override
    @After
    public void after() {
        super.after();
        testFilesCounter++;
        getRestClient().deleteDocument(document.getId());
        if (testFilesCounter == testFilesCount) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
            MonitoringLogger.getInstance().dumpStatisticsToFile(statisticsPath + "statistics-" + sdf.format(new Date()) + ".csv");
        }
    }

    protected static void addToMetaData(MetaData markupData, String key, String value) {
        Map<String, String> metadata = markupData.getMetadata();
        if (metadata == null) {
            metadata = new HashMap<>();
            markupData.setMetadata(metadata);
        }
        metadata.put(key, value);
    }

    protected TextMarkup getTextMarkup(Document document, int index) {
        TextMarkup result = null;
        GetTextMarkupVisitor visitor = new GetTextMarkupVisitor();
        document.accept(visitor);
        List<TextMarkup> textMarkups = visitor.getTextMarkupList();
        if (textMarkups != null && textMarkups.size() > 0) {
            if (index < 0) {
                index = 0;
            }
            if (index > textMarkups.size()) {
                index = textMarkups.size() - 1;
            }
            result = textMarkups.get(index);
        }
        return result;
    }

    public boolean compare(MetaData obj1, MetaData obj2) {
        return obj1.deepEquals(obj2);
    }

    protected static TerminologyData getTerminologyData(String text) {
        TerminologyData terminologyData = new TerminologyData();
        terminologyData.setId(1);
        terminologyData.setOrigin("Terminology JUnit Test");

        List<Term> terms = new ArrayList<>();
        Term term = new Term();
        term.setId("termId1");
        term.setScore(1);
        term.setText(text);
        terms.add(term);

        terminologyData.setTerms(terms);
        return terminologyData;
    }

    protected abstract AbstractRestClient getRestClient();

}
