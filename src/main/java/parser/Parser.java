package parser;

import java.util.ArrayList;
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
    this.tokensList = Tokeniser.tokenise(source);
    this.tokens = this.tokensList.listIterator();
  }

  public Parser() throws Exception {
    this.source = "";
    this.tokensList = Tokeniser.tokenise(source);
    this.tokens = this.tokensList.listIterator();
  }

  public Program parse(String source) throws Exception {
    this.source += "\n" + source;
    this.tokensList.addAll(Tokeniser.tokenise(source));
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
      if (cur.type == TokenType.EOL) {
        remove_and_get_token();
      } else {
        program.body.add(this.parse_statement());
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
      case IntegerDeclaration:
      case StringDeclaration:
      case DoubleDeclaration:
      case BooleanDeclaration:
        return parse_variable_declaration(remove_and_get_token());
      case WhileStatement:
        return parse_while_statement();
      case ForStatement:
        return parse_for_statement();
      case FunctionDeclaration:
        return parse_function_declaration();
      case ReturnStatement:
        return parse_return_statement();
      case IfStatement:
        return parse_if_statement();
      case Break:
        remove_and_get_token();
        expect_token(TokenType.EOL, "Unexpected token after break");
        return new BreakStatement();
      case Continue:
        remove_and_get_token();
        expect_token(TokenType.EOL, "Unexpected token after break");
        return new ContinueStatement();
      default:
        return parse_expression();
    }
  }

  private Expression parse_expression() throws Exception {
    return parse_assignment_expression();
  }

  private Expression parse_assignment_expression() throws Exception {
    Expression left = parse_comparison_expression();
    if (cur.type == TokenType.Equals | cur.type == TokenType.AssignmentOperator) {
      String operator = remove_and_get_token().value;
      Expression value = parse_comparison_expression();
      return new AssignmentExpression(left, value, operator);
    }
    return left;
  }

  private Expression parse_comparison_expression() throws Exception {
    Expression left = parse_additive_expression();
    while (cur.value.equals(">") || cur.value.equals(">=") || cur.value.equals("<=") || cur.value.equals("<") || cur.value.equals("==")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_additive_expression();
      left = new BinaryExpression(left, right, operator);
    }
    return left;
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
    Expression left = parse_call_expression();
    while (cur.value.equals("*") || cur.value.equals("/") || cur.value.equals("%")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_call_expression();
      left = new BinaryExpression(left, right, operator);
    }
    return left;
  }

  private Expression parse_call_expression() throws Exception {
    Expression left = parse_primary_expression();
    if (left != null && left.kind == NodeType.Identifier && cur.type == TokenType.OpenParenthesis) {
      Identifier identifier = (Identifier) left;
      CallExpression callExpression = new CallExpression(identifier);
      for (Expression e : parse_arguments()) {
        callExpression.add_argument(e);
      }
      return callExpression;
    } else {
      return left;
    }
  }

  private Expression parse_primary_expression() throws Exception {
    switch (cur.type) {
      case Integer:
        return new IntegerLiteral(Integer.parseInt(remove_and_get_token().value));
      case Double:
        return new DoubleLiteral(Double.parseDouble(remove_and_get_token().value));
      case Boolean:
        return new BooleanLiteral(Boolean.parseBoolean((remove_and_get_token().value)));
      case String:
        return new StringLiteral(remove_and_get_token().value);
      case Null:
        remove_and_get_token();
        return new NullLiteral();
      case Identifier:
        return new Identifier(remove_and_get_token().value);
      case EOF:
      case CloseParenthesis:
        return null;
      case OpenParenthesis:
        remove_and_get_token();
        Expression value = parse_expression();
        expect_token(TokenType.CloseParenthesis, "Unexpected token found, expecting closing parenthesis");
        return value;
      default:
        throw new Exception("Unexpected token found during parsing: " + cur);
    }
  }

  private Statement parse_variable_declaration(Token type) throws Exception {
    String identifier = expect_token(TokenType.Identifier, "Expected identifier name").value;
    if (cur.type == TokenType.EOL) {
      remove_and_get_token();
      return new VariableDeclarationStatement(type.type, identifier);
    } else if (cur.type == TokenType.Comma) {
      remove_and_get_token();
      VariableDeclarationStatement newVarDec = new VariableDeclarationStatement(type.type, identifier);
      newVarDec.lateralDeclaration = (VariableDeclarationStatement) parse_variable_declaration(type);
      return newVarDec;
    } else {
      expect_token(TokenType.Equals, "Extpected '=' assignment operator");
      VariableDeclarationStatement declaration;
      if (type.type == TokenType.BooleanDeclaration) {
        Expression exp = parse_primary_expression();
        if (!(exp instanceof BooleanLiteral || exp instanceof NullLiteral)) {
          throw new Exception("Expected boolean declaration");
        }
        declaration = new VariableDeclarationStatement(type.type, identifier, exp);
      } else {
        declaration = new VariableDeclarationStatement(type.type, identifier, parse_expression());
      }
      if (cur.type == TokenType.Comma) {
        remove_and_get_token();
        declaration.lateralDeclaration = (VariableDeclarationStatement) parse_variable_declaration(type);
      } else {
        expect_token(TokenType.EOL, "Expected end of line");
      }
      return declaration;
    }
  }

  private Statement parse_single_declaration() throws Exception {
    Token type = remove_and_get_token();
    String identifier = expect_token(TokenType.Identifier, "Expected identifier name").value;
    expect_token(TokenType.Equals, "Extpected '=' assignment operator");
    return new VariableDeclarationStatement(type.type, identifier, parse_expression());
  }

  private Identifier parse_param_declaration() throws Exception {
    Token type = remove_and_get_token();
    String identifier = expect_token(TokenType.Identifier, "Expected identifier name").value;
    return new Identifier(identifier, type.type);
  }

  private List<Expression> parse_arguments() throws Exception {
    expect_token(TokenType.OpenParenthesis, "Expected open paranthesis");
    List<Expression> expressions = new ArrayList<>();
    while (cur.type != TokenType.CloseParenthesis) {
      expressions.add(parse_expression());
      if (cur.type != TokenType.CloseParenthesis) {
        expect_token(TokenType.Comma, "Expected comma after argument");
      }
    }
    expect_token(TokenType.CloseParenthesis, "Expected ')' after arguments");
    return expressions;
  }

  private void parse_block(BlockBody body) throws Exception {
    expect_token(TokenType.Indentation, "Expected block");
    while (cur.type != TokenType.Dedentation) {
      if (cur.type == TokenType.EOL) {
        remove_and_get_token();
        continue;
      }
      body.body.add(parse_statement());
    }
    remove_and_get_token();
  }

  private Statement parse_while_statement() throws Exception {
    remove_and_get_token();
    Expression condition = parse_expression();
    if (!(condition instanceof BinaryExpression || condition instanceof BooleanLiteral)) {
      throw new Exception("Invalid condition in while statement");
    }
    WhileStatement whileStatement = new WhileStatement(condition);
    expect_token(TokenType.Colon, "Expected ':' in the while statement");
    expect_token(TokenType.EOL, "Expected block for the while loop");
    parse_block(whileStatement.body);
    return whileStatement;
  }

  private Statement parse_for_statement() throws Exception {
    remove_and_get_token();
    Statement init;
    if (cur.type == TokenType.Identifier) {
      init = parse_assignment_expression();
    } else {
      init = parse_single_declaration();
    }
    if (!(init instanceof VariableDeclarationStatement || init instanceof AssignmentExpression)) {
      throw new Exception("Invalid variable declaration or initialisation");
    }
    expect_token(TokenType.Comma, "Expected ',' after variable declaration in the for statement");
    Expression test = parse_expression();
    expect_token(TokenType.Comma, "Expected ',' after condition in the for statement");
    Statement update = parse_statement();
    expect_token(TokenType.Colon, "Expected ':' in the for statement");
    expect_token(TokenType.EOL, "Expected block for the for statement");
    ForStatement forStatement = new ForStatement(init, test, update);
    parse_block(forStatement.body);
    return forStatement;
  }

  private Statement parse_function_declaration() throws Exception {
    remove_and_get_token();
    String name = expect_token(TokenType.Identifier, "Expected function name after function declaration").value;
    FunctionDeclaration functionDeclaration = new FunctionDeclaration(new Identifier(name));
    expect_token(TokenType.OpenParenthesis, "Expected '(' after function name");
    while (cur.type != TokenType.CloseParenthesis) {
      functionDeclaration.add_argument(parse_param_declaration());
      if (cur.type != TokenType.CloseParenthesis) {
        expect_token(TokenType.Comma, "Expected ',' after argument declaration");
      }
    }
    expect_token(TokenType.CloseParenthesis, "Expected ')' after function arguments");
    expect_token(TokenType.ArrowOperator, "Expected '->' to define return type of function");
    switch (cur.type) {
      case IntegerDeclaration:
      case StringDeclaration:
      case DoubleDeclaration:
      case BooleanDeclaration:
      case Null:
        functionDeclaration.set_return_type(remove_and_get_token().type);
        break;
      default:
        throw new Exception("Invalid function return type");
    }
    expect_token(TokenType.Colon, "Expected ':'");
    expect_token(TokenType.EOL, "Expected block for the function");
    expect_token(TokenType.Indentation, "Expected block for the function");
    while (cur.type != TokenType.Dedentation) {
      if (cur.type == TokenType.EOL) {
        remove_and_get_token();
        continue;
      }
      if (cur.type == TokenType.ReturnStatement) {
        functionDeclaration.body.body.add(parse_return_statement());
      } else {
        functionDeclaration.body.body.add(parse_statement());
      }
    }
    remove_and_get_token();
    return functionDeclaration;
  }

  private ReturnStatement parse_return_statement() throws Exception {
    expect_token(TokenType.ReturnStatement, "Expected return statement");
    ReturnStatement returnStatement = new ReturnStatement(parse_expression());
    expect_token(TokenType.EOL, "Unexpected tokens after return statement");
    return returnStatement;
  }

  private IfStatement parse_if_statement() throws Exception {
    Token type = remove_and_get_token();
    Expression test = null;
    if (type.type == TokenType.IfStatement || type.type == TokenType.ElifStatement) {
      test = parse_expression();
    }
    expect_token(TokenType.Colon, "Expected colon");
    expect_token(TokenType.EOL, "Unexpected token after ':'");
    IfStatement ifStatement = new IfStatement(test);
    parse_block(ifStatement.consequent);
    if (cur.type == TokenType.ElifStatement || cur.type == TokenType.ElseStatement) {
      ifStatement.set_alternate(parse_if_statement());
    }
    return ifStatement;
  }
}
