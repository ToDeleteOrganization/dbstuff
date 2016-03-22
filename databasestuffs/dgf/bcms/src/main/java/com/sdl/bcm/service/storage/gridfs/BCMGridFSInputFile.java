package com.sdl.bcm.service.storage.gridfs;

import java.io.InputStream;

import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * 
 * @{inheritDoc}
 *
 */
public class BCMGridFSInputFile extends GridFSInputFile {

	private static final String CHUNK_FILES_ID = "files_id";

	protected BCMGridFSInputFile(BCMGridFS gridFS, InputStream inputStream, String filename) {
		super(gridFS, inputStream, filename);
	}

	/**
	 * Adds/Updates the 'fs.files' collection.
	 */
	public void updateFSFiles() {
		// this does an update if the 'fs.files' entry already exists
		super.save();
	}

	/**
	 * Removes the associated 'fs.chunks' collections.
	 */
	public void removeChunkCollections() {
		BCMGridFS bcmGridFS = getGridFS();
		bcmGridFS.getChunksCollection().remove(new BasicDBObject(CHUNK_FILES_ID, super.getId()));
	}

	@Override
	public BCMGridFS getGridFS() {
		return (BCMGridFS) super.getGridFS();
	}

}
