/*
 * Copyright 2002-2004,2009 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.code128;


import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import com.github.mbhk.barcode4j.Configuration;
import com.github.mbhk.barcode4j.ConfigurationException;

/**
 * This class is an implementation of the Code 128 barcode.
 *
 * @version $Id$
 */
public class Code128 extends ConfigurableBarcodeGenerator {

    /** Create a new instance. */
    public Code128() {
        this.bean = new Code128Bean();
    }

    @Override
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        final Length mw = new Length(cfg.getChild("module-width").getValue("0.21mm"), "mm");
        getCode128Bean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);

        String codesets = cfg.getChild("codesets").getValue(null);
        if (codesets != null) {
            codesets = codesets.toUpperCase();
            Code128Constants bits = null;
            if (codesets.indexOf('A') >= 0) {
                bits = Code128Constants.and(Code128Constants.CODESET_A, bits);
            }
            if (codesets.indexOf('B') >= 0) {
                bits = Code128Constants.and(Code128Constants.CODESET_B, bits);
            }
            if (codesets.indexOf('C') >= 0) {
                bits = Code128Constants.and(Code128Constants.CODESET_C, bits);
            }
            getCode128Bean().setCodeset(bits);
        }
    }

    /**
     * @return the underlying Code128Bean
     */
    public Code128Bean getCode128Bean() {
        return (Code128Bean)getBean();
    }
}
