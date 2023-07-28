package tokeniser;

import java.util.HashMap;
import java.util.Map;

public final class ReservedKeywords {
  private static final Map<String, TokenType> rk = new HashMap<>();

  static {
    rk.put("let", TokenType.Let);
    rk.put("int", TokenType.Integer);
    rk.put("string", TokenType.String);
    rk.put("double", TokenType.Double);
  }

  public static boolean contains(String s) {
    return rk.containsKey(s);
  }

  public static TokenType get(String s) {
    return rk.get(s);
  }
}
