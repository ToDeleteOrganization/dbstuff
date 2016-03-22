package com.sdl.bcm.service.storage.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.service.model.DocumentFileMongoObject;
import com.sdl.bcm.service.storage.DocumentMetadataKeys;

public class ConverterUtil {

	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static DocumentFileMongoObject convertToDocumentFileStreamDTO(String docId, String docContent, Map<String, Object> docProp) {
		DocumentFileMongoObject docDto = new DocumentFileMongoObject();
		docDto.setDocumentId(docId);
		docDto.setMetaData(convertPropertiesToMongoDBObject(docProp));
		docDto.setInputStream(new ByteArrayInputStream(docContent.getBytes(UTF8)));
		return docDto;
	}

	public static DBObject convertPropertiesToMongoDBObject(Map<String, Object> properties) {
		if (properties == null) {
			return null;
		}

		DBObject metaData = new BasicDBObject();
		if (properties.isEmpty()) {
			return metaData;
		}

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			metaData.put(entry.getKey(), entry.getValue());
		}

		return metaData;
	}

	public static DocumentFile convertGridFsFileToFullDocument(GridFSDBFile latestFile) {
		final DocumentFile fullDocument = new DocumentFile();

		final DBObject docMetaData = latestFile.getMetaData();
		if (docMetaData != null) {
			
			final Map<String, Object> properties = new HashMap<>();
			for (String key : docMetaData.keySet()) {
				properties.put(key, docMetaData.get(key));
			}

			// set properties
			fullDocument.setProperties(properties);

			// set document ID from properties ???
			if (properties.get(DocumentMetadataKeys.ID_KEY) != null) {
				fullDocument.setId((String) properties.get(DocumentMetadataKeys.ID_KEY));
			}
		}

		return fullDocument;
	}
}
