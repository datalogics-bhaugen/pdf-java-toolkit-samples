/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.manipulation;

import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.core.types.ASRectangle;
import com.adobe.pdfjt.pdf.document.PDFCatalog;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFSaveFullOptions;
import com.adobe.pdfjt.pdf.graphics.PDFRectangle;
import com.adobe.pdfjt.pdf.page.PDFPage;

import com.datalogics.pdf.document.DocumentHelper;
import com.datalogics.pdf.samples.extraction.TextExtract;
import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * This sample demonstrates how to adjust the cropbox of an existing document.
 */
public class AdjustCropbox {

    public static final String INPUT_PDF_PATH = "/com/datalogics/pdf/samples/manipulation/pdfjavatoolkit-ds-no-cropbox.pdf";
    public static final String OUTPUT_PDF_PATH = "pdfjavatoolkit-ds-cropbox.pdf";

    /**
     * @param args
     * @throws IOException
     * @throws PDFUnableToCompleteOperationException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws URISyntaxException
     */
    public static void main(final String[] args) throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException, PDFUnableToCompleteOperationException, IOException, URISyntaxException {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;
        URL outputUrl = null;

        if (args.length > 1) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
            outputUrl = IoUtils.createUrlFromPath(args[1]);
        } else {
            inputUrl = TextExtract.class.getResource(INPUT_PDF_PATH);
            outputUrl = IoUtils.createUrlFromPath(OUTPUT_PDF_PATH);
        }

        setCropbox(inputUrl, outputUrl);
    }

    public static void setCropbox(final URL inputUrl, final URL outputUrl) throws PDFInvalidDocumentException,
                    PDFIOException, PDFSecurityException, IOException, PDFUnableToCompleteOperationException,
                    URISyntaxException {
        PDFDocument document = null;
        try {
            document = DocumentUtils.openPdfDocument(inputUrl);

            final PDFCatalog catalog = document.requireCatalog();
            if (catalog != null) {
                final int numberOfPages = catalog.getPages().getCount();
                for (int i = 0; i < numberOfPages; i++) {
                    final PDFPage page = catalog.getPages().getPage(i);
                    page.setCropBox(PDFRectangle.newInstance(document, ASRectangle.A4));
                }
            }

            DocumentHelper.saveAndClose(document, outputUrl.toURI().getPath(), PDFSaveFullOptions.newInstance());;
        } finally {
            if (document != null) {
                document.close();
                document = null;
            }
        }
    }
}
