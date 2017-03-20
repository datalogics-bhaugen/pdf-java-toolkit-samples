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
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.interactive.forms.PDFFieldChoice;
import com.adobe.pdfjt.pdf.interactive.forms.PDFFieldList;
import com.adobe.pdfjt.pdf.interactive.forms.PDFFieldText;
import com.adobe.pdfjt.pdf.interactive.forms.PDFInteractiveForm;
import com.adobe.pdfjt.services.ap.AppearanceService;

import com.datalogics.pdf.document.DocumentHelper;
import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PopulateDropdown {

    public static final String DEFAULT_INPUT_FILE = "Empty-Acroform.pdf";
    public static final String DEFAULT_OUTPUT_FILE = "Updated-Acroform.pdf";
    public static URL inputForm = PopulateDropdown.class.getResource(DEFAULT_INPUT_FILE);

    /**
     * @param args
     * @throws IOException
     * @throws PDFSecurityException
     * @throws PDFIOException
     * @throws PDFInvalidDocumentException
     * @throws PDFInvalidParameterException
     * @throws PDFConfigurationException
     * @throws PDFFontException
     */
    public static void main(final String[] args)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException, IOException,
                    PDFFontException, PDFConfigurationException, PDFInvalidParameterException {
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
        final PDFDocument pdfDocument = DocumentUtils.openPdfDocument(inputForm);

        // get the Acroform in order to access the items in the form
        final PDFInteractiveForm form = pdfDocument.getInteractiveForm();
        final PDFFieldList formElements = form.getChildren();

        // get the first text field of the Acroform by name
        final PDFFieldText textField = (PDFFieldText) formElements.getFieldNamed("Label1");
        textField.setStringValue("Fruit");
        textField.setReadOnly(true);

        // get the first dropdown list of the Acroform by Name
        final PDFFieldChoice dropdownField = (PDFFieldChoice) formElements.getFieldNamed("Dropdown1");

        // get the current dropdown list
        List dropdownOptions = dropdownField.getOptionList();
        if (dropdownOptions == null) {
            // create a new list of options if none exist
            dropdownOptions = new ArrayList();
        }

        // add a few options
        dropdownOptions.add("Apple");
        dropdownOptions.add("Peach");
        dropdownOptions.add("Grapefruit");

        // set the option list to the list we just created
        dropdownField.setOptionList(dropdownOptions);

        // generate appearances for the entire document so that the form fields have appearances generated
        AppearanceService.generateAppearances(pdfDocument, null, null);

        DocumentHelper.saveFullAndClose(pdfDocument, DEFAULT_OUTPUT_FILE);
    }

}
