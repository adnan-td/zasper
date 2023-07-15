import tokeniser.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Token[] tokens = Tokeniser.tokenise("let x = 45 * (100 / 20)");
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
}
