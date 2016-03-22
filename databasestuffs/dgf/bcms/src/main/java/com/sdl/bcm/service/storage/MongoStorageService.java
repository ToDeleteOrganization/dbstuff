package com.sdl.bcm.service.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;
import com.sdl.bcm.BCMSerializer;
import com.sdl.bcm.api.response.DocumentIdAndIsoCodePair;
import com.sdl.bcm.model.Document;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.model.File;
import com.sdl.bcm.service.model.DocumentFileMongoObject;
import com.sdl.bcm.service.storage.gridfs.DataRepository;
import com.sdl.bcm.service.storage.util.ConverterUtil;

/**
 * Storage manager that stores full documents into Mongo GridFS
 */
@Service
public class MongoStorageService implements StorageService {

	private static final String UNKNOWN_FILE_NAME = "Unknown file name";

	@Autowired(required = true)
	protected DataRepository gridFSRepository;

	/**
	 * {@inheritDoc}
	 */
	public void addDocument(String documentId, String document, Map<String, Object> properties) {
		// add/increase version number and add ID field in properties.
		Integer version = properties.get(DocumentMetadataKeys.VERSION_KEY) != null ? (Integer) properties.get(DocumentMetadataKeys.VERSION_KEY) : 1;
		properties.put(DocumentMetadataKeys.VERSION_KEY, version);
		properties.put(DocumentMetadataKeys.ID_KEY, documentId);

		DocumentFileMongoObject docStreamDto = ConverterUtil.convertToDocumentFileStreamDTO(documentId, document, properties);

		gridFSRepository.addFile(docStreamDto);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateDocument(String documentId, String document) {
		DocumentFileMongoObject docDto = ConverterUtil.convertToDocumentFileStreamDTO(documentId, document, null);
		gridFSRepository.updateFile(docDto);
	}

	/**
	 * {@inheritDoc}
	 */
	public void revert(String documentId, Integer version) {
		gridFSRepository.revert(documentId, version);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<DocumentIdAndIsoCodePair> copyToTargetLanguage(String documentId, String[] targetLanguageISOCodes) throws IOException {
		DocumentFile documentFile = gridFSRepository.getDocument(documentId);
		// reset version
		documentFile.getProperties().remove(DocumentMetadataKeys.VERSION_KEY);
		Document document = BCMSerializer.deserializeBCM(documentFile.getDocJSON());
		List<DocumentIdAndIsoCodePair> pairList = new ArrayList<>();
		for (String targetIsoCode : targetLanguageISOCodes) {
			// validation of ISO code would go here
			document.setTargetLanguageCode(targetIsoCode);

			for (File file : document.getFiles()) {
				file.getMetadata().put(DocumentMetadataKeys.SDL_TARGET_LANG_CODE, targetIsoCode);
			}

			String newDocId = UUID.randomUUID().toString();
			document.setId(newDocId);
			addDocument(newDocId, BCMSerializer.serializeBCM(document), documentFile.getProperties());
			pairList.add(new DocumentIdAndIsoCodePair(newDocId, targetIsoCode));
		}
		return pairList;
	}

	/**
	 * {@inheritDoc}
	 */
	public DocumentFile getDocument(String documentId) {
		return gridFSRepository.getDocument(documentId);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteFullDocument(String documentId) {
		gridFSRepository.deleteDocument(documentId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getStoredDocuments() {
		Map<String, String> result = new HashMap<>();
		Map<String, GridFSDBFile> filesMap = new HashMap<>();

		List<GridFSDBFile> gridFsFiles = gridFSRepository.getAllDocuments();

		for (GridFSDBFile gridFSDBFile : gridFsFiles) {
			GridFSDBFile lastUploadedFile = filesMap.get(gridFSDBFile.getFilename());
			if (lastUploadedFile == null || gridFSDBFile.getUploadDate().after(lastUploadedFile.getUploadDate())) {
				filesMap.put(gridFSDBFile.getFilename(), gridFSDBFile);
			}
		}

		for (String fileId : filesMap.keySet()) {
			GridFSDBFile file = filesMap.get(fileId);
			if (file.getMetaData() != null && file.getMetaData().get(DocumentMetadataKeys.FILENAME_KEY) != null) {
				result.put(file.getFilename(), file.getMetaData().get(DocumentMetadataKeys.FILENAME_KEY).toString());
			} else {
				result.put(file.getFilename(), UNKNOWN_FILE_NAME);
			}
		}

		return result;
	}

}
