package com.sdl.bcm.service.storage.gridfs;

import java.io.InputStream;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

/**
 * BCMS specific implementation of {@link GridFS}
 *
 */
public class BCMGridFS extends GridFS {

	public BCMGridFS(DB db) {
		super(db);
	}

	@Override
	public DBCollection getChunksCollection() {
		return super.getChunksCollection();
	}

	@Override
	public DBCollection getFilesCollection() {
		return super.getFilesCollection();
	}

	@Override
	public GridFSInputFile createFile(final InputStream in, final String filename) {
		return new BCMGridFSInputFile(this, in, filename);
	}

	/**
	 * Creates a {@link BCMGridFSInputFile} based on an existing 'GridFSDBFile'
	 * entity.
	 * 
	 * @param content
	 *            The content of the document.
	 * @param filename
	 *            The filename/Id of the file.
	 * @param gridDBFile
	 *            The existing 'GridFSDBFile' file;
	 * @return
	 */
	public BCMGridFSInputFile createFile(final InputStream content, final String filename, final GridFSDBFile gridDBFile) {
		BCMGridFSInputFile bcmsInputFile = (BCMGridFSInputFile) this.createFile(content, filename);
		bcmsInputFile.setId(gridDBFile.getId());
		bcmsInputFile.setMetaData(gridDBFile.getMetaData());
		return bcmsInputFile;
	}

}
