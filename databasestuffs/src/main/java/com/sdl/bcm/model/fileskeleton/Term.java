package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.List;

/**
 * A matching source term with a score for the quality of the match.
 */
@JsonIgnoreProperties(value = { "metadata" })
public class Term extends MetaData{
    private String id;
    private String text;
    private double score;
    private List<TermTranslation> termTranslations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<TermTranslation> getTermTranslations() {
        return termTranslations;
    }

    public void setTermTranslations(List<TermTranslation> termTranslations) {
        this.termTranslations = termTranslations;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        Term that = (Term) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(text, that.text)
                .append(score, that.score)
                .append(termTranslations, that.termTranslations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        long temp;
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (termTranslations != null ? termTranslations.hashCode() : 0);
        return result;
    }

}
