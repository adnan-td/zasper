package tokeniser;

import Exceptions.ErrorTypes;
import Exceptions.ParsingException;

import java.util.ArrayList;
import java.util.List;

public class Tokeniser {
  private static Token newToken(String value, TokenType type, int start, int end) {
    return new Token(value, type, start, end + 1);
  }

  public static List<Token> tokenise(String source) throws ParsingException {
    ArrayList<Token> tokens = new ArrayList<>();
    String[] src = source.split("");
    int Index = 0;
    int curIndent = 0;

    while (Index < src.length) {
      String ch = src[Index];
      int startIndex = Index;
      switch (ch) {
        case "(":
          tokens.add(newToken(ch, TokenType.OpenParenthesis, startIndex, Index));
          break;
        case ")":
          tokens.add(newToken(ch, TokenType.CloseParenthesis, startIndex, Index));
          break;
        case "+":
        case "-":
        case "*":
        case "/":
        case "%":
          if (Index + 1 < src.length && src[Index + 1].equals("=")) {
            tokens.add(newToken(ch + src[Index + 1], TokenType.AssignmentOperator, startIndex, Index + 1));
            Index++;
          } else if (ch.equals("-") && Index + 1 < src.length && src[Index + 1].equals(">")) {
            tokens.add(newToken(ch + src[Index + 1], TokenType.ArrowOperator, startIndex, Index + 1));
            Index++;
          } else {
            tokens.add(newToken(ch, TokenType.BinaryOperator, startIndex, Index));
          }
          break;
        case ">":
        case "<":
          if (Index + 1 < src.length && src[Index + 1].equals("=")) {
            Index++;
            tokens.add(newToken(ch + src[Index], TokenType.BinaryOperator, startIndex, Index));
          } else {
            tokens.add(newToken(ch, TokenType.BinaryOperator, startIndex, Index));
          }
          break;
        case "=":
          if (Index + 1 < src.length && src[Index + 1].equals("=")) {
            Index++;
            tokens.add(newToken(ch + src[Index], TokenType.BinaryOperator, startIndex, Index));
          } else {
            tokens.add(newToken(ch, TokenType.Equals, startIndex, Index));
          }
          break;
        case ",":
          tokens.add(newToken(ch, TokenType.Comma, startIndex, Index));
          break;
        case ":":
          tokens.add(newToken(ch, TokenType.Colon, startIndex, Index));
          break;
        case "\"":
          StringBuilder str = new StringBuilder();
          while (Index + 1 < src.length && !src[Index + 1].equals("\"")) {
            str.append(src[Index + 1]);
            Index++;
          }
          if (Index == src.length)
            throw new ParsingException(ErrorTypes.StringNotClosed, new Location(startIndex, Index), source);
          Index++;
          tokens.add(newToken(str.toString(), TokenType.String, startIndex, Index));
          break;
        case "\t":
          int newIndent = 1;
          while (Index + 1 < src.length && src[Index + 1].equals("\t")) {
            Index++;
            newIndent++;
          }
          // checking previous dedentations
          int t = tokens.size() - 1;
          for (int k = 0; k < newIndent - curIndent; k++) {
            while (tokens.get(t).type == TokenType.EOL) {
              t--;
            }
            if (tokens.get(t).type == TokenType.Dedentation) {
              tokens.remove(t);
              curIndent++;
            } else {
              break;
            }
          }
          if (Math.abs(newIndent - curIndent) > 1 && newIndent > curIndent) {
            throw new ParsingException(ErrorTypes.ImproperIndentation, new Location(startIndex, Index), source);
          } else {
            if (newIndent > curIndent) {
              tokens.add(newToken("/t", TokenType.Indentation, startIndex, Index));
            } else if (newIndent < curIndent) {
              while (curIndent != newIndent) {
                tokens.add(newToken("/t", TokenType.Dedentation, startIndex, Index));
                curIndent--;
              }
            }
          }
          curIndent = newIndent;
          break;
        default:
          if (isNumeric(ch)) {
            StringBuilder num = new StringBuilder(ch);
            Index++;
            while (Index < src.length && isNumeric(src[Index])) {
              num.append(src[Index]);
              Index++;
            }
            if (Index < src.length && src[Index].equals(".")) {
              num.append(src[Index]);
              Index++;
              while (Index < src.length && isNumeric(src[Index])) {
                num.append(src[Index]);
                Index++;
              }
              tokens.add(newToken(num.toString(), TokenType.Double, startIndex, Index - 1));
            } else {
              tokens.add(newToken(num.toString(), TokenType.Integer, startIndex, Index - 1));
            }
            continue;
          } else if (isAlphabetic(ch)) {
            StringBuilder identifier = new StringBuilder(ch);
            Index++;
            while (Index < src.length && isAlphabetic(src[Index])) {
              identifier.append(src[Index]);
              Index++;
            }
            if (ReservedKeywords.contains(identifier.toString())) {
              tokens.add(newToken(identifier.toString(), ReservedKeywords.get(identifier.toString()), startIndex, Index - 1));
            } else {
              tokens.add(newToken(identifier.toString(), TokenType.Identifier, startIndex, Index - 1));
            }
            continue;
          } else if (isSkippable(ch)) {
            if (ch.equals("\n")) {
              tokens.add(newToken("EndOfLine", TokenType.EOL, startIndex, Index));
              // if the next line has no indentations
              if ((Index + 1 < src.length && !src[Index + 1].equals("\t"))) {
                while (curIndent > 0) {
                  tokens.add(newToken("/t", TokenType.Dedentation, startIndex, Index));
                  curIndent--;
                }
              }
            }
            // Do nothing for skippable characters
          } else {
            throw new ParsingException(ErrorTypes.UnrecognizedCharacter, "Unrecognized character found: " + ch, new Location(startIndex, Index), source);
          }
          break;
      }
      Index++;
    }
    if ((Index + 1 >= src.length)) {
      while (curIndent > 0) {
        tokens.add(newToken("/t", TokenType.Dedentation, Index, Index));
        curIndent--;
      }
    }
    tokens.add(newToken("EndOfLine", TokenType.EOL, Index, Index));
    tokens.add(newToken("EndOfFile", TokenType.EOF, Index, Index));
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
