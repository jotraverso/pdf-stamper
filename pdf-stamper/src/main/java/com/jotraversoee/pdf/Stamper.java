package com.jotraversoee.pdf;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

/**
 * The Class Stamper.
 */
public class Stamper {
	private static final int MARGIN_RIGHT = 5;
	private static final int MARGIN_TOP = 5;;
	private static final int INNER_TABLE_PADDING = 1;
	private static final int OUTER_TABLE_PADDING = 3;
	private static final int INNER_TABLE_BORDER_WIDTH = 1;
	private static final int OUTER_TABLE_BORDER_WIDTH = 2;
	private static final int INNER_TABLE_WIDTH_INCREMENT = 5;
	private static final int OUTER_TABLE_WIDTH_INCREMENT = 10;
	/** The original PDF file. */
	public static final String ORIGINAL = "http://www.empleo.gob.es/es/portada/reformalaboral/Ley-3-2012.pdf";
	/** The resulting PDF. */
	public static final String RESULT = "resultado.pdf";

	/** The Constant FILL_OPACITY. */
	public static final float FILL_OPACITY = 0.85f;

	/** The Constant STROKE_OPACITY. */
	public static final float STROKE_OPACITY = 0.3f;

	/** The Constant DEFAULT_FONT_SIZE. */
	public static final float DEFAULT_FONT_SIZE = 9f;

	/** The Constant DETAUL_FONT. */
	public static final int DETAUL_FONT = Font.HELVETICA;

	/** The stamp color. */
	private Color stampColor = Color.RED;

	/** The gstate. */
	private PdfGState gstate = new PdfGState();

	/** The secured. */
	private boolean secured = false;

	/** The font family. */
	private int fontFamily = DETAUL_FONT;

	/** The stamp width. */
	private float stampWidth = 110;

	/**
	 * Main method.
	 * 
	 * @param args
	 *            no arguments needed
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 * @throws SQLException
	 *             the sQL exception
	 */
	public static void main(final String[] args) throws IOException, DocumentException, SQLException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		String fecha = sdf.format(new Date()).toUpperCase();
		String[] textos = new String[] { "INSERVIBLE", fecha };
		float[] sizes = new float[] { 32, 22 };

		Stamper stamper = new Stamper(350, 0.85f, 0.90f, Color.blue, Font.COURIER, true);
		stamper.stampPages(textos, sizes, 1,1, 25,new URL(ORIGINAL).openStream(), new FileOutputStream("azul-" + RESULT));
		//
		// sizes = new float[] { 48, 32 };
		// stamper = new Stamper(400, 0.85f, 0.90f, Color.red, Font.HELVETICA,
		// true);
		// stamper.stamp(textos, sizes, new URL(ORIGINAL).openStream(), new
		// FileOutputStream("rojo-" + RESULT));
		// stamper.stampPages(textos, sizes, 1, -1, new
		// URL(ORIGINAL).openStream(), new FileOutputStream("todas-rojo-" +
		// RESULT));
		System.out.println("Archivos generados!!");
	}

	/**
	 * Instantiates a new stamper.
	 */
	public Stamper() {
		gstate.setFillOpacity(FILL_OPACITY);
		gstate.setStrokeOpacity(STROKE_OPACITY);
	}

	/**
	 * Instantiates a new stamper.
	 * 
	 * @param width
	 *            the stamp width
	 * @param fillOpacity
	 *            the fill opacity
	 * @param strokeOpacity
	 *            the stroke opacity
	 * @param stampColor
	 *            the stamp color
	 * @param fontFamily
	 *            the font family
	 * @param secure
	 *            the secured
	 * @see {@link com.lowagie.text.Font} for fontFamily options.
	 */
	public Stamper(final float width, final float fillOpacity, final float strokeOpacity, final Color stampColor, final int fontFamily,
			final boolean secure) {
		gstate.setFillOpacity(fillOpacity);
		gstate.setStrokeOpacity(strokeOpacity);
		this.stampWidth = width;
		this.stampColor = stampColor;
		this.fontFamily = fontFamily;
		this.secured = secure;
	}

	/**
	 * Stamp.
	 * 
	 * @param texts
	 *            the texts
	 * @param fontSizes
	 *            the font sizes
	 * @param pdf
	 *            the pdf
	 * @param result
	 *            the result
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 */
	public void stamp(final String[] texts, final float[] fontSizes, final File pdf, final File result) throws IOException, DocumentException {
		stamp(texts, fontSizes, new FileInputStream(pdf), new FileOutputStream(result));
	}

	/**
	 * Stamp.
	 * 
	 * @param texts
	 *            the texts
	 * @param fontSizes
	 *            the font sizes
	 * @param pdf
	 *            the pdf
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 */
	public File stamp(final String[] texts, final float[] fontSizes, final File pdf) throws IOException, DocumentException {
		File temp = File.createTempFile("stamper", ".pdf");
		stamp(texts, fontSizes, new FileInputStream(pdf), new FileOutputStream(temp));
		return temp;
	}

	/**
	 * Stamp.
	 * 
	 * @param texts
	 *            the texts
	 * @param fontSizes
	 *            the font sizes
	 * @param pdf
	 *            the pdf
	 * @return the input stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 */
	public InputStream stamp(final String[] texts, final float[] fontSizes, final InputStream pdf) throws IOException, DocumentException {
		File temp = File.createTempFile("stamper", ".pdf");
		stamp(texts, fontSizes, pdf, new FileOutputStream(temp));
		return new FileInputStream(temp);
	}

	/**
	 * Stamp pages.
	 * 
	 * @param texts
	 *            the texts
	 * @param fontSizes
	 *            the font sizes
	 * @param startPage
	 *            the start page
	 * @param endPage
	 *            the end page, a number less than 0 for all pages.
	 * @param degrees
	 *            rotation, between 0 and 90.
	 * @param pdf
	 *            the pdf
	 * @param result
	 *            the result
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 */
	public void stampPages(final String[] texts, final float[] fontSizes, final int startPage, final int endPage, float degrees,
			final InputStream pdf, final OutputStream result) throws IOException, DocumentException {
		PdfReader reader = new PdfReader(pdf);
		int end = endPage;
		if (end < 0 || end > reader.getNumberOfPages()) {
			end = reader.getNumberOfPages();
		}

		PdfStamper stamper = new PdfStamper(reader, result);
		if (secured) {
			SecureRandom rnd = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());
			stamper.setEncryption(true, "", new BigInteger(256, rnd).toString(), PdfWriter.ALLOW_PRINTING);
		}
		PdfPTable table1 = new PdfPTable(1);
		table1.getDefaultCell().setBorderWidth(OUTER_TABLE_BORDER_WIDTH);
		table1.getDefaultCell().setPadding(OUTER_TABLE_PADDING);
		table1.getDefaultCell().setBorderColor(stampColor);
		table1.setTotalWidth(stampWidth + OUTER_TABLE_WIDTH_INCREMENT);

		PdfPTable table2 = new PdfPTable(1);
		table2.getDefaultCell().setBorderWidth(INNER_TABLE_BORDER_WIDTH);
		table2.getDefaultCell().setPadding(INNER_TABLE_PADDING);
		table2.getDefaultCell().setBorderColor(stampColor);
		table2.setTotalWidth(stampWidth + INNER_TABLE_WIDTH_INCREMENT);

		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth(stampWidth);
		PdfPCell defaultCell = table.getDefaultCell();
		defaultCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		defaultCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		defaultCell.setBorderWidth(0f);
		defaultCell.setPadding(INNER_TABLE_PADDING);
		float fontSize = DEFAULT_FONT_SIZE;
		for (int i = 0; i < texts.length; i++) {
			String text = texts[i];
			if (fontSizes.length > i) {
				fontSize = fontSizes[i];
			}
			table.addCell(new Phrase(text, new Font(fontFamily, fontSize, Font.BOLD, stampColor)));
		}
		table2.addCell(table);
		table1.addCell(table2);

		for (int i = startPage; i <= end; i++) {
			PdfContentByte under = stamper.getOverContent(i);
			Rectangle pageSize = reader.getPageSize(i);
			double alpha = Math.toRadians(degrees);
			under.setGState(gstate);
			AffineTransform af = AffineTransform.getRotateInstance(alpha);
			under.transform(af);
			float tw = table1.getTotalWidth();
			float th = table1.getTotalHeight();
			float ox = (float) (pageSize.getWidth() - MARGIN_RIGHT - (Math.cos(alpha) * tw) - (Math.sin(alpha) * th));
			float oy = (float) (pageSize.getHeight() - MARGIN_TOP - (Math.sin(alpha) * tw));
			float oh = (float) Math.sqrt(ox * ox + oy * oy);
			double beta = Math.asin(oy / oh) - alpha;
			float xp = (float) (Math.cos(beta) * oh);
			float yp = (float) (Math.sin(beta) * oh);
			table1.writeSelectedRows(0, -1, xp, yp, under);
		}
		stamper.close();
	}

	/**
	 * Stamp.
	 * 
	 * @param texts
	 *            the texts
	 * @param fontSizes
	 *            the font sizes
	 * @param pdf
	 *            the pdf
	 * @param result
	 *            the result
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DocumentException
	 *             the document exception
	 */
	public void stamp(final String[] texts, final float[] fontSizes, final InputStream pdf, final OutputStream result) throws IOException,
			DocumentException {
		stampPages(texts, fontSizes, 1, 1, 0, pdf, result);
	}

	/**
	 * Gets the stamp color.
	 * 
	 * @return the stamp color
	 */
	public Color getStampColor() {
		return stampColor;
	}

	/**
	 * Sets the stamp color.
	 * 
	 * @param stampColor
	 *            the new stamp color
	 */
	public void setStampColor(final Color stampColor) {
		this.stampColor = stampColor;
	}

	/**
	 * Gets the gstate.
	 * 
	 * @return the gstate
	 */
	public PdfGState getGstate() {
		return gstate;
	}

	/**
	 * Sets the gstate.
	 * 
	 * @param gstate
	 *            the new gstate
	 */
	public void setGstate(final PdfGState gstate) {
		this.gstate = gstate;
	}

	/**
	 * Checks if is secured.
	 * 
	 * @return true, if is secured
	 */
	public boolean isSecured() {
		return secured;
	}

	/**
	 * Sets the secured.
	 * 
	 * @param secured
	 *            the new secured
	 */
	public void setSecured(final boolean secured) {
		this.secured = secured;
	}

	/**
	 * Gets the font family.
	 * 
	 * @return the font family
	 */
	public int getFontFamily() {
		return fontFamily;
	}

	/**
	 * Sets the font family.
	 * 
	 * @param fontFamily
	 *            the new font family
	 */
	public void setFontFamily(final int fontFamily) {
		this.fontFamily = fontFamily;
	}

	/**
	 * Gets the stamp width.
	 * 
	 * @return the stamp width
	 */
	public float getStampWidth() {
		return stampWidth;
	}

	/**
	 * Sets the stamp width.
	 * 
	 * @param stampWidth
	 *            the new stamp width
	 */
	public void setStampWidth(final float stampWidth) {
		this.stampWidth = stampWidth;
	}

}