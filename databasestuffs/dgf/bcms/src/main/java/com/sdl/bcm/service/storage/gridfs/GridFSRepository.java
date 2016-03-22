package com.sdl.bcm.service.storage.gridfs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.service.model.DocumentFileMongoObject;
import com.sdl.bcm.service.storage.DocumentMetadataKeys;
import com.sdl.bcm.service.storage.util.BCMSMongoQueryBuilder;
import com.sdl.bcm.service.storage.util.ConverterUtil;
import com.sdl.bcm.service.storage.util.MetricsConstants;

@Repository
public class GridFSRepository extends DataRepository {

	private static final Logger LOG = Logger.getLogger(GridFSRepository.class);

	@Autowired
	private BCMGridFsTemplate gridFsTemplate;

	@Autowired
	private MeasurableGridFSRepository measure;

	GridFSDBFile getLatestVersionOfDocument(String documentId) {
		GridFSDBFile fsFilesResult = null;

		Query query = new BCMSMongoQueryBuilder().addCriteriaByDocumentId(documentId).sortDescendingByVersion().build();
		LOG.info(query.toString());

		StopWatch timer = new StopWatch();
		timer.start("get latest document for: " + documentId);

		List<GridFSDBFile> files = gridFsTemplate.find(query);
		if (CollectionUtils.isNotEmpty(files)) {
			// get the first element from a descending query
			fsFilesResult = files.get(0);
		}
		timer.stop();

		debugIfEnable("PERFORMANCE >> repository getLatestVersionOfDocument for " + documentId + ", running time (seconds): " + timer.getTotalTimeSeconds());
		return fsFilesResult;
	}

	String getDocumentAsJson(GridFSDBFile latestFile) {
		StopWatch timer = new StopWatch();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			timer.start();
			latestFile.writeTo(baos);
			timer.stop();
		} catch (IOException e) {
			LOG.error(e);
		}
		DBObject metadata = latestFile.getMetaData();
		String encoding = (String) metadata.get("encoding");

		Charset charset = (encoding != null) ? Charset.forName(encoding) : Charset.forName("UTF-8");
		String result = new String(baos.toByteArray(), charset);

		Integer lastVersion = (Integer) latestFile.getMetaData().get(DocumentMetadataKeys.ID_QUERY_KEY);
		debugIfEnable("PERFORMANCE >> repository getDocumentChunks for " + lastVersion + ", running time (seconds): " + timer.getTotalTimeSeconds());

		return result;
	}

	/**
	 * @{inheritDoc}
	 */
	@Timed(name = MetricsConstants.ADD_DOCUMENT_TIMED_MONGO, absolute = true)
	@Counted(name = MetricsConstants.ADD_DOCUMENT_COUNTED_MONGO, absolute = true)
	@ExceptionMetered(name = MetricsConstants.ADD_DOCUMENT_EXCEPTION_MONGO, absolute = true)
	public void addFile(DocumentFileMongoObject bcmMongoDoc) {
		StopWatch timer = new StopWatch();
		timer.start();

		if (!bcmMongoDoc.hasMetadata()) {
			gridFsTemplate.store(bcmMongoDoc.getInputStream(), bcmMongoDoc.getDocumentId());
		} else {
			gridFsTemplate.store(bcmMongoDoc.getInputStream(), bcmMongoDoc.getDocumentId(), bcmMongoDoc.getMetaData());
		}
		timer.stop();
		debugIfEnable("PERFORMANCE >> repository addFile " + bcmMongoDoc.getDocumentId() + " running time (seconds): " + timer.getTotalTimeSeconds());
	}

	/**
	 * @{inheritDoc}
	 */
	@Timed(name = MetricsConstants.UPDATE_MONGO_DOCUMENT_TIMED, absolute = false)
	@Counted(name = MetricsConstants.UPDATE_MONGO_DOCUMENT_COUNTED, absolute = false)
	@ExceptionMetered(name = MetricsConstants.UPDATE_MONGO_DOCUMENT_EXCEPTION, absolute = true)
	public void updateFile(DocumentFileMongoObject newDocument) {
		StopWatch updateTimer = new StopWatch();
		updateTimer.start("update document with ID: " + newDocument.getDocumentId());

		GridFSDBFile latestFile = measure.getLatestVersionOfDocument(this, newDocument.getDocumentId());
		if (latestFile != null) {
			gridFsTemplate.updateGridFSDBFiles(latestFile, newDocument.getInputStream(), newDocument.getDocumentId());
		}
		updateTimer.stop();
		debugIfEnable("PERFORMANCE >> repository updateFile " + newDocument.getDocumentId() + " running time (seconds): " + updateTimer.getTotalTimeSeconds());
	}

	/**
	 * @{inheritDoc}
	 */
	public DocumentFile getDocument(String documentId) {
		DocumentFile fullDocument = null;

		StopWatch timer = new StopWatch();
		timer.start();

		GridFSDBFile latestFile = measure.getLatestVersionOfDocument(this, documentId);
		if (latestFile != null) {
			fullDocument = ConverterUtil.convertGridFsFileToFullDocument(latestFile);
			fullDocument.setDocJSON(measure.getDocumentAsJson(this, latestFile));
		} else {
			LOG.warn("Document with id " + documentId + " wasn't found.");
		}

		timer.stop();
		debugIfEnable("PERFORMANCE >> repository getDocument with ID " + documentId + " running time (seconds): " + timer.getTotalTimeSeconds());
		return fullDocument;
	}

	/**
	 * @{inheritDoc}
	 */
	public void deleteDocument(String documentId) {
		StopWatch timer = new StopWatch();

		timer.start("delete document with ID: " + documentId);
		gridFsTemplate.delete(new BCMSMongoQueryBuilder().addCriteriaByDocumentId(documentId).build());
		timer.stop();

		debugIfEnable("PERFORMANCE >> repository deleteDocument " + documentId + " running time (seconds): " + timer.getTotalTimeSeconds());
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void revert(String documentId, Integer version) {
		StopWatch revertTimer = new StopWatch();
		revertTimer.start("Reverting document " + documentId + " to version " + version);

		GridFSDBFile latestFile = measure.getLatestVersionOfDocument(this, documentId);

		Integer lastVersion = (Integer) latestFile.getMetaData().get(DocumentMetadataKeys.VERSION_KEY);
		if ((version > 0) || (version < lastVersion)) {
			Query query = new BCMSMongoQueryBuilder().addCriteriaByDocumentId(documentId).addCriteriaVersionGreaterThen(version).build();
			gridFsTemplate.delete(query);

		} else {
			LOG.warn("The version " + version + " is greater then the latest version " + latestFile);
		}

		revertTimer.stop();
		debugIfEnable("PERFORMANCE >> repository revert document " + documentId + " running time (seconds): " + revertTimer.getTotalTimeSeconds());
	}

	/**
	 * @{inheritDoc
	 */
	public List<GridFSDBFile> getAllDocuments() {
		StopWatch timer = new StopWatch();
		timer.start();

		List<GridFSDBFile> gridFsFiles = gridFsTemplate.find(new Query());
		timer.stop();

		debugIfEnable("PERFORMANCE >> repository getAllDocuments running time (seconds): " + timer.getTotalTimeSeconds());
		return gridFsFiles;
	}

	private void debugIfEnable(String message) {
		if (LOG.isInfoEnabled()) {
			 LOG.info(message);
		}
	}

}
