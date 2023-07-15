package tokeniser;

import java.util.ArrayList;

public class Tokeniser {
  private static Token newToken(String value, TokenType type) {
    return new Token(value, type);
  }

  public static Token[] tokenise(String source) throws Exception {
    ArrayList<Token> tokens = new ArrayList<Token>();
    String[] src = source.split("");
    int i = 0;

    while (i < src.length) {
      String ch = src[i];
      if (ch.equals("(")) {
        tokens.add(newToken(ch, TokenType.OpenParanthesis));
      } else if (ch.equals(")")) {
        tokens.add(newToken(ch, TokenType.CloseParanthesis));
      } else if (ch.equals("+") || ch.equals("-") || ch.equals("*") || ch.equals("/")) {
        tokens.add(newToken(ch, TokenType.BinaryOperator));
      } else if (ch.equals("=")) {
        tokens.add(newToken(ch, TokenType.Equals));
      } else {
        if (isNumeric(ch)) {
          String num = ch;
          i++;
          while (i < src.length && isNumeric(src[i])) {
            num += src[i];
            i++;
          }
          tokens.add(newToken(num, TokenType.Number));
          continue; // Skip the increment at the end of the loop
        } else if (isAlphabetic(ch)) {
          String identifier = ch;
          i++;
          while (i < src.length && isAlphabetic(src[i])) {
            identifier += src[i];
            i++;
          }
          tokens.add(newToken(identifier, TokenType.Identifier));
          continue; // Skip the increment at the end of the loop
        } else if (isSkippable(ch)) {
          // Do nothing for skippable characters
        } else {
          throw new Exception("Unrecognized character found: " + ch);
        }
      }
      i++;
    }

    tokens.add(newToken("EndOfFile", TokenType.EOF));

    return tokens.toArray(new Token[tokens.size()]);
  }

  private static boolean isNumeric(String source) {
    return source.matches("-?\\d+(\\.\\d+)?");
  }

  private static boolean isAlphabetic(String source) {
    return source.matches("[a-zA-Z]+");
  }

  private static boolean isSkippable(String source) {
    return source.matches("[\\t\\n\\s]*");
  }
}
