package com.sdl.bcm.service.storage.gridfs;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.gridfs.GridFSDBFile;

/**
 * A BCMS specific extension of the {@link GridFsTemplate} class. <br />
 * 
 * This is used to customize the mongo GridFs functionality according to the
 * BCMS needs.
 * 
 */
public class BCMGridFsTemplate extends GridFsTemplate {

	private static final Logger LOG = Logger.getLogger(BCMGridFsTemplate.class);

	private static final int MINIMUM_DATABASE_LOAD_FOR_INDEX_CREATION = 12000;

	private MongoDbFactory dbFactory;

	private BCMGridFS bcmGridFS;

	/**
	 * {@inheritDoc}
	 * 
	 * @param dbFactory
	 * @param converter
	 */
	public BCMGridFsTemplate(MongoDbFactory dbFactory, MongoConverter converter) {
		super(dbFactory, converter);
		this.dbFactory = dbFactory;

		// ensure the 'metadata.ID' index
		DBCollection filesCollection = getGridFs().getFilesCollection();
		if (filesCollection.count() < MINIMUM_DATABASE_LOAD_FOR_INDEX_CREATION) {
			LOG.info("Creating index on 'metadata.ID'.");
			filesCollection.createIndex(new BasicDBObject("metadata.ID", 1), new BasicDBObject("background", true));
		}
	}

	/**
	 * Performs an update on the mongo GridFS. Deletes old records and adds new
	 * ones to 'fs.chunks' <br />
	 * and updates the associated 'fs.files' collection.
	 * 
	 * @param gridDBFile
	 *            The old 'gridDBFile' object, associated with the 'fs.files'
	 *            collection.
	 * 
	 * @param content
	 *            The new content of the document.
	 * 
	 * @param fileName
	 *            The filename/ID of the document.
	 */
	public void updateGridFSDBFiles(GridFSDBFile gridDBFile, InputStream content, String fileName) {
		BCMGridFS bcmGridFs = getGridFs();
		BCMGridFSInputFile bcmsInputFile = bcmGridFs.createFile(content, fileName, gridDBFile);

		try {
			// remove old 'chunks'
			bcmsInputFile.removeChunkCollections();

			// add new 'chunks'
			bcmsInputFile.saveChunks();

			// update 'files' with new data
			bcmsInputFile.updateFSFiles();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private BCMGridFS getGridFs() {
		if (bcmGridFS == null) {
			// create it only once.
			bcmGridFS = new BCMGridFS(dbFactory.getDb());
		}
		return bcmGridFS;
	}

}
