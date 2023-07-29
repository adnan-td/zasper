package tokeniser;

import java.util.HashMap;
import java.util.Map;

public final class ReservedKeywords {
  private static final Map<String, TokenType> rk = new HashMap<>();

  static {
    rk.put("let", TokenType.Let);
    rk.put("int", TokenType.IntegerDeclaration);
    rk.put("string", TokenType.StringDeclaration);
    rk.put("double", TokenType.DoubleDeclaration);
    rk.put("boolean", TokenType.BooleanDeclaration);
    rk.put("while", TokenType.WhileStatement);
    rk.put("for", TokenType.ForStatement);
  }

  public static boolean contains(String s) {
    return rk.containsKey(s);
  }

  public static TokenType get(String s) {
    return rk.get(s);
  }
}
