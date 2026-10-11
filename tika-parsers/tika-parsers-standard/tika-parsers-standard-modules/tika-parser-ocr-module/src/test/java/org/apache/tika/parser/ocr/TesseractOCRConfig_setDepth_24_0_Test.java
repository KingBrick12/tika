/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.parser.ocr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TesseractOCRConfig_setDepth_24_0_Test {

    @Test
    public void testSetDepth() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        int[] allowedValues = { 2, 4, 8, 16, 32, 64, 256, 4096 };
        for (int allowedValue : allowedValues) {
            config.setDepth(allowedValue);
            assertEquals(allowedValue, config.getDepth());
        }
    }

    @Test
    public void testSetDepthInvalidValue() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        int invalidValue = 1;
        assertThrows(IllegalArgumentException.class, () -> config.setDepth(invalidValue));
    }
}
