package com.sdl.bcm.visitor.impl;

import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.BCMAbstractVisitor;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated since 2.5 as new apply terminology algorithm is used
 */
@Deprecated
public class InitDocumentVisitor extends BCMAbstractVisitor {
    private static final Logger LOG = Logger.getLogger(InitDocumentVisitor.class);
    private int maxAnnotationId = 0;
    private int maxTerminologyDataId = 0;
    private int maxContainerId = 0;
    private ParagraphUnit paragraphUnit;
    private Map<String, Segment> segmentMap;
    private File currentFile;
    private Document document;
    private int fileParagraphUnitsCount = 0;
    private int fileStructureParagraphUnitsCount = 0;

    private int documentParagraphUnitsCount = 0;
    private int documentStructureParagraphUnitsCount = 0;

    public InitDocumentVisitor() {
        segmentMap = new HashMap();
    }


    @Override
    public boolean visitEnter(File element) {
        currentFile = element;
        element.setParentDocument(document);
        fileParagraphUnitsCount = 0;
        fileStructureParagraphUnitsCount = 0;
        return true;
    }

    @Override
    public boolean visitLeave(File element) {
        currentFile = null;
        documentStructureParagraphUnitsCount += fileStructureParagraphUnitsCount;
        fileStructureParagraphUnitsCount = 0;
        documentParagraphUnitsCount += fileParagraphUnitsCount;
        fileParagraphUnitsCount = 0;
        return true;
    }

    @Override
    public boolean visitEnter(Document element) {
        document = element;
        return true;
    }

    @Override
    public boolean visit(ParagraphUnit element) {
        paragraphUnit = element;
        element.getSource().accept(this);
        element.getTarget().accept(this);
        paragraphUnit = null;
        element.setParentFile(currentFile);
        if (currentFile != null) {
            element.setParentFileId(currentFile.getId());
        }
        if(element.isStructure()) {
            fileStructureParagraphUnitsCount++;
        }
        fileParagraphUnitsCount++;
        return true;
    }

    @Override
    public boolean visitEnter(Paragraph element) {
        segmentMap.clear();
        return true;
    }

    @Override
    public boolean visitLeave(Paragraph element) {
        return true;
    }

    @Override
    public boolean visitEnter(Segment element) {
        segmentMap.put(element.getSegmentNumber(), element);
        return true;
    }

    @Override
    public boolean visitEnter(TerminologyAnnotationContainer element) {
        maxAnnotationId = Math.max(maxAnnotationId, element.getAnnotationId());
        maxTerminologyDataId = Math.max(maxTerminologyDataId, element.getTerminologyDataId());
        extractMaxId(element);
        return true;
    }

    @Override
    public boolean visitEnter(TagPair element) {
        extractMaxId(element);
        return true;
    }

    @Override
    public boolean visit(PlaceholderTag element) {
        extractMaxId(element);
        return true;
    }

    @Override
    public boolean visit(StructureTag element) {
        extractMaxId(element);
        return true;
    }

    @Override
    public boolean visitEnter(LockedContentContainer element) {
        extractMaxId(element);
        return true;
    }

    @Override
    public boolean visitEnter(RevisionContainer element) {
        extractMaxId(element);
        return true;
    }

    @Override
    public boolean visitEnter(CommentContainer element) {
        maxAnnotationId = Math.max(maxAnnotationId, element.getAnnotationId());
        extractMaxId(element);
        return true;
    }

    private void extractMaxId(MarkupData element) {
        String id = element.getId();
        if (id == null) {
            return;
        }
        //test if id encountered is an UUID (32 characters, sometimes separated by '-')
        if (id.length()>30 || id.contains("-")) {
            return;
        }
        Character character = id.charAt(id.length() - 1);
        while (character < '0' || character > '9') {
            id = id.substring(0, id.length() - 1);
            character = id.charAt(id.length() - 1);
        }
        try {
            Integer numericId = Integer.parseInt(id);
            if (numericId > maxContainerId) {
                maxContainerId = numericId;
            }
        } catch (NumberFormatException e) {

            LOG.error(e);
        }
    }

    public int getMaxAnnotationId() {
        return maxAnnotationId;
    }

    public int getMaxTerminologyDataId() {
        return maxTerminologyDataId;
    }

    public int getMaxContainerId() {
        return maxContainerId;
    }
}
