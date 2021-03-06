/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.webapp;

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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Error handler servlet for Barcode exceptions.
 *
 * @author Jeremias Maerki
 * @version $Id$
 */
public class BarcodeErrorServlet extends HttpServlet {

    private static final long serialVersionUID = 6515981491896593768L;

    private static final Logger LOGGER = Logger.getLogger(BarcodeErrorServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

        final Throwable t = (Throwable)request.getAttribute("javax.servlet.error.exception");
        try {
            final SAXTransformerFactory factory = (SAXTransformerFactory)TransformerFactory.newInstance();
            final java.net.URL xslt = getServletContext().getResource("/WEB-INF/exception2svg.xslt");
            TransformerHandler thandler;
            if (xslt != null) {
                LOGGER.log(Level.FINE, xslt.toExternalForm());
                final Source xsltSource = new StreamSource(xslt.toExternalForm());
                thandler = factory.newTransformerHandler(xsltSource);
                response.setContentType("image/svg+xml");
            } else {
                LOGGER.log(Level.SEVERE, "Exception stylesheet not found, sending back raw XML");
                thandler = factory.newTransformerHandler();
                response.setContentType("application/xml");
            }

            final ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
            try {
                final Result res = new javax.xml.transform.stream.StreamResult(bout);
                thandler.setResult(res);
                generateSAX(t, thandler);
            } finally {
                bout.close();
            }

            response.setContentLength(bout.size());
            response.getOutputStream().write(bout.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in error servlet", e);
            throw new ServletException(e);
        }
    }

    private void generateSAX(Throwable t, ContentHandler handler) throws SAXException {
        if (t == null) {
            throw new NullPointerException("Throwable must not be null");
        }
        if (handler == null) {
            throw new NullPointerException("ContentHandler not set");
        }

        handler.startDocument();
        generateSAXForException(t, handler, "exception");
        handler.endDocument();
    }

    private void generateSAXForException(Throwable t,
                ContentHandler handler, String elName) throws SAXException {
        final AttributesImpl attr = new AttributesImpl();
        attr.addAttribute(null, "classname", "classname", "CDATA", t.getClass().getName());
        handler.startElement(null, elName, elName, attr);
        attr.clear();
        handler.startElement(null, "msg", "msg", attr);
        final char[] chars = t.getMessage().toCharArray();
        handler.characters(chars, 0, chars.length);
        handler.endElement(null, "msg", "msg");

        handler.endElement(null, elName, elName);
    }
}
