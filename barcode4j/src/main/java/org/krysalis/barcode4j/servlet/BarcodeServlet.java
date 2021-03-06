/*
 * Copyright 2002-2007 Jeremias Maerki or contributors to Barcode4J, as applicable
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krysalis.barcode4j.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

import com.github.mbhk.barcode4j.Configuration;

/**
 * Simple barcode servlet.
 *
 * @version $Id$
 */
public class BarcodeServlet extends HttpServlet {

    private static final long serialVersionUID = -1612710758060435089L;

    /** Parameter name for the message */
    public static final String BARCODE_MSG = "msg";
    /** Parameter name for the barcode type */
    public static final String BARCODE_TYPE = "type";
    /** Parameter name for the barcode height */
    public static final String BARCODE_HEIGHT = "height";
    /** Parameter name for the module width */
    public static final String BARCODE_MODULE_WIDTH = "mw";
    /** Parameter name for the wide factor */
    public static final String BARCODE_WIDE_FACTOR = "wf";
    /** Parameter name for the quiet zone */
    public static final String BARCODE_QUIET_ZONE = "qz";
    /** Parameter name for the human-readable placement */
    public static final String BARCODE_HUMAN_READABLE_POS = "hrp";
    /** Parameter name for the output format */
    public static final String BARCODE_FORMAT = "fmt";
    /** Parameter name for the image resolution (for bitmaps) */
    public static final String BARCODE_IMAGE_RESOLUTION = "res";
    /** Parameter name for the grayscale or b/w image (for bitmaps) */
    public static final String BARCODE_IMAGE_GRAYSCALE = "gray";
    /** Parameter name for the font size of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_SIZE = "hrsize";
    /** Parameter name for the font name of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_FONT = "hrfont";
    /** Parameter name for the pattern to format the human readable message */
    public static final String BARCODE_HUMAN_READABLE_PATTERN = "hrpattern";

    private static final String ERROR_WHILE_GENERATING_BARCODE = "Error while generating barcode";

    private static final Logger LOGGER = Logger.getLogger(BarcodeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            final String format = determineFormat(request);
            final Orientation orientation = Orientation.ZERO;

            final Configuration cfg = buildCfg(request);

            String msg = request.getParameter(BARCODE_MSG);
            if (msg == null) {
                msg = "0123456789";
            }

            final BarcodeUtil util = BarcodeUtil.getInstance();
            final BarcodeGenerator gen = util.createBarcodeGenerator(cfg);

            final ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
            try {
                if (format.equals(MimeTypes.MIME_SVG)) {
                    // Create Barcode and render it to SVG
                    final SVGCanvasProvider svg = new SVGCanvasProvider(true, orientation);
                    gen.generateBarcode(svg, msg);
                    final org.w3c.dom.DocumentFragment frag = svg.getDOMFragment();

                    // Serialize SVG barcode
                    final TransformerFactory factory = TransformerFactory.newInstance();
                    final Transformer trans = factory.newTransformer();
                    final Source src = new javax.xml.transform.dom.DOMSource(frag);
                    final Result res = new javax.xml.transform.stream.StreamResult(bout);
                    trans.transform(src, res);
                } else if (format.equals(MimeTypes.MIME_EPS)) {
                    final EPSCanvasProvider eps = new EPSCanvasProvider(bout, orientation);
                    gen.generateBarcode(eps, msg);
                    eps.finish();
                } else {
                    final String resText = request.getParameter(BARCODE_IMAGE_RESOLUTION);
                    int resolution = 300; // dpi
                    if (resText != null) {
                        resolution = Integer.parseInt(resText);
                    }
                    if (resolution > 2400) {
                        throw new IllegalArgumentException("Resolutions above 2400dpi are not allowed");
                    }
                    if (resolution < 10) {
                        throw new IllegalArgumentException("Minimum resolution must be 10dpi");
                    }
                    final String gray = request.getParameter(BARCODE_IMAGE_GRAYSCALE);
                    final BitmapCanvasProvider bitmap = "true".equalsIgnoreCase(gray)
                            ? new BitmapCanvasProvider(bout, format, resolution, BufferedImage.TYPE_BYTE_GRAY, true,
                                    orientation)
                            : new BitmapCanvasProvider(bout, format, resolution, BufferedImage.TYPE_BYTE_BINARY, false,
                                    orientation);
                    gen.generateBarcode(bitmap, msg);
                    bitmap.finish();
                }
            } finally {
                bout.close();
            }
            response.setContentType(format);
            response.setContentLength(bout.size());
            response.getOutputStream().write(bout.toByteArray());
            response.getOutputStream().flush();
        } catch (BarcodeException e) {
            LOGGER.log(Level.SEVERE, ERROR_WHILE_GENERATING_BARCODE, e);
            throw new ServletException(e);
        } catch (TransformerException e) {
            LOGGER.log(Level.SEVERE, ERROR_WHILE_GENERATING_BARCODE, e);
            throw new ServletException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_WHILE_GENERATING_BARCODE, e);
            throw new ServletException(e);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, ERROR_WHILE_GENERATING_BARCODE, e);
            throw new ServletException(e);
        }
    }

    /**
     * Check the request for the desired output format.
     * 
     * @param request
     *            the request to use
     * @return MIME type of the desired output format.
     */
    protected String determineFormat(HttpServletRequest request) {
        String format = request.getParameter(BARCODE_FORMAT);
        format = MimeTypes.expandFormat(format);
        if (format == null) {
            format = MimeTypes.MIME_SVG;
        }
        return format;
    }

    /**
     * Build an Configuration object from the request.
     * 
     * @param request
     *            the request to use
     * @return the newly built COnfiguration object
     * @todo Change to bean API
     */
    protected Configuration buildCfg(HttpServletRequest request) {
        // Get type
        String type = request.getParameter(BARCODE_TYPE);
        if (type == null || type.trim().isEmpty()) {
            type = "code128";
        }
        final Configuration cfg = new Configuration(type);
        // Get additional attributes
        final String height = request.getParameter(BARCODE_HEIGHT);
        final String moduleWidth = request.getParameter(BARCODE_MODULE_WIDTH);
        final String wideFactor = request.getParameter(BARCODE_WIDE_FACTOR);
        if (height != null)
            cfg.addChild(new Configuration("height", height));
        if (moduleWidth != null)
            cfg.addChild(new Configuration("module-width", moduleWidth));
        if (wideFactor != null)
            cfg.addChild(new Configuration("wide-factor", wideFactor));
        final String quietZone = request.getParameter(BARCODE_QUIET_ZONE);
        if (quietZone != null) {
            final Configuration quietCfg = new Configuration("quiet-zone", quietZone);
            quietCfg.setAttribute("enabled", quietZone.startsWith("disable") ? "false" : "true");
            cfg.addChild(quietCfg);
        }

        // creating human readable configuration according to the new Barcode
        // Element Mappings
        // where the human-readable has children for font name, font size,
        // placement and
        // custom pattern.
        final String barcodePattern = request.getParameter(BARCODE_HUMAN_READABLE_PATTERN);
        final String readableSize = request.getParameter(BARCODE_HUMAN_READABLE_SIZE);
        final String readableFont = request.getParameter(BARCODE_HUMAN_READABLE_FONT);
        final String readablePos = request.getParameter(BARCODE_HUMAN_READABLE_POS);

        if (!(barcodePattern == null && readableSize == null && readableFont == null && readablePos == null)) {
            final Configuration attr = new Configuration("human-readable");
            if (barcodePattern != null)
                attr.addChild(new Configuration("pattern", barcodePattern));
            if (readableSize != null)
                attr.addChild(new Configuration("font-size", readableSize));
            if (readableFont != null)
                attr.addChild(new Configuration("font-name", readableFont));
            if (readablePos != null)
                attr.addChild(new Configuration("placement", readablePos));
            cfg.addChild(attr);
        }

        return cfg;
    }
}
