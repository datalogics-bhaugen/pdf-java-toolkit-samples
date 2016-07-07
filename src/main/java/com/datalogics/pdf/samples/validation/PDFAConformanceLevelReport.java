/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.validation;

import com.adobe.internal.io.RandomAccessFileByteReader;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidXMLException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFOpenOptions;
import com.adobe.pdfjt.services.pdfa.PDFAConformanceLevel;
import com.adobe.pdfjt.services.pdfa.PDFADefaultValidationHandler;
import com.adobe.pdfjt.services.pdfa.PDFAService;
import com.adobe.pdfjt.services.pdfa.PDFAValidationOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 */
public class PDFAConformanceLevelReport {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final File f = new File(args[0]);
        RandomAccessFile inputStream = null;
        RandomAccessFileByteReader byteReader = null;
        PDFDocument document = null;
        try {
            inputStream = new RandomAccessFile(f, "r");
            byteReader = new RandomAccessFileByteReader(inputStream);
            document = PDFDocument.newInstance(byteReader, PDFOpenOptions.newInstance());

            final String version = PDFAService.getVersion(document);
            final String conformanceLevel = PDFAService.getConformanceLevel(document).toLowerCase();

            PDFAService.validate(document, PDFAConformanceLevel.Level_1a, new PDFAValidationOptions(),
                                 new PDFADefaultValidationHandler());
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
        } catch (final PDFInvalidXMLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFInvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (final PDFInvalidDocumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final PDFIOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final PDFSecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (final PDFUnableToCompleteOperationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    document = null;
                }
            }

            if (byteReader != null) {
                try {
                    byteReader.close();
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    byteReader = null;
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                inputStream = null;
            }
        }
    }

}
