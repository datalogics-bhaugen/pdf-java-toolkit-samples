/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.images;

import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.page.PDFPage;
import com.adobe.pdfjt.services.rasterizer.PageRasterizer;
import com.adobe.pdfjt.services.rasterizer.RasterizationOptions;

import com.datalogics.pdf.document.FontSetLoader;
import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 *
 */
public class RasterizeDocument {
    private static final Logger LOGGER = Logger.getLogger(RasterizeDocument.class.getName());
    public static final String DEFAULT_INPUT = "pdfjavatoolkit-ds.pdf";

    private static PageRasterizer pageRasterizer;

    /**
     * Main program.
     *
     * @param args command line arguments
     * @throws Exception a general exception was thrown
     */
    public static void main(final String... args) throws Exception {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where
        // PDFJT can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;
        if (args.length > 0) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
        } else {
            inputUrl = RasterizeDocument.class.getResource(DEFAULT_INPUT);
        }

        rasterizePdf(inputUrl);
    }

    /**
     * Print the specified PDF.
     *
     * @param inputUrl path to the PDF to print
     * @throws Exception a general exception was thrown
     */
    public static void rasterizePdf(final URL inputUrl) throws Exception {
        // Only log info messages and above
        LOGGER.setLevel(Level.INFO);

        try {
            // Read the PDF input file and detect the page size of the first page. This sample assumes all pages in
            // the document are the same size.
            final PDFDocument pdfDocument = DocumentUtils.openPdfDocument(inputUrl);
            final PDFPage pdfPage = pdfDocument.requirePages().getPage(0);
            final int pdfPageWidth = (int) pdfPage.getMediaBox().width();
            final int pdfPageHeight = (int) pdfPage.getMediaBox().height();

            // Create a default FontSetLoader. This will include the Base 14 fonts, plus all fonts in the standard
            // system locations.
            final FontSetLoader fontSetLoader = FontSetLoader.newInstance();

            // Create a set of options that will be used to rasterize the pages. We use the page width, height, and the
            // printer resolution to tell the Java Toolkit what dimensions the bitmap should be. Matching the resolution
            // of the printer will give us as high a quality output as the device is capable of.
            final RasterizationOptions rasterizationOptions = new RasterizationOptions();
            rasterizationOptions.setFontSet(fontSetLoader.getFontSet());
            rasterizationOptions.setWidth(pdfPageWidth);
            rasterizationOptions.setHeight(pdfPageHeight);

            // Use a PageRasterizer to create a bitmap for each page. NOTE: Acrobat and Reader will also create bitmaps
            // when normal printing does not produce the desired results.
            pageRasterizer = new PageRasterizer(pdfDocument.requirePages(), rasterizationOptions);

            final BufferedImage page = pageRasterizer.next();

            final File outputfile = new File("saved.png");
            ImageIO.write(page, "png", outputfile);
        } catch (final IOException exp) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(exp.getMessage());
            }
        }
    }
}
