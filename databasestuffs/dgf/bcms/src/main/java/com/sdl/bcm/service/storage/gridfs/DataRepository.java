package com.sdl.bcm.service.storage.gridfs;

import java.util.List;

import com.mongodb.gridfs.GridFSDBFile;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.service.model.DocumentFileMongoObject;

/**
 * Created by dtarba on 3/25/2015.
 */
public abstract class DataRepository {

	/**
	 * Reverts this document to a specific version. The higher versions are
	 * deleted from the storage, no revert back is possible.
	 *
	 * @param documentId
	 *            database Id of document (UUID String form)
	 * @param version
	 *            database integer version of stored document
	 */
	public abstract void revert(String documentId, Integer version);

	/**
	 * Adds a file with the given document ID.<br>
	 * The <i>properties</i> will be saved in metadata and the <i>documentId</i>
	 * will be also saved as the filename.
	 * 
	 * @param documentFileMongo
	 *            The document file in a representation for mongo gridfs.
	 */
	public abstract void addFile(DocumentFileMongoObject documentFileMongo);

	/**
	 * Updates the content of the files with the new content specified by the
	 * <i>inputStream</i> parameter. <br>
	 * All metadata properties will be preserved.
	 * 
	 * @param documentFileMongo
	 *            The document file in a representation for mongo gridfs.
	 */
	public abstract void updateFile(DocumentFileMongoObject documentFileMongo);

	/**
	 * Returns the latest version of a document identified by the
	 * <i>documentId</i> parameter. <br>
	 * The filtering for the last version is made by the <i>metadata.version</i>
	 * property.
	 * 
	 * @param documentId
	 * @return The document file associated with the <i>documentId</i>.
	 */
	public abstract DocumentFile getDocument(String documentId);

	/**
	 * Deletes all documents that have the <i>metadata.ID</i> property matching
	 * the parameter <i>documentId</i>.
	 * 
	 * @param documentId
	 *            the id of the document(s) to be deleted
	 */
	public abstract void deleteDocument(String documentId);

	/**
	 * Returns latest uploaded version of all documents from the storage.<br>
	 *
	 * @return A list containing all documents from fs.files collection.
	 */
	public abstract List<GridFSDBFile> getAllDocuments();

}
