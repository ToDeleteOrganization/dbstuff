package com.sdl.bcm;

import com.sdl.bcm.model.*;
import com.sdl.bcm.model.fileskeleton.FileSkeleton;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author vnastase
 * @version 1.0
 */
public class ModelTest extends TestBase {
    public static Logger LOG = Logger.getLogger(EqualsTest.class);

    @Override
    public void before() {
        super.before();
    }

    @Override
    public void after() {
        super.before();
    }

    @Test
    public void testDocument() {

        Document doc = new Document();
        doc.setId("documentId");
        doc.setModelVersion("modelVersion");
        doc.setName("documentName");
        doc.setSourceLanguageCode("slang");
        doc.setTargetLanguageCode("tlang");

        //test getFile
        File f1 = new File();
        f1.setId("file1Id");
        File f2 = new File();
        f2.setId("file2Id");
        doc.addFile(f1);
        doc.addFile(f2);

        Assert.assertTrue(f1.equals(doc.getFile("file1Id")));
        Assert.assertTrue(f2.equals(doc.getFile("file2Id")));
        Assert.assertFalse(f1.equals(doc.getFile("invalidID")));

        //test getSegments
        ParagraphUnit pu1 = new ParagraphUnit();
        pu1.setId("parUnit1Id");
        ParagraphUnit pu2 = new ParagraphUnit();
        pu2.setId("parUnit2Id");
        f1.addParagraphUnit(pu1);
        f1.addParagraphUnit(pu2);
        ParagraphUnit pu3 = new ParagraphUnit();
        pu3.setId("parUnit3Id");
        f2.addParagraphUnit(pu3);

        Paragraph p1s = new Paragraph();
        p1s.setId("p1sID");
        Segment ss1 = new Segment(p1s,"ss1ID");
        p1s.addChild(ss1);
        Paragraph p2s = new Paragraph();
        p2s.setId("p2sID");
        Segment ss2 = new Segment(p2s,"ss2ID");
        p2s.addChild(ss2);
        Paragraph p2t = new Paragraph();
        p2t.setId("p2tID");
        Segment st2 = new Segment(p2t,"st2ID");
        p2t.addChild(st2);
        Paragraph p3s = new Paragraph();
        p3s.setId("p3sID");
        Segment ss3 = new Segment(p3s,"ss3ID");
        p3s.addChild(ss3);
        Segment ss32 = new Segment(p3s,"ss32ID");
        p3s.addChild(ss32);

        pu1.setSource(p1s);
        pu2.setSource(p2s);
        pu2.setTarget(p2t);
        pu3.setSource(p3s);

        // doc{ f1 [pu1{p1s(ss1), null}, pu2{p2s(ss2), p2t(st2)}], f2 [pu3{p3s(ss3,ss32), null}] }

        List<Segment> segments = doc.getSegments();
        Assert.assertTrue(segments.get(0).equals(ss1));
        Assert.assertTrue(segments.get(1).equals(ss2));
        Assert.assertTrue(segments.get(2).equals(ss3));
        Assert.assertTrue(segments.get(3).equals(ss32));
        Assert.assertFalse(segments.contains(st2));

        //test getParagraphUnits

        ParagraphUnit pu4 = new ParagraphUnit();
        pu4.setId("parUnit4Id");
        pu4.setStructure(true);
        f2.addParagraphUnit(pu4);
        ParagraphUnit pu5 = new ParagraphUnit();
        pu5.setId("parUnit5Id");
        pu5.setStructure(true);
        f2.addParagraphUnit(pu5);

        // doc{ f1[pu1(f),pu2(f)], f2[pu3(f),pu4(t),pu5(t)] }
        List<ParagraphUnit> puList;

        puList = doc.getParagraphUnits(0,1,false);//just first pu
        Assert.assertTrue(puList.get(0).equals(pu1));
        puList = doc.getParagraphUnits(0,2,false);//first 2
        Assert.assertTrue(puList.get(0).equals(pu1) && puList.get(1).equals(pu2));
        puList = doc.getParagraphUnits(0,3,false);//first 3 non-structure
        Assert.assertTrue(puList.get(0).equals(pu1) && puList.get(1).equals(pu2) && puList.get(2).equals(pu3));
        puList = doc.getParagraphUnits(0,12,false);//first 3 non structure
        Assert.assertTrue(puList.get(0).equals(pu1) && puList.get(1).equals(pu2) && puList.get(2).equals(pu3));
        puList = doc.getParagraphUnits(0,5,true);//all 5
        Assert.assertTrue(puList.get(0).equals(pu1)
                && puList.get(1).equals(pu2)
                && puList.get(2).equals(pu3)
                && puList.get(3).equals(pu4)
                && puList.get(4).equals(pu5)
        );
        puList = doc.getParagraphUnits(0,9,true);//all 5
        Assert.assertTrue(puList.get(0).equals(pu1)
                        && puList.get(1).equals(pu2)
                        && puList.get(2).equals(pu3)
                        && puList.get(3).equals(pu4)
                        && puList.get(4).equals(pu5)
        );
        puList = doc.getParagraphUnits(0,4,true);//first 4
        Assert.assertTrue(puList.get(0).equals(pu1)
                        && puList.get(1).equals(pu2)
                        && puList.get(2).equals(pu3)
                        && puList.get(3).equals(pu4)
                        && ! puList.contains(pu5)
        );
        puList = doc.getParagraphUnits(7,4,true);//empty list returned
        Assert.assertTrue(puList.size() == 0);
        puList = doc.getParagraphUnits(7,12,true);//empty list returned
        Assert.assertTrue(puList.size() == 0);
        puList = doc.getParagraphUnits(-2,14,true);//return all
        Assert.assertTrue(puList.get(0).equals(pu1)
                        && puList.get(1).equals(pu2)
                        && puList.get(2).equals(pu3)
                        && puList.get(3).equals(pu4)
                        && puList.get(4).equals(pu5)
        );
        puList = doc.getParagraphUnits(12,2,true);//empty list returned
        Assert.assertTrue(puList.size() == 0);
        puList = doc.getParagraphUnits(-2,-14,true);//empty list returned
        Assert.assertTrue(puList.size() == 0);
        puList = doc.getParagraphUnits(4,1,true);//empty list returned
        Assert.assertTrue(puList.size() == 0);
        puList = doc.getParagraphUnits(-42,-1,true);//empty list returned
        Assert.assertTrue(puList.size() == 0);

    }

    @Test
    public void testFile() {
        Document doc = new Document();
        LinkedList<Integer> commDef1 = new LinkedList<>();
        commDef1.add(1);
        FileSkeleton skel1 = new FileSkeleton();
        skel1.setFileId("file1");

        File file = new File();
        file.setId("id1");
        file.setParentDocument(doc);
        file.setOriginalEncoding("enc1");
        file.setPreferredTargetEncoding("tenc1");
        file.setOriginalFileName("file1");
        file.setFileTypeDefinitionId("ftypeid1");
        file.setCommentDefinitionIds(commDef1);
        file.setSkeleton(skel1);
        Assert.assertTrue(file.getPreferredTargetEncoding().equals("tenc1"));

        Assert.assertTrue(file.getParentDocument().equals(doc));

        ParagraphUnit pu1 = new ParagraphUnit();
        pu1.setId("pu1");
        ParagraphUnit pu2 = new ParagraphUnit();
        pu2.setId("pu2");
        ParagraphUnit pu3 = new ParagraphUnit();
        pu3.setId("pu3");
        ParagraphUnit puM = new ParagraphUnit();
        puM.setId("puM");
        puM.setContextId(10);
        ParagraphUnit puM2 = new ParagraphUnit();
        puM2.setId("puM");
        puM2.setContextId(15);

        file.addParagraphUnit(pu1);
        file.addParagraphUnit(1, pu2);
        file.addParagraphUnit(2, pu3);
        file.addParagraphUnit(2, puM);
        Assert.assertTrue(file.getParagraphUnits().get(0).equals(pu1));
        Assert.assertTrue(file.getParagraphUnits().get(1).equals(pu2));
        Assert.assertTrue(file.getParagraphUnits().get(2).equals(puM));
        Assert.assertTrue(file.getParagraphUnits().get(3).equals(pu3));

        Assert.assertTrue(file.containsParagraphUnit(pu1));
        Assert.assertTrue(file.getParagraph("pu1").equals(pu1));

        Assert.assertTrue(file.getParagraph("puM").getContextId() == 10);
        file.updateParagraphUnit(puM2);
        Assert.assertTrue(file.getParagraph("puM").getContextId() == 15);

        //addDependencyFile
        DependencyFile df1 = new DependencyFile();
        df1.setId("df1");
        DependencyFile df2 = new DependencyFile();
        df2.setId("df2");

        file.addDependencyFile(df1);
        Assert.assertTrue(file.getDependencyFiles().get(0).equals(df1));
        file.addDependencyFile(df2);
        Assert.assertTrue(file.getDependencyFiles().get(1).equals(df2));
    }

    @Test
    public void testFeedBackContainerDuplicateWithoutChildren() {
        Date date1 = new Date();
        date1.setTime(1000000000);
        FeedbackContainer fed1 = new FeedbackContainer();
        fed1.setAuthor("a1");
        fed1.setTimestamp(date1);
        fed1.setCategory("c1");
        fed1.setDocumentCategory("dc1");
        fed1.setSeverity("s1");
        fed1.setCommentDefinitionId(1);
        fed1.setFeedbackType(FeedbackType.ADDED);

        FeedbackContainer fedClone = (FeedbackContainer) fed1.duplicateWithoutChildren();
        Assert.assertTrue(fed1.equals(fedClone));
        //toString check:
        Assert.assertTrue(fed1.toString().equals("FeedbackContainer{ADDED,s1}"));
    }

    @Test
    public void testStructureTag() {
        Paragraph par = new Paragraph();
        par.setId("par");
        StructureTag st = new StructureTag();
        st.setId("st");
        st.setParent(par);
        st.setStructureTagDefinitionId(12);
        //duplicateWithoutChildren
        StructureTag clone = (StructureTag) st.duplicateWithoutChildren();
        Assert.assertTrue(st.equals(clone));
    }

    @Test
    public void testMarkupdataContainer() {
        MarkupDataContainer mc = new TagPair();

        //addChild test
        TextMarkup textMarkup = new TextMarkup(mc,"some text");
        mc.addChild(textMarkup);
        TextMarkup textMarkup2 = new TextMarkup(mc,"some text");
        mc.addChild(textMarkup2);
        Assert.assertTrue(mc.getChildren().get(0).equals(textMarkup));
        Assert.assertTrue(mc.getChildren().get(1).equals(textMarkup2));

        //populateClone test
        Segment seg = new Segment();
        seg.setId("id");
        seg.setType("tipe");
        seg.addMetaData("k", "v");

        Segment clone = (Segment) seg.duplicateWithoutChildren(); //this calls populateClone()
        Assert.assertTrue(clone.getId().equals("id"));
        Assert.assertTrue(clone.getType().equals("tipe"));
        Assert.assertTrue(clone.getMetaData("k").equals("v"));

    }

    @Test
    public void testParagraph() {

        ParagraphUnit parUnit = new ParagraphUnit();

        Paragraph par = new Paragraph();
        par.setId("id1");
        par.setParagraphUnit(parUnit);

        //duplicateWithoutChildren test
        MarkupDataContainer parClone = par.duplicateWithoutChildren();
        Assert.assertTrue(par.equals(parClone));
        parUnit.addMetaData("k1", "v1");
        Assert.assertNotNull(parUnit.getMetadata());
        Assert.assertTrue(par.equals(par.duplicateWithoutChildren()));
        parUnit.addMetaData("k2", "v2");
        Assert.assertNotNull(parUnit.getMetadata());
        Assert.assertTrue(par.equals(par.duplicateWithoutChildren()));

        //updateSegment test
        TranslationOrigin to1 = new TranslationOrigin();
        to1.setMatchPercent(15);
        TranslationOrigin to2 = new TranslationOrigin();
        to2.setMatchPercent(25);

        Segment s1 = new Segment(par,"id1");
        Segment s2 = new Segment(par,"id2");
        s2.setSegmentNumber("2");
        s2.setCharacterCount(21);
        s2.setWordCount(22);
        s2.setLocked(false);
        s2.setConfirmationLevel(ConfirmationLevel.NOT_TRANSLATED);
        s2.setTranslationOrigin(to1);
        s2.addChild(new TextMarkup(s2, "s2 Text1"));
        s2.addChild(new TextMarkup(s2, "s2 Text2"));

        Segment sup = (Segment) s2.duplicateWithoutChildren();
        sup.setSegmentNumber("2");
        sup.setCharacterCount(31);
        sup.setWordCount(32);
        sup.setLocked(true);
        sup.setConfirmationLevel(ConfirmationLevel.DRAFT);
        sup.setTranslationOrigin(to2);
        sup.addChild(new TextMarkup(sup, "sup Text1"));

        par.addChild(s1);
        par.addChild(new TextMarkup(par, "par text"));
        par.addChild(s2);

        sup.setSegmentNumber("4");// segment 4 doesn't exist; shouldn't update
        par.updateSegment(sup);
        Assert.assertTrue(s2.equals(par.getChildren().get(2)));

        sup.setSegmentNumber("2");//update segment 2, the 3rd child of paragraph par
        par.updateSegment(sup);

        Assert.assertNotNull(par.getChildren().get(2));
        Assert.assertTrue(Segment.TYPE.equals(par.getChildren().get(2).getType()));
        Segment seg = (Segment) par.getChildren().get(2);
        Assert.assertTrue(seg.getSegmentNumber().equals("2"));
        Assert.assertTrue(seg.getId().equals("id2"));
        Assert.assertTrue(seg.getCharacterCount() == 31);
        Assert.assertTrue(seg.getWordCount() == 32);
        Assert.assertTrue(seg.isLocked());
        Assert.assertTrue(seg.getConfirmationLevel().equals(ConfirmationLevel.DRAFT));
        Assert.assertTrue(seg.getTranslationOrigin().equals(to2));
        Assert.assertTrue(seg.getChildren().get(0) instanceof TextMarkup);
        TextMarkup tm = (TextMarkup) seg.getChildren().get(0);
        Assert.assertTrue(tm.getText().equals("sup Text1"));

        //metadata updating test
        sup.addMetaData("k1", "v1");
        par.updateSegment(sup);
        Assert.assertTrue(par.getChildren().get(2).getMetadata().get("k1").equals("v1"));
        sup.getMetadata().clear();
        sup.addMetaData("k2", "v2");
        par.updateSegment(sup);
        Assert.assertTrue(par.getChildren().get(2).getMetadata().get("k2").equals("v2"));
        Assert.assertFalse(par.getChildren().get(2).getMetadata().containsKey("k1"));
        Assert.assertFalse(par.getChildren().get(2).getMetadata().containsValue("v1"));
    }

    @Test
    public void testSegment() {
        Segment seg = new Segment();
        seg.setId("segId");

        //getPlainText
        seg.addChild(new TextMarkup(seg,"some text "));
        seg.addChild(new TextMarkup(seg,"and then some "));
        Assert.assertTrue(seg.getPlainText().equals("some text and then some "));
    }

    @Override
    public void log(String message) { LOG.info(message); }

}
