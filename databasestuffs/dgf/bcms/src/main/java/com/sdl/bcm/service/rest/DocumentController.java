package com.sdl.bcm.service.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sdl.bcm.api.response.AddDocumentResponse;
import com.sdl.bcm.api.response.CopyToTargetLanguageResponse;
import com.sdl.bcm.api.response.DocumentIdAndIsoCodePair;
import com.sdl.bcm.api.response.GetStorageDocumentsResponse;
import com.sdl.bcm.model.DocumentFile;
import com.sdl.bcm.rest.http.RestLink;
import com.sdl.bcm.service.exception.EndPointNotFoundException;
import com.sdl.bcm.service.storage.DocumentMetadataKeys;
import com.sdl.bcm.service.storage.util.MetricsConstants;

@Controller
@RequestMapping(value = "/bcm")
public class DocumentController extends OldBCMServiceController {
    private static final Logger LOG = Logger.getLogger(DocumentController.class);

    @Value("${show.all.documents:false}")
    private boolean isShowAllDocumentsAllowed;

    @ResponseBody
    @RequestMapping(value = "/documents", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8")
    @Timed(name = MetricsConstants.ADD_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.ADD_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.ADD_DOCUMENT_EXCEPTION, absolute = true)
    public AddDocumentResponse addDocument(@RequestBody DocumentFile doc) {
        LOG.info("REST Call: addDocument(" + doc.getId() + ")");

        StopWatch stopWatch = new StopWatch();

        stopWatch.start("storing document");
        dataStorageService.addDocument(doc.getId(), doc.getDocJSON(), doc.getProperties());

        AddDocumentResponse response = new AddDocumentResponse();
        List<RestLink> responseLinks = new ArrayList<>();
        String bcmDocumentURL = bcmHost + "/" + bcmAppPrefix + "/bcm/documents/" + doc.getId();
        responseLinks.add(new RestLink(doc.getId(), "bcmDocDownloadUrl", "GET", bcmDocumentURL));
        responseLinks.add(new RestLink(doc.getId(), "bcmDocUploadUrl", "PUT", bcmDocumentURL));
        response.setBcmDocumentId(doc.getId());
        response.setLinks(responseLinks);
        stopWatch.stop();

        statisticsTraceableLogger.logStatistic("controller addDocument", doc.getId(), stopWatch);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    @Timed(name = MetricsConstants.GET_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.GET_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.GET_DOCUMENT_EXCEPTION, absolute = true)
    public String getDocument(@PathVariable("documentId") String documentId) {
        LOG.info("REST Call: getDocument(" + documentId + ")");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("retrieving from storage document");
        DocumentFile fullDocument = dataStorageService.getDocument(documentId);
        stopWatch.stop();

        checkForNonNullDocument(fullDocument, documentId, "Document with ID [" + documentId + "] was not found for getDocument().");
        statisticsTraceableLogger.logStatistic("controller getDocument", documentId, stopWatch);

        return (fullDocument != null) ? fullDocument.getDocJSON() : StringUtils.EMPTY;
    }

    @ResponseBody
    @RequestMapping(value = "/documents/{documentId}/newRevision", method = RequestMethod.GET, consumes = "text/plain;charset=UTF-8")
    @Timed(name = MetricsConstants.NEW_REVISION_TIMED, absolute = true)
    @Counted(name = MetricsConstants.NEW_REVISION_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.NEW_REVISION_EXCEPTION, absolute = true)
    public String newRevision(@PathVariable("documentId") String documentId) {
        LOG.info("REST Call: newRevision(" + documentId + ")");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start("getting latest version of document");
        DocumentFile document = dataStorageService.getDocument(documentId);
        stopWatch.stop();

        checkForNonNullDocument(document, documentId, "Document with ID [" + documentId + "] was not found for newRevision().");
        statisticsTraceableLogger.logStatistic("controller newRevision", documentId, stopWatch);

        Integer newVersion = (Integer) document.getProperties().get(DocumentMetadataKeys.VERSION_KEY) + 1;
        document.getProperties().put(DocumentMetadataKeys.VERSION_KEY, newVersion);
        stopWatch.start("saving version " + newVersion + " of document");

        dataStorageService.addDocument(documentId, document.getDocJSON(), document.getProperties());

        stopWatch.stop();
        statisticsTraceableLogger.logStatistic("controller newRevision", document.getId(), stopWatch);
        return newVersion.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.PUT, consumes = "text/plain;charset=UTF-8")
    @Timed(name = MetricsConstants.UPDATE_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.UPDATE_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.UPDATE_DOCUMENT_EXCEPTION, absolute = true)
    public String updateDocument(@PathVariable("documentId") String documentId, @RequestBody String doc) {
        LOG.info("REST Call: updateDocument(" + documentId + ")");
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("updating document");
        dataStorageService.updateDocument(documentId, doc);
        stopWatch.stop();

        statisticsTraceableLogger.logStatistic("controller updateDocument", documentId, stopWatch);
        return documentId;
    }

    @ResponseBody
    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.DELETE, produces = "text/plain;charset=UTF-8")
    @Timed(name = MetricsConstants.DELETE_DOCUMENT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.DELETE_DOCUMENT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.DELETE_DOCUMENT_EXCEPTION, absolute = true)
    public String deleteDocument(@PathVariable("documentId") String documentId) throws JsonProcessingException {
        LOG.info("REST Call: deleteDocument(" + documentId + ")");
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("deleting document");
        dataStorageService.deleteFullDocument(documentId);
        stopWatch.stop();

        statisticsTraceableLogger.logStatistic("controller deleteDocument", documentId, stopWatch);
        return documentId;
    }

    @ResponseBody
    @RequestMapping(value = "/documents/{documentId}/revert/{version}", method = RequestMethod.PUT)
    @Timed(name = MetricsConstants.REVERT_TIMED, absolute = true)
    @Counted(name = MetricsConstants.REVERT_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.REVERT_EXCEPTION, absolute = true)
    public void revert(@PathVariable("documentId") String documentId, @PathVariable("version") Integer version) {
        LOG.info("REST Call: revert(" + documentId + "," + version + ")");
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("reverting document");
        dataStorageService.revert(documentId, version);
        stopWatch.stop();

        statisticsTraceableLogger.logStatistic("controller revert", documentId, stopWatch);
    }

    @ResponseBody
    @RequestMapping(value = "/documents/{documentId}/copyToTargetLanguage", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @Timed(name = MetricsConstants.COPY_TO_TARGET_LANGUAGE_TIMED, absolute = true)
    @Counted(name = MetricsConstants.COPY_TO_TARGET_LANGUAGE_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.COPY_TO_TARGET_LANGUAGE_EXCEPTION, absolute = true)
    public CopyToTargetLanguageResponse copyToTargetLanguage(@PathVariable("documentId") String documentId, @RequestParam("isoCode") String[] isoCodes) throws IOException {
        CopyToTargetLanguageResponse response = new CopyToTargetLanguageResponse();
        LOG.info("REST Call: copyToTargetLanguage(" + documentId + " -> " + Arrays.toString(isoCodes) + ")");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("copying to target languages document");

        List<DocumentIdAndIsoCodePair> pairList = dataStorageService.copyToTargetLanguage(documentId, isoCodes);
        response.setIdAndIsoCodePairList(pairList);

        stopWatch.stop();
        statisticsTraceableLogger.logStatistic("controller copyToTargetLanguage", documentId, stopWatch);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/documents", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Timed(name = MetricsConstants.GET_DOCUMENTS_TIMED, absolute = true)
    @Counted(name = MetricsConstants.GET_DOCUMENTS_COUNTED, absolute = true)
    @ExceptionMetered(name = MetricsConstants.GET_DOCUMENTS_EXCEPTION, absolute = true)
    public GetStorageDocumentsResponse getDocuments() {
        LOG.info("REST Call: getDocuments()");
        if (!isShowAllDocumentsAllowed) {
            EndPointNotFoundException epnfe = new EndPointNotFoundException("Not Found");
            epnfe.setCustomReason("GET '/documents', not available for this environment. Host is [" + bcmHost + "].");
            throw epnfe;
        }

        GetStorageDocumentsResponse response = new GetStorageDocumentsResponse();
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("getting documents");
        Map<String, String> result = dataStorageService.getStoredDocuments();
        stopWatch.stop();

        response.setDocsIds(result);
        
        statisticsTraceableLogger.logStatistic("controller getAllDocuments", stopWatch);
        return response;
    }
}
