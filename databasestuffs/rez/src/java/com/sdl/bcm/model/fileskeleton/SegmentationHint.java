package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SegmentationHint {

    /**
     * <summary>
     * If possible, the segmentation engine should always include this tag inside a segment.
     * </summary>
     * <remarks>
     * <para>
     * Tags with this property should be treated similar to tags with "TagHandlingAlwaysInclude"
     * in Filter Framework 1 and "placeholder" Trados tools.
     * </para>
     * </remarks>
     */
    INCLUDE("Include"),
    /**
     * <summary>
     * The tag may be left outside of the segment, e.g. if it
     * appears at a segment boundary.
     * </summary>
     * <remarks>
     * <para>
     * There should be no need for a segmentation engine to check the
     * <c>IPlaceholderTagProperties.TextEquivalent</c> property, as this value
     * explicitly states that the tag can be excluded from segments.
     * </para>
     * </remarks>
     */
    MAY_EXCLUDE("MayExclude"),

    /**
     * <summary>
     * The tag will be included in a segment if the segment also contains text content,
     * otherwise it will not be included in a segment (in order to prevent segment-only tags).
     * </summary>
     */
    INCLUDE_WITH_TEXT("IncludeWithText"),

    /**
     * <summary>
     * The tag will be excluded from a segment, even if this means changing the segmentation. This
     * is used for sub-content processing.
     * </summary>
     * <remarks>
     * There are two circumstances where a tag with SegmentationHint set to Exclude will not be excluded from a segment;
     * where the tag is inside a tag pair with SegmentationHint NOT set to Exclude or where the tag is inside a review marker.
     * </remarks>
     */
    EXCLUDE("Exclude");

    private String name;

    SegmentationHint(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

}
