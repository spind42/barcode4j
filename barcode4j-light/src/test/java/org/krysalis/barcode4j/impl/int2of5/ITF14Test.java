/*
 * Copyright 2009 Jeremias Maerki.
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

import static org.junit.Assert.*;
import org.junit.Test;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;

/**
 * Tests for ITF-14
 *
 * @version $Id$
 */
public class ITF14Test {

    @Test
    public void testChecksum() throws Exception {
        StringBuilder sb = new StringBuilder();
        ITF14LogicImpl logic;
        String expected = "<BC><SBG:start-char:null></SBG>"
            + "<SBG:msg-char:15></SBG>"
            + "<SBG:msg-char:40></SBG>"
            + "<SBG:msg-char:01></SBG>"
            + "<SBG:msg-char:41></SBG>"
            + "<SBG:msg-char:28></SBG>"
            + "<SBG:msg-char:87></SBG>"
            + "<SBG:msg-char:63></SBG>"
            + "<SBG:stop-char:null></SBG>"
            + "</BC>";

        logic = new ITF14LogicImpl(ChecksumMode.CP_AUTO, true);
        checkNegative("154001412887", logic, sb); //only 12 digits
        checkPositive("1540014128876", logic, sb, expected);
        checkPositive("15400141288763", logic, sb, expected);
        checkNegative("154001412887634", logic, sb); //too many digits

        logic = new ITF14LogicImpl(ChecksumMode.CP_ADD, true);
        checkNegative("154001412887", logic, sb); //only 12 digits
        checkPositive("1540014128876", logic, sb, expected);
        checkNegative("15400141288763", logic, sb);
        checkNegative("154001412887634", logic, sb); //too many digits

        logic = new ITF14LogicImpl(ChecksumMode.CP_CHECK, true);
        checkNegative("154001412887", logic, sb); //only 12 digits
        checkNegative("1540014128876", logic, sb);
        checkPositive("15400141288763", logic, sb, expected);
        checkNegative("154001412887634", logic, sb); //too many digits

        logic = new ITF14LogicImpl(ChecksumMode.CP_IGNORE, true);
        checkNegative("154001412887", logic, sb); //only 12 digits
        checkNegative("1540014128876", logic, sb);
        checkPositive("15400141288763", logic, sb, expected);
        checkNegative("154001412887634", logic, sb); //too many digits
    }

    private void checkPositive(String msg, Interleaved2Of5LogicImpl logic,
            StringBuilder sb, String expected) {
        sb.setLength(0);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, false, false),
                msg);
        assertEquals(expected, sb.toString());
    }

    private void checkNegative(String msg, Interleaved2Of5LogicImpl logic, StringBuilder sb) {
        try {
            sb.setLength(0);
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, false, false),
                    msg);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            //expected
        }
    }
}
