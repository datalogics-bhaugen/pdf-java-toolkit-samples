/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.structure;

import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.exceptions.PDFInvalidDocumentException;
import com.adobe.pdfjt.core.exceptions.PDFSecurityException;
import com.adobe.pdfjt.core.exceptions.PDFUnableToCompleteOperationException;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.core.types.ASArray;
import com.adobe.pdfjt.core.types.ASDictionary;
import com.adobe.pdfjt.core.types.ASName;
import com.adobe.pdfjt.core.types.ASString;
import com.adobe.pdfjt.pdf.content.Content;
import com.adobe.pdfjt.pdf.content.Instruction;
import com.adobe.pdfjt.pdf.content.InstructionFactory;
import com.adobe.pdfjt.pdf.content.StatefulOperatorHandler;
import com.adobe.pdfjt.pdf.content.processor.StatefulContentStreamProcessor;
import com.adobe.pdfjt.pdf.contentmodify.ContentWriter;
import com.adobe.pdfjt.pdf.contentmodify.ModifiableContent;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFSaveFullOptions;
import com.adobe.pdfjt.pdf.interchange.structure.PDFStructureMCID;
import com.adobe.pdfjt.pdf.interchange.structure.PDFStructureRoot;
import com.adobe.pdfjt.pdf.page.PDFPage;

import com.datalogics.pdf.document.DocumentHelper;
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
public class AddStructureAndTags {
    private static final Logger LOGGER = Logger.getLogger(AddStructureAndTags.class.getName());

    public static final String INPUT_PDF_PATH = "Example.pdf";
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
        URL outputUrl = null;

        if (args.length > 1) {
            inputUrl = IoUtils.createUrlFromPath(args[0]);
            outputUrl = IoUtils.createUrlFromPath(args[1]);
        } else {
            inputUrl = AddStructureAndTags.class.getResource(INPUT_PDF_PATH);
            outputUrl = IoUtils.createUrlFromPath(OUTPUT_PDF_PATH);
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

            // create the structure root if one does not exist
            PDFStructureRoot structureRoot = null;
            if (document.requireCatalog().hasStructureRoot()) {
                structureRoot = document.requireCatalog().getStructureRoot();
            } else {
                structureRoot = PDFStructureRoot.newInstance(document);
            }

            final PDFPage page = document.requireCatalog().getPages().getPage(0);

            // Create a ContentWriter with a ModifiableContent object
            final ContentWriter writer = ContentWriter.newInstance(ModifiableContent.newInstance(document));

            final StatefulOperatorHandler handler = new ContentStreamHandler(document, structureRoot, writer);
            final StatefulContentStreamProcessor processor = new StatefulContentStreamProcessor(handler);

            // Walk through the content stream of the first page of the document
            processor.process(page);

            // Create a Content object from the instructions in the ContentWriter
            final Content newContent = writer.close();

            // Replace the contents of page 1 with the Content object created from the ContentWriter
            page.setContents(newContent.getContents());

            DocumentHelper.saveAndClose(document, outputUrl.toURI().getPath(), PDFSaveFullOptions.newInstance());
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

class ContentStreamHandler extends StatefulOperatorHandler {
    PDFDocument document = null;
    PDFStructureRoot structureRoot = null;
    ContentWriter writer = null;
    int mcid = 0;
    /**
     * @throws PDFInvalidDocumentException
     * @throws PDFIOException
     * @throws PDFSecurityException
     */
    public ContentStreamHandler(final PDFDocument document, final PDFStructureRoot structureRoot,
                             final ContentWriter writer)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException {
        super();
        this.document = document;
        this.structureRoot = structureRoot;
        this.writer = writer;
    }

    public ContentWriter getContentWriter() {
        return writer;
    }

    // Text objects
    @Override
    public void BT(final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void ET(final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    // // Text state
    @Override
    public void Tc(final double charSpacing, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void Tw(final double wordSpacing, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void Tz(final double scaling, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void TL(final double leading, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void Tf(final ASName fontName, final double fontSize, final Instruction instruction)
                    throws PDFInvalidDocumentException,
                    PDFIOException, PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void Tr(final int renderMode, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void Ts(final double textRise, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }
    //
    // // Text positioning
    @Override
    public void Td(final double tx, final double ty, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void TD(final double tx, final double ty, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void Tm(final double a, final double b, final double c, final double d, final double e, final double f,
                   final Instruction instruction)
                                   throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }

    @Override
    public void TStar(final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException, PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }
    //
    // // Text showing
    @Override
    public
    void Tj(final ASString string, final Instruction instruction) throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        writer.write(instruction);
    }
    // void SingleQuote(ASString string, Instruction instruction) throws PDFInvalidDocumentException,
    // PDFIOException, PDFSecurityException;
    // void DoubleQuote(double wordSpacing, double charSpacing, ASString string, Instruction instruction) throws
    // PDFInvalidDocumentException, PDFIOException, PDFSecurityException;
    @Override
    public void TJ(final ASArray tjArray, final Instruction instruction)
                    throws PDFInvalidDocumentException, PDFIOException,
                    PDFSecurityException {
        instruction.getOperator();
        final ASDictionary markedContentDictionary = new ASDictionary();
        markedContentDictionary.put(ASName.k_MCID, new ASString(String.valueOf(mcid)));
        final Instruction markedContentInstruction = InstructionFactory.newBeginMarkedContent(ASName.k_P,
                                                                                              markedContentDictionary);
        final PDFStructureMCID element = PDFStructureMCID.newInstance(document,
                                                                         mcid);

        writer.write(markedContentInstruction);
        writer.write(instruction);
        writer.write(InstructionFactory.newEndMarkedContent());
        structureRoot.addContent(element);
        mcid++;
    }
}
