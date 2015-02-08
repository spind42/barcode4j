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

package org.krysalis.barcode4j.impl.datamatrix;

/**
 * Constants for DataMatrix.
 * 
 * @version 1.2
 */
class DataMatrixConstants {

    /** Padding character */
    public static final char PAD = 129;
    /** mode latch to C40 encodation mode */
    public static final char LATCH_TO_C40 = 230;
    /** mode latch to Base 256 encodation mode */
    public static final char LATCH_TO_BASE256 = 231;
    /** FNC1 Codeword */
    public static final char FNC1 = 232;
    /** Structured Append Codeword */
    public static final char STRUCTURED_APPEND = 233;
    /** Reader Programming */
    public static final char READER_PROGRAMMING = 234;
    /** Upper Shift */
    public static final char UPPER_SHIFT = 235;
    /** 05 Macro */
    public static final char MACRO_05 = 236;
    /** 06 Macro */
    public static final char MACRO_06 = 237;
    /** mode latch to ANSI X.12 encodation mode */
    public static final char LATCH_TO_ANSIX12 = 238;
    /** mode latch to Text encodation mode */
    public static final char LATCH_TO_TEXT = 239;
    /** mode latch to EDIFACT encodation mode */
    public static final char LATCH_TO_EDIFACT = 240;
    /** ECI character (Extended Channel Interpretation) */
    public static final char ECI = 241;
    
    /** Unlatch from C40 encodation */
    public static final char C40_UNLATCH = 254;
    /** Unlatch from X12 encodation */
    public static final char X12_UNLATCH = 254;
    
    /** 05 Macro header */
    public static final String MACRO_05_HEADER = "[)>\u001E05\u001D";
    /** 06 Macro header */
    public static final String MACRO_06_HEADER = "[)>\u001E06\u001D";
    /** Macro trailer */
    public static final String MACRO_TRAILER = "\u001E\u0004";

    private DataMatrixConstants() {
    }
}
