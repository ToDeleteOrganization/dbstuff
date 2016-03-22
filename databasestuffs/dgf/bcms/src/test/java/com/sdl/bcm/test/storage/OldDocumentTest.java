package com.sdl.bcm.test.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;

import com.sdl.bcm.BCMSerializer;
import com.sdl.bcm.api.client.AbstractRestClient;
import com.sdl.bcm.api.client.OldBCMStorageServiceClient;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.TerminologyData;

@RunWith(Parameterized.class)
public class OldDocumentTest extends StorageDataDrivenIntegrationTest {

	private static OldBCMStorageServiceClient client = new OldBCMStorageServiceClient();

	private static final String ENDPOINT_TO_TEST = "/bcm/fullDocument/";

	public OldDocumentTest(File jsonFile) {
		super(jsonFile);
		file.setSkeleton(fileSkeleton);
		file.getParagraphUnits().addAll(paragraphUnits);
	}

	@Parameterized.Parameters
	public static Collection<java.io.File[]> getFiles() {
		return getFiles(StorageDataDrivenIntegrationTest.TEST_DATA_PATH);
	}

	@BeforeClass
	public static void setUp() {
		StorageDataDrivenIntegrationTest.setUp();
		client.setBcmSourceUri(ENDPOINT_TO_TEST);
    	client.setBcmSourceUrl(bcmUrl);
    	client.setBcmAppName(bcmStorageAppName);
	}

    @Test
    public void testGetDocumentLatestVersion() throws Exception {
    	// TODO: update document, get latest updated
        String docJson = client.getDocument(document.getId());
        Document doc = BCMSerializer.deserializeBCM(docJson);
        
        Assert.isTrue(compare(document, doc));
    }

    @Test
    public void testUpdateDocument() throws Exception {
        String docJson = client.getDocument(document.getId());
        Document doc = BCMSerializer.deserializeBCM(docJson);
        Assert.isTrue(compare(document, doc));

        TextMarkup localTextMarkup = getTextMarkup(document, 0);
        TextMarkup remoteTextMarkup = getTextMarkup(doc, 0);
        
        Assert.isTrue(compare(localTextMarkup, remoteTextMarkup));

        remoteTextMarkup.setText(remoteTextMarkup.getText() + " - new added text.");

        List<Segment> segments = doc.getSegments();
        int i = 0;
        for (Segment segment : segments) {
            addToMetaData(segment, "Segment test key " + i, "Segment test value " + i++);
        }

        client.updateDocument(document.getId(), BCMSerializer.serializeBCM(doc));

        docJson = client.getDocument(document.getId());
        Document updatedDoc = BCMSerializer.deserializeBCM(docJson);

        Assert.isTrue(!compare(updatedDoc, document));
        Assert.isTrue(compare(doc, updatedDoc));
    }

    @Test
    public void testUpdateSkeleton() throws IOException {
        addToMetaData(fileSkeleton, "Added metadata skeleton key", "Added metadata skeleton value");
        TerminologyData terminologyData = null;//getTerminologyData("New test text");
        List<TerminologyData> currentTerminologyData = fileSkeleton.getTerminologyData();

        if (currentTerminologyData == null) {
        	List<TerminologyData> termDate = new ArrayList<TerminologyData>();
        	termDate.add(terminologyData);
        	fileSkeleton.setTerminologyData(termDate);
        } else {
        	currentTerminologyData.add(terminologyData);
        }

        //aggregate full document
        file.setSkeleton(fileSkeleton);
        file.getParagraphUnits().addAll(paragraphUnits);

        client.updateDocument(document.getId(), BCMSerializer.serializeBCM(document));

        String docJson = client.getDocument(document.getId());
        Document doc = BCMSerializer.deserializeBCM(docJson);
        Assert.isTrue(compare(document, doc));
    }

    @Test
    public void testGetDocumentNotFound() throws Exception {
        client.deleteDocument(document.getId());

        try {
            client.getDocument(document.getId());
        } catch (HttpClientErrorException hcee) {
            Assert.isTrue(HttpStatus.NOT_FOUND.equals(hcee.getStatusCode()));
        }

        DocumentFile docFile = new DocumentFile();
        docFile.setDocJSON(jsonString);
        docFile.setId(document.getId());
        client.addDocument(docFile);

        String docJson = client.getDocument(document.getId());
        Assert.isTrue(jsonString.equals(docJson));
    }

    @Override
	protected AbstractRestClient getRestClient() {
		return client;
	}
}
