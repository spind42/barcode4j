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

/* $Id$ */

package org.krysalis.barcode4j.tools;

/**
 * Helper methods for testing.
 * 
 * @version $Id$
 */
public class TestHelper {

    /**
     * Convert a string of char codewords into a different string which lists each character 
     * using its decimal value.
     * @param codewords the codewords 
     * @return the visualized codewords
     */
    public static String visualize(String codewords) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < codewords.length(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append((int)codewords.charAt(i));
        }
        return sb.toString();
    }
    
}