package tokeniser;

import java.util.ArrayList;
import java.util.List;

public class Tokeniser {
  private static Token newToken(String value, TokenType type) {
    return new Token(value, type);
  }

  public static List<Token> tokenise(String source) throws Exception {
    ArrayList<Token> tokens = new ArrayList<>();
    source = removeEmptyLines(source);
//    System.out.println(source);
    String[] src = source.split("");
    int i = 0;
    int curIndent = 0;

    while (i < src.length) {
      String ch = src[i];
      switch (ch) {
        case "(":
          tokens.add(newToken(ch, TokenType.OpenParenthesis));
          break;
        case ")":
          tokens.add(newToken(ch, TokenType.CloseParenthesis));
          break;
        case "+":
        case "-":
        case "*":
        case "/":
        case "%":
          if (i + 1 < src.length && src[i + 1].equals("=")) {
            tokens.add(newToken(ch + src[i + 1], TokenType.AssignmentOperator));
            i++;
          } else if (ch.equals("-") && i + 1 < src.length && src[i + 1].equals(">")) {
            tokens.add(newToken(ch + src[i + 1], TokenType.ArrowOperator));
            i++;
          } else {
            tokens.add(newToken(ch, TokenType.BinaryOperator));
          }
          break;
        case ">":
        case "<":
          if (i + 1 < src.length && src[i + 1].equals("=")) {
            i++;
            tokens.add(newToken(ch + src[i], TokenType.BinaryOperator));
          } else {
            tokens.add(newToken(ch, TokenType.BinaryOperator));
          }
          break;
        case "=":
          if (i + 1 < src.length && src[i + 1].equals("=")) {
            i++;
            tokens.add(newToken(ch + src[i], TokenType.BinaryOperator));
          } else {
            tokens.add(newToken(ch, TokenType.Equals));
          }
          break;
        case ",":
          tokens.add(newToken(ch, TokenType.Comma));
          break;
        case ":":
          tokens.add(newToken(ch, TokenType.Colon));
          break;
        case "\"":
          StringBuilder str = new StringBuilder();
          while (i + 1 < src.length && !src[i + 1].equals("\"")) {
            str.append(src[i + 1]);
            i++;
          }
          tokens.add(newToken(str.toString(), TokenType.String));
          i++;
          break;
        case "\t":
          int newIndent = 1;
          while (i + 1 < src.length && src[i + 1].equals("\t")) {
            i++;
            newIndent++;
          }
          if (Math.abs(newIndent - curIndent) > 1 && newIndent > curIndent) {
            throw new Exception("Improper Indentation");
          } else {
            if (newIndent > curIndent) {
              tokens.add(newToken("/t", TokenType.Indentation));
            } else if (newIndent < curIndent) {
              while (curIndent != newIndent) {
                tokens.add(newToken("/t", TokenType.Dedentation));
                curIndent--;
              }
            }
          }
          curIndent = newIndent;
          break;
        default:
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
              // if the next line has no indentations
              if ((i + 1 < src.length && !src[i + 1].equals("\t"))) {
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
          break;
      }
      i++;
    }
    if ((i + 1 >= src.length)) {
      while (curIndent > 0) {
        tokens.add(newToken("/t", TokenType.Dedentation));
        curIndent--;
      }
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

  private static String removeEmptyLines(String input) {
    String[] lines = input.split("\\r?\\n");
    StringBuilder resultBuilder = new StringBuilder();

    for (String line : lines) {
      if (!line.trim().isEmpty()) {
        resultBuilder.append(line).append("\n");
      }
    }

    return resultBuilder.toString().trim();
  }
}
