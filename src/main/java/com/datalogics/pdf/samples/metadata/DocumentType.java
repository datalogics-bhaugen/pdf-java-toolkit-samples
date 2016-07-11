/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.metadata;

import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFDocument.PDFDocumentType;

import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * This sample writes the PDF document type out to the logger.
 */
public class DocumentType {
    private static final Logger LOGGER = Logger.getLogger(DocumentType.class.getName());
    public static final String INPUT_PDF_PATH = "/com/datalogics/pdf/samples/pdfjavatoolkit-ds.pdf";

    /**
     * @param args
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     */
    public static void main(final String[] args)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException, IOException {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");

        URL inputUrl = null;
        if (args.length > 0) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
        } else {
            inputUrl = DocumentType.class.getResource(INPUT_PDF_PATH);
        }

        final PDFDocument document = DocumentUtils.openPdfDocument(inputUrl);

        final PDFDocumentType documentType = document.getPDFDocumentType();

        if (documentType != null) {
            LOGGER.info(documentType.toString());
        } else {
            LOGGER.severe("The specified input PDF had no detectable type!");
        }
    }
}
