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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TesseractOCRConfig_addOtherTesseractConfig_41_0_Test {

    @Mock
    private TesseractOCRConfig config;

    @BeforeEach
    void setUp() {
        config = new TesseractOCRConfig();
    }

    @AfterEach
    void tearDown() {
        config = null;
    }

    @Test
    void addOtherTesseractConfig_validKeyAndValue_addsToOtherTesseractConfig() {
        String key = "key";
        String value = "value";
        config.addOtherTesseractConfig(key, value);
        assertEquals(1, config.getOtherTesseractConfig().size());
        assertEquals(value, config.getOtherTesseractConfig().get(key));
    }

    @Test
    void addOtherTesseractConfig_nullKey_throwsIllegalArgumentException() {
        String key = null;
        String value = "value";
        assertThrows(IllegalArgumentException.class, () -> config.addOtherTesseractConfig(key, value));
    }

    @Test
    void addOtherTesseractConfig_nullValue_throwsIllegalArgumentException() {
        String key = "key";
        String value = null;
        assertThrows(IllegalArgumentException.class, () -> config.addOtherTesseractConfig(key, value));
    }

    @Test
    void addOtherTesseractConfig_invalidKey_throwsIllegalArgumentException() {
        String key = "invalid key";
        String value = "value";
        assertThrows(IllegalArgumentException.class, () -> config.addOtherTesseractConfig(key, value));
    }

    @Test
    void addOtherTesseractConfig_invalidValue_throwsIllegalArgumentException() {
        String key = "key";
        String value = "invalid value";
        assertThrows(IllegalArgumentException.class, () -> config.addOtherTesseractConfig(key, value));
    }
}
