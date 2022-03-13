package Testing;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import wordle.Wordle;

import java.nio.file.Paths;

public class ModelTesting {
    //Tests if the dictionary can be loaded and that it accurately compares
    //a word to the dictionary
    @Test
    public void dictCompare() {
        Wordle wordle = new Wordle();
        Assertions.assertFalse(wordle.hasDictionary());
        //TODO: Put in the path to the working dictionary OR test dictionary
        wordle.loadDictionary(Paths.get(""));
        Assertions.assertTrue(wordle.hasDictionary());
        Assertions.assertTrue(wordle.isValidWord("Crane"));
        Assertions.assertTrue(wordle.isValidWord("Shard"));
        Assertions.assertFalse(wordle.isValidWord("yep"));
        //TODO
    }



}
