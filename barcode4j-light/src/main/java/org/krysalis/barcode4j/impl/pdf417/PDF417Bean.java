/*
 * Copyright 2006-2007 Jeremias Maerki or contributors to Barcode4J, as applicable.
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
package org.krysalis.barcode4j.impl.pdf417;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DefaultTwoDimCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.ECIUtil;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * This class is an implementation of the PDF417 barcode.
 *
 * @version 1.1
 */
public class PDF417Bean extends AbstractBarcodeBean {

    /** The default module width for PDF417. */
    protected static final double DEFAULT_MODULE_WIDTH = UnitConv.in2mm(1.0 / 72); //1px at 72dpi

    /** The default wide factor for PDF417. */
    protected static final int DEFAULT_X_TO_Y_FACTOR = 3;

    /** The default column count for PDF417. */
    protected static final int DEFAULT_COLUMN_COUNT = 2;

    private static final double DEFAULT_WIDTH_TO_HEIGHT_RATIO = 3;
    private static final int MAX_ROW_COUNT = 90;
    private static final int MIN_ROW_COUNT = 3;
    private static final int MIN_COLUMN_COUNT = 1;
    private static final int MAX_COLUMN_COUNT = 30;

    private int minRows = MIN_ROW_COUNT;
    private int maxRows = MAX_ROW_COUNT;
    private int minCols = MIN_COLUMN_COUNT;
    private int maxCols = MAX_COLUMN_COUNT;
    private double widthToHeightRatio = DEFAULT_WIDTH_TO_HEIGHT_RATIO;
    private ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.DEFAULT_ERROR_CORRECTION_LEVEL;

    /** Message encoding */
    private String encoding = "Cp437"; //ECI 000000
    private boolean enableECI = false;

    /** Create a new instance. */
    public PDF417Bean() {
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        this.height = DEFAULT_X_TO_Y_FACTOR * moduleWidth;
        setQuietZone(2 * moduleWidth);

        setColumns(DEFAULT_COLUMN_COUNT);
    }
    
    @Override
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        final TwoDimBarcodeLogicHandler handler = new DefaultTwoDimCanvasLogicHandler(
                this, new Canvas(canvas));

        PDF417LogicImpl.generateBarcodeLogic(handler, msg, this);
    }


    @Override
    public BarcodeDimension calcDimensions(String msg) {

        final int sourceCodeWords = PDF417HighLevelEncoder.encodeHighLevel(msg,
                getEncoding(), isECIEnabled()).length();
        final Dimension dimension = PDF417LogicImpl.determineDimensions(this,
                sourceCodeWords);

        if (dimension == null) {
            throw new IllegalArgumentException("Unable to fit message in columns");
        }

        final double width = (17 * dimension.width + 69) * getModuleWidth();
        final double height = getBarHeight() * dimension.height;
        final double qzh = hasQuietZone() ? getQuietZone() : 0;
        final double qzv = hasQuietZone() ? getVerticalQuietZone() : 0;
        return new BarcodeDimension(width, height,
                width + (2 * qzh), height + (2 * qzv),
                qzh, qzv);
    }

    @Override
    public double getBarWidth(int width) {
        return width * moduleWidth;
    }

    /** @return the number of data columns to produce */
    public int getColumns() {
        return minCols;
    }

    /** @return the error correction level (0-8) */
    public ErrorCorrectionLevel getErrorCorrectionLevel() {
        return this.errorCorrectionLevel;
    }

    /**
     * Gets the maxCols.
     * @return Returns the maxCols.
     */
    public int getMaxCols() {
        return maxCols;
    }

    /**
     * Gets the maximum number of columns.
     * @return Returns the maximum number of columns.
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * Gets the minimum number of columns.
     * @return Returns the minimum number of columns.
     */
    public int getMinCols() {
        return minCols;
    }

    /**
     * Gets the minimum number of rows.
     * @return Returns the minimum number of rows.
     */
    public int getMinRows() {
        return minRows;
    }

    /**
     * Returns the height of the rows.
     * @return the row height (in mm)
     */
    public double getRowHeight() {
        return getBarHeight();
    }

    /**
     * Gets the ratio of the barcode width to the height.
     * e.g. a ratio of 5 means the width is 5 times the height
     * @return Returns the ratio of the barcode width to the height
     */
    public double getWidthToHeightRatio() {
        return widthToHeightRatio;
    }

    /**
     * Returns the message encoding.
     * @return the message encoding (default is "ISO-8859-1")
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Indicates whether the generation of ECI sequences is enabled.
     * @return true if generation of ECI sequences is enabled.
     */
    public boolean isECIEnabled() {
        return this.enableECI;
    }

    private void checkValidColumnCount(int cols) {
        if (cols < MIN_COLUMN_COUNT || cols > MAX_COLUMN_COUNT) {
            throw new IllegalArgumentException(
                    "The number of columns must be between 1 and 30");
        }
    }

    private void checkValidRowCount(int rows) {
        if (rows < MIN_ROW_COUNT || rows > MAX_ROW_COUNT) {
            throw new IllegalArgumentException(
                    "The number of rows must be between 3 and 90");
        }
    }

    /**
     * Sets the number of data columns for the barcode. The number of rows will automatically
     * be determined based on the amount of data.
     * @param cols the number of columns
     */
    public final void setColumns(int cols) {
        setMinCols(cols);
        setMaxCols(cols);
    }

    /**
     * Sets the error correction level for the barcode.
     * @param level the error correction level (a value between 0 and 8)
     */
    public void setErrorCorrectionLevel(ErrorCorrectionLevel level) {
        if(level == null) {
            throw new IllegalArgumentException("Level must not be null");
        }
        this.errorCorrectionLevel = level;
    }

    /**
     * Sets the maximum number of columns.
     * @param maxCols the maximum number of columns..
     */
    public void setMaxCols(int maxCols) {
        checkValidColumnCount(maxCols);
        this.maxCols = maxCols;
    }

    /**
     * Sets the maximum number of rows.
     * @param maxRows the maximum number of rows.
     */
    public void setMaxRows(int maxRows) {
        checkValidRowCount(maxRows);
        this.maxRows = maxRows;
    }

    /**
     * Sets the minimum number of columns.
     * @param minCols The minimum number of columns.
     */
    public void setMinCols(int minCols) {
        checkValidColumnCount(minCols);
        this.minCols = minCols;
    }

    /**
     * Sets the minimum of rows.
     * @param minRows the minimum of rows to set.
     */
    public void setMinRows(int minRows) {
        checkValidRowCount(minRows);
        this.minRows = minRows;
    }

    /**
     * Sets the height of the rows.
     * @param height the height of the rows (in mm)
     */
    public void setRowHeight(double height) {
        setBarHeight(height);
    }

    /**
     * Sets the ratio of the barcode width to the height.
     * e.g. a ratio of 5 means the width is 5 times the height
     * @param widthToHeightRatio the ratio of the barcode width to the height
     */
    public void setWidthToHeightRatio(double widthToHeightRatio) {
        this.widthToHeightRatio = widthToHeightRatio;
    }

    /**
     * Sets the message encoding. The value must conform to one of Java's encodings and
     * have a mapping in the ECI registry.
     * @param encoding the message encoding
     */
    public void setEncoding(String encoding) {
        if (ECIUtil.getECIForEncoding(encoding) < 0) {
            throw new IllegalArgumentException("Not a valid encoding: " + encoding);
        }
        this.encoding = encoding;
    }

    /**
     * Controls whether ECI sequences are generated for the output encoding.
     * @param value true to enable the generation of ECI sequences
     */
    public void setECIEnabled(boolean value) {
        this.enableECI = value;
    }

    @Override
    public Collection<String> getAdditionalNames() {
        final Collection<String> res = new ArrayList<String>(0);
        return res;
    }

    @Override
    public String getId() {
        return "pdf417";
    }
}
