# IFT3913 – Tâche 2 : tests générés par IA et analyse de mutation

**Auteur :** Brice Bakoup Wafo ([KingBrick12](https://github.com/KingBrick12)), travail individuel

**Cas d'étude :** Apache Tika, module `tika-parser-ocr-module`

Dans ce document, `MODULE` désigne
[`tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module`](tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module)
et `TESTS` désigne
[`MODULE/src/test/java/org/apache/tika/parser/ocr`](tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module/src/test/java/org/apache/tika/parser/ocr).

## Résumé

| Suite de tests | Mutants tués | Score de mutation |
|---|---|---|
| Tests originaux | 34 / 70 | 49 % |
| Originaux + tests générés par ChatUniTest | 44 / 70 | 63 % |
| Originaux + générés + tests manuels | 70 / 70 | 100 % |

Les tests générés tuent 10 mutants de plus que les tests originaux, mais ne détectent pas tous les
mutants : il en reste 26. Neuf tests écrits à la main tuent ces 26 mutants.

## 1. Classe choisie

Classe : `org.apache.tika.parser.ocr.TesseractOCRConfig`, avec sa classe interne `RuntimeConfig`
([source](tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module/src/main/java/org/apache/tika/parser/ocr/TesseractOCRConfig.java)).
Tests existants : `TesseractOCRConfigTest` (19 tests).

Justification, d'après la première analyse pitest (tests originaux seulement) :

- **La classe n'est pas couverte à 100 %** : 119 lignes sur 183 (65 %).
- **Elle a des mutants vivants** : sur 70 mutants, 34 sont tués, **11 survivent** et **25 ne sont
  couverts par aucun test**.
- **Méthodes jamais exécutées par les tests originaux** : `getPageSeparator`, `setPageSeparator`,
  `isPreserveInterwordSpacing`, `getOutputType`, `isSkipOcr`, `isInlineContent`,
  `getOtherTesseractConfig`, `setOtherTesseractConfig`, `getTessdataPath`, et les trois setters de
  `RuntimeConfig`. Dans `setTesseractPath`, `setTessdataPath` et `setImageMagickPath`, la condition
  qui ajoute le séparateur final n'est atteinte par aucun test : les deux tests de chemin existants
  provoquent une exception avant, et `setImageMagickPath` n'est jamais appelée.
- **Mutants survivants malgré la couverture** : les deux mutants de borne de `setDensity` (ligne 335),
  `setColorspace` (377), `setOutputType(String)` (298 et 301), `isEnableImagePreprocessing` (312),
  `isPreloadLangs` (473), `getTesseractPath` (542) et `getImageMagickPath` (566).
- **Certains tests originaux ont un oracle trivial.** Par exemple `testValidateDensity` appelle
  `setDensity(300)` et `setDensity(400)`, puis vérifie `assertTrue(true, ...)` : rien n'est vérifié
  sur les valeurs acceptées ni sur les bornes 150 et 1200.

La classe ne contient que de la validation de paramètres. Elle ne dépend pas de Tesseract, donc ses
tests s'exécutent sur n'importe quelle machine, y compris dans la GitHub Action.

## 2. Installation de ChatUniTest

Dans le [`pom.xml` du module](tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module/pom.xml) :

- plugin `io.github.zju-aces-ise:chatunitest-maven-plugin:2.1.1`, configuré pour un modèle local
  (`url` = `http://localhost:11434/v1/chat/completions`, `model` = `code-llama`) ;
- dépendance `io.github.ZJU-ACES-ISE:chatunitest-starter:1.4.0`, placée dans un profil Maven
  `chatunitest` pour que le build normal ne dépende pas de ChatUniTest ;
- dépendances `mockito-core` et `mockito-junit-jupiter` (portée `test`), car les tests générés
  utilisent l'extension Mockito.

Modèle : `codellama:7b` exécuté localement avec Ollama 0.35.1, sous l'alias `code-llama`
(`ollama cp codellama:7b code-llama`). Le modèle tournait entièrement sur le processeur (100 % CPU,
contexte de 4096 jetons). Machine : Machine : Acer Aspire 5, Intel Core i5-1135G7, 16 Go de RAM, sans carte graphique dédiée..

Commande, exécutée depuis `MODULE` pour chaque méthode ciblée :

```bash
mvn -Pchatunitest test-compile chatunitest:method "-DselectMethod=TesseractOCRConfig#setDensity" -Dthread=false -Denforcer.skip=true
```

Problèmes rencontrés pendant l'installation, et réglages associés :

| Problème | Cause | Solution |
|---|---|---|
| Erreurs HTTP 500 d'Ollama | ChatUniTest envoie plusieurs requêtes en parallèle (jusqu'à 80 fils sur cette machine) | `-Dthread=false` |
| Tous les tests générés échouaient à la compilation (`package org.junit.jupiter.api does not exist`) | La dépendance `chatunitest-starter` n'était pas résolue : son `groupId` s'écrit avec des majuscules (`io.github.ZJU-ACES-ISE`). Le plugin compilait alors avec un classpath vide, sans le signaler | Corriger le `groupId` et lancer `test-compile` dans la même commande |
| (Précaution) contrôle de convergence des dépendances de Tika | Le profil `chatunitest` ajoute de nombreuses dépendances transitives, que les règles strictes de Tika pourraient refuser | `-Denforcer.skip=true`, seulement pendant la génération |
| `SocketTimeoutException` | Modèle 7B sur CPU, trop lent pour certaines requêtes de réparation | Aucune ; la tentative est comptée comme un échec |

La génération sur la classe entière (une cinquantaine de méthodes) aurait pris plusieurs heures sur
CPU. Elle a donc été ciblée sur dix méthodes où la première analyse montrait des mutants vivants ou
non couverts. Durées relevées : 4 min 33 s pour `setDensity`, 7 min 36 s pour `setTesseractPath`.

## 3. Tests générés

**Où sont-ils ?** Dans `TESTS`, ce sont les 11 fichiers nommés
`TesseractOCRConfig_<méthode>_<n>_<m>_Test.java` (23 tests au total). `setOutputType` a deux
fichiers parce que la méthode a deux surcharges.

### Compilent-ils et s'exécutent-ils sans intervention ?

D'après ChatUniTest, qui compile, exécute et répare lui-même ses tests (jusqu'à 5 tentatives) :

| Méthode | Résultat annoncé | Tentatives |
|---|---|---|
| `setDensity`, `setResize`, `setDepth`, `setPageSegMode`, `setPageSeparator`, `setColorspace`, `addOtherTesseractConfig` | compile et passe | 1 |
| `getLangs` | compile et passe | 2 (1 échec de compilation) |
| `setTesseractPath` | compile et passe | 2 (1 échec de compilation) |
| `setOutputType`, 1ᵉʳ essai | compile et passe | 4 (2 échecs de compilation, 1 réponse sans code exploitable) |
| `setOutputType`, 2ᵉ essai | abandonné | 5 échecs, dont 2 dépassements de délai |
| `setOutputType`, 3ᵉ essai | compile et passe | 1 |

Dans le vrai build Maven du module, le résultat est moins bon que ce que l'outil annonce :

| Question | Réponse |
|---|---|
| Les 11 classes compilent-elles sans intervention ? | Oui, une fois Mockito ajouté au module |
| Les 23 tests passent-ils sans intervention ? | Non : 19 passent, **4 échouent**, dans 3 classes pour lesquelles ChatUniTest annonçait un succès |
| Le build complet de Tika passe-t-il ? | Non : Checkstyle refuse les imports avec `*` présents dans les 11 fichiers |

### Corrections manuelles

**Quatre corrections de fond**, toutes sur un oracle ou une donnée de test faux :

| Test | Généré | Corrigé | Pourquoi le test généré était faux |
|---|---|---|---|
| `addOtherTesseractConfig_invalidKey_throwsIllegalArgumentException` | clé `"invalid"` | clé `"invalid key"` | `"invalid"` respecte le motif `^[-_/\.A-Z0-9]+$` (insensible à la casse) : aucune exception n'est levée. L'espace, lui, est interdit |
| `addOtherTesseractConfig_invalidValue_throwsIllegalArgumentException` | valeur `"invalid"` | valeur `"invalid value"` | même raison |
| `testSetColorspaceInvalidPattern` | `"invalid-colorspace"` | `"invalid colorspace"` | le tiret est permis par le motif `^[-_A-Z0-9]+$` |
| `testSetResize` | `assertEquals(200, config.getResize())` | `assertEquals(900, ...)` | le test vient de fixer 900 dans une boucle ; les deux valeurs refusées ensuite ne modifient pas l'état. Le modèle a supposé que la valeur par défaut était restaurée |

**Corrections de forme, dans les 11 fichiers**, sans toucher au corps des tests : imports avec `*`
remplacés par des imports explicites, et retrait d'une vingtaine d'imports inutiles par fichier
(recopiés de la classe testée : `Logger`, `Pattern`, `FilenameUtils`...), comme l'exige Checkstyle.
L'en-tête de licence est celui que le build de Tika (Spotless) impose à tous les fichiers.

### Critique des oracles générés

**Pertinence.** Les tests sont bien nommés et suivent presque tous le même schéma : une valeur
valide relue par le getter, puis une valeur invalide avec `assertThrows`. Ce schéma convient à une
classe de validation. Le meilleur exemple est `testSetDepth`, qui parcourt les 8 valeurs permises
et vérifie le getter à chaque fois. Il est plus fort que le test original `testValidateDepth`, qui
appelle `setDepth(4)` et `setDepth(8)` puis vérifie `assertTrue(true)`.

**Spécificité.** Les assertions sont précises quand elles existent : `assertEquals` sur une valeur
exacte, `assertThrows` sur `IllegalArgumentException`, et même le contenu exact de l'ensemble des
langues invalides (`"invalid (invalid syntax)"`). Aucun test ne se contente d'un `assertNotNull`.
En revanche, les **données sont choisies loin des bornes**. `testSetDensity` utilise 300 (valide)
et 100 (invalide), jamais 150 ni 1200. Les deux mutants de borne de la ligne 335 survivent donc,
exactement comme avec les tests originaux.

**Vérifications triviales.** Plusieurs tests écrivent la valeur par défaut puis la relisent :
`setColorspace("gray")`, `setOutputType(OUTPUT_TYPE.TXT)`, `setOutputType("txt")`,
`setPageSegMode("1")`, `setTesseractPath("")`. Un tel test passe même si le setter ne fait rien.
L'analyse de mutation le confirme : le mutant de la ligne 299, qui supprime l'appel
`setOutputType(OUTPUT_TYPE.TXT)`, est exécuté par `setOutputType_txt` mais **survit**, parce que
`TXT` est déjà la valeur par défaut.

**Oracles faux.** 4 tests sur 23 (17 %) vérifient un comportement que le code n'a pas. Dans trois
cas, le modèle a supposé qu'une chaîne appelée « invalid » était invalide, sans tenir compte de
l'expression régulière de la classe testée. Dans le quatrième, il a inventé une remise à la valeur
par défaut. Ces erreurs sont dangereuses : si on les « corrigeait » en modifiant le code plutôt que
le test, on introduirait un bogue.

**Bruit.** Le code généré contient des éléments inutiles : `@ExtendWith(MockitoExtension.class)`
sans aucun mock, un champ `@Mock` aussitôt remplacé par un vrai objet dans `setUp`, des méthodes
`tearDown` qui mettent une variable à `null`, l'appel de la méthode statique `getLangs` à travers
une instance, et plusieurs comportements regroupés dans un seul test (`testSetPageSegMode`,
`testSetResize`), ce qui rend un échec plus difficile à diagnostiquer.

**Comparaison avec les tests écrits à la main.** Les tests originaux sont inégaux : cinq tests
`testValidate...` ont un oracle trivial (`assertTrue(true)`), et `testGoodOtherParameters` n'a
aucune assertion. En revanche, leurs données sont choisies avec une connaissance du domaine que le
modèle n'a pas montrée : codes de langue réels (`chi_tra_vert`, `script/HanT_vert`), tentative
d'injection de commande (`rm -rf *`), octet nul dans un chemin, chargement d'une configuration JSON
complète. Les tests générés vérifient mieux la valeur stockée après un setter ; les tests humains
choisissent mieux les données qui comptent.

## 4. Analyse de mutation

Pitest 1.30.0 avec le plugin JUnit 5 1.2.3, mutateurs par défaut, classes ciblées :
`org.apache.tika.parser.ocr.TesseractOCRConfig*`.

| Suite de tests | Mutants | Tués | Survivants | Sans couverture | Score | Force des tests | Lignes couvertes |
|---|---|---|---|---|---|---|---|
| Tests originaux | 70 | 34 | 11 | 25 | 49 % | 76 % | 119/183 (65 %) |
| Originaux + générés | 70 | 44 | 9 | 17 | 63 % | 83 % | 140/183 (77 %) |
| Originaux + générés + manuels | 70 | 70 | 0 | 0 | 100 % | 100 % | 175/183 (96 %) |

### Les tests générés détectent-ils tous les mutants ?

Non. Ils en détectent 10 de plus que les tests originaux (7 qui n'étaient pas couverts et 3 qui
survivaient), mais 26 mutants restent non détectés.

### Mutants détectés par les tests générés, et pourquoi

| Ligne | Code | Mutation | État avant | Test généré qui le tue | Raison de la détection |
|---|---|---|---|---|---|
| 180 | `return pageSeparator;` | retourne `""` | non couvert | `..._setPageSeparator_6_0_Test.testSetPageSeparator` | Le test fixe `"PageSeparator"` puis compare le getter à cette valeur ; `""` fait échouer `assertEquals` |
| 195 | `if (pageSeparator.isBlank())` | condition inversée | non couvert | même test | La méthode sort alors tout de suite pour une valeur non vide : le séparateur n'est jamais enregistré et `assertEquals` échoue |
| 199 | `if (!m.find())` | condition inversée | non couvert | même test | La valeur valide `"PageSeparator"` déclenche l'exception réservée aux valeurs invalides ; le test échoue sur cette exception inattendue |
| 203 | `setTrustedPageSeparator(pageSeparator);` | appel supprimé | non couvert | même test | La valeur n'est plus stockée ; le getter rend `""` au lieu de `"PageSeparator"` |
| 282 | `return outputType;` | retourne `null` | non couvert | `..._setOutputType_17_0_Test.testSetOutputType` | `assertEquals(OUTPUT_TYPE.TXT, null)` échoue |
| 298 | `if ("txt".equals(lc))` | condition inversée | survivant | `..._setOutputType_18_1_Test.setOutputType_txt` | `"txt"` n'entre plus dans la première branche, n'est pas `"hocr"`, et tombe dans le `else` qui lève `IllegalArgumentException` |
| 301 | `setOutputType(OUTPUT_TYPE.HOCR);` | appel supprimé | survivant | `..._setOutputType_18_1_Test.setOutputType_hocr` | Le type reste à sa valeur par défaut `TXT` ; `assertEquals(HOCR, ...)` échoue |
| 377 | `if (colorspace == null)` | condition inversée | survivant | `..._setColorspace_26_0_Test.testSetColorspace` | Toute valeur non nulle lève alors l'exception « cannot be null » ; `setColorspace("gray")` échoue |
| 493 | `return otherTesseractConfig;` | retourne une map vide | non couvert | `..._addOtherTesseractConfig_41_0_Test.addOtherTesseractConfig_validKeyAndValue_addsToOtherTesseractConfig` | Le test attend une taille de 1 après un ajout ; la map vide donne 0 |
| 547 | `if (!tesseractPath.isEmpty() && ...)` | première condition inversée | non couvert | `..._setTesseractPath_43_0_Test.testSetTesseractPath_Empty` | Avec un chemin vide, la condition inversée ajoute un séparateur ; le getter rend le séparateur seul au lieu de `""` |

Pourquoi les tests originaux ne tuaient pas ces mutants :

- lignes 180 à 203, 282 et 493 : aucun test original n'appelait ces méthodes ;
- ligne 547 : le seul test original de `setTesseractPath` passe un chemin contenant un octet nul,
  ce qui lève une exception avant la condition ;
- ligne 377 : le seul test original de `setColorspace` passe une valeur invalide, qui lève une
  exception avec ou sans la mutation ;
- lignes 298 et 301 : elles n'étaient atteintes que par le chargement d'un fichier de
  configuration, sans aucune assertion sur le type de sortie.

### Mutants non détectés par les tests générés

| Lignes | Méthode | Mutation | État | Pourquoi les tests générés ne le tuent pas |
|---|---|---|---|---|
| 335 (2 mutants) | `setDensity` | borne `<` → `<=`, `>` → `>=` | survivant | Données 300 et 100 : aucune valeur exactement à 150 ou à 1200 |
| 299 | `setOutputType(String)` | appel `setOutputType(TXT)` supprimé | survivant | Le test passe `"txt"` sur un objet neuf, dont le type vaut déjà `TXT` |
| 312, 473 (2 mutants chacune) | `isEnableImagePreprocessing`, `isPreloadLangs` | retourne toujours `true` / `false` | survivant | Méthodes non ciblées par la génération ; elles sont appelées pendant le chargement de configuration, mais aucun test ne vérifie leur résultat |
| 542, 566 | `getTesseractPath`, `getImageMagickPath` | retourne `""` | survivant | `getTesseractPath` n'est vérifié qu'avec un chemin vide, pour lequel `""` est aussi la valeur attendue ; `getImageMagickPath` n'est pas ciblé |
| 547 | `setTesseractPath` | `!endsWith(...)` inversée | non couvert | Avec un chemin vide, la deuxième condition n'est jamais évaluée (court-circuit du `&&`) |
| 220, 421, 469 (2 mutants chacune) | `isPreserveInterwordSpacing`, `isSkipOcr`, `isInlineContent` | retourne toujours `true` / `false` | non couvert | Méthodes non ciblées par la génération |
| 504, 506 | `setOtherTesseractConfig` | condition inversée, appel supprimé | non couvert | Méthode non ciblée |
| 554, 559 (2 mutants), 571 (2 mutants) | `getTessdataPath`, `setTessdataPath`, `setImageMagickPath` | retour `""`, conditions inversées | non couvert | Méthodes non ciblées |
| 593, 600, 607 | `RuntimeConfig.set...Path` | condition inversée | non couvert | Classe interne non ciblée |

Total : 9 survivants et 17 non couverts, soit 26 mutants.

## 5. Tests ajoutés à la main

Fichier : [`TesseractOCRConfigManualTest.java`](tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module/src/test/java/org/apache/tika/parser/ocr/TesseractOCRConfigManualTest.java),
9 tests. Chaque test tue au moins un des 26 mutants restants ; ensemble, ils les tuent tous
(score final : 70/70).

### 5.1 `densityAcceptsBothBounds`

- **Mutants visés :** ligne 335, les deux mutants de borne.
- **Intention :** vérifier que les deux extrémités de la plage de densité sont acceptées.
- **Données :** 150 et 1200, les valeurs exactement égales aux bornes de
  `density < 150 || density > 1200`. Ce sont les seules valeurs pour lesquelles `<` et `<=`
  (ou `>` et `>=`) donnent un résultat différent ; toute autre valeur laisse survivre le mutant.
- **Oracle :** le message d'erreur du code annonce « Valid range of values is 150-1200 », donc une
  plage inclusive : aucune exception ne doit être levée et `getDensity()` doit rendre la valeur donnée.

### 5.2 `outputTypeStringIsCaseInsensitive`

- **Mutant visé :** ligne 299, suppression de l'appel `setOutputType(OUTPUT_TYPE.TXT)`.
- **Intention :** vérifier la conversion d'une chaîne en type de sortie, sans égard à la casse.
- **Données :** `"hocr"`, puis `"TXT"`, puis `"HoCr"`. L'ordre est essentiel : on quitte d'abord la
  valeur par défaut `TXT`, pour que le retour à `TXT` soit un vrai changement d'état. C'est ce qui
  manquait au test généré. Les majuscules exercent la conversion en minuscules faite par le code.
- **Oracle :** après chaque appel, `getOutputType()` doit rendre la constante correspondante,
  d'après les deux branches `"txt"` et `"hocr"` du code.

### 5.3 `booleanFlagsAreFalseByDefault`

- **Mutants visés :** lignes 220, 312, 421, 469, 473, mutation « retourne toujours `true` ».
- **Intention :** vérifier la valeur par défaut des six indicateurs booléens.
- **Données :** un objet tout juste construit, sans aucun appel de setter.
- **Oracle :** les champs sont initialisés à `false` dans la classe (`skipOcr`,
  `enableImagePreprocessing`, `preserveInterwordSpacing`, `inlineContent`, `preloadLangs`,
  `applyRotation`), donc chaque `is...()` doit rendre `false`.

### 5.4 `simpleSettersAreReflectedByGetters`

- **Mutants visés :** lignes 220, 312, 421, 469, 473, mutation « retourne toujours `false` ».
- **Intention :** vérifier que chaque setter simple modifie bien ce que rend son getter.
- **Données :** `true` pour les booléens, et des valeurs différentes des défauts et de zéro pour
  les autres (10, 2048, 45, `HOCR`), afin qu'un getter qui rendrait la valeur par défaut, `false`
  ou 0 soit détecté.
- **Oracle :** ces setters n'ont aucune validation ; le getter doit rendre exactement la valeur donnée.

### 5.5 `setOtherConfigCopiesEveryEntry`

- **Mutant visé :** ligne 506, suppression de l'appel `addOtherTesseractConfig`.
- **Intention :** vérifier que toutes les entrées de la map fournie sont copiées.
- **Données :** une map de deux entrées valides, pour s'assurer que la boucle traite plus d'un élément.
- **Oracle :** `getOtherTesseractConfig()` doit être égal à la map d'entrée.

### 5.6 `setOtherConfigIgnoresNullMap`

- **Mutant visé :** ligne 504, condition `otherTesseractConfig != null` inversée.
- **Intention :** vérifier qu'une map `null` est ignorée sans erreur et sans effacer l'existant.
- **Données :** une entrée ajoutée au préalable, puis `null`. L'entrée préalable permet de voir
  que rien n'est effacé.
- **Oracle :** le code protège explicitement le cas `null` : aucune exception, et la taille reste 1.
  Avec la condition inversée, la boucle s'exécute sur `null` et lève `NullPointerException`.

### 5.7 `pathsAreEmptyByDefaultAndEmptyStaysEmpty`

- **Mutants visés :** lignes 559 et 571, première condition (`!isEmpty()`) inversée.
- **Intention :** vérifier qu'un chemin vide reste vide, sans séparateur ajouté.
- **Données :** `""` pour les trois chemins. Le chemin vide est le seul cas où la première moitié
  de la condition décide du résultat.
- **Oracle :** le code n'ajoute un séparateur que si le chemin n'est pas vide ; le getter doit donc
  rendre `""`, qui est aussi la valeur par défaut de ces trois champs.

### 5.8 `pathsKeepASingleTrailingSeparator`

- **Mutants visés :** lignes 547, 559, 571 (deuxième condition, `!endsWith(...)`, inversée) et
  lignes 542, 554, 566 (getter qui retourne `""`).
- **Intention :** vérifier qu'un chemin qui se termine déjà par un séparateur n'en reçoit pas un second.
- **Données :** un chemin non vide terminé par `File.separator`, différent pour chacun des trois
  setters. Il est construit avec `File.separator` pour que le test passe sous Windows comme sous
  Linux. Un chemin non vide est nécessaire pour atteindre la deuxième condition et pour distinguer
  la vraie valeur de `""`.
- **Oracle :** le chemin doit être rendu inchangé.

### 5.9 `runtimeConfigRejectsNonBlankPaths`

- **Mutants visés :** lignes 593, 600, 607, condition `!StringUtils.isBlank(...)` inversée.
- **Intention :** vérifier qu'une configuration d'exécution refuse toute modification des chemins.
- **Données :** un chemin non vide pour chacun des trois setters de `RuntimeConfig`.
- **Oracle :** le code lève `TikaConfigException` avec le message « Cannot modify ... at runtime.
  Paths must be configured at parser initialization time. »

## 6. Exécution dans GitHub Actions

Le workflow [`.github/workflows/tache2.yml`](.github/workflows/tache2.yml) construit le module et
exécute tous ses tests (originaux, générés et manuels) avec Java 17. Il n'a pas besoin d'Ollama :
les tests générés sont versionnés dans le dépôt.

Exécution réussie : **TODO lien**.

## 7. Reproduire les résultats

```bash
# depuis la racine du dépôt
mvn install -pl :tika-parser-ocr-module -am -DskipTests
cd tika-parsers/tika-parsers-standard/tika-parsers-standard-modules/tika-parser-ocr-module

# tous les tests
mvn test

# 1) tests originaux
mvn test-compile org.pitest:pitest-maven:mutationCoverage "-DtargetTests=org.apache.tika.parser.ocr.TesseractOCRConfigTest" "-DreportsDirectory=target/pit-1-originaux"
# 2) originaux + générés
mvn test-compile org.pitest:pitest-maven:mutationCoverage "-DtargetTests=org.apache.tika.parser.ocr.TesseractOCRConfigTest,org.apache.tika.parser.ocr.TesseractOCRConfig_*" "-DreportsDirectory=target/pit-2-generes"
# 3) originaux + générés + manuels
mvn test-compile org.pitest:pitest-maven:mutationCoverage "-DtargetTests=org.apache.tika.parser.ocr.TesseractOCRConfig*" "-DreportsDirectory=target/pit-3-manuels"
```

Les rapports HTML sont écrits dans `target/pit-*/index.html`.
