/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.optimization;

import com.adobe.pdfjt.core.exceptions.PDFFontException;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.core.fontset.PDFFontSet;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFOpenOptions;
import com.adobe.pdfjt.pdf.document.PDFSaveFullOptions;
import com.adobe.pdfjt.services.optimizer.OptimizerService;

import com.datalogics.pdf.document.FontSetLoader;
import com.datalogics.pdf.samples.manipulation.ConvertPdfDocument;
import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 */
public class OptimizeDocument {

    private static final Logger LOGGER = Logger.getLogger(ConvertPdfDocument.class.getName());

    public static final String INPUT_UNCONVERTED_PDF_PATH = "UnConvertedPdf.pdf";
    public static final String OUTPUT_CONVERTED_PDF_PATH = "ConvertedPdfa-1b.pdf";

    /**
     * Main program.
     *
     * @param args command line arguments
     * @throws Exception a general exception was thrown
     */
    public static void main(final String... args) throws Exception {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;
        URL outputUrl = null;
        if (args.length > 0) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
            outputUrl = IoUtils.createUrlFromPath(args[1]);
        } else {
            outputUrl = IoUtils.createUrlFromPath(OUTPUT_CONVERTED_PDF_PATH);
        }

        optimizeDocument(inputUrl, outputUrl);

        System.out.println("Done!");
    }

    /**
     * @param inputUrl
     * @param outputUrl
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws PDFFontException
     * @throws PDFUnableToCompleteOperationException
     * @throws PDFInvalidParameterException
     */
    private static void optimizeDocument(final URL inputUrl, final URL outputUrl)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException, IOException,
                    PDFFontException, PDFInvalidParameterException, PDFUnableToCompleteOperationException {
        final PDFFontSet pdfaFontSet = FontSetLoader.newInstance().getFontSet();
        final PDFOpenOptions openOptions = PDFOpenOptions.newInstance();
        openOptions.setFontSet(pdfaFontSet);

        final PDFDocument pdfDoc = DocumentUtils.openPdfDocumentWithOptions(inputUrl, openOptions);

        OptimizerService.optimzeEmbeddedFonts(pdfDoc, pdfaFontSet);

        pdfDoc.saveAndClose(IoUtils.newByteWriter(outputUrl), PDFSaveFullOptions.newInstance());
    }

}
