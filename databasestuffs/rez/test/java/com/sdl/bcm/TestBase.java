package com.sdl.bcm;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.sdl.bcm.manager.AnnotationManager;
import com.sdl.bcm.manager.TerminologyManager;
import com.sdl.bcm.model.Segment;
import com.sdl.bcm.model.fileskeleton.Term;
import com.sdl.bcm.model.fileskeleton.TermTranslation;
import com.sdl.bcm.model.fileskeleton.TerminologyData;
import com.sdl.bcm.utils.Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class TestBase {

    protected ObjectMapperWrapper objectMapper = new ObjectMapperWrapper();
    protected static boolean createDataFiles = false;

    @Before
    public void before() {
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        TerminologyManager.resetTerminologyDataId();
        AnnotationManager.resetAnnotationId();
    }

    @After
    public void after() {
        createDataFiles = false;
    }

    protected String logSegmentJSON(Segment segment) {
        try {
            String segmentJSON = objectMapper.writeValueAsString(segment);
            log("Segment JSON:\n" + segmentJSON);
            return segmentJSON;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void checkJSON(Segment segment, String fileName, String logMessage) {
        try {
            if (createDataFiles) {
            String segmentJSON = objectMapper.writeValueAsString(segment);
                Utils.writeFile(fileName, segmentJSON);
            } else {
                String dataReference = Utils.readFile(fileName);
                Segment reference = objectMapper.readValue(dataReference, Segment.class);
//                log(logMessage + segmentJSON);
                Assert.assertTrue(segment.equals(reference));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public abstract void log(String message);


    protected TerminologyData setTerminologyData(Integer terminologyDataId, String text, String termid, Integer score){

        TerminologyData terminologyData = new TerminologyData();

        terminologyData.setId(terminologyDataId);
        terminologyData.setOrigin("Term origin");

        List<Term> terms = new LinkedList<>();
        Term term = new Term();
        term.setId(termid);
        term.setScore(score);
        term.setText(text);
        List<TermTranslation> termTranslations = new LinkedList<>();
        TermTranslation termTranslation = new TermTranslation();
        termTranslation.setId(termid);
        termTranslation.setText("Matched " + text);
        termTranslations.add(termTranslation);
        term.setTermTranslations(termTranslations);
        terms.add(term);

        terminologyData.setTerms(terms);
        return terminologyData;
    }
}



