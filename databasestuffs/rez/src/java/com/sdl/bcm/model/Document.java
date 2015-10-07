package com.sdl.bcm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sdl.bcm.manager.Utils;
import com.sdl.bcm.visitor.BCMCompositeElement;
import com.sdl.bcm.visitor.BCMElement;
import com.sdl.bcm.visitor.BCMVisitor;
import com.sdl.bcm.visitor.impl.GetSegmentsVisitor;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the translation document - A single standalone bilingual document.
 */
@JsonPropertyOrder({"id", "modelVersion", "files", "name", "sourceLanguageCode", "sourceLanguageName", "targetLanguageCode", "targetLanguageName", "metadata"})
@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document extends MetaData implements BCMCompositeElement<Document, File> {
    /**
     * The unique identifier of the document within the document store.
     */
    @Id
    private String id;

    /**
     * One translation document contains one or more files for translation
     */
    @JsonManagedReference
    private List<File> files;

    private String modelVersion;
    /**
     * The source language code of the document(ex: en-GB, en-US).
     */
    private String sourceLanguageCode;

    /**
     * The target language code the document has to be translated into (ex: en-GB, en-US).
     */

    private String targetLanguageCode;

    /**
     * The source language of the document(ex: English, French).
     */
    private String sourceLanguageName;

    /**
     * The target language the document has to be translated into (ex: English, French).
     */
    private String targetLanguageName;

    /**
     * The name of the document, typically the file name (without path) of
     * the file from which this document was created.
     */
    private String name;

    public Document() {
        files = new ArrayList<>();
    }

    /**
     * Returns the file contained in this Document that is specified by its id
     * @param fileId String UUID of the File
     * @return File if found, null if no file with that id exists
     */
    public File getFile(String fileId) {
        for (File file : files) {
            if (file.getId().equals(fileId)) {
                return file;
            }
        }
        return null;
    }

    public List<File> getFiles() {
        return files;
    }

    public void addFile(File file) {
        files.add(file);
        file.setParentDocument(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getSourceLanguageCode() {
        return sourceLanguageCode;
    }

    public void setSourceLanguageCode(String sourceLanguageCode) {
        this.sourceLanguageCode = sourceLanguageCode;
    }

    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }

    public void setTargetLanguageCode(String targetLanguageCode) {
        this.targetLanguageCode = targetLanguageCode;
    }

    public String getSourceLanguageName() {
        return sourceLanguageName;
    }

    public void setSourceLanguageName(String sourceLanguageName) {
        this.sourceLanguageName = sourceLanguageName;
    }

    public String getTargetLanguageName() {
        return targetLanguageName;
    }

    public void setTargetLanguageName(String targetLanguageName) {
        this.targetLanguageName = targetLanguageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves a range of ParagraphUnits from this Document
     * (note: mainly intended for use by UniversalEditor for a web endpoint)
     * @param start int: the first paragraph in range, specified by index
     * @param end int: the last paragraph in range, given by its index
     * @param includeStructureParagraph boolean: if true, include the Structure ParagraphUnits in the selection
     * @return list of ParagraphUnits selected; empty list if nothing found
     */
    public List<ParagraphUnit> getParagraphUnits(int start, int end, boolean includeStructureParagraph) {
        List<ParagraphUnit> paragraphUnitsPage = new ArrayList<>();
        int mStart = start;
        int mEnd = end;
        if (mEnd < mStart || mEnd < 0)
            return paragraphUnitsPage;
        for (File file : files) {
            int count = includeStructureParagraph ? file.getParagraphUnits().size() : file.getRegularParagraphUnitCount();

            if (mStart > count) {
                mStart -= count;
                mEnd -= count;
            } else {
                if (mEnd > count) {
                    List<ParagraphUnit> puToAdd = file.getParagraphUnits(mStart, count, includeStructureParagraph);
                    paragraphUnitsPage.addAll(puToAdd);
                    mStart = 0;
                    mEnd -= puToAdd.size();
                } else {
                    paragraphUnitsPage.addAll(file.getParagraphUnits(mStart, mEnd, includeStructureParagraph));
                    return paragraphUnitsPage;
                }
            }
        }
        return paragraphUnitsPage;
    }

    @Override
    public boolean accept(BCMVisitor visitor) {
        if (visitor.visitEnter(this)) {
            for (BCMElement elementChild : getChildren()) {
                if (!elementChild.accept(visitor)) {
                    break;
                }
            }
        }
        return visitor.visitLeave(this);
    }

    /**
     * Retrieves all the segments in the Source paragraphs of all ParagraphUnits in the file
     * @return a list of all source Segment objects
     */
    @JsonIgnore
    public List<Segment> getSegments() {
        GetSegmentsVisitor getSegmentsVisitor = new GetSegmentsVisitor();
        this.accept(getSegmentsVisitor);
        return getSegmentsVisitor.getSegmentsList();
    }

    /**
     * {@inheritDoc}
     * <br />
     * 
     * For a <i>Document</i> element, this works the same as {@link #getFiles()}.
     * <br />
     * 
     * @return A list with all the files contained by this <i>Document</i>.
     */
    @JsonIgnore
    public List<File> getChildren() {
        return files;
    }

    /**
     * {@inheritDoc}
     * <br />
     * 
     * For the moment a <i>Document</i> doesn't have a parent.
     * 
     * @return For the moment it returns a <i>null</i> value.
     */
	@Override
	@JsonIgnore
	public Document getParent() {
		return null;
	}

	public Document deepClone() {
		Document clonedDoc = new Document();
		copyPropertiesTo(clonedDoc);
		clonedDoc.files = Utils.deepCloneList(files); // TODO: create a setter
		return clonedDoc;
	}

	@Override
	public void copyPropertiesTo(MetaData clone) {
		Document doc = super.convertType(clone);
		if (doc != null) {
			super.copyPropertiesTo(clone);
			doc.setId(id);
			doc.setModelVersion(modelVersion);
			doc.setSourceLanguageCode(sourceLanguageCode);
			doc.setTargetLanguageCode(targetLanguageCode);
			doc.setSourceLanguageName(sourceLanguageName);
			doc.setTargetLanguageName(targetLanguageName);
			doc.setName(name);
		}
	}

	@Override
    public boolean equals(Object o) {
        if( ! super.equals(o))
            return false;
        Document that = (Document) o;
        return new EqualsBuilder()
                .append(id, that.id)
                .append(modelVersion, that.modelVersion)
                .append(name, that.name)
                .append(sourceLanguageCode, that.sourceLanguageCode)
                .append(targetLanguageCode, that.targetLanguageCode)
                .isEquals();
    }

    @Override
    public boolean deepEquals(Object o) {
        if(! this.equals(o))
            return false;
        Document document = (Document) o;
        return Utils.deepEqualsCollection(files, document.getFiles());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (modelVersion != null ? modelVersion.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (sourceLanguageCode != null ? sourceLanguageCode.hashCode() : 0);
        result = 31 * result + (targetLanguageCode != null ? targetLanguageCode.hashCode() : 0);
        return result;
    }

}
