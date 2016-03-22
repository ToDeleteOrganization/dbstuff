package com.sdl.bcm.service.model;

import java.io.InputStream;

import com.mongodb.DBObject;

public class DocumentFileMongoObject {

	private InputStream inputStream;

	private String documentId;

	private DBObject metaData;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public DBObject getMetaData() {
		return metaData;
	}

	public void setMetaData(DBObject MetaData) {
		this.metaData = MetaData;
	}

	public boolean hasMetadata() {
		return (this.metaData != null) && !this.metaData.keySet().isEmpty();
	}
}
