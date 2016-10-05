/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.manipulation;

import com.adobe.internal.io.ByteWriter;
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
import com.adobe.pdfjt.pdf.document.PDFSaveOptions;
import com.adobe.pdfjt.services.pdfa2.PDFA2ConformanceLevel;
import com.adobe.pdfjt.services.pdfa2.PDFA2ConversionOptions;
import com.adobe.pdfjt.services.pdfa2.PDFA2DefaultConversionHandler;
import com.adobe.pdfjt.services.pdfa3.PDFA3Service;

import com.datalogics.pdf.document.FontSetLoader;
import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 */
public class ConvertPdfToPdfA3 {
    private static final Logger LOGGER = Logger.getLogger(ConvertPdfToPdfA3.class.getName());

    public static final String INPUT_PDF_PATH = "UnConvertedPdf.pdf";
    public static final String OUTPUT_PDF_PATH = "ConvertedToPdfA3.pdf";


    /**
     * This is a utility class, and won't be instantiated.
     */
    private ConvertPdfToPdfA3() {}

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
        final PDFA2ConformanceLevel conformanceLevel = PDFA2ConformanceLevel.Level_3u;
        if (args.length == 2) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
            outputUrl = IoUtils.createUrlFromPath(args[1]);
        } else {
            inputUrl = ConvertPdfDocument.class.getResource(INPUT_PDF_PATH);
            outputUrl = IoUtils.createUrlFromPath(OUTPUT_PDF_PATH);
        }

        convertToPdfA3(inputUrl, outputUrl, conformanceLevel);
    }

    /**
     * Converts an input PDF document to the PDF/A-3 standard with the specified conformance level
     *
     * @param inputUrl The URL of the document to be converted
     * @param outputUrl The URL of the converted document
     * @throws IOException an I/O operation failed or was interrupted
     * @throws PDFFontException there was an error in the font set or an individual font
     * @throws PDFInvalidDocumentException a general problem with the PDF document, which may now be in an invalid state
     * @throws PDFIOException there was an error reading or writing a PDF file or temporary caches
     * @throws PDFSecurityException some general security issue occurred during the processing of the request
     * @throws PDFInvalidParameterException one or more of the parameters passed to a method is invalid
     * @throws PDFUnableToCompleteOperationException the operation was unable to be completed
     */
    public static void convertToPdfA3(final URL inputUrl, final URL outputUrl,
                                      final PDFA2ConformanceLevel conformanceLevel)
                                                      throws IOException, PDFFontException,
                                                      PDFInvalidDocumentException, PDFIOException,
                                                      PDFSecurityException, PDFInvalidParameterException,
                                                      PDFUnableToCompleteOperationException {
        ByteWriter writer = null;
        // Attach font set to PDF
        final PDFFontSet pdfaFontSet = FontSetLoader.newInstance().getFontSet();
        final PDFOpenOptions openOptions = PDFOpenOptions.newInstance();
        openOptions.setFontSet(pdfaFontSet);

        final PDFDocument pdfDoc = DocumentUtils.openPdfDocumentWithOptions(inputUrl, openOptions);

        final PDFA2ConversionOptions options = new PDFA2ConversionOptions();
        final PDFA2DefaultConversionHandler handler = new PDFA2DefaultConversionHandler();
        try {
            // Attempt to convert the PDF to the supplied PDF/A3 conformance level
            if (PDFA3Service.convert(pdfDoc, conformanceLevel, options, handler)) {
                final PDFSaveOptions saveOpt = PDFSaveFullOptions.newInstance();

                writer = IoUtils.newByteWriter(outputUrl);
                pdfDoc.save(writer, saveOpt);

                final String successMsg = "\nConverted output written to: " + outputUrl.toString();
                LOGGER.info(successMsg);
            } else {
                LOGGER.info("Errors encountered when converting document.");
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (pdfDoc != null) {
                pdfDoc.close();
            }
        }
    }

}
