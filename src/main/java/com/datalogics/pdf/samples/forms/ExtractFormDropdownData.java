/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.forms;

import com.adobe.pdfjt.core.exceptions.PDFConfigurationException;
import com.adobe.pdfjt.core.exceptions.PDFFontException;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidParameterException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.interactive.forms.PDFField;
import com.adobe.pdfjt.pdf.interactive.forms.PDFFieldChoice;
import com.adobe.pdfjt.pdf.interactive.forms.PDFFieldType;
import com.adobe.pdfjt.pdf.interactive.forms.PDFInteractiveForm;

import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ExtractFormDropdownData {
    private static final Logger LOGGER = Logger.getLogger(ExtractFormDropdownData.class.getName());

    public static final String DEFAULT_INPUT_FILE = "Updated-Acroform.pdf";
    public static URL inputForm = ExtractFormDropdownData.class.getResource(DEFAULT_INPUT_FILE);

    /**
     * @param args
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws PDFInvalidParameterException
     * @throws PDFConfigurationException
     * @throws PDFFontException
     * @throws PDFUnableToCompleteOperationException
     */
    public static void main(final String[] args)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException, IOException,
                    PDFFontException, PDFConfigurationException, PDFInvalidParameterException,
                    PDFUnableToCompleteOperationException {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");

        // If we've been given enough arguments, get the input PDF, the input form data file, and the name of the output
        // file. Try to parse the form data file type.
        if (args.length == 1) {
            inputForm = IoUtils.createUrlFromPath(args[1]);
        }

        // open the form that is the template
        PDFDocument pdfDocument = DocumentUtils.openPdfDocument(inputForm);

        // get the Acroform in order to access the items in the form
        final PDFInteractiveForm form = pdfDocument.getInteractiveForm();
        final Iterator<PDFField> iterator = form.iterator(PDFFieldType.Choice);

        // iterator through all of the dropdown lists in a PDF
        while (iterator.hasNext()) {
            final PDFFieldChoice dropdownField = (PDFFieldChoice) iterator.next();

            // try to print information about the selected value in the dropdown list if it is a string, otherwise just
            // warn in the logger
            final Object selectedObject = dropdownField.getSelectedItem();
            if (selectedObject != null) {
                if (selectedObject instanceof String) {
                    LOGGER.log(Level.INFO, "Selected value in dropdown list " + dropdownField.getQualifiedName()
                                           + " is " + selectedObject);
                } else {
                    LOGGER.log(Level.WARNING, "Selected value in dropdown is not a string");
                }
            }

            // print the list of options to the logger
            final List<String> dropdownOptions = dropdownField.getOptionList();
            if (dropdownOptions == null) {
                LOGGER.log(Level.WARNING, "Dropdown list has no options for " + dropdownField.getQualifiedName());
            } else {
                LOGGER.log(Level.WARNING, "Options for " + dropdownField.getQualifiedName() + " are: ");
                for (final String option : dropdownOptions) {
                    LOGGER.log(Level.INFO, option);
                }
            }
        }

        pdfDocument.close();
        pdfDocument = null;
    }

}
