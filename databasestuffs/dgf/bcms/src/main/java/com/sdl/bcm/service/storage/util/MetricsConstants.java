package com.sdl.bcm.service.storage.util;

public class MetricsConstants {

	public static final String WEB = "web.";

	public static final String TIMED = ".timed";

	public static final String COUNTED = ".counted";

	public static final String EXCEPTION = ".exception";

	// add document
	private static final String ADD_DOCUMENT = "addDocument";

	public static final String ADD_DOCUMENT_TIMED = WEB + ADD_DOCUMENT + TIMED;

	public static final String ADD_DOCUMENT_COUNTED = WEB + ADD_DOCUMENT + COUNTED;

	public static final String ADD_DOCUMENT_EXCEPTION = WEB + ADD_DOCUMENT + EXCEPTION;

	// add document mongo
	private static final String ADD_DOCUMENT_MONGO = "addDocumentMongo";

	public static final String ADD_DOCUMENT_TIMED_MONGO = WEB + ADD_DOCUMENT_MONGO + TIMED;

	public static final String ADD_DOCUMENT_COUNTED_MONGO = WEB + ADD_DOCUMENT_MONGO + COUNTED;

	public static final String ADD_DOCUMENT_EXCEPTION_MONGO = WEB + ADD_DOCUMENT_MONGO + EXCEPTION;

	// getDocument
	private static final String GET_DOCUMENT = "getDocument";

	public static final String GET_DOCUMENT_TIMED = WEB + GET_DOCUMENT + TIMED;

	public static final String GET_DOCUMENT_COUNTED = WEB + GET_DOCUMENT + COUNTED;

	public static final String GET_DOCUMENT_EXCEPTION = WEB + GET_DOCUMENT + EXCEPTION;

	// getDocument fs.files mongo
	private static final String GET_FS_FILES_MONGO = "getFilesMongo";

	public static final String GET_FILES_MONGO_TIMED = WEB + GET_FS_FILES_MONGO + TIMED;

	public static final String GET_FILESC_MONGO_COUNTED = WEB + GET_FS_FILES_MONGO + COUNTED;

	public static final String GET_FILES_MONGO_EXCEPTION = WEB + GET_FS_FILES_MONGO + EXCEPTION;

	// getDocument fs.chunks mongo
	private static final String GET_FS_CHUNKS_MONGO = "getChunksMongo";

	public static final String GET_FS_CHUNKS_DOC_TIMED_MONGO = WEB + GET_FS_CHUNKS_MONGO + TIMED;

	public static final String GET_FS_CHUNKS_DOC_COUNTED_MONGO = WEB + GET_FS_CHUNKS_MONGO + COUNTED;

	public static final String GET_FS_CHUNKS_DOC_EXCEPTION_MONGO = WEB + GET_FS_CHUNKS_MONGO + EXCEPTION;

	// newRevision
	private static final String NEW_REVISION = "newRevision";

	public static final String NEW_REVISION_TIMED = WEB + NEW_REVISION + TIMED;

	public static final String NEW_REVISION_COUNTED = WEB + NEW_REVISION + COUNTED;

	public static final String NEW_REVISION_EXCEPTION = WEB + NEW_REVISION + EXCEPTION;

	// updateDocument
	private static final String UPDATE_DOCUMENT = "updateDocument";

	public static final String UPDATE_DOCUMENT_TIMED = WEB + UPDATE_DOCUMENT + TIMED;

	public static final String UPDATE_DOCUMENT_COUNTED = WEB + UPDATE_DOCUMENT + COUNTED;

	public static final String UPDATE_DOCUMENT_EXCEPTION = WEB + UPDATE_DOCUMENT + EXCEPTION;

	// update Mongo Document
	private static final String UPDATE_MONGO_DOCUMENT = "updateMongo";

	public static final String UPDATE_MONGO_DOCUMENT_TIMED = WEB + UPDATE_MONGO_DOCUMENT + TIMED;

	public static final String UPDATE_MONGO_DOCUMENT_COUNTED = WEB + UPDATE_MONGO_DOCUMENT + COUNTED;

	public static final String UPDATE_MONGO_DOCUMENT_EXCEPTION = WEB + UPDATE_MONGO_DOCUMENT + EXCEPTION;

	// deleteDocument
	private static final String DELETE_DOCUMENT = "deleteDocument";

	public static final String DELETE_DOCUMENT_TIMED = WEB + DELETE_DOCUMENT + TIMED;

	public static final String DELETE_DOCUMENT_COUNTED = WEB + DELETE_DOCUMENT + COUNTED;

	public static final String DELETE_DOCUMENT_EXCEPTION = WEB + DELETE_DOCUMENT + EXCEPTION;

	// revert
	private static final String REVERT = "revert";

	public static final String REVERT_TIMED = WEB + REVERT + TIMED;

	public static final String REVERT_COUNTED = WEB + REVERT + COUNTED;

	public static final String REVERT_EXCEPTION = WEB + REVERT + EXCEPTION;

	// copyToTargetLanguage
	private static final String COPY_TO_TARGET_LANGUAGE = "copyToTargetLanguage";

	public static final String COPY_TO_TARGET_LANGUAGE_TIMED = WEB + COPY_TO_TARGET_LANGUAGE + TIMED;

	public static final String COPY_TO_TARGET_LANGUAGE_COUNTED = WEB + COPY_TO_TARGET_LANGUAGE + COUNTED;

	public static final String COPY_TO_TARGET_LANGUAGE_EXCEPTION = WEB + COPY_TO_TARGET_LANGUAGE + EXCEPTION;

	// getDocuments
	private static final String GET_DOCUMENTS = "getDocuments";

	public static final String GET_DOCUMENTS_TIMED = WEB + GET_DOCUMENTS + TIMED;

	public static final String GET_DOCUMENTS_COUNTED = WEB + GET_DOCUMENTS + COUNTED;

	public static final String GET_DOCUMENTS_EXCEPTION = WEB + GET_DOCUMENTS + EXCEPTION;

	// addFullDocument
	private static final String ADD_FULL_DOCUMENT = "addFullDocument";
	
	public static final String ADD_FULL_DOCUMENT_TIMED = WEB + ADD_FULL_DOCUMENT + TIMED;
	
	public static final String ADD_FULL_DOCUMENT_COUNTED = WEB + ADD_FULL_DOCUMENT + COUNTED;
	
	public static final String ADD_FULL_DOCUMENT_EXCEPTION = WEB + ADD_FULL_DOCUMENT + EXCEPTION;

	// updateFullDocument
	private static final String UPDATE_FULL_DOCUMENT = "updateFullDocument";
	
	public static final String UPDATE_FULL_DOCUMENT_TIMED = WEB + UPDATE_FULL_DOCUMENT + TIMED;
	
	public static final String UPDATE_FULL_DOCUMENT_COUNTED = WEB + UPDATE_FULL_DOCUMENT + COUNTED;
	
	public static final String UPDATE_FULL_DOCUMENT_EXCEPTION = WEB + UPDATE_FULL_DOCUMENT + EXCEPTION;

	// getFullDocument
	private static final String GET_FULL_DOCUMENT = "getFullDocument";
	
	public static final String GET_FULL_DOCUMENT_TIMED = WEB + GET_FULL_DOCUMENT + TIMED;
	
	public static final String GET_FULL_DOCUMENT_COUNTED = WEB + GET_FULL_DOCUMENT + COUNTED;
	
	public static final String GET_FULL_DOCUMENT_EXCEPTION = WEB + GET_FULL_DOCUMENT + EXCEPTION;
	
	// deleteFullDocument
	private static final String DELETE_FULL_DOCUMENT = "deleteFullDocument";

	public static final String DELETE_FULL_DOCUMENT_TIMED = WEB + DELETE_FULL_DOCUMENT + TIMED;

	public static final String DELETE_FULL_DOCUMENT_COUNTED = WEB + DELETE_FULL_DOCUMENT + COUNTED;

	public static final String DELETE_FULL_DOCUMENT_EXCEPTION = WEB + DELETE_FULL_DOCUMENT + EXCEPTION;

	private MetricsConstants() {
	}
}


// ce inseaman queue inbound???