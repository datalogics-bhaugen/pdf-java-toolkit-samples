/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.metadata;

import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.services.auditor.PDFAuditor;

import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This sample uses the PDFAuditor to determine how much space in a PDF is used for specific categories of objects in
 * the PDF.
 */
public class AuditResourceSize {
    private static final Logger LOGGER = Logger.getLogger(AuditResourceSize.class.getName());
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
            inputUrl = AuditResourceSize.class.getResource(INPUT_PDF_PATH);
        }

        final HashMap<String, Long> resourceUsage = auditResoures(inputUrl);

        logResourceUsage(resourceUsage);
    }

    /**
     * @param inputUrl
     * @return
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     */
    private static HashMap<String, Long> auditResoures(final URL inputUrl)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException, IOException {
        final PDFDocument document = DocumentUtils.openPdfDocument(inputUrl);

        final PDFAuditor auditor = new PDFAuditor();
        return auditor.auditPDF(document);
    }

    /**
     * @param resourceUsage
     */
    private static void logResourceUsage(final HashMap<String, Long> resourceUsage) {
        for (final String s : resourceUsage.keySet()) {
            LOGGER.info(s + " " + resourceUsage.get(s));
        }
    }
}
