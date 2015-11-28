/*
 * Copyright 2008,2010 Jeremias Maerki.
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

/* $Id$ */

package org.krysalis.barcode4j.image.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageConverter;
import org.apache.xmlgraphics.image.loader.impl.ImageRawEPS;
import org.apache.xmlgraphics.image.loader.impl.ImageRawStream;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.Orientation;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.tools.PageInfo;
import org.krysalis.barcode4j.tools.VariableUtil;

import com.github.mbhk.barcode4j.Configuration;
import com.github.mbhk.barcode4j.ConfigurationException;

/**
 * This ImageConverter converts barcodes to EPS.
 */
public class ImageConverterBarcode2EPS extends AbstractImageConverter {

    @Override
    public Image convert(Image src, Map hints) throws ImageException, IOException {
        checkSourceFlavor(src);
        final ImageBarcode barcodeImage = (ImageBarcode)src;

        final Configuration cfg = barcodeImage.getBarcodeXML();
        final Orientation orientation = Orientation.fromInt(
                cfg.getAttributeAsInteger("orientation", 0));

        try {
            final String msg = barcodeImage.getMessage();
            final PageInfo pageInfo = PageInfo.fromProcessingHints(hints);
            final String expandedMsg = VariableUtil.getExpandedMessage(pageInfo, msg);

            final BarcodeGenerator bargen = BarcodeUtil.getInstance().
                        createBarcodeGenerator(cfg);

            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            final EPSCanvasProvider canvas = new EPSCanvasProvider(bout, orientation);
            bargen.generateBarcode(canvas, expandedMsg);
            canvas.finish();

            //Create EPS immediately rather than delaying, but create a cacheable EPS image
            final byte[] eps = bout.toByteArray();
            final ImageRawEPS epsImage = new ImageRawEPS(src.getInfo(),
                    new ImageRawStream.ByteArrayStreamFactory(eps));
            return epsImage;
        } catch (ConfigurationException ce) {
            throw new ImageException("Error in Barcode XML", ce);
        } catch (BarcodeException be) {
            throw new ImageException("Error while converting barcode to EPS", be);
        }
    }

    @Override
    public ImageFlavor getSourceFlavor() {
        return ImageBarcode.BARCODE_IMAGE_FLAVOR;
    }

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RAW_EPS;
    }
}
