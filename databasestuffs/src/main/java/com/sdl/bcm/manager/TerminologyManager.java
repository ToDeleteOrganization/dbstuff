package com.sdl.bcm.manager;

import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.model.TerminologyAnnotationContainer;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import com.sdl.bcm.model.fileskeleton.TerminologyData;

import java.util.UUID;

/**
 * @author Dan Tarba
 * @since 2.5
 */
public class TerminologyManager {

    private static int terminologyDataId = 1;

    private TerminologyManager() {
    }

    public static int nextTerminologyDataId() {
        return terminologyDataId++;
    }

    public static void resetTerminologyDataId() {
        terminologyDataId = 1;
    }

    /**
     * Creates a new terminology container. Also adds the terminology data to the skeleton so it can be reused.
     */
    public static TerminologyAnnotationContainer createTerminologyContainer(MarkupData parent, TerminologyData terminologyData, FileSkeleton fileSkeleton) {

        TerminologyAnnotationContainer terminologyContainer = new TerminologyAnnotationContainer(parent);
        terminologyContainer.setId(UUID.randomUUID().toString());
        terminologyContainer.setAnnotationId(AnnotationManager.nextAnnotationId());

        int terminologyDataId = addTerminologyDataToSkeleton(terminologyData, fileSkeleton);
        terminologyContainer.setTerminologyDataId(terminologyDataId);
        return terminologyContainer;
    }

    /**
     * no parent version of new terminology container creation
     */
    public static TerminologyAnnotationContainer createTerminologyContainer(TerminologyData terminologyData, FileSkeleton fileSkeleton) {

        TerminologyAnnotationContainer terminologyContainer = new TerminologyAnnotationContainer();
        terminologyContainer.setId(UUID.randomUUID().toString());
        terminologyContainer.setAnnotationId(AnnotationManager.nextAnnotationId());

        int terminologyDataId = addTerminologyDataToSkeleton(terminologyData, fileSkeleton);
        terminologyContainer.setTerminologyDataId(terminologyDataId);
        return terminologyContainer;
    }

    public static int addTerminologyDataToSkeleton(TerminologyData terminologyData, FileSkeleton fileSkeleton) {
        int termDataId = -1;
        for (TerminologyData termData : fileSkeleton.getTerminologyData()) {
            if (termData.equals(terminologyData)) {
                termDataId = termData.getId();
                break;
            }
        }
        if (termDataId == -1) {
            termDataId = TerminologyManager.nextTerminologyDataId();
            fileSkeleton.getTerminologyData().add(terminologyData);
            terminologyData.setId(termDataId);
        }
        return termDataId;
    }
}
