/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.forms;

import com.adobe.internal.io.InputStreamByteReader;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFDocument.PDFDocumentType;
import com.adobe.pdfjt.pdf.document.PDFOpenOptions;
import com.adobe.pdfjt.services.xfa.XFAService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * This sample demonstrates how to detect the type of form that is in a PDF. It will recursively search through the path
 * supplied as an argument, defaulting to the directory where the sample is run from, for all files that are of type
 * 'PDF' and count the number of each type of form that it finds.
 */
public class FormTypeDetector {

    static HashMap<String, Integer> formTypes = new HashMap<String, Integer>();
    static int numberOfProblemDocuments = 0;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // must have just the one argument of a directory to search through
        if (args.length != 1) {

        } else {
            final File directoryToEvaluateFormTypes = new File(args[0]);
            // check if the path exists and if it is a directory
            if (directoryToEvaluateFormTypes.exists() && directoryToEvaluateFormTypes.isDirectory()) {
                // build our hashmap to store the number of different form types we find
                for (final PDFDocumentType c : PDFDocumentType.values()) {
                    formTypes.put(c.toString(), 0);
                }

                searchRecursivelyForPDF(directoryToEvaluateFormTypes);

                printStatisticsOfFiles();
            }
        }
    }

    /**
     *
     */
    private static void printStatisticsOfFiles() {
        System.out.println("File detection complete!");

        final Set<String> keys = formTypes.keySet();
        for (final String s : keys) {
            System.out.println(s + " : " + formTypes.get(s));
        }

        if (numberOfProblemDocuments != 0) {
            System.out.println("Could not determine form type for " + numberOfProblemDocuments + " documents");
        }
    }

    /**
     * @param directoryToEvaluateFormTypes
     */
    public static void searchRecursivelyForPDF(final File directoryToEvaluateFormTypes) {
        for (final File f : directoryToEvaluateFormTypes.listFiles()) {
            if (f.exists()) {
                // if it is a directory, search again
                if (f.isDirectory()) {
                    searchRecursivelyForPDF(f);
                } else {
                    // if it is a file, then we only want to look at it if it is a PDF
                    if (f.toURI().toString().toLowerCase().endsWith(".pdf")) {
                        FileInputStream inputStream = null;
                        InputStreamByteReader byteReader = null;
                        PDFDocument document = null;
                        try {
                            inputStream = new FileInputStream(f);
                            byteReader = new InputStreamByteReader(inputStream);
                            document = PDFDocument.newInstance(byteReader, PDFOpenOptions.newInstance());
                            final PDFDocumentType documentType = XFAService.getDocumentType(document);
                            if (documentType != null) {
                                final int count = formTypes.containsKey(documentType.toString())
                                                ? formTypes.get(documentType.toString()) : 0;
                                formTypes.put(documentType.toString(), count + 1);

                                switch (documentType) {
                                    case Acroform:
                                        break;
                                    case DynamicNonShellXFA:
                                        break;
                                    case DynamicShellXFA:
                                        break;
                                    case Flat:
                                        break;
                                    case StaticNonShellXFA:
                                        break;
                                    case StaticShellXFA:
                                        break;
                                }
                            }
                        } catch (final FileNotFoundException e) {
                            // We should never get a file not found exception because we are checking for existence
                            // before doing anything with the file
                        } catch (final IOException e) {
                            // Swallow the exception as there is nothing for the user to do
                            numberOfProblemDocuments += 1;
                        } catch (final PDFInvalidDocumentException e) {
                            // Swallow the exception as there is nothing for the user to do
                            numberOfProblemDocuments += 1;
                        } catch (final PDFIOException e) {
                            // Swallow the exception as there is nothing for the user to do
                            numberOfProblemDocuments += 1;
                        } catch (final PDFSecurityException e) {
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
            }
        }
    }
}
