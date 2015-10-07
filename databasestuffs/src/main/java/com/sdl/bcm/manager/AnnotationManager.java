package com.sdl.bcm.manager;

/**
 *  Created by dtarba on 6/12/2015.
 */
public class AnnotationManager {
    private static int annotationId = 1;

    private AnnotationManager() {
    }

    public static int nextAnnotationId() {
        return annotationId++;
    }

    public static void resetAnnotationId() {
        annotationId = 1;
    }
}
