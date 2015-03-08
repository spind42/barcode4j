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
package org.krysalis.barcode4j.impl.upcean;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.tools.CheckUtil;

/**
 * This class is an implementation of the UPC-A barcode.
 *
 * @author Jeremias Maerki
 * @version 1.3
 */
public class UPCALogicImpl extends UPCEANLogicImpl {

    private static final int LENGTH_WITHOUT_CHECKSUM = 11;
    private static final int LENGTH_WITH_CHECKSUM = LENGTH_WITHOUT_CHECKSUM + 1;

    /**
     * Main constructor
     *
     * @param mode the checksum mode
     */
    public UPCALogicImpl(ChecksumMode mode) {
        super(mode);
    }

    /**
     * Validates a UPC-A message. The method throws IllegalArgumentExceptions if
     * an invalid message is passed.
     *
     * @param msg the message to validate
     */
    @Override
    public void validateMessage(String msg) {
        super.validateMessage(msg);
        if (!CheckUtil.intervallContains(LENGTH_WITHOUT_CHECKSUM,
                LENGTH_WITH_CHECKSUM, msg.length())) {
            throw new IllegalArgumentException(
                    "Message must be 11 or 12 characters long. Message: " + msg);
        }
    }

    /**
     * Does checksum processing according to the checksum mode.
     *
     * @param msg the message to process
     * @param mode the checksum mode
     * @return the possibly modified message
     */
    public String handleChecksum(String msg, ChecksumMode mode) {
        if (mode == ChecksumMode.CP_AUTO) {
            if (msg.length() == LENGTH_WITHOUT_CHECKSUM) {
                mode = ChecksumMode.CP_ADD;
            } else if (msg.length() == LENGTH_WITH_CHECKSUM) {
                mode = ChecksumMode.CP_CHECK;
            } else {
                //Shouldn't happen because of validateMessage
                throw new RuntimeException("Internal error");
            }
        }
        if (mode == ChecksumMode.CP_ADD) {
            if (msg.length() != LENGTH_WITHOUT_CHECKSUM) {
                throw new IllegalArgumentException(
                        "Message must be 11 characters long");
            }
            return msg + calcChecksum(msg);
        } else if (mode == ChecksumMode.CP_CHECK) {
            if (msg.length() != LENGTH_WITH_CHECKSUM) {
                throw new IllegalArgumentException(
                        "Message must be 12 characters long");
            }
            final char check = msg.charAt(11);
            final char expected = calcChecksum(msg.substring(0, 11));
            if (check != expected) {
                throw new IllegalArgumentException(
                        "Checksum is bad (" + check + "). Expected: " + expected);
            }
            return msg;
        } else if (mode == ChecksumMode.CP_IGNORE) {
            if (msg.length() != LENGTH_WITH_CHECKSUM) {
                throw new IllegalArgumentException(
                        "Message must be 12 characters long");
            }
            return msg;
        } else {
            throw new UnsupportedOperationException(
                    "Unknown checksum mode: " + mode);
        }
    }

    private String handleChecksum(String msg) {
        return handleChecksum(msg, getChecksumMode());
    }

    @Override
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        final String supp = retrieveSupplemental(msg);
        String s = removeSupplemental(msg);
        validateMessage(s);
        s = handleChecksum(s);

        String canonicalMessage = s;
        if (supp != null) {
            canonicalMessage = canonicalMessage + "+" + supp;
        }
        logic.startBarcode(canonicalMessage, canonicalMessage);

        //Left guard
        drawSideGuard(logic);

        //Number system character
        final char lead = s.charAt(0);
        logic.startBarGroup(BarGroup.UPC_EAN_LEAD, String.valueOf(lead));
        encodeChar(logic, lead, LEFT_HAND_A);
        logic.endBarGroup();

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(1, 6));

        //First five data characters
        for (int i = 1; i < 6; i++) {
            encodeChar(logic, s.charAt(i), LEFT_HAND_A);
        }

        logic.endBarGroup();

        //Center guard
        drawCenterGuard(logic);

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(6, 11));

        //Last five data characters
        for (int i = 6; i < 11; i++) {
            encodeChar(logic, s.charAt(i), RIGHT_HAND);
        }

        logic.endBarGroup();

        //Checksum
        final char check = s.charAt(11);
        logic.startBarGroup(BarGroup.UPC_EAN_CHECK, String.valueOf(check));
        encodeChar(logic, check, RIGHT_HAND);
        logic.endBarGroup();

        //Right guard
        drawSideGuard(logic);

        //Optional Supplemental
        if (supp != null) {
            drawSupplemental(logic, supp);
        }
        logic.endBarcode();
    }
}
