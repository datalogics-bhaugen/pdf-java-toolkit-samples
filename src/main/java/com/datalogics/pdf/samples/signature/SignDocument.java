/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.signature;

import com.adobe.internal.io.ByteWriter;
import com.adobe.pdfjt.core.credentials.Credentials;
import com.adobe.pdfjt.core.exceptions.PDFException;
import com.adobe.pdfjt.core.exceptions.PDFIOException;
import com.adobe.pdfjt.core.fontset.PDFFontSet;
import com.adobe.pdfjt.core.license.LicenseManager;
import com.adobe.pdfjt.core.securityframework.CryptoMode;
import com.adobe.pdfjt.core.types.ASMatrix;
import com.adobe.pdfjt.core.types.ASRectangle;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFOpenOptions;
import com.adobe.pdfjt.pdf.document.PDFSaveFullOptions;
import com.adobe.pdfjt.pdf.document.PDFVersion;
import com.adobe.pdfjt.pdf.graphics.PDFExtGState;
import com.adobe.pdfjt.pdf.graphics.PDFRectangle;
import com.adobe.pdfjt.pdf.graphics.xobject.PDFXObjectImage;
import com.adobe.pdfjt.pdf.page.PDFPage;
import com.adobe.pdfjt.services.digsig.SignatureAppearanceDisplayItemsSet;
import com.adobe.pdfjt.services.digsig.SignatureAppearanceOptions;
import com.adobe.pdfjt.services.digsig.SignatureFieldFactory;
import com.adobe.pdfjt.services.digsig.SignatureFieldInterface;
import com.adobe.pdfjt.services.digsig.SignatureManager;
import com.adobe.pdfjt.services.digsig.SignatureOptions;
import com.adobe.pdfjt.services.digsig.SignatureOptionsDocumentTimeStamp;
import com.adobe.pdfjt.services.digsig.UserInfo;
import com.adobe.pdfjt.services.digsig.cryptoprovider.CertJNonFIPSProvider;
import com.adobe.pdfjt.services.digsig.spi.CryptoContext;
import com.adobe.pdfjt.services.digsig.spi.SignatureServiceProvider;
import com.adobe.pdfjt.services.digsig.spi.TimeStampProvider;
import com.adobe.pdfjt.services.imageconversion.ImageManager;

import com.datalogics.pdf.document.FontSetLoader;
import com.datalogics.pdf.samples.util.DocumentUtils;
import com.datalogics.pdf.samples.util.IoUtils;
import com.datalogics.pdf.samples.util.SampleCredentialGenerator;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.NodeList;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
/**
 * This is a sample that demonstrates how to find a specific signature field in a document so that API users can sign
 * the correct field. This sample signs the document using a certificate and key drawn from the key and certificate
 * files pdfjt-key.der and pdfjt-cert.der.
 *
 * <p>
 * Note that these certificates were created by Datalogics for use in testing digital signatures with PDF Java Toolkit.
 * They are self-signed by Datalogics, and so this sample certificate is not backed by a certifying authority (CA).
 * The certificate is intended for a test environment. If you open the output PDF document created using this sample
 * in Adobe Acrobat, you will see an error message in the PDF document itself, stating that the validity of a signature
 * in the document is unknown.  The identity of the signer is not included in the local list of trusted certificates.
 */

public final class SignDocument {
    private static final Logger LOGGER = Logger.getLogger(SignDocument.class.getName());


    // private static final String DER_KEY_PATH = "pdfjt-key.der";
    // private static final String DER_CERT_PATH = "pdfjt-cert.der";
    // public static final String INPUT_UNSIGNED_PDF_PATH = "UnsignedDocument.pdf";
    public static final String INPUT_SIGNATURE_IMAGE_PATH = "Signature.jpg";
    public static final String OUTPUT_SIGNED_PDF_PATH = "SignedField.pdf";

    private static final String DER_KEY_PATH = "PiotrP.pfx";
    private static final String DER_CERT_PATH = "PiotrP_cert.cer";
    public static final String INPUT_UNSIGNED_PDF_PATH = "UnsignedDocument.pdf";
    // public static final String INPUT_UNSIGNED_PDF_PATH = "B_20131003133345_100000000.pdf";

    static final int inches = 72;

    private static final Double MM_PER_INCH = 25.4; // millimeters per inch
    private static final Double DEFAULT_MM_PER_PIXEL = 0.35277778; // default millimeters per pixel

    /**
     * This is a utility class, and won't be instantiated.
     */
    private SignDocument() {}

    /**
     * Main program.
     *
     * @param args command line arguments. Only one is expected in order to specify the output path. If no arguments are
     *        given, the sample will output to the root of the samples directory by default.
     * @throws Exception a general exception was thrown
     */
    public static void main(final String... args) throws Exception {
        // If you are using an evaluation version of the product (License Managed, or LM), set the path to where PDFJT
        // can find the license file.
        //
        // If you are not using an evaluation version of the product you can ignore or remove this code.
        LicenseManager.setLicensePath(".");

        URL outputUrl = null;
        if (args.length > 0) {
            outputUrl = IoUtils.createUrlFromPath(args[0]);
        } else {
            outputUrl = IoUtils.createUrlFromPath(OUTPUT_SIGNED_PDF_PATH);
        }

        final URL inputUrl = SignDocument.class.getResource(INPUT_UNSIGNED_PDF_PATH);
        // Query and sign all permissible signature fields.
        signExistingSignatureFields(inputUrl, outputUrl);
    }

    /**
     * Sign existing signature fields found in the example document.
     *
     * @param inputUrl the URL to the input file
     * @param outputUrl the path to the file to contain the signed document
     * @throws Exception a general exception was thrown
     */
    public static void signExistingSignatureFields(final URL inputUrl, final URL outputUrl) throws Exception {
        PDFDocument pdfDoc = null;
        try {
            // Attach font set to PDF
            final PDFFontSet fontSet = FontSetLoader.newInstance().getFontSet();
            final PDFOpenOptions openOptions = PDFOpenOptions.newInstance();
            openOptions.setFontSet(fontSet);

            // Get the PDF file.
            pdfDoc = DocumentUtils.openPdfDocumentWithOptions(inputUrl, openOptions);

            // Set up a signature service and iterate over all of the
            // signature fields.
            final SignatureManager sigService = SignatureManager.newInstance(pdfDoc);

            if (sigService.hasUnsignedSignatureFields()) {
                final Iterator<SignatureFieldInterface> iter =
                    sigService.getDocSignatureFieldIterator();
                while (iter.hasNext()) {
                    final SignatureFieldInterface sigField = iter.next();
                    signField(sigService, sigField, fontSet, outputUrl);
                }
            } else {

                final PDFRectangle pdfRectangle = PDFRectangle.newInstance(pdfDoc, 1 * inches, 7.5 * inches,
                                                                           4 * inches, 8 * inches);

                final PDFPage firstPage = pdfDoc.requireCatalog().getPages().getPage(0);

                // brak pola do podpisu muszę stworzyć własne
                // Create an unsigned signature field.
                //
                // page – PDFPage object that we want to put the signatures appearance on
                // annotRect – the rectangular space that the signatures appearance should take up
                // iform – the parent object of the signature, in this case an interactive form

                final SignatureFieldInterface sigField = SignatureFieldFactory.createSignatureField(firstPage,
                                                                                                    pdfRectangle,
                                                                                                    "Podpis");

                sigService.unsignSignatureField(sigField);

                signField(sigService, sigField, fontSet, outputUrl);
            }
        } finally {
            try {
                if (pdfDoc != null) {
                    pdfDoc.close();
                }
            } catch (final PDFException e) {
                LOGGER.severe(e.getMessage());
            }
        }
    }

    private static void signField(final SignatureManager sigMgr,
                                  final SignatureFieldInterface sigField,
                                  final PDFFontSet fontSet,
                                  final URL outputUrl)
                                                     throws Exception {

        final String qualifiedName = "Fully Qualified Name: " + sigField.getQualifiedName();
        LOGGER.info(qualifiedName);

        ByteWriter byteWriter = null;
        try {
            final Credentials credentials = createCredentials();
            // Must be permitted to sign doc and field must be visible.
            if (sigField.isSigningPermitted()) {
                if (sigField.isVisible()) {
                    // Create output file to hold the signed PDF data.
                    byteWriter = IoUtils.newByteWriter(outputUrl);

                    // Set up the appearance of the signature
                    final SignatureOptions signatureOptions = SignatureOptions.newInstance();
                    final SignatureAppearanceOptions appearanceOptions = SignatureAppearanceOptions.newInstance();
                    final SignatureAppearanceDisplayItemsSet displayItems = createSignatureAppearanceDisplayItemsSet();


                    appearanceOptions.setFontSet(fontSet);
                    appearanceOptions.setDisplayItems(displayItems);
                    signatureOptions.setSignatureAppearanceOptions(appearanceOptions);

                    final UserInfo userInfo = UserInfo.newInstance();

                    final URL signatureImageUrl = SignDocument.class.getResource(INPUT_SIGNATURE_IMAGE_PATH);
                    final PDFPage signaturePage = createSignaturePage(signatureImageUrl);
                    // appearanceOptions.setGraphicImage(signaturePage);

                    // This name will show up in the signature as "Digitally signed by <name>".
                    // If no name is specified the signature will say it was signed by whatever name is
                    // on the credentials used to sign the document.
                    userInfo.setName("Piotr śćłóąę znaki");
                    // userInfo.setLocation("Tu jest location");
                    // userInfo.setReason("Tu jest Reason");
                    signatureOptions.setUserInfo(userInfo);


                    appearanceOptions.setSignatureLabel("Podpisany przez: {0}, dla Jan Kowalski jak długi może być napis zaraz sprawdzimy ten jest już dość długi \u0105 wstawiony");

                    final PDFSaveFullOptions saveOptions = PDFSaveFullOptions.newInstance();
                    saveOptions.setVersion(PDFVersion
                               .v2_0);
                    // .v1_7_e5);

                    // Sign the document.
                    // sigMgr.sign(sigField, signatureOptions, credentials, byteWriter);
                    final SignatureOptionsDocumentTimeStamp sigOptions = SignatureOptionsDocumentTimeStamp.newInstance();
                    sigOptions.setSaveOptions(saveOptions);

                    final CryptoContext cryptoContext = new CryptoContext(CryptoMode.NON_FIPS_MODE, "SHA-1",
                                                                          "TSPAlgorithms.SHA1");

                    final TimeStampProvider tsProvider = new MyTimeStampProvider();
                    tsProvider.setTSAURL("http://time.certum.pl/");
                    // tsProvider.setTSAURL("http://timestamp.comodoca.com/authenticode");
                    final String a = tsProvider.getTSAURL();

                    // tsProvider.setDigestAlgorithm("SHA-1");

                    // sigOptions.registerTimeStampProvider(tsProvider);

                    cryptoContext.registerTimeStampProvider(tsProvider);

                    final SignatureServiceProvider defaultCryptoProvider = new CertJNonFIPSProvider(cryptoContext);

                    signatureOptions.setUserInfo(userInfo);


                    // Sign the document - bez znacznika czasu
                    // sigMgr.sign(sigField, signatureOptions, credentials, byteWriter);
                    // ze znacznikiem czasu
                    sigMgr.applyDocumentTimeStampSignature(sigOptions, defaultCryptoProvider, byteWriter);
                    // sigMgr.sign(sigField, sigOptions, credentials, byteWriter);
                } else {
                    throw new PDFIOException("Signature field is not visible");
                }
            }
        } finally {
            if (byteWriter != null) {
                byteWriter.close();
            }
        }
    }

    private static Credentials createCredentials() throws Exception {

        // These are sample files whose authenticity won't be able to be verified by Acrobat. When opening a document
        // signed with this certificate, Acrobat will display a warning. This does not indicate any error in the
        // document itself aside from the unverifiable signature.
        //final String sigAlgorithm = "RSA";
        final String sigAlgorithm = null;
        final InputStream certStream = SignDocument.class.getResourceAsStream(DER_CERT_PATH);
        final InputStream keyStream = SignDocument.class.getResourceAsStream(DER_KEY_PATH);

        return createCredentialsFromDerBytes(certStream, keyStream, sigAlgorithm);
    }


    private static Credentials createCredentialsFromDerBytes(final InputStream certStream,
                                                             final InputStream keyStream,
                                                             final String sigAlgorithm)
                                                                             throws Exception {
        //final byte[] derEncodedPrivateKey = getDerEncodedData(keyStream);
        //final byte[] derEncodedCert = getDerEncodedData(certStream);
        //final PrivateKeyHolder privateKeyHolder = PrivateKeyHolderFactory
        //                                                                 .newInstance()
        //                                                                 .createPrivateKey(derEncodedPrivateKey,
        //                                                                                   sigAlgorithm);
        //final Credentials credentials = CredentialFactory.newInstance()
        //                                                 .createCredentials(privateKeyHolder, derEncodedCert,
        //                                                                    null);
        final char[] pasw = "pasw".toCharArray();

        final Credentials credentials = SampleCredentialGenerator.CredentialsFromPFX(DER_KEY_PATH, pasw);
        return credentials;
    }

    private static byte[] getDerEncodedData(final InputStream inputStream) throws IOException {
        return IOUtils.toByteArray(inputStream);
    }

    private static SignatureAppearanceDisplayItemsSet createSignatureAppearanceDisplayItemsSet() {
        final SignatureAppearanceDisplayItemsSet displayItems = SignatureAppearanceDisplayItemsSet.newInstance();

        // Show everything
        displayItems.enable(SignatureAppearanceDisplayItemsSet.kShowAll);
        return displayItems;
    }

    private static PDFPage createSignaturePage(final URL imagePath) throws Exception {
        final ImageReader reader = loadImage(imagePath);
        final BufferedImage bufferedImage = reader.read(0);

        final Double millimetersPerPixel = getMillimetersPerPixel(reader.getImageMetadata(0));
        final Double pageWidth = millimetersPerPixel / MM_PER_INCH * 72 * bufferedImage.getWidth();
        final Double pageHeight = millimetersPerPixel / MM_PER_INCH * 72 * bufferedImage.getHeight();

        // Create a PDF document with the first page being the same size as the PNG
        final PDFDocument pdfDocument = PDFDocument.newInstance(new ASRectangle(new double[] { 0, 0, pageWidth,
            pageHeight }),
                                                                PDFOpenOptions.newInstance());

        // Convert the BufferedImage to a PDFXObjectImage
        final PDFXObjectImage image = ImageManager.getPDFImage(bufferedImage, pdfDocument);

        // Create a default external graphics state which describes how graphics are to be rendered on a device.
        final PDFExtGState pdfExtGState = PDFExtGState.newInstance(pdfDocument);

        // Create a transformation matrix which maps positions from user coordinates to device coordinates. There is no
        // transform taking place here though but it is a required parameter.
        final ASMatrix asMatrix = new ASMatrix(pageWidth, 0, 0, pageHeight, 0, 0);

        // Now add the image to the first PDF page using the graphics state and the transformation matrix.
        ImageManager.insertImageInPDF(image,
                                      pdfDocument.requirePages().getPage(0), // the first page of the document
                                      pdfExtGState,
                                      asMatrix);

        return pdfDocument.requirePages().getPage(0);
    }

    private static ImageReader loadImage(final URL imagePath) throws Exception {
        // Returns the reader that claims it can decode the given image
        ImageReader reader = null;
        final ImageInputStream imgStm = ImageIO.createImageInputStream(imagePath.openStream());
        final Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imgStm);
        if (imageReaders.hasNext()) {
            reader = imageReaders.next();
            reader.setInput(imgStm, true);
        }

        return reader;
    }

    private static Double getMillimetersPerPixel(final IIOMetadata metadata) {
        // This code assumes square pixels so it only gets the horizontal measurement
        final IIOMetadataNode standardTree;
        standardTree = (IIOMetadataNode) metadata.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
        final IIOMetadataNode dimension = (IIOMetadataNode) standardTree.getElementsByTagName("Dimension").item(0);
        final NodeList pixelSizes = dimension.getElementsByTagName("HorizontalPixelSize");

        // If no physical dimensions were found, assume 1/72 of an inch
        if (pixelSizes.getLength() <= 0) {
            return DEFAULT_MM_PER_PIXEL;
        }

        final IIOMetadataNode pixelSize = (IIOMetadataNode) pixelSizes.item(0);

        return Double.parseDouble(pixelSize.getAttribute("value"));
    }
}
