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

class TesseractOCRConfig_setResize_32_0_Test {

    @Test
    void testSetResize() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        // Test valid resize values
        for (int i = 1; i < 10; i++) {
            config.setResize(i * 100);
            assertEquals(i * 100, config.getResize());
        }
        // Test invalid resize values
        assertThrows(IllegalArgumentException.class, () -> config.setResize(0));
        assertThrows(IllegalArgumentException.class, () -> config.setResize(1000));
        // Rejected values must not change the last valid value
        assertEquals(900, config.getResize());
    }
}
