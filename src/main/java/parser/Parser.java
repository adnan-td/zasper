package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import Exceptions.ErrorTypes;
import Exceptions.ParsingException;
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

  public Parser(String source) throws ParsingException {
    this.source = source;
    this.tokensList = Tokeniser.tokenise(source);
    this.tokens = this.tokensList.listIterator();
  }

  public Parser() throws ParsingException {
    this.source = "";
    this.tokensList = Tokeniser.tokenise(source);
    this.tokens = this.tokensList.listIterator();
  }

  public Program parse(String source) throws ParsingException {
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

  public void printAST() throws ParsingException {
    Printer.print(getAST());
  }

  public Program getAST() throws ParsingException {
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

  private Token expect_token(TokenType type, String err) throws ParsingException {
    Token prev = remove_and_get_token();
    if (prev.type != type) {
      throw new ParsingException(ErrorTypes.UnexpectedToken, String.format("%s\n%s, expecting: %s", err, prev, type), prev.location, source);
    }
    return prev;
  }

  private Statement parse_statement() throws ParsingException {
    int start, end;
    switch (cur.type) {
      case IntegerDeclaration:
      case StringDeclaration:
      case DoubleDeclaration:
      case BooleanDeclaration:
        return parse_variable_declaration(null);
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
        start = remove_and_get_token().location.start;
        end = expect_token(TokenType.EOL, "Unexpected token after break").location.start;
        BreakStatement bs = new BreakStatement();
        bs.loc.set_location(start, end);
        return bs;
      case Continue:
        start = remove_and_get_token().location.start;
        end = expect_token(TokenType.EOL, "Unexpected token after continue").location.start;
        ContinueStatement cs = new ContinueStatement();
        cs.loc.set_location(start, end);
        return cs;
      default:
        return parse_expression();
    }
  }

  private Expression parse_expression() throws ParsingException {
    return parse_assignment_expression();
  }

  private Expression parse_assignment_expression() throws ParsingException {
    Expression left = parse_comparison_expression();
    if (cur.type == TokenType.Equals || cur.type == TokenType.AssignmentOperator) {
      String operator = remove_and_get_token().value;
      Expression value = parse_comparison_expression();
      AssignmentExpression ae = new AssignmentExpression(left, value, operator);
      ae.loc.set_location(left.loc.start, value.loc.end);
      return ae;
    }
    return left;
  }

  private Expression parse_comparison_expression() throws ParsingException {
    Expression left = parse_additive_expression();
    while (cur.value.equals(">") || cur.value.equals(">=") || cur.value.equals("<=") || cur.value.equals("<") || cur.value.equals("==")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_additive_expression();
      BinaryExpression be = new BinaryExpression(left, right, operator);
      be.loc.set_location(left.loc.start, right.loc.end);
      left = be;
    }
    return left;
  }

  private Expression parse_additive_expression() throws ParsingException {
    Expression left = parse_multiplicative_expression();
    while (cur.value.equals("+") || cur.value.equals("-")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_multiplicative_expression();
      BinaryExpression be = new BinaryExpression(left, right, operator);
      be.loc.set_location(left.loc.start, right.loc.end);
      left = be;
    }
    return left;
  }

  private Expression parse_multiplicative_expression() throws ParsingException {
    Expression left = parse_call_expression();
    while (cur.value.equals("*") || cur.value.equals("/") || cur.value.equals("%")) {
      String operator = remove_and_get_token().value;
      Expression right = parse_call_expression();
      BinaryExpression be = new BinaryExpression(left, right, operator);
      be.loc.set_location(left.loc.start, right.loc.end);
      left = be;
    }
    return left;
  }

  private Expression parse_call_expression() throws ParsingException {
    Expression left = parse_primary_expression();
    if (left != null && left.kind == NodeType.Identifier && cur.type == TokenType.OpenParenthesis) {
      Identifier identifier = (Identifier) left;
      CallExpression callExpression = new CallExpression(identifier);
      List<Expression> argsList = parse_arguments();
      int end = left.loc.start + 2;
      for (Expression e : argsList) {
        end = e.loc.end;
        callExpression.add_argument(e);
      }
      callExpression.loc.set_location(left.loc.start, end);
      return callExpression;
    } else {
      return left;
    }
  }

  private Expression parse_primary_expression() throws ParsingException {
    Token curToken;
    switch (cur.type) {
      case Integer:
        curToken = remove_and_get_token();
        IntegerLiteral il = new IntegerLiteral(Integer.parseInt(curToken.value));
        il.loc.set_location(curToken.location);
        return il;
      case Double:
        curToken = remove_and_get_token();
        DoubleLiteral dl = new DoubleLiteral(Double.parseDouble(curToken.value));
        dl.loc.set_location(curToken.location);
        return dl;
      case Boolean:
        curToken = remove_and_get_token();
        BooleanLiteral bl = new BooleanLiteral(Boolean.parseBoolean((curToken.value)));
        bl.loc.set_location(curToken.location);
        return bl;
      case String:
        curToken = remove_and_get_token();
        StringLiteral sl = new StringLiteral(curToken.value);
        sl.loc.set_location(curToken.location);
        return sl;
      case Null:
        curToken = remove_and_get_token();
        NullLiteral nl = new NullLiteral();
        nl.loc.set_location(curToken.location);
        return nl;
      case Identifier:
        curToken = remove_and_get_token();
        Identifier id = new Identifier(curToken.value);
        id.loc.set_location(curToken.location);
        return id;
      case EOF:
      case CloseParenthesis:
        return null;
      case OpenParenthesis:
        curToken = remove_and_get_token();
        Expression value = parse_expression();
        value.loc.set_start(curToken.location.start);
        value.loc.set_end(expect_token(TokenType.CloseParenthesis, "Unexpected token found, expecting closing parenthesis").location.start);
        return value;
      default:
        throw new ParsingException(ErrorTypes.UnexpectedToken, cur.toString(), cur.location, source);
    }
  }

  private Statement parse_variable_declaration(Token type) throws ParsingException {
    int start = cur.location.start;
    int end;
    if (type == null) {
      type = remove_and_get_token();
    }
    String identifier = expect_token(TokenType.Identifier, "Expected identifier name").value;
    if (cur.type == TokenType.EOL) {
      end = remove_and_get_token().location.start;
      VariableDeclarationStatement vd = new VariableDeclarationStatement(type.type, identifier);
      vd.loc.set_location(start, end);
      return vd;
    } else if (cur.type == TokenType.Comma) {
      end = remove_and_get_token().location.start;
      VariableDeclarationStatement newVarDec = new VariableDeclarationStatement(type.type, identifier);
      newVarDec.lateralDeclaration = (VariableDeclarationStatement) parse_variable_declaration(type);
      newVarDec.loc.set_location(start, end);
      return newVarDec;
    } else {
      expect_token(TokenType.Equals, "Extpected '=' assignment operator");
      VariableDeclarationStatement declaration;
      if (type.type == TokenType.BooleanDeclaration) {
        Expression exp = parse_primary_expression();
        if (!(exp instanceof BooleanLiteral || exp instanceof NullLiteral)) {
          assert exp != null;
          throw new ParsingException(ErrorTypes.InvalidAssignmentToBoolean, exp.loc, source);
        }
        declaration = new VariableDeclarationStatement(type.type, identifier, exp);
      } else {
        declaration = new VariableDeclarationStatement(type.type, identifier, parse_expression());
      }
      declaration.loc.set_start(start);
      end = cur.location.start;
      if (cur.type == TokenType.Comma) {
        remove_and_get_token();
        declaration.lateralDeclaration = (VariableDeclarationStatement) parse_variable_declaration(type);
      } else {
        expect_token(TokenType.EOL, "Expected end of line");
      }
      declaration.loc.end = end;
      return declaration;
    }
  }

  private Statement parse_single_declaration() throws ParsingException {
    Token type = remove_and_get_token();
    String identifier = expect_token(TokenType.Identifier, "Expected identifier name").value;
    expect_token(TokenType.Equals, "Extpected '=' assignment operator");
    VariableDeclarationStatement vd = new VariableDeclarationStatement(type.type, identifier, parse_expression());
    vd.loc.set_location(type.location.start, vd.value.loc.end);
    return vd;
  }

  private Identifier parse_param_declaration() throws ParsingException {
    Token type = remove_and_get_token();
    Token identifier = expect_token(TokenType.Identifier, "Expected identifier name");
    Identifier id = new Identifier(identifier.value, type.type);
    id.loc.set_location(type.location.start, identifier.location.end);
    return id;
  }

  private List<Expression> parse_arguments() throws ParsingException {
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

  private int parse_block(BlockBody body) throws ParsingException {
    int start = expect_token(TokenType.Indentation, "Expected block").location.end;
    while (cur.type != TokenType.Dedentation) {
      if (cur.type == TokenType.EOL) {
        remove_and_get_token();
        continue;
      }
      body.body.add(parse_statement());
    }
    int end = remove_and_get_token().location.start;
    body.loc.set_location(start, end);
    return end;
  }

  private Statement parse_while_statement() throws ParsingException {
    int start = remove_and_get_token().location.start;
    Expression condition = parse_expression();
    if (!(condition instanceof BinaryExpression || condition instanceof BooleanLiteral)) {
      throw new ParsingException(ErrorTypes.InvalidCondition, condition.loc, source);
    }
    WhileStatement whileStatement = new WhileStatement(condition);
    expect_token(TokenType.Colon, "Expected ':' in the while statement");
    expect_token(TokenType.EOL, "Expected block for the while loop");
    int end = parse_block(whileStatement.body);
    whileStatement.loc.set_location(start, end);
    return whileStatement;
  }

  private Statement parse_for_statement() throws ParsingException {
    int start = remove_and_get_token().location.start;
    Statement init;
    if (cur.type == TokenType.Identifier) {
      init = parse_assignment_expression();
    } else {
      init = parse_single_declaration();
    }
    if (!(init instanceof VariableDeclarationStatement || init instanceof AssignmentExpression)) {
      throw new ParsingException(ErrorTypes.InvalidVariableDeclarationOrInitialisation, init.loc, source);
    }
    expect_token(TokenType.Comma, "Expected ',' after variable declaration in the for statement");
    Expression test = parse_expression();
    expect_token(TokenType.Comma, "Expected ',' after condition in the for statement");
    Statement update = parse_statement();
    expect_token(TokenType.Colon, "Expected ':' in the for statement");
    expect_token(TokenType.EOL, "Expected block for the for statement");
    ForStatement forStatement = new ForStatement(init, test, update);
    int end = parse_block(forStatement.body);
    forStatement.loc.set_location(start, end);
    return forStatement;
  }

  private Statement parse_function_declaration() throws ParsingException {
    int start = remove_and_get_token().location.start;
    Token name = expect_token(TokenType.Identifier, "Expected function name after function declaration");
    Identifier id = new Identifier(name.value);
    id.loc.set_location(name.location);
    FunctionDeclaration functionDeclaration = new FunctionDeclaration(id);
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
        throw new ParsingException(ErrorTypes.InvalidReturnType, cur.location, source);
    }
    expect_token(TokenType.Colon, "Expected ':'");
    expect_token(TokenType.EOL, "Expected block for the function");
    int end = parse_block(functionDeclaration.body);
    functionDeclaration.loc.set_location(start, end);
    return functionDeclaration;
  }

  private ReturnStatement parse_return_statement() throws ParsingException {
    int start = expect_token(TokenType.ReturnStatement, "Expected return statement").location.start;
    ReturnStatement returnStatement = new ReturnStatement(parse_expression());
    int end = expect_token(TokenType.EOL, "Unexpected tokens after return statement").location.start;
    returnStatement.loc.set_location(start, end);
    return returnStatement;
  }

  private IfStatement parse_if_statement() throws ParsingException {
    Token type = remove_and_get_token();
    int start = type.location.start;
    Expression test = null;
    if (type.type == TokenType.IfStatement || type.type == TokenType.ElifStatement) {
      test = parse_expression();
    }
    expect_token(TokenType.Colon, "Expected colon");
    expect_token(TokenType.EOL, "Unexpected token after ':'");
    IfStatement ifStatement = new IfStatement(test);
    int end = parse_block(ifStatement.consequent);
    if (cur.type == TokenType.ElifStatement || cur.type == TokenType.ElseStatement) {
      ifStatement.set_alternate(parse_if_statement());
    }
    ifStatement.loc.set_location(start, end);
    return ifStatement;
  }
}
