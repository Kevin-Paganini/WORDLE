package Testing;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import wordle.Wordle;

import java.io.*;
import java.nio.file.Paths;


public class ModelTesting {
    //Tests if the dictionary can be loaded and that it accurately compares
    //a word to the dictionary
    @Test
    public void dictCompare() throws IOException {
        Wordle wordle = new Wordle(10, 10, new File("Resources/wordle-official.txt") );
        Assertions.assertFalse(wordle.hasDictionary());
        Assertions.assertThrows(NullPointerException.class, () -> wordle.isValidWord("shard"));

        //TODO: Put in the path to the working dictionary OR test dictionary
        Assertions.assertDoesNotThrow(() -> wordle.loadDictionary(new File("hi")));
        Assertions.assertTrue(wordle.hasDictionary());
        Assertions.assertTrue(wordle.isValidWord("crane"));
        Assertions.assertTrue(wordle.isValidWord("shard"));

        //Caps
        Assertions.assertTrue(wordle.isValidWord("MARCH"));
        Assertions.assertTrue(wordle.isValidWord("Chart"));
        Assertions.assertTrue(wordle.isValidWord("cHaRm"));

        //Wrong length
        Assertions.assertFalse(wordle.isValidWord("yep"));
        Assertions.assertFalse(wordle.isValidWord("eradicate"));
        Assertions.assertFalse(wordle.isValidWord("a"));

        //Null or empty string
        Assertions.assertThrows(NullPointerException.class, () -> wordle.isValidWord(null));
        Assertions.assertFalse(wordle.isValidWord(""));

        //TODO: Put in the path to a DIFFERENT test directory - one that doesn't contain "crane" but does contain "shard"
        Assertions.assertDoesNotThrow(() -> wordle.loadDictionary(new File("hi")));
        Assertions.assertFalse(wordle.isValidWord("crane"));
        Assertions.assertTrue(wordle.isValidWord("shard"));

        //TODO: This should contain path to a file that has string(s) of invalid length
        Assertions.assertThrows(IOException.class, () -> wordle.loadDictionary(new File("hi")));
        //TODO: This one should contain a path to a file that has non-characters in it
        Assertions.assertThrows(IOException.class, () -> wordle.loadDictionary(new File("hi")));
    }
}
