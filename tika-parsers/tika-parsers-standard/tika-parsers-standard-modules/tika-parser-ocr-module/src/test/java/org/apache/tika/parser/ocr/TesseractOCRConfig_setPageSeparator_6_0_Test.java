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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TesseractOCRConfig_setPageSeparator_6_0_Test {

    private TesseractOCRConfig config;

    @BeforeEach
    void setUp() {
        config = new TesseractOCRConfig();
    }

    @Test
    void testSetPageSeparator() {
        // Test with a valid page separator
        String validPageSeparator = "PageSeparator";
        config.setPageSeparator(validPageSeparator);
        assertEquals(validPageSeparator, config.getPageSeparator());
        // Test with an invalid page separator
        String invalidPageSeparator = "Invalid Page Separator";
        assertThrows(IllegalArgumentException.class, () -> config.setPageSeparator(invalidPageSeparator));
    }
}
