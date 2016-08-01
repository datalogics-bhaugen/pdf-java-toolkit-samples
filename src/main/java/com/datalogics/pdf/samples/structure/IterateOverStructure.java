/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.structure;

import com.adobe.pdfjt.core.cos.CosStream;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFContents;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.interchange.structure.PDFStructureElement;
import com.adobe.pdfjt.services.interchange.structure.StructureFinder;
import com.adobe.pdfjt.services.interchange.structure.StructureFinder.Entry;
import com.adobe.pdfjt.services.interchange.structure.StructureFinder.FinderIterator;

import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 */
public class IterateOverStructure {
    private static final Logger LOGGER = Logger.getLogger(AddStructureAndTags.class.getName());

    public static final String INPUT_PDF_PATH = "Example-acrobat-tagged.pdf";
    public static final String OUTPUT_PDF_PATH = "StructureAndTags.pdf";

    /**
     * @param args
     * @throws MalformedURLException
     * @throws PDFUnableToCompleteOperationException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws URISyntaxException
     */
    public static void main(final String[] args) throws MalformedURLException, PDFInvalidDocumentException,
                    PDFIOException, PDFSecurityException, PDFUnableToCompleteOperationException, URISyntaxException {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;
        final URL outputUrl = null;

        if (args.length == 1) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
        } else {
            inputUrl = IterateOverStructure.class.getResource(INPUT_PDF_PATH);
        }

        addStructureAndTags(inputUrl, outputUrl);
    }

    /**
     * @param inputUrl
     * @throws PDFUnableToCompleteOperationException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws URISyntaxException
     */
    private static void addStructureAndTags(final URL inputUrl, final URL outputUrl)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException,
                    PDFUnableToCompleteOperationException, URISyntaxException {
        PDFDocument document = null;

        try {
            document = DocumentUtils.openPdfDocument(inputUrl);

            final StructureFinder structureFinder = StructureFinder.newInstance(document);
            final FinderIterator finderIterator = structureFinder.getIterator();

            while (finderIterator.hasNext()) {
                final Entry entry = finderIterator.next();
                final PDFStructureElement element = entry.getElement();
                PDFContents content = entry.getContents();
                final CosStream contentStream = content.getCosStream();
                content = null;
            }
        } catch (final PDFInvalidDocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFIOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
                document = null;
            }
        }
    }
}
