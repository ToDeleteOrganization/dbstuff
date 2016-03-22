package com.sdl.bcm.service.storage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sdl.bcm.api.response.DocumentIdAndIsoCodePair;
import com.sdl.bcm.model.DocumentFile;

public interface StorageService {

	/**
	 * Adds a new document with a new document id and a new version (if not
	 * provided).
	 * 
	 * @param documentId
	 *            String UUID of document in database
	 * @param document
	 *            The document itself as a String
	 * @param properties
	 *            map of document properties
	 */
	void addDocument(String documentId, String document, Map<String, Object> properties);

	/**
	 * Returns the latest version of a document identified by the
	 * <i>documentId</i> parameter. <br>
	 * The filtering for the last version is made by the <i>metadata.version</i>
	 * property.
	 * 
	 * @param documentId String UUID of document in database
	 * @return The document file associated with the <i>documentId</i>.
	 */
	DocumentFile getDocument(String documentId);

	/**
	 * Updates the content of the files with the new content specified by the
	 * <i>inputStream</i> parameter. 
	 * <br>
	 * All metadata properties will be preserved.
	 * 
	 * @param document
	 *            The new document content.
	 * @param documentId
	 *            The document ID to search for.
	 */
	void updateDocument(String documentId, String document);

	/**
	 * Deletes all documents that have the <i>metadata.ID</i> property matching
	 * the parameter <i>documentId</i>.
	 * 
	 * @param documentId
	 *            the id of the document(s) to be deleted
	 */
	void deleteFullDocument(String documentId);

	/**
     * Reverts this document to a specific version. The higher versions are deleted from the storage, no revert back is possible.
     *
     * @param documentId database Id of document (UUID String form)
     * @param version database integer version of stored document
     */
	void revert(String documentId, Integer version);

	/**
	 * Copies the documents in all the specified target languages.
	 *
	 * @param documentId
	 *            String UUID of document in database
	 * @param targetLanguageISOCodes
	 *            array of Strings representing ISO codes of the target
	 *            languages
	 * @throws IOException
	 */
	List<DocumentIdAndIsoCodePair> copyToTargetLanguage(String documentId, String[] targetLanguageISOCodes) throws IOException;

	/**
	 * 
	 * /** Returns latest uploaded version of all documents from the storage.
	 * <br>
	 *
	 * @return A map containing entries such as: [fileName, metadataFileName]
	 */
	Map<String, String> getStoredDocuments();

}
