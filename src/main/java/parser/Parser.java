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
    while (get_cur_token().type != TokenType.EOF) {
      program.body.add(this.parse_statement());
      remove_and_get_token();
    }
    return program;
  }

  private Token get_cur_token() {
    return cur;
  }

  private Token remove_and_get_token() {
    if (!tokens.hasNext()) return cur;
    prev = cur;
    cur = tokens.next();
    return prev;
  }

  private void expect_token(TokenType type, String err) throws Exception {
    Token prev = remove_and_get_token();
    if (prev == null || prev.type != type) {
      throw new Exception("Parser Error: " + err + " " + prev + " - Expecting: " + type);
    }
  }

  private Statement parse_statement() throws Exception  {
    return parse_expression();
  }

  private Expression parse_expression() throws Exception  {
    return parse_additive_expression();
  }

  private Expression parse_additive_expression() throws Exception {
    Expression left = parse_multiplicative_expression();
    while(cur.value.equals("+") || cur.value.equals("-")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_multiplicative_expression();
      left = new BinaryExpression(left, right, operator);
    }
    return left;
  }

  private Expression parse_multiplicative_expression() throws Exception {
    Expression left = parse_primary_expression();
    while(cur.value.equals("*") || cur.value.equals("/") || cur.value.equals("%")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_primary_expression();
      left = new BinaryExpression(left, right, operator);
    }
    return left;
  }

  private Expression parse_primary_expression() throws Exception {
    switch (cur.type) {
      case Number:
        return new NumericLiteral(Double.parseDouble(remove_and_get_token().value));
      case Identifier:
        return new Identifier(remove_and_get_token().value);
      case EOF:
        return null;
      case OpenParenthesis:
        remove_and_get_token();
        Expression value = parse_expression();
        expect_token(TokenType.CloseParenthesis, "Unexpected token found. Expecting closing parenthesis.");
        return value;

      default:
        throw new Exception("Unexpected token found during parsing: " + cur.toString());
    }
  }
}
