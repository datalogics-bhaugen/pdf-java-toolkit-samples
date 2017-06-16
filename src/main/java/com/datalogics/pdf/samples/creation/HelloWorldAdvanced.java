/*
 * Copyright 2015 Datalogics, Inc.
 */

import java.awt.Color;
import java.io.File;
import java.io.RandomAccessFile;
import com.adobe.internal.io.ByteWriter;
import com.adobe.internal.io.RandomAccessFileByteWriter;
import com.adobe.pdfjt.core.exceptions.PDFException;
import com.adobe.pdfjt.core.types.ASName;
import com.adobe.pdfjt.core.types.ASRectangle;
import com.adobe.pdfjt.core.types.ASString;
import com.adobe.pdfjt.pdf.content.Content;
import com.adobe.pdfjt.pdf.content.Instruction;
import com.adobe.pdfjt.pdf.content.InstructionFactory;
import com.adobe.pdfjt.pdf.contentmodify.ContentWriter;
import com.adobe.pdfjt.pdf.contentmodify.ModifiableContent;
import com.adobe.pdfjt.pdf.document.PDFCatalog;
import com.adobe.pdfjt.pdf.document.PDFContents;
import com.adobe.pdfjt.pdf.document.PDFDocument;
import com.adobe.pdfjt.pdf.document.PDFOpenOptions;
import com.adobe.pdfjt.pdf.document.PDFResources;
import com.adobe.pdfjt.pdf.document.PDFSaveFullOptions;
import com.adobe.pdfjt.pdf.graphics.font.PDFFont;
import com.adobe.pdfjt.pdf.graphics.font.PDFFontMap;
import com.adobe.pdfjt.pdf.graphics.font.PDFFontSimple;
import com.adobe.pdfjt.pdf.interactive.PDFViewerPreferences;
import com.adobe.pdfjt.pdf.page.PDFPage;
import com.adobe.pdfjt.pdf.page.PDFPageLayout;
import com.adobe.pdfjt.pdf.page.PDFPageMode;
import com.adobe.pdfjt.pdf.page.PDFPageTree;

/*
   Demonstrates creating a new document and placing text.
   Comments in this sample refer to the ISO 32000:2008 PDF Reference located at
   http://www.adobe.com/content/dam/Adobe/en/devnet/acrobat/pdfs/PDF32000_2008.pdf
  
   Output file is written to output/HelloWorldAdvanced.pdf
 */

public class HelloWorldAdvanced {
	// One inch in PDF coordinates is 72 points. Create an INCHES constant to make measuring easier
	private static final double INCHES = 72;
	private static final double PAGE_HEIGHT = 11*INCHES;
	private static final double PAGE_WIDTH = 8.5*INCHES;
	static final String FIL‎E_PATH = "output/HelloWorldAdvanced.pdf";

	public static void main(String[] args) {
		
		try{
			/*
			   Create an ASRectangle object to initialize an empty PDF. This one
			   will create a page that is 8.5 x 11 INCHES based on the constants
			   above. Then use that rectangle to generate a new document with a
			   single blank page.
			 */
			
			double[] rectangle = {0, 0, PAGE_WIDTH, PAGE_HEIGHT};
			ASRectangle rect = new ASRectangle(rectangle);
			PDFDocument pdfDocument = PDFDocument.newInstance(rect, PDFOpenOptions.newInstance());
			
			/*
			   The root of a document’s object hierarchy is the catalog
			   dictionary, located by means of the Root entry in the trailer of
			   the PDF file. The catalog contains references to other objects
			   defining the document’s contents, outline, article threads, named
			   destinations, and other attributes. In addition, it contains
			   information about how the document is displayed on the screen,
			   such as whether its outline and thumbnail page images are
			   displayed automatically and whether some location other than the
			   first page will shown when the document is first opened.
			   
			   See section 7.7.2 of the PDF specification.
			   
			   In the case below, we are telling the viewer to... 1) Open the
			   document to single page mode 2) Set the magnification to
			   "Fit Page" 3) Show the the pages only, no additional tabs are
			   visible
			 */
			
			PDFCatalog catalog = pdfDocument.requireCatalog();
			catalog.setPageLayout(PDFPageLayout.SinglePage);
			PDFViewerPreferences viewerPrefs = PDFViewerPreferences.newInstance(pdfDocument);
			viewerPrefs.setFitWindow(true);
			viewerPrefs.setPageMode(PDFPageMode.PagesOnly);
			
			/*
			   The pages of a document are accessed through a structure known as
			   the page tree, which defines the ordering of pages in the
			   document.
			   
			   Get the pageTree, then the first page. Pages numbers are zero
			   based.
			   
			   See section 7.7.3 of the PDF specification, "Page Tree"
			 */
			
			PDFPageTree pdfPageTree = pdfDocument.requirePages();
			PDFPage pdfPage = pdfPageTree.getPage(0);
			
			/*
			   A content stream (PDFContents) is a PDF stream object whose data
			   consists of a sequence of "instructions" describing the graphical
			   elements to be painted on a page.
			   
			   This will be used by the "ContentWriter" below.
			   
			   See section 7.7.3 of the PDF specification, "Content Streams"
			 */
			PDFContents pdfContents = PDFContents.newInstance(pdfPage.getPDFDocument());
			
			/*
			   A content stream’s named resources (PDFResources) are defined by
			   a resource dictionary, which enumerates the named resources
			   needed by the operators in the content stream and the names by
			   which they can be referred to.
			   
			   EXAMPLE: If a text operator appearing within the content stream
			   needs a certain font, the content stream’s resource dictionary
			   can associate the name "F42" with the corresponding font
			   dictionary. The text operator can then use this name to refer to
			   the font.
			   
			   This will be used by the "ContentWriter" below.
			   
			   See section 7.8.3 of the PDF specification,
			   "Resource Dictionaries"
			   
			   For the simplicity we'll use one of the standard 14 fonts. These
			   fonts, or their font metrics and suitable substitution fonts, are
			   available to all conforming PDF readers. Any of the standard 14
			   fonts can be used without the need for font embedding.
			   
			   The standard 14 fonts are as follows: Helvetica Helvetica-Oblique
			   Helvetica-Bold Helvetica-BoldOblique Courier Courier-Oblique
			   Courier-Bold Courier-BoldOblique Times-Roman Times-Italic
			   Times-Bold Times-BoldItalic Symbol ZapfDingbats
			 */

			PDFResources pdfResources = PDFResources.newInstance(pdfPage.getPDFDocument());	       
			//PDFFontSimple abstracts functionality common to the simple font dictionaries
			PDFFont helveticaBold = PDFFontSimple.newInstance(
					pdfPage.getPDFDocument(), ASName.k_Helvetica_Bold, ASName.k_Type1);
			PDFFontMap pdfFontMap = PDFFontMap.newInstance(pdfPage.getPDFDocument());
			pdfFontMap.set(ASName.create("Helvetica-Bold"), helveticaBold);	
			pdfResources.setFontMap(pdfFontMap);
			pdfResources.setProcSetList(new ASName[] {ASName.k_PDF,ASName.k_Text});
			pdfPage.setResources(pdfResources);
			
			/*
			   InstructionFactory is used to create new page content
			   "Instructions". ContentWriter adds Instructions to PDF content.
			 */
			
			ContentWriter contentWriter = ContentWriter.newInstance(
					ModifiableContent.newInstance(pdfContents,pdfResources));
			
			/*
			   The next section of code will show how to use the text rendering
			   mode to fill, stroke or fill and stroke text. we'll use the text
			   "Hello World" and the next line instruction three times so we'll
			   create that instruction ahead of time.
			 */
			
			Instruction showHelloWorld = InstructionFactory.newShowText(new ASString("Hello World"));
			Instruction nextLine = InstructionFactory.newTextNextLine();
			//A new text block
			contentWriter.write(InstructionFactory.newBeginText());
			//One inch from the top minus the height of the text.
			contentWriter.write(InstructionFactory.newTextPosition(
					1*INCHES, PAGE_HEIGHT-(1*INCHES)-36)); 
			// Set the font and size in points.
			contentWriter.write(InstructionFactory.newTextFont(ASName.create("Helvetica-Bold"), 36));
			// used to determine the position of the next line of text
			contentWriter.write(InstructionFactory.newTextLeading(36)); 
			/* 
			   Define the color of the text. "getColorComponents" allows us to
			   specify the color by name
			*/ 
			contentWriter.write(InstructionFactory.newColorFill(getColorComponents(Color.black))); 
			
			/* Solid text is the default so we don't need 
			   to set the rendering mode right now.
			   Show "Hello World"
			 */
			contentWriter.write(showHelloWorld);
			
			// move down by the value of the leading
			contentWriter.write(nextLine); 
			/*
			   Change the TextRenderingMode to Stroked
			   0 = Solid Fill
			   1 = Stroked, no fill
			   2 = Stroked and filled	
			 */
			contentWriter.write(InstructionFactory.newTextRenderingMode(1)); 
			//Set the stroke color
			contentWriter.write(InstructionFactory.newColorStroke(getColorComponents(Color.red)));
			//Show "Hello World" as Stroked text
			contentWriter.write(showHelloWorld); 
			
			contentWriter.write(nextLine); 
			contentWriter.write(InstructionFactory.newTextRenderingMode(2)); 
			contentWriter.write(InstructionFactory.newColorStroke(getColorComponents(Color.black)));
			contentWriter.write(InstructionFactory.newColorFill(getColorComponents(Color.red)));
			// Show "Hello World" as Stroked and Filled Text
			contentWriter.write(showHelloWorld); 
			// End the text block	
			contentWriter.write(InstructionFactory.newEndText()); 
			
			/*
			   Now it's for a few graphics instructions. The following code block
			   will add a few lines with different styles and five squares. The
			   squares will be filled and stroked similarly to the text above.
			   
			   Additionally, because we will be changing the line dash pattern
			   and other graphics operators for which there is no reset, we wrap
			   the drawing operators in GSave and GRestore to save and restore
			   the default graphics state before and after each shape. This
			   prevents one set of instructions from interfering with the next.
			 */
			
			Instruction gSave = InstructionFactory.newGSave();
			Instruction gRestore = InstructionFactory.newGRestore();
			
			//Create a Solid line
			contentWriter.write(gSave);
			//Start of a new path
			contentWriter.write(InstructionFactory.newMoveTo(1*INCHES, PAGE_HEIGHT-(3*INCHES))); 
			//Next point on the path
			contentWriter.write(InstructionFactory.newLineTo(PAGE_WIDTH-(1*INCHES),  PAGE_HEIGHT-(3*INCHES)));  
			// a 1 point line
			contentWriter.write(InstructionFactory.newLineWidth(1)); 
			contentWriter.write(InstructionFactory.newStrokePath());
			contentWriter.write(gRestore);
			
			//Create a Dashed line
			contentWriter.write(gSave);
			//Start of a new path
			contentWriter.write(InstructionFactory.newMoveTo(1*INCHES, PAGE_HEIGHT-(3*INCHES)-(.125*INCHES))); 
			//Next point on the path
			contentWriter.write(InstructionFactory.newLineTo(PAGE_WIDTH-(1*INCHES),  PAGE_HEIGHT-(3*INCHES)-(.125*INCHES))); 
			// Set the line thickness to 1 point	
			contentWriter.write(InstructionFactory.newLineWidth(1)); 
			/*
			   The line dash pattern controls the pattern of dashes and
			   gaps used to stroke paths. It is specified by a dash array
			   and a dash phase. The dash array’s elements are numbers that
			   specify the lengths of alternating dashes and gaps; the numbers
			   are nonnegative and not all zero. The dash phase 
			   specifies the distance into the dash pattern at which to start the
			   dash. The elements of both the dash array and the dash phase
			   are expressed in user space units.
			   
			   See Section 8.4.3.6 "Line Dash Pattern" in the PDF Reference
			   
			   Add a new dashed line
			 */
			contentWriter.write(InstructionFactory.newLineDashPattern(new double[]{2, 1}, 0)); 
			contentWriter.write(InstructionFactory.newStrokePath());
			contentWriter.write(gRestore);
			
			/*
			   Add a rectangle where the x,y is the bottom/left and 
			   width and height move up the page and to the right.
			   
			   Filled Square
			*/
			contentWriter.write(gSave);
			contentWriter.write(InstructionFactory.newRectangle(1*INCHES, PAGE_HEIGHT-(4.25*INCHES), 1*INCHES, 1*INCHES));   
			contentWriter.write(InstructionFactory.newColorFill(getColorComponents(Color.black)));
			// Fill the rectangle with the fill color
			contentWriter.write(InstructionFactory.newFillPath()); 
			contentWriter.write(gRestore);
			
			//Stroked Square
			contentWriter.write(gSave);
			contentWriter.write(InstructionFactory.newRectangle(2.25*INCHES, PAGE_HEIGHT-(4.25*INCHES), 1*INCHES, 1*INCHES));   
			contentWriter.write(InstructionFactory.newColorStroke(getColorComponents(Color.red)));
			// Stroke the rectangle with the stroke color
			contentWriter.write(InstructionFactory.newStrokePath()); 
			contentWriter.write(gRestore);

			//Filled and Stroked Square
			contentWriter.write(gSave);
			contentWriter.write(InstructionFactory.newRectangle(3.5*INCHES, PAGE_HEIGHT-(4.25*INCHES), 1*INCHES, 1*INCHES));   
			contentWriter.write(InstructionFactory.newColorFill(getColorComponents(Color.red)));
			contentWriter.write(InstructionFactory.newColorStroke(getColorComponents(Color.black)));
			// Fill and Stroke the rectangle with their respective colors
			contentWriter.write(InstructionFactory.newFillAndStrokePath()); 
			contentWriter.write(gRestore);
			
			/*
			Now we'll draw the same squares except change the line join type. Starting with 
			rounded corners
			*/
			contentWriter.write(gSave);
			contentWriter.write(InstructionFactory.newRectangle(1*INCHES, PAGE_HEIGHT-(5.5*INCHES), 1*INCHES, 1*INCHES));
			// a thick 10 point line, so you can see the line join better
			contentWriter.write(InstructionFactory.newLineWidth(10)); 
			//The argument for newLineJoin can be one of 0, 1, or 2 meaning respectively: miter corners, round corners, or bevel corners
			contentWriter.write(InstructionFactory.newLineJoin(1));   
			contentWriter.write(InstructionFactory.newStrokePath());
			contentWriter.write(gRestore);
			
			//beveled corners
			contentWriter.write(gSave);
			contentWriter.write(InstructionFactory.newRectangle(2.25*INCHES, PAGE_HEIGHT-(5.5*INCHES), 1*INCHES, 1*INCHES));
			contentWriter.write(InstructionFactory.newLineWidth(10));
			contentWriter.write(InstructionFactory.newLineJoin(2)); // beveled corners
			contentWriter.write(InstructionFactory.newStrokePath());
			contentWriter.write(gRestore);
			
			/*
			   "Content" represents both a content stream and the resources
			   reference by the content stream.
			 */
			Content content = contentWriter.close();
			
			/*
			   "setContents" overwrites the page content stream with the stream
			   passed here.
			 */
			
			pdfPage.setContents(content.getContents());
					
			/*
			   ByteWriter does not overwrite existing files, so we want to make
			   sure this file doesn't already exist beforehand by deleting
			   anything that is currently at our output path. Be careful when
			   doing this.
			 */
			
			File file = new File(FIL‎E_PATH); 
			file.mkdirs();
			if (file.exists()) {
				file.delete();
			}
			
			/*
			   Write everything we've done to the original PDF file
			 */
			
			RandomAccessFile outputPDFFile = new RandomAccessFile(FIL‎E_PATH, "rw");
			ByteWriter outputWriter = new RandomAccessFileByteWriter(outputPDFFile);	
			
			/*
			   Save the document and clean up the resources that were used to
			   create it.
			 */
			
			pdfDocument.saveAndClose(outputWriter, PDFSaveFullOptions.newInstance());
			System.out.println("Done!");

			
			try { 
				if (pdfDocument != null) {
					pdfDocument.close();
				}
			} catch (PDFException e) {
				e.printStackTrace();
			}

			if (outputWriter != null) {
				outputWriter.close();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static double[] getColorComponents(Color color) {
		float[] colorComponents = color.getColorComponents(null);
		double[] colorComponentsDouble = new double[colorComponents.length];
		for (int i = 0; i < colorComponents.length; i++) {
			colorComponentsDouble[i] = colorComponents[i];
		}
		return colorComponentsDouble;
	}	
}
