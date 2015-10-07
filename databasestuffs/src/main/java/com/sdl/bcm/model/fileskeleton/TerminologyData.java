package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.model.MetaData;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.List;

/**
 * Terminology data related to one or more terminology annotations within this file.
 * Contains one or more matching source terms and for each source term 0 or more translations.
 */
@JsonIgnoreProperties(value = { "metadata" })
public class TerminologyData extends MetaData{
    private int id;
    private String origin;
    private List<Term> terms;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TerminologyData that = (TerminologyData) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(origin, that.origin)
                .append(terms, that.terms)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        result = 31 * result + (origin != null ? origin.hashCode() : 0);
        result = 31 * result + (terms != null ? terms.hashCode() : 0);
        return result;
    }

}
