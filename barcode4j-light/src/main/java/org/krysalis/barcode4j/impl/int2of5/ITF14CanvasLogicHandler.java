/*
 * Copyright 2008 Jeremias Maerki.
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

package org.krysalis.barcode4j.impl.int2of5;

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.DefaultCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Specialized logic handler for ITF-14 (to paint the bearer bar).
 *
 * @version 1.1
 */
public class ITF14CanvasLogicHandler extends DefaultCanvasLogicHandler {

    /**
     * Main constructor.
     * @param bcBean the barcode bean
     * @param canvas the canvas to paint on
     */
    public ITF14CanvasLogicHandler(ITF14Bean bcBean, Canvas canvas) {
        super(bcBean, canvas);
    }

    private ITF14Bean getITF14Bean() {
        return (ITF14Bean)this.bcBean;
    }

    @Override
    public void startBarcode(String msg, String formattedMsg) {
        super.startBarcode(msg, formattedMsg);
        final ITF14Bean bean = getITF14Bean();
        final double bbw = bean.getBearerBarWidth();
        final double w = dimensions.getWidthPlusQuiet();
        final double h = bean.getBarHeight();
        double top = 0;
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            top += bcBean.getHumanReadableHeight();
        }
        canvas.drawRect(0, top, w, top + bbw);
        canvas.drawRect(0, top + bbw + h, w, top + bbw + h + bbw);
        if (bean.isBearerBox()) {
            canvas.drawRect(0, top + bbw, bbw, top + bbw + h);
            canvas.drawRect(w - bbw, top + bbw, w, top + bbw + h);
        }
        //canvas.drawRect(getStartX(), 2 * bbw, getStartX() + dimensions.getWidth(), 3 * bbw);
    }

    @Override
    protected double getStartX() {
        final ITF14Bean bean = getITF14Bean();
        return super.getStartX() + (bean.isBearerBox() ? bean.getBearerBarWidth() : 0);
    }

    @Override
    protected double getStartY() {
        final double y = super.getStartY() + getITF14Bean().getBearerBarWidth();
        return y;
    }

    @Override
    protected double getTextBaselinePosition() {
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            double ty = super.getTextBaselinePosition();
            ty += 2 * getITF14Bean().getBearerBarWidth();
            return ty;
        } else {
            return super.getTextBaselinePosition();
        }
    }
}
