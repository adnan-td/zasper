package parser;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import gson.Printer;
import parser.ast.Statement;
import tokeniser.Token;
import tokeniser.TokenType;
import tokeniser.Tokeniser;

import parser.ast.*;

public class Parser {
  private ListIterator<Token> tokens;
  private Token cur;
  public String source;
  public List<Token> tokensList;

  public Parser(String source) throws Exception {
    this.source = source;
    this.tokensList = Arrays.asList(Tokeniser.tokenise(source));
    this.tokens = this.tokensList.listIterator();
  }

  public Parser() throws Exception {
    this.source = "";
    this.tokensList = Arrays.asList(Tokeniser.tokenise(source));
    this.tokens = this.tokensList.listIterator();
  }

  public Program parse(String source) throws Exception {
    this.source += "\n" + source;
    this.tokensList = Arrays.asList(Tokeniser.tokenise(source));
    this.tokens = this.tokensList.listIterator();
    return getAST();
  }


  public void printTokens() {
    for (Token t : tokensList) {
      System.out.println(t);
    }
    System.out.println();
  }

  public void printAST() throws Exception {
    Printer.print(getAST());
  }

  public Program getAST() throws Exception {
    Program program = new Program();
    remove_and_get_token();
    while (cur.type != TokenType.EOF) {
      program.body.add(this.parse_statement());
      if (cur.type == TokenType.EOL) {
        remove_and_get_token();
      }
    }
    return program;
  }

  private Token remove_and_get_token() {
    Token prev;
    if (!tokens.hasNext()) return cur;
    prev = cur;
    cur = tokens.next();
    return prev;
  }

  private Token expect_token(TokenType type, String err) throws Exception {
    Token prev = remove_and_get_token();
    if (prev == null || prev.type != type) {
      throw new Exception(String.format("Parser Error: %s\n%s - Expecting: %s", err, prev, type));
    }
    return prev;
  }

  private Statement parse_statement() throws Exception {
    switch (cur.type) {
      case Integer:
      case String:
      case Double:
        return parse_variable_declaration(cur.type);
      case Let:
      default:
        return parse_expression();
    }
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

  private Statement parse_variable_declaration(TokenType type) throws Exception {
    remove_and_get_token();
    String identifier = expect_token(TokenType.Identifier, "Expected identifier name").value;
    if (cur.type == TokenType.EOL) {
      remove_and_get_token();
      return new VariableDeclarationStatement(identifier);
    }
    expect_token(TokenType.Equals, "Extpected '=' assignment operator");
    Statement declaration = new VariableDeclarationStatement(identifier, parse_expression());
    expect_token(TokenType.EOL, "Expected end of line");
    return declaration;
  }
}
