/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.stress;

import java.io.File;

/**
 *
 */
public class Stress {

    static String directoryToSearch = null;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // must have just the one argument of a directory to search through
        if (args.length != 1) {
            // Solution taken from Stackoverflow : http://stackoverflow.com/a/7603444/924
            directoryToSearch = System.getProperty("user.dir");
            System.out.println("Using " + directoryToSearch + " as the directory to search for PDFs to evaluate");
        } else if (args.length == 1) {
            directoryToSearch = args[0];
        }

        try {
            final File directoryToEvaluateFormTypes = new File(directoryToSearch);
            // check if the path exists and if it is a directory
            if (directoryToEvaluateFormTypes.exists() && directoryToEvaluateFormTypes.isDirectory()) {
                searchRecursivelyForPDF(directoryToEvaluateFormTypes);
            }
        } catch (final OutOfMemoryError e) {
            System.out.println("Too many files to look through, specify a directory with fewer files/directories");
        }
    }

    /**
     * @param directoryToEvaluateFormTypes
     */
    public static void searchRecursivelyForPDF(final File directoryToEvaluateFormTypes) {
        final File[] filesInDirectory = directoryToEvaluateFormTypes.listFiles();
        if (filesInDirectory != null && filesInDirectory.length > 0) {
            for (final File f : filesInDirectory) {
                if (f.exists()) {
                    // if it is a directory, search again
                    if (f.isDirectory()) {
                        searchRecursivelyForPDF(f);
                    } else {
                        // if it is a file, then we only want to look at it if it is a PDF
                        if (f.toURI().toString().toLowerCase().endsWith(".pdf")) {
                            try {

                                // insert sample to run here
                                final String[] args = new String[] { f.getAbsolutePath() };

                            } catch (final Exception e) {
                            }
                        }
                    }
                }
            }
        }
    }
}
