/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.standards.conformance;


import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidXMLException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.services.pdfa.PDFAConformanceLevel;
import com.adobe.pdfjt.services.pdfa.PDFADefaultValidationHandler;
import com.adobe.pdfjt.services.pdfa.PDFAService;
import com.adobe.pdfjt.services.pdfa.PDFAValidationOptions;

import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ValidatePdfA1Conformance {
    private static final Logger LOGGER = Logger.getLogger(ValidatePdfA1Conformance.class.getName());

    /**
     * @param args
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws PDFInvalidParameterException
     * @throws PDFInvalidXMLException
     */
    public static void main(final String[] args)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException, IOException,
                    PDFInvalidXMLException, PDFInvalidParameterException {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;

        if (args.length == 1) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
        } else {
            throw new IllegalArgumentException("Must provide an PDF to validate");
        }

        validatePdfA1Conformance(inputUrl);
    }

    /**
     * @param inputUrl
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws PDFInvalidParameterException
     * @throws PDFInvalidXMLException
     */
    private static void validatePdfA1Conformance(final URL inputUrl) throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException, IOException, PDFInvalidXMLException, PDFInvalidParameterException {
        final PDFDocument document = DocumentUtils.openPdfDocument(inputUrl);

        final PDFAValidationOptions validationOptions = new PDFAValidationOptions();

        if (validationOptions.checkDocID()) {
            LOGGER.log(Level.INFO, "PDF/A Validation will check the DocumentID");
        }

        if (validationOptions.checkDocVersion()) {
            LOGGER.log(Level.INFO, "PDF/A Validation will check the Document Version");
        }

        if (validationOptions.detectCertifiedDocumentEnabled()) {
            LOGGER.log(Level.INFO, "PDF/A Validation will detect certifying signatures");
        }

        if (validationOptions.detectXFAEnabled()) {
            LOGGER.log(Level.INFO, "PDF/A Validation will detect XFA content");
        }

        if (validationOptions.validateUnusedResourcesEnabled()) {
            LOGGER.log(Level.INFO, "PDF/A Validation will validate resources that are not referenced in the document");
        }

        final boolean pdfConformsToPdfA = PDFAService.validate(document, PDFAConformanceLevel.Level_1b,
                                                               validationOptions,
                             new PDFADefaultValidationHandler());

        if (pdfConformsToPdfA) {
            LOGGER.log(Level.INFO, "Document conforms to PDF/A");
        } else {
            LOGGER.log(Level.INFO, "Document does not conform to PDF/A");
        }
    }
}
