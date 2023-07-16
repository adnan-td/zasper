package parser;

import java.util.ArrayList;

class Statement {
  NodeType kind;

  public Statement(NodeType kind) {
    this.kind = kind;
  }
}

class Program extends Statement {
  ArrayList<Statement> body = new ArrayList<>();

  public Program() {
    super(NodeType.Program);
  }
}

class Expression extends Statement {
  Expression expression;

  public Expression(NodeType kind) {
    super(kind);
  }

  public Expression(Expression expression) {
    super(NodeType.ExpressionStatement);
    this.expression = expression;
  }
}

class BinaryExpression extends Expression {
  Expression left;
  Expression right;
  String operator;

  public BinaryExpression(Expression left, Expression right, String operator) {
    super(NodeType.BinaryExpression);
    this.left = left;
    this.right = right;
    this.operator = operator;
  }
}

class Identifier extends Expression {
  String symbol;

  public Identifier(String symbol) {
    super(NodeType.Identifier);
    this.symbol = symbol;
  }
}

class NumericLiteral extends Expression {
  double value;

  public NumericLiteral(double value) {
    super(NodeType.Identifier);
    this.value = value;
  }
}