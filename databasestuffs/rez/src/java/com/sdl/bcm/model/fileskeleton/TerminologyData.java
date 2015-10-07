package com.sdl.bcm.model.fileskeleton;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sdl.bcm.manager.Utils;
import com.sdl.bcm.model.MetaData;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.List;

/**
 * Terminology data related to one or more terminology annotations within this file.
 * Contains one or more matching source terms and for each source term 0 or more translations.
 */
@JsonIgnoreProperties(value = { "metadata" })
public class TerminologyData extends AbstractSkeletonItem {

	private String origin;

	private List<Term> terms;

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
    protected void copyPropertiesTo(MetaData clone) {
    	TerminologyData termData = super.convertType(clone);
    	if (termData != null) {
    		super.copyPropertiesTo(termData);
    		termData.setOrigin(origin);
    		termData.setTerms(Utils.deepCloneList(terms));
    	}
    }

    @Override
    public TerminologyData deepClone() {
    	TerminologyData termData = new TerminologyData();
    	copyPropertiesTo(termData);
    	return termData;
    }

    @Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        TerminologyData that = (TerminologyData) o;
        return new EqualsBuilder()
                .append(origin, that.origin)
                .append(terms, that.terms)
                .isEquals();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (origin != null ? origin.hashCode() : 0);
        result = 31 * result + (terms != null ? terms.hashCode() : 0);
        return result;
    }

}
