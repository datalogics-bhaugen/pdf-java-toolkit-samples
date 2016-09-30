/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.save;

import com.adobe.internal.io.ByteWriter;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFSaveLinearOptions;

import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class LinearizeDocument {
    final static String INPUT_PDF = "pdfjavatoolkit-ds.pdf";
    final static String OUTPUT_PDF = "Linearized.pdf";

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(final String[] args) throws MalformedURLException {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;
        URL outputUrl = null;
        if (args.length == 2) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
            outputUrl = IoUtils.createUrlFromPath(args[1]);
        } else {
            inputUrl = LinearizeDocument.class.getResource(INPUT_PDF);
            outputUrl = IoUtils.createUrlFromPath(OUTPUT_PDF);
        }

        linearizeDocument(inputUrl, outputUrl);
    }

    /**
     * @param inputUrl
     * @param outputUrl
     */
    private static void linearizeDocument(final URL inputUrl, final URL outputUrl) {
        PDFDocument pdfDoc = null;
        ByteWriter writer = null;

        try {
            pdfDoc = DocumentUtils.openPdfDocument(inputUrl);

            writer = IoUtils.newByteWriter(outputUrl);

            final PDFSaveLinearOptions options = PDFSaveLinearOptions.newInstance();

            pdfDoc.saveAndClose(writer, options);
        } catch (final Exception e) {

        }
    }
}
