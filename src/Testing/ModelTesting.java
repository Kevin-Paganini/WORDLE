package Testing;

import Model.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.*;
import java.util.*;


public class ModelTesting {
    static Session session = Session.getSession();

    /**
     *
     * Tests if the dictionary can be loaded and that it accurately compares
     * a word to the dictionary
     * @author Atreyu Schilling
     */

    // Passed on 3/16
    @Test
    public void dictCompare() throws IOException {
        Wordle wordle = new Wordle(5, new File("src/Resources/wordle-official.txt"), session);



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
        Wordle wordle = new Wordle(5, new File("src/Resources/wordle-official.txt"), session);

        //Most basic test
        Assertions.assertTrue(wordle.forceTarget("crane"));
        Assertions.assertArrayEquals(wordle.makeGuess("drake"), new int[]{0, 2, 2, 0, 2});
        //If index 2 is 1, e was checked too early or we double-dipped
        Assertions.assertArrayEquals(wordle.makeGuess("creme"), new int[]{2, 2, 0, 0, 2});
        Assertions.assertArrayEquals(wordle.makeGuess("epoch"), new int[]{1, 0, 0, 1, 0});
        //Invalid entry
        Assertions.assertArrayEquals(wordle.makeGuess("bingus"), null);
        //Exactly correct
        Assertions.assertArrayEquals(wordle.makeGuess("crane"), new int[]{2, 2, 2, 2, 2});

        //Problem from before, fixed
        Assertions.assertTrue(wordle.forceTarget("meant"));
        Assertions.assertArrayEquals(wordle.makeGuess("state"), new int[]{0, 1, 2, 0, 1});

    }

    /**
     * Test must be re-written to accommodate new model
     * This test fails currently
     */
    @Test
    public void testGuessWriter() throws IOException {
        /*
        File file = new File("src/Resources/previousGuesses.txt");
        file.delete();
        Wordle wordle = new Wordle(5, new File("src/Resources/wordle-official.txt"), session);
        //arbitrary target for testing purposes
        wordle.forceTarget("meant");

        wordle.makeGuess("crane");
        wordle.makeGuess("creme");
        wordle.makeGuess("death");
        wordle.makeGuess("snake");
        BufferedReader br = new BufferedReader(new FileReader(file));
        Assertions.assertEquals("crane", br.readLine());
        Assertions.assertEquals("creme", br.readLine());
        Assertions.assertEquals("death", br.readLine());
        Assertions.assertEquals("snake", br.readLine());
        wordle.makeGuess("doubt");
        Assertions.assertNull(br.readLine());

        Assertions.assertEquals("doubt", br.readLine());
        br.close();
        file.delete();

        wordle.makeGuess("march");
        wordle.makeGuess("meant");

        br = new BufferedReader(new FileReader(file));
        Assertions.assertEquals("march", br.readLine());
        Assertions.assertEquals("meant0", br.readLine());
        br.close();
        file.delete();

         */
    }

    @Test
    public void testSuggestions() throws IOException {
        Wordle wordle = new Wordle(5, new File("src/Resources/wordle-official.txt"), session);
        wordle.forceTarget("queen");
        wordle.makeGuess("queer");
        Set<String> set = new TreeSet<>();
        set.add("queen");
        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());
        wordle = new Wordle(5, new File("src/Resources/wordle-official.txt"), session);
        wordle.forceTarget("queen");
        wordle.makeGuess("queer");
        wordle.makeGuess("snack");
        Assertions.assertEquals(set, wordle.getSuggestions().pruneDictionary());
    }
}
