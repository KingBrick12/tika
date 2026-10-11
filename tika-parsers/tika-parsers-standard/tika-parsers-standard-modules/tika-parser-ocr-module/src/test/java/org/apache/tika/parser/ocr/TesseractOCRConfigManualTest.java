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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.apache.tika.exception.TikaConfigException;

/**
 * Tests ecrits a la main (tache 2, IFT3913). Chaque test tue au moins un mutant de
 * {@link TesseractOCRConfig} qui n'est detecte ni par les tests originaux ni par les
 * tests generes par ChatUniTest. Les mutants vises sont indiques par numero de ligne
 * et chaque test est documente dans le README.
 */
public class TesseractOCRConfigManualTest {

    private static final String SEP = File.separator;
    private static final String DIR = SEP + "opt" + SEP + "outil";

    // ------------------------------------------------------------------
    // setDensity, ligne 335 : mutants de borne (< devient <=, > devient >=)
    // ------------------------------------------------------------------

    @Test
    public void densityAcceptsBothBounds() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setDensity(150);
        assertEquals(150, config.getDensity());
        config.setDensity(1200);
        assertEquals(1200, config.getDensity());
    }

    // ------------------------------------------------------------------
    // setOutputType(String), ligne 299 : suppression de l'appel setOutputType(TXT)
    // ------------------------------------------------------------------

    @Test
    public void outputTypeStringIsCaseInsensitive() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        assertEquals(TesseractOCRConfig.OUTPUT_TYPE.TXT, config.getOutputType());
        config.setOutputType("hocr");
        assertEquals(TesseractOCRConfig.OUTPUT_TYPE.HOCR, config.getOutputType());
        config.setOutputType("TXT");
        assertEquals(TesseractOCRConfig.OUTPUT_TYPE.TXT, config.getOutputType());
        config.setOutputType("HoCr");
        assertEquals(TesseractOCRConfig.OUTPUT_TYPE.HOCR, config.getOutputType());
    }

    // ------------------------------------------------------------------
    // Accesseurs booleens, lignes 220, 312, 421, 469, 473 : retour force a true ou a false
    // ------------------------------------------------------------------

    @Test
    public void booleanFlagsAreFalseByDefault() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        assertFalse(config.isSkipOcr());
        assertFalse(config.isEnableImagePreprocessing());
        assertFalse(config.isPreserveInterwordSpacing());
        assertFalse(config.isInlineContent());
        assertFalse(config.isPreloadLangs());
        assertFalse(config.isApplyRotation());
    }

    @Test
    public void simpleSettersAreReflectedByGetters() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setSkipOcr(true);
        config.setEnableImagePreprocessing(true);
        config.setPreserveInterwordSpacing(true);
        config.setInlineContent(true);
        config.setPreloadLangs(true);
        config.setApplyRotation(true);
        config.setMinFileSizeToOcr(10L);
        config.setMaxFileSizeToOcr(2048L);
        config.setTimeoutSeconds(45);
        config.setOutputType(TesseractOCRConfig.OUTPUT_TYPE.HOCR);

        assertTrue(config.isSkipOcr());
        assertTrue(config.isEnableImagePreprocessing());
        assertTrue(config.isPreserveInterwordSpacing());
        assertTrue(config.isInlineContent());
        assertTrue(config.isPreloadLangs());
        assertTrue(config.isApplyRotation());
        assertEquals(10L, config.getMinFileSizeToOcr());
        assertEquals(2048L, config.getMaxFileSizeToOcr());
        assertEquals(45, config.getTimeoutSeconds());
        assertEquals(TesseractOCRConfig.OUTPUT_TYPE.HOCR, config.getOutputType());
    }

    // ------------------------------------------------------------------
    // setOtherTesseractConfig, lignes 504 et 506
    // ------------------------------------------------------------------

    @Test
    public void setOtherConfigCopiesEveryEntry() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        Map<String, String> other = new HashMap<>();
        other.put("k1", "v1");
        other.put("k2", "v2");
        config.setOtherTesseractConfig(other);
        assertEquals(other, config.getOtherTesseractConfig());
    }

    @Test
    public void setOtherConfigIgnoresNullMap() {
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.addOtherTesseractConfig("k1", "v1");
        config.setOtherTesseractConfig(null);
        assertEquals(1, config.getOtherTesseractConfig().size());
    }

    // ------------------------------------------------------------------
    // Chemins, lignes 542, 547, 554, 559, 566, 571
    // ------------------------------------------------------------------

    @Test
    public void pathsAreEmptyByDefaultAndEmptyStaysEmpty() throws Exception {
        TesseractOCRConfig config = new TesseractOCRConfig();
        assertEquals("", config.getTesseractPath());
        assertEquals("", config.getTessdataPath());
        assertEquals("", config.getImageMagickPath());
        config.setTesseractPath("");
        config.setTessdataPath("");
        config.setImageMagickPath("");
        assertEquals("", config.getTesseractPath());
        assertEquals("", config.getTessdataPath());
        assertEquals("", config.getImageMagickPath());
    }

    @Test
    public void pathsKeepASingleTrailingSeparator() throws Exception {
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setTesseractPath(DIR + "1" + SEP);
        config.setTessdataPath(DIR + "2" + SEP);
        config.setImageMagickPath(DIR + "3" + SEP);
        assertEquals(DIR + "1" + SEP, config.getTesseractPath());
        assertEquals(DIR + "2" + SEP, config.getTessdataPath());
        assertEquals(DIR + "3" + SEP, config.getImageMagickPath());
    }

    // ------------------------------------------------------------------
    // RuntimeConfig, lignes 593, 600, 607
    // ------------------------------------------------------------------

    @Test
    public void runtimeConfigRejectsNonBlankPaths() {
        TesseractOCRConfig config = new TesseractOCRConfig.RuntimeConfig();
        assertThrows(TikaConfigException.class, () -> config.setTesseractPath(DIR));
        assertThrows(TikaConfigException.class, () -> config.setTessdataPath(DIR));
        assertThrows(TikaConfigException.class, () -> config.setImageMagickPath(DIR));
    }
}
