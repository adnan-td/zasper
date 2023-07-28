package parser.ast;

import parser.NodeType;

public class DoubleLiteral extends Expression {
  public double value;

  public DoubleLiteral(double value) {
    super(NodeType.DoubleLiteral);
    this.value = value;
  }
}

