package tokeniser;

import java.util.ArrayList;
import java.util.List;

public class Tokeniser {
  private static Token newToken(String value, TokenType type) {
    return new Token(value, type);
  }

  public static List<Token> tokenise(String source) throws Exception {
    ArrayList<Token> tokens = new ArrayList<Token>();
    String[] src = source.split("");
    int i = 0;
    int curIndent = 0;

    while (i < src.length) {
      String ch = src[i];
      if (ch.equals("(")) {
        tokens.add(newToken(ch, TokenType.OpenParenthesis));
      } else if (ch.equals(")")) {
        tokens.add(newToken(ch, TokenType.CloseParenthesis));
      } else if (ch.equals("+") || ch.equals("-") || ch.equals("*") || ch.equals("/") || ch.equals("%")) {
        if (i + 1 < src.length && src[i + 1].equals("=")) {
          tokens.add(newToken(ch + src[i + 1], TokenType.AssignmentOperator));
          i++;
        } else {
          tokens.add(newToken(ch, TokenType.BinaryOperator));
        }
      } else if (ch.equals(">") || ch.equals("<")) {
        if (i + 1 < src.length && src[i + 1].equals("=")) {
          i++;
          tokens.add(newToken(ch + src[i], TokenType.BinaryOperator));
        } else {
          tokens.add(newToken(ch, TokenType.BinaryOperator));
        }
      } else if (ch.equals("=")) {
        if (i + 1 < src.length && src[i + 1].equals("=")) {
          i++;
          tokens.add(newToken(ch + src[i], TokenType.BinaryOperator));
        } else {
          tokens.add(newToken(ch, TokenType.Equals));
        }
      } else if (ch.equals(",")) {
        tokens.add(newToken(ch, TokenType.Comma));
      } else if (ch.equals(":")) {
        tokens.add(newToken(ch, TokenType.Colon));
      } else if (ch.equals("\t")) {
        int newIndent = 1;
        while (i + 1 < src.length && src[i + 1].equals("\t")) {
          i++;
          newIndent++;
        }
        if (Math.abs(newIndent - curIndent) > 1) {
          throw new Exception("Improper Indentation");
        } else {
          if (newIndent != curIndent) {
            tokens.add(newToken("/t", newIndent > curIndent ? TokenType.Indentation : TokenType.Dedentation));
          }
        }
        curIndent = newIndent;
      } else {
        if (isNumeric(ch)) {
          StringBuilder num = new StringBuilder(ch);
          i++;
          while (i < src.length && isNumeric(src[i])) {
            num.append(src[i]);
            i++;
          }
          if (i < src.length && src[i].equals(".")) {
            num.append(src[i]);
            i++;
            while (i < src.length && isNumeric(src[i])) {
              num.append(src[i]);
              i++;
            }
            tokens.add(newToken(num.toString(), TokenType.Double));
          } else {
            tokens.add(newToken(num.toString(), TokenType.Integer));
          }
          continue;
        } else if (isAlphabetic(ch)) {
          StringBuilder identifier = new StringBuilder(ch);
          i++;
          while (i < src.length && isAlphabetic(src[i])) {
            identifier.append(src[i]);
            i++;
          }
          if (ReservedKeywords.contains(identifier.toString())) {
            tokens.add(newToken(identifier.toString(), ReservedKeywords.get(identifier.toString())));
          } else {
            tokens.add(newToken(identifier.toString(), TokenType.Identifier));
          }
          continue;
        } else if (isSkippable(ch)) {
          if (ch.equals("\n")) {
            tokens.add(newToken("EndOfLine", TokenType.EOL));
            if ((i + 1 < src.length && !src[i + 1].equals("\t")) || i + 1 == src.length) {
              while (curIndent > 0) {
                tokens.add(newToken("/t", TokenType.Dedentation));
                curIndent--;
              }
            }
          }
          // Do nothing for skippable characters
        } else {
          throw new Exception("Unrecognized character found: " + ch);
        }
      }
      i++;
    }

    tokens.add(newToken("EndOfLine", TokenType.EOL));
    tokens.add(newToken("EndOfFile", TokenType.EOF));
    return tokens;
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
