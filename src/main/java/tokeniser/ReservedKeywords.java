package tokeniser;

import java.util.HashMap;
import java.util.Map;

public class ReservedKeywords {
    private static Map<String, TokenType> rk = new HashMap<>();

    static {
        rk.put("let", TokenType.Let);
    }

    public static boolean contains(String s) {
        return rk.containsKey(s);
    }

    public static TokenType get(String s) {
        return rk.get(s);
    }
}
