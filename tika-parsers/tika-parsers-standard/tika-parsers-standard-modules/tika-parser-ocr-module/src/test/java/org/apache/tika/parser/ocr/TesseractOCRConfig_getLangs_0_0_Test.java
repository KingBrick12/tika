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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class TesseractOCRConfig_getLangs_0_0_Test {

    @Test
    public void testGetLangs() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        Set<String> validLangs = new HashSet<>();
        Set<String> invalidLangs = new HashSet<>();
        config.getLangs("eng", validLangs, invalidLangs);
        assertEquals(1, validLangs.size());
        assertTrue(validLangs.contains("eng"));
        assertEquals(0, invalidLangs.size());
    }

    @Test
    public void testGetLangsWithMultipleLanguages() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        Set<String> validLangs = new HashSet<>();
        Set<String> invalidLangs = new HashSet<>();
        config.getLangs("eng+fra", validLangs, invalidLangs);
        assertEquals(2, validLangs.size());
        assertTrue(validLangs.contains("eng"));
        assertTrue(validLangs.contains("fra"));
        assertEquals(0, invalidLangs.size());
    }

    @Test
    public void testGetLangsWithInvalidLanguage() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        Set<String> validLangs = new HashSet<>();
        Set<String> invalidLangs = new HashSet<>();
        config.getLangs("eng+fra+invalid", validLangs, invalidLangs);
        assertEquals(2, validLangs.size());
        assertTrue(validLangs.contains("eng"));
        assertTrue(validLangs.contains("fra"));
        assertEquals(1, invalidLangs.size());
        assertTrue(invalidLangs.contains("invalid (invalid syntax)"));
    }
}
