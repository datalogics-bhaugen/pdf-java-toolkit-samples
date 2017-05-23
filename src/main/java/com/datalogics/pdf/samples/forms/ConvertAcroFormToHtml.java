/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.forms;

import com.adobe.pdfjt.core.license.LicenseManager;

import com.datalogics.pdf.samples.util.IoUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * This sample demonstrates how to create an HTML form by inspecting an AcroForm in a PDF.
 */
public class ConvertAcroFormToHtml {
    public static final String DEFAULT_INPUT = "acroform_fdf.pdf";
    public static final String HTML_OUTPUT = "acroform.html";

    private static final Logger LOGGER = Logger.getLogger(ConvertAcroFormToHtml.class.getName());

    /**
     * This is a utility class, and won't be instantiated.
     */
    private ConvertAcroFormToHtml() {}

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
        if (args.length > 0) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
            outputUrl = IoUtils.createUrlFromPath(args[1]);
        } else {
            inputUrl = ExportFormDataToCsv.class.getResource(DEFAULT_INPUT);
            outputUrl = IoUtils.createUrlFromPath(HTML_OUTPUT);
        }

        createHtmlForm(inputUrl, outputUrl);
    }

    /**
     * Create an HTML form from the AcroForm found in the PDF specified by inputUrl.
     *
     * @param inputUrl
     * @param outputUrl
     */
    public static void createHtmlForm(final URL inputUrl, final URL outputUrl) {
    }
}
