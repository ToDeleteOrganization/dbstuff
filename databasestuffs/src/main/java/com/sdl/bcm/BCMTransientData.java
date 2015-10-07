package com.sdl.bcm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @deprecated class no longer used as helper for InitDocumentVisitor for adding Terminology
 */
@Deprecated
public class BCMTransientData {

    private static Map<String, BCMTransientData> entries = new ConcurrentHashMap<String, BCMTransientData>();

    private int maxAnnotationId = 0;

    private int maxTerminologyDataId = 0;

    private Integer maxContainerId = 0;


    public static void setValues(String documentId, int maxAnnotationId, int maxTerminologyId, int maxContainerId) {
        BCMTransientData entry = new BCMTransientData();
        entry.maxAnnotationId = ++maxAnnotationId;
        entry.maxTerminologyDataId = ++maxTerminologyId;
        entry.maxContainerId = ++maxContainerId;
        entries.put(documentId, entry);
    }

    public static BCMTransientData getBcmTransientData(String documentId) {
        return entries.get(documentId);
    }

    public int getNextAnnotationId() {
        return maxAnnotationId++;
    }

    public int getNextTerminologyDataId() {
        return maxTerminologyDataId++;
    }

    public String getNextContainerId() {
        return (maxContainerId++).toString();
    }
}
