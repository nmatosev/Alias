package com.example.alias;

import com.example.alias.utils.Utilities;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GameplayActivityTest {

    @Test
    public void checkForDuplicatesTest() {
        List<String> list = Arrays.asList("kupac", "klijent", "klijent", "auto", "auto", "auto");
        List<String> words = Utilities.prepareWords(list);
        Assert.assertNotNull(words);
        Assert.assertFalse(words.isEmpty());
        Assert.assertEquals(3, words.size());
        Assert.assertTrue(words.contains("auto"));
        Assert.assertTrue(words.contains("klijent"));
        Assert.assertTrue(words.contains("kupac"));
    }

    @Test
    public void checkForNonTrimmedWordsTest() {
        List<String> list = Arrays.asList("   kupac ", "  klijent ", "auto          ");
        List<String> words = Utilities.prepareWords(list);
        Assert.assertNotNull(words);
        Assert.assertFalse(words.isEmpty());
        Assert.assertEquals(3, words.size());
        Assert.assertTrue(words.contains("auto"));
        Assert.assertTrue(words.contains("klijent"));
        Assert.assertFalse(words.contains("   kupac "));
    }


    @Test
    public void checkForEmptyStringTest() {
        List<String> list = Arrays.asList("   ", "  klijent ", "auto          ");
        List<String> words = Utilities.prepareWords(list);
        Assert.assertNotNull(words);
        Assert.assertFalse(words.isEmpty());
        Assert.assertEquals(2, words.size());
        Assert.assertTrue(words.contains("klijent"));
        Assert.assertFalse(words.contains("   kupac "));
    }

}
