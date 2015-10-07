package com.sdl.bcm.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated no longer in use since 2.5 (different add Terminology algorithm employed)
 */
@Deprecated
public class TransientData {
    private Map<String, Segment> segmentMap;
    private ParagraphUnit paragraphUnit;

    public TransientData() {
        segmentMap = new HashMap<>();
    }

    public Map<String, Segment> getSegmentMap() {
        return segmentMap;
    }

    public void setSegmentMap(Map<String, Segment> segmentMap) {
        this.segmentMap = segmentMap;
    }

    public ParagraphUnit getParagraphUnit() {
        return paragraphUnit;
    }

    public void setParagraphUnit(ParagraphUnit paragraphUnit) {
        this.paragraphUnit = paragraphUnit;
    }
}
