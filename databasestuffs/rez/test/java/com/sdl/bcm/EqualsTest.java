package com.sdl.bcm;

import com.sdl.bcm.model.*;
import com.sdl.bcm.model.fileskeleton.*;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author vnastase
 * @version 1.0
 * @since 2.5
 */
public class EqualsTest extends TestBase{
    public static Logger LOG = Logger.getLogger(EqualsTest.class);

    @Override
    public void before() {
        super.before();
    }

    @Override
    public void after() {
        super.before();
    }

    /**
     * deep Equality testing for MarkupContainers (the descendants in the object structure are tested for equality)
     * the two provided containers need to be identical and separate at the start (both equal and deepEqual)
     */
    private void testContainerDeepEquals(MarkupDataContainer container1, MarkupDataContainer container2) {
        Assert.assertTrue(container1.deepEquals(container2));

        TagPair tag1 = new TagPair();
        tag1.setId("tag1");
        tag1.setTagPairDefinitionId(1);
        TagPair tag2 = new TagPair();
        tag2.setId("tag1");
        tag2.setTagPairDefinitionId(1);
        Assert.assertTrue(tag1.equals(tag2));

        container1.addChild(tag1);
        Assert.assertFalse(container1.deepEquals(container2));
        container2.addChild(tag2);
        Assert.assertTrue(container1.deepEquals(container2));

        TextMarkup text = new TextMarkup();
        text.setText("SomeText");
        tag1.addChild(text);
        Assert.assertFalse(container1.deepEquals(container2));
    }

    /**
     * test the metadata field for equality
     * parameter objects must be equal and distinct
     */
    private void testMetadata(MetaData obj1, MetaData obj2) {
        Assert.assertTrue(obj1.equals(obj2));

        obj1.addMetaData("key", "value");
        Assert.assertFalse(obj1.equals(obj2));
        obj2.addMetaData("key", "value");
        Assert.assertTrue(obj1.equals(obj2));

        obj1.setMetadata(new HashMap<String, String>());
        obj2.setMetadata(new HashMap<String, String>());

        Assert.assertNotNull(obj1.hashCode());
        Assert.assertNotNull(obj2.hashCode());
        Assert.assertTrue(obj1.hashCode() != 0);
    }

    /**
     * test Map metadata, String id, String type, for MarkupData objects in model
     * parameter objects must be equal and distinct
     */
    private void testMarkupData(MarkupData obj1, MarkupData obj2) {
        Assert.assertTrue(obj1.equals(obj2));
        testMetadata(obj1, obj2);

        String savedId = obj1.getId();
        obj1.setId("id1");
        Assert.assertFalse(obj1.equals(obj2));
        obj2.setId(null);
        Assert.assertFalse(obj1.equals(obj2));
        obj1.setId(null);
        Assert.assertTrue(obj1.equals(obj2));
        obj1.setId("id1");
        Assert.assertFalse(obj1.equals(obj2));
        obj2.setId("id1");
        Assert.assertTrue(obj1.equals(obj2));

        obj2.setId("id2");
        Assert.assertTrue(obj2.getKey().equals("id2"));
        Assert.assertFalse(obj1.equals(obj2));
        obj1.setId(savedId);
        obj2.setId(savedId);


        String savedType = obj1.getType();
        obj1.setType("type1");
        Assert.assertFalse(obj1.equals(obj2));
        obj2.setType(null);
        Assert.assertFalse(obj1.equals(obj2));
        obj1.setType(null);
        Assert.assertTrue(obj1.equals(obj2));
        obj1.setType("type1");
        Assert.assertFalse(obj1.equals(obj2));
        obj2.setType("type1");
        Assert.assertTrue(obj1.equals(obj2));

        obj1.setType("type2");
        Assert.assertFalse(obj1.equals(obj2));
        obj1.setType(savedType);
        obj2.setType(savedType);
    }

    @Test
    public void testDocument() {

        Document markup1 = new Document();
        Assert.assertTrue(markup1.hashCode() == 0);
        markup1.setId("id1");
        markup1.setModelVersion("model1");
        markup1.setName("document1");
        markup1.setSourceLanguageCode("slang1");
        markup1.setTargetLanguageCode("tlang1");

        Document markup1Copy = new Document();
        markup1Copy.setId("id1");
        markup1Copy.setModelVersion("model1");
        markup1Copy.setName("document1");
        markup1Copy.setSourceLanguageCode("slang1");
        markup1Copy.setTargetLanguageCode("tlang1");

        // standard equality testing (only class fields are compared)
        Assert.assertTrue(markup1.equals(markup1Copy));
        testMetadata(markup1, markup1Copy);
        testDocumentWhiteBox(markup1, markup1Copy);

        //deep equality testing (test the descendants in the object structure as well)
        File file1 = new File();
        File file2 = new File();
        markup1.addFile(file1);
        markup1Copy.addFile(file2);

        Assert.assertTrue(markup1.deepEquals(markup1Copy));

        ParagraphUnit pu1 = new ParagraphUnit();
        pu1.setId("paragraph1");
        file1.addParagraphUnit(pu1);

        Assert.assertFalse(markup1.deepEquals(markup1Copy));
    }

    private void testDocumentWhiteBox(Document markup1, Document markup1Copy) {

        markup1Copy.setId("changed_id1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setId("id1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1.setModelVersion("changed_model1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1.setModelVersion("model1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1.setName("changed_document1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1.setName("document1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1.setSourceLanguageCode("changed_slang1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1.setSourceLanguageCode("slang1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1Copy.setTargetLanguageCode("changed_tlang1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setTargetLanguageCode("tlang1");
        Assert.assertTrue(markup1.equals(markup1Copy));
                                                        //null testing
        markup1.setId(null);
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setId(null);
        Assert.assertTrue(markup1.equals(markup1Copy));
        markup1.setId("id1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setId("id1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1.setModelVersion(null);
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setModelVersion(null);
        Assert.assertTrue(markup1.equals(markup1Copy));
        markup1.setModelVersion("model1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setModelVersion("model1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1.setName(null);
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setName(null);
        Assert.assertTrue(markup1.equals(markup1Copy));
        markup1.setName("document1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setName("document1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1.setSourceLanguageCode(null);
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setSourceLanguageCode(null);
        Assert.assertTrue(markup1.equals(markup1Copy));
        markup1.setSourceLanguageCode("slang1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setSourceLanguageCode("slang1");
        Assert.assertTrue(markup1.equals(markup1Copy));

        markup1.setTargetLanguageCode(null);
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setTargetLanguageCode(null);
        Assert.assertTrue(markup1.equals(markup1Copy));
        markup1.setTargetLanguageCode("tlang1");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setTargetLanguageCode("tlang1");
        Assert.assertTrue(markup1.equals(markup1Copy));
    }

    @Test
    public void testFile() {
        LinkedList<DependencyFile> dependencies1 = new LinkedList<>();
        LinkedList<DependencyFile> dependencies2 = new LinkedList<>();
        dependencies1.add(new DependencyFile());
        LinkedList<Integer> commDef1 = new LinkedList<>();
        LinkedList<Integer> commDef2 = new LinkedList<>();
        commDef1.add(1);
        FileSkeleton skel1 = new FileSkeleton();
        FileSkeleton skel2 = new FileSkeleton();
        skel1.setFileId("file1");
        skel2.setFileId("file2");
        ParagraphUnit par1 = new ParagraphUnit();
        ParagraphUnit par2 = new ParagraphUnit();
        par1.setId("par1");
        par2.setId("par2");

        File file1 = new File();
        Assert.assertTrue(file1.hashCode() != 0);
        file1.setId("id1");
        file1.setOriginalEncoding("enc1");
        file1.setPreferredTargetEncoding("tenc1");
        file1.setOriginalFileName("file1");
        file1.setFileTypeDefinitionId("ftypeid1");
        file1.setDependencyFiles(dependencies1);
        file1.setCommentDefinitionIds(commDef1);
        file1.setSkeleton(skel1);
        file1.addParagraphUnit(par1);

        File file1Copy = new File();
        file1Copy.setId("id1");
        file1Copy.setOriginalEncoding("enc1");
        file1Copy.setPreferredTargetEncoding("tenc1");
        file1Copy.setOriginalFileName("file1");
        file1Copy.setFileTypeDefinitionId("ftypeid1");
        file1Copy.setDependencyFiles(dependencies1);
        file1Copy.setCommentDefinitionIds(commDef1);
        file1Copy.setSkeleton(skel1);
        file1Copy.addParagraphUnit(par1);

        // standard equality testing (only class fields are compared)
        Assert.assertTrue(file1.equals(file1Copy));
        testMetadata(file1, file1Copy);
        testFileWhiteBox(dependencies1, dependencies2, commDef1, commDef2, skel1, skel2, file1, file1Copy);

        //deep equality testing (test the descendants in the object structure as well)
        Assert.assertTrue(file1.deepEquals(file1Copy));

        ParagraphUnit pu1 = new ParagraphUnit();
        pu1.setId("pu1");
        file1.addParagraphUnit(pu1);
        Assert.assertFalse(file1.deepEquals(file1Copy));

        ParagraphUnit pu2 = new ParagraphUnit();
        pu2.setId(pu1.getId());
        pu2.setParentFileId(pu1.getParentFileId());
        Assert.assertTrue(pu1.equals(pu2));
        file1Copy.addParagraphUnit(pu2);
        Assert.assertTrue(file1.deepEquals(file1Copy));

        file1.setId("anotherId");
        Assert.assertFalse(file1.deepEquals(file1Copy));
        file1.setId("id1");

        Paragraph pa1 = new Paragraph();
        pa1.setId("pa1");
        pu1.setSource(pa1);
        Assert.assertFalse(file1.deepEquals(file1Copy));
    }

    private void testFileWhiteBox(LinkedList<DependencyFile> dependencies1, LinkedList<DependencyFile> dependencies2,
                                  LinkedList<Integer> commDef1, LinkedList<Integer> commDef2,
                                  FileSkeleton skel1, FileSkeleton skel2, File file1, File file1Copy) {

        file1.setId("changed_id1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setId("id1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setOriginalEncoding("changed_enc1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setOriginalEncoding("enc1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setPreferredTargetEncoding("changed_tenc1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setPreferredTargetEncoding("tenc1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setOriginalFileName("changed_file1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setOriginalFileName("file1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setFileTypeDefinitionId("changed_ftypeid1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setFileTypeDefinitionId("ftypeid1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setDependencyFiles(dependencies2);
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setDependencyFiles(dependencies1);
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setCommentDefinitionIds(commDef2);
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setCommentDefinitionIds(commDef1);
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setSkeleton(skel2);
        Assert.assertFalse(file1.equals(file1Copy));
        file1.setSkeleton(skel1);
        Assert.assertTrue(file1.equals(file1Copy));
                                                    //null fields testing
        file1.setId(null);
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setId(null);
        Assert.assertTrue(file1.equals(file1Copy));
        file1.setId("id1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setId("id1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setOriginalEncoding(null);
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setOriginalEncoding(null);
        Assert.assertTrue(file1.equals(file1Copy));
        file1.setOriginalEncoding("enc1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setOriginalEncoding("enc1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setPreferredTargetEncoding(null);
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setPreferredTargetEncoding(null);
        Assert.assertTrue(file1.equals(file1Copy));
        file1.setPreferredTargetEncoding("tenc1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setPreferredTargetEncoding("tenc1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setOriginalFileName(null);
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setOriginalFileName(null);
        Assert.assertTrue(file1.equals(file1Copy));
        file1.setOriginalFileName("file1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setOriginalFileName("file1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setFileTypeDefinitionId(null);
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setFileTypeDefinitionId(null);
        Assert.assertTrue(file1.equals(file1Copy));
        file1.setFileTypeDefinitionId("ftypeid1");
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setFileTypeDefinitionId("ftypeid1");
        Assert.assertTrue(file1.equals(file1Copy));

        file1.setSkeleton(null);
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setSkeleton(null);
        Assert.assertTrue(file1.equals(file1Copy));
        file1.setSkeleton(skel1);
        Assert.assertFalse(file1.equals(file1Copy));
        file1Copy.setSkeleton(skel1);
        Assert.assertTrue(file1.equals(file1Copy));
    }

    @Test
    public void testDependencyFile() {

        DependencyFile df1 = new DependencyFile();
        Assert.assertTrue(df1.hashCode() == 0);
        df1.setId("id1");
        df1.setUsage(DependencyFileUsage.EXTRACTION);
        df1.setLocation("location1");
        df1.setFileName("name1");

        DependencyFile df1Copy = new DependencyFile();
        df1Copy.setId("id1");
        df1Copy.setUsage(DependencyFileUsage.EXTRACTION);
        df1Copy.setLocation("location1");
        df1Copy.setFileName("name1");

        Assert.assertTrue(df1.getId().equals("id1"));
        Assert.assertTrue(df1.getFileName().equals("name1"));
        Assert.assertTrue(df1.getLocation().equals("location1"));
        Assert.assertTrue(df1.getUsage().equals(DependencyFileUsage.EXTRACTION));

        Assert.assertTrue(df1.equals(df1Copy));
        Assert.assertTrue(df1.deepEquals(df1Copy));

        df1.addMetaData("k", "v");
        Assert.assertFalse(df1.equals(df1Copy));
        df1Copy.addMetaData("k", "v");
        testDependencyFileWhiteBox(df1, df1Copy);
        Assert.assertTrue(df1.hashCode() != 0);
    }

    private void testDependencyFileWhiteBox(DependencyFile df1, DependencyFile df1Copy) {
        df1.setId("changed_id1");
        Assert.assertFalse(df1.equals(df1Copy));
        df1.setId("id1");
        Assert.assertTrue(df1.equals(df1Copy));

        df1.setUsage(DependencyFileUsage.GENERATION);
        Assert.assertFalse(df1.equals(df1Copy));
        df1.setUsage(DependencyFileUsage.EXTRACTION);
        Assert.assertTrue(df1.equals(df1Copy));

        df1.setLocation("changed_location1");
        Assert.assertFalse(df1.equals(df1Copy));
        df1.setLocation("location1");
        Assert.assertTrue(df1.equals(df1Copy));

        df1.setFileName("changed_name1");
        Assert.assertFalse(df1.equals(df1Copy));
        df1.setFileName("name1");
        Assert.assertTrue(df1.equals(df1Copy));
                                                //null fields tests
        df1.setId(null);
        Assert.assertFalse(df1.equals(df1Copy));
        df1Copy.setId(null);
        Assert.assertTrue(df1.equals(df1Copy));
        df1.setId("id1");
        Assert.assertFalse(df1.equals(df1Copy));
        df1Copy.setId("id1");
        Assert.assertTrue(df1.equals(df1Copy));

        df1.setLocation(null);
        Assert.assertFalse(df1.equals(df1Copy));
        df1Copy.setLocation(null);
        Assert.assertTrue(df1.equals(df1Copy));
        df1.setLocation("location1");
        Assert.assertFalse(df1.equals(df1Copy));
        df1Copy.setLocation("location1");
        Assert.assertTrue(df1.equals(df1Copy));

        df1.setFileName(null);
        Assert.assertFalse(df1.equals(df1Copy));
        df1Copy.setFileName(null);
        Assert.assertTrue(df1.equals(df1Copy));
        df1.setFileName("name1");
        Assert.assertFalse(df1.equals(df1Copy));
        df1Copy.setFileName("name1");
        Assert.assertTrue(df1.equals(df1Copy));
    }

    @Test
    public void testLocalizableSubContent() {

        LocalizableSubContent loc1 = new LocalizableSubContent();
        Assert.assertTrue(loc1.hashCode() == 0); //all fields should be null
        loc1.setSourceTagContentOffset(1);
        loc1.setLength(5);
        loc1.setParagraphUnitId("1");

        Assert.assertTrue(loc1.getLength() == 5);
        Assert.assertTrue(loc1.getParagraphUnitId().equals("1"));
        Assert.assertTrue(loc1.getSourceTagContentOffset() == 1);

        LocalizableSubContent loc1Copy = new LocalizableSubContent();
        loc1Copy.setSourceTagContentOffset(1);
        loc1Copy.setLength(5);
        loc1Copy.setParagraphUnitId("1");

        loc1.addMetaData("k", "v");
        Assert.assertFalse(loc1.equals(loc1Copy));
        loc1Copy.addMetaData("k", "v");

        Assert.assertTrue(loc1.equals(loc1Copy));
        Assert.assertTrue(loc1.deepEquals(loc1Copy));

        loc1.setSourceTagContentOffset(2);
        Assert.assertFalse(loc1.equals(loc1Copy));
        loc1.setSourceTagContentOffset(1);
        Assert.assertTrue(loc1.equals(loc1Copy));

        loc1.setLength(6);
        Assert.assertFalse(loc1.equals(loc1Copy));
        loc1.setLength(5);
        Assert.assertTrue(loc1.equals(loc1Copy));

        loc1.setParagraphUnitId("2");
        Assert.assertFalse(loc1.equals(loc1Copy));
        loc1.setParagraphUnitId("1");
        Assert.assertTrue(loc1.equals(loc1Copy));

        loc1.setParagraphUnitId(null);
        Assert.assertFalse(loc1.equals(loc1Copy));
        loc1Copy.setParagraphUnitId(null);
        Assert.assertTrue(loc1.equals(loc1Copy));
        loc1.setParagraphUnitId("1");
        Assert.assertFalse(loc1.equals(loc1Copy));
        loc1Copy.setParagraphUnitId("1");
        Assert.assertTrue(loc1.equals(loc1Copy));
    }

    @Test
    public void testParagraphUnit() {

        LinkedList<Integer> contextList1 = new LinkedList<>();
        LinkedList<Integer> contextList2 = new LinkedList<>();
        contextList1.add(1);
        contextList2.add(2);
        LinkedList<Integer> commentDefIds1 = new LinkedList<>();
        LinkedList<Integer> commentDefIds2 = new LinkedList<>();
        commentDefIds1.add(1);
        commentDefIds1.add(2);
        Paragraph paragraph1 = new Paragraph();
        Paragraph paragraph2 = new Paragraph();
        paragraph1.setId("1");
        paragraph2.setId("2");

        ParagraphUnit pu1 = new ParagraphUnit();
        Assert.assertTrue(pu1.hashCode() == 0);
        pu1.setId("1");
        pu1.setParentFileId("file1");
        pu1.setStructure(false);
        pu1.setLocked(false);
        pu1.setStructureContextId(1);
        pu1.setContextList(contextList1);
        pu1.setIndex(1);
        pu1.setSource(paragraph1);
        pu1.setTarget(paragraph1);
        pu1.setCommentDefinitionIds(commentDefIds1);

        ParagraphUnit pu1Copy = new ParagraphUnit();
        pu1Copy.setId("1");
        pu1Copy.setParentFileId("file1");
        pu1Copy.setStructure(false);
        pu1Copy.setLocked(false);
        pu1Copy.setStructureContextId(1);
        pu1Copy.setContextList(contextList1);
        pu1Copy.setIndex(1);
        pu1Copy.setSource(paragraph1);
        pu1Copy.setTarget(paragraph1);
        pu1Copy.setCommentDefinitionIds(commentDefIds1);

        // standard equality testing (only class fields are compared)
        Assert.assertTrue(pu1.equals(pu1Copy));
        testMetadata(pu1, pu1Copy);
        testParagraphUnitWhiteBox(contextList1, contextList2, commentDefIds1, commentDefIds2, pu1, pu1Copy);

        //deep equality testing (test the descendants in the object structure as well)
        Assert.assertTrue(pu1.deepEquals(pu1Copy));

        Paragraph paragraph3 = new Paragraph();
        paragraph3.setId("paragraphId");
        pu1.setSource(paragraph3);
        Assert.assertFalse(pu1.deepEquals(pu1Copy));
        Assert.assertTrue(pu1.equals(pu1Copy));

        Paragraph paragraph4 = new Paragraph();
        paragraph4.setId("paragraphId");
        pu1Copy.setSource(paragraph4);
        Assert.assertTrue(pu1.deepEquals(pu1Copy));

        Segment seg = new Segment();
        paragraph4.addChild(seg);
        Assert.assertFalse(pu1.deepEquals(pu1Copy));
    }

    private void testParagraphUnitWhiteBox(LinkedList<Integer> contextList1, LinkedList<Integer> contextList2,
                                           LinkedList<Integer> commentDefIds1, LinkedList<Integer> commentDefIds2,
                                           ParagraphUnit pu1, ParagraphUnit pu1Copy) {
        pu1.setId("2");
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setId("1");
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setParentFileId("file2");
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setParentFileId("file1");
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setStructure(true);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setStructure(false);
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setLocked(true);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setLocked(false);
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setStructureContextId(2);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setStructureContextId(1);
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setContextList(contextList2);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setContextList(contextList1);
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setIndex(2);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setIndex(1);
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setCommentDefinitionIds(commentDefIds2);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1.setCommentDefinitionIds(commentDefIds1);
        Assert.assertTrue(pu1.equals(pu1Copy));
                                                    //test null fields
        pu1.setId(null);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1Copy.setId(null);
        Assert.assertTrue(pu1.equals(pu1Copy));
        pu1.setId("1");
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1Copy.setId("1");
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setParentFileId(null);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1Copy.setParentFileId(null);
        Assert.assertTrue(pu1.equals(pu1Copy));
        pu1.setParentFileId("file1");
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1Copy.setParentFileId("file1");
        Assert.assertTrue(pu1.equals(pu1Copy));

        pu1.setStructureContextId(null);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1Copy.setStructureContextId(null);
        Assert.assertTrue(pu1.equals(pu1Copy));
        pu1.setStructureContextId(1);
        Assert.assertFalse(pu1.equals(pu1Copy));
        pu1Copy.setStructureContextId(1);
        Assert.assertTrue(pu1.equals(pu1Copy));
    }

    @Test
    public void testTextMarkup() {
        Segment segment = new Segment(null);

        TextMarkup markup1 = new TextMarkup(segment);
        Assert.assertTrue(markup1.hashCode() != 0);
        markup1.setText("This is a text.");
        TextMarkup markup2 = new TextMarkup(segment);
        markup2.setText("This is another text.");
        TextMarkup markup1Copy = new TextMarkup(segment);
        markup1Copy.setText("This is a text.");

        testMarkupData(markup1, markup1Copy);

        Assert.assertTrue(markup1.equals(markup1Copy));
        Assert.assertFalse(markup1.equals(markup2));
        Assert.assertTrue(markup1.deepEquals(markup1Copy));
        Assert.assertFalse(markup1.deepEquals(markup2));

        markup1.setText(null);
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setText(null);
        Assert.assertTrue(markup1.equals(markup1Copy));
        markup1.setText("This is a text.");
        Assert.assertFalse(markup1.equals(markup1Copy));
        markup1Copy.setText("This is a text.");
        Assert.assertTrue(markup1.equals(markup1Copy));
    }

    @Test
    public void testPlaceholderTag() {
        Segment segment = new Segment(null);

        PlaceholderTag markup1 = new PlaceholderTag(segment);
        Assert.assertTrue(markup1.hashCode() != 0);
        markup1.setPlaceholderTagDefinitionId(1);
        PlaceholderTag markup2 = new PlaceholderTag(segment);
        markup2.setPlaceholderTagDefinitionId(2);
        PlaceholderTag markup1Copy = new PlaceholderTag(segment);
        markup1Copy.setPlaceholderTagDefinitionId(1);

        testMarkupData(markup1, markup1Copy);

        Assert.assertTrue(markup1.equals(markup1Copy));
        Assert.assertFalse(markup1.equals(markup2));
        Assert.assertTrue(markup1.deepEquals(markup1Copy));
        Assert.assertFalse(markup1.deepEquals(markup2));
    }

    @Test
    public void testStructureTag() {
        Segment segment = new Segment(null);

        StructureTag markup1 = new StructureTag(segment);
        Assert.assertTrue(markup1.hashCode() != 0);
        markup1.setStructureTagDefinitionId(1);
        StructureTag markup2 = new StructureTag(segment);
        markup2.setStructureTagDefinitionId(2);
        StructureTag markup1Copy = new StructureTag(segment);
        markup1Copy.setStructureTagDefinitionId(1);

        testMarkupData(markup1, markup1Copy);

        Assert.assertTrue(markup1.equals(markup1Copy));
        Assert.assertFalse(markup1.equals(markup2));
        Assert.assertTrue(markup1.getStructureTagDefinitionId() == markup1Copy.getStructureTagDefinitionId());
        Assert.assertFalse(markup1.getStructureTagDefinitionId() == markup2.getStructureTagDefinitionId());
        Assert.assertTrue(markup1.deepEquals(markup1Copy));
        Assert.assertFalse(markup1.deepEquals(markup2));
    }

    @Test
    public void testParagraph() {

        ParagraphUnit parUnit1 = new ParagraphUnit();
        ParagraphUnit parUnit2 = new ParagraphUnit();

        Paragraph par1 = new Paragraph();
        Assert.assertTrue(par1.hashCode() != 0);
        par1.setId("1");
        par1.setParagraphUnit(parUnit1);
        Assert.assertTrue(par1.getParagraphUnit().equals(parUnit1));

        Paragraph par2 = new Paragraph();
        par2.setId("2");
        par2.setParagraphUnit(parUnit2);

        Paragraph par1Copy = new Paragraph();
        par1Copy.setId("1");
        par1Copy.setParagraphUnit(parUnit1);

        testMarkupData(par1, par1Copy);

        Assert.assertTrue(par1.equals(par1Copy));
        Assert.assertFalse(par1.equals(par2));

        testContainerDeepEquals(par1, par1Copy);
    }

    @Test
    public void testSegment() {

        TranslationOrigin tOrigin1 = new TranslationOrigin();
        TranslationOrigin tOrigin2 = new TranslationOrigin();
        tOrigin1.setMatchPercent(15);
        tOrigin2.setMatchPercent(47);

        Segment seg1 = new Segment();
        Assert.assertTrue(seg1.hashCode() != 0);
        seg1.setId("1");
        seg1.setConfirmationLevel(ConfirmationLevel.APPROVED_SIGN_OFF);
        seg1.setLocked(false);
        seg1.setWordCount(1);
        seg1.setCharacterCount(1);
        seg1.setSegmentNumber("1");
        seg1.setTranslationOrigin(tOrigin1);

        Segment seg1Copy = new Segment();
        seg1Copy.setId("1");
        seg1Copy.setConfirmationLevel(ConfirmationLevel.APPROVED_SIGN_OFF);
        seg1Copy.setLocked(false);
        seg1Copy.setWordCount(1);
        seg1Copy.setCharacterCount(1);
        seg1Copy.setSegmentNumber("1");
        seg1Copy.setTranslationOrigin(tOrigin1);

        testMarkupData(seg1, seg1Copy);

        Assert.assertTrue(seg1.equals(seg1Copy));

        testSegmentWhiteBox(tOrigin1, tOrigin2, seg1, seg1Copy);

        testContainerDeepEquals(seg1, seg1Copy);
    }

    private void testSegmentWhiteBox(TranslationOrigin tOrigin1, TranslationOrigin tOrigin2, Segment seg1, Segment seg1Copy) {
        seg1.setId("2");
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1.setId("1");
        Assert.assertTrue(seg1.equals(seg1Copy));

        seg1.setConfirmationLevel(ConfirmationLevel.APPROVED_TRANSLATION);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1.setConfirmationLevel(ConfirmationLevel.APPROVED_SIGN_OFF);
        Assert.assertTrue(seg1.equals(seg1Copy));

        seg1.setLocked(true);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1.setLocked(false);
        Assert.assertTrue(seg1.equals(seg1Copy));

        seg1.setWordCount(2);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1.setWordCount(1);
        Assert.assertTrue(seg1.equals(seg1Copy));

        seg1.setCharacterCount(2);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1.setCharacterCount(1);
        Assert.assertTrue(seg1.equals(seg1Copy));

        seg1.setSegmentNumber("2");
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1.setSegmentNumber("1");
        Assert.assertTrue(seg1.equals(seg1Copy));

        seg1.setTranslationOrigin(tOrigin2);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1.setTranslationOrigin(tOrigin1);
        Assert.assertTrue(seg1.equals(seg1Copy));
                                                    //testing null fields
        seg1.setSegmentNumber(null);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1Copy.setSegmentNumber(null);
        Assert.assertTrue(seg1.equals(seg1Copy));
        seg1.setSegmentNumber("1");
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1Copy.setSegmentNumber("1");
        Assert.assertTrue(seg1.equals(seg1Copy));

        seg1.setTranslationOrigin(null);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1Copy.setTranslationOrigin(null);
        Assert.assertTrue(seg1.equals(seg1Copy));
        seg1.setTranslationOrigin(tOrigin1);
        Assert.assertFalse(seg1.equals(seg1Copy));
        seg1Copy.setTranslationOrigin(tOrigin1);
        Assert.assertTrue(seg1.equals(seg1Copy));
    }

    @Test
    public void testTranslationOrigin() {
        TranslationOrigin bef1 = new TranslationOrigin();
        TranslationOrigin bef2 = new TranslationOrigin();
        Assert.assertTrue(bef1.hashCode() == 0);
        bef1.setMatchPercent(20);
        bef2.setMatchPercent(53);

        TranslationOrigin tor1 = new TranslationOrigin();
        tor1.setOriginType("t1");
        tor1.setOriginSystem("s1");
        tor1.setMatchPercent(1);
        tor1.setStructureContextMatch(true);
        tor1.setOriginalTranslationHash("h1");
        tor1.setOriginBeforeAdaptation(bef1);
        tor1.setTextContextMatchLevel(TextContextMatchLevel.SOURCE);

        Assert.assertTrue(tor1.getOriginType().equals("t1"));
        Assert.assertTrue(tor1.getOriginSystem().equals("s1"));
        Assert.assertTrue(tor1.getMatchPercent() == 1);
        Assert.assertTrue(tor1.isStructureContextMatch());
        Assert.assertTrue(tor1.getOriginalTranslationHash().equals("h1"));
        Assert.assertTrue(tor1.getOriginBeforeAdaptation().equals(bef1));
        Assert.assertTrue(tor1.getTextContextMatchLevel().equals(TextContextMatchLevel.SOURCE));

        TranslationOrigin tor1Copy = new TranslationOrigin();
        tor1Copy.setOriginType("t1");
        tor1Copy.setOriginSystem("s1");
        tor1Copy.setMatchPercent(1);
        tor1Copy.setStructureContextMatch(true);
        tor1Copy.setOriginalTranslationHash("h1");
        tor1Copy.setOriginBeforeAdaptation(bef1);
        tor1Copy.setTextContextMatchLevel(TextContextMatchLevel.SOURCE);

        testMetadata(tor1, tor1Copy);

        Assert.assertTrue(tor1.equals(tor1Copy));
        Assert.assertTrue(tor1.deepEquals(tor1Copy));

        testTranslationOriginWhiteBox(bef1, bef2, tor1, tor1Copy);
    }

    private void testTranslationOriginWhiteBox(TranslationOrigin bef1, TranslationOrigin bef2,
                                               TranslationOrigin tor1, TranslationOrigin tor1Copy) {
        tor1.setOriginType("t2");
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1.setOriginType("t1");
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setOriginSystem("s2");
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1.setOriginSystem("s1");
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setMatchPercent(2);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1.setMatchPercent(1);
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setStructureContextMatch(false);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1.setStructureContextMatch(true);
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setOriginalTranslationHash("h2");
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1.setOriginalTranslationHash("h1");
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setOriginBeforeAdaptation(bef2);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1.setOriginBeforeAdaptation(bef1);
        Assert.assertTrue(tor1.equals(tor1Copy));
                                                    //test null fields
        tor1.setOriginType(null);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginType(null);
        Assert.assertTrue(tor1.equals(tor1Copy));
        tor1.setOriginType("t1");
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginType("t1");
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setOriginSystem(null);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginSystem(null);
        Assert.assertTrue(tor1.equals(tor1Copy));
        tor1.setOriginSystem("s1");
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginSystem("s1");
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setOriginalTranslationHash(null);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginalTranslationHash(null);
        Assert.assertTrue(tor1.equals(tor1Copy));
        tor1.setOriginalTranslationHash("h1");
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginalTranslationHash("h1");
        Assert.assertTrue(tor1.equals(tor1Copy));

        tor1.setOriginBeforeAdaptation(null);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginBeforeAdaptation(null);
        Assert.assertTrue(tor1.equals(tor1Copy));
        tor1.setOriginBeforeAdaptation(bef1);
        Assert.assertFalse(tor1.equals(tor1Copy));
        tor1Copy.setOriginBeforeAdaptation(bef1);
        Assert.assertTrue(tor1.equals(tor1Copy));
    }

    @Test
    public void testRevisionContainer() {

        Date date1 = new Date();
        Date date2 = new Date();
        date1.setTime(1000000000);
        date2.setTime(2000000000);

        RevisionContainer rev1 = new RevisionContainer();
        Assert.assertTrue(rev1.hashCode() != 0);
        rev1.setRevisionType(RevisionType.DELETED);
        rev1.setAuthor("a1");
        rev1.setTimestamp(date1);

        RevisionContainer rev1Copy = new RevisionContainer();
        rev1Copy.setRevisionType(RevisionType.DELETED);
        rev1Copy.setAuthor("a1");
        rev1Copy.setTimestamp(date1);

        testMarkupData(rev1, rev1Copy);

        Assert.assertTrue(rev1.equals(rev1Copy));

        testRevisionContainerWhiteBox(date1, date2, rev1, rev1Copy);

        testContainerDeepEquals(rev1, rev1Copy);
    }

    private void testRevisionContainerWhiteBox(Date date1, Date date2, RevisionContainer rev1, RevisionContainer rev1Copy) {

        rev1.setRevisionType(RevisionType.INSERTED);
        Assert.assertFalse(rev1.equals(rev1Copy));
        rev1.setRevisionType(RevisionType.DELETED);
        Assert.assertTrue(rev1.equals(rev1Copy));

        rev1.setAuthor("a2");
        Assert.assertFalse(rev1.equals(rev1Copy));
        rev1.setAuthor("a1");
        Assert.assertTrue(rev1.equals(rev1Copy));

        rev1.setTimestamp(date2);
        Assert.assertFalse(rev1.equals(rev1Copy));
        rev1.setTimestamp(date1);
        Assert.assertTrue(rev1.equals(rev1Copy));
                                                    //testing null fields
        rev1.setAuthor(null);
        Assert.assertFalse(rev1.equals(rev1Copy));
        rev1Copy.setAuthor(null);
        Assert.assertTrue(rev1.equals(rev1Copy));
        rev1.setAuthor("a1");
        Assert.assertFalse(rev1.equals(rev1Copy));
        rev1Copy.setAuthor("a1");
        Assert.assertTrue(rev1.equals(rev1Copy));

        rev1.setTimestamp(null);
        Assert.assertFalse(rev1.equals(rev1Copy));
        rev1Copy.setTimestamp(null);
        Assert.assertTrue(rev1.equals(rev1Copy));
        rev1.setTimestamp(date1);
        Assert.assertFalse(rev1.equals(rev1Copy));
        rev1Copy.setTimestamp(date1);
        Assert.assertTrue(rev1.equals(rev1Copy));

    }

    @Test
    public void testFeedBackContainer() {

        Date date1 = new Date();
        Date date2 = new Date();
        date1.setTime(1000000000);
        date2.setTime(2000000000);

        FeedbackContainer fed1 = new FeedbackContainer();
        Assert.assertTrue(fed1.hashCode() != 0);
        fed1.setAuthor("a1");
        fed1.setTimestamp(date1);
        fed1.setCategory("c1");
        fed1.setDocumentCategory("dc1");
        fed1.setSeverity("s1");
        fed1.setCommentDefinitionId(1);
        fed1.setFeedbackType(FeedbackType.ADDED);

        FeedbackContainer fed1Copy = new FeedbackContainer();
        Assert.assertTrue(fed1Copy.hashCode() != 0);
        fed1Copy.setAuthor("a1");
        Assert.assertTrue(fed1Copy.getAuthor().equals("a1"));
        fed1Copy.setTimestamp(date1);
        Assert.assertTrue(fed1Copy.getTimestamp().equals(date1));
        fed1Copy.setCategory("c1");
        Assert.assertTrue(fed1Copy.getCategory().equals("c1"));
        fed1Copy.setDocumentCategory("dc1");
        Assert.assertTrue(fed1Copy.getDocumentCategory().equals("dc1"));
        fed1Copy.setSeverity("s1");
        Assert.assertTrue(fed1Copy.getSeverity().equals("s1"));
        fed1Copy.setCommentDefinitionId(1);
        Assert.assertTrue(fed1Copy.getCommentDefinitionId().equals(1));
        fed1Copy.setFeedbackType(FeedbackType.ADDED);
        Assert.assertTrue(fed1Copy.getFeedbackType().equals(FeedbackType.ADDED));

        testMarkupData(fed1, fed1Copy);

        Assert.assertTrue(fed1.equals(fed1Copy));

        testFeedBackContainerWhiteBox(date1, date2, fed1, fed1Copy);

        testContainerDeepEquals(fed1, fed1Copy);
    }

    private void testFeedBackContainerWhiteBox(Date date1, Date date2, FeedbackContainer fed1, FeedbackContainer fed1Copy) {
        fed1.setAuthor("a2");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1.setAuthor("a1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setTimestamp(date2);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1.setTimestamp(date1);
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setCategory("c2");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1.setCategory("c1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setDocumentCategory("dc2");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1.setDocumentCategory("dc1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setSeverity("s2");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1.setSeverity("s1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setCommentDefinitionId(2);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1.setCommentDefinitionId(1);
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setFeedbackType(FeedbackType.COMMENT);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1.setFeedbackType(FeedbackType.ADDED);
        Assert.assertTrue(fed1.equals(fed1Copy));
                                                    //test null fields
        fed1.setAuthor(null);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setAuthor(null);
        Assert.assertTrue(fed1.equals(fed1Copy));
        fed1.setAuthor("a1");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setAuthor("a1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setTimestamp(null);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setTimestamp(null);
        Assert.assertTrue(fed1.equals(fed1Copy));
        fed1.setTimestamp(date1);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setTimestamp(date1);
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setCategory(null);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setCategory(null);
        Assert.assertTrue(fed1.equals(fed1Copy));
        fed1.setCategory("c1");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setCategory("c1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setDocumentCategory(null);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setDocumentCategory(null);
        Assert.assertTrue(fed1.equals(fed1Copy));
        fed1.setDocumentCategory("dc1");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setDocumentCategory("dc1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setSeverity(null);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setSeverity(null);
        Assert.assertTrue(fed1.equals(fed1Copy));
        fed1.setSeverity("s1");
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setSeverity("s1");
        Assert.assertTrue(fed1.equals(fed1Copy));

        fed1.setCommentDefinitionId(null);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setCommentDefinitionId(null);
        Assert.assertTrue(fed1.equals(fed1Copy));
        fed1.setCommentDefinitionId(1);
        Assert.assertFalse(fed1.equals(fed1Copy));
        fed1Copy.setCommentDefinitionId(1);
        Assert.assertTrue(fed1.equals(fed1Copy));
    }

    @Test
    public void testLockedContentContainer() {

        LockedContentContainer lok1 = new LockedContentContainer();
        Assert.assertTrue(lok1.hashCode() != 0);
        lok1.setId("1");

        LockedContentContainer lok2 = new LockedContentContainer();
        lok2.setId("2");

        LockedContentContainer lok1Copy = new LockedContentContainer();
        lok1Copy.setId("1");

        testMarkupData(lok1, lok1Copy);

        Assert.assertTrue(lok1.equals(lok1Copy));
        Assert.assertFalse(lok1.equals(lok2));

        testContainerDeepEquals(lok1, lok1Copy);
    }

    @Test
    public void testTagPair() {

        TagPair tag1 = new TagPair();
        Assert.assertTrue(tag1.hashCode() != 0);
        tag1.setTagPairDefinitionId(1);
        tag1.setCanHide(true);

        TagPair tag2 = new TagPair();
        tag2.setTagPairDefinitionId(2);
        tag2.setCanHide(false);

        TagPair tag1Copy = new TagPair();
        Assert.assertTrue(tag1Copy.hashCode() != 0);
        tag1Copy.setTagPairDefinitionId(1);
        tag1Copy.setCanHide(true);

        testMarkupData(tag1, tag1Copy);

        Assert.assertTrue(tag1.equals(tag1Copy));
        Assert.assertFalse(tag1.equals(tag2));

        tag1Copy.setTagPairDefinitionId(2);
        Assert.assertFalse(tag1.equals(tag1Copy));
        tag1Copy.setTagPairDefinitionId(1);
        tag1Copy.setCanHide(false);
        Assert.assertFalse(tag1.equals(tag1Copy));
        tag1Copy.setCanHide(true);

        testContainerDeepEquals(tag1, tag1Copy);

    }

    @Test
    public void testTerminologyAnnotationContainer() {

        TerminologyAnnotationContainer term1 = new TerminologyAnnotationContainer();
        Assert.assertTrue(term1.hashCode() != 0);
        term1.setTerminologyDataId(1);
        term1.setAnnotationId(1);

        Assert.assertTrue(term1.toString().equals("TerminologyAnnotationContainer{1}"));

        TerminologyAnnotationContainer term1Copy = new TerminologyAnnotationContainer();
        term1Copy.setTerminologyDataId(1);
        term1Copy.setAnnotationId(1);

        testMarkupData(term1, term1Copy);

        Assert.assertTrue(term1.equals(term1Copy));
        term1.setTerminologyDataId(2);
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setTerminologyDataId(1);
        Assert.assertTrue(term1.equals(term1Copy));

        term1.setAnnotationId(2);
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setAnnotationId(1);
        Assert.assertTrue(term1.equals(term1Copy));

        testContainerDeepEquals(term1, term1Copy);
    }

    @Test
    public void testCommentContainer() {

        CommentContainer com1 = new CommentContainer();
        Assert.assertTrue(com1.hashCode() != 0);
        com1.setCommentDefinitionId(1);
        com1.setAnnotationId(1);

        CommentContainer com1Copy = new CommentContainer();
        com1Copy.setCommentDefinitionId(1);
        com1Copy.setAnnotationId(1);

        testMarkupData(com1, com1Copy);

        Assert.assertTrue(com1.equals(com1Copy));
        com1.setCommentDefinitionId(2);
        Assert.assertFalse(com1.equals(com1Copy));
        com1.setCommentDefinitionId(1);
                                                    //null field testing
        Assert.assertTrue(com1.equals(com1Copy));
        com1.setCommentDefinitionId(null);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setCommentDefinitionId(null);
        Assert.assertTrue(com1.equals(com1Copy));
        com1.setCommentDefinitionId(1);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setCommentDefinitionId(1);
        Assert.assertTrue(com1.equals(com1Copy));

        Assert.assertTrue(com1.equals(com1Copy));
        com1.setAnnotationId(2);
        Assert.assertFalse(com1.equals(com1Copy));
        com1.setAnnotationId(1);

        testContainerDeepEquals(com1, com1Copy);
    }

    @Test
    public void testFileSkeleton() {

        LinkedList<CommentDefinition> comments1 = new LinkedList<>();
        LinkedList<CommentDefinition> comments2 = new LinkedList<>();
        comments1.add(new CommentDefinition());
        LinkedList<ContextDefinition> contextDefs1 = new LinkedList<>();
        LinkedList<ContextDefinition> contextDefs2 = new LinkedList<>();
        contextDefs1.add(new ContextDefinition());
        LinkedList<Context> contexts1 = new LinkedList<>();
        LinkedList<Context> contexts2 = new LinkedList<>();
        contexts1.add(new Context());
        LinkedList<FormattingGroup> formatGroup1 = new LinkedList<>();
        LinkedList<FormattingGroup> formatGroup2 = new LinkedList<>();
        formatGroup1.add(new FormattingGroup());
        LinkedList<StructureTagDefinition> strTagDefs1 = new LinkedList<>();
        LinkedList<StructureTagDefinition> strTagDefs2 = new LinkedList<>();
        strTagDefs1.add(new StructureTagDefinition());
        LinkedList<TagPairDefinition> tagPairDefs1 = new LinkedList<>();
        LinkedList<TagPairDefinition> tagPairDefs2 = new LinkedList<>();
        tagPairDefs1.add(new TagPairDefinition());
        LinkedList<PlaceholderTagDefinition> placeholderDefs1 = new LinkedList<>();
        LinkedList<PlaceholderTagDefinition> placeholderDefs2 = new LinkedList<>();
        placeholderDefs1.add(new PlaceholderTagDefinition());
        LinkedList<TerminologyData> termData1 = new LinkedList<>();
        LinkedList<TerminologyData> termData2 = new LinkedList<>();
        termData1.add(new TerminologyData());

        FileSkeleton skel1 = new FileSkeleton();
        Assert.assertTrue(skel1.hashCode() == 0);
        skel1.setFileId("file1");
        skel1.setCommentDefinitions(comments1);
        skel1.setContextDefinitions(contextDefs1);
        skel1.setContexts(contexts1);
        skel1.setFormattingGroups(formatGroup1);
        skel1.setStructureTagDefinitions(strTagDefs1);
        skel1.setTagPairDefinitions(tagPairDefs1);
        skel1.setPlaceholderTagDefinitions(placeholderDefs1);
        skel1.setTerminologyData(termData1);

        FileSkeleton skel1Copy = new FileSkeleton();
        skel1Copy.setFileId("file1");
        skel1Copy.setCommentDefinitions(comments1);
        skel1Copy.setContextDefinitions(contextDefs1);
        skel1Copy.setContexts(contexts1);
        skel1Copy.setFormattingGroups(formatGroup1);
        skel1Copy.setStructureTagDefinitions(strTagDefs1);
        skel1Copy.setTagPairDefinitions(tagPairDefs1);
        skel1Copy.setPlaceholderTagDefinitions(placeholderDefs1);
        skel1Copy.setTerminologyData(termData1);

        Assert.assertTrue(skel1.equals(skel1Copy));
        Assert.assertTrue(skel1.deepEquals(skel1Copy));

        testMetadata(skel1, skel1Copy);

        testSkeletonWhiteBox(comments1, comments2, contextDefs1, contextDefs2, contexts1, contexts2,
                formatGroup1, formatGroup2, strTagDefs1, strTagDefs2, tagPairDefs1, tagPairDefs2,
                placeholderDefs1, placeholderDefs2, termData1, termData2, skel1, skel1Copy);
    }

    private void testSkeletonWhiteBox(LinkedList<CommentDefinition> comments1, LinkedList<CommentDefinition> comments2,
                                      LinkedList<ContextDefinition> contextDefs1, LinkedList<ContextDefinition> contextDefs2,
                                      LinkedList<Context> contexts1, LinkedList<Context> contexts2,
                                      LinkedList<FormattingGroup> formatGroup1, LinkedList<FormattingGroup> formatGroup2,
                                      LinkedList<StructureTagDefinition> strTagDefs1, LinkedList<StructureTagDefinition> strTagDefs2,
                                      LinkedList<TagPairDefinition> tagPairDefs1, LinkedList<TagPairDefinition> tagPairDefs2,
                                      LinkedList<PlaceholderTagDefinition> placeholderDefs1, LinkedList<PlaceholderTagDefinition> placeholderDefs2,
                                      LinkedList<TerminologyData> termData1, LinkedList<TerminologyData> termData2,
                                      FileSkeleton skel1, FileSkeleton skel1Copy) {
        skel1.setFileId("file2");
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setFileId("file1");
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setCommentDefinitions(comments2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setCommentDefinitions(comments1);
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setContextDefinitions(contextDefs2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setContextDefinitions(contextDefs1);
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setContexts(contexts2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setContexts(contexts1);
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setFormattingGroups(formatGroup2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setFormattingGroups(formatGroup1);
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setStructureTagDefinitions(strTagDefs2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setStructureTagDefinitions(strTagDefs1);
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setTagPairDefinitions(tagPairDefs2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setTagPairDefinitions(tagPairDefs1);
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setPlaceholderTagDefinitions(placeholderDefs2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setPlaceholderTagDefinitions(placeholderDefs1);
        Assert.assertTrue(skel1.equals(skel1Copy));

        skel1.setTerminologyData(termData2);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setTerminologyData(termData1);
        Assert.assertTrue(skel1.equals(skel1Copy));
                                                    //null fields testing
        skel1.setFileId(null);
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1Copy.setFileId(null);
        Assert.assertTrue(skel1.equals(skel1Copy));
        skel1Copy.setFileId("file1");
        Assert.assertFalse(skel1.equals(skel1Copy));
        skel1.setFileId("file1");
        Assert.assertTrue(skel1.equals(skel1Copy));
    }

    @Test
    public void testCommentDefinition() {

        Date date1 = new Date();
        Date date2 = new Date();
        date1.setTime(1000000000);
        date2.setTime(2000000000);

        CommentDefinition com1 = new CommentDefinition();
        Assert.assertTrue(com1.hashCode() == 0);
        com1.setId(1);
        com1.setText("t1");
        com1.setAuthor("a1");
        com1.setDate(date1);
        com1.setCommentSeverity(CommentSeverity.HIGH);

        CommentDefinition com1Copy = new CommentDefinition();
        com1Copy.setId(1);
        com1Copy.setText("t1");
        com1Copy.setAuthor("a1");
        com1Copy.setDate(date1);
        com1Copy.setCommentSeverity(CommentSeverity.HIGH);

        testMetadata(com1, com1Copy);

        Assert.assertTrue(com1.getCommentSeverity().equals(com1Copy.getCommentSeverity()));
        Assert.assertTrue(com1.equals(com1Copy));
        Assert.assertTrue(com1.deepEquals(com1Copy));

        testCommentDefinitionWhiteBox(date1, date2, com1, com1Copy);
    }

    private void testCommentDefinitionWhiteBox(Date date1, Date date2, CommentDefinition com1, CommentDefinition com1Copy) {
        com1.setId(2);
        Assert.assertFalse(com1.equals(com1Copy));
        com1.setId(1);
        Assert.assertTrue(com1.equals(com1Copy));

        com1.setText("t2");
        Assert.assertFalse(com1.equals(com1Copy));
        com1.setText("t1");
        Assert.assertTrue(com1.equals(com1Copy));

        com1.setAuthor("a2");
        Assert.assertFalse(com1.equals(com1Copy));
        com1.setAuthor("a1");
        Assert.assertTrue(com1.equals(com1Copy));

        com1.setDate(date2);
        Assert.assertFalse(com1.equals(com1Copy));
        com1.setDate(date1);
        Assert.assertTrue(com1.equals(com1Copy));

        com1.setCommentSeverity(CommentSeverity.LOW);
        Assert.assertFalse(com1.equals(com1Copy));
        com1.setCommentSeverity(CommentSeverity.HIGH);
        Assert.assertTrue(com1.equals(com1Copy));
                                                    //test null fields
        com1.setId(null);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setId(null);
        Assert.assertTrue(com1.equals(com1Copy));
        com1.setId(1);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setId(1);
        Assert.assertTrue(com1.equals(com1Copy));

        com1.setText(null);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setText(null);
        Assert.assertTrue(com1.equals(com1Copy));
        com1.setText("t1");
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setText("t1");
        Assert.assertTrue(com1.equals(com1Copy));

        com1.setAuthor(null);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setAuthor(null);
        Assert.assertTrue(com1.equals(com1Copy));
        com1.setAuthor("a1");
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setAuthor("a1");
        Assert.assertTrue(com1.equals(com1Copy));

        com1.setDate(null);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setDate(null);
        Assert.assertTrue(com1.equals(com1Copy));
        com1.setDate(date1);
        Assert.assertFalse(com1.equals(com1Copy));
        com1Copy.setDate(date1);
        Assert.assertTrue(com1.equals(com1Copy));
    }

    @Test
    public void testContextDefinition() {

        ContextDefinition con1 = new ContextDefinition();
        Assert.assertTrue(con1.hashCode() == 0);
        con1.setId(1);
        con1.setTmContext(true);
        con1.setStructureContext(false);
        con1.setTypeId("t1");
        con1.setDisplayName("name1");
        con1.setDisplayCode("code1");
        con1.setDisplayColor("color1");
        con1.setDescription("desc1");
        con1.setFormattingGroupId(1);

        Assert.assertTrue(con1.getId() == 1);
        Assert.assertTrue(con1.isTmContext());
        Assert.assertFalse(con1.isStructureContext());
        Assert.assertTrue(con1.getTypeId().equals("t1"));
        Assert.assertTrue(con1.getDisplayName().equals("name1"));
        Assert.assertTrue(con1.getDisplayCode().equals("code1"));
        Assert.assertTrue(con1.getDisplayColor().equals("color1"));
        Assert.assertTrue(con1.getDescription().equals("desc1"));
        Assert.assertTrue(con1.getFormattingGroupId() == 1);

        ContextDefinition con1Copy = new ContextDefinition();
        con1Copy.setId(1);
        con1Copy.setTmContext(true);
        con1Copy.setStructureContext(false);
        con1Copy.setTypeId("t1");
        con1Copy.setDisplayName("name1");
        con1Copy.setDisplayCode("code1");
        con1Copy.setDisplayColor("color1");
        con1Copy.setDescription("desc1");
        con1Copy.setFormattingGroupId(1);

        Assert.assertTrue(con1.equals(con1Copy));
        Assert.assertTrue(con1.deepEquals(con1Copy));

        testMetadata(con1, con1Copy);

        testContextDefinitionWhiteBox(con1, con1Copy);
    }

    private void testContextDefinitionWhiteBox(ContextDefinition con1, ContextDefinition con1Copy) {
        con1.setId(2);
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setId(1);
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setTmContext(false);
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setTmContext(true);
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setStructureContext(true);
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setStructureContext(false);
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setTypeId("t2");
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setTypeId("t1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDisplayName("name2");
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setDisplayName("name1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDisplayCode("code2");
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setDisplayCode("code1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDisplayColor("color2");
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setDisplayColor("color1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDescription("desc2");
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setDescription("desc1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setFormattingGroupId(2);
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setFormattingGroupId(1);
        Assert.assertTrue(con1.equals(con1Copy));
                                                    //null testing
        con1.setId(null);
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setId(null);
        Assert.assertTrue(con1.equals(con1Copy));
        con1.setId(1);
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setId(1);
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setTypeId(null);
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setTypeId(null);
        Assert.assertTrue(con1.equals(con1Copy));
        con1.setTypeId("t1");
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setTypeId("t1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDisplayName(null);
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDisplayName(null);
        Assert.assertTrue(con1.equals(con1Copy));
        con1.setDisplayName("name1");
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDisplayName("name1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDisplayCode(null);
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDisplayCode(null);
        Assert.assertTrue(con1.equals(con1Copy));
        con1.setDisplayCode("code1");
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDisplayCode("code1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDisplayColor(null);
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDisplayColor(null);
        Assert.assertTrue(con1.equals(con1Copy));
        con1.setDisplayColor("color1");
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDisplayColor("color1");
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setDescription(null);
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDescription(null);
        Assert.assertTrue(con1.equals(con1Copy));
        con1.setDescription("desc1");
        Assert.assertFalse(con1.equals(con1Copy));
        con1Copy.setDescription("desc1");
        Assert.assertTrue(con1.equals(con1Copy));
    }

    @Test
    public void testContext() {

        Context con1 = new Context();
        Assert.assertTrue(con1.hashCode() == 0);
        con1.setId(1);
        con1.setContextDefinitionId(1);
        con1.setParentContextId(1);

        Assert.assertTrue(con1.getId() == 1);
        Assert.assertTrue(con1.getContextDefinitionId() == 1);
        Assert.assertTrue(con1.getParentContextId() == 1);

        Context con1Copy = new Context();
        con1Copy.setId(1);
        con1Copy.setContextDefinitionId(1);
        con1Copy.setParentContextId(1);

        Assert.assertTrue(con1.equals(con1Copy));
        Assert.assertTrue(con1.deepEquals(con1Copy));

        testMetadata(con1, con1Copy);

        con1.setId(2);
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setId(1);
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setContextDefinitionId(2);
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setContextDefinitionId(1);
        Assert.assertTrue(con1.equals(con1Copy));

        con1.setParentContextId(2);
        Assert.assertFalse(con1.equals(con1Copy));
        con1.setParentContextId(1);
        Assert.assertTrue(con1.equals(con1Copy));
    }

    @Test
    public void testFormattingGroup() {

        HashMap<String,String> items1 = new HashMap<>();
        HashMap<String,String> items2 = new HashMap<>();
        items1.put("name", "value");

        FormattingGroup fg1 = new FormattingGroup();
        Assert.assertTrue(fg1.hashCode() == 0);
        fg1.setId(1);
        fg1.setItems(items1);

        FormattingGroup fg1Copy = new FormattingGroup();
        fg1Copy.setId(1);
        fg1Copy.setItems(items1);

        Assert.assertTrue(fg1.equals(fg1Copy));
        Assert.assertTrue(fg1.deepEquals(fg1Copy));

        testMetadata(fg1, fg1Copy);

        fg1.setId(2);
        Assert.assertFalse(fg1.equals(fg1Copy));
        fg1.setId(1);
        Assert.assertTrue(fg1.equals(fg1Copy));

        fg1.setItems(items2);
        Assert.assertFalse(fg1.equals(fg1Copy));
        fg1.setItems(items1);
        Assert.assertTrue(fg1.equals(fg1Copy));

        fg1.addItem("k1", "v1");
        Assert.assertTrue(fg1.getItems().get("k1").equals("v1"));
        Assert.assertTrue(fg1.getId() == 1);
    }

    @Test
    public void testStructureTagDefinition() {

        LinkedList<LocalizableSubContent> subContent1 = new LinkedList<>();
        LinkedList<LocalizableSubContent> subContent2 = new LinkedList<>();
        subContent1.add(new LocalizableSubContent());

        StructureTagDefinition st1 = new StructureTagDefinition();
        Assert.assertTrue(st1.hashCode() == 0);
        st1.setId(1);
        st1.setDisplayText("t1");
        st1.setSubContent(subContent1);
        st1.setTagContent("tagContent1");

        Assert.assertTrue(st1.getId() == 1);
        Assert.assertTrue(st1.getDisplayText().equals("t1"));
        Assert.assertTrue(st1.getTagContent().equals("tagContent1"));
        Assert.assertTrue(st1.getSubContent() != null);

        StructureTagDefinition st1Copy = new StructureTagDefinition();
        st1Copy.setId(1);
        st1Copy.setDisplayText("t1");
        st1Copy.setSubContent(subContent1);
        st1Copy.setTagContent("tagContent1");

        Assert.assertTrue(st1.equals(st1Copy));
        Assert.assertTrue(st1.deepEquals(st1Copy));

        testMetadata(st1, st1Copy);

        testStructureTagDefinitionWhiteBox(subContent1, subContent2, st1, st1Copy);
    }

    private void testStructureTagDefinitionWhiteBox(LinkedList<LocalizableSubContent> subContent1, LinkedList<LocalizableSubContent> subContent2,
                                                    StructureTagDefinition st1, StructureTagDefinition st1Copy) {
        st1.setId(2);
        Assert.assertFalse(st1.equals(st1Copy));
        st1.setId(1);
        Assert.assertTrue(st1.equals(st1Copy));

        st1.setDisplayText("t2");
        Assert.assertFalse(st1.equals(st1Copy));
        st1.setDisplayText("t1");
        Assert.assertTrue(st1.equals(st1Copy));

        st1.setSubContent(subContent2);
        Assert.assertFalse(st1.equals(st1Copy));
        st1.setSubContent(subContent1);
        Assert.assertTrue(st1.equals(st1Copy));

        st1.setTagContent("tagContent2");
        Assert.assertFalse(st1.equals(st1Copy));
        st1.setTagContent("tagContent1");
        Assert.assertTrue(st1.equals(st1Copy));
                                                    //null testing
        st1.setDisplayText(null);
        Assert.assertFalse(st1.equals(st1Copy));
        st1Copy.setDisplayText(null);
        Assert.assertTrue(st1.equals(st1Copy));
        st1.setDisplayText("t1");
        Assert.assertFalse(st1.equals(st1Copy));
        st1Copy.setDisplayText("t1");
        Assert.assertTrue(st1.equals(st1Copy));

        st1.setTagContent(null);
        Assert.assertFalse(st1.equals(st1Copy));
        st1Copy.setTagContent(null);
        Assert.assertTrue(st1.equals(st1Copy));
        st1.setTagContent("tagContent1");
        Assert.assertFalse(st1.equals(st1Copy));
        st1Copy.setTagContent("tagContent1");
        Assert.assertTrue(st1.equals(st1Copy));
    }

    @Test
    public void testPlaceholderTagDefinition() {

        LinkedList<LocalizableSubContent> subContent1 = new LinkedList<>();
        LinkedList<LocalizableSubContent> subContent2 = new LinkedList<>();
        subContent1.add(new LocalizableSubContent());

        PlaceholderTagDefinition pdef1 = new PlaceholderTagDefinition();
        Assert.assertTrue(pdef1.hashCode() != 0);
        pdef1.setId(1);
        pdef1.setDisplayText("t1");
        pdef1.setTagContent("tagcont1");
        pdef1.setTextEquivalent("equiv1");
        pdef1.setSegmentationHint(SegmentationHint.EXCLUDE);
        pdef1.setSubContent(subContent1);

        PlaceholderTagDefinition pdef1Copy = new PlaceholderTagDefinition();
        pdef1Copy.setId(1);
        pdef1Copy.setDisplayText("t1");
        pdef1Copy.setTagContent("tagcont1");
        pdef1Copy.setTextEquivalent("equiv1");
        pdef1Copy.setSegmentationHint(SegmentationHint.EXCLUDE);
        pdef1Copy.setSubContent(subContent1);

        Assert.assertTrue(pdef1.getId() == 1);
        Assert.assertTrue(pdef1.getDisplayText().equals("t1"));
        Assert.assertTrue(pdef1.getTagContent().equals("tagcont1"));
        Assert.assertTrue(pdef1.getTextEquivalent().equals("equiv1"));
        Assert.assertTrue(pdef1.getSegmentationHint().equals(SegmentationHint.EXCLUDE));
        Assert.assertTrue(pdef1.getSubContent() != null);

        Assert.assertTrue(pdef1.equals(pdef1Copy));
        Assert.assertTrue(pdef1.deepEquals(pdef1Copy));

        testMetadata(pdef1, pdef1Copy);

        testPlaceholderTagDefinitionWhiteBox(subContent1, subContent2, pdef1, pdef1Copy);
    }

    private void testPlaceholderTagDefinitionWhiteBox(LinkedList<LocalizableSubContent> subContent1, LinkedList<LocalizableSubContent> subContent2,
                                                      PlaceholderTagDefinition pdef1, PlaceholderTagDefinition pdef1Copy) {
        pdef1.setId(2);
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1.setId(1);
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setDisplayText("t2");
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1.setDisplayText("t1");
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setTagContent("tagcont2");
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1.setTagContent("tagcont1");
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setTextEquivalent("equiv2");
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1.setTextEquivalent("equiv1");
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setSegmentationHint(SegmentationHint.INCLUDE);
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1.setSegmentationHint(SegmentationHint.EXCLUDE);
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setSubContent(subContent2);
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1.setSubContent(subContent1);
        Assert.assertTrue(pdef1.equals(pdef1Copy));
                                                        //null field testing
        pdef1.setDisplayText(null);
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setDisplayText(null);
        Assert.assertTrue(pdef1.equals(pdef1Copy));
        pdef1.setDisplayText("t1");
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setDisplayText("t1");
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setTagContent(null);
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setTagContent(null);
        Assert.assertTrue(pdef1.equals(pdef1Copy));
        pdef1.setTagContent("tagcont1");
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setTagContent("tagcont1");
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setTextEquivalent(null);
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setTextEquivalent(null);
        Assert.assertTrue(pdef1.equals(pdef1Copy));
        pdef1.setTextEquivalent("equiv1");
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setTextEquivalent("equiv1");
        Assert.assertTrue(pdef1.equals(pdef1Copy));

        pdef1.setTextEquivalent(null);
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setTextEquivalent(null);
        Assert.assertTrue(pdef1.equals(pdef1Copy));
        pdef1.setTextEquivalent("equiv1");
        Assert.assertFalse(pdef1.equals(pdef1Copy));
        pdef1Copy.setTextEquivalent("equiv1");
        Assert.assertTrue(pdef1.equals(pdef1Copy));
    }

    @Test
    public void testTagPairDefinition() {

        LinkedList<LocalizableSubContent> subContent1 = new LinkedList<>();
        LinkedList<LocalizableSubContent> subContent2 = new LinkedList<>();
        subContent1.add(new LocalizableSubContent());

        TagPairDefinition def1 = new TagPairDefinition();
        Assert.assertTrue(def1.hashCode() != 0);
        def1.setId(1);
        def1.setStartTagDisplayText("start1");
        def1.setStartTagContent("scont1");
        def1.setEndTagDisplayText("end1");
        def1.setEndTagContent("econt1");
        def1.setCanHide(true);
        def1.setSegmentationHint(SegmentationHint.EXCLUDE);
        def1.setFormattingGroupId(1);
        def1.setSubContent(subContent1);

        Assert.assertTrue(def1.getId() == 1);
        Assert.assertTrue(def1.getStartTagDisplayText().equals("start1"));
        Assert.assertTrue(def1.getStartTagContent().equals("scont1"));
        Assert.assertTrue(def1.getEndTagDisplayText().equals("end1"));
        Assert.assertTrue(def1.getEndTagContent().equals("econt1"));
        Assert.assertTrue(def1.isCanHide());
        Assert.assertTrue(def1.getSegmentationHint().equals(SegmentationHint.EXCLUDE));
        Assert.assertTrue(def1.getFormattingGroupId() == 1);
        Assert.assertTrue(def1.getSubContent().equals(subContent1));

        TagPairDefinition def1Copy = new TagPairDefinition();
        def1Copy.setId(1);
        def1Copy.setStartTagDisplayText("start1");
        def1Copy.setStartTagContent("scont1");
        def1Copy.setEndTagDisplayText("end1");
        def1Copy.setEndTagContent("econt1");
        def1Copy.setCanHide(true);
        def1Copy.setSegmentationHint(SegmentationHint.EXCLUDE);
        def1Copy.setFormattingGroupId(1);
        def1Copy.setSubContent(subContent1);

        Assert.assertTrue(def1.equals(def1Copy));
        Assert.assertTrue(def1.deepEquals(def1Copy));

        testMetadata(def1, def1Copy);

        testTagPairDefinitionWhiteBox(subContent1, subContent2, def1, def1Copy);
    }

    private void testTagPairDefinitionWhiteBox(LinkedList<LocalizableSubContent> subContent1, LinkedList<LocalizableSubContent> subContent2,
                                               TagPairDefinition def1, TagPairDefinition def1Copy) {
        def1.setId(2);
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setId(1);
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setStartTagDisplayText("start2");
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setStartTagDisplayText("start1");
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setStartTagContent("scont2");
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setStartTagContent("scont1");
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setEndTagDisplayText("end2");
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setEndTagDisplayText("end1");
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setEndTagContent("econt2");
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setEndTagContent("econt1");
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setCanHide(false);
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setCanHide(true);
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setSegmentationHint(SegmentationHint.INCLUDE);
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setSegmentationHint(SegmentationHint.EXCLUDE);
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setFormattingGroupId(2);
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setFormattingGroupId(1);
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setSubContent(subContent2);
        Assert.assertFalse(def1.equals(def1Copy));
        def1.setSubContent(subContent1);
        Assert.assertTrue(def1.equals(def1Copy));
                                                    //null tests
        def1.setStartTagDisplayText(null);
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setStartTagDisplayText(null);
        Assert.assertTrue(def1.equals(def1Copy));
        def1.setStartTagDisplayText("start1");
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setStartTagDisplayText("start1");
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setStartTagContent(null);
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setStartTagContent(null);
        Assert.assertTrue(def1.equals(def1Copy));
        def1.setStartTagContent("scont1");
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setStartTagContent("scont1");
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setEndTagDisplayText(null);
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setEndTagDisplayText(null);
        Assert.assertTrue(def1.equals(def1Copy));
        def1.setEndTagDisplayText("end1");
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setEndTagDisplayText("end1");
        Assert.assertTrue(def1.equals(def1Copy));

        def1.setEndTagContent(null);
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setEndTagContent(null);
        Assert.assertTrue(def1.equals(def1Copy));
        def1.setEndTagContent("econt1");
        Assert.assertFalse(def1.equals(def1Copy));
        def1Copy.setEndTagContent("econt1");
        Assert.assertTrue(def1.equals(def1Copy));
    }

    @Test
    public void testTerminologyData() {

        LinkedList<Term> terms1 = new LinkedList<>();
        LinkedList<Term> terms2 = new LinkedList<>();
        terms1.add(new Term());

        TerminologyData tdata1 = new TerminologyData();
        Assert.assertTrue(tdata1.hashCode() == 0);
        tdata1.setId(1);
        tdata1.setOrigin("origin1");
        tdata1.setTerms(terms1);

        TerminologyData tdata1Copy = new TerminologyData();
        tdata1Copy.setId(1);
        tdata1Copy.setOrigin("origin1");
        tdata1Copy.setTerms(terms1);

        Assert.assertTrue(tdata1.equals(tdata1Copy));
        Assert.assertTrue(tdata1.deepEquals(tdata1Copy));

        testMetadata(tdata1, tdata1Copy);

        testTerminologyDataWhiteBox(terms1, terms2, tdata1, tdata1Copy);
    }

    private void testTerminologyDataWhiteBox(LinkedList<Term> terms1, LinkedList<Term> terms2,
                                             TerminologyData tdata1, TerminologyData tdata1Copy) {
        tdata1.setId(2);
        Assert.assertFalse(tdata1.equals(tdata1Copy));
        tdata1.setId(1);
        Assert.assertTrue(tdata1.equals(tdata1Copy));

        tdata1.setOrigin("origin2");
        Assert.assertFalse(tdata1.equals(tdata1Copy));
        tdata1.setOrigin("origin1");
        Assert.assertTrue(tdata1.equals(tdata1Copy));

        tdata1.setTerms(terms2);
        Assert.assertFalse(tdata1.equals(tdata1Copy));
        tdata1.setTerms(terms1);
        Assert.assertTrue(tdata1.equals(tdata1Copy));
                                                        //null testing
        tdata1.setOrigin(null);
        Assert.assertFalse(tdata1.equals(tdata1Copy));
        tdata1Copy.setOrigin(null);
        Assert.assertTrue(tdata1.equals(tdata1Copy));
        tdata1.setOrigin("origin1");
        Assert.assertFalse(tdata1.equals(tdata1Copy));
        tdata1Copy.setOrigin("origin1");
        Assert.assertTrue(tdata1.equals(tdata1Copy));
    }

    @Test
    public void testTerm() {

        LinkedList<TermTranslation> trans1 = new LinkedList<>();
        LinkedList<TermTranslation> trans2 = new LinkedList<>();
        trans1.add(new TermTranslation());

        Term term1 = new Term();
        Assert.assertTrue(term1.hashCode() == 0);
        term1.setId("id1");
        term1.setText("text1");
        term1.setScore(0.1);
        term1.setTermTranslations(trans1);

        Term term1Copy = new Term();
        term1Copy.setId("id1");
        term1Copy.setText("text1");
        term1Copy.setScore(0.1);
        term1Copy.setTermTranslations(trans1);

        Assert.assertTrue(term1.equals(term1Copy));
        Assert.assertTrue(term1.deepEquals(term1Copy));

        testMetadata(term1, term1Copy);

        testTermWhiteBox(trans1, trans2, term1, term1Copy);
    }

    private void testTermWhiteBox(LinkedList<TermTranslation> trans1, LinkedList<TermTranslation> trans2,
                                  Term term1, Term term1Copy) {
        term1.setId("id2");
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setId("id1");
        Assert.assertTrue(term1.equals(term1Copy));

        term1.setText("text2");
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setText("text1");
        Assert.assertTrue(term1.equals(term1Copy));

        term1.setScore(0.2);
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setScore(0.1);
        Assert.assertTrue(term1.equals(term1Copy));

        term1.setTermTranslations(trans2);
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setTermTranslations(trans1);
        Assert.assertTrue(term1.equals(term1Copy));
                                                    //null tests
        term1.setId(null);
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setId(null);
        Assert.assertTrue(term1.equals(term1Copy));
        term1.setId("id1");
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setId("id1");
        Assert.assertTrue(term1.equals(term1Copy));

        term1.setText(null);
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setText(null);
        Assert.assertTrue(term1.equals(term1Copy));
        term1.setText("text1");
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setText("text1");
        Assert.assertTrue(term1.equals(term1Copy));
    }

    @Test
    public void testTermTranslation() {

        TermTranslation term1 = new TermTranslation();
        Assert.assertTrue(term1.hashCode() == 0);
        term1.setId("id1");
        term1.setText("text1");

        TermTranslation term2 = new TermTranslation();
        term2.setId("id2");
        term2.setText("text2");

        TermTranslation term1Copy = new TermTranslation();
        term1Copy.setId("id1");
        term1Copy.setText("text1");

        Assert.assertTrue(term1.equals(term1Copy));
        Assert.assertFalse(term1.equals(term2));
        Assert.assertTrue(term1.deepEquals(term1Copy));
        Assert.assertFalse(term1.deepEquals(term2));

        testMetadata(term1, term1Copy);

        term1.setId("id2");
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setId("id1");
        Assert.assertTrue(term1.equals(term1Copy));

        term1.setText("text2");
        Assert.assertFalse(term1.equals(term1Copy));
        term1.setText("text1");
        Assert.assertTrue(term1.equals(term1Copy));
                                                    //null tests
        term1.setId(null);
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setId(null);
        Assert.assertTrue(term1.equals(term1Copy));
        term1.setId("id1");
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setId("id1");
        Assert.assertTrue(term1.equals(term1Copy));

        term1.setText(null);
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setText(null);
        Assert.assertTrue(term1.equals(term1Copy));
        term1.setText("text1");
        Assert.assertFalse(term1.equals(term1Copy));
        term1Copy.setText("text1");
        Assert.assertTrue(term1.equals(term1Copy));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDocumentFile()
    {
        Map<String, Object> props1 = new HashMap();
        Map<String, Object> props2 = new HashMap();
        props1.put("k1", "v1");
        props2.put("k1", "v1");

        DocumentFile df = new DocumentFile();
        Assert.assertTrue(df.hashCode() == 0);
        df.setId("id1");
        df.setDocJSON("doc1");
        Assert.assertNotNull(df.getProperties());
        df.setProperties(props1);
        Assert.assertTrue(df.hashCode() != 0);

        DocumentFile dfCopy = new DocumentFile("id1","doc1");
        dfCopy.setProperties(props2);
        Assert.assertTrue(dfCopy.getId().equals("id1"));
        Assert.assertTrue(dfCopy.getDocJSON().equals("doc1"));
        Assert.assertTrue(dfCopy.getProperties().equals(props2));

        Assert.assertTrue(df.equals(df));
        Assert.assertFalse(df.equals(null));
        Assert.assertFalse(df.equals("string object"));
        Assert.assertTrue(df.equals(dfCopy));

        df.setId("id2");
        Assert.assertFalse(df.equals(dfCopy));
        df.setId("id1");
        Assert.assertTrue(df.equals(dfCopy));
        df.setId(null);
        Assert.assertFalse(df.equals(dfCopy));
        dfCopy.setId(null);
        Assert.assertTrue(df.equals(dfCopy));
        df.setId("id1");
        Assert.assertFalse(df.equals(dfCopy));
        dfCopy.setId("id1");
        Assert.assertTrue(df.equals(dfCopy));

        df.setDocJSON("doc2");
        Assert.assertFalse(df.equals(dfCopy));
        df.setDocJSON("doc1");
        Assert.assertTrue(df.equals(dfCopy));
        df.setDocJSON(null);
        Assert.assertFalse(df.equals(dfCopy));
        dfCopy.setDocJSON(null);
        Assert.assertTrue(df.equals(dfCopy));
        df.setDocJSON("doc1");
        Assert.assertFalse(df.equals(dfCopy));
        dfCopy.setDocJSON("doc1");
        Assert.assertTrue(df.equals(dfCopy));

        df.setProperties(null);
        Assert.assertFalse(df.equals(dfCopy));
        dfCopy.setProperties(null);
        Assert.assertTrue(df.equals(dfCopy));
        df.setProperties(props1);
        Assert.assertFalse(df.equals(dfCopy));
        dfCopy.setProperties(props1);
        Assert.assertTrue(df.equals(dfCopy));

        props2.put("k2", "v2");
        dfCopy.setProperties(props2);
        Assert.assertFalse(df.equals(dfCopy));
    }
    @Override
    public void log(String message) { LOG.info(message); }
}
