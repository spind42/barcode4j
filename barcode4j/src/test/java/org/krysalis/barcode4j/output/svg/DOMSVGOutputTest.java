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
package org.krysalis.barcode4j.output.svg;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.Orientation;
import org.w3c.dom.DocumentFragment;

import com.github.mbhk.barcode4j.Configuration;


/**
 * Test class for SVG output to JDOM.
 * 
 * @author Jeremias Maerki
 * @version $Id$
 */
public class DOMSVGOutputTest {

    @Test
    public void testDOMSVG() throws Exception {
        Configuration cfg = new Configuration("cfg");
        cfg.addChild(new Configuration("intl2of5"));
        BarcodeGenerator gen = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
        SVGCanvasProvider svg = new SVGCanvasProvider(false, Orientation.ZERO);

        //Create Barcode and render it to SVG
        gen.generateBarcode(svg, "123");

        DocumentFragment frag = svg.getDOMFragment();
        assertNotNull(frag);
    }
}
