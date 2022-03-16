package Testing;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import wordle.Wordle;

import java.io.File;
import java.io.IOException;


public class ModelTesting {
    //Tests if the dictionary can be loaded and that it accurately compares
    //a word to the dictionary
    @Test
    public void dictCompare() throws IOException {

        Wordle wordle = new Wordle(10, 10, new File("Resources/wordle-official.txt") );

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
        Wordle finalWordle = wordle; //IntelliJ doesn't like me, I don't know why, but this is here now
        Assertions.assertThrows(NullPointerException.class, () -> finalWordle.isValidWord(null));
        Assertions.assertFalse(wordle.isValidWord(""));

        //Dictionary contains shard but not crane - make sure we don't get false positives
        wordle = new Wordle(5, 5, new File("Resources/shardnocrane.txt"));
        Assertions.assertFalse(wordle.isValidWord("crane"));
        Assertions.assertTrue(wordle.isValidWord("shard"));

        // file that has strings of invalid length
        Assertions.assertThrows(IOException.class ,() -> new Wordle(5, 5, new File("Resources/invalidLengths.txt")));
        // file that has non-characters in it
        Assertions.assertThrows(IOException.class, () -> new Wordle(5, 5, new File("Resources/invalidCharacters.txt")));

    }

}
