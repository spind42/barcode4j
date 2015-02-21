/*
 * Copyright 2015 mk.
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import junit.framework.TestCase;

/**
 *
 * @author mk
 */
public class ChecksumModeTest extends TestCase {

    public ChecksumModeTest(String testName) {
        super(testName);
    }

    /**
     * Test of getName method, of class ChecksumMode.
     */
    public void testGetName() {
        System.out.println("getName");
        assertEquals("auto", ChecksumMode.CP_AUTO.getName());
    }

    /**
     * Test of byName method, of class ChecksumMode.
     */
    public void testByName() {
        System.out.println("byName");
        assertEquals(ChecksumMode.CP_IGNORE, ChecksumMode.byName("ignore"));

        try {
            ChecksumMode.byName("foo");
            fail("foo is no ChecksumMode");
        } catch (IllegalArgumentException e) {
        }
    }
}