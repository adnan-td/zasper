package parser;

import com.google.gson.Gson;

class Statement {
  NodeType kind;

  public Statement(NodeType kind) {
    this.kind = kind;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

class Expression extends Statement {
  Expression expression;

  public Expression(NodeType kind) {
    super(kind);
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
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

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

class Identifier extends Expression {
  String symbol;

  public Identifier(String symbol) {
    super(NodeType.Identifier);
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}

class NumericLiteral extends Expression {
  double value;

  public NumericLiteral(double value) {
    super(NodeType.NumericLiteral);
    this.value = value;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
