package com.sdl.bcm.service.storage.gridfs;

import org.springframework.stereotype.Component;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.mongodb.gridfs.GridFSDBFile;
import com.sdl.bcm.service.storage.util.MetricsConstants;

@Component
public class MeasurableGridFSRepository {

	@Timed(name = MetricsConstants.GET_FS_CHUNKS_DOC_TIMED_MONGO, absolute = true)
	@Counted(name = MetricsConstants.GET_FS_CHUNKS_DOC_COUNTED_MONGO, absolute = true)
	@ExceptionMetered(name = MetricsConstants.GET_FS_CHUNKS_DOC_EXCEPTION_MONGO, absolute = true)
	public String getDocumentAsJson(GridFSRepository gridRepo, GridFSDBFile latestFile) {
		return gridRepo.getDocumentAsJson(latestFile);
	}

	@Timed(name = MetricsConstants.GET_FILES_MONGO_TIMED, absolute = true)
	@Counted(name = MetricsConstants.GET_FILESC_MONGO_COUNTED, absolute = true)
	@ExceptionMetered(name = MetricsConstants.GET_FILES_MONGO_EXCEPTION, absolute = true)
	public GridFSDBFile getLatestVersionOfDocument(GridFSRepository gridRepo,String documentId) {
		return gridRepo.getLatestVersionOfDocument(documentId);
	}

}
