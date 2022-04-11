package Testing;

import Model.*;
import javafx.util.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.*;


public class ModelTesting {
    static Session session = new Session();
    private static final File OFFICIAL = new File("src/Resources/wordle-official.txt");

    /**
     *
     * Tests if the dictionary can be loaded and that it accurately compares
     * a word to the dictionary
     * @author Atreyu Schilling
     */

    // Passed on 3/16
    @Test
    public void dictCompare() throws IOException {
        Wordle wordle = new Wordle(5, OFFICIAL, session);



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
        Wordle finalWordle = wordle; //IntelliJ doesn't like me. I don't know why, but this is here now
        Assertions.assertThrows(NullPointerException.class, () -> finalWordle.isValidWord(null));
        Assertions.assertFalse(wordle.isValidWord(""));

        //Dictionary contains shard but not crane - make sure we don't get false positives
        wordle = new Wordle(5, new File("src/Resources/shardnocrane.txt"), session);
        Assertions.assertFalse(wordle.isValidWord("crane"));
        Assertions.assertTrue(wordle.isValidWord("shard"));

        // file that has strings of invalid length
        Assertions.assertThrows(IOException.class ,() ->
                new Wordle(5, new File("src/Resources/invalidLengths.txt"), session));
        // file that has non-characters in it
        Assertions.assertThrows(IOException.class, () ->
                new Wordle(5, new File("src/Resources/invalidCharacters.txt"), session));

    }

    /**
     * Tests if the returnPositions method does what it's supposed to do
     * @author Atreyu Schilling
     */
    // Passed on 3/16
    @Test
    public void guessCompare() throws IOException {
        Wordle wordle = new Wordle(5, OFFICIAL, session);

        //Most basic test
        Assertions.assertDoesNotThrow(() -> wordle.forceTarget("crane"));
        Assertions.assertArrayEquals(wordle.makeGuess("drake"), new int[]{0, 2, 2, 0, 2});
        //If index 2 is 1, e was checked too early or we double-dipped
        Assertions.assertArrayEquals(wordle.makeGuess("creme"), new int[]{2, 2, 0, 0, 2});
        Assertions.assertArrayEquals(wordle.makeGuess("epoch"), new int[]{1, 0, 0, 1, 0});
        //Invalid entry
        Assertions.assertNull(wordle.makeGuess("bingus"));
        //Exactly correct
        Assertions.assertArrayEquals(wordle.makeGuess("crane"), new int[]{2, 2, 2, 2, 2});

        //Problem from before, fixed
        Assertions.assertDoesNotThrow(() -> wordle.forceTarget("meant"));
        Assertions.assertArrayEquals(wordle.makeGuess("state"), new int[]{0, 1, 2, 0, 1});

    }


    @Test
    public void testDictionaryReading() {
        Assertions.assertDoesNotThrow(() -> new Wordle(5, OFFICIAL, session));
        Assertions.assertThrows(IOException.class, () -> new Wordle(5, new File("src/Resources/shakespeare_dict_test.txt"), session));
        Assertions.assertThrows(IOException.class, () -> new Wordle(5, new File("src/Resources/invalidLengths.txt"), session));
        Assertions.assertThrows(IOException.class, () -> new Wordle(5, new File("src/Resources/invalidCharacters.txt"), session));
        Assertions.assertThrows(IOException.class, () -> new Wordle(5, new File("src/Resources/thisDoesNotExist.txt"), session));
    }

    @Test
    public void testSuggestions() throws IOException {
        Wordle wordle = new Wordle(5, OFFICIAL, session);
        wordle.forceTarget("queen");
        wordle.makeGuess("queer");
        Set<String> set = new TreeSet<>();
        set.add("queen");
        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());

        //Calling this twice should return the same thing
        Assertions.assertEquals(wordle.getSuggestions().pruneDictionary(), wordle.getSuggestions().pruneDictionary());

        wordle = new Wordle(5, OFFICIAL, session);

        wordle.forceTarget("queen");
        wordle.makeGuess("queer");
        wordle.getSuggestions().pruneDictionary();
        wordle.makeGuess("snack");
        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());

        wordle = new Wordle(6, new File("src/Resources/words_6_letters.txt"), session);
        wordle.forceTarget("bubble");
        wordle.makeGuess("bubals"); //Yes this is a real word
        set.clear();
        set.add("bubble");
        set.add("bubbly");
        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());
    }

    @Test
    public void testHints() throws IOException {
        Wordle wordle = new Wordle(5, OFFICIAL, session);
        wordle.forceTarget("marsh");
        wordle.makeGuess("march");
        Assertions.assertEquals(new Pair<>(3, 's'), wordle.getHint());
        wordle.makeGuess("sedan");
        Assertions.assertEquals(new Pair<>(3, 's'), wordle.getHint());
        wordle.makeGuess("swish");
        Assertions.assertEquals(new Pair<>(4, 'h'), wordle.getHint());
        wordle = new Wordle(5, OFFICIAL, session);
        wordle.forceTarget("ninny");
        wordle.makeGuess("ninja");
        Assertions.assertEquals(new Pair<>(4, 'y'), wordle.getHint());
        wordle.makeGuess("wryly");
        Assertions.assertEquals(new Pair<>(3, 'n'), wordle.getHint());
        wordle = new Wordle(5, OFFICIAL, session);
        wordle.forceTarget("queen");
        wordle.makeGuess("quest");
        Assertions.assertEquals(new Pair<>(4, 'n'), wordle.getHint());
    }

    @Test
    public void testHintsWithSuggestions() throws IOException {
        Set<String> set = new TreeSet<>();
        set.add("queer");
        set.add("queen");
        set.add("quell");
        set.add("query");
        set.add("queue");

        Wordle wordle = new Wordle(5, OFFICIAL, session);
        wordle.forceTarget("queen");
        wordle.makeGuess("quest");

        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());
        Assertions.assertEquals(new Pair<>(4, 'n'), wordle.getHint());

        set = new TreeSet<>();
        set.add("queen");

        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());
        //Make getting a hint doesn't somehow change the suggestions
        wordle = new Wordle(5, OFFICIAL, session);
        wordle.forceTarget("queen");
        wordle.makeGuess("queer");
        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());
        Assertions.assertEquals(new Pair<>(4, 'n'), wordle.getHint());
        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());

    }
}
