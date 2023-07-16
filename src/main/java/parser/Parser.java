package parser;

import java.util.Arrays;
import java.util.Iterator;

import tokeniser.Token;
import tokeniser.TokenType;
import tokeniser.Tokeniser;

public class Parser {
  Iterator<Token> tokens;
  Token cur;
  Token prev;

  public Program getAST(String source) throws Exception {
    Program program = new Program();
    this.tokens = Arrays.asList(Tokeniser.tokenise(source)).iterator();
    remove_and_get_token();
    while (remove_and_get_token().type != TokenType.EOF) {
      program.body.add(this.parse_statement());
    }
    return program;
  }

  Token get_prev_token() {
    return prev;
  }

  Token get_cur_token() {
    return cur;
  }

  Token remove_and_get_token() {
    prev = cur;
    cur = tokens.next();
    return prev;
  }

  private Statement parse_statement() {
    return new Expression(parse_expression());
  }

  private Expression parse_expression() {
    return parse_primary_expression();
  }

  private Expression parse_primary_expression() {

    TokenType type = get_cur_token().type;

    switch (type) {
      case Number:
        // Handle Number case
        break;
      case Identifier:
        // Handle Identifier case
        break;
      case Equals:
        // Handle Equals case
        break;
      case Let:
        // Handle Let case
        break;
      case OpenParanthesis:
        // Handle OpenParanthesis case
        break;
      case CloseParanthesis:
        // Handle CloseParanthesis case
        break;
      case BinaryOperator:
        // Handle BinaryOperator case
        break;
      case EOF:
        // Handle EOF case
        break;
      default:
        // Handle unknown TokenType
        break;
    }

    return new BinaryExpression(null, null, null);
  }
}
