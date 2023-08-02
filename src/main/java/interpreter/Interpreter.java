package interpreter;

import Exceptions.InterruptException;
import parser.NodeType;
import parser.Parser;
import parser.ast.*;
import tokeniser.TokenType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Interpreter {
  public String source;
  public Program AST;
  public final Parser parser;
  private final Environment globalEnv = new Environment(null);

  public Interpreter(String source) throws Exception {
    this.source = source;
    this.parser = new Parser(source);
    this.AST = parser.getAST();
  }

  public Interpreter() throws Exception {
    this.source = "";
    this.parser = new Parser();
    this.AST = null;
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
    } else if (left.type == ValueType.Double && right.type == ValueType.Integer) {
      return eval_double_binary_expression((double) left.value, (int) right.value, astNode.operator);
    } else if (left.type == ValueType.Double && right.type == ValueType.Double) {
      return eval_double_binary_expression((double) left.value, (double) right.value, astNode.operator);
    } else if (left.type == ValueType.Boolean && right.type == ValueType.Boolean) {
      return eval_boolean_binary_expression((boolean) left.value, (boolean) right.value, astNode.operator);
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
      case ">":
        return getBoolVal(left > right);
      case ">=":
        return getBoolVal(left >= right);
      case "<":
        return getBoolVal(left < right);
      case "<=":
        return getBoolVal(left <= right);
      case "==":
        return getBoolVal(left == right);
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
      case ">":
        return getBoolVal(left > right);
      case ">=":
        return getBoolVal(left >= right);
      case "<":
        return getBoolVal(left < right);
      case "<=":
        return getBoolVal(left <= right);
      case "==":
        return getBoolVal(left == right);
    }
    return new DoubleVal(result);
  }

  private static RuntimeValue<?> eval_boolean_binary_expression(boolean left, boolean right, String operator) throws Exception {
    if (operator.equals("==")) {
      return getBoolVal(left == right);
    } else {
      throw new Exception("Invalid operator %s on booleans");
    }
  }


  private static BoolVal getBoolVal(boolean val) {
    return new BoolVal(val);
  }

  public RuntimeValue<?> evaluate() throws Exception {
    return evaluate_statement(AST, globalEnv);
  }

  public RuntimeValue<?> evaluate_statement(Statement astNode, Environment env) throws Exception {
    if (astNode == null) {
      throw new Exception("Null astNode cannot be evaluated");
    }
    NodeType type = astNode.kind;
    switch (type) {
      case IntegerLiteral:
        return new IntVal(((IntegerLiteral) astNode).value);
      case DoubleLiteral:
        return new DoubleVal(((DoubleLiteral) astNode).value);
      case BooleanLiteral:
        return new BoolVal(((BooleanLiteral) astNode).value);
      case StringLiteral:
        return new StringVal(((StringLiteral) astNode).value);
      case NullLiteral:
        return new NullVal();
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
      case WhileStatement:
        return evaluate_while_statement((WhileStatement) astNode, env);
      case ForStatement:
        return evaluate_for_statement((ForStatement) astNode, env);
      case IfStatement:
        return evaluate_if_statement((IfStatement) astNode, env);
      case FunctionDeclaration:
        return evaluate_function_declaration((FunctionDeclaration) astNode, env);
      case CallExpression:
        return evaluate_call_expression((CallExpression) astNode, env);
      case ReturnStatement:
        throw new Exception("Return statement outside function");
      case BlockStatement:
        throw new Exception("Unexpected block");
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
    if (astNode.lateralDeclaration != null) {
      evaluate_var_declaration(astNode.lateralDeclaration, env);
    }
    return env.declare_var(astNode.identifier, value);
  }

  private RuntimeValue<?> evaluate_assignment(AssignmentExpression astNode, Environment env) throws Exception {
    if (astNode.assignedTo.kind != NodeType.Identifier) {
      throw new Exception("Invalid LHS inside assignment expression " + astNode);
    }
    Identifier assignedTo = (Identifier) astNode.assignedTo;
    return env.assign_var(assignedTo.symbol, evaluate_statement(astNode.value, env), astNode.operator);
  }

  private NullVal evaluate_while_statement(WhileStatement whileStatement, Environment env) throws Exception {
    BoolVal cond = (BoolVal) evaluate_statement(whileStatement.condition, env);
    while (cond.value) {
      int cs = execute_block_body_loop(whileStatement.body, env.create_child_environment());
      if (cs == 1) {
        break;
      }
      cond = (BoolVal) evaluate_statement(whileStatement.condition, env);
    }
    return new NullVal();
  }


  private NullVal evaluate_for_statement(ForStatement forStatement, Environment env) throws Exception {
    Environment forEnv = env.create_child_environment();
    evaluate_statement(forStatement.init, forEnv);
    BoolVal cond = (BoolVal) evaluate_statement(forStatement.test, forEnv);
    while (cond.value) {
      int cs = execute_block_body_loop(forStatement.body, forEnv.create_child_environment());
      if (cs == 1) {
        break;
      }
      evaluate_statement(forStatement.update, forEnv);
      cond = (BoolVal) evaluate_statement(forStatement.test, forEnv);
    }
    return new NullVal();
  }

  private int execute_block_body_loop(BlockBody block, Environment env) throws Exception {
    for (Statement statement : block.body) {
      if (statement.kind == NodeType.BreakStatement) {
        return 1;
      } else if (statement.kind == NodeType.ContinueStatment) {
        return 2;
      } else if (statement.kind == NodeType.ReturnStatement) {
        throw new InterruptException(3, "Return statement outside function", (ReturnStatement) statement);
      }
      try {
        evaluate_statement(statement, env);
      } catch (InterruptException err) {
        if (err.interrupt_id == 1) {
          return 1;
        } else if (err.interrupt_id == 2) {
          return 2;
        } else {
          throw err;
        }
      }
    }
    return 0;
  }

  private ReturnStatement execute_block_return(BlockBody block, Environment env) throws Exception {
    for (Statement st : block.body) {
      if (st.kind == NodeType.ReturnStatement) {
        return (ReturnStatement) st;
      } else if (st.kind == NodeType.BreakStatement) {
        throw new InterruptException(1, "Break statement outside loop");
      } else if (st.kind == NodeType.ContinueStatment) {
        throw new InterruptException(2, "Continue statement outside loop");
      }
      try {
        evaluate_statement(st, env);
      } catch (InterruptException err) {
        if (err.interrupt_id == 3) {
          return err.return_value;
        } else {
          throw err;
        }
      }
    }
    return null;
  }

  private void execute_block_body_if(BlockBody block, Environment env) throws Exception {
    for (Statement statement : block.body) {
      if (statement.kind == NodeType.BreakStatement) {
        throw new InterruptException(1, "Break statement outside loop");
      } else if (statement.kind == NodeType.ContinueStatment) {
        throw new InterruptException(2, "Continue statement outside loop");
      } else if (statement.kind == NodeType.ReturnStatement) {
        throw new InterruptException(3, "Return statement outside function", (ReturnStatement) statement);
      }
      evaluate_statement(statement, env);
    }
  }

  private NullVal evaluate_function_declaration(FunctionDeclaration astNode, Environment env) throws Exception {
    env.declare_function(astNode.id.symbol, new FunctionRuntime(env.create_child_environment(), astNode.body, astNode.parameters, astNode.returnType));
    return new NullVal();
  }

  private RuntimeValue<?> evaluate_call_expression(CallExpression callExpression, Environment env) throws Exception {
    List<RuntimeValue<?>> arguments = new ArrayList<>();
    for (Expression exp : callExpression.arguments) {
      arguments.add(evaluate_statement(exp, env));
    }

    FunctionRuntime function = env.lookup_function(callExpression.caller.symbol);
    if (function.isNative) {
      function.execute(arguments);
      return new NullVal();
    }
    Environment funcEnv = function.assign_arguments(arguments);
    ReturnStatement returnStatement = execute_block_return(function.body, funcEnv);
    if (returnStatement == null) {
      if (function.returnType == ValueType.Null) return new NullVal();
      else throw new Exception("Return type did not match the function");
    } else {
      RuntimeValue<?> returnValue = evaluate_statement(returnStatement.argument, funcEnv);
      if (returnValue.type == function.returnType) {
        return returnValue;
      } else throw new Exception("Return type did not match the function");
    }
  }

  private RuntimeValue<?> evaluate_if_statement(IfStatement ifStatement, Environment env) throws Exception {
    BoolVal test = (BoolVal) evaluate_statement(ifStatement.test, env);
    while (!test.value && ifStatement.alternate != null && ifStatement.test != null) {
      ifStatement = ifStatement.alternate;
      if (ifStatement.test == null) {
        continue;
      }
      test = (BoolVal) evaluate_statement(ifStatement.test, env);
    }
    if (ifStatement.test == null || test.value) {
      execute_block_body_if(ifStatement.consequent, env.create_child_environment());
    }
    return new NullVal();
  }
}


