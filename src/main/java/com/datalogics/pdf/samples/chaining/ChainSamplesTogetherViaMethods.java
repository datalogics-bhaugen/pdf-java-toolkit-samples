/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.chaining;

import com.datalogics.pdf.samples.forms.FillForm;
import com.datalogics.pdf.samples.manipulation.RemoveInteractivity;
import com.datalogics.pdf.samples.signature.SignDocument;
import com.datalogics.pdf.samples.util.IoUtils;

import java.net.MalformedURLException;

/**
 * This sample demonstrates how to chain other samples together to build a larger workflow by calling methods defined in
 * multiple samples.
 *
 * This sample
 * - fills in a PDF form
 * - signs an existing signature field in the PDF
 * - removes all interactivity from the PDF (flattens the form)
 * saving a copy of the PDF at each step of the process.
 */
public class ChainSamplesTogetherViaMethods {
    public static final String UNFILLED_PDF_FORM = "src/main/resources/com/datalogics/pdf/samples/forms/acroform_fdf.pdf";
    public static final String FILLED_PDF_FORM = "acroform_fdf_filled.pdf";
    public static final String SIGNED_AND_FILLED_PDF_FORM = "acroform_fdf_filled_signed.pdf";
    public static final String FLATTENED_SIGNED_AND_FILLED_PDF_FORM = "acroform_fdf_filled_signed_removed_interactivity.pdf";
    public static final String PDF_FORM_DATA = "src/main/resources/com/datalogics/pdf/samples/forms/acroform_fdf.fdf";

    /**
     * @param args
     * @throws Exception
     * @throws MalformedURLException
     */
    public static void main(final String[] args) throws MalformedURLException, Exception {
        FillForm.fillPdfForm(IoUtils.createUrlFromPath(UNFILLED_PDF_FORM), IoUtils.createUrlFromPath(PDF_FORM_DATA),
                             FillForm.FDF_FORMAT, IoUtils.createUrlFromPath(FILLED_PDF_FORM));
        SignDocument.signExistingSignatureFields(IoUtils.createUrlFromPath(FILLED_PDF_FORM),
                                                 IoUtils.createUrlFromPath(SIGNED_AND_FILLED_PDF_FORM));
        RemoveInteractivity.removeInteractivity(IoUtils.createUrlFromPath(SIGNED_AND_FILLED_PDF_FORM),
                                                IoUtils.createUrlFromPath(FLATTENED_SIGNED_AND_FILLED_PDF_FORM));
    }

}
