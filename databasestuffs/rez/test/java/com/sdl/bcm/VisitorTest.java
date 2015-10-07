package com.sdl.bcm;

import com.sdl.bcm.manager.Utils;
import com.sdl.bcm.model.*;
import com.sdl.bcm.visitor.BCMVisitor;
import com.sdl.bcm.visitor.helper.TerminologyMarker;
import com.sdl.bcm.visitor.impl.ExtractPlainTextVisitor;
import com.sdl.bcm.visitor.impl.GetSegmentsVisitor;
import com.sdl.bcm.visitor.impl.GetTextMarkupVisitor;
import com.sdl.bcm.visitor.impl.SegmentVisitor;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * @author vnastase
 * @version 1.0
 */
public class VisitorTest extends TestBase {
    public static Logger LOG = Logger.getLogger(VisitorTest.class);

    @Override
    public void log(String message) {
        LOG.info(message);
    }

    @Override
    public void before() {
        super.before();
    }

    @Override
    public void after() {
        super.before();
    }

    @Test
    public void testAcceptVisitor() {
        ExtractPlainTextVisitor visitor = new ExtractPlainTextVisitor();
        Document doc = new Document();
        doc.setId("docId");
        File file = new File();
        file.setId("fileId");
        file.setParentDocument(doc);
        doc.addFile(file);
        ParagraphUnit pu = new ParagraphUnit();
        pu.setId("puId");
        pu.setParentFile(file);
        file.addParagraphUnit(pu);
        Paragraph paragraph = new Paragraph();
        paragraph.setId("paragraphId");
        paragraph.setParagraphUnit(pu);
        pu.setSource(paragraph);

        //dependency file
        DependencyFile df = new DependencyFile();
        Assert.assertFalse(df.accept(visitor));

        //TranslationOrigin
        TranslationOrigin to = new TranslationOrigin();
        Assert.assertTrue(to.accept(visitor));

        //StructureTag
        StructureTag st = new StructureTag();
        Assert.assertTrue(st.accept(visitor));

        //PlaceholderTag
        PlaceholderTag pt = new PlaceholderTag();

        //LocalizableSubContent
        LocalizableSubContent ls = new LocalizableSubContent();
        Assert.assertFalse(ls.accept(visitor));

        //TextMarkup
        visitor = new ExtractPlainTextVisitor();
        TextMarkup tm = new TextMarkup();
        tm.setId("tmId");
        tm.setParent(paragraph);
        paragraph.addChild(tm);
        tm.setText("text markup text");
        Assert.assertTrue(tm.accept(visitor));
        Assert.assertTrue(visitor.getText().equals(tm.toString()));

        //LockedContentContainer
        visitor = new ExtractPlainTextVisitor();
        LockedContentContainer lc = new LockedContentContainer();
        lc.setId("lcId");
        lc.setParent(paragraph);
        paragraph.addChild(lc);
        lc.addChild(new TextMarkup(lc, "locked text"));
        Assert.assertTrue(lc.accept(visitor)); //locked containers can accept extract text visitors
        Assert.assertTrue(visitor.getText().equals("")); // locked containers don't return their text
        GetSegmentsVisitor vis = new GetSegmentsVisitor();
        lc.addChild(new LockedContentContainer(lc));
        Assert.assertTrue(lc.accept(vis));

        //TagPair
        visitor = new ExtractPlainTextVisitor();
        TagPair tp = new TagPair();
        tp.setId("tpId");
        tp.setParent(paragraph);
        paragraph.addChild(tp);
        tp.addChild(new TextMarkup(lc, "tag pair text"));
        Assert.assertTrue(tp.accept(visitor));
        Assert.assertTrue(visitor.getText().equals("tag pair text"));

        //CommentContainer
        visitor = new ExtractPlainTextVisitor();
        CommentContainer cc = new CommentContainer();
        cc.setId("ccId");
        cc.setParent(paragraph);
        paragraph.addChild(cc);
        cc.addChild(new TextMarkup(cc, "commented text"));
        Assert.assertTrue(cc.accept(visitor));
        Assert.assertTrue(visitor.getText().equals("commented text"));

        //FeedbackContainer
        visitor = new ExtractPlainTextVisitor();
        FeedbackContainer fc = new FeedbackContainer();
        fc.setId("fcId");
        fc.setParent(paragraph);
        paragraph.addChild(fc);
        fc.addChild(new TextMarkup(fc, "feedback text"));
        Assert.assertTrue(fc.accept(visitor));
        Assert.assertTrue(visitor.getText().equals("")); // no text is extracted
        fc.setFeedbackType(FeedbackType.ADDED);
        visitor = new ExtractPlainTextVisitor();
        fc.accept(visitor);
        Assert.assertTrue(visitor.getText().equals("feedback text")); // text is extracted only from ADDED and COMMENT containers

        //RevisionContainer
        visitor = new ExtractPlainTextVisitor();
        RevisionContainer rc = new RevisionContainer();
        rc.setId("rcId");
        rc.setParent(paragraph);
        paragraph.addChild(rc);
        rc.addChild(new TextMarkup(fc, "revision text"));
        Assert.assertTrue(rc.accept(visitor));
        Assert.assertTrue(visitor.getText().equals("")); // no text is extracted
        rc.setRevisionType(RevisionType.INSERTED);
        visitor = new ExtractPlainTextVisitor();
        rc.accept(visitor);
        Assert.assertTrue(visitor.getText().equals("revision text")); // text is extracted only from INSERTED and UNCHANGED containers

        //TerminologyAnnotationContainer
        visitor = new ExtractPlainTextVisitor();
        TerminologyAnnotationContainer tc = new TerminologyAnnotationContainer();
        tc.setId("tcId");
        tc.setParent(paragraph);
        paragraph.addChild(tc);
        tc.addChild(new TextMarkup(tc, "terminology text"));
        Assert.assertTrue(tc.accept(visitor));
        Assert.assertTrue(visitor.getText().equals("terminology text"));
    }

    @Test
    public void testGetTextMarkupVisitor() {
        GetTextMarkupVisitor visitor = new GetTextMarkupVisitor();

        //extract the TextMarkups of a given BCM element into a list
        File file = new File();
        ParagraphUnit pu1 = new ParagraphUnit();
        pu1.setId("pu1");
        pu1.setParentFile(file);
        file.addParagraphUnit(pu1);
        Paragraph source1 = new Paragraph();
        source1.setParagraphUnit(pu1);
        pu1.setSource(source1);

        Segment seg1 = new Segment();
        Segment seg2 = new Segment();
        source1.addChild(seg1);
        source1.addChild(seg2);
        seg1.setParent(source1);
        seg2.setParent(source1);

        seg1.addChild(new TextMarkup(seg1, "AA "));
        seg1.addChild(new TextMarkup(seg1, "BBB "));
        seg2.addChild(new TextMarkup(seg1, "C "));

        file.accept(visitor);
        List<TextMarkup> textList = visitor.getTextMarkupList();
        Assert.assertTrue(textList.get(0).getText().equals("AA "));
        Assert.assertTrue(textList.get(1).getText().equals("BBB "));
        Assert.assertTrue(textList.get(2).getText().equals("C "));
    }

    @Test
    public void testSegmentVisitor() {
        SegmentVisitor visitor = new SegmentVisitor();

        //adding segment numbers to the segments of a file
        File file = new File();
        ParagraphUnit pu1 = new ParagraphUnit();
        pu1.setId("pu1");
        pu1.setParentFile(file);
        file.addParagraphUnit(pu1);
        Assert.assertTrue(visitor.visitEnter(pu1));// basic null check

        Paragraph source1 = new Paragraph();
        source1.setParagraphUnit(pu1);
        pu1.setSource(source1);
        Paragraph target1 = new Paragraph();
        target1.setParagraphUnit(pu1);
        pu1.setTarget(target1);

        Segment seg1s1 = new Segment();
        Segment seg1s2 = new Segment();
        Segment seg1t1 = new Segment();
        Segment seg1t2 = new Segment();
        source1.addChild(seg1s1);
        source1.addChild(seg1s2);
        target1.addChild(seg1t1);
        target1.addChild(seg1t2);
        seg1s1.setParent(source1);
        seg1s2.setParent(source1);
        seg1t1.setParent(target1);
        seg1t1.setParent(target1);

        file.accept(visitor);
        Assert.assertTrue(seg1s1.getSegmentNumber().equals("1"));
        Assert.assertTrue(seg1s2.getSegmentNumber().equals("2"));
        Assert.assertTrue(seg1t1.getSegmentNumber().equals("1"));
        Assert.assertTrue(seg1t2.getSegmentNumber().equals("2"));

        //basic visitEnter calls
        Assert.assertTrue(visitor.visit(source1));
        Assert.assertTrue(visitor.visitEnter(target1));
        TagPair tp = new TagPair();
        Assert.assertTrue(visitor.visitEnter(tp));
        PlaceholderTag pl = new PlaceholderTag();
        Assert.assertTrue(visitor.visitEnter(pl));
        TextMarkup tx = new TextMarkup();
        Assert.assertTrue(visitor.visitEnter(tx));
        LockedContentContainer lk = new LockedContentContainer();
        Assert.assertTrue(visitor.visitEnter(lk));
    }

    @Test
    public void testExtractPlainTextVisitor() {
        ExtractPlainTextVisitor visitor = new ExtractPlainTextVisitor();

        // ParagraphUnit with no Target field
        File file = new File();
        ParagraphUnit pu = new ParagraphUnit();
        pu.setParentFile(file);
        Paragraph paragraph = new Paragraph();
        paragraph.setParagraphUnit(pu);
        pu.setSource(paragraph);

        TextMarkup tx1 = new TextMarkup();
        tx1.setText("AAA ");
        tx1.setParent(paragraph);
        paragraph.addChild(tx1);

        pu.accept(visitor);
        String str = visitor.getText();

        Assert.assertTrue("AAA ".equals(str));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeepEqualsCollection() {
        List s1 = null;
        List s2 = null;
        Assert.assertTrue(Utils.deepEqualsCollection(s1,s2));
        s1 = new LinkedList();
        Assert.assertFalse(Utils.deepEqualsCollection(s1,s2));
        s1 = null;
        s2 = new LinkedList();
        Assert.assertFalse(Utils.deepEqualsCollection(s1,s2));
        s1 = new LinkedList();
        Assert.assertTrue(Utils.deepEqualsCollection(s1, s2));
        s1.add("some text");
        s2.add(new Segment());
        Assert.assertFalse(Utils.deepEqualsCollection(s1, s2));
    }

    @Test
    public void testBCMSerializer() {
        Segment seg = new Segment();
        seg.setSegmentNumber("1");
        seg.setId("idSeg");
        seg.setType("segType");
        String segString="";
        try {
            segString = BCMSerializer.serializeObject(seg);
        } catch (IOException e) {
            log(e.getMessage());
        }
        Assert.assertFalse(segString.equals(""));

        Document doc = new Document();
        doc.setId("docId");
        doc.setName("docName");
        doc.setSourceLanguageCode("so");
        String docString = "";
        try {
            docString = BCMSerializer.serializeBCM(doc);
        } catch (IOException e) {
            log(e.getMessage());
        }
        Assert.assertFalse(docString.equals(""));
        byte docBytes[];
        docBytes = docString.getBytes(Charset.forName("UTF-8"));
        Document doc2 = new Document();
        try {
            doc2 = BCMSerializer.deserializeBCM(docBytes);
        } catch (IOException e) {
            log(e.getMessage());
        }
        Assert.assertTrue(doc.equals(doc2));
    }

    /**
     * @deprecated since 2.5; TerminologyMarker was only used by the now deprecated AddTerminologyVisitor
     */
    @Test
    @Deprecated
    public void testTerminologyMarker() {
        Segment seg = new Segment();
        TerminologyMarker tmark = new TerminologyMarker(seg);
        Assert.assertTrue(tmark.getAnnotationId() == -1);

        TerminologyMarker tm2 = new TerminologyMarker(seg, true, 3);
        Assert.assertTrue(tm2.isTermStart());
        Assert.assertTrue(tm2.getAnnotationId() == 3);
        MarkupDataContainer tm3 = tm2.duplicateWithoutChildren();
        Assert.assertTrue(tm2.equals(tm3));

        TerminologyMarker tm = new TerminologyMarker(seg,true,1);
        TerminologyMarker tmCopy = new TerminologyMarker(seg,true,1);
        TerminologyMarker tmA = new TerminologyMarker(seg,false,1);
        TerminologyMarker tmB = new TerminologyMarker(seg,true,2);
        Assert.assertTrue(tm.equals(tmCopy));
        Assert.assertFalse(tm.equals(tmA));
        Assert.assertFalse(tm.equals(tmB));

        tm.setId("id1");
        tmCopy.setId("id2");
        Assert.assertFalse(tm.equals(tmCopy));

        Assert.assertTrue(tm.hashCode() != 0);
        Assert.assertTrue(tmA.hashCode() != 0);

        BCMVisitor vis = new ExtractPlainTextVisitor();
        Assert.assertFalse(tm.accept(vis));
    }
}
