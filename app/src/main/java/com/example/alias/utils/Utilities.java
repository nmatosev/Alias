package com.example.alias.utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Utilities {

    private Utilities(){

    }


    /**
     * Returns SoundPool instance responsible for playing click sounds.
     * @return SoundPool instance
     */
    public static SoundPool getSoundPool() {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            soundPool = new SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        return soundPool;
    }

    public static List<String> getDictionary() {
        List<String> dictionary = parseFile();
        return prepareWords(dictionary);
    }

    /**
     * Gets words from storage and prepares them for app (removes duplicates, empty strings, trims strings)
     *
     * @return list of words
     */
    public static List<String> prepareWords(List<String> words) {
        //trims and removes empty strings
        words = words.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        //remove duplicates
        Set<String> removedDuplicates = new HashSet<>(words);

        //clear list and add removed duplicates set to empty list
        words.clear();
        words.addAll(removedDuplicates);
        Collections.shuffle(words);
        return words;
    }


    /**
     * Parses file where words are stored.
     *
     * @return source file where questions are stored.
     */
    private static List<String> parseFile() {
        List<String> words = new ArrayList<>();
        InputStream in = Objects.requireNonNull(Utilities.class.getClassLoader()).getResourceAsStream(Constants.WORDS_PATH);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String lineInFile;
        try {
            while ((lineInFile = reader.readLine()) != null) {
                Log.d("line in file ", lineInFile);
                words.addAll(Arrays.asList(lineInFile.split(",")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }
}
