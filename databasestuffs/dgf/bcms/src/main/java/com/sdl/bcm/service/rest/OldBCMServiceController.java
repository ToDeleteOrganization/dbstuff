package com.sdl.bcm.service.rest;

import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.rest.http.RestLink;
import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.sdl.bcm.api.response.AddDocumentResponse;
import com.sdl.bcm.service.exception.DocumentNotFoundException;
import com.sdl.bcm.service.monitoring.StatisticsLogger;
import com.sdl.bcm.service.storage.StorageService;
import com.sdl.bcm.service.storage.util.MetricsConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by dtarba on 3/20/2015.
 */
public abstract class OldBCMServiceController extends AbstractController {
    private static final Logger LOG = Logger.getLogger(OldBCMServiceController.class);

    @Autowired(required = true)
    protected StorageService dataStorageService;

    @Value("${bcm.host}")
    protected String bcmHost;

    @Value("${bcm.app.prefix}")
    protected String bcmAppPrefix;

    @Autowired
    protected StatisticsLogger statisticsTraceableLogger;

    /**
     * @deprecated use addDocument
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "/fullDocument", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8")
    @Timed(name = MetricsConstants.ADD_FULL_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.ADD_FULL_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.ADD_FULL_DOCUMENT_EXCEPTION, absolute = true)
    public AddDocumentResponse addFullDocument(@RequestBody DocumentFile doc) {
        LOG.info("REST Call: addFullDocument(" + doc.getId() + ")");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("adding document");

        dataStorageService.addDocument(doc.getId(), doc.getDocJSON(), doc.getProperties());

        stopWatch.stop();
        statisticsTraceableLogger.logStatistic("old controller addDocument", doc.getId(), stopWatch);

        stopWatch.start("building response");

        AddDocumentResponse response = new AddDocumentResponse();
        List<RestLink> links = new ArrayList<>();
        String bcmFullDocumentURL = bcmHost + "/" + bcmAppPrefix + "/bcm/fullDocument/" + doc.getId();
        links.add(new RestLink(doc.getId(), "bcmFullDocDownloadUrl", "GET", bcmFullDocumentURL));
        links.add(new RestLink(doc.getId(), "bcmFullDocUploadUrl", "PUT", bcmFullDocumentURL));
        response.setBcmDocumentId(doc.getId());
        response.setLinks(links);
        stopWatch.stop();
        statisticsTraceableLogger.logStatistic("old controller addDocument response", doc.getId(), stopWatch);
        return response;
    }

    /**
     * @deprecated use updateDocument
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "/fullDocument/{documentId}", method = RequestMethod.PUT, consumes = "text/plain;charset=UTF-8")
    @Timed(name = MetricsConstants.UPDATE_FULL_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.UPDATE_FULL_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.UPDATE_FULL_DOCUMENT_EXCEPTION, absolute = true)
    public String updateFullDocument(@PathVariable("documentId") String documentId, @RequestBody String doc) {
        LOG.info("REST Call: updateDocument(" + documentId + ")");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("updating document");

        dataStorageService.updateDocument(documentId, doc);

        stopWatch.stop();
        statisticsTraceableLogger.logStatistic("old controller updateDocument", documentId, stopWatch);
        return documentId;
    }

    /**
     * @deprecated use getDocument
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "/fullDocument/{documentId}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    @Timed(name = MetricsConstants.GET_FULL_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.GET_FULL_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.GET_FULL_DOCUMENT_EXCEPTION, absolute = true)
    public String getFullDocument(@PathVariable("documentId") String documentId) {
        LOG.info("REST Call: getDocument(" + documentId + ")");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("getting document");
        DocumentFile fullDocument = dataStorageService.getDocument(documentId);
        stopWatch.stop();

        checkForNonNullDocument(fullDocument, documentId, "Document with ID [" + documentId + "] was not found for getFullDocument().");
        statisticsTraceableLogger.logStatistic("old controller getDocument", documentId, stopWatch);

        return (fullDocument != null) ? fullDocument.getDocJSON() : StringUtils.EMPTY;
    }

    /**
     * @deprecated use deleteDocument
     */
    @Deprecated
    @ResponseBody
    @RequestMapping(value = "/deleteFullDocument/{documentId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Timed(name = MetricsConstants.DELETE_FULL_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.DELETE_FULL_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.DELETE_FULL_DOCUMENT_EXCEPTION, absolute = true)
    public String deleteFullDocument(@PathVariable("documentId") String documentId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("updating document");

        dataStorageService.deleteFullDocument(documentId);

        stopWatch.stop();
        statisticsTraceableLogger.logStatistic("old controller deleteDocument", documentId, stopWatch);
        return documentId;
    }

    protected void checkForNonNullDocument(DocumentFile documentFile, String docId, String customMessage) {
        if (documentFile == null) {
            DocumentNotFoundException doc = new DocumentNotFoundException();
            doc.setCustomReason(customMessage);
            doc.setDocumentId(docId);
            throw doc;
        }
    }
}

