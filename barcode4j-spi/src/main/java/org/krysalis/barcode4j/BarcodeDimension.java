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
package org.krysalis.barcode4j;

import java.awt.geom.Rectangle2D;
import org.krysalis.barcode4j.output.Orientation;

/**
 * This class provides information on the dimensions of a barcode. It makes a
 * distinction between the dimensions with and without quiet zone.
 *
 * @author Jeremias Maerki
 * @version $Id$
 */
public class BarcodeDimension {

    private final double width;
    private final double height;

    private final double widthPlusQuiet;
    private final double heightPlusQuiet;
    private final double xOffset;
    private final double yOffset;

    /**
     * Creates a new BarcodeDimension object. No quiet-zone is respected.
     *
     * @param w width of the barcode in millimeters (mm).
     * @param h height of the barcode in millimeters (mm).
     */
    public BarcodeDimension(double w, double h) {
        this(w, h, w, h, 0.0, 0.0);
    }

    /**
     * Creates a new BarcodeDimension object.
     *
     * @param w width of the raw barcode (without quiet-zone) in millimeters
     * (mm).
     * @param h height of the raw barcode (without quiet-zone) in millimeters
     * (mm).
     * @param wpq width of the barcode (quiet-zone included) in millimeters
     * (mm).
     * @param hpq height of the barcode (quiet-zone included) in millimeters
     * (mm).
     * @param xoffset x-offset if the upper-left corner of the barcode within
     * the extended barcode area.
     * @param yoffset y-offset if the upper-left corner of the barcode within
     * the extended barcode area.
     */
    public BarcodeDimension(double w, double h,
            double wpq, double hpq,
            double xoffset, double yoffset) {
        this.width = w;
        this.height = h;
        this.widthPlusQuiet = wpq;
        this.heightPlusQuiet = hpq;
        this.xOffset = xoffset;
        this.yOffset = yoffset;
    }

    /**
     * Returns the height of the barcode (ignores quiet-zone).
     *
     * @return height in millimeters (mm)
     */
    public double getHeight() {
        return height;
    }

    public double getHeight(Orientation orientation) {
        if (orientation.isSwitched()) {
            return getWidth();
        } else {
            return getHeight();
        }
    }

    /**
     * Returns the height of the barcode (quiet-zone included).
     *
     * @return height in millimeters (mm)
     */
    public double getHeightPlusQuiet() {
        return heightPlusQuiet;
    }

    public double getHeightPlusQuiet(Orientation orientation) {
        if (orientation.isSwitched()) {
            return getWidthPlusQuiet();
        } else {
            return getHeightPlusQuiet();
        }
    }

    /**
     * Returns the width of the barcode (ignores quiet-zone).
     *
     * @return width in millimeters (mm)
     */
    public double getWidth() {
        return width;
    }

    public double getWidth(Orientation orientation) {
        if (orientation.isSwitched()) {
            return getHeight();
        } else {
            return getWidth();
        }
    }

    /**
     * Returns the width of the barcode (quiet-zone included).
     *
     * @return width in millimeters (mm)
     */
    public double getWidthPlusQuiet() {
        return widthPlusQuiet;
    }

    public double getWidthPlusQuiet(Orientation orientation) {
        if (orientation.isSwitched()) {
            return getHeightPlusQuiet();
        } else {
            return getWidthPlusQuiet();
        }
    }

    /**
     * Returns the x-offset of the upper-left corner of the barcode within the
     * extended barcode area.
     *
     * @return double x-offset in millimeters (mm)
     */
    public double getXOffset() {
        return xOffset;
    }

    /**
     * Returns the y-offset of the upper-left corner of the barcode within the
     * extended barcode area.
     *
     * @return double y-offset in millimeters (mm)
     */
    public double getYOffset() {
        return yOffset;
    }

    /**
     * @return a bounding rectangle (including quiet zone if applicable)
     */
    public Rectangle2D getBoundingRect() {
        return new Rectangle2D.Double(
                0, 0, getWidthPlusQuiet(), getHeightPlusQuiet());
    }

    /**
     * @return a content rectangle (excluding quiet zone)
     */
    public Rectangle2D getContentRect() {
        return new Rectangle2D.Double(
                getXOffset(), getYOffset(), getWidth(), getHeight());
    }

    @Override
    public String toString() {
        return new StringBuilder(50).append("[width=")
                .append(getWidth())
                .append("(")
                .append(getWidthPlusQuiet())
                .append("),height=")
                .append(getHeight())
                .append("(")
                .append(getHeightPlusQuiet())
                .append(")]").toString();
    }
}
