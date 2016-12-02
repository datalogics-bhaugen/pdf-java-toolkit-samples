/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.chaining;

import com.datalogics.pdf.samples.creation.MakeWhiteFangBook;
import com.datalogics.pdf.samples.extraction.TextExtract;

/**
 * This sample demonstrates the concept of chaining multiple samples together to build a larger workflow. First this
 * sample will run the MakeWhiteFangBook sample and then it will run the TextExtract sample to write the text out to a
 * text file.
 */
public class ChainSamplesTogether {

    public static final String WHITE_FANG_BOOK_PDF = "WhiteFangBook.pdf";
    public static final String WHITE_FANG_BOOK_TXT = "WhiteFangBook.txt";

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        MakeWhiteFangBook.main(WHITE_FANG_BOOK_PDF);
        TextExtract.main(WHITE_FANG_BOOK_PDF, WHITE_FANG_BOOK_TXT);
    }

}
