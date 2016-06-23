/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.creation;

import com.adobe.internal.io.RandomAccessFileByteWriter;
import com.adobe.pdfjt.core.exceptions.PDFConfigurationException;
import com.adobe.pdfjt.core.exceptions.PDFFontException;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.core.types.ASRectangle;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFOpenOptions;
import com.adobe.pdfjt.pdf.document.PDFSaveFullOptions;
import com.adobe.pdfjt.pdf.graphics.PDFRectangle;
import com.adobe.pdfjt.pdf.interactive.annotation.PDFAnnotationList;
import com.adobe.pdfjt.pdf.interactive.annotation.PDFAnnotationWidget;
import com.adobe.pdfjt.pdf.interactive.forms.PDFFieldText;
import com.adobe.pdfjt.pdf.interactive.forms.PDFInteractiveForm;
import com.adobe.pdfjt.pdf.page.PDFPage;
import com.adobe.pdfjt.pdf.page.PDFPageTree;
import com.adobe.pdfjt.services.ap.AppearanceService;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * This sample demonstrates how to create a "simple" AcroForm.
 */
public class MakeSimpleAcroForm {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            final PDFDocument document = PDFDocument.newInstance(PDFOpenOptions.newInstance());

            final PDFPageTree pageTree = PDFPageTree.newInstance(document, PDFPage.newInstance(document,
                                                                                               PDFRectangle.newInstance(document,
                                                                                                                        ASRectangle.US_LEGAL)));

            final PDFInteractiveForm form = PDFInteractiveForm.newInstance(document, null);

            final PDFAnnotationWidget firstNameWidget = PDFAnnotationWidget.newInstance(pageTree.getPage(0),
                                                                                        PDFRectangle.newInstance(document,
                                                                                                                 new ASRectangle(100,
                                                                                                                                 100,
                                                                                                                                 100,
                                                                                                                                 100)));
            final PDFFieldText firstNameNode = PDFFieldText.newInstance("First Name", pageTree.getPage(0),
                                                                        PDFRectangle.newInstance(document,
                                                                                                 new ASRectangle(100,
                                                                                                                 100,
                                                                                                                 100,
                                                                                                                 100)),
                                                                        null,
                                                                        false);
            firstNameWidget.setParentField(firstNameNode);
            final PDFAnnotationList firstNameNodeAnnotationList = PDFAnnotationList.newInstance(document);
            firstNameNodeAnnotationList.add(firstNameWidget);
            firstNameNode.setAnnotations(firstNameNodeAnnotationList);

            form.addChild(firstNameNode);

            document.setInteractiveForm(form);

            AppearanceService.generateAppearances(document, null, null);

            final RandomAccessFile file = new RandomAccessFile("/Users/bhaugen/Desktop/acroform.pdf", "rw");
            final RandomAccessFileByteWriter byteWriter = new RandomAccessFileByteWriter(file);
            document.saveAndClose(byteWriter, PDFSaveFullOptions.newInstance());
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFInvalidDocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFInvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFUnableToCompleteOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFFontException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
