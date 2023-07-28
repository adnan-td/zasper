package parser;

import java.util.Arrays;
import java.util.ListIterator;

import parser.ast.Statement;
import tokeniser.Token;
import tokeniser.TokenType;
import tokeniser.Tokeniser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import parser.ast.*;

public class Parser {
  private final ListIterator<Token> tokens;
  private Token cur;
  public final String source;
  public final Token[] tokenArray;
  private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public Parser(String source) throws Exception {
    this.source = source;
    this.tokenArray = Tokeniser.tokenise(source);
    this.tokens = Arrays.asList(this.tokenArray).listIterator();
  }

  public void printTokens() {
    System.out.println("*** Tokens ***");
    for (Token t : tokenArray) {
      System.out.println(t);
    }
    System.out.println();
  }

  public void printAST() throws Exception {
    Program pg = getAST();
    String json = gson.toJson(pg);
    System.out.println("*** AST ***");
    System.out.println(json);
  }

  public Program getAST() throws Exception {
    Program program = new Program();
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
    Token prev;
    if (!tokens.hasNext()) return cur;
    prev = cur;
    cur = tokens.next();
    return prev;
  }

  private void expect_token(TokenType type, String err) throws Exception {
    Token prev = remove_and_get_token();
    if (prev == null || prev.type != type) {
      throw new Exception(String.format("Parser Error: %s\n%s - Expecting: %s", err, prev, type));
    }
  }

  private Statement parse_statement() throws Exception {
    return parse_expression();
  }

  private Expression parse_expression() throws Exception {
    return parse_additive_expression();
  }

  private Expression parse_additive_expression() throws Exception {
    Expression left = parse_multiplicative_expression();
    while (cur.value.equals("+") || cur.value.equals("-")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_multiplicative_expression();
      left = new BinaryExpression(left, right, operator);
    }
    return left;
  }

  private Expression parse_multiplicative_expression() throws Exception {
    Expression left = parse_primary_expression();
    while (cur.value.equals("*") || cur.value.equals("/") || cur.value.equals("%")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_primary_expression();
      left = new BinaryExpression(left, right, operator);
    }
    return left;
  }

  private Expression parse_primary_expression() throws Exception {
    switch (cur.type) {
      case Number:
        return new IntegerLiteral(Integer.parseInt(remove_and_get_token().value));
      case Identifier:
        return new Identifier(remove_and_get_token().value);
      case EOF:
        return null;
      case OpenParenthesis:
        remove_and_get_token();
        Expression value = parse_expression();
        expect_token(TokenType.CloseParenthesis, "Unexpected token found, expecting closing parenthesis");
        return value;
      case CloseParenthesis:
        return null;
      default:
        throw new Exception("Unexpected token found during parsing: " + cur.toString());
    }
  }
}
