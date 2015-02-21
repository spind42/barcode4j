/*
 * Copyright 2006 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Default 2D Logic Handler implementation for painting on a Canvas.
 * 
 * @author Jeremias Maerki
 * @version 1.2
 */
public class DefaultTwoDimCanvasLogicHandler implements TwoDimBarcodeLogicHandler {
    
    private final AbstractBarcodeBean bcBean;
    private final Canvas canvas;
    private double x = 0.0;
    private double y = 0.0;
    
    /**
     * Main constructor.
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public DefaultTwoDimCanvasLogicHandler(AbstractBarcodeBean bcBean, Canvas canvas) {
        this.bcBean = bcBean;
        this.canvas = canvas;
    }
    
    private double getStartX() {
        if (bcBean.hasQuietZone()) {
            return bcBean.getQuietZone();
        } else {
            return 0.0;
        }
    }            

    private double getStartY() {
        if (bcBean.hasQuietZone()) {
            return bcBean.getVerticalQuietZone();
        } else {
            return 0.0;
        }
    }            

    @Override
    public void startBarcode(String msg, String formattedMsg) {
        //Calculate extents
        final BarcodeDimension dim = bcBean.calcDimensions(msg);
        
        canvas.establishDimensions(dim);
        y = getStartY();
    }

    @Override
    public void startRow() {
        x = getStartX();
    }

    @Override
    public void startBarGroup(BarGroup type, String submsg) {
        //nop
    }

    @Override
    public void addBar(boolean black, int width) {
        final double w = bcBean.getBarWidth(width);
        if (black) {
            canvas.drawRectWH(x, y, w, bcBean.getBarHeight());
        }
        x += w;
    }

    @Override
    public void endBarGroup() {
        //nop
    }

    @Override
    public void endRow() {
        y += bcBean.getBarHeight(); //=row height
    }

    @Override
    public void endBarcode() {
        //nop
    }
}