import wordle.App;

/**
 * Jar runner for wordle.
 * Required because a jar's entry point can't be a class that extends Application.
 */
public class JarRunner {
    public static void main(String[] args) {
        App.main(args);
    }
}