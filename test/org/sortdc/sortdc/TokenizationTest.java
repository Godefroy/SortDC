package org.sortdc.sortdc;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TokenizationTest {

    String testText = "Hello world!\nGrand-mère fait des confitures avec application.";
    String testLang = "french";

    public TokenizationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExtractOnlyWords() {
        try {
            Tokenization instance = new Tokenization();
            instance.setExtractWords(true);
            instance.disableStemming();
            instance.setExtractBigrams(false);
            instance.setExtractTrigrams(false);
            instance.setWordsMinLength(0);
            List<String> test = instance.extract(this.testText);
            List<String> expected = Arrays.asList("hello", "world", "grand-mere", "fait", "des", "confitures", "avec", "application");
            assertEquals(expected.size(), test.size());
            for (int i = 0; i < expected.size(); i++) {
                assertTrue(test.contains(expected.get(i)));
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void testExtractWordsWithStemming() {
        try {
            Tokenization instance = new Tokenization();
            instance.setExtractWords(true);
            instance.enableStemming(this.testLang);
            instance.setExtractBigrams(false);
            instance.setExtractTrigrams(false);
            instance.setWordsMinLength(0);
            List<String> test = instance.extract(this.testText);
            List<String> expected = Arrays.asList("hello", "world", "grand-mer", "fait", "de", "confitur", "avec", "appliqu");
            assertEquals(expected.size(), test.size());
            for (int i = 0; i < expected.size(); i++) {
                assertTrue(test.contains(expected.get(i)));
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void testExtractWordsWithStopWords() {
        try {
            List<String> stopWords = Arrays.asList("fait", "avec");
            Tokenization instance = new Tokenization();
            instance.setExtractWords(true);
            instance.disableStemming();
            instance.setExtractBigrams(false);
            instance.setExtractTrigrams(false);
            instance.setWordsMinLength(0);
            instance.setStopWords(stopWords);
            List<String> test = instance.extract(this.testText);
            List<String> expected = Arrays.asList("hello", "world", "grand-mere", "des", "confitures", "application");
            assertEquals(expected.size(), test.size());
            for (int i = 0; i < expected.size(); i++) {
                assertTrue(test.contains(expected.get(i)));
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void testExtractBigrams() {
        try {
            Tokenization instance = new Tokenization();
            instance.setExtractWords(false);
            instance.disableStemming();
            instance.setExtractBigrams(true);
            instance.setExtractTrigrams(false);
            instance.setWordsMinLength(0);
            List<String> test = instance.extract(this.testText);
            List<String> expected = Arrays.asList(
                    "he", "el", "ll", "lo", "wo", "or", "rl", "ld", "gr", "ra", "an",
                    "nd", "d-", "-m", "me", "er", "re", "fa", "ai", "it", "de", "es",
                    "co", "on", "nf", "fi", "it", "tu", "ur", "re", "es", "av", "ve",
                    "ec", "ap", "pp", "pl", "li", "ic", "ca", "at", "ti", "io", "on");
            assertEquals(expected.size(), test.size());
            for (int i = 0; i < expected.size(); i++) {
                assertTrue(test.contains(expected.get(i)));
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void testExtractTrigrams() {
        try {
            Tokenization instance = new Tokenization();
            instance.setExtractWords(false);
            instance.disableStemming();
            instance.setExtractBigrams(false);
            instance.setExtractTrigrams(true);
            instance.setWordsMinLength(0);
            List<String> test = instance.extract(this.testText);
            List<String> expected = Arrays.asList(
                    "hel", "ell", "llo", "wor", "orl", "rld", "gra", "ran", "and", "nd-",
                    "d-m", "-me", "mer", "ere", "fai", "ait", "des", "con", "onf", "nfi",
                    "fit", "itu", "tur", "ure", "res", "ave", "vec", "app", "ppl", "pli",
                    "lic", "ica", "cat", "ati", "tio", "ion");
            assertEquals(expected.size(), test.size());
            for (int i = 0; i < expected.size(); i++) {
                assertTrue(test.contains(expected.get(i)));
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }

    @Test
    public void testExtractWithAllOptions() {
        try {
            List<String> stopWords = Arrays.asList("fait", "avec");
            Tokenization instance = new Tokenization();
            instance.setExtractWords(true);
            instance.enableStemming(this.testLang);
            instance.setExtractBigrams(true);
            instance.setExtractTrigrams(true);
            instance.setWordsMinLength(3);
            instance.setStopWords(stopWords);
            List<String> test = instance.extract(this.testText);
            List<String> expected = Arrays.asList(
                    "hello", "world", "grand-mer", "de", "confitur", "appliqu", "he",
                    "el", "ll", "lo", "wo", "or", "rl", "ld", "gr", "ra", "an", "nd",
                    "d-", "-m", "me", "er", "re", "fa", "ai", "it", "de", "es", "co",
                    "on", "nf", "fi", "it", "tu", "ur", "re", "es", "av", "ve", "ec",
                    "ap", "pp", "pl", "li", "ic", "ca", "at", "ti", "io", "on", "hel",
                    "ell", "llo", "wor", "orl", "rld", "gra", "ran", "and", "nd-", "d-m",
                    "-me", "mer", "ere", "fai", "ait", "des", "con", "onf", "nfi", "fit",
                    "itu", "tur", "ure", "res", "ave", "vec", "app", "ppl", "pli", "lic",
                    "ica", "cat", "ati", "tio", "ion");
            assertEquals(expected.size(), test.size());
            for (int i = 0; i < expected.size(); i++) {
                assertTrue(test.contains(expected.get(i)));
            }
        } catch (Exception e) {
            throw new AssertionError(e.getMessage());
        }
    }
}
