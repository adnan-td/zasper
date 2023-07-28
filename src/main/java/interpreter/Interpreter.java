package interpreter;

import parser.NodeType;
import parser.Parser;
import parser.Program;
import parser.ast.BinaryExpression;
import parser.ast.Identifier;
import parser.ast.IntegerLiteral;
import parser.ast.Statement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Interpreter {
  public final String source;
  public final Program AST;
  private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public Interpreter(String source) throws Exception {
    this.source = source;
    Parser parser = new Parser(source);
    this.AST = parser.getAST();
  }

  public void print_output() throws Exception {
    String json = gson.toJson(evaluate());
    System.out.println(json);
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
      return eval_numeric_binary_expression((Integer) left.value, (Integer) right.value, astNode.operator);
    }
    return new NullVal();
  }

  private RuntimeValue<?> eval_numeric_binary_expression(int left, int right, String operator) {
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

  public RuntimeValue<?> evaluate() throws Exception {
    Environment env = new Environment(null);
    env.declare_var("x", new IntVal(100));
    env.declare_var("true", new BoolVal(true));
    env.declare_var("false", new BoolVal(false));
    env.declare_var("null", new NullVal());

    return evaluate_statement(AST, env);
  }

  public RuntimeValue<?> evaluate_statement(Statement astNode, Environment env) throws Exception {
    NodeType type = astNode.kind;
    switch (type) {
      case NumericLiteral:
        return new IntVal(((IntegerLiteral) astNode).value);
      case BinaryExpression:
        return evaluate_binary_expression((BinaryExpression) astNode, env);
      case Program:
        return evaluate_program((Program) astNode, env);
      case Identifier:
        return evaluate_identifier((Identifier) astNode, env);
      default:
        throw new Exception("Support for this AST Node will be added soon");
    }
  }

  private RuntimeValue<?> evaluate_identifier(Identifier astNode, Environment env) {
    return env.lookup_var(astNode.symbol);
  }
}
