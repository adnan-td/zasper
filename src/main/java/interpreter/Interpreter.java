package interpreter;

import parser.NodeType;
import parser.Parser;
import parser.ast.*;

public class Interpreter {
  public String source;
  public Program AST;
  public final Parser parser;
  private final Environment globalEnv = new Environment(null);

  public Interpreter(String source) throws Exception {
    this.source = source;
    this.parser = new Parser(source);
    this.AST = parser.getAST();
    declare_default_global_var();
  }

  public Interpreter() throws Exception {
    this.source = "";
    this.parser = new Parser();
    this.AST = null;
    declare_default_global_var();
  }

  public RuntimeValue<?> interpret(String source) throws Exception {
    this.source += "\n" + source;
    this.AST = parser.parse(source);
    return evaluate();
  }

  private RuntimeValue<?> evaluate_program(Program program, Environment env) throws Exception {
    RuntimeValue<?> lastVal = new NullVal();
    for (Statement statement : program.body) {
      lastVal = evaluate_statement(statement, env);
    }
    return lastVal;
  }

  private RuntimeValue<?> evaluate_binary_expression(BinaryExpression astNode, Environment env) throws Exception {
    RuntimeValue<?> left = evaluate_statement(astNode.left, env);
    RuntimeValue<?> right = evaluate_statement(astNode.right, env);

    if (left.type == ValueType.Integer && right.type == ValueType.Integer) {
      return eval_integer_binary_expression((int) left.value, (int) right.value, astNode.operator);
    } else if (left.type == ValueType.Integer && right.type == ValueType.Double) {
      return eval_double_binary_expression((int) left.value, (double) right.value, astNode.operator);
    }else if (left.type == ValueType.Double && right.type == ValueType.Integer) {
      return eval_double_binary_expression((double) left.value, (int) right.value, astNode.operator);
    }else if (left.type == ValueType.Double && right.type == ValueType.Double) {
      return eval_double_binary_expression((double)left.value, (double) right.value, astNode.operator);
    }
    return new NullVal();
  }

  private static RuntimeValue<?> eval_integer_binary_expression(int left, int right, String operator) {
    int result = 0;
    switch (operator) {
      case "+":
        result = left + right;
        break;
      case "-":
        result = left - right;
        break;
      case "*":
        result = left * right;
        break;
      case "/":
        result = left / right;
        break;
      case "%":
        result = left % right;
        break;
    }
    return new IntVal(result);
  }

  private static RuntimeValue<?> eval_double_binary_expression(double left, double right, String operator) {
    double result = 0.0;
    switch (operator) {
      case "+":
        result = left + right;
        break;
      case "-":
        result = left - right;
        break;
      case "*":
        result = left * right;
        break;
      case "/":
        result = left / right;
        break;
      case "%":
        result = left % right;
        break;
    }
    return new DoubleVal(result);
  }


  private void declare_default_global_var() throws Exception {
    globalEnv.declare_constant_var("x", new IntVal(100));
    globalEnv.declare_constant_var("true", new BoolVal(true));
    globalEnv.declare_constant_var("false", new BoolVal(false));
    globalEnv.declare_constant_var("null", new NullVal());
  }

  public RuntimeValue<?> evaluate() throws Exception {
    return evaluate_statement(AST, globalEnv);
  }

  public RuntimeValue<?> evaluate_statement(Statement astNode, Environment env) throws Exception {
    NodeType type = astNode.kind;
    switch (type) {
      case IntegerLiteral:
        return new IntVal(((IntegerLiteral) astNode).value);
      case DoubleLiteral:
        return new DoubleVal(((DoubleLiteral) astNode).value);
      case BinaryExpression:
        return evaluate_binary_expression((BinaryExpression) astNode, env);
      case ProgramStatement:
        return evaluate_program((Program) astNode, env);
      case Identifier:
        return evaluate_identifier((Identifier) astNode, env);
      case VariableDeclarationStatement:
        return evaluate_var_declaration((VariableDeclarationStatement) astNode, env);
      case AssignmentExpression:
        return evaluate_assignment((AssignmentExpression) astNode, env);
      default:
        throw new Exception("Support for this AST Node will be added soon");
    }
  }

  private RuntimeValue<?> evaluate_identifier(Identifier astNode, Environment env) throws Exception {
    return env.lookup_var(astNode.symbol);
  }

  private RuntimeValue<?> evaluate_var_declaration(VariableDeclarationStatement astNode, Environment env) throws Exception {
    RuntimeValue<?> value;
    if (astNode.value != null) {
      value = evaluate_statement(astNode.value, env);
    } else {
      value = new RuntimeValue<>(astNode.type, null);
    }
    return env.declare_var(astNode.identifier, value);
  }

  private RuntimeValue<?> evaluate_assignment(AssignmentExpression astNode, Environment env) throws Exception {
    Identifier assignedTo = (Identifier) astNode.assignedTo;
    if (assignedTo.kind != NodeType.Identifier) {
      throw new Exception("Invalid LHS inside assignment expression " + astNode);
    }
    return env.assign_var(assignedTo.symbol, evaluate_statement(astNode.value, env));
  }
}


