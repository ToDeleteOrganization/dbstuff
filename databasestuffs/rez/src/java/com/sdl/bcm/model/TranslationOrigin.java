package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.bcm.visitor.BCMVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Information about the origin of the translation. Only valid for target segments.
 */
public class TranslationOrigin extends MetaData {
    /**
     * The origin of the translation for the segment pair.
     * Typically one of the following pre-defined values:
     * - "interactive" (The segment has been manually adapted or translated from scratch.)
     * - "document-match" (Batch translation by applying a Context TM type tool like PerfectMatch)
     * - "mt" (Machine translated content)
     * - "tm" (Batch pre-translation using a fuzzy or 100% match)
     * - "not-translated" (The segment has not yet been translated. This is usually an empty segment.)
     * - "auto-propagated" (The segment has been translated using AutoPropagation from internal matches.)
     * - "source" (The segment has been translated by copying the source to the target.)
     * - "auto-aligned" ( The translated segment was created by an automated linguistic alignment of previously
     * translated source and target content.)
     * - "unknown" (The segment was translated by an unknown tool - usually from a third party provider.)
     */
    private String originType;

    /**
     * The translation memory, machine translation system, or a similar source from which the translation originates,
     * or null if unknown or not applicable.
     */
    private String originSystem;

    /**
     * The match value for a translation memory match, or the confidence level for a machine translation.
     * Should be a percentage value from 0 - 100%
     */
    private int matchPercent;

    /**
     * If true, indicates that the translation originates from a system that takes structural context information into
     * account, and that the structure context matches.
     */
    private boolean isStructureContextMatch;

    /**
     * Indicates whether the match originates from a source where the textual surrounding content corresponds
     * to the text surrounding this segment.
     */
    private TextContextMatchLevel textContextMatchLevel;

    /**
     * A hash value that identifies the entry in the TM that provided the translation.
     * This is used to update the correct TM entry if a translator provides an updated version of the translation.
     */
    private String originalTranslationHash;

    private TranslationOrigin originBeforeAdaptation;

    public TranslationOrigin() {
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getOriginSystem() {
        return originSystem;
    }

    public void setOriginSystem(String originSystem) {
        this.originSystem = originSystem;
    }

    public int getMatchPercent() {
        return matchPercent;
    }

    public void setMatchPercent(int matchPercent) {
        this.matchPercent = matchPercent;
    }

    @JsonProperty("isStructureContextMatch")
    public boolean isStructureContextMatch() {
        return isStructureContextMatch;
    }

    @JsonProperty("isStructureContextMatch")
    public void setStructureContextMatch(boolean isStructureContextMatch) {
        this.isStructureContextMatch = isStructureContextMatch;
    }

    public TextContextMatchLevel getTextContextMatchLevel() {
        return textContextMatchLevel;
    }

    public void setTextContextMatchLevel(TextContextMatchLevel textContextMatchLevel) {
        this.textContextMatchLevel = textContextMatchLevel;
    }

    public String getOriginalTranslationHash() {
        return originalTranslationHash;
    }

    public void setOriginalTranslationHash(String originalTranslationHash) {
        this.originalTranslationHash = originalTranslationHash;
    }

    public TranslationOrigin getOriginBeforeAdaptation() {
        return originBeforeAdaptation;
    }

    public void setOriginBeforeAdaptation(TranslationOrigin originBeforeAdaptation) {
        this.originBeforeAdaptation = originBeforeAdaptation;
    }


    @Override
    public boolean accept(BCMVisitor visitor) {
        return true;
    }

    @Override
    protected void copyPropertiesTo(MetaData clone) {
    	TranslationOrigin tp = convertType(clone);
    	if (tp != null) {
    		super.copyPropertiesTo(clone);
    		tp.setOriginType(originType);
    		tp.setOriginSystem(originSystem);
    		tp.setMatchPercent(matchPercent);
    		tp.setStructureContextMatch(isStructureContextMatch);
    		tp.setTextContextMatchLevel(textContextMatchLevel);
    		tp.setOriginalTranslationHash(originalTranslationHash);
    		tp.setOriginBeforeAdaptation(originBeforeAdaptation);
    	}
    }

    @Override
    public TranslationOrigin deepClone() {
    	TranslationOrigin to = new TranslationOrigin();
    	copyPropertiesTo(to);
    	return to;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TranslationOrigin that = (TranslationOrigin) o;
        return new EqualsBuilder()
                .append(originType, that.originType)
                .append(originSystem, that.originSystem)
                .append(matchPercent, that.matchPercent)
                .append(isStructureContextMatch, that.isStructureContextMatch)
                .append(textContextMatchLevel, that.textContextMatchLevel)
                .append(originalTranslationHash, that.originalTranslationHash)
                .append(originBeforeAdaptation, that.originBeforeAdaptation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (originType != null ? originType.hashCode() : 0);
        result = 31 * result + (originSystem != null ? originSystem.hashCode() : 0);
        result = 31 * result + matchPercent;
        result = 31 * result + (isStructureContextMatch ? 1 : 0);
        result = 31 * result + (textContextMatchLevel != null ? textContextMatchLevel.hashCode() : 0);
        result = 31 * result + (originalTranslationHash != null ? originalTranslationHash.hashCode() : 0);
        result = 31 * result + (originBeforeAdaptation != null ? originBeforeAdaptation.hashCode() : 0);
        return result;
    }
}
