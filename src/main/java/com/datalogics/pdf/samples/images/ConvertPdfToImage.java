/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.images;

import com.adobe.pdfjt.core.exceptions.PDFFontException;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 *
 */
public class ConvertPdfToImage {
    private static final Logger LOGGER = Logger.getLogger(ConvertPdfToImage.class.getName());
    public static final String DEFAULT_INPUT = "ducky.pdf";

    private static PageRasterizer pageRasterizer;

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(final String[] args) throws MalformedURLException {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where
        // PDFJT can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");
        URL inputUrl = null;
        if (args.length > 0) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
        } else {
            inputUrl = ConvertPdfToImage.class.getResource(DEFAULT_INPUT);
        }

        rasterizeToImage(inputUrl);
    }

    public static void rasterizeToImage(final URL inputUrl) {
        // Only log info messages and above
        LOGGER.setLevel(Level.INFO);

        try {
            // Read the PDF input file and detect the page size of the first page. This sample assumes all pages in
            // the document are the same size.
            final PDFDocument pdfDocument = DocumentUtils.openPdfDocument(inputUrl);
            final PDFPage pdfPage = pdfDocument.requirePages().getPage(0);
            final int pdfPageWidth = (int) pdfPage.getMediaBox().width();
            final int pdfPageHeight = (int) pdfPage.getMediaBox().height();

            final int resolution = 300;
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info("Resolution: " + resolution + " DPI");
            }

            // Create a default FontSetLoader. This will include the Base 14 fonts, plus all fonts in the standard
            // system locations.
            final FontSetLoader fontSetLoader = FontSetLoader.newInstance();

            // Create a set of options that will be used to rasterize the pages. We use the page width, height, and the
            // resolution to tell the Java Toolkit what dimensions the bitmap should be.
            final RasterizationOptions rasterizationOptions = new RasterizationOptions();
            rasterizationOptions.setFontSet(fontSetLoader.getFontSet());
            rasterizationOptions.setWidth(pdfPageWidth / 72 * resolution);
            rasterizationOptions.setHeight(pdfPageHeight / 72 * resolution);

            // Use a PageRasterizer to create a bitmap for each page. NOTE: Acrobat and Reader will also create bitmaps
            // when normal printing does not produce the desired results.
            pageRasterizer = new PageRasterizer(pdfDocument.requirePages(), rasterizationOptions);

            int pageNumber = 0;
            BufferedImage page = null;
            while (pageRasterizer.hasNext()) {
                page = pageRasterizer.next();
                final File output = new File("test" + "_dpi" + resolution + "_page" + pageNumber + ".png");
                ImageIO.write(page, "png", output);
                pageNumber++;
            }

            LOGGER.info("Completed rasterizing document");

        } catch (final IOException exp) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(exp.getMessage());
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
        } catch (final PDFFontException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final PDFInvalidParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
