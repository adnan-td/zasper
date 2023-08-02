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
    rk.put("true", TokenType.Boolean);
    rk.put("false", TokenType.Boolean);
    rk.put("while", TokenType.WhileStatement);
    rk.put("for", TokenType.ForStatement);
    rk.put("func", TokenType.FunctionDeclaration);
    rk.put("return", TokenType.ReturnStatement);
    rk.put("null", TokenType.Null);
    rk.put("break", TokenType.Break);
    rk.put("continue", TokenType.Continue);
    rk.put("if", TokenType.IfStatement);
    rk.put("elif", TokenType.ElifStatement);
    rk.put("else", TokenType.ElseStatement);
  }

  public static boolean contains(String s) {
    return rk.containsKey(s);
  }

  public static TokenType get(String s) {
    return rk.get(s);
  }
}
