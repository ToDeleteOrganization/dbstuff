package com.sdl.bcm.test.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;

import com.sdl.bcm.BCMSerializer;
import com.sdl.bcm.api.client.AbstractRestClient;
import com.sdl.bcm.api.client.BCMStorageServiceClient;
import com.sdl.bcm.api.response.CopyToTargetLanguageResponse;
import com.sdl.bcm.api.response.DocumentIdAndIsoCodePair;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.TextMarkup;
import com.sdl.bcm.model.fileskeleton.TerminologyData;

@RunWith(Parameterized.class)
public class FullDocumentTest extends StorageDataDrivenIntegrationTest {

	private static BCMStorageServiceClient client = new BCMStorageServiceClient();

	private static final Logger LOG = Logger.getLogger(FullDocumentTest.class);

	private static final String ENDPOINT_TO_TEST = "/bcm/documents/";

	private List<String> documentLeftOversIDs = new ArrayList<String>();

	public FullDocumentTest(java.io.File jsonFile) {
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

	@Override
	@After
	public void after() {
		super.after();
		for (String documentId : documentLeftOversIDs) {
			try {
				client.deleteDocument(documentId);
			} catch (Exception e) {
				LOG.warn("Error cleanning left over document: " + documentId + ": " + e.getMessage());
			}
		}
		documentLeftOversIDs.clear();
	}

	@Test
	public void testGetDocumentLatestVersion() throws Exception {
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
	public void testDocumentNewRevision() throws Exception {
		Integer oldVersion = 1;

		String sNewVersion = client.newRevision(document.getId());
		Integer newVersion = Integer.parseInt(sNewVersion);
		Assert.isTrue(oldVersion.equals(newVersion - 1));

		sNewVersion = client.newRevision(document.getId());
		newVersion = Integer.parseInt(sNewVersion);
		Assert.isTrue(oldVersion.equals(newVersion - 2));
	}

	@Test
	public void testGetDocumentLatestRevision() throws Exception {

		String docJson = client.getDocument(document.getId());
		Document doc = BCMSerializer.deserializeBCM(docJson);
		doc.setName(doc.getName() + "_firstRevision");
		client.updateDocument(document.getId(), BCMSerializer.serializeBCM(doc));

		client.newRevision(document.getId());

		docJson = client.getDocument(document.getId());
		doc = BCMSerializer.deserializeBCM(docJson);
		Assert.isTrue(doc.getName().endsWith("_firstRevision"));
		doc.setName(doc.getName() + "_secondRevision");
		client.updateDocument(document.getId(), BCMSerializer.serializeBCM(doc));
		client.newRevision(document.getId());

		docJson = client.getDocument(document.getId());
		doc = BCMSerializer.deserializeBCM(docJson);
		Assert.isTrue(doc.getName().endsWith("_secondRevision"));
	}

	@Test
	public void testCopyToTarget() throws Exception {
		List<String> isoCodes = new ArrayList<>();
		isoCodes.add("en-UK");
		isoCodes.add("es-ES");
		isoCodes.add("ro-RO");
		isoCodes.add("ro-MD");

		CopyToTargetLanguageResponse response = client.copyToTargetLanguage(document.getId(), isoCodes.toArray(new String[] {}));
		List<DocumentIdAndIsoCodePair> idAndIsoCodePairList = response.getIdAndIsoCodePairList();

		Assert.isTrue(idAndIsoCodePairList != null);
		Assert.isTrue(!idAndIsoCodePairList.isEmpty());
		Assert.isTrue(idAndIsoCodePairList.size() == 4);

		for (DocumentIdAndIsoCodePair pair : idAndIsoCodePairList) {
			Assert.isTrue(isoCodes.contains(pair.getIsoCode()));
			documentLeftOversIDs.add(pair.getDocumentId());
		}
	}

	public void testUpdateDocumentNewRevision() throws Exception {
		Integer oldVersion = 1;
		String sNewVersion = client.newRevision(document.getId());
		Assert.isTrue(oldVersion.equals(Integer.parseInt(sNewVersion) - 1));

		String uDocument = client.getDocument(document.getId());
		// TODO: finish this or remove it
	}

	@Test
	public void testUpdateSkeleton() throws IOException {
		addToMetaData(fileSkeleton, "Added metadata skeleton key", "Added metadata skeleton value");
		TerminologyData terminologyData = getTerminologyData("New test text");
		List<TerminologyData> currentTerminologyData = fileSkeleton.getTerminologyData();

		if (currentTerminologyData == null) {
			List<TerminologyData> termDate = new ArrayList<TerminologyData>();
			termDate.add(terminologyData);
			fileSkeleton.setTerminologyData(termDate);
		} else {
			currentTerminologyData.add(terminologyData);
		}

		// aggregate full document
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

	@Test
	public void testNewRevisionNotFound() throws Exception {
		client.deleteDocument(document.getId());

		try {
			client.newRevision(document.getId());
		} catch (HttpClientErrorException hcee) {
			Assert.isTrue(HttpStatus.NOT_FOUND.equals(hcee.getStatusCode()));
		}

		DocumentFile docFile = new DocumentFile();
		docFile.setDocJSON(jsonString);
		docFile.setId(document.getId());
		client.addDocument(docFile);

		Integer oldVersion = 1;
		String newRevision = client.newRevision(document.getId());

		Assert.isTrue(oldVersion.equals(Integer.parseInt(newRevision) - 1));
	}

	@Override
	protected AbstractRestClient getRestClient() {
		return client;
	}
}
