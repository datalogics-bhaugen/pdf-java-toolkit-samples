/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.extraction;

import com.adobe.pdfjt.core.exceptions.PDFFontException;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.core.fontset.PDFFontSet;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.services.readingorder.LayoutModeTextExtractor;
import com.adobe.pdfjt.services.readingorder.ReadingOrderTextExtractor;
import com.adobe.pdfjt.services.textextraction.Word;
import com.adobe.pdfjt.services.textextraction.WordsIterator;

import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.FontUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This sample demonstrates how to extract text from a document. The text is extracted in reading order and then saved
 * to a text file.
 */
public final class TextExtract {
    private static final Logger LOGGER = Logger.getLogger(TextExtract.class.getName());

    public static final String INPUT_PDF_PATH = "/com/datalogics/pdf/samples/pdfjavatoolkit-ds.pdf";
    public static final String OUTPUT_READING_ORDER_TEXT_PATH = "ReadingOrderTextExtract.txt";
    public static final String OUTPUT_LAYOUT_MODE_TEXT_PATH = "LayoutModeTextExtract.txt";

    /**
     * This is a utility class, and won't be instantiated.
     */
    private TextExtract() {}

    /**
     * Main method.
     *
     * @param args two command line arguments - input path and output path
     * @throws Exception a general exception was thrown
     */
    public static void main(final String... args) throws Exception {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;
        URL readingOrderOutputUrl = null;
        URL layoutModeOutputUrl = null;

        if (args.length > 2) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
            readingOrderOutputUrl = IoUtils.createUrlFromPath(args[1]);
            layoutModeOutputUrl = IoUtils.createUrlFromPath(args[2]);
        } else {
            inputUrl = TextExtract.class.getResource(INPUT_PDF_PATH);
            readingOrderOutputUrl = IoUtils.createUrlFromPath(OUTPUT_READING_ORDER_TEXT_PATH);
            layoutModeOutputUrl = IoUtils.createUrlFromPath(OUTPUT_LAYOUT_MODE_TEXT_PATH);
        }

        extractTextReadingOrder(inputUrl, readingOrderOutputUrl);
        extractTextLayoutOrder(inputUrl, layoutModeOutputUrl);
    }

    /**
     * Extracts the text from a PDF file in reading order.
     *
     * @param inputUrl An URL for the input document, to extract text from
     * @param outputUrl An URL for the file stream where the extracted text will be written
     * @throws PDFInvalidDocumentException a general problem with the PDF document, which may now be in an invalid state
     * @throws PDFIOException there was an error reading or writing a PDF file or temporary caches
     * @throws PDFFontException there was an error in the font set or an individual font
     * @throws PDFSecurityException some general security issue occurred during the processing of the request
     * @throws UnsupportedEncodingException the character encoding is not supported
     * @throws IOException an I/O operation failed or was interrupted
     * @throws PDFUnableToCompleteOperationException the operation was unable to be completed
     * @throws URISyntaxException the input URL could not be converted to a string
     */
    public static void extractTextReadingOrder(final URL inputUrl, final URL outputUrl)
                    throws PDFInvalidDocumentException, PDFIOException, PDFFontException, PDFSecurityException,
                    UnsupportedEncodingException, IOException, PDFUnableToCompleteOperationException,
                    URISyntaxException {
        PDFDocument document = null;
        try {
            document = DocumentUtils.openPdfDocument(inputUrl);

            final PDFFontSet docFontSet = FontUtils.getDocFontSet(document);
            final ReadingOrderTextExtractor extractor = ReadingOrderTextExtractor.newInstance(document, docFontSet);
            final WordsIterator wordsIter = extractor.getWordsIterator();

            if (wordsIter.hasNext()) {
                try (final FileOutputStream outputStream = obtainOutputStream(outputUrl)) {
                    do {
                        final Word word = wordsIter.next();
                        outputStream.write(word.toString().getBytes("UTF-8"));
                    } while (wordsIter.hasNext());
                }
            } else {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info(inputUrl.toURI().getPath() + " did not have any text to extract.");
                }
            }

        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * Extracts the text from a PDF file in layout order.
     *
     * @param inputUrl An URL for the input document, to extract text from
     * @param outputUrl An URL for the file stream where the extracted text will be written
     * @throws PDFInvalidDocumentException a general problem with the PDF document, which may now be in an invalid state
     * @throws PDFIOException there was an error reading or writing a PDF file or temporary caches
     * @throws PDFFontException there was an error in the font set or an individual font
     * @throws PDFSecurityException some general security issue occurred during the processing of the request
     * @throws UnsupportedEncodingException the character encoding is not supported
     * @throws IOException an I/O operation failed or was interrupted
     * @throws PDFUnableToCompleteOperationException the operation was unable to be completed
     * @throws URISyntaxException the input URL could not be converted to a string
     */
    public static void extractTextLayoutOrder(final URL inputUrl, final URL outputUrl)
                    throws PDFInvalidDocumentException, PDFIOException, PDFFontException, PDFSecurityException,
                    UnsupportedEncodingException, IOException, PDFUnableToCompleteOperationException,
                    URISyntaxException {
        PDFDocument document = null;
        try {
            document = DocumentUtils.openPdfDocument(inputUrl);

            final PDFFontSet docFontSet = FontUtils.getDocFontSet(document);
            final LayoutModeTextExtractor extractor = LayoutModeTextExtractor.newInstance(document, docFontSet);
            final WordsIterator wordsIter = extractor.getWordsIterator();

            if (wordsIter.hasNext()) {
                try (final FileOutputStream outputStream = obtainOutputStream(outputUrl)) {
                    do {
                        final Word word = wordsIter.next();
                        outputStream.write(word.toString().getBytes("UTF-8"));
                    } while (wordsIter.hasNext());
                }
            } else {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.info(inputUrl.toURI().getPath() + " did not have any text to extract.");
                }
            }

        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    private static FileOutputStream obtainOutputStream(final URL outputUrl) throws PDFIOException, IOException {
        File outputFile = null;
        try {
            outputFile = new File(outputUrl.toURI());
        } catch (final URISyntaxException e) {
            throw new PDFIOException(e);
        }

        if (outputFile.exists()) {
            Files.delete(outputFile.toPath());
        }
        return new FileOutputStream(outputFile);
    }
}
